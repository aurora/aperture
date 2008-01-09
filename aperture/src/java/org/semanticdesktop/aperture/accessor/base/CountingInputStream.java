/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.io.InputStream;


public class CountingInputStream extends InputStream {
    private InputStream wrappedInputStream;
    
    private long currentByte;
    private long markedByte;

    public CountingInputStream(InputStream wrappedInputStream) {
        this.wrappedInputStream = wrappedInputStream;
    }
    
    public long getCurrentByte() {
        return currentByte;
    }
    
    /**
     * @return
     * @throws IOException
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return wrappedInputStream.available();
    }

    /**
     * @throws IOException
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
        wrappedInputStream.close();
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return wrappedInputStream.equals(obj);
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return wrappedInputStream.hashCode();
    }

    /**
     * @param readlimit
     * @see java.io.InputStream#mark(int)
     */
    public void mark(int readlimit) {
        wrappedInputStream.mark(readlimit);
        markedByte = currentByte;
    }

    /**
     * @return
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return wrappedInputStream.markSupported();
    }

    /**
     * @return
     * @throws IOException
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        currentByte++;
        return wrappedInputStream.read();
    }

    /**
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = wrappedInputStream.read(b, off, len);
        currentByte += bytesRead;
        return bytesRead;
    }

    /**
     * @param b
     * @return
     * @throws IOException
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        int bytesRead = wrappedInputStream.read(b); 
        currentByte += bytesRead;
        return bytesRead;
    }

    /**
     * @throws IOException
     * @see java.io.InputStream#reset()
     */
    public void reset() throws IOException {
        wrappedInputStream.reset();
        currentByte = markedByte;
    }

    /**
     * @param n
     * @return
     * @throws IOException
     * @see java.io.InputStream#skip(long)
     */
    public long skip(long n) throws IOException {
        long bytesSkipped = wrappedInputStream.skip(n);
        currentByte += bytesSkipped;
        return bytesSkipped;
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return wrappedInputStream.toString();
    }
}
