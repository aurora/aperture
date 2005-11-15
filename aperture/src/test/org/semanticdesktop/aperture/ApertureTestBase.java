/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture;

import java.util.Collection;
import java.util.Iterator;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

import junit.framework.TestCase;

public class ApertureTestBase extends TestCase {

    public static final String DOCS_PATH = "org/semanticdesktop/aperture/docs/";

    public void checkStatement(URI property, String substring, SesameRDFContainer container) {
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
    
    public void checkStatement(URI property, URI value, SesameRDFContainer container) {
        Repository repository = container.getRepository();
        ValueFactory valueFactory = repository.getSail().getValueFactory();
        String uriString = container.getDescribedUri().toString();
        URI subject = valueFactory.createURI(uriString);
        checkStatement(subject, property, value, container);
    }
    
    public void checkStatement(URI subject, URI property, URI value, SesameRDFContainer container) {
        Repository repository = container.getRepository();
        boolean encounteredValue = false;
        
        // loop over all statements that have the specified property uri as predicate
        Collection statements = repository.getStatements(subject, property, null);
        Iterator iterator = statements.iterator();
        
        while (iterator.hasNext()) {
            // check the property type
            Statement statement = (Statement) iterator.next();
            assertTrue(statement.getPredicate().equals(property));
            
            // see if it has a Literal containing the specified substring
            Value object = statement.getObject();
            if (object.equals(value)) {
                encounteredValue = true;
                break;
            }
        }
        
        // see if any of the found properties contains the specified substring
        assertTrue(encounteredValue);
    }
}

