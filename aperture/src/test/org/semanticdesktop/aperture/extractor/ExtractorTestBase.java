/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

public class ExtractorTestBase extends TestCase {

    public static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";
    
    public RDFContainerSesame extract(String resourceName, Extractor extractor) throws ExtractorException, IOException {
        // setup some info
        String uriString = "http://docs-r-us.com/dummy";
        URI id = new URIImpl(uriString);
        InputStream stream = ClassLoader.getSystemResourceAsStream(resourceName);
        RDFContainerSesame rdfContainer = new RDFContainerSesame(id);

        // apply the extractor
        extractor.extract(id, stream, null, null, rdfContainer);
        stream.close();

        return rdfContainer;
    }
    
    public void checkStatement(org.openrdf.model.URI property, String substring, RDFContainerSesame container) {
        // setup some info
        String uriString = container.getDescribedUri().toString();
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        boolean encounteredSubstring = false;
        
        // loop over all statements that have the specified property uri as predicate
        Collection statements = repository.getStatements(valueFactory.createURI(uriString), property, null);
        Iterator iterator = statements.iterator();
        
        while (iterator.hasNext()) {
            // check the property type
            Statement statement = (Statement) iterator.next();
            assertTrue(statement.getPredicate().equals(property));
            
            // see if it has a Literal containing the specified substring
            Value object = statement.getObject();
            if (object instanceof Literal) {
                String value = ((Literal) object).getLabel();
                if (value.indexOf(substring) >= 0) {
                    encounteredSubstring = true;
                    break;
                }
            }
        }
        
        // see if any of the found properties contains the specified substring
        assertTrue(encounteredSubstring);
    }
}
