/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.fileextractor.FileExtractorFactory;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * DefaultExtractorRegistry provides the complete set of ExtractorFactories available in Aperture.
 * 
 * <p>
 * The main purpose of this class is to be able to conveniently access the set of ExtractorFactories in
 * non-OSGi applications, which take care of this initialization in a different way. A single line of code
 * gives you the entire set without requiring further setup.
 * 
 * <p>
 * The set of factory class names are loaded from an XML file which can optionally be specified to the
 * constructor. This class requires all the listed classes to have a no-argument constructor.
 */
public class DefaultFileExtractorRegistry extends FileExtractorRegistryImpl {

    private static final String DEFAULT_FILE = "org/semanticdesktop/aperture/fileextractor/impl/defaults.xml";

    private static final String FILE_EXTRACTOR_FACTORY_TAG = "fileExtractorFactory";

    private static final String NAME_TAG = "name";

    public DefaultFileExtractorRegistry() {
        try {
            InputStream stream = ResourceUtil.getInputStream(DEFAULT_FILE, DefaultFileExtractorRegistry.class);
            BufferedInputStream buffer = new BufferedInputStream(stream);
            parse(buffer);
            buffer.close();
        }
        catch (IOException e) {
            throw new RuntimeException("unable to parse " + DEFAULT_FILE, e);
        }
    }

    public DefaultFileExtractorRegistry(InputStream stream) throws IOException {
        parse(stream);
    }

    private void parse(InputStream stream) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
            parser.setListener(new ExtractorParser());
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

    private class ExtractorParser extends SimpleSAXAdapter {

        private Logger logger = LoggerFactory.getLogger(getClass());

        private boolean insideFactoryElement = false;

        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (FILE_EXTRACTOR_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = true;
            }
            else if (NAME_TAG.equals(tagName) && insideFactoryElement && text != null) {
                processClassName(text);
            }
        }

        public void endTag(String tagName) {
            if (FILE_EXTRACTOR_FACTORY_TAG.equals(tagName)) {
                insideFactoryElement = false;
            }
        }

        private void processClassName(String className) {
            className = className.trim();
            if (!className.equals("")) {
                try {
                    Class clazz = Class.forName(className);
                    Object instance = clazz.newInstance();
                    FileExtractorFactory factory = (FileExtractorFactory) instance;
                    add(factory);
                }
                catch (ClassNotFoundException e) {
                    logger.warn("unable to find class " + className + ", ignoring", e);
                }
                catch (InstantiationException e) {
                    logger.warn("unable to instantiate class " + className + ", ignoring", e);
                }
                catch (IllegalAccessException e) {
                    logger.warn("unable to access class " + className + ", ignoring", e);
                }
                catch (ClassCastException e) {
                    logger.warn("unable to cast instance to " + ExtractorFactory.class.getName()
                            + ", ignoring", e);
                }
            }
        }
    }
}
