package org.semanticdesktop.nepomuk.nrl.validator.testers;

import java.io.IOException;
import java.util.ArrayList;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage.MessageType;
import org.semanticdesktop.nepomuk.nrl.validator.exception.ModelTesterException;
import org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.XSDDatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NRL implementation of the repository tester.
 * 
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */

/**
 * Command line application to validate NRL files serialized as TriG the
 * validator was developed to work with the NRL inferencer developed by Gunnar
 * Grimmes. Currently, the NRL inferencer does not support SELECT queries.
 * Additionally, the NRL inferencer does not support nrl:imports. So, it is not
 * being used but it is fully integrated. Missing features are marked with TBD
 * (to-be-done)
 * 
 * @author <a href="milena.caires@deri.org">Milena Constantino Caires</a>
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 * @email milena.caires@deri.org
 * @date 29th June 2007
 */
public class NRLClosedWorldModelTester implements ModelTester {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NRL_NS = "http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#";

    // this one is not fixed (07.07.2007)
    private static final String NAO_NS = "http://www.semanticdesktop.org/ontologies/2007/08/15/nao#";
    
    private static final URI NRL_CARDINALITY_URI = new URIImpl(NRL_NS + "cardinality");
    
    private static final URI NRL_MIN_CARDINALITY_URI = new URIImpl(NRL_NS + "minCardinality");
    
    private static final URI NRL_MAX_CARDINALITY_URI = new URIImpl(NRL_NS + "maxCardinality");

    /** The union model */
    private Model _unionModel;

    /** The data model */
    private Model _dataModel;

    /** The validation report */
    private ValidationReport _report;

    /**
     * @see ModelTester#performTests(Model, Model)
     */
    public ValidationReport performTests(Model unionModel, Model dataModel) throws ModelTesterException {
        ValidationReport report = new ValidationReport();
        performTests(unionModel, dataModel, report);
        return report;
    }
    
    /**
     * @see ModelTester#performTests(Model, Model, ValidationReport)
     */
    public void performTests(Model unionModel, Model dataModel, ValidationReport report)
        throws ModelTesterException {

        _unionModel = unionModel;
        _dataModel = dataModel;
        _report = report;
        
        try {

            // Basic checks (isDefinedBy, BlankNodes and Containers)
            // Check isDefinedBy
            checkIsDefinedBy();

            // Check Blank Nodes
            checkBNodes();

            // Check Containers
            checkContainers();

            // check domains and ranges
            checkDomainsAndRanges();

            // TBD
            // Cardinality checks (maxCardinality and minCardinality)
            checkCardinality();

        } catch (Exception mqe) {
            throw new ModelTesterException(mqe);
        }
    }
    
    private void checkIsDefinedBy()
        throws IOException,
        ModelException,
        ModelRuntimeException {

        ClosableIterator<? extends Statement> iter = null;

        try {
            iter = _dataModel.findStatements(
                Variable.ANY,
                RDFS.isDefinedBy,
                Variable.ANY);
            while (iter.hasNext()) {
                Statement st = iter.next();
                repErrInvD("RDFS.isDefinedBy is legal but not valid NRL", st);
            }
        } finally {
            closeIterator(iter);
        }
    }

    private void checkBNodes() throws ModelTesterException {
        String blankFilter = "FILTER ( isBlank(?s) || isBlank(?o) )";
        String sparqlQuery = "CONSTRUCT {?s ?p ?o} \n"
            + ((_dataModel.getContextURI() != null) ? ("WHERE { GRAPH <"
                + _dataModel.getContextURI()
                + "> "
                + "{?s ?p ?o . "
                + blankFilter + "}} ") : "WHERE { ?s ?p ?o . "
                + blankFilter
                + "} ");

        ClosableIterable<? extends Statement> iterable = null;
        ClosableIterator<? extends Statement> iterator = null;

        try {
            iterable = _dataModel.sparqlConstruct(sparqlQuery);
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Statement st = iterator.next();
                /*
                 * TF-Ont decided at the Karlsruhe meeting on 15.08.2007 that
                 * blank nodes should be reported as warnings, not as errors.
                 */
                _report.addMessage(
                    MessageType.WARNING,
                    "INVALID DATA",
                    "Blank Nodes are not valid NRL",
                    st);
            }
        } catch (Exception e) {
            throw new ModelTesterException(e);
        } finally {
            closeIterator(iterator);
        }
    }

    private void checkContainers() throws ModelTesterException {
        String containersFilter = "FILTER ( (?o = rdf:Bag) || (?o = rdf:Seq) || (?o = rdf:Alt))";
        String graph = "GRAPH <" + _dataModel.getContextURI() + ">";
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
            + "CONSTRUCT {?s ?p ?o} "
            + ((_dataModel.getContextURI() != null) ? 
               ("WHERE { " + graph + " {?s ?p ?o . " + containersFilter + "}} ") : 
               ("WHERE { ?s ?p ?o . " + containersFilter + "} "));

        ClosableIterable<? extends Statement> iterable = null;
        ClosableIterator<? extends Statement> iterator = null;

        try {
            iterable = _dataModel.sparqlConstruct(query);
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Statement st = iterator.next();

                String subject = st.getSubject().toString();
                String object = st.getObject().toString();

                if (subject != null && object != null) {
                    if (object.toString().equals(RDF.Bag.toString())) {
                        _report.addMessage(
                            MessageType.WARNING,
                            "CONTAINER WARNING",
                            "RDF.Bag is not valid NRL",
                            st);
                    } else if (object.toString().equals(RDF.Seq.toString())) {
                        _report.addMessage(
                            MessageType.WARNING,
                            "CONTAINER WARNING",
                            "RDF.Seq is not valid NRL",
                            st);
                    } else if (object.toString().equals(RDF.Alt.toString())) {
                        _report.addMessage(
                            MessageType.WARNING,
                            "CONTAINER WARNING",
                            "RDF.Alt is not valid NRL",
                            st);
                    }
                }
            }
        } catch (Exception e) {
            throw new ModelTesterException(e);
        } finally {
            closeIterator(iterator);
        }
    }
    
    private void checkDomainsAndRanges() throws IOException { 
        
        // Select all the predicates (except rdf: rdfs: nao: nrl:)
        String predicateFilter = 
            "FILTER (" +
                "!(regex(str(?p), \"" + RDF.RDF_NS + "\")) &&" +
                "!(regex(str(?p), \"" + RDFS.RDFS_NS + "\")) &&" +
                "!(regex(str(?p), \"" + NAO_NS + "\")) &&" +
                "!(regex(str(?p), \"" + NRL_NS + "\")) &&" +
                "!(regex(str(?p), \"" + RDF.RDF_NS + "\")))";
        String graph = "GRAPH <" + _dataModel.getContextURI() + ">";
        String query = "CONSTRUCT {?s ?p ?o} "
            + ((_dataModel.getContextURI() != null) ? 
               ("WHERE { " + graph + " {?s ?p ?o . " + predicateFilter + "}} ") : 
               ("WHERE { ?s ?p ?o . " + predicateFilter + "} "));
            
        ClosableIterable<? extends Statement> iterable = null;
        ClosableIterator<? extends Statement> iterator = null;
        
        try {
            iterable = _dataModel.sparqlConstruct(query);
            iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Statement st = iterator.next();
                Resource propertySubject = st.getSubject();
                URI property = st.getPredicate();
                Node propertyValue = st.getObject();
                boolean propertyDefined = checkIfAResourceHasAGivenType(property, RDF.Property);
                if (propertyDefined) {
                    Resource propertyDomain = retrieve_domain(property);
                    Resource propertyRange = retrieve_range(property);
                    
                    if (propertyDomain != null) {
                        // Check Domain
                        checkDomain(st, property, propertySubject, propertyDomain);
                    }
                    
                    if (propertyRange != null) {
                        // Check range
                        checkRange(st, property, propertyValue, propertyRange);
                    }
                    
                } else {
                    _report.addMessage(
                        MessageType.WARNING,
                        "CWA WARNING",
                        "Property not defined, cannot be validated",
                        st);
                }
            }
        } finally {
            closeIterator(iterator);
        }
    }

    private void checkDomain(Statement st, URI property, Resource propertySubject, Resource propertyDomain) {
        if (propertyDomain == null) {
            _report.addMessage(
                MessageType.ERROR,
                "INVALID DATA",
                "Domain not specified for " + property,
                st);
        } else if (propertyDomain instanceof URI) {
            URI domainURI = (URI)propertyDomain;
            if (!checkIfAResourceHasAGivenType(propertySubject, domainURI)) {
                _report.addMessage(
                    MessageType.ERROR,
                    "INVALID DATA",
                    "Property domain invalid. The subject should be instance of "+domainURI,
                    st);
            }
        } else {
            _report.addMessage(
                MessageType.ERROR,
                "INVALID DATA",
                "Property domain is a blank node",
                st);
        }
    }

    private void checkRange(Statement st, URI property, Node propertyValue, Resource propertyRange) {
        if (propertyRange == null) {
            _report.addMessage(
                MessageType.ERROR,
                "INVALID ONTOLOGY",
                "Range not specified for " + property,
                st);
        } else if (propertyRange instanceof URI){ // Check range: uri or Literal
            // (Blank nodes were checked before)
            URI rangeURI = (URI) propertyRange;
            if (propertyValue instanceof Resource) {
                /*
                 * At the meeting in Karlsruhe on 15.08.2007 TF-Ont decided that it
                 * is illegal to have lists of values where single values should
                 * appear. In other words if a property has a range of ex:Cookie
                 * only a single cookie can appear and a list of cookies is considered
                 * an error. 
                 */
                if (!checkIfAResourceHasAGivenType((Resource)propertyValue,rangeURI)) {
                    _report.addMessage(
                        MessageType.ERROR,
                        "INVALID DATA",
                        "Property range violated, the object should be an instance of " + rangeURI.toString(),
                        st);
                }
            } else {
                // if a node is not a resource, then it must be a literal...
                checkLiteralRange((Literal)propertyValue,rangeURI,st);
            }
        } else {
            _report.addMessage(
                MessageType.ERROR,
                "INVALID ONTOLOGY",
                "Range of this property has been specified as a blank node",
                st);
        }
    }

    private boolean checkIfAResourceHasAGivenType(Resource resource, URI typeUri) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = _unionModel.findStatements(resource, RDF.type, typeUri);
            if (iterator.hasNext()) {
                return true;
            } else {
                return false;
            }
        } finally {
            closeIterator(iterator);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean checkLiteralRange(Literal value, URI expectedRange, Statement st) {
        boolean result = false;
        if (expectedRange.equals(RDFS.Literal)) {
            return true;
        } else if (!(expectedRange.toString().startsWith(XSD.XSD_NS))) {
            repErrInvD("The object of a statement should be an instance of " + expectedRange, st);
            return false;
        } else if (expectedRange.equals(XSD._string) && (isLanguageLiteral(value) || isPlainLiteral(value))) {
            return true;
        } else if (!isDatatypeLiteral(value)) {
            repErrInvD("Expected literal with a datatype xsd:" + getLocalName(expectedRange), st);
            return false;
        } else {
            DatatypeLiteral dataTypeLit = value.asDatatypeLiteral();
            URI typeUri = dataTypeLit.getDatatype();
            if (typeUri != null) {
                if (typeUri.equals(XSD._duration)) {
                    _report.addMessage(
                        MessageType.WARNING,
                        "xsd:duration warning",
                        "Usage of xsd:duration datatype is discouraged.",
                        st);
                }
                boolean typeCorrect = XSDDatatypeHelper
                    .isCorrect(dataTypeLit.getDatatype(), expectedRange);
                boolean stringCorrect = XSDDatatypeHelper
                    .isCorrectString(dataTypeLit);
                if (typeCorrect && stringCorrect) {
                    return true;
                } else if (!typeCorrect) {
                    repErrInvD("Literal datatype invalid, expected xsd:"+getLocalName(expectedRange), st);
                    result = false;
                } else if (!stringCorrect) {
                    repErrInvD("The literal is invalid for the given datatype", st);
                    result = false;
                }
            } else {
                repErrInvD("Expected datatype literal", st);
                result = false;
            }
        }
        return result;
    }

    private String getLocalName(URI typeUri) {
        String string = typeUri.toString();
        int hashIndex = string.indexOf('#');
        if (hashIndex == -1) {
            hashIndex = string.lastIndexOf('/');
        }
        if (hashIndex != -1) {
            return string.substring(hashIndex + 1,string.length());
        } else {
            return "";
        }
    }

    private boolean isPlainLiteral(Literal value) {
        try {
            @SuppressWarnings("unused")
            PlainLiteral plLit = (PlainLiteral)value;
            return true;
        } catch (ClassCastException cce) {
            return false;
        }
    }

    private void repErrInvD(String string, Statement st) {
        _report.addMessage(
            MessageType.ERROR,
            "INVALID DATA",
            string,
            st);
    }

    private boolean isDatatypeLiteral(Literal value) {
        try {
            DatatypeLiteral dataTypeLit = value.asDatatypeLiteral();
            return (dataTypeLit.getDatatype() != null);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    private boolean isLanguageLiteral(Literal value) {
        try {
            @SuppressWarnings("unused")
            LanguageTagLiteral langLit = value.asLanguageTagLiteral();
            // there was no exception, so ok
            return true;
        } catch (ClassCastException cce) {
            // this obviously means that this is not a language tag literal
            return false;
        }
    }

    private Resource retrieve_range(Resource resource) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = _unionModel.findStatements(resource, RDFS.range, Variable.ANY);
            if (!iterator.hasNext()) {
                _report.addMessage(MessageType.WARNING,"Ontology warning","The property " 
                    + resource + " doesn't have a range");
                return null;
            } else {
                Statement st = iterator.next();
                Node node = st.getObject();
                if (!(node instanceof Resource)) {
                    logger.warn("Weird, range of " + resource + " is a blank node");
                    return null;
                } else {
                    return (Resource)node;
                }
            }
        } finally {
            closeIterator(iterator);
        }
    }

    private Resource retrieve_domain(Resource resource) {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = _unionModel.findStatements(resource, RDFS.domain, Variable.ANY);
            if (!iterator.hasNext()) {
                _report.addMessage(MessageType.ERROR,"Ontology error","The property " 
                    + resource + " doesn't have a domain");
                return null;
            } else {
                Statement st = iterator.next();
                Node node = st.getObject();
                if (!(node instanceof Resource)) {
                    logger.warn("Weird, domain of " + resource + " is a blank node");
                    return null;
                } else {
                    return (Resource)node;
                }
            }
        } finally {
            closeIterator(iterator);
        }
    }
    
    private int retrieveMinCardinality(Resource resource) {
      ClosableIterator<? extends Statement> iterator = null;
      try {
          iterator = _unionModel.findStatements(resource, NRL_MIN_CARDINALITY_URI, Variable.ANY);
          if (!iterator.hasNext()) {
              return 0;
          } else {
              Statement st = iterator.next();
              Node node = st.getObject();
              if (!(node instanceof Literal)) {
                  logger.warn("Weird, minimum cardinality of " + resource + " is not a literal");
                  return 0;
              } else if (!checkLiteralRange(node.asLiteral(), RDFS.Literal, st)){
                  return 0;
              } else {
                  return Integer.parseInt(node.asLiteral().getValue());
              }
          }
      } finally {
          closeIterator(iterator);
      }
    }
    
    private int retrieveMaxCardinality(Resource resource) {
      ClosableIterator<? extends Statement> iterator = null;
      try {
          iterator = _unionModel.findStatements(resource, NRL_MAX_CARDINALITY_URI, Variable.ANY);
          if (!iterator.hasNext()) {
              return 0;
          } else {
              Statement st = iterator.next();
              Node node = st.getObject();
              if (!(node instanceof Literal)) {
                  logger.warn("Weird, maximum cardinality of " + resource + 
                      " is not a literal");
                  return 0;
              } else if (!checkLiteralRange(node.asLiteral(), RDFS.Literal, st)){
                return 0;
              } else {
                  return Integer.parseInt(node.asLiteral().getValue());
              }
          }
      } finally {
          closeIterator(iterator);
      }
    }
    
    private int retrieveCardinality(Resource resource) {
      ClosableIterator<? extends Statement> iterator = null;
      try {
          iterator = _unionModel.findStatements(resource, NRL_CARDINALITY_URI, Variable.ANY);
          if (!iterator.hasNext()) {
              return 0;
          } else {
              Statement st = iterator.next();
              Node node = st.getObject();
              if (!(node instanceof Literal)) {
                  logger.warn("Weird, cardinality of " + resource + 
                      " is not a literal");
                  return 0;
              } else if (!checkLiteralRange(node.asLiteral(), RDFS.Literal, st)){
                return 0;
              } else {
                  return Integer.parseInt(node.asLiteral().getValue());
              }
          }
      } finally {
          closeIterator(iterator);
      }
    }

    private void checkCardinality() {
      // Select all the predicates (except rdf: rdfs: nao: nrl:)
      String predicateFilter = 
          "FILTER (" +
              "!(regex(str(?p), \"" + RDF.RDF_NS + "\")) &&" +
              "!(regex(str(?p), \"" + RDFS.RDFS_NS + "\")) &&" +
              "!(regex(str(?p), \"" + NAO_NS + "\")) &&" +
              "!(regex(str(?p), \"" + NRL_NS + "\")) &&" +
              "!(regex(str(?p), \"" + RDF.RDF_NS + "\")))";
      String graph = "GRAPH <" + _dataModel.getContextURI() + ">";
      String query = "SELECT DISTINCT ?s ?p "
          + ((_dataModel.getContextURI() != null) ? 
             ("WHERE { " + graph + " { ?s ?p ?o . " + predicateFilter + "}} ") : 
             ("WHERE { ?s ?p ?o . " + predicateFilter + "} "));
          
      QueryResultTable iterable = null;
      ClosableIterator<? extends QueryRow> iterator = null;
      
      try {
          iterable = _dataModel.sparqlSelect(query);
          iterator = iterable.iterator();
          while (iterator.hasNext()) {
              QueryRow row = iterator.next();
              Resource s = row.getValue("s").asResource();
              URI p = row.getValue("p").asURI();
              if (s instanceof BlankNode)
              {
            	  _report.addMessage(
                          MessageType.ERROR,
                          "INVALID DATA",
                          "Found a blank node while testing cardinality. subject="+s+" predicate="+p);
            	  continue;
              } 
              Statement[] statements = retrieveStatementBlock(s, p);
              boolean propertyDefined = checkIfAResourceHasAGivenType(p, RDF.Property);
              if (propertyDefined) {
                  int propertyCard = retrieveCardinality(p);
                  int propertyMinCard = retrieveMinCardinality(p);
                  int propertyMaxCard = retrieveMaxCardinality(p);
                  
                  if (propertyCard > 0) {
                      // Check Cardinality
                      if (statements.length != propertyCard)
                        _report.addMessage(
                            MessageType.ERROR,
                            "INVALID DATA",
                            "Cardinality for " + p + " must be " + propertyCard,
                            statements);
                  }
                  
                  if (propertyCard > 0 && (propertyMinCard > 0 || propertyMaxCard > 0))
                      // Check if either cardinality or minimum cardinality and
                      // maximum cardinality is defined
                      _report.addMessage(
                          MessageType.ERROR,
                          "INVALID DATA",
                          "Property "+p+" cannot have a fixed cardinality and a minimum " +
                              "or maximum cardinality",
                          statements);
                  
                  if (propertyMinCard > 0 && propertyMaxCard > 0 && 
                      propertyMinCard > propertyMaxCard)
                      // Check if minimum cardinality is lower than maximum cardinality
                      _report.addMessage(
                          MessageType.ERROR,
                          "INVALID DATA",
                          "Minimum cardinality cannot be higher than maximum " +
                          "cardinality for " + p,
                          statements);
                  
                  if (propertyMinCard > 0) {
                      // Check minimum cardinality
                      if (statements.length < propertyMinCard)
                        _report.addMessage(
                            MessageType.ERROR,
                            "INVALID DATA",
                            "Minimum cardinality for " + p + " is " + propertyMinCard,
                            statements);
                  }
                  
                  if (propertyMaxCard > 0) {
                      // Check maximum cardinality
                      if (statements.length > propertyMaxCard)
                        _report.addMessage(
                            MessageType.ERROR,
                            "INVALID DATA",
                            "Maximum cardinality for " + p + " is " + propertyMaxCard,
                            statements);
                  }
                  
              } else {
                  _report.addMessage(
                      MessageType.WARNING,
                      "CWA WARNING",
                      "Property not defined, cannot be validated",
                      statements);
              }
          }
      } finally {
          closeIterator(iterator);
      }
    }
    
    private Statement[] retrieveStatementBlock(Resource s, URI p) {
      ArrayList<Statement> statements = new ArrayList<Statement>();
      String graph = "GRAPH <" + _dataModel.getContextURI() + ">";
      String query = "CONSTRUCT {"+s.toSPARQL()+" "+p.toSPARQL()+" ?o} "
          + ((_dataModel.getContextURI() != null) ? 
             ("WHERE { " + graph + " {"+s.toSPARQL()+" "+p.toSPARQL()+" ?o . }} ") : 
             ("WHERE { ?s ?p ?o . } "));
          
      ClosableIterable<? extends Statement> iterable = null;
      ClosableIterator<? extends Statement> iterator = null;
      
      try {
          iterable = _dataModel.sparqlConstruct(query);
          iterator = iterable.iterator();
          while (iterator.hasNext()) {
              statements.add(iterator.next());
          }
      } finally {
          closeIterator(iterator);
      }
      Statement[] array = statements.toArray(new Statement[statements.size()]);
      return array;
    }

    private void closeIterator(ClosableIterator<? extends Object> iter) {
        if (iter != null) {
            try {
                iter.close();
            } catch (Exception e) {
                logger.warn("Couldn't close an iterator", e);
            }
        }
    }
}
