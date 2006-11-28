/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.xml.sax.SAXException;

/**
 * DefaultCrawlerRegistry provides the complete set of CrawlerFactories available in Aperture.
 * 
 * <p>
 * The main purpose of this class is to be able to conveniently access the set of CrawlerFactories in
 * non-OSGi applications, which take care of this initialization in a different way. A single line of
 * code gives you the entire set without requiring further setup.
 * 
 * <p>
 * The set of factory class names are loaded from an XML file which can optionally be specified to the
 * constructor. This class requires all the listed classes to have a no-argument constructor.
 */
public class DefaultCrawlerRegistry extends CrawlerRegistryImpl {

    private static final String DEFAULT_FILE = "org/semanticdesktop/aperture/crawler/impl/defaults.xml";

    private static final String CRAWLER_FACTORY_TAG = "crawlerFactory";

    private static final String NAME_TAG = "name";

    private static final Logger LOGGER = Logger.getLogger(DefaultCrawlerRegistry.class.getName());

    public DefaultCrawlerRegistry() {
        try {
            InputStream stream = ResourceUtil.getInputStream(DEFAULT_FILE,DefaultCrawlerRegistry.class);
            BufferedInputStream buffer = new BufferedInputStream(stream);
            parse(buffer);
            buffer.close();
        }
        catch (IOException e) {
            throw new RuntimeException("unable to parse " + DEFAULT_FILE, e);
        }
    }

    public DefaultCrawlerRegistry(InputStream stream) throws IOException {
        parse(stream);
    }

    private void parse(InputStream stream) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
            parser.setListener(new CrawlerParser());
            parser.parse(stream);
        }
        catch (ParserConfigurationException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (SAXException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }

    private class CrawlerParser extends SimpleSAXAdapter {

        private boolean insideFactoryElement = false;

        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (CRAWLER_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = true;
            }
            else if (NAME_TAG.equals(tagName) && insideFactoryElement && text != null) {
                processClassName(text);
            }
        }

        public void endTag(String tagName) {
            if (CRAWLER_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = false;
            }
        }

        private void processClassName(String className) {
            className = className.trim();
            if (!className.equals("")) {
                try {
                    Class clazz = Class.forName(className);
                    Object instance = clazz.newInstance();
                    CrawlerFactory factory = (CrawlerFactory) instance;
                    add(factory);
                }
                catch (ClassNotFoundException e) {
                    LOGGER.log(Level.WARNING, "unable to find class " + className + ", ignoring", e);
                }
                catch (InstantiationException e) {
                    LOGGER.log(Level.WARNING, "unable to instantiate class " + className + ", ignoring", e);
                }
                catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, "unable to access class " + className + ", ignoring", e);
                }
                catch (ClassCastException e) {
                    LOGGER.log(Level.WARNING, "unable to cast instance to " + CrawlerFactory.class.getName()
                            + ", ignoring", e);
                }
            }
        }
    }
}
