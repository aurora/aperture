/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.semanticdesktop.aperture.subcrawler.base.AbstractArchiverSubCrawler;

/**
 * A SubCrawler Implementation working with ZIP archives.
 */
public class ZipSubCrawler extends AbstractArchiverSubCrawler {

    protected class ZipSubCrawlerInputStream extends AbstractArchiverSubCrawler.ArchiveInputStream {
        public ZipSubCrawlerInputStream(InputStream in) { super(new ZipInputStream(in)); }

        @Override
        public ArchiveEntry getNextEntry() throws IOException {
            ZipEntry entry = ((ZipInputStream)in).getNextEntry();
            return (entry == null) ? null : new ZipSubCrawlerEntry(entry);
        }

        @Override public void closeEntry() throws IOException { ((ZipInputStream)in).closeEntry(); }        
    }
    
    protected class ZipSubCrawlerEntry extends AbstractArchiverSubCrawler.ArchiveEntry {
        private ZipEntry entry;
        public ZipSubCrawlerEntry(ZipEntry entry) { this.entry = entry; }
        @Override public String getName()               { return entry.getName(); }
        @Override public String getComment()            { return entry.getComment(); }
        @Override public long getCompressedSize()       { return entry.getCompressedSize(); }
        @Override public long getCrc()                  { return entry.getCrc(); }
        @Override public long getLastModificationTime() { return entry.getTime(); }
        @Override public boolean isDirectory()          { return entry.isDirectory(); }
    }
    
    @Override
    protected ArchiveInputStream getArchiveInputStream(InputStream in) {
        return new ZipSubCrawlerInputStream(in);
    }
}
