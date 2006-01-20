/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.UrlUtil;

/**
 * A Crawler implementation for WebDataSources.
 */
public class WebCrawler extends CrawlerBase {

    private static final Logger LOGGER = Logger.getLogger(WebCrawler.class.getName());

    /**
     * The DataAccessorRegistry used to fetch DataAccessorFactories for specific URL schemes.
     */
    private DataAccessorRegistry accessorRegistry;

    /**
     * The MimeTypeIdentifier used to determine the mime type of a DataObject.
     */
    private MimeTypeIdentifier mimeTypeIdentifier;

    /**
     * The LinkExtractorRegistry used to fetch LinkExtractorFactories for specific MIME types.
     */
    private LinkExtractorRegistry linkExtractorRegistry;

    /**
     * The DataSource's byte size limit.
     */
    private int maxByteSize;

    /**
     * A Boolean indicating the preference for crawling embedded, not-hyperlinked resources (images,
     * background, etc.).
     */
    private Boolean includeEmbeddedResources;

    /**
     * A DomainBoundaries holding the url include and exclude patterns.
     */
    private DomainBoundaries domainBoundaries;

    /**
     * A sorted list of CrawlJobs. The jobs are ordered so that jobs with the largest depths come first.
     */
    private LinkedList jobsQueue;

    /**
     * Additional registration of CrawlJobs, indexed by URL (a String), for fast retrieval.
     */
    private HashMap jobsMap;

    /**
     * The set of URLs (Strings) that have been crawled so far during this scan.
     */
    private HashSet crawledUrls;

    public void setDataAccessorRegistry(DataAccessorRegistry accessorRegistry) {
        this.accessorRegistry = accessorRegistry;
    }

    public DataAccessorRegistry getDataAccessorRegistry() {
        return accessorRegistry;
    }

    public void setMimeTypeIdentifier(MimeTypeIdentifier mimeTypeIdentifier) {
        this.mimeTypeIdentifier = mimeTypeIdentifier;
    }

    public MimeTypeIdentifier getMimeTypeIdentifier() {
        return mimeTypeIdentifier;
    }

    public void setLinkExtractorRegistry(LinkExtractorRegistry linkExtractorRegistry) {
        this.linkExtractorRegistry = linkExtractorRegistry;
    }

    public LinkExtractorRegistry getLinkExtractorRegistry() {
        return linkExtractorRegistry;
    }

    protected ExitCode crawlObjects() {
        initialize();
        processQueue();
        cleanUp();

        return jobsQueue.isEmpty() ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
    }

    private void initialize() {
        // initialize variables
        jobsQueue = new LinkedList();
        jobsMap = new HashMap(1024);
        crawledUrls = new HashSet(1024);

        // fetch crawl instructions from the RDF configuration model
        RDFContainer configuration = getDataSource().getConfiguration();
        String startUrl = ConfigurationUtil.getRootUrl(configuration);
        domainBoundaries = ConfigurationUtil.getDomainBoundaries(configuration);
        includeEmbeddedResources = ConfigurationUtil.getIncludeEmbeddedResourceS(configuration);

        Integer integer = ConfigurationUtil.getMaximumDepth(configuration);
        int crawlDepth = integer == null ? Integer.MAX_VALUE : integer.intValue();

        integer = ConfigurationUtil.getMaximumByteSize(configuration);
        maxByteSize = integer == null ? Integer.MAX_VALUE : integer.intValue();

        // schedule the start URL
        schedule(startUrl, crawlDepth, false);
    }

    private void schedule(String url, int crawlDepth, boolean checkDomain) {
        if (url == null) {
            return;
        }

        // normalize http and file URLs
        url = normalizeURL(url);
        if (url == null) {
            return;
        }

        // skip when it has been crawled already
        if (crawledUrls.contains(url)) {
            return;
        }

        // skip when it fails outside the domain
        if (checkDomain && !domainBoundaries.inDomain(url)) {
            return;
        }

        // check if it is already in the queue
        CrawlJob job = (CrawlJob) jobsMap.get(url);
        if (job == null) {
            // the url has not been queued yet: schedule it now
            // note that it will be inserted in the jobsQueue later in this method
            job = new CrawlJob(url, crawlDepth);
            jobsMap.put(url, job);
        }
        else {
            // there is an existing job for this url: maximize its depth
            if (job.getDepth() >= crawlDepth) {
                // no further scheduling actions necessary
                return;
            }
            else {
                job.setDepth(crawlDepth);

                // remove it from the jobsQueue as it will be re-inserted below at the appropriate
                // location
                jobsQueue.remove(job);
            }
        }

        // Insert the new or updated CrawlJob just after the last job with a
        // higher or equal depth, if any. It is assumed that most of these jobs
        // should be inserted at or near the end of the jobsQueue so we iterate
        // through the jobsQueue backwards to find the place to insert the job.
        ListIterator iterator = jobsQueue.listIterator(jobsQueue.size());
        while (iterator.hasPrevious()) {
            CrawlJob scheduledJob = (CrawlJob) iterator.previous();

            if (scheduledJob.getDepth() >= crawlDepth) {
                // The new job needs to be inserted after this job
                iterator.next();
                break;
            }
        }

        iterator.add(job);
    }

    private void processQueue() {
        // loop over all queued jobs
        while (!jobsQueue.isEmpty() && !isStopRequested()) {
            // fetch the job and its properties
            CrawlJob job = (CrawlJob) jobsQueue.removeFirst();
            String url = job.getURL();
            int depth = job.getDepth();

            // notify that we're processing this URL
            handler.accessingObject(this, url);

            // adjust some registries
            crawledUrls.add(url);
            jobsMap.remove(url);

            // see if we've ever accessed this url before
            boolean knownUrl = accessData.isKnownId(url);

            // fetch a DataAccessor for this id
            DataAccessor accessor = getDataAccessor(url);
            if (accessor != null) {
                try {
                    // fetch the data object
                    RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, url);
                    DataObject dataObject = accessor.getDataObjectIfModified(url, source, accessData, null,
                            containerFactory);

                    // register that this data object has successfully been processed
                    deprecatedUrls.remove(url);

                    // check if the object is unmodified
                    if (dataObject == null) {
                        // report the object as unmodified
                        handler.objectNotModified(this, url);
                        crawlReport.increaseUnchangedCount();

                        // schedule all its links, which we have stored in a previous crawl
                        if (depth > 0) {
                            scheduleCachedLinks(url, depth - 1);
                        }
                    }
                    // we have a new or changed object
                    else {
                        // only report the object when it does not exceed the size limit
                        if (hasAcceptableByteSize(dataObject)) {
                            // report the object
                            if (knownUrl) {
                                handler.objectChanged(this, dataObject);
                                crawlReport.increaseChangedCount();
                            }
                            else {
                                handler.objectNew(this, dataObject);
                                crawlReport.increaseNewCount();
                            }

                            // extract and schedule links
                            if (depth > 0 && dataObject instanceof FileDataObject) {
                                scheduleLinks((FileDataObject) dataObject, url, depth - 1);
                            }
                        }
                        else {
                            // if we've accessed this object in the past, we should re-add it to
                            // deprecatedIDs, so that it will be reported as removed
                            if (knownUrl) {
                                deprecatedUrls.add(url);
                            }
                        }
                    }
                }
                catch (IOException e) {
                    LOGGER.log(Level.INFO, "I/O error while accessing " + url, e);
                }
            }
        }
    }

    private boolean hasAcceptableByteSize(DataObject dataObject) {
        // first test if it makes sense to determine acceptability (retrieving the metadata may be a
        // relatively slow operation)
        if (maxByteSize == Integer.MAX_VALUE) {
            return true;
        }
        // now check whether the size is below the threshold
        else {
            Integer integer = dataObject.getMetadata().getInteger(Vocabulary.BYTE_SIZE);
            return integer == null ? true : integer.intValue() <= maxByteSize;
        }
    }

    private DataAccessor getDataAccessor(String url) {
        // determine the scheme
        int index = url.indexOf(':');
        if (index <= 0) {
            return null;
        }
        String scheme = url.substring(0, index);

        // fetch a DataAccessor for this scheme
        Set factories = accessorRegistry.get(scheme);
        if (factories.isEmpty()) {
            return null;
        }
        DataAccessorFactory factory = (DataAccessorFactory) factories.iterator().next();
        return factory.get();
    }

    private void scheduleCachedLinks(String url, int depth) {
        Set links = accessData.getReferredIDs(url);

        if (links != null) {
            Iterator iterator = links.iterator();
            while (iterator.hasNext()) {
                String link = (String) iterator.next();
                schedule(link, depth, true);
            }
        }
    }

    private void scheduleLinks(FileDataObject object, String url, int depth) {
        InputStream content = null;

        // determine the MIME type
        String mimeType = null;
        try {
            int bufferSize = mimeTypeIdentifier.getMinArrayLength();
            content = object.getContent();
            content.mark(bufferSize);

            try {
                byte[] magicBytes = IOUtil.readBytes(content, bufferSize);
                mimeType = mimeTypeIdentifier.identify(magicBytes, null, object.getID());
            }
            finally {
                content.reset();
            }
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "IOException while determining MIME type", e);
            // the stream is now in an undetermined state: remove it to prevent any processing
            object.setContent(null);

            // no use to continue
            return;
        }

        // bail out if MIME type determination was unsuccesful
        if (mimeType == null) {
            return;
        }

        // fetch a LinkExtractor for this MIME type and exit when there is none
        LinkExtractor extractor = null;
        Set factories = linkExtractorRegistry.get(mimeType);
        if (!factories.isEmpty()) {
            LinkExtractorFactory factory = (LinkExtractorFactory) factories.iterator().next();
            extractor = factory.get();
        }

        if (extractor == null) {
            return;
        }

        // Read the stream fully in a ByteArrayInputStream as we will process it entirely (so mark/reset
        // is a bit inappropriate) and it is likely that a CrawlerHandler implementation will do so too.
        // FIXME: there are some optimization possibilities here: IOUtil.readBytes uses a
        // ByteArrayOutputStream that internally uses a growing array, each time copying the old array
        // into the new array. Finally, its toArray method returns a copy of the internal array.
        // Optimization possibility: minimize the number of array allocations and copies.
        if (!(content instanceof ByteArrayInputStream)) {
            try {
                content = new ByteArrayInputStream(IOUtil.readBytes(content));
            }
            catch (IOException e) {
                LOGGER.log(Level.WARNING, "IOException while buffering document", e);
                object.setContent(null);
                return;
            }
            object.setContent(content);
        }

        // extract the links
        List links = null;
        try {
            // as it's a ByteArrayInputStream, its read limit actually has no effect
            content.mark(Integer.MAX_VALUE);

            // create parameters to direct the LinkExtractor
            HashMap params = new HashMap();
            params.put(LinkExtractor.BASE_URL_KEY, url);
            if (includeEmbeddedResources != null) {
                params.put(LinkExtractor.INCLUDE_EMBEDDED_RESOURCES_KEY, includeEmbeddedResources);
            }

            // extract all links
            links = extractor.extractLinks(content, params);
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "IOException while extracting links", e);
        }
        finally {
            // make sure the stream can be read again
            try {
                content.reset();
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE,
                        "internal error: IOException while resetting a ByteArrayInputStream", e);
            }
        }

        // schedule and register these links
        if (links != null) {
            // Keep a local set of scheduled links to prevent duplicate scheduling ASAP (the schedule
            // method will take care of normalization and reconsider the issue afterwards). We don't use
            // crawledUrls or jobsMap for this purpose as, regardless of whether the crawling status of
            // the link, we must register it in accessData.
            HashSet scheduledLinks = new HashSet(links.size());

            Iterator iterator = links.iterator();
            while (iterator.hasNext()) {
                String link = (String) iterator.next();

                link = normalizeURL(link);
                if (link == null) {
                    continue;
                }

                if (!url.equals(link) && !scheduledLinks.contains(link)) {
                    schedule(link, depth, true);
                    scheduledLinks.add(link);
                    accessData.putReferredID(url, link);
                }
            }
        }
    }

    private String normalizeURL(String url) {
        // normalize http and file URLs
        if (url.startsWith("file:") || url.startsWith("http:") || url.startsWith("https:")) {
            URL parsedUrl;
            try {
                parsedUrl = new URL(url);
                return UrlUtil.normalizeURL(parsedUrl).toExternalForm();
            }
            catch (MalformedURLException e) {
                // if it cannot be parsed, it can definitely never successfully be retrieved: ignore it
                return null;
            }
        }

        return url;
    }

    private void cleanUp() {
        domainBoundaries = null;
        jobsQueue = null;
        jobsMap = null;
        crawledUrls = null;
        includeEmbeddedResources = null;
    }
}
