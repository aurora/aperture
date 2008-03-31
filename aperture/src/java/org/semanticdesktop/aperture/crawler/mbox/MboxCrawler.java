/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mbox;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import net.fortuna.mstor.util.CapabilityHints;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.mail.AbstractJavaMailCrawler;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.mbox.MboxDataSource;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A crawler implementation for mbox files.
 */
@SuppressWarnings("unchecked")
public class MboxCrawler extends AbstractJavaMailCrawler {

    private static final String MBOX_URL_SCHEME = "mbox:";
    
    private static final String MSTOR_PROVIDER_PATH_PREFX = "mstor:";

    private static final String SIZE_KEY = "size";

    private static final String SUBFOLDERS_KEY = "subfolders";
    
    //private static final String FOLDER_LAST_MODIFIED = "folderLastModified";

    private Logger logger = LoggerFactory.getLogger(getClass());

    // The source whose properties we're currently using. 
    // I actually don't know what do we need it for (apart from the obvious convenience of not having
    // to cast the super.dataSource to (MboxDataSource) every time we use it).
    // It's a copy-paste from the IMAPCrawler
    private MboxDataSource configuredDataSource;

    private Store store;
    
    private String mboxStoreUri;

    /* ----------------------------- Crawler implementation ----------------------------- */

    protected ExitCode crawlObjects() {
        // determine host name, user name, etc.
        retrieveConfigurationData(getDataSource());

        // make sure we have a connection to the mail store
        try {
            ensureConnectedStore();
        }
        catch (MessagingException e) {
            logger.warn("Unable to open and MBOX file", e);
            closeConnection();
            return ExitCode.FATAL_ERROR;
        }

        // crawl the folder contents
        boolean fatalError = false;

        try {
            // crawl all specified base folders
            int nrFolders = baseFolders.size();
            if (nrFolders == 0) {
                Folder folder = store.getDefaultFolder();
                folder.open(Folder.READ_ONLY);
                crawlFolder(store.getDefaultFolder(), 0);
            }
            else {
                for (int i = 0; i < nrFolders; i++) {
                    String baseFolderName = (String) baseFolders.get(i);
                    Folder baseFolder = store.getFolder(baseFolderName);
                    crawlFolder(baseFolder, 0);
                }
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
        configuredDataSource = (MboxDataSource)dataSource;

        baseFolders.clear();

        String mboxFilePath = configuredDataSource.getMboxPath();
        if (mboxFilePath == null) {
            // hehe, the crawler API doesn't allow us to throw an exception at this point
            // the crawl will quickly end with an error and the user will have to figure out
            // the cause by him/herself
            mboxStoreUri = null;
        } else {
            // let's hope that mstor will be able to cope with spaces in the paths
            // this replace has been inspired by the example at the http://mstor.sourceforge.net/
            // main page
            mboxStoreUri = MSTOR_PROVIDER_PATH_PREFX + mboxFilePath.replace('\\', '/');;
        }
        
        Integer maxDepthI = configuredDataSource.getMaximumDepth();

        if (maxDepthI == null) {
            maxDepth = -1;
        }
        else {
            maxDepth = maxDepthI.intValue();
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
            CapabilityHints.setHint(CapabilityHints.KEY_METADATA, CapabilityHints.VALUE_METADATA_DISABLED);            
            CapabilityHints.setHint(CapabilityHints.KEY_MBOX_CACHE_BUFFERS, CapabilityHints.VALUE_MBOX_CACHE_BUFFERS_DISABLED);
            CapabilityHints.setHint(CapabilityHints.KEY_MBOX_BUFFER_STRATEGY, CapabilityHints.VALUE_MBOX_BUFFER_STRATEGY_DEFAULT);
            System.setProperty("mstor.cache.maxentries", "1");
            Session session = Session.getDefaultInstance(new Properties());
            store = session.getStore(new URLName(mboxStoreUri));
        }

        // make sure it is connected
        if (!store.isConnected()) {
            store.connect();
        }
    }

    /**
     * Closes any connections this ImapCrawler may have to an IMAP server. Afterwards, the InputStream of any
     * returned FileDataObjects may no longer be accessible. Invoking this method when no connections are open
     * has no effect.
     */
    private void closeConnection() {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                logger.warn("Unable to close connection", e);
            }
        }
    }
        
    @Override
    protected void recordCurrentFolderInAccessData(AccessData newAccessData) throws MessagingException {        
        if (newAccessData != null) {
            if (holdsMessages(currentFolder)) {
                int messageCount = currentFolder.getMessageCount() - currentFolder.getDeletedMessageCount();
                newAccessData.put(currentFolderURI.toString(), SIZE_KEY, String.valueOf(messageCount));
            }
            if (holdsFolders(currentFolder)) {
                newAccessData.put(currentFolderURI.toString(), SUBFOLDERS_KEY, getSubFoldersString(currentFolder));
            }
        }
    }
    
    @Override
    protected boolean checkIfCurrentFolderHasBeenChanged(AccessData newAccessData) throws MessagingException{
        //Message[] messages = null;
        if (!currentFolder.isOpen()) {
            // this is a crappy hack to solve the problems with the root folder of a multi-folder thunderbird
            // mailbox being a special case
            // it is a javax.mail.Folder, but it doesn't contain messages and therefore is not open by the
            // abstract javamail crawler by default
            // this depends on the equally crappy hack that makes the crawler crawl into folders that have
            // been reported as changed
            return false;
        }
        //messages = folder.getMessages();
        
//        IMAPFolder imapFolder = (IMAPFolder)folder;
//        
//        if (newAccessData != null) {
//            // the results default to 'true'; unless we can find complete evidence that the folder has not
//            // changed, we will always access the folder
//            boolean messagesChanged = true;
//            boolean foldersChanged = true;
//
//            if (holdsMessages(folder)) {
//                String nextUIDString = newAccessData.get(url, NEXT_UID_KEY);
//                String sizeString = newAccessData.get(url, SIZE_KEY);
//
//                // note: this is -1 for servers that don't support retrieval of a next UID, meaning that the
//                // folder will always be reported as changed when it should have been unmodified
//                long nextUID = imapFolder.getUIDNext();
//
//                if (nextUIDString != null && sizeString != null && nextUID != -1L) {
//                    try {
//                        // parse stored information
//                        long previousNextUID = Long.parseLong(nextUIDString);
//                        long previousSize = Integer.parseInt(sizeString);
//
//                        // determine the new folder size, excluding all deleted/deletion-marked messages
//                        messages = folder.getMessages();
//                        FetchProfile profile = new FetchProfile();
//                        profile.add(FetchProfile.Item.FLAGS); // needed for DELETED flag
//                        folder.fetch(messages, profile);
//                        int messageCount = getMessageCount(messages);
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
//            if (holdsFolders(folder)) {
//                String registeredSubFolders = newAccessData.get(url, SUBFOLDERS_KEY);
//
//                if (registeredSubFolders != null) {
//                    String subfolders = getSubFoldersString(folder);
//                    if (registeredSubFolders.equals(subfolders)) {
//                        foldersChanged = false;
//                    }
//                }
//            }
//
//            if (!messagesChanged && !foldersChanged) {
//                // the folder contents have not changed, we can return immediately
//                logger.debug("Folder \"" + folder.getFullName() + "\" has not changed.");
//                return null;
//            }
//
//            logger.debug("Folder \"" + folder.getFullName() + "\" is new or has changes.");
//        }
        return true;
    }


    
    /* -------------------- Methods related to URI generation --------------------- */
    
    private String getFolderURIPrefix(Folder folder) throws MessagingException {
        StringBuilder buffer = new StringBuilder(100);

        // start with 'scheme'
        buffer.append(MBOX_URL_SCHEME);

        // append path
        buffer.append('/');
        buffer.append(encodeFolderPath(folder.getFullName()));

        return buffer.toString();
    }

    
    private String encodeFolderPath(String string) {
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
                        || cInt == '/' || cInt == ':' || cInt == '.' || cInt == '-') {
                    // alphanumeric character or slash or colon or dot or hyphen
                    buffer.append(c);
                } else if (cInt == '\\'){
                    // convert backslashes to slashes
                    buffer.append('/');
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
        return new URIImpl(getFolderURIPrefix(folder));
    }
    
    @Override
    protected String getMessageUri(Folder folder, Message message) throws MessagingException{
        String [] messageIds = message.getHeader("Message-ID");
        String id = null;
        String hash = null;
        try {
            hash = IOUtil.rollingHash(message.getInputStream());
        }
        catch (IOException e1) {
            throw new MessagingException("Couldn't obtain a hash of the message");
        }
        
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
            id = String.valueOf(builder.toString().hashCode());
        }
        return getFolderURIPrefix(folder) + "/" + id + "-" + hash;
    }
}
