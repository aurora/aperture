package org.semanticdesktop.nepomuk.nrl.validator.testers;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.nepomuk.nrl.validator.ModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage.MessageType;
import org.semanticdesktop.nepomuk.nrl.validator.exception.ModelTesterException;

/**
 * A trivial model tester that checks if the data model contains ANY root elements. This makes no
 * guarantees as to whether the root elements are in any sane relation to other data objects in the
 * model.
 */
public class RootElementModelTester implements ModelTester {

	private static final String NIE_NS = "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#";
	private static final URI NIE_ROOT_ELEMENT_OF = new URIImpl(NIE_NS + "rootElementOf");
	
    public ValidationReport performTests(Model unionModel, Model dataModel) throws ModelTesterException {
        ValidationReport report = new ValidationReport();
        performTests(unionModel,dataModel,report);
        return report;
    }

    public void performTests(Model unionModel, Model dataModel, ValidationReport report)
            throws ModelTesterException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = dataModel.findStatements(Variable.ANY,NIE_ROOT_ELEMENT_OF,Variable.ANY);
            if (!iterator.hasNext()) {
                report.addMessage(MessageType.ERROR,
                    "ROOT ELEMENTS MISSING",
                    "The data model doesn't contain any root elements",
                    (Statement)null);
            }
        } finally {
            if (iterator != null) {
                try {
                    iterator.close();
                } catch (Exception e) {
                    // there is hardly anything that can be done at this point
                }
            }
        }
    }
}