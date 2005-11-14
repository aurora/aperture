/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

/**
 * A CrawlReport instance contains statistics about the last performed or currently active scan procedure
 * of a Crawler.
 */
public interface CrawlReport {

    /**
     * Returns when the scan was started, encoded in the typical milliseconds style.
     */
    public long getScanStarted();

    /**
     * Returns when the scan stopped. Returns a negative value when the scan is still in progress.
     */
    public long getScanStopped();

    /**
     * Returns the status with which the scan completed. Returns null when the scan is still in progress.
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
