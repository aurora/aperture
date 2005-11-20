/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A DataAccessor implementation for the file scheme.
 * 
 * <p>
 * FileAccessor can be passed a File instance by putting a File object in the params Map with the String
 * "file" as key. It will then use this File, rather than constructing one based on the specified URL.
 * This can optimize cases where a File instance is already available.
 */
public class FileAccessor implements DataAccessor {

    public static final String FILE_KEY = "file";

    private static final Logger LOGGER = Logger.getLogger(FileAccessor.class.getName());

    /**
     * Return a DataObject for the specified url. If the specified Map contains a "file" key with a File
     * instance as value, this File will be used to retrieve information from, else one will be created
     * by using the specified url.
     */
    public DataObject getDataObject(String url, DataSource source, Map params, RDFContainer metadataContainer)
            throws UrlNotFoundException, IOException {
        return get(url, source, null, params, metadataContainer);
    }

    /**
     * Return a DataObject for the specified url. If the specified Map contains a "file" key with a File
     * instance as value, this File will be used to retrieve information from, else one will be created
     * by using the specified url.
     */
    public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
            Map params, RDFContainer metadataContainer) throws UrlNotFoundException, IOException {
        return get(url, source, accessData, params, metadataContainer);
    }

    private DataObject get(String url, DataSource source, AccessData accessData, Map params,
            RDFContainer metadataContainer) throws UrlNotFoundException, IOException {
        // sanity check: make sure we're processing file urls
        if (!url.startsWith("file:")) {
            throw new IllegalArgumentException("non-file scheme: " + url);
        }

        // get the File instance
        File file = getFile(url, params);
        file = file.getCanonicalFile();

        // make sure the physical resource exists
        if (!file.exists()) {
            throw new UrlNotFoundException(url);
        }

        // determine what kind of File it is
        boolean isFile = file.isFile();
        boolean isFolder = file.isDirectory();
        if (!isFile && !isFolder) {
            // we can still handle this by using a plain DataObjectBase but log it anyway
            LOGGER.warning("not a file nor a folder: " + file);
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
                    LOGGER.log(Level.WARNING, "illegal long: " + value, e);
                }
            }

            // It has been modified; register the new modification date.
            accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));
        }

        // create the metadata
        URI id = toURI(file);
        putMetadata(file, id, isFile, isFolder, metadataContainer);

        // create the DataObject
        DataObject result = null;

        if (isFile) {
            InputStream contentStream = null;
            if (file.canRead()) {
                contentStream = new BufferedInputStream(new FileInputStream(file));
            }

            result = new FileDataObjectBase(id, source, contentStream);
        }
        else if (isFolder) {
            result = new FolderDataObjectBase(id, source);
        }
        else {
            result = new DataObjectBase(id, source);
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

    private void putMetadata(File file, URI id, boolean isFile, boolean isFolder,
            RDFContainer metadataContainer) {
        // update the described URI
        metadataContainer.setDescribedUri(id);
        
        // create regular File metadata first
        long lastModified = file.lastModified();
        if (lastModified != 0l) {
            metadataContainer.put(Vocabulary.DATE, new Date(lastModified));
        }

        String name = file.getName();
        if (name != null) {
            metadataContainer.put(Vocabulary.NAME, name);
        }

        File parent = file.getParentFile();
        if (parent != null) {
            metadataContainer.put(Vocabulary.PART_OF, toURI(parent));
        }

        // add file-specific metadata
        if (isFile) {
            long length = file.length();
            if (length != 0l) {
                metadataContainer.put(Vocabulary.BYTE_SIZE, length);
            }
        }

        // add folder-specific metadata
        else if (isFolder) {
            // note: this array is null for certain types of directories, see Java bug #4803836
            File[] children = file.listFiles();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    File child = children[i];
                    if (child != null) {
                        metadataContainer.add(new StatementImpl(toURI(child), Vocabulary.PART_OF, id));
                    }
                }
            }
        }
    }

    private URI toURI(File file) {
        // file.toURI is costly (some parsing and construction stuff going on in both File and URI, but
        // it does make sure we have legal URIs so do it anyway. We could also do "file:///" +
        // file.getAbsolutePath() or something similar, which may be cheaper, but I'm not sure whether we
        // can expect any issues there
        return new URIImpl(file.toURI().toString());
    }
}
