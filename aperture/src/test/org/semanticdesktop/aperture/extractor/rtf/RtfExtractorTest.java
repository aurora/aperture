/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.io.IOException;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class RtfExtractorTest extends ExtractorTestBase {

    public void testRegularExtraction() throws ExtractorException, IOException {
        // apply the extractor on a text file
        SesameRDFContainer container = getStatements(DOCS_PATH + "rtf-word-2000.rtf");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        
        // fetch the full-text property
        String uriString = container.getDescribedUri().toString();
        CloseableIterator statements = repository.getStatements(valueFactory.createURI(uriString), AccessVocabulary.FULL_TEXT, null);
        try {
	        // check predicate
	        RStatement statement = (RStatement) statements.next();
	        assertTrue(statement.getPredicate().equals(AccessVocabulary.FULL_TEXT));
	        
	        // check number of statements
	        assertFalse(statements.hasNext());
	        
	        // check value
	        Literal value = (Literal) statement.getObject();
	        String text = value.getLabel();
	        assertTrue((text.indexOf("RTF") != -1));
        }
        finally {
        	statements.close();
        }
    }

    private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        ExtractorFactory factory = new RtfExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(resourceName, extractor);
        return container;
    }
}
