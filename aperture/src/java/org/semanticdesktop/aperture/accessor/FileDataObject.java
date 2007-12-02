/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.semanticdesktop.aperture.util.IOUtil;

/**
 * A general interface for DataObjects that have File-like semantics. Examples are files or web pages.
 */
public interface FileDataObject extends DataObject {

    /**
     * Gets an InputStream containing the content represented by the DataObject.
     * 
     * <p>
     * The returned InputStream is required to support marking (markSupported() must return true).
     * Calling this method multiple times may return references to one-and-the-same InputStream instance.
     * Care should therefore be taken to mark and reset the stream when the stream's content is to be
     * read again later.
     * 
     * <p>
     * The returned InputStream is null in cases where the physical resource could not be accessed, e.g.
     * in case of missing file read permissions.
     * 
     * @return An InputStream from which the content of the data object can be read, or null when the
     *         stream cannot be accessed.
     * @throws IOException If an I/O error occurred.
     */
    public InputStream getContent() throws IOException;
    
    /**
     * Sets the InputStream containing the content represented by the DataObject. The specified
     * InputStream is required to support marking (markSupported() must return true).
     * 
     * @param stream The InputStream containing the resource's content.
     */
    public void setContent(InputStream stream);

    /**
     * Returns an instance of java.io.File representing the content of this FileDataObject. If no such file
     * instance is available null is returned. If the user needs a File (e.g. to pass it to a FileExtractor)\
     * and this method returns null, he or she is advised to use the input stream returned by
     * {@link #getContent()} to create a temporary copy (e.g. with the
     * {@link IOUtil#writeStream(InputStream, File)} method.
     * 
     * @return a File representing the content of this FileDataObject or null if no such File is available
     */
    public File getFile();
    
    /**
     * Sets the file containing the content represented by this FileDataObject.
     * @param file the file with the content of this FileDataObject
     */
    public void setFile(File file);
}
