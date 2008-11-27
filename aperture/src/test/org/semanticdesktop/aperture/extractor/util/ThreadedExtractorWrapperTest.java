/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.ThreadedExtractorWrapper.ExtractionAbortedException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Tests for the {@link ThreadedExtractorWrapper} class. It establishes a stream and a mock extractor to test
 * various behavior patterns in extractors.
 */
public class ThreadedExtractorWrapperTest extends ApertureTestBase {
    
    private byte [] _array;
    
    private InputStream _stream;
    
    private List<PlanPoint> _readPlan;
    
    /**
     * Tests a normal, well behaving extractor that reads the stream at regular intervals and doesn't need any
     * additional processing afterwards. This extractor will read data from a 4MB file in chunks of 16384
     * bytes every 10 miliseconds
     * 
     * @throws Exception
     */
    public void testNormalWellBehavingExtractor() throws Exception {
        initializeStream(4 * 1024 * 1024);
        WaitingExtractor extractor = new WaitingExtractor(0);
        ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor);
        int currentByte = 0;
        while (currentByte < 4 * 1024 * 1024) { // prepare a read plan that will read 16KB every 10 ms
            _readPlan.add(new PlanPoint(16384, 10));
            currentByte += 16384;
        }
        wrapper.extract(null, _stream, null, null, null); // nothing should happen
    }
    
    /**
     * Tests an extractor that halted before the reading phase is over. We set the max read idle time to 300
     * ms, first we test an extractor that reads from the stream at 100 ms intervals, then we test an
     * extractor that does two reads at 100 ms and one much longer, above the read idle time limit.
     * 
     * @throws Exception
     */
    public void testExtractorThatStoppedReading() throws Exception {
        initializeStream(4 * 1024);
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        
        WaitingExtractor extractor = new WaitingExtractor(0);
        ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor,
                ThreadedExtractorWrapper.DEFAULT_MAX_PROCESSING_TIME_PER_MB,
                ThreadedExtractorWrapper.DEFAULT_MINIMUM_MAX_PROCESSING_TIME, 200);
        wrapper.extract(null, _stream, null, null, null); // nothing should happen
        initializeStream(4*1024);
        _readPlan.clear();
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 1200));
        _readPlan.add(new PlanPoint(1024, 100));
        _readPlan.add(new PlanPoint(1024, 100));
        try {
            wrapper.extract(null, _stream, null, null, null); // an exception should be thrown
            fail();
        } catch (ExtractionAbortedException eae) {
            // this should happen
        }
    }
    
    /**
     * Tests how an extractor would process a small file (1 KB). We set the minMaxProcessing time to 300 ms,
     * first we try to extract stuff with an extractor that fits within the minMaxProcessingTime, then an
     * extractor that doesn't fit within that time. We have to set the read idle time to a much shorter value
     * to ensure that the wrapper will get into the 'processing' mode.
     * 
     * @throws Exception
     */
    public void testSmallFileLimit() throws Exception {
        initializeStream(1024);
        _readPlan.add(new PlanPoint(1024, 20)); // 20 ms of waiting, below the 50 ms limit
        _readPlan.add(new PlanPoint(2, 20)); // this is necessary for the extractor to finish reading and
                                             // start processing
        
        WaitingExtractor extractor = new WaitingExtractor(200); // 200 ms of processing time
        ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor,
                ThreadedExtractorWrapper.DEFAULT_MAX_PROCESSING_TIME_PER_MB, 300, 50);
        wrapper.extract(null, _stream, null, null, null); // nothing should happen here
        
        initializeStream(1024);
        extractor = new WaitingExtractor(400); // 400 ms of processing time - above the limit
        wrapper = new ThreadedExtractorWrapper(extractor,
                ThreadedExtractorWrapper.DEFAULT_MAX_PROCESSING_TIME_PER_MB, 300, 50);
        try {
            wrapper.extract(null, _stream, null, null, null); // an exception should be thrown
            fail(); // if it doesn't it's a failure
        } catch (ExtractionAbortedException eae) {
            // this is ok
        }
    }
    
    /**
     * Tests whether the per-MB processing speed is obeyed correctly. We simulate a 2 MB file and a processing
     * speed of 100 ms per MB. First we have an extractor that reads the file quickly and processes it for 100
     * ms which is OK, then we have an extractor that reads the file quickly and then processes it for 300 ms
     * which is too long, according to the settings.
     * 
     * @throws Exception
     */
    public void testLargeFileProcessing() throws Exception {
        initializeStream(2 * 1024 * 1024); // 2 MB file
        for (int i = 0; i < 206; i++) {
            _readPlan.add(new PlanPoint(50 * 1024, 5)); // read 50 KB every 5 ms
        }
        WaitingExtractor extractor = new WaitingExtractor(50); // 500 ms of processing time, OK
        /*
         * 1MB should be processed within 100 ms, for our file (2 MB) the entire processing time shouldn't
         * exceed 200ms, the minMax processing and the read idle time are set to be very small
         */ 
        ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor, 200, 20, 40);
        wrapper.extract(null, _stream, null, null, null); // nothing should happen here, 100 ms is within the
                                                          // limit
        initializeStream(2 * 1024 * 1024);
        extractor = new WaitingExtractor(800); // 800 ms of processing time, above the limit
        wrapper = new ThreadedExtractorWrapper(extractor, 200, 20, 40);
        try {
            // an exception should be thrown, 250 ms is outside the limit
            wrapper.extract(null, _stream, null, null, null); 
            fail(); // if it doesn't it's a failure
        }
        catch (ExtractionAbortedException eae) {
            // this is ok
        }
    }
    
    /**
     * Tests if the ThreadedExtractorWrapper correctly behaves in a situation when the extractor throws a
     * checked ExtractorException i.e. it throws exactly the same exception instance.
     */
    public void testExtractorException() {
        testThrowException(new ExtractorException());
    }

    /**
     * Tests if the ThreadedExtractorWrapper correctly behaves in a situation when the extractor throws an
     * unchecked RuntimeException i.e. it throws exactly the same exception instance.
     */
    public void testRuntimeException() {
        testThrowException(new ArrayIndexOutOfBoundsException());
    }

    private void testThrowException(Exception e) {
        ExceptionThrowingExtractor extractor = new ExceptionThrowingExtractor(e);
        ThreadedExtractorWrapper tewrapper = new ThreadedExtractorWrapper(extractor);
        try {
            tewrapper.extract(null, null, null, null, null);
            // when any exceptions are lost in the extracting thread (i.e. are not caught and end up at the
            // top of the thread's own stack), the extract method will seem to terminate normally and proceed,
            // even though extraction has failed and an exception should have been thrown
            fail();
        }
        catch (Exception exc) {
            // make sure we receive the same Exception instance as was thrown by the original Extractor
            assertSame(e, exc);
        }
    }
    
    private void initializeStream(int streamLength) {
        _readPlan = new LinkedList<PlanPoint>();
        this._array = new byte[streamLength];
        new Random(System.currentTimeMillis()).nextBytes(_array);
        this._stream = new ByteArrayInputStream(_array);
    }

    private static class ExceptionThrowingExtractor implements Extractor {

        private Exception e;

        public ExceptionThrowingExtractor(Exception e) {
            this.e = e;
        }

        public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
                throws ExtractorException {
            if (e instanceof ExtractorException) {
                throw (ExtractorException) e;
            }
            else {
                throw (RuntimeException) e;
            }
        }
    }
    
    private class PlanPoint {
        private int bytes;
        private long pointInTimeOrLength;
        public PlanPoint(int bytes, long pointInTimeOrLength) {
            this.bytes = bytes;
            this.pointInTimeOrLength = pointInTimeOrLength;
        }
    }
    
    /**
     * A mock of an extractor that reads from the underlying stream prescribed amounts of bytes in prescribed
     * points in time.
     */
    private class WaitingExtractor implements Extractor {
        
        private long processingTime;
        
        public WaitingExtractor(long processingTime) {
            this.processingTime = processingTime;
        }

        public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
                throws ExtractorException {
            // first we need to convert the generic plan, containing the intervals between read commands, into a 
            // concrete plan, with exact timestamps when the read operations should occur
            List<PlanPoint> concretePlan = new LinkedList<PlanPoint>();
            for (int i = 0; i < _readPlan.size(); i++) {
                PlanPoint genericPoint = _readPlan.get(i);
                if (i > 0) {
                    concretePlan.add(
                        new PlanPoint(
                            genericPoint.bytes, 
                            concretePlan.get(i-1).pointInTimeOrLength + genericPoint.pointInTimeOrLength));
                } else {
                    concretePlan.add(new PlanPoint(genericPoint.bytes,System.currentTimeMillis()));
                }
            }
            
            // then we can read.
            while (concretePlan.size() > 0) {
                try {
                    PlanPoint currentPoint = concretePlan.remove(0);
                    long currentTime = System.currentTimeMillis();
                    interruptibleSleep(currentPoint.pointInTimeOrLength - currentTime);
                    byte [] array = new byte[currentPoint.bytes];
                    if (stream.read(array) == -1) {
                        break;
                    }
                }
                catch (IOException e) {
                    // this may happen if the extraction is stopped with the stop() method of the 
                    // ThreadedExtractorWrapper
                    throw new ExtractorException(e);
                }
                catch (InterruptedException e) {
                    // this may happen if the extraction is interrupted with the abortExtraction() method
                    // of the ThreadExtractorWrapper.ExtractionThread
                    throw new ExtractorException(e);
                }
            }
            
            // and afterwards we process
            try {
                interruptibleSleep(processingTime);
            }
            catch (InterruptedException e) {
                throw new ExtractorException(e);
            }
        }
    }
}
