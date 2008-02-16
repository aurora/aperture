/*
 * Copyright (c) 2005 - 2007 Aduna.
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
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.activation.MimeType;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.CrawlReport;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the Crawler interface that offers generic implementations for some of its methods.
 */
public abstract class CrawlerBase implements Crawler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The DataSource representing the physical source of information.
	 */
	protected DataSource source;

	/**
	 * The current DataAccessorRegistry.
	 */
	protected DataAccessorRegistry accessorRegistry;

	/**
	 * The current AccessData instance.
	 */
	protected AccessData accessData;

	/**
	 * The file for persistent storage of CrawlReports.
	 */
	protected File crawlReportFile;

	/**
	 * The CrawlReport containing statistics about the last or ongoing crawl. Created by the crawl method or
	 * sometimes lazily created by the getLastCrawlReport method when trying to retrieve the last report of a
	 * previous session.
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
	 * A set that is used to temporary record all urls that do no longer point to existing resources, so that
	 * we can report them as removed.
	 */
	protected Set<String> deprecatedUrls;

	private DomainBoundaries domain;

	/**
	 * Used to synchronize the access to the subCrawler field.
	 */
	private Object subCrawlerMonitor = new Object();
	
	/**
	 * The SubCrawler that is currently running.
	 */
	private SubCrawler subCrawler;
	
	/**
	 * The default constructor
	 */
	public CrawlerBase() {
		this.stopRequested = false;
	}

	/**
	 * Sets the data source
	 * @param source the new data source
	 */
	public void setDataSource(DataSource source) {
		this.source = source;
	}

	/**
	 * Returns the data source
	 * @return the data source
	 * @see Crawler#getDataSource()
	 */
	public DataSource getDataSource() {
		return source;
	}

	/**
	 * Sets the data accessor registry
	 * @param accessorRegistry the new data accessor registry
	 * @see Crawler#setDataAccessorRegistry(DataAccessorRegistry)
	 */
	public void setDataAccessorRegistry(DataAccessorRegistry accessorRegistry) {
		this.accessorRegistry = accessorRegistry;
	}

	/**
	 * Returns the data accessor registry
	 * @return the data accessor registry
	 * @see Crawler#getDataAccessorRegistry()
	 */
	public DataAccessorRegistry getDataAccessorRegistry() {
		return accessorRegistry;
	}

	/**
	 * Sets the AccessData instance to be used by the crawler
	 * @param accessData the AccessData instance to be used by the crawler
	 * @see Crawler#setAccessData(AccessData)
	 */
	public void setAccessData(AccessData accessData) {
		this.accessData = accessData;
	}

	/**
	 * Returns the AccessData instance used by the crawler
	 * @return the AccessData instance used by the crawler
	 * @see Crawler#getAccessData()
	 */
	public AccessData getAccessData() {
		return accessData;
	}

	/**
	 * Sets the crawler handler
	 * @param handler the crawler handler
	 * @see Crawler#setCrawlerHandler(CrawlerHandler)
	 */
	public void setCrawlerHandler(CrawlerHandler handler) {
		this.handler = handler;
	}

	/**
	 * Returns the crawler handler
	 * @return the crawler handler
	 * @see Crawler#getCrawlerHandler()
	 */
	public CrawlerHandler getCrawlerHandler() {
		return handler;
	}

	/**
	 * @see Crawler#crawl()
	 */
	@SuppressWarnings("unchecked")
	public synchronized void crawl() {
		// set up a new CrawlReport
		crawlReport = new CrawlReportBase();
		crawlReport.setCrawlStarted(System.currentTimeMillis());

		domain = ConfigurationUtil.getDomainBoundaries(source.getConfiguration());

		// initialize flags
		stopRequested = false;
		ExitCode exitCode = null;

		// we do all this before notifying the CrawlerHandler as its implementation may depend on this
		// initialization
		handler.crawlStarted(this);

		try {
			// read the access data from the previous crawl
			deprecatedUrls = Collections.emptySet();

			if (accessData != null) {
				accessData.initialize();

				// this set will at the end of the crawl prodecure hold the urls of resources found in a
				// previous crawl that can no longer be found
				deprecatedUrls = new HashSet<String>(accessData.getStoredIDs());
			}

			// start crawling
			exitCode = crawlObjects();

			// only when the scan was completed succesfully will we report removed resources,
			// else we might indirectly destroy information that may still be up-to-date
			if (exitCode.equals(ExitCode.COMPLETED)) {
				crawlReport.setRemovedCount(deprecatedUrls.size());
				reportRemoved(deprecatedUrls);
			}

			// this set *can* be very large, get rid of it ASAP
			deprecatedUrls = null;

			// store the access data
			if (accessData != null) {
				accessData.store();
			}
		}
		catch (IOException e) {
			logger.error("IOException while accessing AccessData", e);
			exitCode = ExitCode.FATAL_ERROR;
		}

		// wrap up and store the CrawlReport
		crawlReport.setExitCode(exitCode);
		crawlReport.setCrawlStopped(System.currentTimeMillis());
		storeCrawlReport();

		// notify the CrawlerHandler
		handler.crawlStopped(this, exitCode);
	}

	/**
	 * Method called by crawl() that should implement the actual crawling of the DataSource. The return value
	 * of this method should indicate whether the scanning was completed successfully (i.e. it wasn't
	 * interrupted or anything). Also this method is expected to update the deprecatedUrls set, as any
	 * remaining URLs in this set will be removed as being removed after this method completes.
	 * 
	 * @return An ExitCode indicating how the crawl procedure terminated.
	 */
	protected abstract ExitCode crawlObjects();

	/**
	 * @see Crawler#stop()
	 */
	public void stop() {
		synchronized (subCrawlerMonitor) {
		    stopRequested = true;
            if (subCrawler != null) {
                subCrawler.stopSubCrawler();
            }
        }
	}

	/**
	 * Returns true if the crawler is currently stopping, false otherwise
	 * @return true if the crawler is currently stopping, false otherwise
	 */
	public boolean isStopRequested() {
		return stopRequested;
	}

	/**
	 * Reports all IDs stored in the AccessData as being cleared to the CrawlerHandler and then gets rid of
	 * the AccessData instance.
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		handler.clearStarted(this);

		ExitCode exitCode = ExitCode.COMPLETED;

		try {
			if (accessData != null) {
				// read the persistent access data
				accessData.initialize();

				// Report removal of data objects
				Iterator<String> iterator = accessData.getStoredIDs().iterator();
				while (!stopRequested && iterator.hasNext()) {
					clear(iterator.next());
				}

				// remove persistent access data registration
				accessData.clear();

				if (stopRequested) {
					exitCode = ExitCode.STOP_REQUESTED;
				}
			}
		}
		catch (IOException e) {
			logger.error("IOException while accessing AccessData", e);
			exitCode = ExitCode.FATAL_ERROR;
		}

		handler.clearFinished(this, exitCode);
	}

	protected void clear(String url) {
		handler.clearingObject(this, url);
	}

	/**
	 * Sets the file where the crawl report is to be saved
	 * @param file the file where the crawl report is to be saved
	 */
	public void setCrawlReportFile(File file) {
		this.crawlReportFile = file;
	}

	/**
	 * Returns the file where the crawl report is to be saved
	 * @return the file where the crawl report is to be saved
	 */
	public File getCrawlReportFile() {
		return crawlReportFile;
	}

	/**
	 * @see Crawler#getCrawlReport()
	 */
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
				logger.error("Unable to load crawl report file", e);
			}
		}

		return crawlReport;
	}

	protected void reportRemoved(Set<String> ids) {
		for (String url : ids) {
			if (accessData != null) {
				accessData.remove(url);
			}
			handler.objectRemoved(this, url);
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
				logger.error("Unable to write crawl report file", e);
			}
		}
	}

	protected boolean inDomain(String uri) {
		return domain.inDomain(uri);
	}

    /** 
     * @see org.semanticdesktop.aperture.crawler.Crawler#runSubCrawler(org.semanticdesktop.aperture.subcrawler.SubCrawler, org.semanticdesktop.aperture.accessor.DataObject, java.io.InputStream, java.nio.charset.Charset, javax.activation.MimeType)
     */
    public void runSubCrawler(SubCrawler localSubCrawler, DataObject object, InputStream stream,
            Charset charset, String mimeType) throws SubCrawlerException {
        try {
            synchronized (subCrawlerMonitor) {
                if (this.subCrawler != null) {
                    throw new SubCrawlerException("Only one SubCrawler can run at a time");
                }
                else if (stopRequested) {
                    logger.debug("Not starting the subCrawler, the crawler has been requested to stop");
                    return;
                }
                else {
                    this.subCrawler = localSubCrawler;
                }
            }
            subCrawler.subCrawl(object.getID(), stream, new DefaultSubCrawlerHandler(handler, this),
                this.source, this.accessData, charset, mimeType, object.getMetadata());
        }
        finally {
            synchronized (subCrawlerMonitor) {
                this.subCrawler = null;
            }
        }
    }
	
    /**
     * A default simple implementation of the SubCrawlerHandler interface. It delegates all callbacks to the
     * CrawlerHandler. This class is static to prevent it from accessing the private fields of CrawlerBase
     * directly, just that we retain control over who calls what.
     */
    private static class DefaultSubCrawlerHandler implements SubCrawlerHandler {

        private CrawlerHandler innerHandler;
        private CrawlerBase localCrawler;
        
        /**
         * A default constructor.
         * 
         * @param innerHandler the crawler handler that is to receive notifications
         * @param innerCrawler the crawler that will be used as the source of events passed to the crawler handler
         * @throws NullPointerException if the handler is null
         */
        public DefaultSubCrawlerHandler(CrawlerHandler innerHandler, CrawlerBase innerCrawler) {
            this.innerHandler = innerHandler;
            this.localCrawler = innerCrawler;
        }
        
        /**
         * @see SubCrawlerHandler#getRDFContainerFactory(String)
         */
        public RDFContainerFactory getRDFContainerFactory(String url) {
            return innerHandler.getRDFContainerFactory(localCrawler, url);
        }
        
        /**
         * @see SubCrawlerHandler#objectChanged(DataObject)
         */
        public void objectChanged(DataObject object) {
            localCrawler.deprecatedUrls.remove(object.getID().toString());
            innerHandler.objectChanged(localCrawler, object);
        }
        
        /**
         * @see SubCrawlerHandler#objectNew(DataObject)
         */
        public void objectNew(DataObject object) {
            localCrawler.deprecatedUrls.remove(object.getID().toString());
            innerHandler.objectNew(localCrawler, object);
            
        }
        
        /**
         * @see SubCrawlerHandler#objectNotModified(String)
         */
        public void objectNotModified(String url) {
            localCrawler.deprecatedUrls.remove(url);
            innerHandler.objectNotModified(localCrawler, url);
        }
    }
}
