/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.web.WebCrawler;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.config.RegExpPattern;
import org.semanticdesktop.aperture.datasource.config.SubstringCondition;
import org.semanticdesktop.aperture.datasource.config.SubstringPattern;
import org.semanticdesktop.aperture.datasource.web.WebDataSource;
import org.semanticdesktop.aperture.hypertext.linkextractor.impl.DefaultLinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;

/**
 * Example class that crawls a web site or locally stored hypertext graph and dumps all extracted information
 * to a RDF file.
 * 
 * <p>
 * The include and exclude pattern options use Java regular expressions. When no include and exclude patterns
 * are specified, then the domain is automatically bounded to those URLs that have the same host and path name
 * as the start URL.
 * 
 * <p>
 * Unlike the other example crawler classes, this class has no switch for setting on MIME type detection. This
 * is because MIME type detection is already a crucial part of the WebCrawler's operation (needed for
 * determining an appropriate LinkExtractor), there is no need to redo it.
 */
public class ExampleWebCrawler extends AbstractExampleCrawler {

    public static final String DEPTH_OPTION = "-depth";

    public static final String INCLUDE_OPTION = "-include";

    public static final String EXCLUDE_OPTION = "-exclude";

    public static final String INCLUDE_EMBEDDED_RESOURCES_OPTION = "-includeEmbeddedResources";

    private String startUrl;

    private int depth = -1;

    private DomainBoundaries boundaries;

    private boolean includeEmbeddedResources = false;

    public DomainBoundaries getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(DomainBoundaries boundaries) {
        this.boundaries = boundaries;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isIncludeEmbeddedResources() {
        return includeEmbeddedResources;
    }

    public void setIncludeEmbeddedResources(boolean includeEmbeddedResources) {
        this.includeEmbeddedResources = includeEmbeddedResources;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public void crawl() throws ModelException {
        if (startUrl == null) {
            throw new IllegalArgumentException("start URL cannot be null");
        }

        // create a data source configuration
        RDFContainerFactoryImpl factory = new RDFContainerFactoryImpl();
        RDFContainer config = factory.newInstance("urn:test:exampleimapsource");
        WebDataSource source = new WebDataSource();
        source.setConfiguration(config);
        
        source.setRootUrl(startUrl);
        source.setIncludeEmbeddedResources(includeEmbeddedResources);

        if (depth >= 0) {
            source.setMaximumDepth(depth);
        }

        if (boundaries != null) {
            source.setDomainBoundaries(boundaries);
        }

        // create the DataSource
        

        // setup a crawler that can handle this type of DataSource
        WebCrawler crawler = new WebCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setMimeTypeIdentifier(new MagicMimeTypeIdentifier());
        crawler.setLinkExtractorRegistry(new DefaultLinkExtractorRegistry());
        crawler.setCrawlerHandler(getHandler());
        crawler.setAccessData(getAccessData());
        
        // start crawling
        crawler.crawl();
    }
    
    public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
        ExampleWebCrawler crawler = new ExampleWebCrawler();

        // parse the command line options
        parseArgs(args, crawler);

        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void parseArgs(String[] arguments, ExampleWebCrawler crawler) throws Exception {
        // create an empty DomainBoundaries
        DomainBoundaries boundaries = new DomainBoundaries();
        
        List<String> remainingOptions = crawler.processCommonOptions(arguments);
        
        Iterator<String> iterator = remainingOptions.iterator();

        // parse the command-line options
        while (iterator.hasNext()) {
            String arg = iterator.next();
            String nextArg = null;

            if (INCLUDE_EMBEDDED_RESOURCES_OPTION.equals(arg)) {
                crawler.setIncludeEmbeddedResources(true);
            }
            else if (DEPTH_OPTION.equals(arg)) {
                try {
                    if (!iterator.hasNext()) {
                        System.err.println("missing value for option " + DEPTH_OPTION);
                        crawler.exitWithUsageMessage();
                    }
                    else {
                        nextArg = iterator.next();
                        crawler.setDepth(Integer.parseInt(nextArg));
                    }
                }
                catch (NumberFormatException e) {
                    System.err.println("illegal depth: " + nextArg);
                    crawler.exitWithUsageMessage();
                }
            }
            else if (INCLUDE_OPTION.endsWith(arg)) {
                if (!iterator.hasNext()) {
                    System.err.println("missing value for option " + INCLUDE_OPTION);
                    crawler.exitWithUsageMessage();
                }
                else {
                    try {
                        nextArg = iterator.next();
                        boundaries.addIncludePattern(new RegExpPattern(nextArg));
                    }
                    catch (PatternSyntaxException e) {
                        System.err.println("illegal regular expression: " + nextArg);
                        crawler.exitWithUsageMessage();
                    }
                }
            }
            else if (EXCLUDE_OPTION.endsWith(arg)) {
                if (!iterator.hasNext()) {
                    System.err.println("missing value for option " + EXCLUDE_OPTION);
                    crawler.exitWithUsageMessage();
                }
                else {
                    try {
                        nextArg = iterator.next();
                        boundaries.addExcludePattern(new RegExpPattern(nextArg));
                    }
                    catch (PatternSyntaxException e) {
                        System.err.println("illegal regular expression: " + nextArg);
                        crawler.exitWithUsageMessage();
                    }
                }
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                crawler.exitWithUsageMessage();
            }
            else if (crawler.getStartUrl() == null) {
                crawler.setStartUrl(arg);
            }
            else {
                crawler.exitWithUsageMessage();
            }
        }

        // make sure that the required properties have been set
        if (crawler.getStartUrl() == null) {
            System.err.println("Missing start URL");
            crawler.exitWithUsageMessage();
        }

        // if the domain boundaries is still empty, initialize it with the start URL's path to prevent
        // accidental infinite crawling
        if (boundaries.getIncludePatterns().isEmpty() && boundaries.getExcludePatterns().isEmpty()) {
            String includePath = crawler.getStartUrl();

            if (includePath.startsWith("http") || includePath.startsWith("https")
                    || includePath.startsWith("file:")) {
                try {
                    URL url = new URL(includePath);

                    String path = url.getPath();
                    int lastPathSep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
                    if (lastPathSep >= 0) {
                        path = path.substring(0, lastPathSep + 1);
                    }

                    includePath = new URL(url, path).toExternalForm();
                }
                catch (MalformedURLException e) {
                    System.err.println("invalid URL: " + includePath);
                    crawler.exitWithUsageMessage();
                }
            }

            boundaries.addIncludePattern(new SubstringPattern(includePath, SubstringCondition.STARTS_WITH));
        }

        // now set it on the crawler
        crawler.setBoundaries(boundaries);
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "[" + DEPTH_OPTION + " depth] " +
            "[" + INCLUDE_OPTION + " includePattern] " +
            "[" + EXCLUDE_OPTION + " exludePattern] " +
            "[" + INCLUDE_EMBEDDED_RESOURCES_OPTION + "] " +
            "startUrl";
    }
    
    @Override
    protected String getSpecificExplanationPart() {
        StringBuilder builder = new StringBuilder();
        builder.append("  " + DEPTH_OPTION + "        - crawl depth (optional)\n");
        builder.append("  " + INCLUDE_OPTION + "      - regular expression for URLs that are to be INCLUDED in the crawl\n" +
        		       "                  (optional, can be specified multple times)\n");
        builder.append("  " + EXCLUDE_OPTION + "      - regular expression for URLs that are to be EXCLUDED from the crawl\n" +
                       "                  (optional, can be specified multiple times)\n");
        builder.append("  " + INCLUDE_EMBEDDED_RESOURCES_OPTION + " - if specified, the embedded resources will be included in the crawl");
        return builder.toString();
    }
}
