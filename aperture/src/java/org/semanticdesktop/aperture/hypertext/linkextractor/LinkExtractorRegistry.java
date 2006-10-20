/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import java.util.Set;

/**
 * A LinkExtractorRegistry keeps track of the available LinkExtractorFactories.
 */
public interface LinkExtractorRegistry {

    /**
     * Register a LinkExtractorFactory.
     */
    public void add(LinkExtractorFactory factory);

    /**
     * Remove a LinkExtractorFactory.
     */
    public void remove(LinkExtractorFactory factory);

    /**
     * Get all registered LinkExtractorFactories.
     * 
     * @return A Set of LinkExtractorFactories.
     */
    public Set getAll();

    /**
     * Get all LinkExtractorFactories registered for the specified MIME type.
     * 
     * @return A Set containing all LinkExtractorFactories that support the specified MIME type.
     */
    public Set get(String mimeType);
}
