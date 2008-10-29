/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A trivial default implementation of FileDataObject. 
 */
public class FileDataObjectBase extends DataObjectBase implements FileDataObject {

    private CountingInputStream content;
    
    private File file;

    public FileDataObjectBase() { }
    
    public FileDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, InputStream content) {
        super(id, dataSource, metadata);
        setContent(content);
    }
    
    public FileDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, File file) {
        super(id, dataSource, metadata);
        setFile(file);
    }
    
    public void finalize() throws Throwable {
        try {
            // Just try to close the InputStream once more: can remedy nasty programming errors.
            // Not documented in the Javadoc as programmers shouldn't rely on this.
            closeContent();
        }
        finally {
            super.finalize();
        }
    }
    

    public void setContent(InputStream content) {
        if (content != null && !content.markSupported()) {
            throw new IllegalArgumentException("content should support mark and reset");
        }
        closeContent();
        this.content = new CountingInputStream(content);
        this.file = null;
    }
    
    public InputStream getContent() {
        return content;
    }
    
    public void setFile(File file) {
        if (file == null) {
            throw new NullPointerException("the file passed to a FileDataObject base cannot be null");
        } else if ( !file.exists()) {
            throw new IllegalArgumentException("File not found: " + file);
        } else if ( !file.isFile()) {
            throw new IllegalArgumentException("Not a normal file: " + file);
        } else if ( !file.canRead()) {
            throw new IllegalArgumentException("File not readable: " + file);
        }
        closeContent();
        try {
            this.file = file;
            this.content = new CountingInputStream(new BufferedInputStream(new FileInputStream(file)));
        }
        catch (FileNotFoundException e) {
            // this can't happen because we check for this case
        }
    }
    
    public File getFile() {
        return file;
    }
    
    public File downloadContent() throws IOException {
        if (content.getCurrentByte() != 0) {
            throw new IOException("The content stream hasn't been reset before calling getFile(), " +
            		"can't create a temporary file");
        } else {
            File file = File.createTempFile("aperture","tmp");
            IOUtil.writeStream(content, file);
            return file;
        }
    }
    
    /**
     * Closes the stream encapsulated by this FileDataObject. If this object contains a wrapped data object
     * (set with the {@link #setWrappedDataObject(DataObject)}) method - the wrapped data object is also
     * disposed.
     */
    public void dispose() {
        closeContent();
        super.dispose();
    }
    
    private void closeContent() {
        try {
            if (content != null) {
                content.close();
                content = null;
            }
        }
        catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("IOException while closing stream", e);
        }
    }   
}
