/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class that wraps an {@link InputStream} and counts the bytes read
 * from the stream to provide the {@link #getCurrentByte()} method.
 */
public class CountingInputStream extends InputStream {
    private InputStream wrappedInputStream;
    
    private long currentByte;
    private long markedByte;

    /**
     * A main constructor.
     * @param wrappedInputStream
     */
    public CountingInputStream(InputStream wrappedInputStream) {
        this.wrappedInputStream = wrappedInputStream;
    }
    
    /**
     * @return the zero-based index of the next byte to be read (or a count of the bytes that have been read
     * from this stream).
     */
    public long getCurrentByte() {
        return currentByte;
    }
    
    /**
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return wrappedInputStream.available();
    }

    /**
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        wrappedInputStream.close();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return wrappedInputStream.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return wrappedInputStream.hashCode();
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    public void mark(int readlimit) {
        wrappedInputStream.mark(readlimit);
        markedByte = currentByte;
    }

    /**
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return wrappedInputStream.markSupported();
    }

    /**
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        currentByte++;
        return wrappedInputStream.read();
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = wrappedInputStream.read(b, off, len);
        currentByte += bytesRead;
        return bytesRead;
    }

    /**
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        int bytesRead = wrappedInputStream.read(b); 
        currentByte += bytesRead;
        return bytesRead;
    }

    /**
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException {
        wrappedInputStream.reset();
        currentByte = markedByte;
    }

    /**
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        long bytesSkipped = wrappedInputStream.skip(n);
        currentByte += bytesSkipped;
        return bytesSkipped;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return wrappedInputStream.toString();
    }
}
