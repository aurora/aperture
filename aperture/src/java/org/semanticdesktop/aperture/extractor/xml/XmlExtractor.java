/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.xml;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extracts text inside elements and attribute values from XML documents.
 */
public class XmlExtractor implements Extractor {

    private static final int BUFFER_SIZE = 8192;

    public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
            throws ExtractorException {
        try {
            // make sure we can reset the stream, in case the initial parse fails on external DTD loading
            if (!stream.markSupported()) {
                stream = new BufferedInputStream(stream, BUFFER_SIZE);
            }
            stream.mark(BUFFER_SIZE);

            // wrap the stream in a filter that ignores calls to close (see java bug #6354964); the
            // parser.parse call will attempt to close the stream, meaning that we cannot reset it if we don't
            // catch that call
            FilterInputStream filterStream = new FilterInputStream(stream) {

                public void close() {
                // don't do anything
                }
            };

            // setup a parser
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            // prepare a ContentHandler that gathers all text
            XmlTextExtractor listener = new XmlTextExtractor();

            // parse the stream
            try {
                parser.parse(filterStream, listener);
            }
            catch (Exception e) {
                if (!isFailingDTDException(e)) {
                    return;
                }

                // a FNFE is typically thrown when an external DTD cannot be found. An UnknownHostException is
                // thrown when the user is not online and certain hosts cannot be found.

                // External DTDs are useful to support resolving of entities but failing to load the DTD
                // should not result in an aborted text extraction. Now let's assume we use Xerxes or a
                // Xerces-derived parser (e.g. the one in Java 5), switch off external DTD loading and try
                // again
                try {
                    // disable external dtd loading
                    parser.getXMLReader().setFeature(
                        "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

                    // reset some data structures
                    listener.clear();
                    stream.reset();

                    // try again to parse the document
                    parser.parse(stream, listener);
                }
                catch (SAXException se) {
                    // the FNFE is probably more worthy to report than the SAXException
                    Logger logger = LoggerFactory.getLogger(getClass());
                    logger.error("FileNotFoundException while parsing document and unable "
                            + "to disable external dtd loading: " + id.toString(), e);
                }
            }

            // store the extracted text
            String text = listener.getText();
            if (!text.equals("")) {
                result.add(DATA.fullText, text);
            }
        }
        catch (ParserConfigurationException e) {
            throw new ExtractorException(e);
        }
        catch (SAXException e) {
            throw new ExtractorException(e);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }
    }

    private boolean isFailingDTDException(Exception e) {
        return e instanceof FileNotFoundException || e instanceof UnknownHostException;
    }

    private static class XmlTextExtractor extends DefaultHandler {

        private StringBuilder buffer = new StringBuilder(64 * 1024);

        public String getText() {
            return buffer.toString().trim();
        }

        public void clear() {
            buffer.setLength(0);
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attributes)
                throws SAXException {
            int nrAtts = attributes.getLength();
            for (int i = 0; i < nrAtts; i++) {
                String value = attributes.getValue(i);
                if (value != null && value.length() > 0 && !isGarbage(value)) {
                    buffer.append(value);
                    buffer.append(' ');
                }
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            buffer.append(ch, start, length);
            buffer.append(' ');
        }

        /**
         * Returns true if the given attribute value is considered to be garbage.
         */
        private boolean isGarbage(String attsValue) {
            return "true".equalsIgnoreCase(attsValue) || "false".equalsIgnoreCase(attsValue)
                    || "yes".equalsIgnoreCase(attsValue) || "no".equalsIgnoreCase(attsValue);
        }
    }
}
