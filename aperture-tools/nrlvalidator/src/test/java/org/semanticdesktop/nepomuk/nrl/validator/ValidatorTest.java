package org.semanticdesktop.nepomuk.nrl.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testres.TESTRESOURCES;

public class ValidatorTest {
    
    public void doTest(String ontology, String ontologyUri, String instance, int errors) throws Exception {
        StandaloneValidator validator = new StandaloneValidatorImpl();
        InputStream stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RES_PACKAGE_NAME + "/" + ontology);
        assertNotNull(stream);
        validator.addOntology(stream, RDFFormat.TURTLE.getDefaultMIMEType(), ontologyUri);
        stream = ClassLoader.getSystemResourceAsStream(TESTRESOURCES.RES_PACKAGE_NAME + "/" + instance);
        
        ModelTester tester = new NRLClosedWorldModelTester();
        validator.setModelTesters(tester);
        ValidationReport report = validator.validate(stream, RDFFormat.TURTLE.getDefaultMIMEType());
        printValidationReport(report);
        assertEquals(errors, report.getMessages().size());
    }
    
    public void printValidationReport(ValidationReport report) {
        System.out.println("Validation report");
        List<ValidationMessage> messages = report.getMessages();
        int i = 1;
        for (ValidationMessage msg : messages) {
            System.out.print("" + i + ": ");
            System.out.print(msg.getMessageType().toString() + " ");
            System.out.print(msg.getMessageTitle() + " ");
            System.out.print(msg.getMessage() + " ");
            for (Statement stmt : msg.getStatements()) {
                System.out.print("{"
                    + stmt.getSubject().toString()
                    + ","
                    + stmt.getPredicate().toString()
                    + ","
                    + stmt.getObject().toString()
                    + "}");
            }
            System.out.println();
            i++;
        }
    }

}
