/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import java.util.Set;

/**
 * A LinkExtractorFactory returns LinkExtractor instances that can handle a specified set of MIME types.
 */
public interface LinkExtractorFactory {

    /**
     * Returns the MIME types supported by the LinkExtractors provided by this factory.
     * 
     * @return A Set of Strings describing the supported MIME types.
     */
    public Set getSupportedMimeTypes();

    /**
     * Returns a LinkExtractor instance capable of handling the supported MIME types.
     */
    public LinkExtractor get();
}
