/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.MessageDataObject;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.slf4j.impl.JDK14LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Tests for the {@link MessageDataObjectBase} class
 */
public class TestMessageDataObject extends ApertureTestBase {

    /**
     * Tests if the constructor accepting a MimeMessage works correctly, so that the getContent, method yields
     * correct results i.e. byte-by-byte identical with the source file.
     * 
     * @throws Exception
     */
    public void testMimeMessageConstructor() throws Exception {
        MessageDataObjectBase obj = getTestInstance();
        byte[] newbytes = IOUtil.readBytes(obj.getContent());
        byte[] oldbytes = IOUtil.readBytes(ResourceUtil.getInputStream(DOCS_PATH + "/mail-multipart-test.eml", getClass()));
        assertEquals(oldbytes.length, newbytes.length);
        for (int i = 0; i < oldbytes.length; i++) {
            assertEquals(newbytes[i], oldbytes[i]);
        }
        obj.dispose();
    }

    /**
     * The simpler constructor creates an executor service for each MessageDataObject instance. It should be
     * shutdown on disposal. This check tests for this.
     * 
     * @throws Exception
     */
    public void testNonSharedExecutorService() throws Exception {
        MessageDataObjectBase obj = getTestInstance();
        IOUtil.readBytes(obj.getContent());
        obj.dispose();
        assertTrue(obj.getExecutorService().isShutdown());
    }

    /**
     * Tests if the constructor accepting a shared executor service does NOT shutdown it on disposal.
     * 
     * @throws Exception
     */
    public void testSharedExecutorService() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        MessageDataObjectBase obj = getTestInstance(service);
        IOUtil.readBytes(obj.getContent());
        obj.dispose();
        assertFalse(obj.getExecutorService().isShutdown());
        service.shutdown();
    }

    /**
     * Tests if the MessageDataObjectBase class behaves correctly if the content stream is obtained but not
     * read in it's entirety. No exceptions should be logged.
     * 
     * @throws Exception
     */
    public void testPrematureStreamClose() throws Exception {
        // this will only work if JDK logging is used, so we need to check this
        assertEquals(JDK14LoggerFactory.class.getName(), StaticLoggerBinder.SINGLETON.getLoggerFactoryClassStr());
        CheckingLogger logger = new CheckingLogger(MessageDataObjectBase.class.getName());
        LogManager.getLogManager().addLogger(logger);
        MessageDataObject obj = getTestInstance();

        InputStream contentStream = obj.getContent(); // get the content stream
        contentStream.read(new byte[25]); // read some bytes
        contentStream.close(); // close it all without reading everything
        safelySleep(500); // sleep some time, to allow the pushing thread to
        // throw an exception

        // all is OK, nothing should be logged
        assertFalse(logger.wasUsed());
        obj.dispose();
    }

    /**
     * The implementation is supposed to make the content stream available only once. If all bytes from the
     * stream have been read a call to stream.read() should return -1.
     * 
     * @throws Exception
     */
    public void testContentStreamAvailableOnlyOnce() throws Exception {
        MessageDataObjectBase obj = getTestInstance();
        IOUtil.readBytes(obj.getContent());
        assertEquals(-1, obj.getContent().read());
        obj.dispose();
    }

    /**
     * Multiple calls to getContent should return the same content stream.
     * 
     * @throws Exception
     */
    public void testMultipleGetContentCallsReturnTheSameStream() throws Exception {
        MessageDataObjectBase obj = getTestInstance();
        InputStream content1_1 = obj.getContent();
        int b1 = content1_1.read();
        int b2 = content1_1.read();
        InputStream content1_2 = obj.getContent();
        int b3 = content1_2.read();
        int b4 = content1_2.read();

        assertTrue(content1_1 == content1_2);

        MessageDataObjectBase obj2 = getTestInstance();
        assertTrue(obj != obj2);
        InputStream content2 = obj2.getContent();
        assertEquals(b1, content2.read());
        assertEquals(b2, content2.read());
        assertEquals(b3, content2.read());
        assertEquals(b4, content2.read());

        obj.dispose();
        obj2.dispose();
    }
    
    /**
     * After a call to dispose() the getContent() method should throw an IllegalStateException, both if it
     * has and if it hasn't been used before.
     * @throws Exception
     */
    public void testNoGetContentAfterDisposal() throws Exception {
        MessageDataObjectBase obj = getTestInstance();
        obj.getContent().read();
        obj.getContent().read();
        obj.dispose();
        try {
            obj.getContent();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
        
        MessageDataObjectBase obj2 = getTestInstance();
        obj2.dispose();
        try {
            obj2.getContent();
            fail();
        } catch (IllegalStateException e) {
            // ok
        }
    }

    private MessageDataObjectBase getTestInstance() throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "/mail-multipart-test.eml", getClass());
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, stream);
        URI id = new URIImpl("file://somefile.eml");
        MessageDataObjectBase obj = new MessageDataObjectBase(id, null, createRDFContainer(id), message);
        return obj;
    }

    private MessageDataObjectBase getTestInstance(ExecutorService service) throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "/mail-multipart-test.eml", getClass());
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, stream);
        URI id = new URIImpl("file://somefile.eml");
        MessageDataObjectBase obj = new MessageDataObjectBase(id, null, createRDFContainer(id), message,
                service);
        return obj;
    }
}

class CheckingLogger extends Logger {

    boolean used;

    public CheckingLogger(String name) {
        super(name, null);
        used = false;
    }

    public boolean wasUsed() {
        return used;
    }

    @Override
    public void config(String msg) {
        used = true;
        super.config(msg);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object param1) {
        used = true;
        super.entering(sourceClass, sourceMethod, param1);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        used = true;
        super.entering(sourceClass, sourceMethod, params);
    }

    @Override
    public void entering(String sourceClass, String sourceMethod) {
        used = true;
        super.entering(sourceClass, sourceMethod);
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod, Object result) {
        used = true;
        super.exiting(sourceClass, sourceMethod, result);
    }

    @Override
    public void exiting(String sourceClass, String sourceMethod) {
        used = true;
        super.exiting(sourceClass, sourceMethod);
    }

    @Override
    public void fine(String msg) {
        used = true;
        super.fine(msg);
    }

    @Override
    public void finer(String msg) {
        used = true;
        super.finer(msg);
    }

    @Override
    public void finest(String msg) {
        used = true;
        super.finest(msg);
    }

    @Override
    public void info(String msg) {
        used = true;
        super.info(msg);
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        used = true;
        super.log(level, msg, param1);
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        used = true;
        super.log(level, msg, params);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        used = true;
        super.log(level, msg, thrown);
    }

    @Override
    public void log(Level level, String msg) {
        used = true;
        super.log(level, msg);
    }

    @Override
    public void log(LogRecord record) {
        used = true;
        super.log(record);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        used = true;
        super.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        used = true;
        super.logp(level, sourceClass, sourceMethod, msg, params);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        used = true;
        super.logp(level, sourceClass, sourceMethod, msg, thrown);
    }

    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        used = true;
        super.logp(level, sourceClass, sourceMethod, msg);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
            Object param1) {
        used = true;
        super.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
            Object[] params) {
        used = true;
        super.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg,
            Throwable thrown) {
        used = true;
        super.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }

    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        used = true;
        super.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }

    @Override
    public void severe(String msg) {
        used = true;
        super.severe(msg);
    }

    @Override
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        used = true;
        super.throwing(sourceClass, sourceMethod, thrown);
    }

    @Override
    public void warning(String msg) {
        used = true;
        super.warning(msg);
    }
}