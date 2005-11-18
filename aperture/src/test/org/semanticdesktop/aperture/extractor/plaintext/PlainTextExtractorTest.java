/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;
import java.util.Collection;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class PlainTextExtractorTest extends ExtractorTestBase {

    public void testRegularExtraction() throws ExtractorException, IOException {
        // apply the extractor on a text file
        SesameRDFContainer container = getStatements(DOCS_PATH + "plain-text.txt");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
                
        // check number of statements
        String uriString = container.getDescribedUri().toString();
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT, null);
        assertEquals(1, statements.size());

        // check predicate
        Statement statement = (Statement) statements.iterator().next();
        assertTrue(statement.getPredicate().equals(Vocabulary.FULL_TEXT));
        
        // check value
        Literal value = (Literal) statement.getObject();
        String text = value.getLabel();
        assertTrue((text.indexOf("plain text") != -1));
    }

    public void testFailingExtraction() throws ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        SesameRDFContainer container = getStatements(DOCS_PATH + "plain-text-with-null-character.txt");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        
        // check number of statements
        String uriString = container.getDescribedUri().toString();
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT, null);
        assertEquals(0, statements.size());
    }
    
    private SesameRDFContainer getStatements(String resourceName) throws ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        ExtractorFactory factory = new PlainTextExtractorFactory();
        Extractor extractor = factory.get();
        SesameRDFContainer container = extract(resourceName, extractor);
        return container;
    }
}
