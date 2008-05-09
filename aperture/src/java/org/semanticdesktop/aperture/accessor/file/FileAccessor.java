/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
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
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DataAccessor implementation for the file scheme.
 * 
 * <p>
 * FileAccessor can be passed a File instance by putting a File object in the params Map with the String
 * "file" as key. It will then use this File, rather than constructing one based on the specified URL. This
 * can optimize cases where a File instance is already available.
 */
public class FileAccessor implements DataAccessor {

	public static final String FILE_KEY = "file";

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Return a DataObject for the specified url. If the specified Map contains a "file" key with a File
	 * instance as value, this File will be used to retrieve information from, else one will be created by
	 * using the specified url.
	 */
	public DataObject getDataObject(String url, DataSource source, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		return get(url, source, null, params, containerFactory);
	}

	/**
	 * Return a DataObject for the specified url. If the specified Map contains a "file" key with a File
	 * instance as value, this File will be used to retrieve information from, else one will be created by
	 * using the specified url.
	 */
	public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		return get(url, source, accessData, params, containerFactory);
	}

	private DataObject get(String url, DataSource source, AccessData accessData, Map params,
			RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		// sanity check: make sure we're processing file urls
		if (!url.startsWith("file:")) {
			throw new IllegalArgumentException("non-file scheme: " + url);
		}

		// get the File instance
		File file = getFile(url, params);

		// make sure the physical resource exists
		if (!file.exists()) {
			throw new UrlNotFoundException(url);
		}

		// determine what kind of File it is
		boolean isFile = file.isFile();
		boolean isFolder = file.isDirectory();
		
		// Removed this functionality after Christiaan Fluit complained about it
		// TODO Return here after resolving the addParent issue
		//boolean addParent = 
		//    params == null || 
		//    params.get("addParent") == null || 
		//    params.get("addParent").equals(Boolean.TRUE);
		if (!isFile && !isFolder) {
			// we can still handle this by using a plain DataObjectBase but log it anyway
			logger.warn("not a file nor a folder: " + file);
		}

		// Check whether the file has been modified, if required. Note: this assumes that a folder gets a
		// different last modified date when Files are added to or removed from it. This seems reasonable
		// and is the case on Windows, but the API isn't clear about it.
		if (accessData != null) {
			// determine when the file was last modified
			long lastModified = file.lastModified();

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
					logger.error("illegal long: " + value, e);
				}
			}

			// It has been modified; register the new modification date.
			accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));
		}

		// create the metadata
		URI id = toURI(file);
		
		// the default behavior is to add the references to children to each
		// folder data object, this may be overridden by 
		boolean addFolderChildren = true;
		if (params != null && params.get("suppressParentChildLinks") != null
                && params.get("suppressParentChildLinks").equals(Boolean.TRUE)) {
            addFolderChildren = false;
        }
		
		// TODO Return here after resolving the addParent issue
		//RDFContainer metadata = createMetadata(file, id, isFile, isFolder, addParent, containerFactory);
		RDFContainer metadata = createMetadata(file, id, isFile, isFolder, addFolderChildren, containerFactory);
		
		// create the DataObject
		DataObject result = null;

		if (isFile) {
			//InputStream contentStream = null;
			if (file.canRead()) {
			    // 2.12.2007 - commented this line out, so that the file accessor returns
			    // FileDataObjects backed by real files
				//contentStream = new BufferedInputStream(new FileInputStream(file));
			    result = new FileDataObjectBase(id, source, metadata, file);
			}
			// Add type info. (type, File) cannot be added in the FileDataObject class itself because it is
			// also used for things like email Messages with content, that are strictly speaking not files.
			result.getMetadata().add(RDF.type, NFO.FileDataObject);
		}
		else if (isFolder) {
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

	private File getFile(String url, Map params) throws IOException {
		// first try to fetch it from the params map
		if (params != null) {
			Object value = params.get(FILE_KEY);
			if (value instanceof File) {
				return (File) value;
			}
		}

		// create one based on the url and use java.net.URI because File has a convenient constructor
		try {
			java.net.URI id = new java.net.URI(url);
			return new File(id);
		}
		catch (URISyntaxException e) {
			IOException ioe = new IOException("invalid url");
			ioe.initCause(e);
			throw ioe;
		}
	}

	private RDFContainer createMetadata(File file, URI id, boolean isFile, boolean isFolder, 
			boolean addFolderChildren, RDFContainerFactory containerFactory) {
		// get the RDFContainer instance
		RDFContainer metadata = containerFactory.getRDFContainer(id);
		
		metadata.add(RDF.type,NFO.FileDataObject);
		
		// create regular File metadata first
		long lastModified = file.lastModified();
		if (lastModified != 0l) {
			metadata.add(NFO.fileLastModified, new Date(lastModified));
		}

		String name = file.getName();
		if (name != null) {
			metadata.add(NFO.fileName, name);
		}

		File parent = file.getParentFile();
		// TODO Return here after resolving the addParent issue
		//if (parent != null && addParent) {
		if (parent != null) {
			metadata.add(NFO.belongsToContainer, toURI(parent));
            metadata.add(metadata.getModel().createStatement(toURI(parent), RDF.type, NFO.Folder));
		}

		// add file-specific metadata
		if (isFile) {
			long length = file.length();
            // NOTE: The bytesize of 0 is an important information we should generate.
            // When people search for files with size 0, they should be able to do so.
            // Therefore LeoSauermann changed this code to return 0 on 27.06.2007
			//if (length != 0l) {
			    metadata.add(NFO.fileSize, length);
			//}
		}

		// add folder-specific metadata
		else if (isFolder && addFolderChildren) {
		    
		    final RDFContainer finalMetadata = metadata;
		    file.listFiles(new FileFilter() {
                public boolean accept(File child) {
                    if (child != null) {
                        finalMetadata.add(NIE.hasPart, toURI(child));
                        finalMetadata.add(finalMetadata.getModel().createStatement(toURI(child), RDF.type, NFO.FileDataObject));
                    }
                    return false;
                }});
		    
			// note: this array is null for certain types of directories, see Java bug #4803836
			//File[] children = file.listFiles();
			//if (children != null) {
			//	for (int i = 0; i < children.length; i++) {
			//		File child = children[i];
			//		if (child != null) {
            //           metadata.add(NIE.hasPart, toURI(child));
            //            metadata.add(metadata.getModel().createStatement(toURI(child), RDF.type, NFO.FileDataObject));
			//		}
			//	}
			//}
		}

		return metadata;
	}

	private URI toURI(File file) {
		// file.toURI is costly (some parsing and construction stuff going on in both File and URI, but
		// it does make sure we have legal URIs so do it anyway. We could also do "file:///" +
		// file.getAbsolutePath() or something similar, which may be cheaper, but I'm not sure whether we
		// can expect any issues there
		return URIImpl.createURIWithoutChecking(file.toURI().toString());
	}
}
