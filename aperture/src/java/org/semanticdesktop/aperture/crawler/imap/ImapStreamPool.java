/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper of all InputStreams referring to messages in a single store.
 * 
 * The purpose of this class is to provide safe means to close an IMAP store. Invoking store.close()
 * invalidates all the input streams in the FileDataObjects that represent messages crawled from this store.
 */
class ImapStreamPool {
    
    private Logger logger = LoggerFactory.getLogger(ImapCrawler.class);
    private boolean closeRequested;
    private Store store;
    private Set<InputStream> streamSet;
    
    public ImapStreamPool(Store store) {
        this.store = store;
        closeRequested = false;
        streamSet = new HashSet<InputStream>();
    }
    
    /**
     * Returns an InputStream for the given message. It's a safe wrapper for the
     * {@link Message#getInputStream()}. It is supposed to ensure that the connection to the mail store will
     * be closed only when all streams have been closed. <br/><br/>
     * 
     * There is no way to check if the given part actually comes from the store wrapped by this stream
     * pool. That's why this class has package access. 
     * 
     * @param message
     * @return
     * @throws MessagingException
     */
    public synchronized InputStream getStreamForAMessage(Part part) throws MessagingException, IOException {
        if (!store.isConnected()) {
            // this should work. I've checked out the source code of the javax.mail.Service class
            // and it seems that all the settings are preserved, so we can use the 0-argument
            // connect() method
            store.connect();
            closeRequested = false;
        }
        InputStream result = new ImapInputStream(part.getInputStream(),this);
        this.streamSet.add(result);
        return result;
    }
    
    public synchronized void requestClose() {
        this.closeRequested = true;
        if (streamSet.isEmpty()) {
            closeStore();
        }
    }
    
    private synchronized void notifyStreamClosed(InputStream stream) {
        if (streamSet.contains(stream)) {
            streamSet.remove(stream);
            if (closeRequested) {
                closeStore();
            }
        } else {
            logger.warn("Trying to return a stream that doesn't belong here");
        }
    }
    
    private void closeStore() {
        try {
            store.close();
        }
        catch (MessagingException e) {
            logger.warn("Couldn't close the IMAP store",e);
        }
    }
    
    private static class ImapInputStream extends InputStream {
        
        private InputStream stream;
        private ImapStreamPool pool;
        
        public ImapInputStream(InputStream stream, ImapStreamPool pool) {
            this.pool = pool;
            this.stream = stream;
        }

        /**
         * @return
         * @throws IOException
         * @see java.io.InputStream#available()
         */
        public int available() throws IOException {
            return stream.available();
        }

        /**
         * @throws IOException
         * @see java.io.InputStream#close()
         */
        public void close() throws IOException {
            // note that we first notify the pool and then close the stream
            // if close() throws an exception, the pool will still be notified
            pool.notifyStreamClosed(this);
            stream.close();
        }

        /**
         * @param obj
         * @return
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            return stream.equals(obj);
        }

        /**
         * @return
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return stream.hashCode();
        }

        /**
         * @param readlimit
         * @see java.io.InputStream#mark(int)
         */
        public void mark(int readlimit) {
            stream.mark(readlimit);
        }

        /**
         * @return
         * @see java.io.InputStream#markSupported()
         */
        public boolean markSupported() {
            return stream.markSupported();
        }

        /**
         * @return
         * @throws IOException
         * @see java.io.InputStream#read()
         */
        public int read() throws IOException {
            return stream.read();
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
            return stream.read(b, off, len);
        }

        /**
         * @param b
         * @return
         * @throws IOException
         * @see java.io.InputStream#read(byte[])
         */
        public int read(byte[] b) throws IOException {
            return stream.read(b);
        }

        /**
         * @throws IOException
         * @see java.io.InputStream#reset()
         */
        public void reset() throws IOException {
            stream.reset();
        }

        /**
         * @param n
         * @return
         * @throws IOException
         * @see java.io.InputStream#skip(long)
         */
        public long skip(long n) throws IOException {
            return stream.skip(n);
        }

        /**
         * @return
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return stream.toString();
        }
    }
}

