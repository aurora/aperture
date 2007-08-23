/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import java.util.List;

import junit.framework.TestCase;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NID3;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.semanticdesktop.aperture.vocabulary.TAGGING;
import org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidator;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;

public class ApertureTestBase extends TestCase {

	public static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";
    
    public static StandaloneValidator validator;

	public Model createModel() {
		try {
            Model model = RDF2Go.getModelFactory().createModel(); 
            model.open();
			return model;
		}
		catch (ModelRuntimeException me) {
			return null;
		}
	}
    
    protected RDFContainer createRDFContainer(String uri) {
        return createRDFContainer(new URIImpl(uri,false));
    }
    
    protected RDFContainer createRDFContainer(URI uri) {
        Model newModel = createModel();
        return new RDFContainerImpl(newModel,uri);
    }

	public void checkStatement(URI property, String substring, RDFContainer container) 
			throws ModelException {
		// setup some info
		String uriString = container.getDescribedUri().toString();
		Model model = container.getModel();
		ValueFactory valueFactory = container.getValueFactory();
		boolean encounteredSubstring = false;

		// loop over all statements that have the specified property uri as predicate
        ClosableIterator<? extends Statement> statements = model.findStatements(valueFactory
                .createURI(uriString), property, Variable.ANY);
        try {
			while (statements.hasNext()) {
				// check the property type
				Statement statement = (Statement) statements.next();
				assertTrue(statement.getPredicate().equals(property));

				// see if it has a Literal containing the specified substring
				Node object = statement.getObject();
				if (object instanceof Literal) {
					String value = ((Literal) object).getValue();
					if (value.indexOf(substring) >= 0) {
						encounteredSubstring = true;
						break;
					}
				}
			}
		}
		finally {
			statements.close();
		}

		// see if any of the found properties contains the specified substring
        if (!encounteredSubstring)
            fail("Expected substring '"+substring+
                "' in property "+property+" not found");
		assertTrue(encounteredSubstring);
	}
    
    public void checkSimpleContact(URI property, String fullname, RDFContainer container) {
        Node node = container.getNode(property);
        assertTrue(node instanceof Resource);
        Resource resource = (Resource)node;
        Model model = container.getModel();
        assertTrue(model.contains(resource, RDF.type, NCO.Contact));
        assertTrue(model.contains(resource, NCO.fullname, fullname));
    }
    
    public void checkMultipleSimpleContacts(URI property, String fullname, RDFContainer container) {
        Model model = container.getModel();
        QueryResultTable table = model.sparqlSelect(
                "PREFIX nco: <" + NCO.NS_NCO + "> " +
                "SELECT ?contact " +
                "WHERE" +
                "  {" + container.getDescribedUri().toSPARQL() + " " + property.toSPARQL() + " ?contact ." +
                "    ?contact nco:fullname ?name . " +
                "    FILTER (regex(?name,\"" + fullname + "\"))" +
                "  }");
        ClosableIterator<QueryRow> iterator = null;
        try {
            iterator = table.iterator();
            assertTrue(iterator.hasNext());;
        } finally {
            iterator.close();
        }
    }

    public void checkStatement(URI property, URI value, RDFContainer container) 
			throws ModelException {
		URI subject = container.getDescribedUri(); 
		checkStatement(subject, property, value, container);
	}

	public void checkStatement(URI subject, URI property, Node value, RDFContainer container) 
			throws ModelException {
		checkStatement(subject, property, value, container.getModel());
	}

	public void checkStatement(URI subject, URI property, Node value, Model model) throws ModelException {
		boolean encounteredValue = false;

		// loop over all statements that have the specified property uri as predicate
        ClosableIterator<? extends Statement> statements = model.findStatements(subject,property,Variable.ANY);
		try {
			while (statements.hasNext()) {
				// check the property type
				Statement statement = (Statement) statements.next();
				assertTrue(statement.getPredicate().equals(property));

				// see if it has a Literal containing the specified substring
				Node object = statement.getObject();
				if (object.equals(value)) {
					encounteredValue = true;
					break;
				}
			}
		}
		finally {
			statements.close();
		}

		// see if any of the found properties contains the specified substring
		assertTrue(encounteredValue);
	}
    
    public void validate(RDFContainer container) {
        validate(container,true);
    }
    
    public void validate(RDFContainer container, boolean print) {
        validate(container.getModel(), print);
    }

    public void validate(Model model) {
        validate(model,true);
    }
    
    public void validate(Model model, boolean print) {
        try {
            if (validator == null) {
                initializeValidator();
            }
            ValidationReport report = validator.validate(model);
            if ( !report.isValid() || (report.getMessages().size() > 0 && print)) {
                printValidationReport(report);
            }
            if (!report.isValid()) {
                fail();
            } 
        } catch (StandaloneValidatorException sve) {
            sve.printStackTrace();
            if (sve.getCause() != null) {
                sve.getCause().printStackTrace();
            }
            fail();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void initializeValidator() throws Exception {
        validator = new StandaloneValidatorImpl();        
        Model tempModel  = RDF2Go.getModelFactory().createModel();
        tempModel.open();
        
        NIE.getNIEOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NIE.NS_NIE));
        tempModel.removeAll();
        
        NCO.getNCOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NCO.NS_NCO));
        tempModel.removeAll();
        
        NFO.getNFOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NFO.NS_NFO));
        tempModel.removeAll();
        
        NMO.getNMOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NMO.NS_NMO));
        tempModel.removeAll();
        
        NCAL.getNCALOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NCAL.NS_NCAL));
        tempModel.removeAll();
        
        NEXIF.getNEXIFOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NEXIF.NS_NEXIF));
        tempModel.removeAll();
        
        NID3.getNID3Ontology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(NID3.NS_NID3));
        tempModel.removeAll();
        
        TAGGING.getTAGGINGOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(TAGGING.NS_TAGGING));
        tempModel.removeAll();
        
        GEO.getGEOOntology(tempModel);
        validator.addOntology(tempModel, getOntUriFromNs(TAGGING.NS_TAGGING));
        tempModel.removeAll();
        
        tempModel.close();
        
        validator.setModelTester(new NRLClosedWorldModelTester());
    }
    
    private String getOntUriFromNs(URI uri) {
        return uri.toString().substring(0,uri.toString().length() - 1);
    }

    private void printValidationReport(ValidationReport report) {
        System.out.println("Validation report");
        List<ValidationMessage> messages = report.getMessages();
        int i = 1;
        for (ValidationMessage msg : messages) {
            System.out.print  ("" + i + ": ");
            System.out.println(msg.getMessageType().toString() + " ");
            System.out.println("   " + msg.getMessageTitle() + " ");
            System.out.println("   " + msg.getMessage() + " ");
            for (Statement stmt : msg.getStatements()) {
                System.out.println("   {" + stmt.getSubject().toSPARQL() + ",");
                System.out.println("    " + stmt.getPredicate().toSPARQL() + ",");
                System.out.println("    " + stmt.getObject().toSPARQL() + "}");
            }
            i++;
        }
    }
}
