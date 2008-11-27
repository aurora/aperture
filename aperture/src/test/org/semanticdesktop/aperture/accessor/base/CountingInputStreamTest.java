/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class CountingInputStreamTest extends ApertureTestBase {

    private CountingInputStream stream;

    public void setUp() {
        InputStream testStream = ResourceUtil.getInputStream(DOCS_PATH
                + "counting-input-stream-test-file.dat", this.getClass());
        // This check is necessary, we need a stream that supports mark and tests have shown
        // that various classloaders return different stream implementations, some of them
        // support mark() some don't
        if (testStream.markSupported()) {
            stream = new CountingInputStream(testStream);
        } else {
            stream = new CountingInputStream(new BufferedInputStream(testStream));
        }
    }
    
    public void tearDown() throws IOException {
        stream.close();
    }

    public void testReadSingleByte() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        int readByte = stream.read();
        assertTrue(stream.getCurrentByte() == 1);
    }
    
    public void testReadTenBytes() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        for (int i = 0; i<10; i++) {
            int readbyte = stream.read();
        }
        assertTrue(stream.getCurrentByte() == 10);
    }
    
    public void testReadToArray() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        for (int i = 1; i<=10; i++) {
            stream.read();
        }
        assertTrue(stream.getCurrentByte() == 10);
        byte [] byteArray = new byte[100];
        stream.read(byteArray);
        assertTrue(stream.getCurrentByte() == 110);
    }
    
    public void testReadToArrayWithAnOffset() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        byte [] byteArray = new byte[100];
        stream.read(byteArray, 10, 10);
        assertTrue(stream.getCurrentByte() == 10);
    }
    
    public void testMarkResetToZero() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        stream.mark(100);
        byte [] firstByteArray = new byte[100];
        stream.read(firstByteArray);
        assertTrue(stream.getCurrentByte() == 100);
        stream.reset();
        assertTrue(stream.getCurrentByte() == 0);
        byte [] secondByteArray = new byte[100];
        stream.read(secondByteArray);
        assertTrue(stream.getCurrentByte() == 100);
        for (int i = 0; i < 100; i++) {
            assertTrue(firstByteArray[i] == secondByteArray[i]);
        }
    }
    
    public void testSkip() throws IOException {
        assertTrue(stream.getCurrentByte() == 0);
        stream.mark(100);
        byte [] firstByteArray = new byte[100];
        stream.read(firstByteArray);
        assertTrue(stream.getCurrentByte() == 100);
        stream.reset();
        assertTrue(stream.getCurrentByte() == 0);
        byte [] secondByteArray = new byte[100];
        for (int i = 0; i < 100; i++) {
            secondByteArray[i] = 0;
        }
        
        stream.read(secondByteArray,0,20);
        assertTrue(stream.getCurrentByte() == 20);
        stream.skip(20);
        assertTrue(stream.getCurrentByte() == 40);
        stream.read(secondByteArray,40,20);
        assertTrue(stream.getCurrentByte() == 60);
        stream.skip(20);
        assertTrue(stream.getCurrentByte() == 80);
        
        // the first twenty are the same as first
        for (int i = 0; i < 20; i++) {
            assertTrue(secondByteArray[i] == firstByteArray[i]);
        }
        // the second twenty are zeroes
        for (int i = 20; i < 40; i++) {
            assertTrue(secondByteArray[i] == 0);
        }
        // the third twenty are the same as in first
        for (int i = 40; i < 60; i++) {
            assertTrue(secondByteArray[i] == firstByteArray[i]);
        }
    }
    
}
