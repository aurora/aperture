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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Flags;
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

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource;
import org.semanticdesktop.aperture.datasource.imap.ImapDataSource.ConnectionSecurity;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManager;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.imap.IMAPFolder;

/**
 * A Combined Crawler and DataAccessor implementation for IMAP.
 */
@SuppressWarnings("unchecked")
public class ImapCrawler extends CrawlerBase implements DataAccessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ACCESSED_KEY = "accessed";

    private static final String NEXT_UID_KEY = "nextuid";

    private static final String SIZE_KEY = "size";

    private static final String SUBFOLDERS_KEY = "subfolders";

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

    private ArrayList baseFolders = new ArrayList();

    private long maximumByteSize;

    private Store store;

    private String cachedMessageUrl;

    private Map cachedDataObjectsMap = new HashMap();

    private int maxDepth;

    private boolean includeInbox;

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

    private void crawlFolder(Folder folder, int depth) throws MessagingException {
        if (isStopRequested()) {
            return;
        }

        // skip if there is a problem
        if (folder == null) {
            logger.info("passed null folder, ignoring");
            return;
        }
        else if (!folder.exists()) {
            logger.info("folder does not exist: \"" + folder.getFullName() + "\"");
            return;
        }

        // crawl the folder and its messages, if any
        logger.info("crawling folder \"" + folder.getFullName() + "\"");
        crawlSingleFolder(folder);

        // crawl its subfolders, if any and when allowed
        if (holdsFolders(folder)) {
            logger.info("crawling subfolders in folder \"" + folder.getFullName() + "\"");
            crawlSubFolders(folder, depth);
        }

        if (folder.isOpen()) {
            // close the folder without deleting expunged messages
            folder.close(false);
        }
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

    private void crawlSingleFolder(Folder folder) throws MessagingException {
        // open the folder in read-only mode
        if (holdsMessages(folder) && !folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }

        // report the folder's metadata
        String folderUrl = getURIPrefix(folder) + ";TYPE=LIST";

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
                    crawlMessages((IMAPFolder) folder, folderObject.getID());
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
            logger.info("Reached crawling depth limit (" + maxDepth + ") - stopping.");
            return;
        }

        try {
            Folder[] subFolders = folder.list();
            logger.info("Crawling " + subFolders.length + " sub-folders.");
            for (int i = 0; !isStopRequested() && i < subFolders.length; i++) {
                try {
                    crawlFolder(subFolders[i], depth + 1);
                }
                catch (MessagingException e) {
                    logger.info("Error crawling subfolder \"" + subFolders[i].getFullName() + "\"");
                    // but continue..
                }
            }
        }
        catch (MessagingException e) {
            logger.warn("Exception while crawling subFolders of \"" + folder.getFullName() + "\"", e);
        }
    }

    private String getURIPrefix(Folder folder) throws MessagingException {
        StringBuilder buffer = new StringBuilder(100);
        URLName url = store.getURLName();

        // start with protocol
        // don't use url.getProtocol or your urls may start with "imaps://"
        buffer.append("imap://");

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
        if (!url.startsWith("imap://")) {
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

    private void crawlMessages(IMAPFolder folder, URI folderUri) throws MessagingException {
        if (isStopRequested()) {
            return;
        }

        logger.info("Crawling messages in folder " + folder.getFullName());

        // determine the set of messages we haven't seen yet, to prevent prefetching the content info for old
        // messages: especially handy when only a few mails were added to a large folder.
        Message[] messages = folder.getMessages();
        String messagePrefix = getURIPrefix(folder) + "/";

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
                long messageID = folder.getUID(message);

                // determine the uri
                String uri = messagePrefix + messageID;

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
                        reportNotModified(messagePrefix + messageID);
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
            long messageID = folder.getUID(message);
            String uri = messagePrefix + messageID;

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

    private int getMessageCount(Message[] messages) throws MessagingException {
        int result = 0;

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            if (!isRemoved(message)) {
                result++;
            }
        }

        return result;
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

            // see if we need to process a folder or a message
            int typeIndex = url.indexOf(";TYPE=");
            if (typeIndex < 0) {
                // determine the message UID: cutoff the ID part
                int separatorIndex = url.lastIndexOf('/');
                if (separatorIndex < 0 || separatorIndex >= url.length() - 1) {
                    throw new IllegalArgumentException("unable to get message UID from " + url);
                }
                String messageNumberString = url.substring(separatorIndex + 1);

                // remove the fragment identifier
                separatorIndex = messageNumberString.indexOf('#');
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

                // create a DataObject for the requested message or message part
                return getObject(message, url, getURI(folder), dataSource, newAccessData, containerFactory);
            }
            else {
                // create a DataObject for this Folder
                return getObject(folder, url, dataSource, newAccessData, containerFactory);
            }
        }
        catch (MessagingException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    private DataObject getObject(MimeMessage message, String url, URI folderUri, DataSource dataSource,
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

    private FolderDataObject getObject(Folder folder, String url, DataSource dataSource, AccessData newAccessData,
            RDFContainerFactory containerFactory) throws MessagingException {
        // See if this url has been accessed before and hasn't changed in the mean time.
        // A check for the next UID guarantees that no mails have been added (see RFC 3501).
        // If this is still the same, a check on the number of messages guarantees that no mails have
        // been removed either. Finally, we check that it has the same set of subfolders
        IMAPFolder imapFolder = (IMAPFolder) folder;
        Message[] messages = null;

        // check if the folder has changed
        if (newAccessData != null) {
            // the results default to 'true'; unless we can find complete evidence that the folder has not
            // changed, we will always access the folder
            boolean messagesChanged = true;
            boolean foldersChanged = true;

            if (holdsMessages(folder)) {
                String nextUIDString = newAccessData.get(url, NEXT_UID_KEY);
                String sizeString = newAccessData.get(url, SIZE_KEY);

                // note: this is -1 for servers that don't support retrieval of a next UID, meaning that the
                // folder will always be reported as changed when it should have been unmodified
                long nextUID = imapFolder.getUIDNext();

                if (nextUIDString != null && sizeString != null && nextUID != -1L) {
                    try {
                        // parse stored information
                        long previousNextUID = Long.parseLong(nextUIDString);
                        long previousSize = Integer.parseInt(sizeString);

                        // determine the new folder size, excluding all deleted/deletion-marked messages
                        messages = folder.getMessages();
                        FetchProfile profile = new FetchProfile();
                        profile.add(FetchProfile.Item.FLAGS); // needed for DELETED flag
                        folder.fetch(messages, profile);
                        int messageCount = getMessageCount(messages);

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

            if (holdsFolders(folder)) {
                String registeredSubFolders = newAccessData.get(url, SUBFOLDERS_KEY);

                if (registeredSubFolders != null) {
                    String subfolders = getSubFoldersString(folder);
                    if (registeredSubFolders.equals(subfolders)) {
                        foldersChanged = false;
                    }
                }
            }

            if (!messagesChanged && !foldersChanged) {
                // the folder contents have not changed, we can return immediately
                logger.info("Folder \"" + folder.getFullName() + "\" has not changed.");
                return null;
            }

            logger.info("Folder \"" + folder.getFullName() + "\" is new or has changes.");
        }

        // register the folder's name
        URI folderURI = new URIImpl(url);
        RDFContainer metadata = containerFactory.getRDFContainer(folderURI);
        metadata.add(NFO.fileName, folder.getName());

        // register the folder's parent
        Folder parent = folder.getParent();
        if (parent != null) {
            metadata.add(NIE.isPartOf, getURI(parent));
        }

        // add message URIs as children
        String uriPrefix = getURIPrefix(folder) + "/";

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
                    long messageID = imapFolder.getUID(message);
                    try {
                        URI messageURI = metadata.getValueFactory().createURI(uriPrefix + messageID);
                        metadata.add(metadata.getValueFactory().createStatement(messageURI, NIE.isPartOf,
                            folderURI));
                    }
                    catch (ModelException e) {
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
                metadata.add(metadata.getValueFactory().createStatement(getURI(subFolder), NIE.isPartOf,
                    folderURI));
            }
        }

        // register the access data of this url
        if (newAccessData != null) {
            if (holdsMessages(folder)) {
                // getUIDNext may return -1 (unknown), be careful not to store that
                long uidNext = imapFolder.getUIDNext();
                if (uidNext != -1L) {
                    newAccessData.put(url, NEXT_UID_KEY, String.valueOf(imapFolder.getUIDNext()));
                }

                int messageCount = getMessageCount(messages);
                newAccessData.put(url, SIZE_KEY, String.valueOf(messageCount));
            }
            if (holdsFolders(folder)) {
                newAccessData.put(url, SUBFOLDERS_KEY, getSubFoldersString(folder));
            }
        }

        // TODO get back to it after introducing rootFolderOf
        // if this is a base folder then add some metadata
        //if (baseFolders.contains(folder.getFullName())) {
        //    metadata.add(DATA.rootFolderOf, dataSource.getID());
        // }

        // create the resulting FolderDataObject instance
        return new FolderDataObjectBase(folderURI, dataSource, metadata);
    }

    private URI getURI(Folder folder) throws MessagingException {
        return new URIImpl(getURIPrefix(folder) + ";TYPE=LIST");
    }

    private String getSubFoldersString(Folder folder) throws MessagingException {
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
}
