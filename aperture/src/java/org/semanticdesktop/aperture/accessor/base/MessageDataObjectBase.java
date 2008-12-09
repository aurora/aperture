/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

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
        this.message = message;
    }

    public void setMimeMessage(MimeMessage message) {
        this.message = message;
    }

    public MimeMessage getMimeMessage() {
        return message;
    }
}