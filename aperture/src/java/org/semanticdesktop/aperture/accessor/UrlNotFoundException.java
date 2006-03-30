/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import java.io.IOException;

/**
 * Thrown by DataAccessors when the requested url did not point to an existing resource. 
 */
public class UrlNotFoundException extends IOException {

    private String url;
    
    public UrlNotFoundException(String url) {
        super("URL not found: " + url);
        this.url = url;
    }
    
    public UrlNotFoundException(String url, String message) {
        super(message);
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
}
