/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor;

import java.util.Set;

/**
 * An ExtractorRegistry serves as a central registry for registering and obtaining ExtractorFactories.
 */
public interface ExtractorRegistry {

    /**
     * Adds an ExtractorFactory to this registry.
     */
    public void add(ExtractorFactory factory);

    /**
     * Removes an ExtractorFactory from this registry.
     */
    public void remove(ExtractorFactory factory);

    /**
     * Adds a file extractor factory to this registry.
     */
    public void add(FileExtractorFactory factory);

    /**
     * Remove a FileExtractorFactory from this registry.
     */
    public void remove(FileExtractorFactory factory);

    /**
     * Returns all ExtractorFactories that support the specified MIME type.
     * 
     * @return A Set of ExtractorFactories whose getSupportedMimeTypes method results contain the specified
     *         MIME type.
     * @deprecated Use getExtractorFactories instead. After the introduction of FileExtractorFactories the
     *             semantics of this method may have become unclear. It returns normal stream-based extractor
     *             factories. The result is the same as {@link #getExtractorFactories}
     */
    public Set get(String mimeType);

    /**
     * Returns all ExtractorFactories registered in this ExtractorRegistry.
     * 
     * @return A Set of ExtractorFactory instances.
     * @deprecated Use getAllExtractorFactories instead. After the introduction of FileExtractorFactories the
     *             semantics of this method may have become unclear. It returns normal stream-based extractor
     *             factories. The result is the same as {@link #getAllExtractorFactories}
     */
    public Set getAll();

    /**
     * Returns all ExtractorFactories that support the specified MIME type.
     * 
     * @return A Set of ExtractorFactories whose getSupportedMimeTypes method results contain the specified
     *         MIME type.
     */
    public Set getExtractorFactories(String mimeType);

    /**
     * Returns all ExtractorFactories registered in this ExtractorRegistry.
     * 
     * @return A Set of ExtractorFactory instances.
     */
    public Set getAllExtractorFactories();
    
    /**
     * Returns all FileExtractorFactories that support the specified MIME type.
     * 
     * @return A Set of ExtractorFactories whose getSupportedMimeTypes method results contain the specified
     *         MIME type.
     */
    public Set getFileExtractorFactories(String mimeType);

    /**
     * Returns all FileExtractorFactories registered in this ExtractorRegistry.
     * 
     * @return A Set of ExtractorFactory instances.
     */
    public Set getAllFileExtractorFactories();
}
