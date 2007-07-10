/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;

/**
 * Example class that crawls a file system and stores all extracted metadata in a RDF file.
 * @author fluit, sauermann, klinkigt
 */
public class ExampleFileCrawler {

    /** The command line flag that states if the MIME types of the encountered
     * DataObjects are to be identified or not. 
     * if the IDENTIFY_MIME_TYPE_OPTION is set (commando line call -identifyMimeType)
     * the mime type of the files will be detected 
     */
    public static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

    /** if the EXTRACT_CONTENTS_OPTION is set (commando line call -extractContents)
       the content of the files will be scanned */
    public static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

    /**
     * if the VERBOSE_OPTION is set (command line switch '-verbose') the crawler
     * will print more verbose information about what it's doing
     */
    public static final String VERBOSE_OPTION = "-verbose";

    /**
     * If the NOOUTPUT_OPTION is set (command line switch -nooutput), the crawler
     * will not save the generated RDF in a file
     */
    public static final String NOOUTPUT_OPTION = "-nooutput";

    private File rootFile;

    // the result will be stored in outputFile
    private File outputFile;

    private boolean identifyingMimeType = false;

    private boolean extractingContents = false;

    private boolean verbose = false;

    private boolean noOutput = false;

    
    public boolean isExtractingContents() {
        return extractingContents;
    }

    public boolean isIdentifyingMimeType() {
        return identifyingMimeType;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getRootFile() {
        return rootFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }

    public void setIdentifyingMimeType(boolean identifyingMimeType) {
        this.identifyingMimeType = identifyingMimeType;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void crawl() throws ModelException {
        if (rootFile == null) {
            throw new IllegalArgumentException("root file cannot be null");
        }
        if (outputFile == null) {
            throw new IllegalArgumentException("output file cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer configuration = factory.newInstance("source:testsource");
        ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);

        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(new SimpleCrawlerHandler(identifyingMimeType,extractingContents,verbose,outputFile));

        // start crawling
        crawler.crawl();
    }

    /**
     * The main method
     * @param args command line arguments
     * @throws ModelException
     */
    public static void main(String[] args) throws ModelException {
        // create a new ExampleFileCrawler instance
        ExampleFileCrawler crawler = new ExampleFileCrawler();

        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (IDENTIFY_MIME_TYPE_OPTION.equals(arg)) {
                crawler.setIdentifyingMimeType(true);
            }
            else if (EXTRACT_CONTENTS_OPTION.equals(arg)) {
                crawler.setExtractingContents(true);
            }
            else if (VERBOSE_OPTION.equals(arg)) {
                crawler.setVerbose(true);
            }
            else if (NOOUTPUT_OPTION.equals(arg)) {
                crawler.setNoOutput(true);
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                exitWithUsageMessage();
            }
            else if (crawler.getRootFile() == null) {
                crawler.setRootFile(new File(arg));
            }
            else if (crawler.getOutputFile() == null) {
                crawler.setOutputFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getRootFile() == null || 
                (!crawler.noOutput && (crawler.getOutputFile() == null))) {
            exitWithUsageMessage();
        }

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleFileCrawler.class.getName() + " ["
                + IDENTIFY_MIME_TYPE_OPTION + "] [" + EXTRACT_CONTENTS_OPTION + "] [" + VERBOSE_OPTION
                + "] rootDirectory outputFile");
        System.err.println("  "+IDENTIFY_MIME_TYPE_OPTION+": if set, the mime type of the files will be detected");
        System.err.println("  "+EXTRACT_CONTENTS_OPTION+": if set, the content of the files will be scanned");
        System.err.println("  "+VERBOSE_OPTION+": if set, messages are printed");
        System.err.println("  "+NOOUTPUT_OPTION+": if set, no outputFile is used, the folders are crawled and the\n" +
                           "     extracted content is discarded. This is used for performance testing");
        System.err.println("  rootDirectory: the directory to start crawling");
        System.err.println("  outputFile: the file where to store the RDF using TRIX");
        System.exit(-1);
    }

    /**
     * returns 'true' if the crawler is in the 'no output' mode
     * @return 'true' if the crawler is in the 'no output' mode
     */
    public boolean isNoOutput() {
        return noOutput;
    }

    
    public void setNoOutput(boolean noOutput) {
        this.noOutput = noOutput;
    }
}
