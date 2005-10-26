/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.access;

import java.io.IOException;
import java.io.InputStream;

/**
 * A general interface for data objects that have some Stream-based file
 * content. These objects may be files on filesystems, web files received
 * through http or emails stored in an email server. All methods of DataObject
 * are inherited. Additional is the <b>binary content</b> and the handling of
 * the stream. For the extraction, both the InputStream returned by getContent()
 * and the RDF metadata returned by getMetadata() are important.
 */
public interface DataObjectFile extends DataObject {

    /**
     * Returns the byte size of the represented resource. This has been defined
     * at this global level due to the importance of this attribute for
     * performance reasons.
     * 
     * @return the size of the binary resource in bytes, or a negative value
     *         when the size is unknown or does not make sense for this
     *         particular DataObject implementation.
     */
    public long getSize();

    /**
     * Gets an InputStream containing the content represented by the DataObject.
     * The returned InputStream is required to support marking (markSupported()
     * returns true). Calling this method multiple times may references to
     * one-and-the-same InputStream instance. Care should therefore be taken to
     * mark and reset the stream when the stream's content is to be read again
     * later.
     * 
     * @return An InputStream from which the content of the data object can be
     *         read.
     * @throws IOException
     *             If an I/O error occurred.
     */
    public InputStream getContent() throws IOException;

    /**
     * Instructs the DataObject that its content stream will most likely be used
     * multiple times in its entirety, making the mark-and-reset procedure
     * difficult to work, and that it better should cache the entire contents.
     * 
     * @throws IOException
     *             when an IOException occured during caching of the content.
     */
    public void cacheContent() throws IOException;

    /**
     * what is the mime-type of the content, if there is content? This is set by
     * the DataAccessor. This method may cause complicated mimetype detection,
     * like looking at the http mime-type, file extensions, magic bytes inside
     * the file-stream.
     * 
     * @return a mimetype identifier like "text/plain" or null if the identifier
     *         cannot be determined, even using all tricks available.
     */
    public String getContentMimeType();

    /**
     * what is the character-encoding (using ansi identifiers like "UTF-8" or
     * "ISO-8859-1") of the content, if there is content. Will return null if
     * not known or if content is null. This is set by the DataAccessor
     * 
     * @return null or a encoding identifier like "UTF-8"
     */
    public String getContentEncoding();
}

/*
 * $Log$
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */