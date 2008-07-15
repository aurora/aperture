/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.html;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.HtmlParserUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

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
            // add an appropriate InformationElement type
            container.add(RDF.type,NFO.HtmlDocument);
            
            // store extracted text
            container.add(NIE.plainTextContent, getText());

            // store keywords
            Iterator keywords = getKeywords();
            while (keywords.hasNext()) {
            	addProperty(NIE.keyword, (String) keywords.next());
            }

            // store other metadata
            addProperty(NIE.title, getTitle());
            addContactProperty(NCO.creator, getAuthor());
            addProperty(NIE.description, getDescription());
        }
        
        private void addContactProperty(URI property, String fullname) {
            if (fullname != null) {
                fullname = fullname.trim();
                Model model = container.getModel();
                Resource contactResource = UriUtil.generateRandomResource(model);
                model.addStatement(contactResource,RDF.type,NCO.Contact);
                model.addStatement(contactResource,NCO.fullname,fullname);
                container.add(property, contactResource);
            }
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
