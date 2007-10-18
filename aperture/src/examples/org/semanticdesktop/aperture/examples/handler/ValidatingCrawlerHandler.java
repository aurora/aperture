/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.handler;

import java.io.File;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.ExitCode;
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
import org.semanticdesktop.nepomuk.nrl.validator.testers.DataObjectTreeModelTester;
import org.semanticdesktop.nepomuk.nrl.validator.testers.NRLClosedWorldModelTester;

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
    public ValidatingCrawlerHandler(boolean identifyingMimeType, boolean extractingContents, boolean verbose, File outputFile) {
        super(identifyingMimeType, extractingContents, verbose, outputFile);
        try {
            initializeValidator();
        }
        catch (StandaloneValidatorException sve) {
            throw new RuntimeException(sve);
        }
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

            validator.setModelTesters(new NRLClosedWorldModelTester());
            ValidationReport report = validator.validate(object.getMetadata().getModel());
            if (report.getMessages().size() > 0) {
                System.out.println("Validation report for: " + object.getID());
                printValidationReport(report);
            }

            if (!wasOpen) {
                model.close();
            }
        }
        catch (StandaloneValidatorException e) {
            System.err.println("validation failed uri: " + object.getID());
            e.printStackTrace();
        }
    }
    

    /**
     * @see SimpleCrawlerHandler#crawlStopped(Crawler, ExitCode)
     */
    @Override
    public void crawlStopped(Crawler crawler, ExitCode code) {

        
        Model overallModel = RDF2Go.getModelFactory().createModel();
        overallModel.open();
        ModelSet modelSet = getModelSet();
        ClosableIterator<? extends Statement> iterator = modelSet.iterator();
        
        while (iterator.hasNext()) {
            Statement statement = iterator.next();
            overallModel.addStatement(statement);
        }
        validator.setModelTesters(new NRLClosedWorldModelTester(), new DataObjectTreeModelTester());
        try {
            ValidationReport report = validator.validate(overallModel);
            if (report.getMessages().size() > 0) {
                System.out.println("Tree structure validation report:");
                printValidationReport(report);
            }
        }
        catch (StandaloneValidatorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        super.crawlStopped(crawler, code);
    }

    private void printValidationReport(ValidationReport report) {
        List<ValidationMessage> messages = report.getMessages();
        int i = 1;
        for (ValidationMessage msg : messages) {
            System.out.print("" + i + ": ");
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

    private void initializeValidator() throws StandaloneValidatorException {
        validator = new StandaloneValidatorImpl();
        Model tempModel = RDF2Go.getModelFactory().createModel();
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

        validator.setModelTesters(new NRLClosedWorldModelTester());
    }
    
    private String getOntUriFromNs(URI uri) {
        return uri.toString().substring(0, uri.toString().length() - 1);
    }
}
