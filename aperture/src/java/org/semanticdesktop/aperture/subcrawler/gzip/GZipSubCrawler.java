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

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.subcrawler.base.AbstractCompressorSubCrawler;
import org.semanticdesktop.aperture.util.UriUtil;

/**
 * A SubCrawler Implementation working with GZIP archives.
 */
public class GZipSubCrawler extends AbstractCompressorSubCrawler {

    @Override
    protected InputStream getUncompressedStream(InputStream stream) throws IOException {
        return new GZIPInputStream(stream);
    }
    
    @Override
    protected URI getContentUri(URI archiveUri) {
        String name = UriUtil.getFileName(archiveUri);
        if (name.endsWith(".gz")) {
            return createChildUri(archiveUri, name.substring(0,name.length() - 3));
        } else if (name.endsWith(".tgz")) {
            return createChildUri(archiveUri, name.substring(0,name.length() - 3) + "tar");
        } else {
            return super.getContentUri(archiveUri);
        }
    }

    @Override
    public String getUriPrefix() {
        return GZipSubCrawlerFactory.GZIP_URI_PREFIX;
    }
}
