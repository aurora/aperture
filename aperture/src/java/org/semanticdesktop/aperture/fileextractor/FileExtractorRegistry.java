/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor;

import java.util.Set;

/**
 * An FileExtractorRegistry serves as a central registry for registering and obtaining FileExtractorFactories.
 */
public interface FileExtractorRegistry {

    /**
     * Adds a FileExtractorFactory to this registry.
     * @param factory the factory to be added
     */
    public void add(FileExtractorFactory factory);

    /**
     * Removes a FileExtractorFactory from this registry.
     * @param factory the factory to be removed
     */
    public void remove(FileExtractorFactory factory);

    /**
     * Returns all FileExtractorFactories that support the specified MIME ype.
     * 
     * @param mimeType the mime type supported by the returned factories
     * @return A Set of FileExtractorFactories whose getSupportedMimeTypes method results contain the specified
     *         MIME type.
     */
    public Set get(String mimeType);

    /**
     * Returns all FileExtractorFactories registered in this FileExtractorRegistry.
     * 
     * @return A Set of ExtractorFactory instances.
     */
    public Set getAll();
}
