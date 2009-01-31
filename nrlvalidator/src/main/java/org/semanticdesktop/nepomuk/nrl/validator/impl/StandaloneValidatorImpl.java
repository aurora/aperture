package org.semanticdesktop.nepomuk.nrl.validator.impl;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidator;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;
import org.semanticdesktop.nepomuk.openrdf.InfSail;
import org.semanticdesktop.nepomuk.openrdf.SemanticViewSpecification;
import org.semanticdesktop.nepomuk.openrdf.UnionMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a standalone validator. 
 * 
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class StandaloneValidatorImpl implements StandaloneValidator {

    private Logger log = LoggerFactory.getLogger(StandaloneValidatorImpl.class);

    /** List of ontology uris */
    private Set<String> ontologies;
    
    /** Base UnionSail, the InfSail operates on top of it */
    private Sail baseSail;
    
    /** A simple repository that wraps the basic sail */
    private Repository baseRepository;
    
    /** The infSail */
    private InfSail infSail;

    /** Repository encapsulating the InfSail */
    private Repository infRepository;
    
    /** The URI of the context where all ontologies are */
    private URI ontologiesInferenceClosureURI;
    
    /** The repository tester */
    private ModelTester [] testers;

    /** The default constructor. */
    public StandaloneValidatorImpl() {
        initialize(null);
    }

    /**
     * Constructor that accepts a sail.
     * 
     * @param store the underlying NativeStore.
     */
    public StandaloneValidatorImpl(InfSail store) {
        initialize(store);
    }
    
    private void initialize(InfSail sail) {
        if (sail == null) {                        
            try {
                this.baseSail = new UnionMemoryStore();
                this.infSail = new InfSail(this.baseSail);
                this.infSail.initialize();
            } catch (Exception e) {
                // nothing should happen when initializing a simple memory stor
                log.error("Couldn't initialize a memory store", e);
            }
        } else {
            this.infSail = sail;
            this.baseSail = sail.getBaseSail();
        }
        this.infRepository = new SailRepository(infSail);
        this.baseRepository = new SailRepository(baseSail);
        this.ontologies = new TreeSet<String>();
        this.ontologiesInferenceClosureURI = null;
    }

    /**
     * @see StandaloneValidator#addOntology(Reader, java.lang.String,
     *      java.lang.String)
     */
    public void addOntology(Reader reader, String syntax, String ontologyURI)
        throws StandaloneValidatorException {
        if (reader == null) {
            throw new NullPointerException("The reader cannot be null");
        }
        URI uri = new URIImpl(ontologyURI);
        RDFFormat format = stringToFormat(syntax);
        Repository tempRepository = readFromReaderToTempRepository(
            reader,
            format,
            uri);
        addTempRepositoryToRawRepository(tempRepository, uri);
        this.ontologies.add(ontologyURI);
        
        if (ontologiesInferenceClosureURI != null) {
            refreshOntologiesInferenceClosure();
        }
    }

    /**
     * @see StandaloneValidator#addOntology(java.io.InputStream,
     *      java.lang.String, java.lang.String)
     */
    public void addOntology(
        InputStream stream,
        String syntax,
        String ontologyURI) throws StandaloneValidatorException {

        if (stream == null) {
            throw new NullPointerException("The stream cannot be null");
        }
        URI uri = new URIImpl(ontologyURI);
        RDFFormat format = stringToFormat(syntax);
        Repository tempRepository = readFromStreamToTempRepository(
            stream,
            format,
            uri);
        addTempRepositoryToRawRepository(tempRepository, uri);
        this.ontologies.add(ontologyURI);
        if (ontologiesInferenceClosureURI != null) {
            refreshOntologiesInferenceClosure();
        }
    }
    
    /**
     * 
     * @see StandaloneValidator#addOntology(org.ontoware.rdf2go.model.Model, java.lang.String)
     */
    public void addOntology(Model model, String ontologyURI) throws StandaloneValidatorException {
        if (model == null) {
            throw new NullPointerException("The model cannot be null");
        }
        URI uri = new URIImpl(ontologyURI);
        Repository tempRepository = readFromModelToTempRepository(model);
        addTempRepositoryToRawRepository(tempRepository, uri);
        this.ontologies.add(ontologyURI);
        if (ontologiesInferenceClosureURI != null) {
            refreshOntologiesInferenceClosure();
        }
    }

    /**
     * @see org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidator#listOntologyUris()
     */
    public List<String> listOntologyUris() {
        List<String> result = new LinkedList<String>();
        result.addAll(ontologies);
        return result;
    }

    /**
     * @see StandaloneValidator#removeOntology(java.lang.String)
     */
    public void removeOntology(String uriString) throws StandaloneValidatorException {
        if (ontologies.contains(uriString)) {
            URI uri = new URIImpl(uriString);
            removeGraph(uri);
            this.ontologies.remove(uriString);
            if (ontologiesInferenceClosureURI != null) {
                refreshOntologiesInferenceClosure();
            }
        } else {
            throw new StandaloneValidatorException("The URI: "
                + uriString
                + " does note denote an ontology within the validator");
        }
    }

    /**
     * @see StandaloneValidator#validate(java.lang.String, java.lang.String)
     */
    public ValidationReport validate(
        String dataGraphContent,
        String syntax) throws StandaloneValidatorException {
        return performValidation(
            new StringReader(dataGraphContent),
            syntax);
    }

    /**
     * @see StandaloneValidator#validate(java.io.File, java.lang.String,
     *      java.lang.String[])
     */
    public ValidationReport validate(
        File file,
        String syntax) throws StandaloneValidatorException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return performValidation(stream, syntax);
        } catch (Exception e) {
            throw new StandaloneValidatorException(e);
        } finally {
            closeStream(stream);
        }
    }

    /**
     * @see StandaloneValidator#validate(java.io.InputStream, java.lang.String,
     *      java.lang.String[])
     */
    public ValidationReport validate(
        InputStream stream,
        String syntax) throws StandaloneValidatorException {
        return performValidation(stream, syntax);
    }

    /**
     * @see StandaloneValidator#validate(java.io.Reader, java.lang.String,
     *      java.lang.String[])
     */
    public ValidationReport validate(
        Reader reader,
        String syntax) throws StandaloneValidatorException {
        return performValidation(reader, syntax);
    }

    /**
     * @see StandaloneValidator#validate(org.ontoware.rdf2go.model.Model)
     */
    public ValidationReport validate(Model model) throws StandaloneValidatorException {
        return performValidation(model, null);
    }

    
    private ValidationReport performValidation(
        Object inputStreamOrReaderOrModel,
        String syntax) throws StandaloneValidatorException {
        
        if (inputStreamOrReaderOrModel == null) {
            throw new StandaloneValidatorException("The InputStream or Reader parameter cannot be null");
        }
        
        checkTesterArray();
        
        if (ontologiesInferenceClosureURI == null) {
            refreshOntologiesInferenceClosure();
        }
        
        URI dataUri = new URIImpl("urn:" + UUID.randomUUID().toString());
        URI unionUri = new URIImpl("urn:" + UUID.randomUUID().toString());
        URI semanticViewUri = new URIImpl("urn:" + UUID.randomUUID().toString());
        
        RepositoryModel dataModel = null;
        RepositoryModel semanticViewModel = null;
        try {
            Repository tempRepository = null;
            if (inputStreamOrReaderOrModel instanceof Reader) {
                RDFFormat format = stringToFormat(syntax);
                tempRepository = readFromReaderToTempRepository(
                    (Reader) inputStreamOrReaderOrModel,
                    format,
                    dataUri);
            } else if (inputStreamOrReaderOrModel instanceof InputStream) {
                RDFFormat format = stringToFormat(syntax);
                tempRepository = readFromStreamToTempRepository(
                    (InputStream) inputStreamOrReaderOrModel,
                    format,
                    dataUri);
            } else if (inputStreamOrReaderOrModel instanceof Model) {
                tempRepository = readFromModelToTempRepository(
                    (Model) inputStreamOrReaderOrModel);
            }
            // the tempRepository contains the statements in their original contexts
            // the question is what are we going to do with those contexts
            
            // TODO this part will need some rewriting to take the contexts into account
            // right now the contexts are ignored and all statements from the
            // tempRepository are shoved into the same context in the rawRepository
            
            addTempRepositoryToRawRepository(tempRepository, dataUri);
                        
            infSail.importGraph(unionUri, dataUri, ontologiesInferenceClosureURI);
            infSail.createSemanticView(unionUri,semanticViewUri, SemanticViewSpecification.getNRL());
            
            dataModel = new RepositoryModel(
                new org.ontoware.rdf2go.model.node.impl.URIImpl(dataUri
                    .toString()),
                infRepository);
            semanticViewModel = new RepositoryModel(
                new org.ontoware.rdf2go.model.node.impl.URIImpl(semanticViewUri
                    .toString()),
                infRepository);
            dataModel.open();
            
            semanticViewModel.open();
            
            ValidationReport report = testers[0].performTests(semanticViewModel, dataModel);
            for (int i = 1; i < testers.length; i++) {
                testers[i].performTests(semanticViewModel, dataModel, report);
            }
           
            return report;
        } catch (Exception e) {
            throw new StandaloneValidatorException(e);
        } finally {
            closeModel(dataModel);
            closeModel(semanticViewModel);
            removeGraph(semanticViewUri);
            removeGraph(unionUri);
            removeGraph(dataUri);
        }
    }

    private void checkTesterArray() throws StandaloneValidatorException{
        if (testers == null || testers.length == 0) {
            throw new StandaloneValidatorException("no model testers set");
        } else {
            for (int i = 0; i<testers.length; i++) {
                if (testers[i] == null) {
                    throw new StandaloneValidatorException("model tester number " + (i+1) + " is null");
                }
            }
        }
    }

    private void closeModel(Model model) {
        if (model != null) {
            try {
                model.close();
            } catch (Exception e) {
                log.warn("Couldn't close a model",e);
            }
        }
    }

    /**
     * @see StandaloneValidator#getModelTesters()
     */
    public ModelTester[] getModelTesters() {
        return testers;
    }

    /**
     * @see StandaloneValidator#setModelTester(ModelTester)
     */
    public void setModelTesters(ModelTester... testers) {
        this.testers = testers;
    }

    // //////////////////////////////////////////////////////////////////////
    // ///////////////////////// UTILITY METHODS ////////////////////////////
    // //////////////////////////////////////////////////////////////////////
    
    /**
     * Removes a graph from the underlying repository. This method is supposed
     * to encapsulate the workaround for the lack of real removeGraph method
     * in the infsail. 
     */
    private void removeGraph(URI graphUri) {
        try {
            infSail.removeGraph(graphUri);
        } catch (SailException se) {
            log.error("Couldn't remove a graph",se);
        }
    }
    
    /**
     * Removes an ontology from the ontology context. This is a crappy hack
     * that assumes that every statement that belongs to an ontology has one
     * element that belongs to the namespace of that ontology. This should be
     * good enough for our purposes.
     */
    private void removeOntology(URI graphUri) {
        RepositoryConnection connection = null;

        GraphQueryResult result = null;
        try {
            connection = baseRepository.getConnection();
            GraphQuery query = connection.prepareGraphQuery(QueryLanguage.SPARQL,
                "CONSTRUCT {?s ?p ?o} " +
                "WHERE { GRAPH <" + ontologiesInferenceClosureURI.toString() + "> { ?s ?p ?o " +
                "   FILTER (regex(str(?s), \"" + graphUri.toString() + "\") ||" +
                "           regex(str(?p), \"" + graphUri.toString() + "\") ||" +
                "           regex(str(?o), \"" + graphUri.toString() + "\") || )}}");
            result = query.evaluate();
            List<Statement> statementsToRemove = new LinkedList<Statement>();
            while (result.hasNext()) {
                Statement statement = result.next();
                statementsToRemove.add(statement);
            }
            result.close();
            connection.remove(statementsToRemove, ontologiesInferenceClosureURI);
            //connection.remove((Resource) null, (URI) null, (Value) null, graphUri);
        } catch (QueryEvaluationException re) {
            log.warn("QueryEvaluation exception ", re);
        } catch (MalformedQueryException re) {
            log.warn("Malformed query", re);
        } catch (RepositoryException re) {
            log.warn("Couldn't remove an ontology", re);
        } finally {
            closeIteration(result);
            closeConnection(connection);
        }
    }
    
    private void refreshOntologiesInferenceClosure() throws StandaloneValidatorException {
        URI tempOntologyUnionUri = new URIImpl("urn:" + UUID.randomUUID().toString());
        URI tempOntologyViewUri = new URIImpl("urn:" + UUID.randomUUID().toString());
        
        Resource [] contexts = new Resource[ontologies.size()];
        Iterator<String> stringIterator = ontologies.iterator();
        for (int i = 0; i < contexts.length; i++) {
            contexts[i] = new URIImpl(stringIterator.next());
        }
        
        RepositoryConnection connection = null;
        RepositoryResult<Statement> statements = null;
        try {
            infSail.importGraph(tempOntologyUnionUri, contexts);
            infSail.createSemanticView(tempOntologyUnionUri, 
                tempOntologyViewUri, SemanticViewSpecification.getNRL());
            connection = infRepository.getConnection();
            if (ontologiesInferenceClosureURI != null) {
            connection.clear(ontologiesInferenceClosureURI);
            } else {
                ontologiesInferenceClosureURI = new URIImpl("urn:" + UUID.randomUUID().toString());
            }
            statements = connection.getStatements(null, null, null, true, tempOntologyViewUri);
            
            List<Statement> statementsToAdd = new LinkedList<Statement>();
            while (statements.hasNext()) {
                statementsToAdd.add(statements.next());
            }
            closeIteration(statements);
            
            for (Statement statement : statementsToAdd) {
                connection.add(statement, ontologiesInferenceClosureURI);
            }
        } catch (SailException se) {
            throw new StandaloneValidatorException("Couldn't refresh the ontology union", se);
        } catch (RepositoryException re) {
            throw new StandaloneValidatorException("Couldn't refresh the ontology union", re);
        } finally {
            closeIteration(statements);
            closeConnection(connection);
            removeGraph(tempOntologyViewUri);
            removeGraph(tempOntologyUnionUri);
        }
    }
    
    /**
     * Creates a temporary repository that contains the content from the stream.
     * 
     * @param stream the content
     * @param syntax the mime type of the syntax
     * @param baseURI this uri is used to resolve any relative uris in the
     *        content
     */
    private Repository readFromStreamToTempRepository(
        InputStream stream,
        RDFFormat format,
        URI baseURI) throws StandaloneValidatorException {
        Repository tempRepository = new SailRepository(new MemoryStore());
        RepositoryConnection tempConnection = null;

        try {
            tempRepository.initialize();
            tempConnection = tempRepository.getConnection();
            tempConnection.add(stream, baseURI.toString(), format);
        } catch (Exception e) {
            throw new StandaloneValidatorException(e);
        } finally {
            closeConnection(tempConnection);
        }
        return tempRepository;
    }

    /**
     * Creates a temporary repository that contains the content from the reader.
     * 
     * @param reader the content
     * @param syntax the mime type of the syntax
     * @param baseURI this uri is used to resolve any relative uris in the
     *        content
     */
    private Repository readFromReaderToTempRepository(
        Reader reader,
        RDFFormat format,
        URI baseURI) throws StandaloneValidatorException {
        Repository tempRepository = new SailRepository(new MemoryStore());
        RepositoryConnection tempConnection = null;

        try {
            tempRepository.initialize();
            tempConnection = tempRepository.getConnection();
            tempConnection.add(reader, baseURI.toString(), format);
        } catch (Exception e) {
            throw new StandaloneValidatorException(e);
        } finally {
            closeConnection(tempConnection);
        }
        return tempRepository;
    }
    
    private Repository readFromModelToTempRepository(Model inputModel) throws StandaloneValidatorException {
        Repository tempRepository = null ; 
        
        Model tempModel = null; 
        
        ClosableIterator<? extends org.ontoware.rdf2go.model.Statement> iterator = null;
        try {
            tempRepository = new SailRepository(new MemoryStore());
            tempRepository.initialize();
            tempModel = new RepositoryModel(tempRepository);
            tempModel.open();
            iterator = inputModel.iterator();
            tempModel.addAll(iterator);
        } catch (Exception e) {
            throw new StandaloneValidatorException(e);
        } finally {
            closeClosableIterator(iterator);
            closeModel(tempModel);
        }
        return tempRepository;
    }

    private void addTempRepositoryToRawRepository(
        Repository tempRepository,
        URI contextURI) {
        RepositoryConnection connection = null;
        RepositoryConnection tempConnection = null;
        RepositoryResult<Statement> res = null;
        try {
            connection = infRepository.getConnection();
            tempConnection = tempRepository.getConnection();
            res = tempConnection.getStatements(null, null, null, false);
            connection.add(res, contextURI);
        } catch (RepositoryException e) {
            log.warn("Couldn't add an ontology to the repository", e);
        } finally {
            closeIteration(res);
            closeConnection(tempConnection);
            closeConnection(connection);
        }
    }

    private RDFFormat stringToFormat(String string) {
        if (string == null) {
            throw new NullPointerException("the string cannot be null");
        } else if (string.equals(RDFFormat.N3.getDefaultMIMEType())) {
            return RDFFormat.N3;
        } else if (string.equals(RDFFormat.NTRIPLES.getDefaultMIMEType())) {
            return RDFFormat.NTRIPLES;
        } else if (string.equals(RDFFormat.RDFXML.getDefaultMIMEType())) {
            return RDFFormat.RDFXML;
        } else if (string.equals(RDFFormat.TURTLE.getDefaultMIMEType())) {
            return RDFFormat.TURTLE;
        } else if (string.equals(RDFFormat.TRIX.getDefaultMIMEType())) {
            throw new UnsupportedRDFormatException("Context-aware syntaxes aren't supported at the moment");
        } else if (string.equals(RDFFormat.TRIG.getDefaultMIMEType())) {
            throw new UnsupportedRDFormatException("Context-aware syntaxes aren't supported at the moment");
        } else {
            throw new UnsupportedRDFormatException(string);
        }
    }

    private void closeConnection(RepositoryConnection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (RepositoryException re) {
                log.warn("Couldn't close a connection", re);
            }
        }
    }

    private void closeIteration(CloseableIteration iter) {
        if (iter != null) {
            try {
                iter.close();
            } catch (Exception e) {
                log.warn("Couldn't close an iteration", e);
            }
        }
    }

    private void closeStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                log.warn("Couldn't close a stream", e);
            }
        }
    }
    
    private void closeClosableIterator(ClosableIterator<? extends org.ontoware.rdf2go.model.Statement> iterator) {
        if (iterator != null) {
            try {
                iterator.close();
            } catch (Exception e) {
                log.warn("Couldn't close a closable iterator",e);
            }
        }
    }
}
