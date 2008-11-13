/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.MessageDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A trivial default implementation of a MessageDataObject.
 * </p>
 * 
 * <p>
 * This implementation needs an ExecutorService in order to work properly. Here's an explanation why is it
 * so:.
 * <ol>
 * <li>The requirement was to get a stream of bytes EXACTLY as they appear in an .eml file, with ALL headers
 * and such
 * <li>there is no way to get the raw bytes of the message with all headers <br/> 
 * - getRawInputStream returns the raw input stream of the CONTENT <br/> 
 * - getContentStream obviously returns the CONTENT input stream, converted to 'intended' byte values according 
 *   to the "Content-Transfer-Encoding" header if such header is present <br/>
 * - tried subclassing the MimeMessage to fool java into giving me access to the protected byte[] content 
 * field, but java won't do it
 * <li>I don't want to copy the message content for each MessageDataObject because it the memory consumption
 * will skyrocket
 * <li>The only method that returns the desired data is writeTo, but it accepts an OutputStream, while I need
 * to return an InputStream, that's why I need to use PipedInputStream and PipedOutputStream, the writeTo
 * method has to be invoked by a separate thread, so that the client can read from the returned input stream
 * <li>I don't want to create a separate thread for each new MessageDataObjectBase instance, because
 * performance will suck, therefore a shared thread pool with pre-created threads is a much better idea
 * </ol>
 * </p>
 */
public class MessageDataObjectBase extends DataObjectBase implements MessageDataObject {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private MimeMessage message;
    private InputStream contentStream;
    private ExecutorService executorService;
    private boolean executorServiceShared;

    /**
     * Constructor accepting a message. It will use an exclusive single-thread executor service. Therefore
     * when you create a MessageDataObjectBase instance with this constructor and then call getContent() on it
     * a new thread will be created. It may incur a significant performance overhead. Wherever feasible,
     * please set up a shared executor service and use the other constructor instead.
     * 
     * @param id URI of the data object (obligatory)
     * @param dataSource the data source where this data object came from (optional)
     * @param metadata the metadata for this data object (optional)
     * @param message the MimeMessage
     */
    public MessageDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, MimeMessage message){
        super(id,dataSource,metadata);
        this.executorService = Executors.newSingleThreadExecutor();
        this.executorServiceShared = false;
        this.message = message;
    }

    /**
     * <p>
     * Constructor accepting a message and a shared ExecutorService. The executor service will not be closed
     * on disposal of this MessageDataObject. If you cal getContent() on this instance, a new task will be
     * submitted to the executor service. Please adjust the executor service for the amount of
     * MessageDataObjectBase instances you wish to process simultaneously.
     * </p>
     * 
     * @param id URI of the data object (obligatory)
     * @param dataSource the data source where this data object came from (optional)
     * @param metadata the metadata for this data object (optional)
     * @param message the MimeMessage
     * @param service the ExecutorService, see the docs for this class for an explanation what it's for
     */
    public MessageDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, MimeMessage message, 
            ExecutorService service) {
        super(id,dataSource,metadata);
        this.executorService = service;
        this.executorServiceShared = true;
        this.message = message;
    }

    private InputStream getMessageStream(final MimeMessage msg) throws MessagingException, IOException {
        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);

        Runnable r = new Runnable() {
            public void run() {
                try {
                    msg.writeTo(out);
                }
                catch (Exception e) {
                    if (e.getMessage() == null || !e.getMessage().equals("Pipe closed")) {
                        // a "pipe closed" message is OK, it means that the reader closed the stream
                        // the user doesn't have to read the entire content of the stream
                        logger.warn("Error while generating the MailDataObject content stream",e);
                    }
                }
                finally {
                    try {
                        out.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        executorService.execute(r);
        return in;
    }

    public void setMimeMessage(MimeMessage message) {
        this.message = message;
    }

    public MimeMessage getMimeMessage() {
        return message;
    }
    
    /**
     * @return Returns the executorService.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    public InputStream getContent() throws MessagingException, IOException {
        if (isDisposed()) {
            throw new IllegalStateException("Can't get content from a disposed data object");
        } else if (contentStream != null) {
            return contentStream;
        } else {
            contentStream = getMessageStream(this.message);
            return contentStream;
        }  
    }

    @Override
    public void dispose() {
        super.dispose();
        closeContent();
        if (!executorServiceShared) {
            executorService.shutdown();
        }
    }
    
    /**
     * Closes the content stream and sets it to null.
     */
    protected void closeContent() {
        try {
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }
        }
        catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("IOException while closing stream", e);
        }
    }
}