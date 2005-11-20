/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataBase;
import org.semanticdesktop.aperture.crawler.CrawlReport;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * An implementation of the Crawler interface that offers generic implementations for some of its
 * methods.
 */
public abstract class CrawlerBase implements Crawler {

    private static final Logger LOGGER = Logger.getLogger(CrawlerBase.class.getName());

    /**
     * The DataSource representing the physical source of information.
     */
    protected DataSource source;

    /**
     * The file for persistent storage of the AccessData.
     */
    protected File accessDataFile;

    /**
     * The file for persistent storage of CrawlReports.
     */
    protected File crawlReportFile;

    /**
     * The current AccessData instance.
     */
    protected AccessData accessData;

    /**
     * The CrawlReport containing statistics about the last or ongoing crawl. Created by the crawl method
     * or sometimes lazily created by the getLastCrawlReport method when trying to retrieve the last
     * report of a previous session.
     */
    protected CrawlReportBase crawlReport;

    /**
     * The CrawlerHandler that gets notified about crawling progress and that delivers RDFContainers on
     * demand.
     */
    protected CrawlerHandler handler;

    /**
     * Flag indicating that this Crawler should stop scanning or clearing as soon as possible.
     */
    protected boolean stopRequested;

    /**
     * A set that is used to temporary record all urls that do no longer point to existing resources, so
     * that we can report them as removed.
     */
    protected HashSet deprecatedUrls;

    public CrawlerBase() {
        this.stopRequested = false;
    }

    public void setDataSource(DataSource source) {
        this.source = source;
    }

    public DataSource getDataSource() {
        return source;
    }

    public void setCrawlerHandler(CrawlerHandler handler) {
        this.handler = handler;
    }

    public CrawlerHandler getCrawlerHandler() {
        return handler;
    }

    public synchronized void crawl() {
        // set up a new CrawlReport
        crawlReport = new CrawlReportBase();
        crawlReport.setCrawlStarted(System.currentTimeMillis());

        // initialize flags
        stopRequested = false;

        // we do all this before notifying the CrawlerHandler as its implementation may depend on this
        // initialization
        handler.crawlStarted(this);

        // read the access data from the previous crawl
        setUpAccessData();

        // this set will at the end of the crawl prodecure hold the urls of resources found in a previous
        // crawl that can no longer be found
        deprecatedUrls = new HashSet(accessData.getStoredIDs());

        // start crawling
        ExitCode exitCode = crawlObjects();

        // only when the scan was completed succesfully will we report removed resources,
        // else we might indirectly destroy information that may still be up-to-date
        if (exitCode.equals(ExitCode.COMPLETED)) {
            crawlReport.setRemovedCount(deprecatedUrls.size());
            reportRemoved(deprecatedUrls);
        }

        // this set *can* be very large, get rid of it ASAP
        deprecatedUrls = null;

        // store the access data to disk
        storeAccessData();

        // wrap up and store the CrawlReport
        crawlReport.setExitCode(exitCode);
        crawlReport.setCrawlStopped(System.currentTimeMillis());
        storeCrawlReport();

        // notify the CrawlerHandler
        handler.crawlStopped(this, exitCode);
    }

    /**
     * Method called by crawl() that should implement the actual crawling of the DataSource. The return
     * value of this method should indicate whether the scanning was completed successfully (i.e. it
     * wasn't interrupted or anything). Also this method is expected to update the deprecatedUrls set, as
     * any remaining URLs in this set will be removed as being removed after this method completes.
     * 
     * @return An ExitCode indicating how the crawl procedure terminated.
     */
    protected abstract ExitCode crawlObjects();

    public void stop() {
        stopRequested = true;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void clear() {
        handler.clearStarted(this);

        // read the persistent access data
        setUpAccessData();

        // Report removal of data objects
        if (accessData != null) {
            Iterator idIter = accessData.getStoredIDs().iterator();
            while (!stopRequested && idIter.hasNext()) {
                clear((String) idIter.next());
            }
        }

        // remove persistent access data registration
        accessData = null;
        clearAccessData();

        ExitCode exitCode = stopRequested ? ExitCode.STOP_REQUESTED : ExitCode.COMPLETED;
        handler.clearFinished(this, exitCode);
    }

    protected void clear(String url) {
        handler.clearingObject(this, url);
    }

    public void setAccessDataFile(File file) throws IOException {
        this.accessDataFile = file;
    }

    public File getAccessDataFile() {
        return accessDataFile;
    }

    public void setCrawlReportFile(File file) {
        this.crawlReportFile = file;
    }

    public File getCrawlReportFile() {
        return crawlReportFile;
    }

    public CrawlReport getCrawlReport() {
        if (crawlReport == null && crawlReportFile != null && crawlReportFile.exists()) {
            try {
                CrawlReportBase tmp = new CrawlReportBase();

                InputStream stream = new BufferedInputStream(new FileInputStream(crawlReportFile));
                try {
                    tmp.read(stream);
                    crawlReport = tmp;
                }
                finally {
                    stream.close();
                }
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IOException while loading crawl report file", e);
            }
        }

        return crawlReport;
    }

    private void reportRemoved(HashSet ids) {
        Iterator idIter = ids.iterator();
        while (idIter.hasNext()) {
            String url = (String) idIter.next();
            accessData.remove(url);
            handler.objectRemoved(this, url);
        }
    }

    /**
     * Returns returns a new AccessData instance. This defaults to an AccessDataBase instance. Subclasses
     * can override this (and other related) methods to provide their own AccessData implementation.
     */
    protected AccessData createEmptyAccessData() {
        return new AccessDataBase();
    }

    /**
     * Reads the access data from the configured access data file, if any. When no access data file has
     * been configured or when the file does not exist, an empty AccessData instance is set, so that
     * afterwards an AccessData instance is always available.
     */
    protected void setUpAccessData() {
        accessData = null;

        if (accessDataFile != null && accessDataFile.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(accessDataFile);
                AccessDataBase tmp = new AccessDataBase();
                in = new GZIPInputStream(in);
                tmp.read(in);
                accessData = tmp;
            }
            catch (IOException e) {
                // log but ignore
                LOGGER.log(Level.WARNING, "IOException while reading scan data", e);
            }
            finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException e) {
                    // ignore
                    LOGGER.log(Level.WARNING, "IOException while closing stream", e);
                }
            }
        }

        // if no access data file is available or when an error occurred, then at least make sure an
        // AccessData instance is available
        if (accessData == null) {
            accessData = createEmptyAccessData();
        }
    }

    /**
     * Stores the specified access data in the configured access data file, if any. If no access data
     * file has been configured, this method has no effect.
     */
    protected void storeAccessData() {
        // nothing to do when there is no access data or we know no access file to store to
        if (accessData == null || accessDataFile == null) {
            return;
        }

        // we only know how to handle an AccessDataBase
        if (accessData == null) {
            throw new IllegalArgumentException("no AccessData available");
        }
        else if (!(accessData instanceof AccessDataBase)) {
            throw new IllegalArgumentException("unknown AccessData implementation: "
                    + accessData.getClass().getName());
        }

        // cast to the implementation class
        AccessDataBase accessDataBase = (AccessDataBase) createEmptyAccessData();

        // store the data
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(accessDataFile));
            out = new GZIPOutputStream(out);
            accessDataBase.write(out);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to store access data", e);
        }

        // get rid of the AccessData instance, it will be reread the next time a scan is started and only
        // occupies a lot of memory in the mean time
        accessData = null;
    }

    /**
     * Removes the stored AccessData, if any. By default this deletes the access data file if it exists.
     * Subclasses can override this if they want to provide their own AccessData implementation.
     */
    protected void clearAccessData() {
        if (accessDataFile.exists()) {
            accessDataFile.delete();
        }
    }

    /**
     * Stores the current CrawlReport, if any, to the crawl report file, is set.
     */
    protected void storeCrawlReport() {
        if (crawlReport != null && crawlReportFile != null) {
            try {
                OutputStream stream = new BufferedOutputStream(new FileOutputStream(crawlReportFile));
                try {
                    crawlReport.write(stream);
                }
                finally {
                    stream.close();
                }
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to write scan report file", e);
            }
        }
    }
}
