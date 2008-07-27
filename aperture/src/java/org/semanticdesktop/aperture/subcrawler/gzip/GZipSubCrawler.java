/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.semanticdesktop.aperture.subcrawler.base.AbstractCompressorSubCrawler;

/**
 * A SubCrawler Implementation working with GZIP archives.
 */
public class GZipSubCrawler extends AbstractCompressorSubCrawler {

    @Override
    protected InputStream getUncompressedStream(InputStream stream) throws IOException {
        return new GZIPInputStream(stream);
    }
}
