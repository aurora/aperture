/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.access.file;

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
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.access.AccessData;
import org.semanticdesktop.aperture.access.DataAccessor;
import org.semanticdesktop.aperture.access.UrlNotFoundException;
import org.semanticdesktop.aperture.datasource.DataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.datasource.base.BinaryObjectBase;
import org.semanticdesktop.aperture.datasource.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.base.FolderBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.RDFContainerFactory;

/**
 * A DataAccessor implementation for the file scheme.
 */
public class FileAccessor implements DataAccessor {

    private static final Logger LOGGER = Logger.getLogger(FileAccessor.class.getName());

    private RDFContainerFactory containerFactory;

    public FileAccessor(RDFContainerFactory containerFactory) {
        if (containerFactory == null) {
            throw new IllegalArgumentException("containerFactory should not be null");
        }
        this.containerFactory = containerFactory;
    }

    /**
     * Return a DataObject for the specified url. If the specified Map contains a "file" key with a File
     * instance as value, this File will be used to retrieve information from, else one will be created
     * by using the specified url.
     */
    public DataObject get(String url, DataSource source, AccessData accessData, Map params)
            throws UrlNotFoundException, IOException {
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

            // It has been modified; register the new modification date. Note: we store them under the
            // specified url, not the uri of the canonical path: quicker for incremental rescans and this
            // is what we were asked for to retrieve: important when other people access the AccessData
            // as well
            accessData.put(url, AccessData.DATE_KEY, String.valueOf(lastModified));
        }

        // create the metadata
        URI id = toURI(file);
        RDFContainer metadata = constructMetadata(file, id, isFile, isFolder);

        // create the DataObject
        DataObject result = null;

        if (isFile) {
            InputStream contentStream = null;
            if (file.canRead()) {
                contentStream = new BufferedInputStream(new FileInputStream(file));
            }

            result = new BinaryObjectBase(id, source, metadata, contentStream);
        }
        else if (isFolder) {
            result = new FolderBase(id, source, metadata);
        }
        else {
            result = new DataObjectBase(id, source, metadata);
        }

        // done!
        return result;
    }

    private File getFile(String url, Map params) throws IOException {
        // first try to fetch it from the params map
        if (params != null) {
            Object value = params.get("file");
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

    private RDFContainer constructMetadata(File file, URI id, boolean isFile, boolean isFolder) {
        // create a new, empty RDFContainer
        RDFContainer metadata = containerFactory.newInstance(id);

        // populate it with "regular" metadata first
        long lastModified = file.lastModified();
        if (lastModified != 0l) {
            metadata.put(Vocabulary.DATE_URI, new Date(lastModified));
        }

        String name = file.getName();
        if (name != null) {
            metadata.put(Vocabulary.NAME_URI, name);
        }

        File parent = file.getParentFile();
        if (parent != null) {
            metadata.put(Vocabulary.PART_OF_URI, toURI(parent));
        }

        // add file-specific metadata
        if (isFile) {
            long length = file.length();
            if (length != 0l) {
                metadata.put(Vocabulary.BYTE_SIZE_URI, length);
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
                        metadata.add(toURI(child), Vocabulary.PART_OF_URI, id);
                    }
                }
            }
        }

        // done!
        return metadata;
    }

    private URI toURI(File file) {
        // file.toURI is costly (some parsing and construction stuff going on in both File and URI, but
        // it does make sure we have legal URIs so do it anyway. We could also do "file:///" +
        // file.getAbsolutePath() or something similar, which may be cheaper, but I'm not sure whether we
        // can expect any issues there
        return new URIImpl(file.toURI().toString());
    }
}
