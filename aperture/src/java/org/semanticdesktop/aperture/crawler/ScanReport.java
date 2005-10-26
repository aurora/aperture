/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.crawler;

/**
 * A ScanReport instance contains statistics about the last performed or
 * currently active scan procedure of a DataCrawler.
 */
public interface ScanReport {

    /**
     * Returns when the scan was started, encoded in the typical milliseconds style.
     */
    public long getScanStarted();

    /**
     * Returns when the scan stopped. Returns a negative value when the scan
     * is still in progress.
     */
    public long getScanStopped();
    
    /**
     * Returns the status with which the scan completed. Returns null when the
     * scan is still in progress.
     */
    public int getExitCode();
    
    /**
     * Returns the number of new data objects encountered so far.
     */
    public int getNewCount();
    
    /**
     * Returns the number of changed data objects encountered so far.
     */
    public int getChangedCount();
    
    /**
     * Returns the number of removed data objects encountered so far.
     */
    public int getRemovedCount();
    
    /**
     * Returns the number of unchanged data objects encountered so far.
     */
    public int getUnchangedCount();
}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */