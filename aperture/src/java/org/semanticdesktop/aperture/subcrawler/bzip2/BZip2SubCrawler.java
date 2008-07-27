/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.bzip2;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.semanticdesktop.aperture.subcrawler.base.AbstractCompressorSubCrawler;

/**
 * A SubCrawler Implementation working with BZIP2 archives.
 */
public class BZip2SubCrawler extends AbstractCompressorSubCrawler {

    @Override
    protected InputStream getUncompressedStream(InputStream stream) throws IOException {
        // these two reads are necessary because for some bizarre reason, the CBZip2InputStream
        // assumes that the input stream passed to the constructor DOES NOT have the two
        // header bytes ('BZ')
        stream.read();
        stream.read();
        return new CBZip2InputStream(stream);
    }
}
