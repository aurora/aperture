/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;

/**
 * <p>
 * An SubCrawer implementation for message/rfc822-style messages.
 * </p>
 * 
 * <p>
 * It is basically a thin wrapper around the DataObjectFactory.
 * </p>
 */
public class MimeSubCrawler implements SubCrawler, DataObjectFactory.PartStreamFactory {
    
    private boolean stopRequested;
    
    @SuppressWarnings("unchecked")
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata)
            throws SubCrawlerException {
        DataObjectFactory fac = null;
        try {
            MimeMessage msg = new MimeMessage(null, stream);
            URI messageUri = parentMetadata.getDescribedUri();
            RDFContainerFactory myFac = 
                new FilteringRDFContainerFactory(
                    handler.getRDFContainerFactory(parentMetadata.getDescribedUri().toString()),
                    parentMetadata);
            fac = new DataObjectFactory(msg,myFac,this,dataSource,messageUri,null);
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
                if (object.getID().equals(parentMetadata.getDescribedUri())) {
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
            if (fac != null) {
                fac.disposeRemainingObjects();
            }
        }
    }
    
    public void stopSubCrawler() {
        stopRequested = true;
    }

    public InputStream getPartStream(Part part) throws MessagingException, IOException {
        return part.getInputStream();
    }
    
    private class FilteringRDFContainerFactory implements RDFContainerFactory {
        private RDFContainerFactory wrappedFactory;
        private RDFContainer filterContainer;
        FilteringRDFContainerFactory(RDFContainerFactory factory, RDFContainer filterContainer) {
            this.wrappedFactory = factory;
            this.filterContainer = new UndisposableRDFContainer(filterContainer);
        }
        public RDFContainer getRDFContainer(URI uri) {
            if (uri.equals(filterContainer.getDescribedUri())) {
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
        /**
         * @param statement
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.Statement)
         */
        public void add(Statement statement) throws UpdateException {
            wrappedContainer.add(statement);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, boolean)
         */
        public void add(URI property, boolean value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, java.util.Calendar)
         */
        public void add(URI property, Calendar value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, java.util.Date)
         */
        public void add(URI property, Date value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, int)
         */
        public void add(URI property, int value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, long)
         */
        public void add(URI property, long value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, org.ontoware.rdf2go.model.node.Node)
         */
        public void add(URI property, Node value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#add(org.ontoware.rdf2go.model.node.URI, java.lang.String)
         */
        public void add(URI property, String value) throws UpdateException {
            wrappedContainer.add(property, value);
        }
        /**
         * 
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#dispose()
         */
        public void dispose() {
            /*
             * ignore the call to dispose
             */
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getAll(org.ontoware.rdf2go.model.node.URI)
         */
        @SuppressWarnings("unchecked")
        public Collection getAll(URI property) {
            return wrappedContainer.getAll(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getBoolean(org.ontoware.rdf2go.model.node.URI)
         */
        public Boolean getBoolean(URI property) {
            return wrappedContainer.getBoolean(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getCalendar(org.ontoware.rdf2go.model.node.URI)
         */
        public Calendar getCalendar(URI property) {
            return wrappedContainer.getCalendar(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getDate(org.ontoware.rdf2go.model.node.URI)
         */
        public Date getDate(URI property) {
            return wrappedContainer.getDate(property);
        }
        /**
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getDescribedUri()
         */
        public URI getDescribedUri() {
            return wrappedContainer.getDescribedUri();
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getInteger(org.ontoware.rdf2go.model.node.URI)
         */
        public Integer getInteger(URI property) {
            return wrappedContainer.getInteger(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getLong(org.ontoware.rdf2go.model.node.URI)
         */
        public Long getLong(URI property) {
            return wrappedContainer.getLong(property);
        }
        /**
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getModel()
         */
        public Model getModel() {
            return wrappedContainer.getModel();
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getNode(org.ontoware.rdf2go.model.node.URI)
         */
        public Node getNode(URI property) {
            return wrappedContainer.getNode(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getString(org.ontoware.rdf2go.model.node.URI)
         */
        public String getString(URI property) {
            return wrappedContainer.getString(property);
        }
        /**
         * @param property
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getURI(org.ontoware.rdf2go.model.node.URI)
         */
        public URI getURI(URI property) {
            return wrappedContainer.getURI(property);
        }
        /**
         * @return
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#getValueFactory()
         */
        public ValueFactory getValueFactory() {
            return wrappedContainer.getValueFactory();
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, boolean)
         */
        public void put(URI property, boolean value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, java.util.Calendar)
         */
        public void put(URI property, Calendar value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, java.util.Date)
         */
        public void put(URI property, Date value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, int)
         */
        public void put(URI property, int value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, long)
         */
        public void put(URI property, long value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, org.ontoware.rdf2go.model.node.Node)
         */
        public void put(URI property, Node value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param property
         * @param value
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#put(org.ontoware.rdf2go.model.node.URI, java.lang.String)
         */
        public void put(URI property, String value) throws UpdateException {
            wrappedContainer.put(property, value);
        }
        /**
         * @param statement
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#remove(org.ontoware.rdf2go.model.Statement)
         */
        public void remove(Statement statement) throws UpdateException {
            wrappedContainer.remove(statement);
        }
        /**
         * @param property
         * @throws UpdateException
         * @see org.semanticdesktop.aperture.rdf.RDFContainer#remove(org.ontoware.rdf2go.model.node.URI)
         */
        public void remove(URI property) throws UpdateException {
            wrappedContainer.remove(property);
        }
    }
}
