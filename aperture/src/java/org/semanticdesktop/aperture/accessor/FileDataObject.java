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
     * The returned InputStream is required to support marking (markSupported() must return true). Calling
     * this method multiple times may return references to one-and-the-same InputStream instance. Care should
     * therefore be taken to mark and reset the stream when the stream's content is to be read again later.
     * 
     * <p>
     * The returned InputStream is null in cases where the physical resource could not be accessed, e.g. in
     * case of missing file read permissions.
     * 
     * @return An InputStream from which the content of the data object can be read, or null when the stream
     *         cannot be accessed.
     * @throws IOException If an I/O error occurred.
     */
    public InputStream getContent() throws IOException;

    /**
     * Sets the InputStream containing the content represented by the DataObject. The specified InputStream is
     * required to support marking (markSupported() must return true).
     * 
     * @param stream The InputStream containing the resource's content.
     */
    public void setContent(InputStream stream);

    /**
     * Returns an instance of java.io.File representing the content of this FileDataObject. If no such file
     * instance is available null is returned. <br/><br/>
     * 
     * If you need a {@link File} instance (e.g. to pass it to a FileExtractor) and this method returns null,
     * you can use the {@link #downloadContent} method. If the {@link #downloadContent()} method doesn't meet
     * your needs, you can use the stream returned by {@link #getContent()} and implement the download process
     * by yourself. Note that the {@link IOUtil#writeStream(InputStream, File)} method may be useful for this.
     * 
     * @return a File representing the content of this FileDataObject or null if no such File is available
     */
    public File getFile();

    /**
     * Downloads the entire content of the stream to a temporary file (created with
     * {@link File#createTempFile()}). It is the responsibility of the user to delete the file if it is no
     * longer needed. <br/><br/>
     * 
     * <b>This method works only if the content stream hasn't been used or if it has been reset prior to
     * calling this method. Otherwise the downloaded file would be corrupted. Aperture tries to check for this
     * kind of error, but nevertheless this method should be used with care.</b><br/><br/>
     * 
     * Note that the content stream may become unusable after a call to this method.<br/><br/>
     * 
     * This method will always create a temporary file, including cases when the {@link #getFile()} method
     * returns a non-null value.
     * 
     * @return the {@link File} instance for the temporary file where the content of the stream has been
     *         downloaded.
     * @throws IOException If the content stream is not at the beginning of the file, or if an I/O error
     *             occured during the download process.
     */
    public File downloadContent() throws IOException;

    /**
     * Sets the file containing the content represented by this FileDataObject.
     * 
     * @param file the file with the content of this FileDataObject
     */
    public void setFile(File file);
}
