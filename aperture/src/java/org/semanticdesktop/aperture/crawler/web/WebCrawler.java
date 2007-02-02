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
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractor;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.UrlUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * A Crawler implementation for WebDataSources.
 * 
 * <p>
 * Due to the large amount of information needed to be stored for incremental crawling, the use of a
 * RepositoryAccessData or similar on-disk AccessData implementation is advised. Please note that the entire
 * hypertext graph will be stored in this AccessData.
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

	private static final Logger LOGGER = Logger.getLogger(WebCrawler.class.getName());

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
	 * The set of URLs that have been crawled so far during this scan.
	 */
	private HashSet<String> crawledUrls;

	private int initialDepth;

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
		crawledUrls = new HashSet<String>(1024);

		// fetch crawl instructions from the RDF configuration model
		RDFContainer configuration = getDataSource().getConfiguration();
		String startUrl = ConfigurationUtil.getRootUrl(configuration);
		domainBoundaries = ConfigurationUtil.getDomainBoundaries(configuration);
		includeEmbeddedResources = ConfigurationUtil.getIncludeEmbeddedResourceS(configuration);

		Integer integer = ConfigurationUtil.getMaximumDepth(configuration);
		int crawlDepth = integer == null ? Integer.MAX_VALUE : integer.intValue();

		initialDepth = crawlDepth;

		Long l = ConfigurationUtil.getMaximumByteSize(configuration);
		maxByteSize = l == null ? Long.MAX_VALUE : l.longValue();

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

	private void processQueue() {
		// loop over all queued jobs
		loop: while (!jobsQueue.isEmpty() && !isStopRequested()) {
			// fetch the job and its properties
			CrawlJob job = jobsQueue.removeFirst();
			String url = job.getURL();
			int depth = job.getDepth();
			
			// notify that we're processing this URL
			handler.accessingObject(this, url);

			// adjust some registries
			crawledUrls.add(url);
			jobsMap.remove(url);

			// see if we've ever accessed this url before
			boolean knownUrl = accessData == null ? false : accessData.isKnownId(url);

			// fetch a DataAccessor for this id
			DataAccessor accessor = getDataAccessor(url);
			if (accessor != null) {
				try {
					// Fetch the data object. Wrap the AccessData in a WebAccessData to get notified when a
					// URL redirects to another URL.
					RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, url);
					WebAccessData wad = null;
					if (accessData != null) {
						wad = new WebAccessData(accessData);
                    }
				
					DataObject dataObject = accessor.getDataObjectIfModified(url, source, wad, null, containerFactory);

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
						// if this is the root URI, add that metadata
						if (depth == initialDepth) {
							dataObject.getMetadata().add(DATA.rootFolderOf, source.getID());
						}

						// As the URL may have lead to redirections, the ID of the resulting DataObject may be
						// different. Make sure this URL is never scheduled or reported during this crawl.
						String finalUrl = dataObject.getID().toString();
						if (!finalUrl.equals(url)) {
							deprecatedUrls.remove(finalUrl);

							CrawlJob redundantJob = jobsMap.remove(finalUrl);
							if (redundantJob != null) {
								jobsQueue.remove(redundantJob);
							}

							// If this is the case, the resulting DataObject may have been reported already.
							// In that case the DataObject should be ignored, rather than reporting it
							// multiple times.
							if (crawledUrls.contains(finalUrl)) {
								dataObject.dispose();
								continue loop;
							}
							else {
								crawledUrls.add(finalUrl);
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
								handler.objectChanged(this, dataObject);
								crawlReport.increaseChangedCount();
							}
							else {
								handler.objectNew(this, dataObject);
								crawlReport.increaseNewCount();
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
					LOGGER.log(Level.INFO, "I/O error while accessing " + url, e);
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
			Long l = dataObject.getMetadata().getLong(DATA.byteSize);
			return l == null ? true : l.longValue() <= maxByteSize;
		}
	}

	private void unregisterUrl(String url, boolean knownUrl) {
		// if we've accessed this object in the past, we should re-add it to deprecatedIDs, so that it will be
		// reported as removed
		if (knownUrl) {
			deprecatedUrls.add(url);
		}

		// furthermore we should not list this object as accessed any longer; when it can be accessed normally
		// in the next crawl, it should be reported as a new object
        if (accessData != null) {
            accessData.remove(url);
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

	@SuppressWarnings("unchecked")
	private void scheduleCachedLinks(String url, int depth) {
		if (accessData == null) {
			LOGGER.log(Level.SEVERE,
				"Internal error: scheduling cached links for unmodified url while no AccessData is set: "
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
		InputStream content = null;

		// remove any previously registered links
		String url = object.getID().toString();
		if (accessData != null) {
			accessData.removeReferredIDs(url);
		}

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

		// fall-back to what the server returned
		if (mimeType == null) {
			mimeType = object.getMetadata().getString(DATA.mimeType);
		}
		else {
			// overrule the DataObject's MIME type: magic-number based determination is much more reliable
			// than what web servers return, especially for non-web formats
			object.getMetadata().put(DATA.mimeType, mimeType);
		}

		// bail out if MIME type determination has still not produced anything
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
		List<String> links = null;

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
			HashSet<String> scheduledLinks = new HashSet<String>(links.size());

			for (String link : links) {
				link = normalizeURL(link);
				if (link == null) {
					continue;
				}

				if (!url.equals(link) && !scheduledLinks.contains(link)) {
					if (depth >= 0) {
						schedule(link, depth, true);
						scheduledLinks.add(link);
					}

					if (accessData != null) {
						accessData.putReferredID(url, link);
					}
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

	private void removeDeprecatedRedirections() {
		// the access data may get populated with "orphaned redirections", URLs that redirect to other URLs
		// but that themselves are never used as links. This may for example happen due to session IDs in the
		// URLs. These URLs should not be removed from the access data and not be reported as removed.
		if (accessData != null) {
			Iterator<String> iterator = deprecatedUrls.iterator();
			while (iterator.hasNext()) {
				String url = iterator.next();
				if (accessData.get(url, AccessData.REDIRECTS_TO_KEY) != null) {
					accessData.remove(url, AccessData.REDIRECTS_TO_KEY);
					iterator.remove();
				}
			}
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
				crawledUrls.add(id);
				deprecatedUrls.remove(id);

				CrawlJob job = jobsMap.remove(id);
				if (job != null) {
					jobsQueue.remove(job);
				}
			}

			super.put(id, key, value);
		}
	}
}
