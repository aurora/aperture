package org.semanticdesktop.aperture.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * A general interface for data objects that have some binary, stream-based content. Examples are files
 * or web pages.
 */
public interface BinaryObject extends DataObject {

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
}
