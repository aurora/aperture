/*
 * Copyright (c) 2005 - 2007 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access individual Outlook Objects.
 * 
 * @author sauermann $Id$
 */
public class OutlookAccessor implements DataAccessor {

	// params in the map
	/**
	 * for passing in the already instantiated outlook resource object. type: OutlookResource
	 */
	public static final String PARAM_OUTLOOKRESOURCE = "ol_resource";

	/**
	 * the parent of the current node. type: OutlookResource
	 */
	public static final String PARAM_OUTLOOKPARENT = "ol_parent";

	/**
	 * for passing in the already instantiated mapi
	 */
	public static final String PARAM_MAPI = "ol_mapi";

    private Logger logger = LoggerFactory.getLogger(getClass());
    
	/**
	 * 
	 */
	public OutlookAccessor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObject(java.lang.String,
	 *      org.semanticdesktop.aperture.datasource.DataSource, java.util.Map,
	 *      org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObject(String url, DataSource source, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		OutlookResource res = null;
		OutlookResource parent = null;
		if (params != null) {
			res = (OutlookResource) params.get(PARAM_OUTLOOKRESOURCE);
			parent = (OutlookResource) params.get(PARAM_OUTLOOKPARENT);
		}

		return getDataObjectIfModifiedOutlook(url, source, null, params, containerFactory, res, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObjectIfModified(java.lang.String,
	 *      org.semanticdesktop.aperture.datasource.DataSource,
	 *      org.semanticdesktop.aperture.accessor.AccessData, java.util.Map,
	 *      org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {

		OutlookResource res = null;
		OutlookResource parent = null;
		if (params != null) {
			res = (OutlookResource) params.get(PARAM_OUTLOOKRESOURCE);
			parent = (OutlookResource) params.get(PARAM_OUTLOOKPARENT);
		}

		return getDataObjectIfModifiedOutlook(url, source, accessData, params, containerFactory, res, parent);
	}

	public DataObject getDataObjectIfModifiedOutlook(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory, OutlookResource resource, OutlookResource parent)
			throws UrlNotFoundException, IOException {
		logger.info("get data of " + url);

		// check if I have to crawl this first
		OutlookCrawler crawler = null;
		try {
			if (resource == null) {
				crawler = new OutlookCrawler();
				crawler.setDataSource(source);
				crawler.beginCall();
				resource = OutlookResource.createWrapperFor(crawler, url, logger);
				if (resource == null)
					throw new UrlNotFoundException(url, "cannot crawl " + url + ", not found in Outlook.");
			}

			// check date
			if (accessData != null) {
				// determine when the OutlookResource was last modified
				long lastModified = resource.getLastModified();

				// check whether it has been modified
				String value = accessData.get(url, AccessData.DATE_KEY);
				if (value != null) {
					try {
						long registeredDate = Long.parseLong(value);

						// now that we now its previous last modified date, see if it has been modified
						if (registeredDate == lastModified) {
							logger.info(url + " not modified - reg:" + registeredDate);
							// the file has not been modified
							return null;
						}
						else
							logger
									.info(url + " was modified - reg:" + registeredDate + " new:"
											+ lastModified);

					}
					catch (NumberFormatException e) {
						logger.warn("illegal long: " + value, e);
					}
				}

				// It has been modified; register the new modification date.
				accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));

			}

			// create the metadata
			URI id = URIImpl.createURIWithoutChecking(url);
			RDFContainer metadata = containerFactory.getRDFContainer(id);

			// basic info
			metadata.add(RDF.type, resource.getType());
			if (parent != null)
				metadata.add(DATA.partOf, URIImpl.createURIWithoutChecking(parent.getUri()));

			// get the details
			resource.addData(metadata);

			// create the DataObject
			DataObject result = null;

			if (resource.isFolder()) {
				result = new FolderDataObjectBase(id, source, metadata);
				// RDF typing is added in the folderdata object itself.
			}
			else {
				result = new DataObjectBase(id, source, metadata);
				// This is also rdf typed to being a dataobjectbase.
			}

			// done!
			return result;

		}
		finally {
			if (crawler != null) {
				crawler.endCall();
				crawler.release();
			}
		}
	}

}
