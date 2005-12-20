/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.HttpClientUtil;

/**
 * A Combined Crawler and DataAccessor implementation for IMAP.
 */
public class ImapCrawler extends CrawlerBase implements DataAccessor {

    private static final Logger LOGGER = Logger.getLogger(ImapCrawler.class.getName());

    private static final String MANDATORY_URL_PREFIX = "imap://";

    private static final String ACCESSED_KEY = "x";

    // The source whose properties we're currently using. A separate DataSource is necessary as the
    // DataAccessor implementation may be passed a different DataSource.
    private DataSource configuredDataSource;

    private String hostName;

    private String userName;

    private String password;

    private String connectionType;

    private String folderName;

    private int maximumByteSize;

    private Store store;

    private String cachedMessageUrl;

    private Map cachedDataObjectsMap = new HashMap();

    /* ----------------------------- Crawler implementation ----------------------------- */

    protected ExitCode crawlObjects() {
        // determine host name, user name, etc.
        retrieveConfigurationData(getDataSource());

        // make sure we have a connection to the mail store
        try {
            ensureConnectedStore();
        }
        catch (MessagingException e) {
            LOGGER.log(Level.WARNING, "Unable to connect to IMAP mail store", e);
            closeConnection();
            return ExitCode.FATAL_ERROR;
        }

        // crawl the folder contents
        boolean fatalError = false;

        try {
            crawlFolder();
        }
        catch (MessagingException e) {
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

    private void retrieveConfigurationData(DataSource dataSource) {
        // retrieve the DataSource's configuration object
        configuredDataSource = dataSource;
        RDFContainer config = dataSource.getConfiguration();

        // reset all values to to prevent garbage settings when an error occurs below
        hostName = userName = password = connectionType = folderName = null;
        maximumByteSize = Integer.MAX_VALUE;

        // fetch the root url
        String url = ConfigurationUtil.getRootUrl(config);

        // some error checking on this url
        if (!url.startsWith(MANDATORY_URL_PREFIX)) {
            throw new IllegalArgumentException("root url does not start with \"" + MANDATORY_URL_PREFIX
                    + "\": " + url);
        }
        if (url.equals(MANDATORY_URL_PREFIX)) {
            throw new IllegalArgumentException("missing host name: " + url);
        }

        // extract the host part and folder part of this url
        int slashIndex = url.indexOf('/', MANDATORY_URL_PREFIX.length());
        if (slashIndex < 0 || slashIndex == url.length() - 1) {
            throw new IllegalArgumentException("missing folder name: " + url);
        }

        String hostPart = url.substring(MANDATORY_URL_PREFIX.length(), slashIndex);
        String folderPart = url.substring(slashIndex + 1);

        // Split up the host part in a user name and host name. I use lastIndexOf instead of indexOf just
        // in case the user name contains an '@' and someone forgot to escape it
        int atIndex = hostPart.lastIndexOf('@');
        if (atIndex < 0) {
            hostName = hostPart;
        }
        else if (atIndex == 0) {
            throw new IllegalArgumentException("missing user name: " + url);
        }
        else if (atIndex == hostPart.length() - 1) {
            throw new IllegalArgumentException("missing host name: " + url);
        }
        else {
            hostName = hostPart.substring(atIndex + 1);
            userName = decode(hostPart.substring(0, atIndex));
        }

        // determine the folder name
        folderName = decode(folderPart);

        if (folderName.indexOf('*') >= 0 || folderName.indexOf('%') >= 0) {
            throw new IllegalArgumentException("wildcards in the folder name are not supported");
        }

        // Formally a ";TYPE=..." part is required according to RFC 2192. However, since we ignore it
        // anyway, we simply cut it off.
        int semiColonIndex = folderName.indexOf(';');
        if (semiColonIndex >= 0) {
            folderName = folderName.substring(0, semiColonIndex);
        }

        // determine the password
        password = ConfigurationUtil.getPassword(config);

        // determine the connection type
        String securityType = ConfigurationUtil.getConnectionSecurity(config);
        if (securityType == null || Vocabulary.PLAIN.toString().equals(securityType)) {
            connectionType = "imap";
        }
        else if (Vocabulary.SSL.toString().equals(securityType)) {
            connectionType = "imaps";
        }
        else {
            throw new IllegalArgumentException("Illegal connection security type: " + securityType);
        }

        // determine the maximum byte size
        Integer maximumSize = ConfigurationUtil.getMaximumByteSize(config);
        if (maximumSize == null) {
            maximumByteSize = Integer.MAX_VALUE;
        }
        else {
            maximumByteSize = maximumSize.intValue();
        }
    }

    public String decode(String string) {
        int percentIndex = string.indexOf('%');
        if (percentIndex < 0) {
            return string;
        }

        int startIndex = 0;
        StringBuffer buffer = new StringBuffer(string.length());

        while (percentIndex >= 0) {
            buffer.append(string.substring(startIndex, percentIndex));

            // The two character following the '%' contain a hexadecimal
            // code for the original character, i.e. '%20'
            String xx = string.substring(percentIndex + 1, percentIndex + 3);
            buffer.append((char) Integer.parseInt(xx, 16));

            startIndex = percentIndex + 3;
            percentIndex = string.indexOf('%', startIndex);
        }

        buffer.append(string.substring(startIndex));

        return buffer.toString();
    }

    private void ensureConnectedStore() throws MessagingException {
        // if there is no store yet, create one now
        if (store == null) {
            Properties properties = System.getProperties();
            Session session = Session.getDefaultInstance(properties);
            store = session.getStore(connectionType);
        }

        // make sure it is connected
        if (!store.isConnected()) {
            store.connect(hostName, userName, password);
        }
    }

    private void closeConnection() {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                LOGGER.log(Level.WARNING, "Unable to close connection", e);
            }
        }
    }

    private void crawlFolder() throws MessagingException {
        // FIXME: give the crawler handler a FolderDataObject?

        // fetch the Folder instance
        Folder folder = store.getFolder(folderName);
        UIDFolder uidFolder = (UIDFolder) folder;

        // skip if the folder does not exist
        if (folder == null || !folder.exists()) {
            LOGGER.info("folder does not exist: \"" + folderName + "\"");
            return;
        }
        else {
            LOGGER.info("crawling folder \"" + folderName + "\"");
        }

        // skip if the folder is empty
        if ((folder.getType() & Folder.HOLDS_MESSAGES) != Folder.HOLDS_MESSAGES) {
            LOGGER.info("empty folder: \"" + folderName + "\"");
            return;
        }

        // open the folder in read-only mode
        if (!folder.isOpen()) {
            folder.open(Folder.READ_ONLY);
        }

        // determine the start of all URIs originating from this folder
        String uriPrefix = getURIPrefix(folder);

        // scan this folder for new and changed messages
        Message[] messages = folder.getMessages();

        for (int i = 0; !isStopRequested() && i < messages.length; i++) {
            MimeMessage message = (MimeMessage) messages[i];
            long messageID = uidFolder.getUID(message);
            String uri = uriPrefix + messageID;

            try {
                scan(message, uri);
            }
            catch (Exception e) {
                // just log these exceptions; as they only affect a single message, they are
                // not considered fatal exceptions
                LOGGER.log(Level.WARNING, "Exception while scanning message " + uri, e);
            }
        }

        // close the folder without deleting expunged messages
        folder.close(false);
    }

    private String getURIPrefix(Folder folder) throws MessagingException {
        StringBuffer buffer = new StringBuffer(100);
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
        buffer.append('/');

        return buffer.toString();
    }

    // does the same as HttpClientUtil.formUrlEncode (i.e. RFC 1738) except for encoding the slash,
    // which should not be encoded according to RFC 2192.
    private String encodeFolderName(String string) {
        int length = string.length();
        StringBuffer buffer = new StringBuffer(length + 10);

        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);

            // Only characters in the range 48 - 57 (numbers), 65 - 90 (upper case letters), 97 - 122
            // (lower case letters) can be left unencoded. The rest needs to be escaped.

            if (c == ' ') {
                // replace all spaces with a '+'
                buffer.append('+');
            }
            else {
                int cInt = (int) c;
                if (cInt >= 48 && cInt <= 57 || cInt >= 65 && cInt <= 90 || cInt >= 97 && cInt <= 122
                        || cInt == 46) {
                    // alphanumeric character or slash
                    buffer.append(c);
                }
                else {
                    // escape all non-alphanumerics
                    buffer.append('%');
                    String hexVal = Integer.toHexString((int) c);

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

    private void scan(MimeMessage message, String uri) throws MessagingException {
        // see if we should skip this message for some reason
        if (message.isExpunged() || message.isSet(Flags.Flag.DELETED) || message.getSize() > maximumByteSize) {
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
                DataObject object = getObject(message, queuedUri, source, accessData, containerFactory);

                if (object == null) {
                    // report this object and all its children as unmodified
                    reportNotModified(queuedUri);
                }
                else {
                    // report this object as a new object (assumption: objects are always new, never
                    // changed, since mails are immutable)
                    crawlReport.increaseNewCount();
                    handler.objectNew(this, object);

                    // register parent child relationship
                    registerParent(object);

                    // queue all its children
                    queueChildren(object, queue);
                }
            }
            catch (MessagingException e) {
                // just log and continue with the next url
                LOGGER.log(Level.WARNING, "MessagingException while processing " + queuedUri, e);
            }
            catch (UrlNotFoundException e) {
                // this is most likely an internal error in the DataObjectFactory or DataAccessor, so log
                // it differently
                LOGGER.log(Level.SEVERE, "Internal error while processing " + queuedUri, e);
            }
            catch (IOException e) {
                // just log and continue with the next url
                LOGGER.log(Level.WARNING, "IOException while processing " + queuedUri, e);
            }
        }
    }

    private void reportNotModified(String uri) {
        // report this object as unmodified
        crawlReport.increaseUnchangedCount();
        handler.objectNotModified(this, uri);
        deprecatedUrls.remove(uri);

        // repeat recursively on all registered children
        Set children = accessData.getChildren(uri);
        if (children != null) {
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                reportNotModified((String) iterator.next());
            }
        }
    }

    private void registerParent(DataObject object) {
        // query for all parents
        Repository metadata = (Repository) object.getMetadata().getModel();
        Collection statements = metadata.getStatements(object.getID(),
                org.semanticdesktop.aperture.accessor.Vocabulary.PART_OF, null);

        // determine all unique parent URIs (the partOf statement may be
        HashSet parentIDs = new HashSet();
        Iterator iterator = statements.iterator();
        while (iterator.hasNext()) {
            Statement statement = (Statement) iterator.next();
            Value parent = statement.getObject();

            if (parent instanceof URI) {
                parentIDs.add(parent);
            }
            else {
                LOGGER.severe("Internal error: unexpected parent value type: " + parent.getClass());
            }
        }

        // register the parent
        if (parentIDs.isEmpty()) {
            return;
        }
        else if (parentIDs.size() > 1) {
            LOGGER.warning("Multiple parents for " + object.getID() + ", ignoring all");
        }
        else {
            URI parent = (URI) parentIDs.iterator().next();

            String parentID = parent.toString();
            String childID = object.getID().toString();

            if (accessData.isKnownId(parentID)) {
                if (parentID.equals(childID)) {
                    LOGGER.warning("cyclical " + org.semanticdesktop.aperture.accessor.Vocabulary.PART_OF
                            + " property for " + parentID + ", ignoring");
                }
                else {
                    accessData.putChild(parentID, childID);
                }
            }
            else {
                LOGGER.severe("Internal error: encountered unknown parent: " + parentID + ", child = "
                        + childID);
            }
        }
    }

    private void queueChildren(DataObject object, LinkedList queue) {
        // fetch the DataObject's Repository
        Repository metadata = (Repository) object.getMetadata().getModel();

        // query for all child URIs
        Collection statements = metadata.getStatements(null,
                org.semanticdesktop.aperture.accessor.Vocabulary.PART_OF, object.getID());

        // queue these URIs
        Iterator iterator = statements.iterator();
        while (iterator.hasNext()) {
            Statement statement = (Statement) iterator.next();
            Resource resource = statement.getSubject();

            if (resource instanceof URI) {
                String id = resource.toString();
                if (!queue.contains(id)) {
                    queue.add(id);
                }
            }
            else {
                LOGGER.severe("Internal error: unknown child value type: " + resource.getClass());
            }
        }
    }

    /* ----------------------------- DataAccessor implementation ----------------------------- */

    public DataObject getDataObject(String url, DataSource source, Map params,
            RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        return getDataObjectIfModified(url, source, null, params, containerFactory);
    }

    public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
            Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        MimeMessage message;

        // reconfigure for the specified DataSource if necessary
        if (source != configuredDataSource) {
            retrieveConfigurationData(source);
        }

        // retrieve the MimeMessage
        try {
            // make sure we have a connection to the mail store
            ensureConnectedStore();

            // retrieve the indicated folder
            Folder folder = store.getFolder(folderName);

            // determine the message UID
            int index = url.lastIndexOf(folder.getSeparator());
            if (index < 0 || index >= url.length() - 1) {
                throw new IllegalArgumentException("unable to get message UID from " + url);
            }
            String messageNumberString = url.substring(index + 1);

            long messageUID;
            try {
                messageUID = Long.parseLong(messageNumberString);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("illegal message UID: " + messageNumberString);
            }

            // retrieve the message
            message = (MimeMessage) ((UIDFolder) folder).getMessageByUID(messageUID);
        }
        catch (MessagingException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
        finally {
            closeConnection();
        }

        // create a DataObject for this message
        try {
            return getObject(message, url, source, accessData, containerFactory);
        }
        catch (MessagingException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    private DataObject getObject(MimeMessage message, String url, DataSource source, AccessData accessData,
            RDFContainerFactory containerFactory) throws MessagingException, IOException {
        // See if this url has been accessed before so that we can stop immediately. Note
        // that no check on message date is done as messages are immutable. Therefore we only have to
        // check whether the AccessData knows this ID.
        if (accessData != null && accessData.get(url, ACCESSED_KEY) != null) {
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
            DataObjectFactory factory = new DataObjectFactory(source, containerFactory);
            List objects = factory.createDataObjects(message, messageUrl);

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
        if (accessData != null) {
            accessData.put(url, ACCESSED_KEY, "");
        }

        return result;
    }
}
