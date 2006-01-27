/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A Crawler implementation for crawling file system sources modeled by a FileSystemDataSource.
 */
public class FileSystemCrawler extends CrawlerBase {

    private static final boolean DEFAULT_IGNORE_HIDDEN_FILES = true;

    private static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;

    private static final int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;

    private static final Logger LOGGER = Logger.getLogger(FileSystemCrawler.class.getName());

    private DataAccessorRegistry accessorRegistry;

    private boolean ignoreHiddenFiles;

    private int maximumSize;

    private DataAccessorFactory accessorFactory;

    private HashMap params;

    public void setDataAccessorRegistry(DataAccessorRegistry registry) {
        accessorRegistry = registry;
    }

    protected ExitCode crawlObjects() {
        // fetch the source and its configuration
        DataSource source = getDataSource();
        RDFContainer configuration = source.getConfiguration();
        
        // determine the root file
        File root = getRootFile(configuration);
        if (root == null) {
            // treat this as an error rather than an "empty source" to prevent information loss
            LOGGER.log(Level.SEVERE, "missing root file");
            return ExitCode.FATAL_ERROR;
        }
        root = root.getAbsoluteFile();

        // determine the maximum depth
        Integer i = ConfigurationUtil.getMaximumDepth(configuration);
        int maxDepth = i == null ? DEFAULT_MAX_DEPTH : i.intValue();

        // determine the maximum byte size
        i = ConfigurationUtil.getMaximumByteSize(configuration);
        maximumSize = i == null ? DEFAULT_MAX_SIZE : i.intValue();

        // determine whether we should crawl hidden files and directories
        Boolean b = ConfigurationUtil.getIncludeHiddenResourceS(configuration);
        ignoreHiddenFiles = b == null ? DEFAULT_IGNORE_HIDDEN_FILES : b.booleanValue();

        // init some other params
        params = new HashMap(2);
        getAccessorFactory();

        // crawl the file tree
        boolean crawlCompleted = crawlFileTree(root, maxDepth);

        // clean-up
        params = null;

        // determine the exit code
        return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
    }

    private File getRootFile(RDFContainer configuration) {
        String rootUrl = ConfigurationUtil.getRootUrl(configuration);
        if (rootUrl == null) {
            return null;
        }

        URI uri = null;
        try {
            uri = new URI(rootUrl);
        }
        catch (URISyntaxException e) {
            return null;
        }

        return new File(uri);
    }
    
    /**
     * Retrieves a DataAccessorFactory for the file scheme and throws an exception when there is no such
     * factory or when the DataAccessorRegistry has not been set.
     */
    private void getAccessorFactory() {
        if (accessorRegistry == null) {
            throw new IllegalStateException("DataAccessorRegistry not set");
        }

        Set factories = accessorRegistry.get("file");

        if (factories != null && !factories.isEmpty()) {
            accessorFactory = (DataAccessorFactory) factories.iterator().next();
        }
        else {
            throw new IllegalStateException("Could not retrieve a file data accessor");
        }
    }

    /**
     * Crawls a File tree.
     * 
     * @return true if the path has been crawler completely, false if the crawl was aborted.
     */
    private boolean crawlFileTree(File file, int depth) {
        if (file.isFile() && depth >= 0) {
            // report the File
            if (inDomain(file) && file.canRead() && file.length() <= maximumSize) {
                crawlSingleFile(file);
            }

            // by definition we've completed this subtree
            return true;
        }
        else if (file.isDirectory() && depth >= 0) {
            // report the Folder itself
            if (inDomain(file)) {
                crawlSingleFile(file);
            }

            // report nested Files
            if (depth > 0) {
                File[] nestedFiles = file.listFiles();

                if (nestedFiles == null) {
                    // This happens on certain "special" directories, although the
                    // API documentation doesn't mention it, see java bug #4803836.
                    return true;
                }

                int i = 0;
                for (; !stopRequested && i < nestedFiles.length; i++) {
                    File nestedFile = nestedFiles[i];

                    if (ignoreHiddenFiles && nestedFile.isHidden()) {
                        continue;
                    }

                    boolean scanCompleted = crawlFileTree(nestedFile, depth - 1);

                    if (!scanCompleted) {
                        return false;
                    }
                }

                // scan has been completed when i has reached the end of the array successfully
                return i == nestedFiles.length;
            }
            else {
                return true;
            }
        }
        else {
            // Unknown path type (is this possible?) or depth < 0
            return true;
        }
    }

    private boolean inDomain(File file) {
        // FIXME: properly implement this method as soon as DataSourceBase has support for setting and
        // retrieving include and exclude patterns
        return true;
    }

    /**
     * Crawls a single File and reports it to the registered DataSourceListeners.
     */
    private void crawlSingleFile(File file) {
        // create an identifier for the file
        String url = file.toURI().toString();

        // register that we're processing this file
        handler.accessingObject(this, url);
        deprecatedUrls.remove(url);

        // see if this object has been encountered before (we must do this before applying the accessor!)
        boolean knownObject = accessData.isKnownId(url);

        // fetch a RDFContainer from the handler (note: is done for every
        RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, url);

        // fetch the DataObject
        DataAccessor accessor = accessorFactory.get();
        params.put("file", file);
        try {
            DataObject dataObject = accessor.getDataObjectIfModified(url, source, accessData, params,
                    containerFactory);

            if (dataObject == null) {
                // the object was not modified
                handler.objectNotModified(this, url);
                crawlReport.increaseUnchangedCount();
            }
            else {
                // we scanned a new or changed object
                if (knownObject) {
                    handler.objectChanged(this, dataObject);
                    crawlReport.increaseChangedCount();
                }
                else {
                    handler.objectNew(this, dataObject);
                    crawlReport.increaseNewCount();
                }
            }
        }
        catch (UrlNotFoundException e) {
            LOGGER.log(Level.WARNING, "unable to access " + url, e);
        }
        catch (IOException e) {
            LOGGER.log(Level.WARNING, "I/O error while processing " + url, e);
        }
    }
}
