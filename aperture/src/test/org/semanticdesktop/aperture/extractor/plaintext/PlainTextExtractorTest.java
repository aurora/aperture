/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.Vocabulary;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

public class PlainTextExtractorTest extends ExtractorTestBase {

    public void testRegularExtraction() throws URISyntaxException, ExtractorException, IOException {
        // apply the extractor on a text file
        RDFContainerSesame container = getStatements("org/semanticdesktop/aperture/docs/plain-text.txt");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
                
        // check number of statements
        String uriString = container.getDataObjectUri().toString();
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT_URI, null);
        assertEquals(1, statements.size());

        // check predicate
        Statement statement = (Statement) statements.iterator().next();
        assertTrue(statement.getPredicate().equals(Vocabulary.FULL_TEXT_URI));
        
        // check value
        Literal value = (Literal) statement.getObject();
        String text = value.getLabel();
        assertTrue((text.indexOf("plain text") != -1));
    }

    public void testFailingExtraction() throws URISyntaxException, ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        RDFContainerSesame container = getStatements("org/semanticdesktop/aperture/docs/plain-text-with-null-character.txt");
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        
        // check number of statements
        String uriString = container.getDataObjectUri().toString();
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT_URI, null);
        assertEquals(0, statements.size());
    }
    
    private RDFContainerSesame getStatements(String resourceName) throws URISyntaxException, ExtractorException, IOException {
        // apply the extractor on a text file containing a null character
        ExtractorFactory factory = new PlainTextExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainerSesame container = extract(resourceName, extractor);
        return container;
    }
}
