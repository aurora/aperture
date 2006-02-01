/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.word;

import java.io.IOException;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.AccesVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class WordExtractorTest extends ExtractorTestBase {

    public void testRegularExtraction() throws ExtractorException, IOException {
        // apply the extractor on a text file
        SesameRDFContainer container = getStatements(DOCS_PATH + "microsoft-word-2000.doc");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        
        // fetch the full-text property
        String uriString = container.getDescribedUri().toString();
        CloseableIterator statements = repository.getStatements(valueFactory.createURI(uriString), AccesVocabulary.FULL_TEXT, null);

        // check predicate
        RStatement statement = (RStatement) statements.next();
        assertTrue(statement.getPredicate().equals(AccesVocabulary.FULL_TEXT));
        
        // check number of statements
        assertFalse(statements.hasNext());
        
        // check value
        Literal value = (Literal) statement.getObject();
        String text = value.getLabel();
        assertTrue((text.indexOf("Microsoft") != -1));
        
        statements.close();
    }

    private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        ExtractorFactory factory = new WordExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(resourceName, extractor);
        return container;
    }
}
