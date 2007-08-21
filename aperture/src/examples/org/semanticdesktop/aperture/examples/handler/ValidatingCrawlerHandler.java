/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.handler;

import java.io.File;
import java.util.List;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.nepomuk.nrl.validator.StandaloneValidator;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationReport;
import org.semanticdesktop.nepomuk.nrl.validator.exception.StandaloneValidatorException;

/**
 * An an extension of the SimpleCrawlerHandler that validates each DataObject
 */
public class ValidatingCrawlerHandler extends SimpleCrawlerHandler {

    private StandaloneValidator validator;

    /**
     * Constructor.
     * 
     * @param identifyingMimeType 'true' if the crawler is to use a MIME type identifier on each
     *            FileDataObject it gets, 'false' if not
     * @param extractingContents 'true' if the crawler is to use an extractor on each DataObject it gets
     *            'false' if not
     * @param verbose 'true' if the crawler is to print verbose messages on what it is doing, false otherwise
     * @param outputFile the file where the extracted RDF metadata is to be stored. This argument can also be
     *            set to 'null', in which case the RDF metadata will not be stored in a file. This setting is
     *            useful for performance measurements.
     * @throws ModelException
     */
    public ValidatingCrawlerHandler(boolean identifyingMimeType, boolean extractingContents, boolean verbose, File outputFile)
            throws ModelException {
        super(identifyingMimeType,extractingContents,verbose,outputFile);
    }

    /**
     * This method gets called when the crawler has encountered a new DataObject
     * 
     * @param dataCrawler the crawler
     * @param object the DataObject
     */
    public void objectNew(Crawler dataCrawler, DataObject object) {
        super.objectNew(dataCrawler, object);
        try {
            Model model = object.getMetadata().getModel();
            boolean wasOpen = model.isOpen();
            
            if (!wasOpen) {
                model.open();
            }
            
            ValidationReport report = validator.validate(object.getMetadata().getModel());
            System.out.println("Validation report for: " + object.getID());
            printValidationReport(report);
            
            if (!wasOpen) {
                model.close();
            }
        }
        catch (StandaloneValidatorException e) {
            System.err.println("validation failed uri: " + object.getID());
            e.printStackTrace();
        }
    }

    private void printValidationReport(ValidationReport report) {
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
