/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.HtmlParserUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * HtmlExtractor extracts full-text and metadata from HTML and XHTML documents.
 */
public class HtmlExtractor implements Extractor {

    public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
            throws ExtractorException {
    	HtmlParserUtil.parse(stream, charset, new TextAndMetadataExtractor(result));
    }

    private static class TextAndMetadataExtractor extends HtmlParserUtil.ContentExtractor {

        private RDFContainer container;

        public TextAndMetadataExtractor(RDFContainer container) {
            this.container = container;
        }

        public void finishedParsing() {
            // store extracted text
            container.add(DATA.fullText, getText());

            // store keywords
            Iterator keywords = getKeywords();
            while (keywords.hasNext()) {
            	addProperty(DATA.keyword, (String) keywords.next());
            }

            // store other metadata
            addProperty(DATA.title, getTitle());
            addProperty(DATA.creator, getAuthor());
            addProperty(DATA.description, getDescription());
        }
        
        private void addProperty(URI property, String value) {
        	if (value != null) {
        		value = value.trim();
        		if (value.length() > 0) {
        			container.add(property, value);
        		}
        	}
        }
    }
}
