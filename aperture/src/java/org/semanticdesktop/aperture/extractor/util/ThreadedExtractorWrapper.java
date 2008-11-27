/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A ThreadedExtractorWrapper wraps an Extractor and executes it on a separate thread, bailing out if the
 * wrapped Extractor appears to be hanging. The heuristic for determining whether the Extractor is hanging is
 * by looking at whether the InputStream is regularly accessed. Any Exceptions thrown by the wrapped Extractor
 * are eventually thrown by the ThreadedExtractorWrapper.
 * 
 * <p>
 * Furthermore, a ThreadedExtractorWrapper can be requested to stop processing, causing it to throw an
 * IOException on the InputStream the next time it is accessed by the wrapped Extractor. This allows for
 * interrupting an extraction process upon user request, for example because it has been processing a single
 * file for a very long time (especially large PDF documents are notorious). This implementation strategy is
 * preferred over interrupting the Thread as that should only be used as a last resort to stop a thread.
 */
public class ThreadedExtractorWrapper implements Extractor {

    /**
     * The maximum time per MB of data that the wrapped Extractor is allowed to work on the read data before
     * it is considered to be hanging, in milliseconds.
     */
    public static final long DEFAULT_MAX_PROCESSING_TIME_PER_MB = 10L * 1000L; // 10 seconds

    /**
     * The minimum maximum processing time that the wrapped Extractor is allowed to work on the read data
     * before it is considered to be hanging. This minimum gives a lower bound for very small files.
     */
    public static final long DEFAULT_MINIMUM_MAX_PROCESSING_TIME = 30L * 1000L; // 30 seconds

    /**
     * The maximum time between two reads that the wrapped Extractor is allowed to work on the read data
     * before it is considered to be hanging.
     */
    public static final long DEFAULT_MAX_IDLE_READ_TIME = 30L * 1000L; // 30 seconds

    /**
     * The wrapped Extractor.
     */
    private Extractor extractor;

    /**
     * Flag that indicates that a request to stop extracting has been issued.
     */
    private boolean stopRequested;
    
    /**
     * The actual max processing time per MB for this ThreadedExtractorWrapper instance
     */
    private long maxProcessingTimePerMb = DEFAULT_MAX_PROCESSING_TIME_PER_MB;
    
    /**
     * The actual minimal processing time.
     */
    private long minimumMaxProcessingTime = DEFAULT_MINIMUM_MAX_PROCESSING_TIME;

    /**
     * The actual max idle read time.
     */
    private long maxIdleReadTime = DEFAULT_MAX_IDLE_READ_TIME;
    
    /**
     * Creates a new wrapper for the specified Extractor. It uses default timeout values. It is equivalent to
     * 
     * <pre>
     * new ThreadedExtractorWrapper(extractor, DEFAULT_MAX_PROCESSING_TIME_PER_MB,
     *         DEFAULT_MINIMUM_MAX_PROCESSING_TIME, DEFAULT_MAX_IDLE_READ_TIME);
     * </pre>
     * 
     * @param extractor The Extractor to wrap.
     * @see #DEFAULT_MAX_PROCESSING_TIME_PER_MB
     * @see #DEFAULT_MINIMUM_MAX_PROCESSING_TIME
     * @see #DEFAULT_MAX_IDLE_READ_TIME
     */
    public ThreadedExtractorWrapper(Extractor extractor) {
        this.extractor = extractor;
        stopRequested = false;
    }
    
    /**
     * Creates a new wrapper for the specified Extractor. It allows the user to customize the timeout
     * values.
     * @param extractor The Extractor to wrap.
     * @param maxProcessingTimePerMb  see {@link #DEFAULT_MAX_PROCESSING_TIME_PER_MB}
     * @param minimumMaxProcessingTime see {@link #DEFAULT_MINIMUM_MAX_PROCESSING_TIME}
     * @param maxIdleReadTime see {@link #DEFAULT_MAX_IDLE_READ_TIME}
     */
    public ThreadedExtractorWrapper(Extractor extractor, long maxProcessingTimePerMb,
            long minimumMaxProcessingTime, long maxIdleReadTime) {
        this.extractor = extractor;
        this.stopRequested = false;
        this.maxProcessingTimePerMb = maxProcessingTimePerMb;
        this.minimumMaxProcessingTime = minimumMaxProcessingTime;
        this.maxIdleReadTime = maxIdleReadTime;
    }

    /**
     * Interrupts processing of the wrapped extractor as soon as possible.
     */
    public void stop() {
        stopRequested = true;
    }

    /**
     * Starts the extraction process using the wrapped Extractor on a separate thread. This Thread is
     * interrupted as soon as no progress is reported. In this case an {@link ExtractionAbortedException} will
     * be thrown.l
     * 
     * @throws ExtractorException if any problem with the extractor occurs, this is exactly the same Exception
     *             instance as the one thrown by the extractor.
     * @throws ExtractionAbortedException if the extractor wrapper decided that the extractor has stalled and
     *             the extraction has been aborted
     */
    public void extract(URI id, InputStream input, Charset charset, String mimeType, RDFContainer result)
            throws ExtractorException {
        ExtractionStream monitoredStream = new ExtractionStream(input);

        ExtractionThread thread = new ExtractionThread(id, monitoredStream, charset, mimeType, result);
        thread.start();

        while (true) {
            long waitTime;
            if (monitoredStream.allBytesRead()) {
                /*
                 * This means that the underlying extractor has read all bytes within the underlying stream,
                 * we must wait until the processing ends. We assume that the processing time is linearly
                 * dependent on the file length (with the maxProcessingTimePerMb coefficient).
                 */
                waitTime = (long)(maxProcessingTimePerMb * monitoredStream.getTotalBytesRead() / 
                        (1024.0 * 1024.0));
                /*
                 * Very small files don't fit into this linear heuristic, so we place a bound, that allows
                 * very small files to be processed in fixed time (minimumMaxProcessingTime)
                 */
                waitTime = Math.max(waitTime, minimumMaxProcessingTime);
            }
            else {
                /*
                 * This means that the extractor hasn't stopped reading. We try to detect if if has hanged
                 * during reading.
                 */
                waitTime = maxIdleReadTime;
            }

            /*
             * We may be in the middle of an interval between two reads. We need to subtract the time that
             * has already elapsed since the last read from the overall waiting time.
             */
            waitTime -= (System.currentTimeMillis() - monitoredStream.getLastAccessTime());

            /*
             * This indicates that the extraction thread has stopped, which in turn means that the extractor
             * has finished extracting data. This is OK, we may safely bail out from this loop. 
             */
            if (!thread.isAlive()) {
                break;
            }
            
            /*
             * This will occur if the time that has elapsed since the last read is greater than the time we
             * may wait. E.g. the processing time is longer than the max processing time for the given file
             * length, or the time between reads is longer than the max time between reads. This means that
             * we officially declare this extractor as stalled and bail out from this loop.
             */
            if (waitTime <= 0L) {
                break;
            }

            try {
                /*
                 * Now we wait for the computed amount of miliseconds. We use the join method, so if the
                 * extractor finishes earlier, the join method will also finish and the isAlive() check will
                 * make this loop end gracefully. On the other hand, if the extractor doesn't finish within
                 * the prescribed time, we'll get to decide later what to do with it.
                 */
                thread.join(waitTime);
            }
            catch (InterruptedException e) {
                throw new ExtractorException(e);
            }
        }

        if (thread.isAlive()) {
            /*
             * This indicates that the previous loop ended with a decision that the extractor has hanged. We
             * must abort the extraction and throw an appropriate exception.
             */
            thread.abortExtraction();
            throw new ExtractionAbortedException();
        }
        else {
            /*
             * This means that the extraction thread has ended, which in turn means that the extraction
             * process has ended with a success, or the extractor has thrown some exception, if there are any
             * exceptions we need to propagate them further to the user.
             */
            Exception e = thread.getException();
            if (e != null) {
                if (e instanceof ExtractorException) {
                    throw (ExtractorException) e;
                }
                else {
                    throw (RuntimeException) e;
                }
            }
        }
    }
    
    /** An exception that gets thrown if the underlying extractor hangs. */
    public static class ExtractionInterruptedException extends IOException {

        private static final long serialVersionUID = -4052987146699550288L;

        /** The default constructor */
        public ExtractionInterruptedException() {
            super("Extraction interrupted upon request");
        }
    }

    /**
     * An exception that gets thrown if the extraction is aborted per user request i.e. when the
     * {@link ThreadedExtractorWrapper#stop()} method is called.
     */
    public static class ExtractionAbortedException extends ExtractorException {

        private static final long serialVersionUID = -1806199350296089459L;

        /** The default constructor */
        public ExtractionAbortedException() {
            super("Extraction has been aborted due to problems with the extractor");
        }
    }

    private class ExtractionThread extends Thread {

        private URI id;

        private InputStream input;

        private Charset charset;

        private String mimeType;

        private RDFContainer result;

        private Exception exception;

        public ExtractionThread(URI id, InputStream input, Charset charset, String mimeType,
                RDFContainer result) {
            this.id = id;
            this.input = input;
            this.charset = charset;
            this.mimeType = mimeType;
            this.result = result;
        }

        public void run() {
            try {
                extractor.extract(id, input, charset, mimeType, result);
            }
            catch (Exception e) {
                exception = e;
            }
        }

        public void abortExtraction() {
            interrupt();
        }

        public Exception getException() {
            return exception;
        }
    }

    private class ExtractionStream extends FilterInputStream {

        private long lastAccessTime;

        private boolean allBytesRead;

        private int totalBytesRead;

        public ExtractionStream(InputStream in) {
            super(in);

            lastAccessTime = System.currentTimeMillis();
            allBytesRead = false;
            totalBytesRead = 0;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public boolean allBytesRead() {
            return allBytesRead;
        }

        public int getTotalBytesRead() {
            return totalBytesRead;
        }

        public int read() throws IOException {
            checkStopRequested();

            int result = super.read();

            if (result >= 0) {
                lastAccessTime = System.currentTimeMillis();
                totalBytesRead++;
            }
            else {
                allBytesRead = true;
            }

            return result;
        }

        public int read(byte[] b) throws IOException {
            checkStopRequested();

            int result = super.read(b);

            if (result >= 0) {
                lastAccessTime = System.currentTimeMillis();
                totalBytesRead += result;
            }
            else {
                allBytesRead = true;
            }

            return result;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            checkStopRequested();

            int result = super.read(b, off, len);

            if (result >= 0) {
                lastAccessTime = System.currentTimeMillis();
                totalBytesRead += result;
            }
            else {
                allBytesRead = true;
            }

            return result;
        }

        public void close() throws IOException {
            super.close();
            allBytesRead = true;
        }

        private void checkStopRequested() throws ExtractionInterruptedException {
            if (stopRequested) {
                throw new ExtractionInterruptedException();
            }
        }
    }
}
