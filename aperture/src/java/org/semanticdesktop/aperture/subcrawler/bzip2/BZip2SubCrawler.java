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
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.subcrawler.base.AbstractCompressorSubCrawler;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.util.UrlUtil;

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

    @Override
    public String getUriPrefix() {
        return BZip2SubCrawlerFactory.BZIP2_URI_PREFIX;
    }

    @Override
    protected URI getContentUri(URI archiveUri) {
        String name = UriUtil.getFileName(archiveUri);
        if (name.endsWith(".bz2")) {
            return createChildUri(archiveUri, name.substring(0,name.length() - 4));
        } else if (name.endsWith(".tbz")) {
            return createChildUri(archiveUri, name.substring(0,name.length() - 3) + "tar");
        } if (name.endsWith(".tbz2")) {
            return createChildUri(archiveUri, name.substring(0,name.length() - 4) + "tar");
        } else {
            return super.getContentUri(archiveUri);
        }
    }
}
