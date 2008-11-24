/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.crawler.mail.AbstractJavaMailCrawler;
import org.semanticdesktop.aperture.crawler.mail.DataObjectFactory;
import org.semanticdesktop.aperture.crawler.mail.MailUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.UpdateException;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.subcrawler.PathNotFoundException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.subcrawler.base.AbstractSubCrawler;
import org.semanticdesktop.aperture.util.StringUtil;

/**
 * <p>
 * A SubCrawer implementation for message/rfc822-style messages.
 * </p>
 * 
 * <p>
 * It is basically a thin wrapper around the DataObjectFactory.
 * </p>
 */
public class MimeSubCrawler extends AbstractSubCrawler implements DataObjectFactory.PartStreamFactory {
    
    private boolean stopRequested;
    
    private boolean sharedService = true;
    
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata)
            throws SubCrawlerException {
        DataObjectFactory fac = null;
        ExecutorService executorService = (sharedService ? Executors.newSingleThreadExecutor() : null);
        try {
            MimeMessage msg = new MimeMessage(null, stream);
            URI attachmentUriPrefix = createChildUri(parentMetadata.getDescribedUri(), "");
            RDFContainerFactory myFac = 
                new FilteringRDFContainerFactory(
                    handler.getRDFContainerFactory(parentMetadata.getDescribedUri().toString()),
                    parentMetadata,attachmentUriPrefix);
            fac = new DataObjectFactory(msg,myFac,executorService,this,dataSource,attachmentUriPrefix,null,"");
            DataObject object = null;
            
            /*
             * Note that the stopRequested check is BEFORE getObject(). Otherwise if the crawler is stopped
             * the object is obtained and only AFTER this loop is stopped. This object is not disposed by the
             * dataObjectFactory.disposeRemainingObjects() in the finally clause, yields a warning message and
             * can potentially lead to problems.
             */
            while (!stopRequested && (object = fac.getObject()) != null) { 
                /*
                 * we bypass the data object that corresponds to the message itself. by virtue of the
                 * FilterRDFContainerFactory, the metadata of this object is actually the same instance as the
                 * parent metadata passed as an argument to this method.
                 */
                if (object.getMetadata().getDescribedUri().equals(parentMetadata.getDescribedUri())) {
                    /*
                     * we may safely call dispose here, because the object metadata is our
                     * UnDisposableRDFContainer
                     */
                    object.dispose();
                    continue;
                }

                // first of all get a string version of the message uri
                String queuedUri = object.getID().toString();           
                    
                /*
                 * See if this url has been accessed before so that we can stop immediately. Note that no
                 * check on message date is done as messages are immutable. Therefore we only have to check
                 * whether the AccessData knows this ID.
                 */
                if (accessData != null && accessData.get(queuedUri, AbstractJavaMailCrawler.ACCESSED_KEY) != null) {
                    /*
                     * Report the object as changed, we assume that the application that calls the subcrawler
                     * will be able to detect by other means if the entire message has been changed or not. If
                     * the subcrawler has been called, we assume that the entire message has been detected as
                     * changed
                     */
                    handler.objectChanged(object); 
                    continue;
                }
    
                /*
                 * store the information in the access data that we have met this object, 
                 */
                if (accessData != null) {
                    accessData.put(queuedUri, AbstractJavaMailCrawler.ACCESSED_KEY, "");
                }
    
                /*
                 * register parent child relationship (necessary in order to be able to report unmodified or
                 * deleted attachments). This relationship is recorded in the accessdata no new information is
                 * added to the objects metadata RDFContainer
                 */
                MailUtil.registerParentRelationshipInAccessData(object, accessData);
    
                /*
                 * Report this object as a new object (assumption: objects are always new, never changed,
                 * since mails are immutable). This MUST happen last because the CrawlerHandler will probably
                 * dispose of it.
                 */
                handler.objectNew(object);
            }
            
        }
        catch (MessagingException e) {
            throw new SubCrawlerException(e);
        }
        catch (IOException e) {
            throw new SubCrawlerException(e);
        }
        finally {
            if (executorService != null) {
                executorService.shutdown();
            }
            if (fac != null) {
                fac.disposeRemainingObjects();
            }
        }
    }
    
    public void stopSubCrawler() {
        stopRequested = true;
    }
    
    @Override
    public DataObject getDataObject(URI parentUri, String path, InputStream stream, DataSource dataSource, Charset charset,
            String mimeType, RDFContainerFactory factory) throws SubCrawlerException, PathNotFoundException {
        sharedService = false;
        DataObject result = null;
        try {
            result = super.getDataObject(parentUri, path, stream, dataSource, charset, mimeType, factory);
        } finally {
            sharedService = true;
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private String getMessageId(Message message) throws MessagingException {
        String [] messageIds = message.getHeader("Message-ID");
        String id = null;
        
        if (messageIds != null && messageIds.length > 0) {
            id = messageIds[0];
            if (id.startsWith("<")) {
                id = id.substring(1);
            }
            if (id.endsWith(">")) {
                id = id.substring(0,id.length() - 1);
            }
            try {
                id = URLEncoder.encode(id, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                // this obviously won't happen
            }
        } else {
            /*
             * This is insane, but you never know...
             */
            StringBuilder builder = new StringBuilder();
            Enumeration enumeration = message.getAllHeaders();
            /*
             * This obviously depends on the headers being returned in the same
             * order every time the message is read. Let's hope this is actually
             * the case, didn't actually test it.
             */
            while (enumeration.hasMoreElements()) {
                Header header = (Header)enumeration.nextElement();
                builder.append(header.getName());
                builder.append(header.getValue());
            }
            id = StringUtil.sha1Hash(builder.toString());
        }
        return id;
    }
    
    @Override
    public String getUriPrefix() {
        return MimeSubCrawlerFactory.MIME_URI_PREFIX;
    }

    public InputStream getPartStream(Part part) throws MessagingException, IOException {
        return part.getInputStream();
    }
    
    private class FilteringRDFContainerFactory implements RDFContainerFactory {
        private RDFContainerFactory wrappedFactory;
        private RDFContainer filterContainer;
        private URI messageUri;
        FilteringRDFContainerFactory(RDFContainerFactory factory, RDFContainer filterContainer, URI uri) {
            this.wrappedFactory = factory;
            this.filterContainer = new UndisposableRDFContainer(filterContainer);
            this.messageUri = uri;
        }
        public RDFContainer getRDFContainer(URI uri) {
            if (uri.equals(messageUri)) {
                return filterContainer;
            } else {
                return wrappedFactory.getRDFContainer(uri);
            }
        }
    }
    
    private class UndisposableRDFContainer implements RDFContainer {
        private RDFContainer wrappedContainer;
        UndisposableRDFContainer(RDFContainer container) {
            this.wrappedContainer = container;
        }
        public void add(Statement statement) throws UpdateException {
            wrappedContainer.add(statement);
        }
        public void add(URI property, boolean value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, Calendar value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, Date value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, int value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, long value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, Node value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void add(URI property, String value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        public void dispose() {
            /* ignore the call to dispose */
        }
        @SuppressWarnings("unchecked")
        public Collection getAll(URI property) {
            return wrappedContainer.getAll(property);
        }
        public Boolean getBoolean(URI property) {
            return wrappedContainer.getBoolean(property);
        }
        public Calendar getCalendar(URI property) {
            return wrappedContainer.getCalendar(property);
        }
        public Date getDate(URI property) {
            return wrappedContainer.getDate(property);
        }
        public URI getDescribedUri() {
            return wrappedContainer.getDescribedUri();
        }
        public Integer getInteger(URI property) {
            return wrappedContainer.getInteger(property);
        }
        public Long getLong(URI property) {
            return wrappedContainer.getLong(property);
        }
        public Model getModel() {
            return wrappedContainer.getModel();
        }
        public Node getNode(URI property) {
            return wrappedContainer.getNode(property);
        }
        public String getString(URI property) {
            return wrappedContainer.getString(property);
        }
        public URI getURI(URI property) {
            return wrappedContainer.getURI(property);
        }
        public ValueFactory getValueFactory() {
            return wrappedContainer.getValueFactory();
        }
        public void put(URI property, boolean value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, Calendar value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, Date value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, int value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, long value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, Node value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void put(URI property, String value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        public void remove(Statement statement) throws UpdateException {
            wrappedContainer.remove(statement);
        }
        public void remove(URI property) throws UpdateException {
            wrappedContainer.remove(property);
        }
    }
}
