/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract crawler implementation that works with an email store implementation hidden behind the Java
 * Mail API. The details about the connections, authentication and security are the responsibility of the
 * concrete subclasses.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJavaMailCrawler extends CrawlerBase {

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// COMMON CONFIGURATION FIELDS //////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected int maxDepth;
    
    protected long maximumByteSize;
    
    protected ArrayList baseFolders = new ArrayList();
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// OTHER FIELDS /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    protected static final String ACCESSED_KEY = "accessed";
    
    private String cachedMessageUrl;

    private Map cachedDataObjectsMap = new HashMap();
 
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// ABSTRACT METHODS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected abstract URI getFolderURI(Folder folder) throws MessagingException;

    protected abstract String getMessageUri(Folder folder, Message message) throws MessagingException;

    protected abstract void recordFolderInAccessData(Folder folder, String url, AccessData newAccessData,
            Message[] messages) throws MessagingException;

    protected abstract Message[] checkIfAFolderHasBeenChanged(Folder folder, String url,
            AccessData newAccessData) throws MessagingException;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// CRAWLING LOGIC ////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected void crawlFolder(Folder folder, int depth) throws MessagingException {
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

        handler.accessingObject(this, folderUrl);

        // see if this object has been encountered before (we must do this before applying the accessor!)
        boolean knownObject = accessData == null ? false : accessData.isKnownId(folderUrl);
        deprecatedUrls.remove(folderUrl);

        RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, folderUrl);

        try {
            FolderDataObject folderObject = getObject(folder, folderUrl, source, accessData, containerFactory);

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
                    handler.objectChanged(this, folderObject);
                    crawlReport.increaseChangedCount();
                }
                else {
                    handler.objectNew(this, folderObject);
                    crawlReport.increaseNewCount();
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

        // determine the set of messages we haven't seen yet, to prevent prefetching the content info for old
        // messages: especially handy when only a few mails were added to a large folder.
        Message[] messages = folder.getMessages();

        if (accessData != null) {
            ArrayList filteredMessages = new ArrayList(messages.length);

            // when messages disappear from the array, we must make sure they are removed from the list of
            // child IDs of the folder
            String folderUriString = folderUri.toString();
            Set deprecatedChildren = accessData.getReferredIDs(folderUriString);
            if (deprecatedChildren == null) {
                deprecatedChildren = Collections.EMPTY_SET;
            }
            else {
                deprecatedChildren = new HashSet(deprecatedChildren);
            }

            // loop over all messages
            for (int i = 0; i < messages.length && !isStopRequested(); i++) {
                MimeMessage message = (MimeMessage) messages[i];
                // this variable was never read, I (Antoni Mylka) commented it out
                //long messageID = getMessageUid(folder, message);

                // determine the uri
                String uri = getMessageUri(folder, message);

                // remove this URI from the set of deprecated children
                deprecatedChildren.remove(uri);

                // see if we've seen this message before
                if (accessData.get(uri, ACCESSED_KEY) == null) {
                    // we haven't: register it for processing if it's not deleted/marked for deletion, etc.
                    if (isAcceptable(message)) {
                        filteredMessages.add(message);
                    }
                }
                else {
                    // we've seen this before: if it's not a removed message, it must be an unmodified message
                    if (isRemoved(message)) {
                        // this message models a deleted or expunged mail: make sure it does no longer appear
                        // as a child data object of the folder
                        accessData.removeReferredID(folderUriString, uri);
                    }
                    else {
                        reportNotModified(getMessageUri(folder, message));
                    }
                }
            }

            // create the subset of messages that we will process
            messages = (Message[]) filteredMessages.toArray(new Message[filteredMessages.size()]);

            // remove all child IDs that we did not encounter in the above loop
            Iterator iterator = deprecatedChildren.iterator();
            while (iterator.hasNext()) {
                String childUri = (String) iterator.next();
                accessData.removeReferredID(folderUriString, childUri);
            }
        }

        if (isStopRequested()) {
            return;
        }

        // pre-fetch content info for the selected messages (assumption: all other info has already been
        // pre-fetched when determining the folder's metadata, no need to prefetch it again)
        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.CONTENT_INFO);
        folder.fetch(messages, profile);

        // crawl every selected message
        for (int i = 0; i < messages.length && !isStopRequested(); i++) {
            MimeMessage message = (MimeMessage) messages[i];
            //this variable was never read, I (Antoni Mylka) commented it out
            //long messageID = getMessageUid(folder, message);
            String uri = getMessageUri(folder, message);

            try {
                if (inDomain(uri)) {
                    crawlMessage(message, uri, folderUri);
                }
            }
            catch (Exception e) {
                // just log these exceptions; as they only affect a single message, they are
                // not considered fatal exceptions
                logger.warn("Exception while crawling message " + uri, e);
            }
        }
    }
    
    private void crawlMessage(MimeMessage message, String uri, URI folderUri) throws MessagingException {
        // see if we should skip this message for some reason
        if (!isAcceptable(message)) {
            return;
        }

        // build a queue of urls to access
        LinkedList queue = new LinkedList();
        // first add the uri of the actual message, so that the message itself is
        // processed first
        queue.add(uri);

        // process the entire queue
        while (!queue.isEmpty()) {
            // fetch the first element in the queue
            String queuedUri = (String) queue.removeFirst();
            handler.accessingObject(this, queuedUri);

            // get this DataObject
            RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, queuedUri);
            try {
                DataObject object = getObject(message, queuedUri, folderUri, source, accessData,
                    containerFactory);

                if (object == null) {
                    // report this object and all its children as unmodified
                    reportNotModified(queuedUri);
                }
                else {
                    // queue all its children
                    queueChildren(object, queue);

                    // register parent child relationship (necessary in order to be able to report
                    // unmodified or deleted attachments)
                    registerParent(object);
                    
                    // attach the message to the parent folder
                    object.getMetadata().add(NIE.isPartOf,folderUri);

                    // TODO this assumption holds for IMAP, but doesn't hold for mbox, nor any other
                    // file-based mailboxes out there, this should be reworked
                    // Report this object as a new object (assumption: objects are always new, never
                    // changed, since mails are immutable).
                    // This MUST happen last because the CrawlerHandler will probably dispose of it.
                    crawlReport.increaseNewCount();
                    handler.objectNew(this, object);
                }
            }
            catch (MessagingException e) {
                // just log and continue with the next url
                logger.warn("MessagingException while processing " + queuedUri, e);
            }
            catch (UrlNotFoundException e) {
                // this is most likely an internal error in the DataObjectFactory or DataAccessor, so log
                // it differently
                logger.error("Internal error while processing " + queuedUri, e);
            }
            catch (IOException e) {
                // just log and continue with the next url
                logger.warn("IOException while processing " + queuedUri, e);
            }
        }
        
        // these lines have been added to make sure that the rare case when two copies of the same message
        // are placed directly after each-other in the same mbox file is treated correctly
        // (meaning - two messages are crawled, but both yield identical sets of triples), the same URI and
        // the same content. Obviously this corrupts the new object count but anyone having a better idea
        // is welcome to share it with the developers of Aperture
        cachedDataObjectsMap.clear();
        cachedMessageUrl = null;
    }
    
    /**
     * Returns a DataObject representing a single message. This data object contains a flattened version
     * of the (arbitrary complex) tree-like MIME structure of the message. This method is called repeatedly
     * for a single MimeMessage. At the first call it creates a cachedDataObjectsMap of all dataObjects that
     * are to be returned from this message. On all subsequent calls DataObjects from this map are returned.
     * 
     * @param message
     * @param url
     * @param folderUri
     * @param dataSource
     * @param newAccessData
     * @param containerFactory
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    protected DataObject getObject(MimeMessage message, String url, URI folderUri, DataSource dataSource,
            AccessData newAccessData, RDFContainerFactory containerFactory) throws MessagingException,
            IOException {
        // See if this url has been accessed before so that we can stop immediately. Note
        // that no check on message date is done as messages are immutable. Therefore we only have to
        // check whether the AccessData knows this ID.
        if (newAccessData != null && newAccessData.get(url, ACCESSED_KEY) != null) {
            return null;
        }

        // determine the root message URL, i.e. remove the fragment identifier
        String messageUrl = url;
        int index = messageUrl.indexOf('#');
        if (index == 0) {
            messageUrl = "";
        }
        else if (index > 0) {
            messageUrl = messageUrl.substring(0, index);
        }

        // see if we have cached the DataObjects obtained from this message
        if (!messageUrl.equals(cachedMessageUrl)) {
            // we haven't: we're going to interpret this message and create all DataObjects at once

            // clear the cache
            cachedMessageUrl = null;
            cachedDataObjectsMap.clear();

            // create DataObjects for the mail and its attachments
            DataObjectFactory factory = new DataObjectFactory();
            List objects = factory
                    .createDataObjects(message, messageUrl, folderUri, dataSource, containerFactory);

            // register the created DataObjects in the cache map
            Iterator iterator = objects.iterator();
            while (iterator.hasNext()) {
                DataObject object = (DataObject) iterator.next();
                cachedDataObjectsMap.put(object.getID().toString(), object);
            }

            // register the message url that these objects came from
            cachedMessageUrl = messageUrl;
        }

        // determine the resulting DataObject
        DataObject result = (DataObject) cachedDataObjectsMap.get(url);
        if (result == null) {
            throw new UrlNotFoundException(url);
        }

        // register the access of this url
        if (newAccessData != null) {
            newAccessData.put(url, ACCESSED_KEY, "");
        }

        return result;
    }

    private void queueChildren(DataObject object, LinkedList queue) {
        Model metadata = object.getMetadata().getModel();

        // query for all child URIs
        ClosableIterator<? extends Statement> statements = null;
        try {
            statements = metadata.findStatements(Variable.ANY, NIE.isPartOf, object.getID());
            // queue these URIs
            while (statements.hasNext()) {
                Statement statement = statements.next();
                Resource resource = statement.getSubject();

                if (resource instanceof URI) {
                    String id = resource.toString();
                    if (!queue.contains(id)) {
                        queue.add(id);
                    }
                }
                else {
                    logger.error("Internal error: unknown child value type: " + resource.getClass());
                }
            }
        }
        catch (ModelRuntimeException me) {
            logger.error("Couldn't queue children", me);
        }
        finally {
            if (statements != null) {
                statements.close();
            }
        }
    }
    
    private void registerParent(DataObject object) {
        if (accessData == null) {
            return;
        }

        URI parent = getParent(object);
        if (parent != null) {
            String parentID = parent.toString();
            String childID = object.getID().toString();

            if (accessData.isKnownId(parentID)) {
                if (parentID.equals(childID)) {
                    logger.error("cyclical " + NIE.isPartOf + " property for " + parentID + ", ignoring");
                }
                else {
                    accessData.putReferredID(parentID, childID);
                }
            }
            else {
                logger.error("Internal error: encountered unknown parent: " + parentID + ", child = "
                        + childID);
            }
        }
    }
    
    private URI getParent(DataObject object) {
        // query for all parents
        Collection parentIDs = object.getMetadata().getAll(NIE.isPartOf);

        // determine all unique parent URIs (the same partOf statement may be returned more than once due
        // to the use of context in the underlying model)
        if (!(parentIDs instanceof Set)) {
            parentIDs = new HashSet(parentIDs);
        }

        // return the parent if there is only one
        if (parentIDs.isEmpty()) {
            return null;
        }
        else if (parentIDs.size() > 1) {
            logger.warn("Multiple parents for " + object.getID() + ", ignoring all");
            return null;
        }
        else {
            Node parent = (Node) parentIDs.iterator().next();
            if (parent instanceof URI) {
                return (URI) parent;
            }
            else {
                logger.error("Internal error: encountered unexpected parent type: " + parent.getClass());
                return null;
            }
        }
    }
    
    /**
     * Returns a DataObject for an JavaMail folder.
     * @param folder
     * @param url
     * @param dataSource
     * @param newAccessData
     * @param containerFactory
     * @return
     * @throws MessagingException
     */
    protected FolderDataObject getObject(Folder folder, String url, DataSource dataSource, AccessData newAccessData,
            RDFContainerFactory containerFactory) throws MessagingException {
        // See if this url has been accessed before and hasn't changed in the mean time.
        // A check for the next UID guarantees that no mails have been added (see RFC 3501).
        // If this is still the same, a check on the number of messages guarantees that no mails have
        // been removed either. Finally, we check that it has the same set of subfolders
        Folder imapFolder = folder;
        Message[] messages = null;

        // check if the folder has changed
        messages = checkIfAFolderHasBeenChanged(imapFolder, url, newAccessData);
        if (messages == null && newAccessData != null) {
            // this means that this folder has not been changed and null can be returned
            return null;
        }
        
        // register the folder's name
        URI folderURI = new URIImpl(url);
        RDFContainer metadata = containerFactory.getRDFContainer(folderURI);
        metadata.add(NIE.title, folder.getName());

        // register the folder's parent
        Folder parent = folder.getParent();
        if (parent != null) {
            metadata.add(NIE.isPartOf, getFolderURI(parent));
            // this is needed to satiate the validator, otherwise errors about missing type
            // occur for the rootFolder begin a part of some non-crawled folder
            metadata.getModel().addStatement(getFolderURI(parent),RDF.type,NFO.Folder);
        }

        if (holdsMessages(folder)) {
            if (messages == null) {
                messages = folder.getMessages();
            }

            // for faster access, prefetch all message UIDs and flags
            FetchProfile profile = new FetchProfile();
            profile.add(UIDFolder.FetchProfileItem.UID); // needed for message.getUID
            profile.add(FetchProfile.Item.FLAGS); // needed for isAcceptable (DELETED)
            profile.add(FetchProfile.Item.ENVELOPE); // needed for isAcceptable (size check)
            folder.fetch(messages, profile);

            for (int i = 0; i < messages.length; i++) {
                MimeMessage message = (MimeMessage) messages[i];

                if (isAcceptable(message)) {
                    //this variable wasn't used, I commented it out (Antoni Mylka)
                    //long messageID = getMessageUid(imapFolder, message); 
                    try {
                        URI messageURI = metadata.getModel().createURI(getMessageUri(folder,message));
                        metadata.getModel().addStatement(messageURI, NIE.isPartOf, folderURI);
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
        Folder[] subFolders = folder.list();
        for (int i = 0; i < subFolders.length; i++) {
            Folder subFolder = subFolders[i];
            if (subFolder.exists()) {
                metadata.add(metadata.getValueFactory().createStatement(getFolderURI(subFolder), NIE.isPartOf,
                    folderURI));
            }
        }

        recordFolderInAccessData(folder, url, newAccessData, messages);

        // if this is a base folder then add some metadata
        if (baseFolders.contains(folder.getFullName())) {
            metadata.add(NIE.rootElementOf, dataSource.getID());
        }

        // create the resulting FolderDataObject instance
        return new FolderDataObjectBase(folderURI, dataSource, metadata);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// UTILITY METHODS ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected long getMessageUid(Folder folder, Message message) throws MessagingException {
        if (folder instanceof UIDFolder) {
            return ((UIDFolder)folder).getUID(message);
        } else {
            return -1;
        }
    }
    
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
    
    protected String getSubFoldersString(Folder folder) throws MessagingException {
        StringBuilder buffer = new StringBuilder();

        Folder[] subFolders = folder.list();
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
    
    private void reportNotModified(String uri) {
        // report this object as unmodified
        crawlReport.increaseUnchangedCount();
        handler.objectNotModified(this, uri);
        deprecatedUrls.remove(uri);

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

    private boolean isRemoved(Message message) throws MessagingException {
        return message.isExpunged() || message.isSet(Flags.Flag.DELETED);
    }

    private boolean isTooLarge(Message message) throws MessagingException {
        return message.getSize() > maximumByteSize;
    }

    private boolean isAcceptable(Message message) throws MessagingException {
        return !(isRemoved(message) || isTooLarge(message));
    }

    
    /**
     * Does this folder hold any subfolders?
     * @param folder the folder to be checked
     * @return true if this folder has any subfolders, false otherwise
     * @throws MessagingException if it prooves impossible to find out
     */
    public static boolean holdsFolders(Folder folder) throws MessagingException {
        return (folder.getType() & Folder.HOLDS_FOLDERS) == Folder.HOLDS_FOLDERS;
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
}
