/*
 * Copyright (c) 2007 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor;

import java.util.Set;

/**
 * A FileExtractorFactory create instances of a specific FileExtractor implementation. As such, it embodies
 * knowledge about whether a singleton or unique instances are best returned and for which MIME types the
 * FileExtractors can be used.
 * 
 * <P>
 * FileExtractorFactories should be very light-weight to create. This allows them to be used for service
 * registration in service-oriented architectures.
 */
public interface FileExtractorFactory {

    /**
     * Returns an instance of the represented FileExtractor implementation. Subsequent invocations may or may
     * not return the same instance.
     * 
     * @return An instance of the FileExtractor interface.
     */
    public FileExtractor get();

    /**
     * Returns the MIME types of the formats supported by the returned FileExtractor.
     * 
     * @return A Set of Strings.
     */
    public Set getSupportedMimeTypes();
}
