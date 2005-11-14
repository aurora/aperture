package org.semanticdesktop.aperture.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * A general interface for data objects that have some binary, stream-based content. Examples are files
 * or web pages.
 */
public interface BinaryObject extends DataObject {

    /**
     * Returns the byte size of the represented resource. This has been defined at this global level due
     * to the importance of this attribute for performance reasons.
     * 
     * @return the size of the binary resource in bytes, or a negative value when the size is unknown.
     */
    public long getSize();

    /**
     * Gets an InputStream containing the content represented by the DataObject.
     * 
     * <p>
     * The returned InputStream is required to support marking (markSupported() must return true).
     * Calling this method multiple times may return references to one-and-the-same InputStream instance.
     * Care should therefore be taken to mark and reset the stream when the stream's content is to be
     * read again later.
     * 
     * @return An InputStream from which the content of the data object can be read.
     * @throws IOException If an I/O error occurred.
     */
    public InputStream getContent() throws IOException;
}
