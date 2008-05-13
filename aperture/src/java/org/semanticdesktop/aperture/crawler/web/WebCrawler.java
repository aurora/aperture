/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.FilterAccessData;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.datasource.web.WebDataSource;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.UrlUtil;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Crawler implementation for WebDataSources.
 * 
 * <p>
 * Due to the large amount of information needed to be stored for incremental crawling, the use of a
 * non-in-memory AccessData implementation is advised. Please note that the entire hypertext graph will be
 * stored in this AccessData.
 * 
 * <p>
 * Implementation note: this WebCrawler fetches URLs one-by-one in a single-threaded manner. Previous
 * implementations used a configurable number of threads to fetch the URLs. However, it turned out that even
 * when running with a single thread, the bandwidth was by far the biggest bottle-neck for crawling websites,
 * rather than processing of documents or network latency. In other words: there was no performance gain in
 * using multiple fetch threads but the implementation was a lot more complicated, especially because the
 * listeners handling the results assumed to be running in a single thread. Therefore we've decided to keep
 * this implementation simple and single-threaded.
 */
public class WebCrawler extends CrawlerBase {

    // TODO: replace crawledUrls with a lookup on the AccessData (using the start time of crawling as a key),
    // as this administration requires a lot of main memory. For example:
    // 100,000 URLs x 100 chars average length x 2 bytes per char = 19+ MB, assuming the arrays do not contain
    // emptry trailing parts and not counting the object overhead of the char array, String object and HashSet
    // overhead.

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    private long maxByteSize;

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
    private LinkedList<CrawlJob> jobsQueue;

    /**
     * Additional registration of CrawlJobs, indexed by URL, for fast retrieval.
     */
    private HashMap<String, CrawlJob> jobsMap;

    /**
     * The set of URLs that have been crawled so far during this scan. This set is used
     * only if there is no access data for this crawler.
     */
    private HashSet<String> crawledUrls;

    private int initialDepth;

    private WebAccessData wad;

    public WebCrawler() {
        wad = null;
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
        removeDeprecatedRedirections();
        boolean completed = jobsQueue.isEmpty();
        cleanUp();

        return completed ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
    }

    private void initialize() {
        // make sure some vital properties have been set (gives more intelligible exceptions)
        if (mimeTypeIdentifier == null) {
            throw new IllegalArgumentException("MimeTypeIdentifier missing");
        }
        if (linkExtractorRegistry == null) {
            throw new IllegalArgumentException("LinkExtractorRegistry missing");
        }
        
        // initialize variables
        jobsQueue = new LinkedList<CrawlJob>();
        jobsMap = new HashMap<String, CrawlJob>(1024);
        if (accessData == null) {
            crawledUrls = new HashSet<String>(1024);
        } else {
            wad = new WebAccessData(accessData);
        }

        // fetch crawl instructions from the RDF configuration model
        WebDataSource source = (WebDataSource)getDataSource();
        String startUrl = source.getRootUrl();
        domainBoundaries = source.getDomainBoundaries();
        includeEmbeddedResources = source.getIncludeEmbeddedResources();

        Integer integer = source.getMaximumDepth();
        int crawlDepth = integer == null ? Integer.MAX_VALUE : integer.intValue();

        initialDepth = crawlDepth;

        Long l = source.getMaximumSize();
        maxByteSize = l == null ? Long.MAX_VALUE : l.longValue();

        // schedule the start URL
        schedule(startUrl, crawlDepth, false);
    }

    private void schedule(String url, int crawlDepth, boolean checkDomain) {
        if (url == null) {
            return;
        }

        // normalize http and file URLs
        url = normalizeAndFixURL(url,null).string;
        if (url == null) {
            return;
        }

        // skip when it has been crawled already
        if (isCrawled(url)) {
            return;
        }

        // skip when it fails outside the domain
        if (checkDomain && !domainBoundaries.inDomain(url)) {
            return;
        }

        // check if it is already in the queue
        CrawlJob job = jobsMap.get(url);
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
        ListIterator<CrawlJob> iterator = jobsQueue.listIterator(jobsQueue.size());
        while (iterator.hasPrevious()) {
            CrawlJob scheduledJob = iterator.previous();

            if (scheduledJob.getDepth() >= crawlDepth) {
                // The new job needs to be inserted after this job
                iterator.next();
                break;
            }
        }

        iterator.add(job);
    }

    private boolean isCrawled(String url) {
        if (wad != null) {
            return wad.isTouched(url);
        } else {
            return crawledUrls.contains(url);
        }
    }
    
    private void addCrawled(String url) {
        if (wad != null) {
            wad.touch(url);
        } else {
            crawledUrls.add(url);
        }
    }

    private void processQueue() {
        // loop over all queued jobs
        loop: while (!jobsQueue.isEmpty() && !isStopRequested()) {
            // fetch the job and its properties
            CrawlJob job = jobsQueue.removeFirst();
            String url = job.getURL();
            int depth = job.getDepth();
            
            if(logger.isDebugEnabled()) {
                try {
                    java.net.URI uri = new java.net.URI(url);
                } catch (URISyntaxException use) {
                    logger.debug("Faulty url: " + url);
                }
            } 
            
            
            // notify that we're processing this URL
            //handler.accessingObject(this, url);
            reportAccessingObject(url);

            // see if we've ever accessed this url before
            boolean knownUrl = accessData == null ? false : accessData.isKnownId(url);
            
            // adjust some registries
            addCrawled(url);
            //crawledUrls.add(url);
            jobsMap.remove(url);

            // fetch a DataAccessor for this id
            DataAccessor accessor = getDataAccessor(url);
            if (accessor != null) {
                try {
                    // Fetch the data object. Wrap the AccessData in a WebAccessData to get notified when a
                    // URL redirects to another URL.
                    //RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, url);
                    RDFContainerFactory containerFactory = getRDFContainerFactory(url);

                    DataObject dataObject = accessor.getDataObjectIfModified(url, source, wad, null,
                        containerFactory);

                    // register that this data object has successfully been processed
                    //deprecatedUrls.remove(url);

                    // check if the object is unmodified
                    if (dataObject == null) {
                        // report the object as unmodified
                        //handler.objectNotModified(this, url);
                        //crawlReport.increaseUnchangedCount();
                        reportUnmodifiedDataObject(url);

                        // schedule all its links, which we have stored in a previous crawl
                        if (depth > 0) {
                            scheduleCachedLinks(url, depth - 1);
                        }
                    }
                    // we have a new or changed object
                    else {
                        // if this is the root URI, add that metadata
                        if (depth == initialDepth) {
                            dataObject.getMetadata().add(NIE.rootElementOf, source.getID());
                        }

                        // As the URL may have lead to redirections, the ID of the resulting DataObject may be
                        // different. Make sure this URL is never scheduled or reported during this crawl.
                        String finalUrl = dataObject.getID().toString();
                        if (!finalUrl.equals(url)) {
                            //deprecatedUrls.remove(finalUrl);

                            CrawlJob redundantJob = jobsMap.remove(finalUrl);
                            if (redundantJob != null) {
                                jobsQueue.remove(redundantJob);
                            }

                            // If this is the case, the resulting DataObject may have been reported already.
                            // In that case the DataObject should be ignored, rather than reporting it
                            // multiple times.
                            if (isCrawled(finalUrl)) {
                                dataObject.dispose();
                                continue loop;
                            }
                            else {
                                addCrawled(finalUrl);
                            }
                        }

                        // only report the object when it does not exceed the size limit
                        if (hasAcceptableByteSize(dataObject)) {
                            // extract and schedule links
                            // do this before reporting: you never know what the handler will do to the
                            // DataObject's stream (e.g. reading it without resetting it, closing it)
                            if (dataObject instanceof FileDataObject) {
                                processLinks((FileDataObject) dataObject, depth - 1);
                            }

                            // report the object
                            if (knownUrl) {
                                //handler.objectChanged(this, dataObject);
                                //crawlReport.increaseChangedCount();
                                reportModifiedDataObject(dataObject);
                            }
                            else {
                                //handler.objectNew(this, dataObject);
                                //crawlReport.increaseNewCount();
                                reportNewDataObject(dataObject);
                            }
                        }
                        else {
                            unregisterUrl(url, knownUrl);
                        }
                    }
                }
                catch (UrlNotFoundException e) {
                    unregisterUrl(url, knownUrl);
                }
                catch (IOException e) {
                    logger.info("I/O error while accessing " + url, e);
                }
                catch (Exception e) {
                    // this will catch RuntimeErrors thrown by the accessor
                    // problems have been reported, if the crawler tries to access a URL that is faulty
                    // the accessor will try to create a URI out of it, the URI constructor will throw
                    // an IllegalArgumentException, which will get past here and propagate upwards killing
                    // the entire crawler - this catch should prevent it
                    logger.info("Error while accessing " + url, e);
                }
            }
        }
    }

    private boolean hasAcceptableByteSize(DataObject dataObject) {
        // first test if it makes sense to determine acceptability (retrieving the metadata may be a
        // relatively slow operation)
        if (maxByteSize == Long.MAX_VALUE) {
            return true;
        }
        // now check whether the size is below the threshold
        else {
            Long l = dataObject.getMetadata().getLong(NIE.byteSize);
            return l == null ? true : l.longValue() <= maxByteSize;
        }
    }

    private void unregisterUrl(String url, boolean knownUrl) {
        // if we've accessed this object in the past, we should re-add it to deprecatedIDs, so that it will be
        // reported as removed
        if (knownUrl) {
            //deprecatedUrls.add(url);
            reportDeletedDataObject(url);
        } else {
            accessData.remove(url);
        }

        // furthermore we should not list this object as accessed any longer; when it can be accessed normally
        // in the next crawl, it should be reported as a new object
        //if (accessData != null) {
        //    accessData.remove(url);
        //}
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

    @SuppressWarnings("unchecked")
    private void scheduleCachedLinks(String url, int depth) {
        if (accessData == null) {
            logger
                    .error("Internal error: scheduling cached links for unmodified url while no AccessData is set: "
                            + url);
        }
        else {
            // see if this is a final URL or one that has redirected to another URL in the past
            String redirectedUrl = accessData.get(url, AccessData.REDIRECTS_TO_KEY);
            if (redirectedUrl != null) {
                url = redirectedUrl;
            }

            Set<String> links = accessData.getReferredIDs(url);
            if (links != null) {
                for (String link : links) {
                    schedule(link, depth, true);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processLinks(FileDataObject object, int depth) {
        // remove any previously registered links
        String url = object.getID().toString();
        if (accessData != null) {
            accessData.removeReferredIDs(url);
        }
        
        // first, ensure that the content stream supports mark() and reset()
        InputStream content = getMarkSupportingContent(object);
        // bail out if there was a problem obtaining the content
        if (content == null) { return ; }

        // determine the MIME type
        String mimeType = getMimeType(content, object);
        // bail out if MIME type determination has  not produced anything
        if (mimeType == null) { return; }

        // fetch a LinkExtractor for this MIME type and exit when there is none
        LinkExtractor extractor = getLinkExtractor(mimeType);
        // bail out if there is no link extractor for this mime type
        if (extractor == null) { return; }

        // get a ByteArrayInputStream (details, see the method)
        content = getByteArrayContent(content, object);
        // bail out if there was a problem buffering the stream
        if (content == null) { return ; }

        // extract the links
        List<String> links = getLinks(content,extractor, url);
        // bail out if no links were produced
        if (links == null) { return ; }

        // schedule and register these links
        
        // Keep a local set of scheduled links to prevent duplicate scheduling ASAP (the schedule
        // method will take care of normalization and reconsider the issue afterwards). We don't use
        // crawledUrls or jobsMap for this purpose as, regardless of whether the crawling status of
        // the link, we must register it in accessData.
        HashSet<String> scheduledLinks = new HashSet<String>(links.size());

        for (String link : links) {
            StringUriPair pair = normalizeAndFixURL(link, object.getMetadata().getModel());
            link = pair.string;
            URI linkedResourceUri = pair.uri;
            if (link == null) {
                // this means that after all the efforts, the link could not be converted to
                // a correct URI and crawling it will be impossible
                continue;
            }
            if (!url.equals(link) && !scheduledLinks.contains(link)) {
                if (depth >= 0) {
                    // if creating the link failed, don't crash out with an exception, just skip it
                    if(link != null) {
                        // now we can schedule the link (which might have been encoded)
                        schedule(link, depth, true);
                        
                        if(linkedResourceUri != null) {
                            object.getMetadata().add(NIE.links,linkedResourceUri);
                            
                            // The following triple needs to be added to satiate the validator complaining
                            // about links to resources that are outside the crawling domain and don't have
                            // their types set properly
                            object.getMetadata().getModel().addStatement(linkedResourceUri,RDF.type,NIE.DataObject);
                            scheduledLinks.add(link);
                        }
                    }
                    else {
                        logger.warn("WebCrawler is skipping link {}", link);
                        // don't allow it to get into AccessData
                        continue;
                    }
                }
                // this is here, because we want to include an entry about a link, even if it
                // already does contain
                if (accessData != null) {
                    accessData.putReferredID(url, link);
                }
            }
        }
    }
    
    private static class StringUriPair {
        private String string;
        private URI uri;
        public StringUriPair(String string, URI uri) {
            this.string = string;
            this.uri = uri;
        }
    }

    private List<String> getLinks(InputStream content, LinkExtractor extractor, String url) {
        try {
            // as it's a ByteArrayInputStream, its read limit actually has no effect
            content.mark(Integer.MAX_VALUE);

            // create parameters to direct the LinkExtractor
            HashMap<Object, Object> params = new HashMap<Object, Object>();
            params.put(LinkExtractor.BASE_URL_KEY, url);
            if (includeEmbeddedResources != null) {
                params.put(LinkExtractor.INCLUDE_EMBEDDED_RESOURCES_KEY, includeEmbeddedResources);
            }

            // extract all links
            return extractor.extractLinks(content, params);
        }
        catch (Exception e) {
            logger.info("IOException while extracting links", e);
        }
        finally {
            // make sure the stream can be read again
            try {
                content.reset();
            }
            catch (IOException e) {
                logger.warn("internal error: IOException while resetting a ByteArrayInputStream", e);
            }
        }
        return null;
    }

    private InputStream getMarkSupportingContent(FileDataObject object) {
        try {
            InputStream content = null;
            content = object.getContent();
            if (!content.markSupported()) {
                content = new BufferedInputStream(content);
            }
            return content;
        } catch (IOException ioe) {
            logger.info("IOException while obtaining the object content", ioe);
            // the stream is now in an undetermined state: remove it to prevent any processing
            object.setContent(null);
            // no use to continue
            return null;
        }
    }

    private InputStream getByteArrayContent(InputStream content, FileDataObject object) {
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
                logger.warn("IOException while buffering document", e);
                object.setContent(null);
                return null;
            }
            object.setContent(content);
            return content;
        } else {
            // the stream already is buffered, no need to do it once more
            return content;
        }
    }

    private String getMimeType(InputStream content, FileDataObject object) {
        String mimeType = null;
        
        try {
            int bufferSize = mimeTypeIdentifier.getMinArrayLength();
            
            content.mark(bufferSize);

            try {
                byte[] magicBytes = IOUtil.readBytes(content, bufferSize);
                mimeType = mimeTypeIdentifier.identify(magicBytes, null, object.getID());
            }
            finally {
                content.reset();
            }
        }
        catch (IOException ioe) {
            logger.debug("IOError while determining the mime type",ioe);
            // the stream may now be in an undetermined state, remove it to preclude futher processing
            try {
                content.close();
            } catch (Exception e) {
                // do nothing, there is no use to
            }
            object.setContent(null);
        }
        
        // fall-back to what the server returned
        if (mimeType == null) {
            mimeType = object.getMetadata().getString(NIE.mimeType);
        }
        else {
            // overrule the DataObject's MIME type: magic-number based determination is much more reliable
            // than what web servers return, especially for non-web formats
            object.getMetadata().put(NIE.mimeType, mimeType);
        }
        
        return mimeType;
    }

    private LinkExtractor getLinkExtractor(String mimeType) {
        Set factories = linkExtractorRegistry.get(mimeType);
        if (!factories.isEmpty()) {
            LinkExtractorFactory factory = (LinkExtractorFactory) factories.iterator().next();
            return factory.get();
        } else {
            return null;
        }
    }

   
    /**
     * This method does it's best to turn a string into a valid URI, normalized and clean. This
     * method is a one-stop-shop for all Url validation and fixing algorithms
     * @param url
     * @return
     */
    private StringUriPair normalizeAndFixURL(String url, Model model) {
        // normalize http and file URLs
        String resultUrl = url;
        if (url.startsWith("file:") || url.startsWith("http:") || url.startsWith("https:")) {
            URL parsedUrl;
            try {
                parsedUrl = new URL(url);
                String externalForm = UrlUtil.normalizeURL(parsedUrl).toExternalForm();
                // let's apply an additional check
                resultUrl = externalForm;
            }
            catch (MalformedURLException e) {
                // if it cannot be parsed, it can definitely never successfully be retrieved: ignore it
                return new StringUriPair(null, null);
            } 
        }
        
        // now the URL is more-or-less normalized, we may try to turn it into a URI
        URI resultUri = null;
        
        // let's apply an additional check, it doesn't make sense to crawl URL's that aren't valid uris
        // because AccessData secretely assumes that ids are uris
        // this has caused problems
        try {
            if (model != null) {
                resultUri = model.createURI(resultUrl);
            } else {
                resultUri = new URIImpl(resultUrl);
            }
        }
        catch(IllegalArgumentException iae) {
            // try again after encoding the link
            try {
                if (resultUrl.startsWith("file:") || resultUrl.startsWith("http:") || resultUrl.startsWith("https:")) {
                    try {
                        URL parsedLink = new URL(resultUrl);
                        java.net.URI parsedUri = new java.net.URI(parsedLink.getProtocol(), parsedLink.getAuthority(), parsedLink.getPath(), parsedLink.getQuery(), parsedLink.getRef());
                        resultUrl = parsedUri.toString();
                        resultUri = model.createURI(resultUrl);
                    }
                    catch(MalformedURLException mfe) {
                        resultUrl = null;
                        resultUri = null;
                    }
                    catch (URISyntaxException e) {
                        resultUrl = null;
                        resultUri = null;
                   }
                } else {
                    // this means that there is nothing we can possibly do
                    resultUrl = null;
                    resultUri = null;
                }
            }
            catch (ModelRuntimeException e) {
                logger.debug("Unable to create URI for link {}", resultUrl);
                resultUrl = null;
                resultUri = null;
            }
        }
        return new StringUriPair(resultUrl,resultUri);
    }

    
    
    private void removeDeprecatedRedirections() {
        // the access data may get populated with "orphaned redirections", URLs that redirect to other URLs
        // but that themselves are never used as links. This may for example happen due to session IDs in the
        // URLs. These URLs should not be removed from the access data and not be reported as removed.
        if (accessData != null) {
            
            // this implementation may be inefficient in some cases, we can't touch the id's while we're
            // iterating over the untouched id's iterator
            
            Set<String> deprecatedRedirections = new HashSet<String>();
            Iterator iter = accessData.getUntouchedIDsIterator();
            while (iter.hasNext()) {
                String url = iter.next().toString();
                if (accessData.get(url, AccessData.REDIRECTS_TO_KEY) != null) {
                    deprecatedRedirections.add(url);
                }
            }
            for (String dep : deprecatedRedirections) {
                accessData.touch(dep);
                accessData.remove(dep, AccessData.REDIRECTS_TO_KEY);
            }
            
            // the previous implementation that worked with the deprecatedUrls set
            //Iterator<String> iterator = deprecatedUrls.iterator();
            //while (iterator.hasNext()) {
            //    String url = iterator.next();
            //    if (accessData.get(url, AccessData.REDIRECTS_TO_KEY) != null) {
            //        accessData.remove(url, AccessData.REDIRECTS_TO_KEY);
            //        iterator.remove();
            //    }
            //}
        }
    }

    private void cleanUp() {
        domainBoundaries = null;
        jobsQueue = null;
        jobsMap = null;
        crawledUrls = null;
        includeEmbeddedResources = null;
    }

    private class WebAccessData extends FilterAccessData {

        public WebAccessData(AccessData accessData) {
            super(accessData);
        }

        public void put(String id, String key, String value) {
            // Make sure the original URL is not accessed anymore. We need to do this using such a complicated
            // approach (using a wrapped AccessData instance) because there may be several redirection steps
            // between the URL passed to the DataAccessor and the URL of the final DataObject.
            if (REDIRECTS_TO_KEY.equals(key)) {
                // do this with the id rather than the value: processingQueue depends on this in order to be
                // able to do a crawledUrls.contains on the last URL in the redirection chain
                //crawledUrls.add(id); // this is obviously not needed, if we use AccessData, then crawledUrls is null
                //deprecatedUrls.remove(id);
                touch(id);

                CrawlJob job = jobsMap.remove(id);
                if (job != null) {
                    jobsQueue.remove(job);
                }
            }

            super.put(id, key, value);
        }
    }
}
