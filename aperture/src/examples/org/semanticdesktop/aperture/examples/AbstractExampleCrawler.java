/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.semanticdesktop.aperture.examples.handler.PerformanceMeasuringCrawlerHandler;
import org.semanticdesktop.aperture.examples.handler.SimpleCrawlerHandler;
import org.semanticdesktop.aperture.examples.handler.ValidatingCrawlerHandler;

public abstract class AbstractExampleCrawler {

    private File outputFile;
    
    private SimpleCrawlerHandler handler;
    
    private boolean verbose;
    
    private boolean identifyingMimeType;
    
    private boolean extractingContents;
    
    public static final String OUTPUT_FILE_OPTION = "-o";
    
    public static final String VALIDATE_OPTION = "--validate";
    
    public static final String PERFORMANCE_OPTION = "--performance";
    
    public static final String VERBOSE_OPTION = "-v";
    
    public static final String IDENTIFY_MIME_OPTION = "-i";
    
    public static final String EXTRACT_CONTENTS_OPTION = "-x";
    
    protected abstract String getSpecificSyntaxPart();
    
    protected abstract String getSpecificExplanationPart();
    
    protected final void exitWithUsageMessage() {
        System.err.println("This program accepts following arguments:");
        System.err.println("  " + getCommonSyntaxPart() + " " + getSpecificSyntaxPart());
        System.err.println("Explanation:");
        System.err.print(getCommonExplanationPart());
        System.err.print(getSpecificExplanationPart());
        System.exit(-1);
    }
    
    protected List<String> processCommonOptions(String [] args) throws Exception {
        boolean validate = false;
        boolean performance = false;
        List<String> remainingOptions = new LinkedList<String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String nextArg = i < args.length - 1 ? args[i + 1] : null;
            if (arg.equals(OUTPUT_FILE_OPTION)) {
                if (nextArg == null) {
                    System.err.println("missing value for option " + OUTPUT_FILE_OPTION);
                    exitWithUsageMessage();
                }
                outputFile = new File(nextArg);
                i++;
            } else if (arg.equals(VALIDATE_OPTION)) {
                validate = true;
            } else if (arg.equals(PERFORMANCE_OPTION)) {
                performance = true;
            } else if (arg.equals(VERBOSE_OPTION)) {
                verbose = true;
            } else if (arg.equals(IDENTIFY_MIME_OPTION)) {
                identifyingMimeType = true;
            } else if (arg.equals(EXTRACT_CONTENTS_OPTION)) {
                identifyingMimeType = true;
                extractingContents = true;
            } else {
                remainingOptions.add(arg);
            }
        }
        if (validate && performance) {
            System.err.println("Cannot specify both validating and performance measuring at the same time");
            exitWithUsageMessage();
        }
        if (!performance && (outputFile == null)) {
            System.err.println("When not measuring performance, the output file needs to be specified");
            exitWithUsageMessage();
        }
        if (validate) {
            handler = new ValidatingCrawlerHandler(identifyingMimeType,extractingContents,verbose,outputFile);
        } else if (performance) {
            handler = new PerformanceMeasuringCrawlerHandler(identifyingMimeType,extractingContents,verbose,outputFile);
        } else {
            handler = new SimpleCrawlerHandler(identifyingMimeType,extractingContents,verbose,outputFile);
        }
        return remainingOptions;
    }
    
    public static String getCommonSyntaxPart() {
        return "[--validate] [--performance] [-i] [-x] [-v] -o <output-file-path>";
    }
    
    public static String getCommonExplanationPart() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writer.println(getAlignedOption("--validate") + "turns on validation, the data is validated after the crawl");
        writer.println(getAlignedOption(null) + "(conflicts with --performance)");
        writer.println(getAlignedOption("--performance") + "turns on performance measuring");
        writer.println(getAlignedOption("-i") + "turns on MIME type identification");
        writer.println(getAlignedOption("-x") + "if specified - the program will try to extract the content of");
        writer.println(getAlignedOption("-v") + "verbose output");
        writer.println(getAlignedOption("-o path") + "path to the output file");
        writer.println(getAlignedOption(null) + "optional if --performance is specified)");
        return stringWriter.toString();
    }
    
    protected static String getAlignedOption(String option) {
        StringBuilder builder = new StringBuilder(30);
        int startPoint = 0;
        if (option != null) {
            builder.append("  " + option);
            startPoint = option.length() + 2;
        }
        for (int i = startPoint; i <= 15; i++) {
            builder.append(" ");
        }
        
        if (option == null) {
            builder.append("   ");
        } else {
            builder.append(" - ");
        }
        
        return builder.toString();
    }
    
    public File getOutputFile() {
        return outputFile;
    }
    
    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    
    /**
     * @return Returns the handler.
     */
    public SimpleCrawlerHandler getHandler() {
        return handler;
    }

    
    /**
     * @param handler The handler to set.
     */
    public void setHandler(SimpleCrawlerHandler handler) {
        this.handler = handler;
    }

    
    /**
     * @return Returns the verbose.
     */
    public boolean isVerbose() {
        return verbose;
    }

    
    /**
     * @param verbose The verbose to set.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    
    /**
     * @return Returns the identifyingMimeType.
     */
    public boolean isIdentifyingMimeType() {
        return identifyingMimeType;
    }

    
    /**
     * @param identifyingMimeType The identifyingMimeType to set.
     */
    public void setIdentifyingMimeType(boolean identifyingMimeType) {
        this.identifyingMimeType = identifyingMimeType;
    }

    
    /**
     * @return Returns the extractingContents.
     */
    public boolean isExtractingContents() {
        return extractingContents;
    }

    
    /**
     * @param extractingContents The extractingContents to set.
     */
    public void setExtractingContents(boolean extractingContents) {
        this.extractingContents = extractingContents;
    }
    
}
