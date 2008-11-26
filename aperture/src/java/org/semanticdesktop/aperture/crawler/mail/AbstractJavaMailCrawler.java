/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.MessageDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.accessor.base.MessageDataObjectBase;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.crawler.mail.DataObjectFactory.PartStreamFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract crawler implementation that works with an email store implementation hidden behind the Java
 * Mail API. <br/><br/>
 * 
 * The details about the connection management, authentication and security are the responsibility of the
 * concrete subclasses. 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJavaMailCrawler extends CrawlerBase implements DataObjectFactory.PartStreamFactory {

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// COMMON CONFIGURATION FIELDS //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // these fields appear in the configuration of all mailbox-related data sources 
    
    /** Maximum depth below the base folders the crawler will crawl */
    protected int maxDepth;
    
    /** Maximum size of the message accepted by the crawler, bigger messages will be ignored */
    protected long maximumByteSize;
    
    /** List of base folders - roots of the crawling */
    protected ArrayList baseFolders = new ArrayList();
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// OTHER FIELDS /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** The logger */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /** The key used in the access data to mark if a given data object has been accessed or not */
    public static final String ACCESSED_KEY = "accessed";
    
    /** 
     * The folder currently crawled by the crawler. 
     * @see #setCurrentFolder(Folder) 
     */
    protected Folder currentFolder;
    
    /**
     * The URI of the current folder. It is set by the {@link #setCurrentFolder(Folder)} using the
     * {@link #getFolderURI(Folder)} implementation.
     * @see #setCurrentFolder(Folder) 
     */
    protected URI currentFolderURI;
    
    /**
     * An executor service for the MessageDataObjects.
     */
    protected ExecutorService executorService;
 
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// ABSTRACT METHODS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the URI of the folder, using the URI scheme appropriate for the current crawler.
     * @param folder the Folder whose URI we'd like to obtain.
     * @return the uri of the folder
     * @throws MessagingException
     */
    protected abstract URI getFolderURI(Folder folder) 
            throws MessagingException;

    /**
     * Returns the URI of the message, using the URI scheme appropriate for the current crawler.
     * @param folder the folder where the message resides
     * @param message the message itself
     * @return the uri of the message
     * @throws MessagingException
     */
    protected abstract String getMessageUri(Folder folder, Message message) 
            throws MessagingException;
    
    /**
     * Applies source-specific methods to determine if the current folder has been changed since it has last
     * been crawled.
     * 
     * @param newAccessData the AccessData instance that is to be consulted
     * @return false if the information stored in the accessData instance indictates that the folder hasn't
     *         been changed, false otherwise
     * @throws MessagingException
     */
    protected abstract boolean checkIfCurrentFolderHasBeenChanged(AccessData newAccessData)
            throws MessagingException;

    /**
     * Records source-specific information about the current folder that will enable the crawler to detect if
     * the crawler has been changed on a future crawl.
     * 
     * @param newAccessData the access data where the information should be stored
     * @throws MessagingException
     */
    protected abstract void recordCurrentFolderInAccessData(AccessData newAccessData)
            throws MessagingException;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// PROTECTED METHODS - OPEN FOR OPTIMIZATIONS //////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sets the current folder. Implementations are free to perform any optimizations at this point (like
     * prefetching). This method is called AFTER the folder is opened ({@link Folder#open(int)}) but 
     * before any messages are actually crawled.
     * 
     * @param folder the folder that is to become the current folder
     * @throws MessagingException 
     */
    protected void setCurrentFolder(Folder folder)  
        throws MessagingException {
        this.currentFolder = folder;
        this.currentFolderURI = getFolderURI(folder);
    }
    
    /**
     * Returns the message from the current folder available at the given index. Note that the exact semantics
     * of the index may be overridden by the subclasses of this class, but it will always follow the javamail
     * convention that folder indexes are one-based
     * 
     * @param index a one-based index. The lowest valid value is one (obviously) the highest valid value is
     *            the one returned by {@link #getCurrentFolderMessageCount()}
     * @return the message placed under the given index
     * @throws MessagingException
     */
    protected Message getMessageFromCurrentFolder(int index) throws MessagingException {
        return currentFolder.getMessage(index);
    }
    
    /**
     * Returns the amount of messages in the current folder. 
     * @return the amount of messages in the current folder.
     * @throws MessagingException
     */
    protected int getCurrentFolderMessageCount() throws MessagingException {
        return currentFolder.getMessageCount();
    }
    
    /**
     * @see PartStreamFactory#getPartStream(Part)
     */
    public InputStream getPartStream(Part part) throws MessagingException, IOException {
        return part.getInputStream();
    }
    
    /**
     * @throws MessagingException 
     * @see PartStreamFactory#createDataObject(URI, DataSource, RDFContainer, MimeMessage, ExecutorService)
     */
    public MessageDataObject createDataObject(URI dataObjectId, DataSource dataSource, RDFContainer metadata,
            MimeMessage msg, ExecutorService executorService) throws MessagingException {
        return new MessageDataObjectBase(dataObjectId, dataSource, metadata, msg, executorService);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// CRAWLING LOGIC ////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * Crawls a subfolder tree starting at the given folder up until the given depth. This method is to 
     * be called by the subclasses after setting up all connection parameters.
     * @param folder the folder where the crawl should be started
     * @param depth how deep should the crawl proceed <br/>
     *    <ul>
     *    <li>-1 - unlimited depth 
     *    <li>0 or 1 - only the given folder will be crawled 
     *    <li>2 - only the given folder and it's direct subfolders 
     *    </ul>
     * @throws MessagingException
     */
    protected final void crawlFolder(Folder folder, int depth) throws MessagingException {
        if (isStopRequested()) {
            return;
        }

        // skip if there is a problem
        if (folder == null) {
            logger.debug("passed null folder, ignoring");
            return;
        }
        else if (!folder.exists()) {
            logger.debug("folder does not exist: \"" + folder.getFullName() + "\"");
            return;
        }

        // crawl the folder and its messages, if any
        logger.debug("crawling folder \"" + folder.getFullName() + "\"");
        crawlSingleFolder(folder);

        // crawl its subfolders, if any and when allowed
        if (holdsFolders(folder)) {
            logger.debug("crawling subfolders in folder \"" + folder.getFullName() + "\"");
            crawlSubFolders(folder, depth);
        }

        if (folder.isOpen()) {
            // close the folder without deleting expunged messages
            folder.close(false);
        }
    }
    
    private void crawlSingleFolder(Folder folder) throws MessagingException {
        // open the folder in read-only mode
        if (holdsMessages(folder) && !folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }
        
        // report the folder's metadata
        String folderUrl = getFolderURI(folder).toString();

        if (!inDomain(folderUrl)) {
            // This gives us different semantics to domainboundaries than the filecrawler,
            // which will still process sub-folder/files when something is not in the domain,
            // however, i think that's wrong :) - (says Gunnar)
            return;
        }

        // set the current folder
        setCurrentFolder(folder);

        //handler.accessingObject(this, folderUrl);
        reportAccessingObject(folderUrl);

        // see if this object has been encountered before (we must do this before applying the accessor!)
        boolean knownObject = accessData == null ? false : accessData.isKnownId(folderUrl);
        //deprecatedUrls.remove(folderUrl);

        //RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, folderUrl);
        RDFContainerFactory containerFactory = getRDFContainerFactory(folderUrl);

        try {
            FolderDataObject folderObject = getCurrentFolderObject(source, accessData, containerFactory);

            if (isStopRequested()) {
                return;
            }

            if (folderObject == null) {
                // report this folder and all its messages as unchanged
                reportNotModified(folderUrl);
            }
            else {
                // report this new or changed folder
                if (knownObject) {
                    //handler.objectChanged(this, folderObject);
                    //crawlReport.increaseChangedCount();
                    reportModifiedDataObject(folderObject);
                }
                else {
                    //handler.objectNew(this, folderObject);
                    //crawlReport.increaseNewCount();
                    reportNewDataObject(folderObject);
                }

                // (re-)crawl its messages
                if (holdsMessages(folder)) {
                    crawlMessages(folder, folderObject.getID());
                }
            }
        }
        catch (MessagingException e) {
            // just log this exception and continue, perhaps the messages can still be accessed
            logger.warn("Exception while crawling folder " + folderUrl, e);
        }
    }

    private void crawlSubFolders(Folder folder, int depth) {
        if (depth + 1 > maxDepth && maxDepth >= 0) {
            logger.debug("Reached crawling depth limit (" + maxDepth + ") - stopping.");
            return;
        }

        try {
            Folder[] subFolders = folder.list();
            logger.debug("Crawling " + subFolders.length + " sub-folders.");
            for (int i = 0; !isStopRequested() && i < subFolders.length; i++) {
                try {
                    crawlFolder(subFolders[i], depth + 1);
                }
                catch (MessagingException e) {
                    logger.debug("Error crawling subfolder \"" + subFolders[i].getFullName() + "\"");
                    // but continue..
                }
            }
        }
        catch (MessagingException e) {
            logger.warn("Exception while crawling subFolders of \"" + folder.getFullName() + "\"", e);
        }
    }
    
    private void crawlMessages(Folder folder, URI folderUri) throws MessagingException {
        if (isStopRequested()) {
            return;
        }

        logger.debug("Crawling messages in folder " + folder.getFullName());

        // crawl every selected message
        int messageCount = getCurrentFolderMessageCount();
        for (int i = 1; i <= messageCount && !isStopRequested(); i++) {
            MimeMessage message = (MimeMessage) getMessageFromCurrentFolder(i);
            //this variable was never read, I (Antoni Mylka) commented it out
            //long messageID = getMessageUid(folder, message);
            String uri = getMessageUri(folder, message);
            try {
                if (inDomain(uri)) {
                    crawlSingleMessage(message, uri, folderUri);
                }
            }
            catch (Exception e) {
                // just log these exceptions; as they only affect a single message, they are
                // not considered fatal exceptions
                logger.warn("Exception while crawling message " + uri, e);
            }
        }
    }
    
    private void crawlSingleMessage(MimeMessage message, String uri, URI folderUri) throws MessagingException, IOException {
        // see if we should skip this message for some reason
        if (!isAcceptable(message)) {
            return;
        }

        DataObjectFactory dataObjectFactory = null;
        try {
            // construct a data object factory for this message, it will parse the message and prepare a list
            // of data objects we'll get one by one
            dataObjectFactory = new DataObjectFactory(message, getRDFContainerFactory(uri), executorService,
                this, this.getDataSource(), new URIImpl(uri), folderUri);
            
            // get all the objects from the factory and report them to the AccessData
            DataObject object = null;
            
            /*
             * Note that the isStopRequested() check is BEFORE getObject(). Otherwise if the crawler is
             * stopped the object is obtained and only AFTER this loop is stopped. This object is not
             * disposed by the dataObjectFactory.disposeRemainingObjects() in the finally clause, yields a
             * warning message and can potentially lead to problems.
             */
            while (!isStopRequested() && (object = dataObjectFactory.getObject()) != null) {
                // first of all get a string version of the message uri
                String queuedUri = object.getID().toString();
                // notify the crawler handler that we're accessing this object
                reportAccessingObject(queuedUri);            
                    
                // See if this url has been accessed before so that we can stop immediately. Note
                // that no check on message date is done as messages are immutable. Therefore we only have to
                // check whether the AccessData knows this ID.
                if (accessData != null && accessData.get(queuedUri, ACCESSED_KEY) != null) {
                    // report this object and all its children as unmodified, but before you do it, dispose
                    // the data object
                    object.dispose();
                    reportNotModified(queuedUri);
                    continue;
                }
    
                // store the information in the access data that we have met this object, not to crawl it
                // in future
                if (accessData != null) {
                    accessData.put(queuedUri, ACCESSED_KEY, "");
                }
    
                // register parent child relationship (necessary in order to be able to report
                // unmodified or deleted attachments). This relationship is recorded in the accessdata
                // no new information is added to the objects metadata RDFContainer
                MailUtil.registerParentRelationshipInAccessData(object, accessData);
                
                // attach the message to the parent folder
                object.getMetadata().add(NIE.isPartOf,folderUri);
    
                // Report this object as a new object (assumption: objects are always new, never
                // changed, since mails are immutable).
                // This MUST happen last because the CrawlerHandler will probably dispose of it.
                reportNewDataObject(object);    
            }
        } finally {
            if (dataObjectFactory != null) {
                dataObjectFactory.disposeRemainingObjects();
            }
        }
    }

    /**
     * Returns a DataObject for the current JavaMail folder.
     * @param dataSource
     * @param newAccessData
     * @param containerFactory
     * @return a FolderDataObject instance for the currentFolder
     * @throws MessagingException
     */
    protected FolderDataObject getCurrentFolderObject(DataSource dataSource, AccessData newAccessData,
            RDFContainerFactory containerFactory) throws MessagingException {
        // See if this url has been accessed before and hasn't changed in the mean time.
        // A check for the next UID guarantees that no mails have been added (see RFC 3501).
        // If this is still the same, a check on the number of messages guarantees that no mails have
        // been removed either. Finally, we check that it has the same set of subfolders
        //Folder imapFolder = folder;
        //Message[] messagesInFolder = null;

        // check if the folder has changed
        boolean folderChanged = checkIfCurrentFolderHasBeenChanged(newAccessData);
        if (!folderChanged && newAccessData != null) {
            // this means that this folder has not been changed and null can be returned
            return null;
        }
        
        // register the folder's name
        RDFContainer metadata = containerFactory.getRDFContainer(currentFolderURI);
        metadata.add(NIE.title, currentFolder.getName());

        // register the folder's parent
        Folder parent = currentFolder.getParent();
        if (parent != null) {
            metadata.add(NIE.isPartOf, getFolderURI(parent));
            // this is needed to satiate the validator, otherwise errors about missing type
            // occur for the rootFolder begin a part of some non-crawled folder
            metadata.getModel().addStatement(getFolderURI(parent),RDF.type,NFO.Folder);
        }

        if (holdsMessages(currentFolder)) {
            //if (messages == null) {
            //    messages = folder.getMessages();
            //}

            int messageCount = getCurrentFolderMessageCount();
            for (int i = 1; i <= messageCount; i++) {
                MimeMessage message = (MimeMessage) getMessageFromCurrentFolder(i);
                //System.out.println(currentFolder.getName() + ":" + i + ":" + message.getSubject());
                if (isAcceptable(message)) {
                    //this variable wasn't used, I commented it out (Antoni Mylka)
                    //long messageID = getMessageUid(imapFolder, message); 
                    try {
                        URI messageURI = metadata.getModel().createURI(getMessageUri(currentFolder,message));
                        metadata.getModel().addStatement(messageURI, NIE.isPartOf, currentFolderURI);
                        // This is needed to satiate the validator, otherwise if an email falls beyond
                        // the domain boundaries, the validator will complain about the missing type triple
                        metadata.getModel().addStatement(messageURI, RDF.type, NMO.MailboxDataObject);
                    }
                    catch (ModelRuntimeException e) {
                        logger.error("ModelException while creating URI", e);
                    }
                }
            }
        }

        // add subfolder URIs
        Folder[] subFolders = currentFolder.list();
        for (int i = 0; i < subFolders.length; i++) {
            Folder subFolder = subFolders[i];
            if (subFolder.exists()) {
                metadata.add(metadata.getValueFactory().createStatement(getFolderURI(subFolder), NIE.isPartOf,
                    currentFolderURI));
            }
        }

        recordCurrentFolderInAccessData(newAccessData);

        // if this is a base folder then add some metadata
        if (baseFolders.contains(currentFolder.getFullName())) {
            metadata.add(NIE.rootElementOf, dataSource.getID());
        }

        // create the resulting FolderDataObject instance
        return new FolderDataObjectBase(currentFolderURI, dataSource, metadata);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// UTILITY METHODS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the UID of the message.
     * 
     * @param folder the folder where the message is located
     * @param message the message whose UID we want to fetch
     * @return the UID of the message. This method may return -1 if the message doesn't have an UID or if the
     *         UID could not be obtained.
     * @throws MessagingException
     */
    protected long getMessageUid(Folder folder, Message message) throws MessagingException {
        if (folder instanceof UIDFolder) {
            return ((UIDFolder)folder).getUID(message);
        } else {
            return -1;
        }
    }
    
    /**
     * Returns the amount of non-removed messages in the given array. Each message in the array is checked
     * with the {@link #isRemoved(Message)} method.
     * 
     * @param messages the array of messages we'd like to check
     * @return the number of messages that have not been marked as removed on the server
     * @throws MessagingException
     */
    protected int getMessageCount(Message[] messages) throws MessagingException {
        int result = 0;

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            if (!isRemoved(message)) {
                result++;
            }
        }

        return result;
    }
    
    /**
     * Returns a string with the names of the subfolders of the given folder. This string may be used to
     * record the state of the folder in the AccessData instance.
     * 
     * @param folder
     * @return a string with the names of the subfolders of the given folder separated with the @ sign
     * @throws MessagingException
     */
    protected String getSubFoldersString(Folder folder) throws MessagingException {
        Folder[] subFolders = folder.list();
        if (subFolders.length == 0) {
            return null;
        }
        
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < subFolders.length; i++) {
            Folder subFolder = subFolders[i];
            if (subFolder.exists()) {
                buffer.append(subFolder.getFullName());

                if (i < subFolders.length - 1) {
                    buffer.append('@');
                }
            }
        }

        return buffer.toString();
    }
    
    /**
     * Reports the given uri as unmodified. This method calls {@link #reportUnmodifiedDataObject(String)} and
     * updates data structures internal to the AbstractJavaMailCrawler
     * 
     * @param uri the uri to be reported as unmodified
     */
    protected void reportNotModified(String uri) {
        // report this object as unmodified
        reportUnmodifiedDataObject(uri);

        // repeat recursively on all registered children
        if (accessData == null) {
            logger.error("Internal error: reporting unmodified uri while no AccessData is set: " + uri);
        }
        else {
            Set children = accessData.getReferredIDs(uri);
            if (children != null) {
                Iterator iterator = children.iterator();
                while (iterator.hasNext()) {
                    reportNotModified((String) iterator.next());
                }
            }
        }
    }

    /**
     * Returns true if the given message has been marked as expunged or deleted
     * 
     * @param message the message to check
     * @return true if the given message has been marked as expunged or deleted
     * @throws MessagingException
     */
    protected boolean isRemoved(Message message) throws MessagingException {
        return message.isExpunged() || message.isSet(Flags.Flag.DELETED);
    }

    /**
     * Returns true if this message is larger than the maximum size defined for the current data source.
     * 
     * @param message the message to check
     * @return true if this message is larger than the maximum size defined for the current data source.
     * @throws MessagingException
     */
    protected boolean isTooLarge(Message message) throws MessagingException {
        return message.getSize() > maximumByteSize;
    }

    /**
     * Returns true if this message can be crawled, according to the criteria defined for this d data source.
     * 
     * @param message the message to check
     * @return true if this message can be crawled, according to the criteria defined for this d data source.
     * @throws MessagingException
     */
    protected boolean isAcceptable(Message message) throws MessagingException {
        return !(isRemoved(message) || isTooLarge(message));
    }

    
    /**
     * Does this folder hold any subfolders?
     * @param folder the folder to be checked
     * @return true if this folder has any subfolders, false otherwise
     * @throws MessagingException if it prooves impossible to find out
     */
    public static boolean holdsFolders(Folder folder) throws MessagingException {
        // this if has been added during the work on issue 2005759
        // gmail returns wrong type, it is necessary to call list() to determine
        // if a folder actually contains subfolders
        if ((folder.getType() & Folder.HOLDS_FOLDERS) == Folder.HOLDS_FOLDERS) {
            return folder.list().length > 0;
        } else {
            // this means that the folder can't have any subfolders "by definition"
            return false;
        }
    }

    /**
     * Does this folder hold any messages?
     * @param folder the folder to be checked
     * @return true if this folder has any messages, false otherwise
     * @throws MessagingException if it prooves impossible to find out
     */
    public static boolean holdsMessages(Folder folder) throws MessagingException {
        return (folder.getType() & Folder.HOLDS_MESSAGES) == Folder.HOLDS_MESSAGES;
    }
    
// cementery for the code, might still come in handy sometime
    
//  Commented out by Antoni in the course of working on 1779556, after the refactoring of the
//  DataSourceFactory this method is not needed anymore
//  private void queueChildren(DataObject object, LinkedList queue) {
//      Model metadata = object.getMetadata().getModel();
//
//      // query for all child URIs
//      ClosableIterator<? extends Statement> statements = null;
//      try {
//          statements = metadata.findStatements(Variable.ANY, NIE.isPartOf, object.getID());
//          // queue these URIs
//          while (statements.hasNext()) {
//              Statement statement = statements.next();
//              Resource resource = statement.getSubject();
//
//              if (resource instanceof URI) {
//                  String id = resource.toString();
//                  if (!queue.contains(id)) {
//                      queue.add(id);
//                  }
//              }
//              else {
//                  logger.error("Internal error: unknown child value type: " + resource.getClass());
//              }
//          }
//      }
//      catch (ModelRuntimeException me) {
//          logger.error("Couldn't queue children", me);
//      }
//      finally {
//          if (statements != null) {
//              statements.close();
//          }
//      }
//  }
}
