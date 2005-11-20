/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler;

/**
 * A CrawlReport instance contains statistics about the last performed or currently active crawl
 * procedure of a Crawler.
 */
public interface CrawlReport {

    /**
     * Returns when the crawl was started, encoded in the typical milliseconds style.
     */
    public long getCrawlStarted();

    /**
     * Returns when the crawl stopped. Returns a negative value when the crawl is still in progress.
     */
    public long getCrawlStopped();

    /**
     * Returns the status with which the crawl completed. Returns null when the crawl is still in
     * progress.
     */
    public ExitCode getExitCode();

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
