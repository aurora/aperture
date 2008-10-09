/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.mail.AbstractJavaMailCrawler;
import org.semanticdesktop.aperture.crawler.mail.DataObjectFactory;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource.ConnectionSecurity;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManager;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;

/**
 * A Combined Crawler and DataAccessor implementation for IMAP.
 * 
 * <p>
 * Note that the same instance of ImapCrawler cannot be used as a crawler and as a DataAccessor at the same
 * time. Please use separate instances, or use the appropriate factory, which will enforce this for you.
 * </p>
 * 
 * <p>
 * A known issue: the incremental crawling only works correctly for servers that persist UIDs for each folder.
 * Otherwise each crawl will start from scratch and report all objects as new. This occurs on IMAP servers
 * backed by the 'mh' message storage mechanism. See 
 * <a href="http://sourceforge.net/mailarchive/forum.php?thread_name=97d19f3c0809181546g3d817e28j82dd928960a166e4%40mail.gmail.com&forum_name=aperture-devel">
 * this email to aperture-dev </a> 
 * and <a href=http://mailman2.u.washington.edu/pipermail/imap-protocol/2007-March/000436.html"> 
 * this post from Marc Crispin - inventor of IMAP</a> for more details.
 * </p>
 */
@SuppressWarnings("unchecked")
public class ImapCrawler extends AbstractJavaMailCrawler implements DataAccessor {

    private static final String IMAP_URL_SCHEME = "imap://";
    
    private static final String NEXT_UID_KEY = "nextuid";

    private static final String SIZE_KEY = "size";

    private static final String SUBFOLDERS_KEY = "subfolders";
    
    private static final String UID_VALIDITY = "uidValidity";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /*
     * The source whose properties we're currently using.
     * 
     * A separate DataSource is necessary as the DataAccessor implementation may be passed a different
     * DataSource. this is the data source instance that served as the basis for setting the values of all
     * other configuration fields, when this class is used as a Crawler, this is equal (==) to the source
     * obtained by getDataSource(), actually this value is obtained by calling getDataSource()
     * 
     * when this class is used as an accessor, this is equal to the last data source instance passed to the
     * getDataObject(ifModified) methods, it is obtained from outside. This is the second source where this
     * reference might come from.
     * 
     * The instance returned by the getDataSource() method is never directly used in this class.
     */  
    private ImapDataSource configuredDataSource;

    /*
     * A Property instance holding *extra* properties to use when a Session is initiated. This can be used in
     * apps running on Java < 5.0 to instruct to use a different SocketFactory, when you don't want to
     * communicate this via the system properties
     */
    private Properties sessionProperties;

    private String hostName;

    private int port;

    private String userName;

    private String password;

    private String connectionType;

    private boolean ignoreSSLCertificates = false;

    private boolean useSSLCertificateFile = false;

    private String SSLCertificateFile;

    private String SSLCertificatePassword;

    private boolean includeInbox;

    private Store store;
    private ImapStreamPool streamPool;
    
    /* ----------------- Fields managed by the setCurrentFolder method ----------------- */
    
    private Message [] currentMessages;
    /** Flag set by the {@link #setCurrentFolder(Folder)} method if the current folder has been changed */
    private boolean currentFolderChanged;

    /* ----------------------------- Crawler implementation ----------------------------- */

    protected ExitCode crawlObjects() {
        // determine host name, user name, etc.
        retrieveConfigurationData(getDataSource());

        
        // make sure we have a connection to the mail store
        try {
            ensureConnectedStore();
        }
        catch (MessagingException e) {
            logger.warn("Unable to connect to IMAP mail store", e);
            closeConnection();
            return ExitCode.FATAL_ERROR;
        }

        // crawl the folder contents
        boolean fatalError = false;

        try {
            // crawl all specified base folders
            int nrFolders = baseFolders.size();
            if (nrFolders == 0) {
                crawlFolder(store.getDefaultFolder(), 0);
            }
            else {
                for (int i = 0; i < nrFolders; i++) {
                    String baseFolderName = (String) baseFolders.get(i);
                    Folder baseFolder = store.getFolder(baseFolderName);
                    crawlFolder(baseFolder, 0);
                }
            }

            // The inbox is a magic folder - include it if config option set.
            if (includeInbox) {
                crawlFolder(store.getFolder("INBOX"), 0);
            }
        }
        catch (MessagingException e) {
            logger.warn("MessagingException while crawling", e);
            fatalError = true;
        }

        // terminate the connection
        closeConnection();

        // determine the correct exit code
        if (fatalError) {
            return ExitCode.FATAL_ERROR;
        }
        else if (isStopRequested()) {
            return ExitCode.STOP_REQUESTED;
        }
        else {
            return ExitCode.COMPLETED;
        }
    }

    /**
     * Prepare for accessing the specified DataSource by fetching all properties from it that are required to
     * connect to the mail box.
     */
    private void retrieveConfigurationData(DataSource dataSource) {
        // see if we have already configured for this source
        if (dataSource == configuredDataSource) {
            return;
        }

        // retrieve the DataSource's configuration object
        configuredDataSource = (ImapDataSource)dataSource;

        // fetch some trivial settings
        hostName = configuredDataSource.getHostname();
        userName = configuredDataSource.getUsername();
        password = configuredDataSource.getPassword();

        port = -1;
        Integer configuredPort = configuredDataSource.getPort();
        if (configuredPort != null) {
            port = configuredPort.intValue();
        }

        baseFolders.clear();
        baseFolders.addAll(configuredDataSource.getAllBasepaths());

        Boolean includeInboxB = configuredDataSource.getIncludeInbox();
        if (includeInboxB == null) {
            includeInbox = false;
        }
        else {
            includeInbox = includeInboxB.booleanValue();
        }

        Integer maxDepthI = configuredDataSource.getMaximumDepth();

        if (maxDepthI == null) {
            maxDepth = -1;
        }
        else {
            maxDepth = maxDepthI.intValue();
        }

        // determine the connection type
        ConnectionSecurity securityType = configuredDataSource.getConnectionSecurity();
        if (securityType == null || securityType.equals(ConnectionSecurity.PLAIN)) {
            connectionType = "imap";
        }
        else if (securityType.equals(ConnectionSecurity.SSL)
                || securityType.equals(ConnectionSecurity.SSL_NO_CERT)) {
            connectionType = "imaps";
        }
        else {
            throw new IllegalArgumentException("Illegal connection security type: " + securityType);
        }

        if (securityType != null && securityType.equals(ConnectionSecurity.SSL_NO_CERT)) {
            ignoreSSLCertificates = true;
        }

        if (configuredDataSource.getSslFileName() != null) {
            useSSLCertificateFile = true;
            SSLCertificateFile = configuredDataSource.getSslFileName();
            SSLCertificatePassword = configuredDataSource.getSslFilePassword();
        }

        // determine the maximum byte size
        Long maximumSize = configuredDataSource.getMaximumSize();
        if (maximumSize == null) {
            maximumByteSize = Long.MAX_VALUE;
        }
        else {
            maximumByteSize = maximumSize.longValue();
        }

        // make sure we get rid of any store that may relate to an older configuration
        if (store != null) {
            closeConnection();
            store = null;
        }
    }

    private void ensureConnectedStore() throws MessagingException {
        // if there is no store yet, create one now
        if (store == null) {
            // get all system properties
            Properties properties = System.getProperties();

            if (ignoreSSLCertificates) {
                properties.setProperty("mail.imaps.socketFactory.class", SimpleSocketFactory.class.getName());
                properties.setProperty("mail.imaps.socketFactory.fallback", "false");
            }

            if (useSSLCertificateFile) {
                properties.setProperty("javax.net.ssl.trustStore", SSLCertificateFile);
                properties.setProperty("javax.net.ssl.trustStorePassword", SSLCertificatePassword);
            }

            // copy all extra registered session properties
            if (sessionProperties != null) {
                Enumeration keys = sessionProperties.elements();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = sessionProperties.getProperty(key);
                    properties.setProperty(key, value);
                }
            }

            Session session = Session.getDefaultInstance(properties);
            store = session.getStore(connectionType);
        }

        // make sure it is connected
        if (!store.isConnected()) {
            store.connect(hostName, port, userName, password);
        }
        this.streamPool = new ImapStreamPool(store);
    }

    /**
     * Requests the streamPool to close the connection to the store.
     */
    public void closeConnection() {
        if (streamPool != null) {
            streamPool.requestClose();
        }
    }
    

    /* ----------------------------- DataAccessor implementation ----------------------------- */

    /**
     * @see DataAccessor#getDataObject(String, DataSource, Map, RDFContainerFactory)
     */
    public DataObject getDataObject(String url, DataSource dataSource, Map params,
            RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        return getDataObjectIfModified(url, dataSource, null, params, containerFactory);
    }

    /**
     * @see DataAccessor#getDataObjectIfModified(String, DataSource, AccessData, Map, RDFContainerFactory)
     */
    public DataObject getDataObjectIfModified(String url, DataSource dataSource, AccessData newAccessData,
            Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        
        // we assume that emails are immutable, so if we know this url we don't need to do anything, so it's
        // best to perform this check right at the beginning of this method
        if (newAccessData != null && newAccessData.get(url, ACCESSED_KEY) != null) {
            return null;
        }
        
        // reconfigure the crawler for the specified DataSource if necessary
        
        retrieveConfigurationData(dataSource); // Rem: this method modifies fields

        try {
            // make sure we have a connection to the mail store
            ensureConnectedStore(); // Rem: this method modifies fields

            // retrieve the specified folder
            String folderName = getFolderName(url);

            Folder folder;
            if (folderName == null || folderName.equals("")) {
                folder = store.getDefaultFolder();
            }
            else {
                folder = store.getFolder(folderName);
            }

            if (!folder.exists()) {
                throw new UrlNotFoundException(url, "unknown folder");
            }

            if (!folder.isOpen() && holdsMessages(folder)) {
                folder.open(Folder.READ_ONLY);
            }
            
            // note that we use this.setCurrentFolder() because we don't need prefetching
            // in this particular case, super.setSetCurrentFolder() suffices
            super.setCurrentFolder(folder);
            
            // see if we need to process a folder or a message
            int typeIndex = url.indexOf(";TYPE=");
            if (typeIndex < 0) {
                // determine the message UID: cutoff the ID part
                int separatorIndex = url.lastIndexOf("/;UID=");
                separatorIndex = Math.max(separatorIndex, url.lastIndexOf("/;uid="));
                if (separatorIndex < 0 || separatorIndex >= url.length() - 6) {
                    throw new IllegalArgumentException("unable to get message UID from " + url);
                }
                String messageNumberString = url.substring(separatorIndex + 6);

                // remove the fragment identifier
                separatorIndex = messageNumberString.indexOf('#'); // fragment identifier that we use
                separatorIndex = Math.max(separatorIndex, messageNumberString.indexOf('/')); // RFC 2192
                if (separatorIndex > 0 && separatorIndex < messageNumberString.length() - 1) {
                    messageNumberString = messageNumberString.substring(0, separatorIndex);
                }

                // parse the remaining number string
                long messageUID;
                try {
                    messageUID = Long.parseLong(messageNumberString);
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("illegal message UID: " + messageNumberString);
                }

                // retrieve the message with this UID
                MimeMessage message = (MimeMessage) ((UIDFolder) folder).getMessageByUID(messageUID);

                if (message == null) {
                    throw new UrlNotFoundException("unknown UID: " + messageUID);
                }
                
                DataObjectFactory fac = new DataObjectFactory(message, containerFactory, this, dataSource,
                        new URIImpl(getMessageUri(folder, message)), getFolderURI(folder));
                
                // create a DataObject for the requested message or message part
                return fac.getObjectAndDisposeAllOtherObjects(url); 
            }
            else {
                // create a DataObject for this Folder
                return getCurrentFolderObject(dataSource, newAccessData, containerFactory);
            }
        }
        catch (MessagingException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        } finally {
            // at the very end, we may request the store to be closed
            closeConnection();
        }
    }
    
    /**
     * This method implements the incremental crawling strategy described by Chris Fluit in the
     * sourceforge issue 1531657.<br/><br/>
     * 
     * See <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=1531657&group_id=150969&atid=779500">
     * http://sourceforge.net/tracker/index.php?func=detail&aid=1531657&group_id=150969&atid=779500</a>
     * 
     */
    @Override
    protected void setCurrentFolder(Folder folder) throws MessagingException {
        super.setCurrentFolder(folder);
        
        if (!holdsMessages(folder)) {
            // this means that this folder doesn't hold messages, and it is NOT open
            currentFolderChanged = checkSubfoldersChanged();
            // checking subfolders is safe because the javadocs
            // http://java.sun.com/products/javamail/javadocs/javax/mail/Folder.html#list()
            // say "This method can be invoked on a closed Folder."
            this.currentMessages = null; // just in case
            return; // bail out now, no prefetching is needed in this case
        }
        
        if (logger.isDebugEnabled()) { logger.debug("Prefetching started: " + currentFolderURI); }
        
        /*
         * Retrieve all non-deleted messages using Folder.search(new FlagTerm(Flags.Flag.DELETED, false)).
         * This is preferable over checking the deleted flag of each mail individually as prefetching these
         * flags takes considerable time and using a search lets the *server* execute this selection. Folder
         * itself uses a naive, client-side approach but that it overridden by a server-based approach in
         * IMAPFolder.
         */
        this.currentMessages = folder.search(new FlagTerm(new Flags(Flags.Flag.DELETED),false));

        if (logger.isDebugEnabled()) { logger.debug("Folder has " + this.currentMessages.length + " non-deleted msgs"); }
        
        /* Determine the subset of non-expunged messages (no need to prefetch anything). */
        this.currentMessages = getNonExpungedMessages(currentMessages);
        if (logger.isDebugEnabled()) { logger.debug("Folder contains " + this.currentMessages.length + " non-expunged msgs"); }
        
        
        if (logger.isDebugEnabled()) { logger.debug("Prefetching uids"); }
        /* Pre-fetch message UIDs for this set. */
        FetchProfile profile = new FetchProfile();
        profile.add(UIDFolder.FetchProfileItem.UID); // needed for message.getUID
        profile.add(FetchProfile.Item.FLAGS); // needed for isAcceptable (DELETED)
        profile.add(FetchProfile.Item.ENVELOPE); // needed for isAcceptable (size check)
        folder.fetch(currentMessages, profile);
        if (logger.isDebugEnabled()) { logger.debug("Prefetching uids completed"); }
        
        /*
         * When the UIDValidity of the folder has changed or was not registered (= initial crawl), we need
         * to crawl all messages, else we try to incrementally crawl it:
         */
        // assume that the folder is unchanged unless proven otherwise
        boolean folderContentChanged = false;
        // we'll need the message count
        int messageCount = getCurrentFolderMessageCount();
        // .. to initialize this array (it's a hack, see the end of this method for an explanation)
        ArrayList<String> unmodifiedMessages = new ArrayList(messageCount); 
        // this is important, if this is false, we need to recrawl everything regardless of the AccessData
        boolean uidValidity = ((accessData != null) ? checkCurrentFolderUIDValidity(accessData) : false);
        if (logger.isDebugEnabled()) { logger.debug("UID validity " + uidValidity); }
        
        // a string for convenience
        String folderUriString = currentFolderURI.toString();
        if (accessData != null && uidValidity) {
            // this means that we can consult the access data the uids are valid
            if (logger.isDebugEnabled()) { logger.debug("Narrowing the current messages array"); }
            folderContentChanged = narrowCurrentMessagesArray(accessData,unmodifiedMessages, folderUriString);
            if (logger.isDebugEnabled()) { logger.debug("Narrowing completed, " + currentMessages.length + " msgs left, folderChanged: " + folderContentChanged); }
        } else if (accessData != null && !uidValidity) {
            // this means that we need to remove all info from the AccessData and recrawl everything
            if (logger.isDebugEnabled()) { logger.debug("Removing folder info from access data"); }
            folderContentChanged = removeFolderInfoFromAccessData(folderUriString);
        } else {
            // this means that the accessdata is null we must assume that the folder has been changed
            if (logger.isDebugEnabled()) { logger.debug("No access data detected"); }
            folderContentChanged = true;
        }
        // stop if requested
        if (isStopRequested()) {
            return;
        }
        
        // not only the messages must not be changed, the subfolders too must be the same
        if (folderContentChanged) {
            currentFolderChanged = true;
        } else {
            currentFolderChanged = checkSubfoldersChanged();
        }
        
        if (logger.isDebugEnabled()) { logger.debug("Finally, after checking subfolders, folderChanged: " + currentFolderChanged); }
       
        if (currentFolderChanged) {
            /*
             * Only now do we really know if the content info of the messages needs to be prefetched or not.
             * pre-fetch content info for the selected messages (assumption: all other info has already been
             * pre-fetched when determining the folder's metadata, no need to prefetch it again)
             */
            if (logger.isDebugEnabled()) { logger.debug("Prefetching content info of " + currentMessages.length + " msgs"); }
            profile = new FetchProfile();
            profile.add(FetchProfile.Item.CONTENT_INFO);
            profile.add("Message-ID");
            profile.add("In-Reply-To");
            profile.add("References");
            currentFolder.fetch(currentMessages, profile);
            if (logger.isDebugEnabled()) { logger.debug("Prefetching content info completed"); }
            
            /*
             * we report the unmodified messages here, only if the folder HAS been changed, otherwise the
             * AbstractJavaMailCrawler will invoke the reportNotModified method on the folder which will
             * report all children of the folder as unmodified. This behavior is preferred in most cases,
             * since it's often possible to determine if a folder has been changed without actually iterating
             * over all the messages. That's why this hack isn't that bad.
             */
            if (logger.isDebugEnabled()) { logger.debug("Reporting " + unmodifiedMessages.size() + " unmodified msgs"); }
            for (String url : unmodifiedMessages) {
                reportNotModified(url);
            }
        }
    }
    
    private Message[] getNonExpungedMessages(Message[] messages) {
        ArrayList<Message> arrayList = new ArrayList<Message>();
        for (Message message : messages) {
            if (!message.isExpunged()) {
                arrayList.add(message);
            }
        }
        return arrayList.toArray(new Message[arrayList.size()]);
    }

    @Override
    protected int getCurrentFolderMessageCount() throws MessagingException {
        return currentMessages.length;
    }

    @Override
    protected Message getMessageFromCurrentFolder(int index) throws MessagingException {
        // the given index is one-based, conformant to the javamail convention
        return currentMessages[index-1];
    }
    
    /** 
     * @see org.semanticdesktop.aperture.crawler.mail.AbstractJavaMailCrawler#getPartStream(javax.mail.Part)
     */
    @Override
    public InputStream getPartStream(Part part) throws MessagingException, IOException{
        return streamPool.getStreamForAMessage(part);
    }

    /**
     * This method narrows down the currentMessages array so that it contains only those messages that
     * have been added or changed. It also reports unmodified messages. The deleted messages are NOT
     * reported here, they are handled by the CrawlerBase. It also determines if the current folder
     * has been changed at all. If it hasn't the AbstractJavaMailCrawler will not crawl it.
     * 
     * @param newAccessData
     * @param messageCount
     * @param unmodifiedMessages
     * @param folderUriString
     * @return
     * @throws MessagingException
     */
    private boolean narrowCurrentMessagesArray(AccessData newAccessData,
            ArrayList<String> unmodifiedMessages, String folderUriString) throws MessagingException {
        boolean folderChanged = false;
        int messageCount = getCurrentFolderMessageCount();
        /*
         * - See if the set of retrieved message UIDs is equal to the set of stored message UIDs (can be
         * done by calculating message URIs and looking them up in AccessData). Also see if the set of
         * subfolders is the same.
         */
        // this will store all messages that are potentially interesting, and should be crawled 
        ArrayList filteredMessages = new ArrayList(messageCount);

        // we use this set to check it against the current set of messages in the folder
        Set deprecatedChildren = newAccessData.getReferredIDs(folderUriString);
        if (deprecatedChildren == null) {
            deprecatedChildren = Collections.EMPTY_SET;
        }
        else {
            // we create a a copy of the set, to protect the original one
            deprecatedChildren = new HashSet(deprecatedChildren);
        }

        // loop over all the current messages
        for (int i = 1; i <= messageCount && !isStopRequested(); i++) {
            MimeMessage message = (MimeMessage) getMessageFromCurrentFolder(i);
            
            // determine the uri
            String uri = getMessageUri(currentFolder, message);
            
            // remove this uri from the deprecatedChildren set 
            if (deprecatedChildren.contains(uri)) {
                deprecatedChildren.remove(uri);
            } else {
                // if it wasn't there, it means that the entire folder has been changed
                folderChanged = true;
            }
            
            // see if we've seen this message before
            if (newAccessData.get(uri, ACCESSED_KEY) == null) {
                // we haven't: register it for processing if it's not deleted/marked for deletion, etc.
                if (isAcceptable(message)) {
                    filteredMessages.add(message);
                    // this also means that the folder has been changes
                    folderChanged = true;
                }
            }
            else {
                // we've seen this before: if it's not a removed message, it must be an unmodified message
                if (isRemoved(message)) {
                    // this message models a deleted or expunged mail: make sure it does no longer appear
                    // as a child data object of the folder
                    newAccessData.removeReferredID(folderUriString, uri);
                    folderChanged = true;
                }
                else {
                    // this is an unmodified message, note that we don't add it to the filteredMessages list
                    unmodifiedMessages.add(getMessageUri(currentFolder,message));
                }
            }
        }

        // create the subset of messages that we will process
        currentMessages = (Message[]) filteredMessages.toArray(new Message[filteredMessages.size()]);

        /*
         * remove all child IDs that we did not encounter in the above loop note that we do NOT report
         * deleted messages to the crawler handler here (even though) we could, the removed messages are
         * reported by the CrawlerBase, we don't want to interfere with this here
         */
        Iterator iterator = deprecatedChildren.iterator();
        while (iterator.hasNext()) {
            String childUri = (String) iterator.next();
            newAccessData.removeReferredID(folderUriString, childUri);
            // if a message has been deleted from the folder, this means that the folder content
            // has been changed
            folderChanged = true;
        }
        return folderChanged;
    }
    
    /**
     * Checks if the list of subfolders of the current folder has been changed in comparison with the
     * list stored in the accessData instance.
     * @return true if the subfolder list has been changed, false otherwise
     * @throws MessagingException
     */
    private boolean checkSubfoldersChanged() throws MessagingException {
        if (accessData == null) {
            // this is pretty obvious, we don't know anything about the past
            // we must assume that something has changed
            return true;
        }
        
        String registeredSubFoldersFromAccessData = accessData.get(currentFolderURI.toString(), SUBFOLDERS_KEY);
        if (!holdsFolders(currentFolder)) {
            if (registeredSubFoldersFromAccessData == null) {
                // this means that there were no subfolders and none have appeared
                return false;
            } else {
                // this means that there were some subfolders but they have disappeared
                return true;
            }
        } else if (registeredSubFoldersFromAccessData == null) {
            // this means that there were no subfolders but some have appeared
            return true;
        }
        
        // if we get here this means that:
        // there is some accessdata
        // there are some subfolders
        // there is some information about the subfolders stored in the accessdatas
        //boolean subFoldersChanged = true;
        //if (registeredSubFoldersFromAccessData != null) {
        String subfolders = getSubFoldersString(currentFolder);
        if (subfolders != null /* the first check shouldn't be necessary*/ 
            && registeredSubFoldersFromAccessData.equals(subfolders)) {
            return false;
        } else {
            return true;
        }
        //} else {
            // if no subfolders were registered, then the current folder can't have
            // any subfolder either
        //    subFoldersChanged = (getSubFoldersString(currentFolder) == null);
        //}
        //return subFoldersChanged;
    }
    
    /**
     * Removes all information about the folder and all messages it contains from the accessData.
     * This method is called when the UID Validity of the folder changes, and can no longer be
     * guaranteed.
     * 
     * @param folderUriString
     * @return
     */
    private boolean removeFolderInfoFromAccessData(String folderUriString) {
        if (accessData == null) {
            return false;
        }
        
        boolean folderContentChanged;
        // this means that the uid validity is expired, we may remove everything we know from the 
        // access data since we will have to crawl everything once more and report all messages
        // as deleted - to be on the safe side
        Set deprecatedChildren = accessData.getReferredIDs(folderUriString);
        if (deprecatedChildren == null) {
            deprecatedChildren = Collections.EMPTY_SET;
        }
        else {
            // we create a a copy of the set, to protect the original one
            deprecatedChildren = new HashSet(deprecatedChildren);
        }
        for (Object uri : deprecatedChildren) {
            accessData.remove(uri.toString());
        }
        accessData.remove(folderUriString);
        folderContentChanged = true;
        return folderContentChanged;
    }

    /**
     * Checks the uid validity of the current folder against the value stored in the access data.
     * It is a very fundamental question. If this method returns false, all messages in the current
     * folder have to be crawled, and previous information about this folder has to be considered
     * out-of-date and needs to be deleted.<br/><br/>
     * 
     * THIS METHOD IS UNTESTED, THE AUTHOR WAS UNABLE TO SET UP AN ENVIRONMENT WHERE THE UID VALIDITY
     * OF AN IMAP SERVER COULD BE CHANGED AT WILL.
     * 
     * @param newAccessData
     * @return
     * @throws MessagingException
     */
    private boolean checkCurrentFolderUIDValidity(AccessData newAccessData) throws MessagingException {
        if (newAccessData == null) {
            return false;
        }
        String uidValidity = newAccessData.get(currentFolderURI.toString(), UID_VALIDITY);
        if (uidValidity == null) {
            return false;
        }
        long oldValidity = 0;
        try {
            oldValidity = Long.parseLong(uidValidity);
        } catch (NumberFormatException nfe) {
            // this case is unlikely, but better safe than sorry
            return false;
        }
        long newValidity = ((UIDFolder)currentFolder).getUIDValidity();
        return oldValidity == newValidity;
    }

    @Override
    protected void recordCurrentFolderInAccessData(AccessData newAccessData) throws MessagingException {
        // register the access data of this url
        IMAPFolder imapFolder = (IMAPFolder)currentFolder;
        String currentUriString = currentFolderURI.toString();
        if (newAccessData != null) {
            if (holdsMessages(imapFolder)) {
                newAccessData.put(currentUriString, UID_VALIDITY, String.valueOf(imapFolder.getUIDValidity()));
            } else {
                newAccessData.put(currentUriString, UID_VALIDITY, String.valueOf(-1));
            }
            if (holdsFolders(currentFolder)) {
                String subFoldersString = getSubFoldersString(currentFolder);
                if (subFoldersString != null) {
                    newAccessData.put(currentUriString, SUBFOLDERS_KEY, subFoldersString);
                }
            }            
        }
    }
    
    @Override
    protected boolean checkIfCurrentFolderHasBeenChanged(AccessData newAccessData) throws MessagingException{
        return currentFolderChanged;
    }


    
    /* -------------------- Methods related to URI generation (RFC 2192) -------------------- */
    
    private String getFolderURIPrefix(Folder folder) {
        StringBuilder buffer = new StringBuilder(100);
        URLName url = store.getURLName();

        // start with protocol
        // don't use url.getProtocol or your urls may start with "imaps://"
        buffer.append(IMAP_URL_SCHEME);

        // append host and username
        String username = url.getUsername();
        if (username != null && !username.equals("")) {
            username = HttpClientUtil.formUrlEncode(username);
            buffer.append(username);
            buffer.append('@');
        }
        buffer.append(url.getHost());

        // append path
        buffer.append('/');
        buffer.append(HttpClientUtil.formUrlEncode(folder.getFullName(), "/"));

        return buffer.toString();
    }

    /**
     * Returns the name of the folder with the given URL
     * @param url the url of the folder
     * @return the name of the folder with the given URL
     */
    public static String getFolderName(String url) {
        if (!url.startsWith(IMAP_URL_SCHEME)) {
            return null;
        }

        int firstIndex = url.indexOf('/', 7);
        int lastIndex = url.endsWith(";TYPE=LIST") ? url.lastIndexOf(';') : url.lastIndexOf('/');

        if (firstIndex >= 0 && lastIndex > firstIndex) {
            String substring = url.substring(firstIndex + 1, lastIndex);
            try {
                return URLDecoder.decode(substring, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return null;
        }
    }
    
    @Override
    protected URI getFolderURI(Folder folder) throws MessagingException {
        return new URIImpl(getFolderURIPrefix(folder) + ";TYPE=LIST");
    }
    
    @Override
    protected String getMessageUri(Folder folder, Message message) throws MessagingException{
        return getFolderURIPrefix(folder) + "/;UID=" + ((UIDFolder)folder).getUID(message);
    }

   
    /* ---------------------------- Socket Factory implementation -------------------------- */

    /**
     * This is a socket factory that ignores ssl certificates.
     */
    public static class SimpleSocketFactory extends SSLSocketFactory {

        private Logger logger = LoggerFactory.getLogger(getClass());

        private SSLSocketFactory factory;

        /**
         * Creates a socket factory that will ignore the ssl certificate, and accept any as valid.
         * 
         */
        public SimpleSocketFactory() {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");

                sslcontext.init(null, new TrustManager[] { new NaiveTrustManager() }, null);
                factory = sslcontext.getSocketFactory();
            }
            catch (Exception e) {
                logger.error("Exception while setting up SimpleSocketFactory", e);
            }
        }

        /**
         * Read trusted certificates from the given keyStore
         * 
         * @param certificateFile
         * @param password
         */
        public SimpleSocketFactory(File certificateFile, String password) {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                StandardTrustManager trustManager = new StandardTrustManager(certificateFile, password
                        .toCharArray());
                sslcontext.init(null, new TrustManager[] { trustManager }, null);
                factory = sslcontext.getSocketFactory();
            }
            catch (Exception e) {

                logger.error("Exception while setting up SimpleSocketFactory", e);
            }
        }

        /** 
         * Returns the default socket factory
         * @return the default socket factory 
         */
        public static SocketFactory getDefault() {
            return new SimpleSocketFactory();
        }

        /**
         * Creates a socket
         * @return a newly created socket
         * @throws IOException if na I/O error occurs
         */
        public Socket createSocket() throws IOException {
            return factory.createSocket();
        }

        /**
         * Creates a socket with the given parameters.
         * @param socket the parent socket
         * @param host the host address
         * @param port the port number
         * @param flag the flag
         * @return a newly created socket
         * @throws IOException if something goes wrong in the process
         */
        public Socket createSocket(Socket socket, String host, int port, boolean flag) throws IOException {
            return factory.createSocket(socket, host, port, flag);
        }

        /**
         * Creates a socket with the given parameters.
         * @param address the internet address
         * @param localAddress the local address
         * @param port the remote port number
         * @param localPort the local port number
         * @return a newly created socket
         * @throws IOException if something goes wrong in the process
         */
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
                throws IOException {
            return factory.createSocket(address, port, localAddress, localPort);
        }

        /**
         * Creates a socket with the given parameters.
         * @param host the internet address
         * @param port the remote port number
         * @return a newly created socket
         * @throws IOException if something goes wrong in the process
         */
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return factory.createSocket(host, port);
        }

        /**
         * Creates a socket with the given parameters.
         * @param host the internet address
         * @param port the remote port number
         * @param localHost the local address
         * @param localPort the local port number
         * @return a newly created socket
         * @throws IOException if something goes wrong in the process
         */
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
                throws IOException {
            return factory.createSocket(host, port, localHost, localPort);
        }

        /**
         * Creates a socket with the given parameters.
         * @param host the internet address
         * @param port the remote port number
         * @return a newly created socket
         * @throws IOException if something goes wrong in the process
         */
        public Socket createSocket(String host, int port) throws IOException {
            return factory.createSocket(host, port);
        }

        /**
         * Returns an array of default cipher suites.
         * @return an array of default cipher suites.
         */
        public String[] getDefaultCipherSuites() {
            return factory.getDefaultCipherSuites();
        }

        /**
         * Returns an array of supported cipher suites.
         * @return an array of supported cipher suites.
         */
        public String[] getSupportedCipherSuites() {
            return factory.getSupportedCipherSuites();
        }

        private class NaiveTrustManager implements X509TrustManager {
            
            /** Default constructor */
            public NaiveTrustManager() {
                // do nothing
            }

            /**
             * Checks if a certificate can be trusted. This naive implementation accepts all certificates.
             * @see X509TrustManager#checkClientTrusted(X509Certificate[], String)
             */
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // accept everything
            }

            
            /**
             * Checks if a certificate can be trusted. This naive implementation accepts all certificates.
             * @see X509TrustManager#checkServerTrusted(X509Certificate[], String)
             */
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // accept everything
            }

            /**
             * Returns null
             * @see X509TrustManager#getAcceptedIssuers()
             */
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        }
    }

    /* ------------------------------------ Other Methods ---------------------------------- */
    
    /**
     * Sets the session properties
     * @param sessionProperties the new session properties
     */
    public void setSessionProperties(Properties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    /**
     * Returns the session properties
     * @return the session properties
     */
    public Properties getSessionProperties() {
        return sessionProperties;
    }
    
    // cementery for the code, might come in handy 
    
//    @Override
//    protected boolean checkIfCurrentFolderHasBeenChanged(AccessData newAccessData) throws MessagingException{
//        //Message[] messages = null;
//        //IMAPFolder imapFolder = (IMAPFolder)folder;
//        String currentUriString = currentFolderURI.toString();
//        if (newAccessData != null) {
//            // the results default to 'true'; unless we can find complete evidence that the folder has not
//            // changed, we will always access the folder
//            boolean messagesChanged = true;
//            boolean foldersChanged = true;
//
//            if (holdsMessages(currentFolder)) {
//                String nextUIDString = newAccessData.get(currentUriString, NEXT_UID_KEY);
//                String sizeString = newAccessData.get(currentUriString, SIZE_KEY);
//
//                // note: this is -1 for servers that don't support retrieval of a next UID, meaning that the
//                // folder will always be reported as changed when it should have been unmodified
//                long nextUID = ((IMAPFolder)currentFolder).getUIDNext();
//
//                if (nextUIDString != null && sizeString != null && nextUID != -1L) {
//                    try {
//                        // parse stored information
//                        long previousNextUID = Long.parseLong(nextUIDString);
//                        long previousSize = Integer.parseInt(sizeString);
//
//                        // determine the new folder size, excluding all deleted/deletion-marked messages
//                        //messages = folder.getMessages();
//                        //FetchProfile profile = new FetchProfile();
//                        //profile.add(FetchProfile.Item.FLAGS); // needed for DELETED flag
//                        //folder.fetch(messages, profile);
//                        int messageCount = getMessageCount(currentMessages);
//
//                        // compare the folder status with what we've stored in the AccessData
//                        if (previousNextUID == nextUID && previousSize == messageCount) {
//                            messagesChanged = false;
//                        }
//                    }
//                    catch (NumberFormatException e) {
//                        logger.error("exception while parsing access data, ingoring access data", e);
//                    }
//                }
//            }
//
//            if (holdsFolders(currentFolder)) {
//                String registeredSubFolders = newAccessData.get(currentUriString, SUBFOLDERS_KEY);
//
//                if (registeredSubFolders != null) {
//                    String subfolders = getSubFoldersString(currentFolder);
//                    if (registeredSubFolders.equals(subfolders)) {
//                        foldersChanged = false;
//                    }
//                }
//            }
//
//            if (!messagesChanged && !foldersChanged) {
//                // the folder contents have not changed, we can return immediately
//                logger.debug("Folder \"" + currentFolder.getFullName() + "\" has not changed.");
//                return false;
//            }
//
//            logger.debug("Folder \"" + currentFolder.getFullName() + "\" is new or has changes.");
//        }
//        return true;
//    }

    
//    @Override
//    protected void recordCurrentFolderInAccessData(AccessData newAccessData) throws MessagingException {
//        // register the access data of this url
//        IMAPFolder imapFolder = (IMAPFolder)currentFolder;
//        String currentUriString = currentFolderURI.toString();
//        if (newAccessData != null) {
//            if (holdsMessages(currentFolder)) {
//                // getUIDNext may return -1 (unknown), be careful not to store that
//                long uidNext = imapFolder.getUIDNext();
//                if (uidNext != -1L) {
//                    newAccessData.put(currentUriString, NEXT_UID_KEY, String.valueOf(imapFolder.getUIDNext()));
//                }
//
//                int messageCount = getMessageCount(currentMessages);
//                newAccessData.put(currentUriString, SIZE_KEY, String.valueOf(messageCount));
//            }
//            if (holdsFolders(currentFolder)) {
//                newAccessData.put(currentUriString, SUBFOLDERS_KEY, getSubFoldersString(currentFolder));
//            }            
//        }
//    }
    
}
