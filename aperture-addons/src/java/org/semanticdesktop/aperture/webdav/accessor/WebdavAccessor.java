package org.semanticdesktop.aperture.webdav.accessor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.webdav.lib.WebdavResource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 *
 * Licensed under the Open Software License version 3.0..
 */

/**
 * @author Patrick Ernst
 * 
 * The Class WebdavAccessor is used for accessing a webdav resource.
 */
public class WebdavAccessor implements DataAccessor {

	/** The Constant WEBDAVFILE_KEY. */
	public static final String WEBDAVFILE_KEY = "webdavFile";
	
	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObject(java.lang.String, org.semanticdesktop.aperture.datasource.DataSource, java.util.Map, org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObject(String url, DataSource source, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		return get(url, source, null, params, containerFactory);
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObjectIfModified(java.lang.String, org.semanticdesktop.aperture.datasource.DataSource, org.semanticdesktop.aperture.accessor.AccessData, java.util.Map, org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObjectIfModified(String url, DataSource source,
			AccessData accessData, Map params, RDFContainerFactory containerFactory)
			throws UrlNotFoundException, IOException {
		return get(url, source, accessData, params, containerFactory);
	}
	
	/**
	 * Gets the DataObject for a WebDavResource
	 * 
	 * @param url the weburl of the file
	 * @param source the DataSource Object
	 * @param accessData the access data like lastmodified date
	 * @param params HashMap, that contains the webdavresource object
	 * @param containerFactory the container factory
	 * 
	 * @return the DataObject
	 * 
	 * @throws UrlNotFoundException the url not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked") // to allow for an unchecked Map
    private DataObject get(String url, DataSource source, AccessData accessData, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		
		if(params.get(WEBDAVFILE_KEY) == null){
			throw new IllegalArgumentException("non-webdav scheme: " + url);
		}
		
		if(!(params.get(WEBDAVFILE_KEY) instanceof WebdavResource)){
			throw new IllegalArgumentException("non-webdav resource: " + url);
		}
		
		WebdavResource wdr = (WebdavResource) params.get(WEBDAVFILE_KEY);
		
		if(accessData != null){
			long lastModified = wdr.getGetLastModified();
			
			//check if it has been modified
			String value = accessData.get(url, AccessData.DATE_KEY);
			
			if(value != null){
				try{
					long registeredDate = Long.parseLong(value);
					
					if(registeredDate == lastModified){
						return null;
					}
				} catch(NumberFormatException nfe){
					logger.error("illegal long: " + value, nfe);
				}
			}
			
			accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));
			

		}
		
		URI id = URIImpl.createURIWithoutChecking(wdr.getHttpURL().getURI());
		
		RDFContainer metadata = createMetadata(wdr, id, !wdr.isCollection(), wdr.isCollection(), containerFactory);
		
		// create the DataObject
		DataObject result = null;

		if (!wdr.isCollection()) {
			//InputStream contentStream = null;
			if (!wdr.isLocked()) {
				InputStream contentStream = new BufferedInputStream(wdr.getMethodData());
			    result = new FileDataObjectBase(id, source, metadata, contentStream);
			}
			// Add type info. (type, File) cannot be added in the FileDataObject class itself because it is
			// also used for things like email Messages with content, that are strictly speaking not files.
			result.getMetadata().add(RDF.type, NFO.FileDataObject);
		}
		else if (wdr.isCollection()) {
			result = new FolderDataObjectBase(id, source, metadata);
			// RDF typing is added in FolderDataObjectBase itself
		}
		else {
			result = new DataObjectBase(id, source, metadata);
			// RDF typing is added in DataObjectBase itself
		}

		// done!
		return result;
	}
	
	/**
	 * Creates the metadata.
	 * 
	 * @param wdr the wdr
	 * @param id the id
	 * @param isFile the is file
	 * @param isFolder the is folder
	 * @param containerFactory the container factory
	 * 
	 * @return the rDF container
	 */
	private RDFContainer createMetadata(WebdavResource wdr, URI id, boolean isFile, boolean isFolder, RDFContainerFactory containerFactory)
	{
		// get the RDFContainer instance
		RDFContainer metadata = containerFactory.getRDFContainer(id);
		
		metadata.add(RDF.type, NFO.RemoteDataObject);
		long lastModified = wdr.getGetLastModified();
		if (lastModified != 0l) {
			metadata.add(NFO.fileLastModified, new Date(lastModified));
		}
		
		String name = wdr.getName();
		if (name != null) {
			metadata.add(NFO.fileName, name);
		}
		
		// add file-specific metadata
		if (isFile) {
			long length = wdr.getGetContentLength();
            // NOTE: The bytesize of 0 is an important information we should generate.
            // When people search for files with size 0, they should be able to do so.
            // Therefore LeoSauermann changed this code to return 0 on 27.06.2007
			//if (length != 0l) {
			    metadata.add(NFO.fileSize, length);
			//}
		}
		return metadata;
	}
}
