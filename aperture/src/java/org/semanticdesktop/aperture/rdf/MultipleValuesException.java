/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.ontoware.rdf2go.model.node.URI;

/**
 * A MultipleValuesException is thown in cases where an RDF model contains multiple values for a
 * subject-property pair, whereas one or zero values were expected.
 */
public class MultipleValuesException extends RuntimeException {

    private URI subject;
    
    private URI property;

    public MultipleValuesException(URI subject, URI property) {
        this.subject = subject;
        this.property = property;
    }
    
    public URI getSubject() {
        return subject;
    }

    public URI getProperty() {
        return property;
    }

	public String getMessage() {
		return "MultipleValuesException, subject <"+subject+"> has multiple values for <"+property+">";
	}
}
