/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.File;
import java.io.IOException;
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
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
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
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource.ConnectionSecurity;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManager;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;



/**
 * A Combined Crawler and DataAccessor implementation for IMAP. Note that the same instance of
 * ImapCrawler cannot be used as a crawler and as a DataAccessor at the same time. Please use
 * separate instances, or use the appropriate factory, which will enforce this for you.
 */
@SuppressWarnings("unchecked")
public class ImapCrawler extends AbstractJavaMailCrawler implements DataAccessor {

    private static final String IMAP_URL_SCHEME = "imap://";
    
    private static final String NEXT_UID_KEY = "nextuid";

    private static final String SIZE_KEY = "size";

    private static final String SUBFOLDERS_KEY = "subfolders";

    private Logger logger = LoggerFactory.getLogger(getClass());

    // The source whose properties we're currently using. A separate DataSource is necessary as the
    // DataAccessor implementation may be passed a different DataSource.
    private ImapDataSource configuredDataSource;

    // A Property instance holding *extra* properties to use when a Session is initiated.
    // This can be used in apps running on Java < 5.0 to instruct to use a different SocketFactory,
    // when you don't want to communicate this via the system properties
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
    
    /* ----------------- Fields managed by the setCurrentFolder method ----------------- */
    
    private Message [] currentMessages;
    private int preparedActiveMessageCount;

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
    }

    /**
     * Closes any connections this ImapCrawler may have to an IMAP server. Afterwards, the InputStream of any
     * returned FileDataObjects may no longer be accessible. Invoking this method when no connections are open
     * has no effect.
     */
    public void closeConnection() {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                logger.warn("Unable to close connection", e);
            }
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
        // reconfigure for the specified DataSource if necessary
        retrieveConfigurationData(dataSource);

        try {
            // make sure we have a connection to the mail store
            ensureConnectedStore();

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
                
                // create a DataObject for the requested message or message part
                return getObject(message, url, getFolderURI(folder), dataSource, newAccessData, containerFactory);
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
        }
    }
    
    @Override
    protected void setCurrentFolder(Folder folder) throws MessagingException {
        super.setCurrentFolder(folder);
        this.currentMessages = folder.getMessages();
        
        // for faster access, prefetch all message UIDs and flags
        FetchProfile profile = new FetchProfile();
        profile.add(UIDFolder.FetchProfileItem.UID); // needed for message.getUID
        profile.add(FetchProfile.Item.FLAGS); // needed for isAcceptable (DELETED)
        profile.add(FetchProfile.Item.ENVELOPE); // needed for isAcceptable (size check)
        folder.fetch(currentMessages, profile);
        
        prefetchContentInfoOfNewAndChangedMessages();
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

    /*
     * Narrows down the messages[] array to those messages that are new or changed and prefetches their
     * content info. Messages detected as unmodified are reported as unmodified (the reportNotModified
     * method from the parent class is called).
     */
    private void prefetchContentInfoOfNewAndChangedMessages() throws MessagingException {
        // determine the set of messages we haven't seen yet, to prevent prefetching the content info for old
        // messages: especially handy when only a few mails were added to a large folder.

        if (accessData != null) {
            //ArrayList filteredMessages = new ArrayList(messages.length);
            int messageCount = getCurrentFolderMessageCount();
            ArrayList filteredMessages = new ArrayList(messageCount);

            // when messages disappear from the array, we must make sure they are removed from the list of
            // child IDs of the folder
            String folderUriString = currentFolderURI.toString();
            Set deprecatedChildren = accessData.getReferredIDs(folderUriString);
            if (deprecatedChildren == null) {
                deprecatedChildren = Collections.EMPTY_SET;
            }
            else {
                deprecatedChildren = new HashSet(deprecatedChildren);
            }

            // loop over all messages
            //for (int i = 0; i < messages.length && !isStopRequested(); i++) {
            for (int i = 1; i <= messageCount && !isStopRequested(); i++) {
                MimeMessage message = (MimeMessage) getMessageFromCurrentFolder(i);
                
                // determine the uri
                String uri = getMessageUri(currentFolder, message);

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
                        reportNotModified(getMessageUri(currentFolder, message));
                    }
                }
            }

            // create the subset of messages that we will process
            currentMessages = (Message[]) filteredMessages.toArray(new Message[filteredMessages.size()]);

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
        currentFolder.fetch(currentMessages, profile);
        
    }

    @Override
    protected void recordCurrentFolderInAccessData(AccessData newAccessData) throws MessagingException {
        // register the access data of this url
        IMAPFolder imapFolder = (IMAPFolder)currentFolder;
        String currentUriString = currentFolderURI.toString();
        if (newAccessData != null) {
            if (holdsMessages(currentFolder)) {
                // getUIDNext may return -1 (unknown), be careful not to store that
                long uidNext = imapFolder.getUIDNext();
                if (uidNext != -1L) {
                    newAccessData.put(currentUriString, NEXT_UID_KEY, String.valueOf(imapFolder.getUIDNext()));
                }

                int messageCount = getMessageCount(currentMessages);
                newAccessData.put(currentUriString, SIZE_KEY, String.valueOf(messageCount));
            }
            if (holdsFolders(currentFolder)) {
                newAccessData.put(currentUriString, SUBFOLDERS_KEY, getSubFoldersString(currentFolder));
            }
        }
    }
    
    @Override
    protected boolean checkIfCurrentFolderHasBeenChanged(AccessData newAccessData) throws MessagingException{
        //Message[] messages = null;
        //IMAPFolder imapFolder = (IMAPFolder)folder;
        String currentUriString = currentFolderURI.toString();
        if (newAccessData != null) {
            // the results default to 'true'; unless we can find complete evidence that the folder has not
            // changed, we will always access the folder
            boolean messagesChanged = true;
            boolean foldersChanged = true;

            if (holdsMessages(currentFolder)) {
                String nextUIDString = newAccessData.get(currentUriString, NEXT_UID_KEY);
                String sizeString = newAccessData.get(currentUriString, SIZE_KEY);

                // note: this is -1 for servers that don't support retrieval of a next UID, meaning that the
                // folder will always be reported as changed when it should have been unmodified
                long nextUID = ((IMAPFolder)currentFolder).getUIDNext();

                if (nextUIDString != null && sizeString != null && nextUID != -1L) {
                    try {
                        // parse stored information
                        long previousNextUID = Long.parseLong(nextUIDString);
                        long previousSize = Integer.parseInt(sizeString);

                        // determine the new folder size, excluding all deleted/deletion-marked messages
                        //messages = folder.getMessages();
                        //FetchProfile profile = new FetchProfile();
                        //profile.add(FetchProfile.Item.FLAGS); // needed for DELETED flag
                        //folder.fetch(messages, profile);
                        int messageCount = getMessageCount(currentMessages);

                        // compare the folder status with what we've stored in the AccessData
                        if (previousNextUID == nextUID && previousSize == messageCount) {
                            messagesChanged = false;
                        }
                    }
                    catch (NumberFormatException e) {
                        logger.error("exception while parsing access data, ingoring access data", e);
                    }
                }
            }

            if (holdsFolders(currentFolder)) {
                String registeredSubFolders = newAccessData.get(currentUriString, SUBFOLDERS_KEY);

                if (registeredSubFolders != null) {
                    String subfolders = getSubFoldersString(currentFolder);
                    if (registeredSubFolders.equals(subfolders)) {
                        foldersChanged = false;
                    }
                }
            }

            if (!messagesChanged && !foldersChanged) {
                // the folder contents have not changed, we can return immediately
                logger.debug("Folder \"" + currentFolder.getFullName() + "\" has not changed.");
                return false;
            }

            logger.debug("Folder \"" + currentFolder.getFullName() + "\" is new or has changes.");
        }
        return true;
    }


    
    /* -------------------- Methods related to URI generation (RFC 2192) -------------------- */
    
    private String getFolderURIPrefix(Folder folder) throws MessagingException {
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
        buffer.append(encodeFolderName(folder.getFullName()));

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

    /** 
     * Does the same as HttpClientUtil.formUrlEncode (i.e. RFC 1738) except for encoding the slash,
     * which should not be encoded according to RFC 2192.
     * @param string the string to be encoded
     * @return the encoded folder name
     */
    public static String encodeFolderName(String string) {
        int length = string.length();
        StringBuilder buffer = new StringBuilder(length + 10);

        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);

            // Only characters in the range 48 - 57 (numbers), 65 - 90 (upper case letters), 97 - 122
            // (lower case letters) can be left unencoded. The rest needs to be escaped.

            if (c == ' ') {
                // replace all spaces with a '+'
                buffer.append('+');
            }
            else {
                int cInt = c;
                if (cInt >= 48 && cInt <= 57 || cInt >= 65 && cInt <= 90 || cInt >= 97 && cInt <= 122
                        || cInt == 46) {
                    // alphanumeric character or slash
                    buffer.append(c);
                }
                else {
                    // escape all non-alphanumerics
                    buffer.append('%');
                    String hexVal = Integer.toHexString(c);

                    // ensure use of two characters
                    if (hexVal.length() == 1) {
                        buffer.append('0');
                    }

                    buffer.append(hexVal);
                }
            }
        }

        return buffer.toString();
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
}
