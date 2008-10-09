/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.tar;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.semanticdesktop.aperture.subcrawler.base.AbstractArchiverSubCrawler;

/**
 * A SubCrawler Implementation working with tar archives.
 */
public class TarSubCrawler extends AbstractArchiverSubCrawler {
    
    protected class TarSubCrawlerInputStream extends AbstractArchiverSubCrawler.ArchiveInputStream {
        public TarSubCrawlerInputStream(InputStream in) { super(new TarInputStream(in)); }

        @Override
        public ArchiveEntry getNextEntry() throws IOException {
            TarEntry entry = ((TarInputStream)in).getNextEntry();
            return (entry == null) ? null : new TarSubCrawlerEntry(entry);
        }

        @Override public void closeEntry() throws IOException { /** the tar input stream doesn't close entries */ }        
    }
    
    protected class TarSubCrawlerEntry extends AbstractArchiverSubCrawler.ArchiveEntry {
        private TarEntry entry;
        public TarSubCrawlerEntry(TarEntry entry) { this.entry = entry; }
        // this hack has been introduced because of a quirk in the solaris tar
        // if you write "tar cvf tar-test.tar zip-test/"
        // the 'root' folder of the archive will be "zip-test//" (double hash at the end)
        // this problem doesn't come up if you write 
        // "tar cvf tar-test.tar zip-test"
        // but the former option also happens, that's why I replace all double slashes with a single slash
        @Override public String getPath()               { return entry.getName().replaceAll("//", "/"); }
        @Override public long getLastModificationTime() { return entry.getModTime().getTime(); }
        @Override public boolean isDirectory()          { return entry.isDirectory(); }
    }
    
    @Override
    protected ArchiveInputStream getArchiveInputStream(InputStream in) {
        return new TarSubCrawlerInputStream(in);
    }

    @Override
    public String getUriPrefix() {
        return TarSubCrawlerFactory.TAR_URI_PREFIX;
    }

}
