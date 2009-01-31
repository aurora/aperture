package org.semanticdesktop.nepomuk.nrl.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.util.RDFTool;
import org.semanticdesktop.nepomuk.nrl.validator.impl.StandaloneValidatorImpl;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;

/**
 * A very simple console frontend to the standalone validator
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class ConsoleFrontend {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Give at least one ontology and a data file");
            System.exit(-1);
        }
        
        StandaloneValidator validator = new StandaloneValidatorImpl();
        validator.setModelTesters(new NRLClosedWorldModelTester());
        addOntologies(args, validator);
                
        File dataFile = new File(args[args.length - 1]);
        Syntax syntax = RDFTool.guessSyntax(dataFile.getName());
        ValidationReport report = validator.validate(dataFile, syntax.getMimeType());
        printValidationReport(report); 
    }
    
    private static void addOntologies(String[] args, StandaloneValidator validator) throws Exception {
        for (int i = 0; i<args.length - 1; i++) {
            File file = new File(args[i]);
            Syntax syntax = RDFTool.guessSyntax(args[i]);
            InputStream fileInputStream = new FileInputStream(file);
            String ontologyUri = "uri:ontology:" + i;
            validator.addOntology(fileInputStream, syntax.getMimeType(), ontologyUri);
        }
    }

    private static void printValidationReport(ValidationReport report) {
        System.out.println("Validation report");
        List<ValidationMessage> messages = report.getMessages();
        int i = 1;
        for (ValidationMessage msg : messages) {
            System.out.print  ("" + i + ": ");
            System.out.println(msg.getMessageType().toString() + " ");
            System.out.println("   " + msg.getMessageTitle() + " ");
            System.out.println("   " + msg.getMessage() + " ");
            for (Statement stmt : msg.getStatements()) {
                System.out.println("   {" + nodeToString(stmt.getSubject()) + ",");
                System.out.println("    " + nodeToString(stmt.getPredicate()) + ",");
                System.out.println("    " + nodeToString(stmt.getObject()) + "}");
            }
            i++;
        }
    }
    
    private static String nodeToString(Node node) {
        if (node instanceof BlankNode) {
            return "_:" + node.toString();
        } else {
            return node.toSPARQL();
        }
    }
}
