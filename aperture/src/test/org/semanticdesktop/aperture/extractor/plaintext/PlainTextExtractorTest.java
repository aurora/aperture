/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.plaintext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collection;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.rdf.Vocabulary;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

import junit.framework.TestCase;

public class PlainTextExtractorTest extends TestCase {

    public void testRegularExtraction() throws URISyntaxException, ParseException, IOException {
        String uriString = "file:plain-text.txt";
        Repository repository = getStatements(uriString, "org/semanticdesktop/aperture/docs/plain-text.txt");
        ValueFactory valueFactory = repository.getSail().getValueFactory();

        // check number of statements
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT_URI, null);
        assertEquals(1, statements.size());

        // check predicate
        Statement statement = (Statement) statements.iterator().next();
        assertTrue(statement.getPredicate().equals(Vocabulary.FULL_TEXT_URI));
        
        // check value
        Literal value = (Literal) statement.getObject();
        String text = value.getLabel();
        assertTrue((text.indexOf("plain text")!=-1));
    }

    public void testFailingExtraction() throws URISyntaxException, ParseException, IOException {
        String uriString = "file:plain-text-with-null-character.txt";
        Repository repository = getStatements(uriString, "org/semanticdesktop/aperture/docs/plain-text-with-null-character.txt");
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        
        // check number of statements
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), Vocabulary.FULL_TEXT_URI, null);
        assertEquals(0, statements.size());
    }
    
    private Repository getStatements(String uriString, String resourceName) throws URISyntaxException, ParseException, IOException {
        // setup some info
        URI id = new URI(uriString);
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourceName);
        RDFContainerSesame rdfContainer = new RDFContainerSesame(id);
        
        // apply the extractor
        ExtractorFactory factory = new PlainTextExtractorFactory();
        Extractor extractor = factory.get();
        extractor.extract(id, stream, null, null, rdfContainer);
        
        return rdfContainer.getRepository();
    }
}
