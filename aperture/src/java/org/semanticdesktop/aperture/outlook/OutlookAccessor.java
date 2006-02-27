/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;

import com.jacob.com.Dispatch;


/**
 * Access individual Outlook Objects.
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookAccessor implements DataAccessor {
	
    protected static Logger log = Logger.getLogger(OutlookAccessor.class.getName());
	
	// params in the map
	/**
	 * for passing in the already instantiated outlook resource object.
	 * type: OutlookResource
	 */
	public static final String PARAM_OUTLOOKRESOURCE = "ol_resource";
	
	/**
	 * the parent of the current node.
	 * type: OutlookResource
	 */
	public static final String PARAM_OUTLOOKPARENT = "ol_parent";
	
	/**
	 * for passing in the already instantiated mapi
	 */
	public static final String PARAM_MAPI = "ol_mapi";
	
	

	/**
	 * 
	 */
	public OutlookAccessor() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObject(java.lang.String, org.semanticdesktop.aperture.datasource.DataSource, java.util.Map, org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObject(String url, DataSource source, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObjectIfModified(java.lang.String, org.semanticdesktop.aperture.datasource.DataSource, org.semanticdesktop.aperture.accessor.AccessData, java.util.Map, org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		OutlookResource res = (OutlookResource)params.get(PARAM_OUTLOOKRESOURCE);
		if (res == null)
			throw new UrlNotFoundException("cannot crawl "+url+" you have to pass the OutlookResource as param");
		OutlookResource parent = (OutlookResource)params.get(PARAM_OUTLOOKPARENT);
		return getDataObjectIfModifiedOutlook(url, source, accessData, params, containerFactory, res, parent);
	}
	
	public DataObject getDataObjectIfModifiedOutlook(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory, OutlookResource resource, OutlookResource parent) throws UrlNotFoundException, IOException {
		log.finest("get data of "+url);
		
		
		// check date
		if (accessData != null)
		{
            // determine when the OutlookResource was last modified
            long lastModified = resource.getLastModified();

            // check whether it has been modified
            String value = accessData.get(url, AccessData.DATE_KEY);
            if (value != null) {
                try {
                    long registeredDate = Long.parseLong(value);

                    // now that we now its previous last modified date, see if it has been modified
                    if (registeredDate == lastModified) {
                        // the file has not been modified
                        return null;
                    }
                }
                catch (NumberFormatException e) {
                    log.log(Level.WARNING, "illegal long: " + value, e);
                }
            }

            // It has been modified; register the new modification date.
            accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));

		}
		
        // create the metadata
        URI id = new URIImpl(url);
        RDFContainer metadata = containerFactory.getRDFContainer(id);
        
        // basic info
        metadata.add(RDF.TYPE, resource.getType());
        if (parent != null)
        	metadata.add(DATA.partOf, new URIImpl(parent.getUri()));
        
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
            // This is also rdf  typed to being a dataobjectbase.
        }
        

        // done!
        return result;

	}

}

