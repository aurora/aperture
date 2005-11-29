/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.base;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A trivial default implementation of the DataSource interface.
 */
public abstract class DataSourceBase implements DataSource {

    // Note: the utility get methods operating on the RDFContainer interpret invalid data as no
    // configuration, i.e. exceptions result in null return values. This is in line with the general
    // spirit of RDF that it should be possible to make any arbitrary statement, only some statements
    // cannot be interpreted automatically.

    private URI id;

    private String name;

    private RDFContainer configuration;

    public URI getID() {
        return id;
    }

    public void setID(URI id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RDFContainer getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RDFContainer configuration) {
        // check whether the described URI matches our ID
        URI describedURI = configuration.getDescribedUri();
        if (describedURI == null) {
            throw new IllegalArgumentException("RDFContainer has no described URI");
        }
        else if (!describedURI.equals(id)) {
            throw new IllegalArgumentException("described URI and ID do not match, ID = " + id
                    + ", described URI = " + describedURI);
        }

        // set the configuration
        this.configuration = configuration;
    }
}
