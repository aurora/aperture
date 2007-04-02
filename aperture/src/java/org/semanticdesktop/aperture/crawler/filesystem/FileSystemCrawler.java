/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.OSUtils;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Crawler implementation for crawling file system sources modeled by a FileSystemDataSource.
 */
public class FileSystemCrawler extends CrawlerBase {

    private static final boolean DEFAULT_IGNORE_HIDDEN_FILES = true;

    private static final boolean DEFAULT_FOLLOW_SYMBOLIC_LINKS = false;

    private static final int DEFAULT_MAX_DEPTH = Integer.MAX_VALUE;

    private static final long DEFAULT_MAX_SIZE = Long.MAX_VALUE;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private boolean ignoreHiddenFiles;

    private boolean followSymbolicLinks;

    private long maximumSize;

    private DataAccessorFactory accessorFactory;

    private HashMap params;

    private File root;

    protected ExitCode crawlObjects() {
        // fetch the source and its configuration
        DataSource source = getDataSource();
        RDFContainer configuration = source.getConfiguration();

        // determine the root file
        String rootFolder = ConfigurationUtil.getRootFolder(configuration);
        if (rootFolder == null) {
            // treat this as an error rather than an "empty source" to prevent information loss when e.g. a
            // network drive is temporarily unavailable
            logger.warn("missing root folder");
            return ExitCode.FATAL_ERROR;
        }
        root = new File(rootFolder);
        if (!root.exists()) {
            logger.warn("root folder does not exist: '" + root + "'");
            return ExitCode.FATAL_ERROR;
        }

        // Resolve the root folder to its canonical form. Canonicalization is also done in CrawlFileTree for
        // every single accessed File but this is part of a procedure to determine whether the file is a
        // symbolic link. Doing it here one extra time for the root folder allows the specification of a root
        // folder whose path includes a symbolic link. Without this extra step, the "follow symbolic links"
        // setting could make crawling of such a source impossible. Therefore, symbolic links in the path of
        // the root folder are always allowed, symbolic links that are encountered later on when descending in
        // the file tree are optionally crawled
        try {
            root = root.getCanonicalFile();
        }
        catch (IOException e) {
            logger.warn("unable to determine canonical file of root folder " + root, e);
            return ExitCode.FATAL_ERROR;
        }

        // determine the maximum depth
        Integer i = ConfigurationUtil.getMaximumDepth(configuration);
        int maxDepth = i == null ? DEFAULT_MAX_DEPTH : i.intValue();

        // determine the maximum byte size
        Long l = ConfigurationUtil.getMaximumByteSize(configuration);
        maximumSize = l == null ? DEFAULT_MAX_SIZE : l.longValue();

        // determine whether we should crawl hidden files and directories
        Boolean b = ConfigurationUtil.getIncludeHiddenResourceS(configuration);
        ignoreHiddenFiles = b == null ? DEFAULT_IGNORE_HIDDEN_FILES : b.booleanValue();

        // determine whether we should crawl symbolic links
        b = ConfigurationUtil.getFollowSymbolicLinks(configuration);
        followSymbolicLinks = b == null ? DEFAULT_FOLLOW_SYMBOLIC_LINKS : b.booleanValue();

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
     * @return true if the path has been crawled completely, false if the crawl was aborted.
     */
    private boolean crawlFileTree(File file, int depth) {
        // resolve the file to its canonical form
        try {
            // determine absolute and canonical paths
            String absolutePath = file.getAbsolutePath();
            String canonicalPath = file.getCanonicalPath();

            // optionally skip symbolic links
            if (!followSymbolicLinks && !absolutePath.equals(canonicalPath)) {
                return true;
            }

            // create the canonical File
            file = new File(canonicalPath);
        }
        catch (IOException e) {
            logger.warn("unable to resolve file to its canocical form, continuing with original file: "
                    + file, e);
        }

        if (file.isFile() && depth >= 0) {
            // report the File
            if (inDomain(file.toURI().toString()) && file.canRead() && file.length() <= maximumSize) {
                crawlSingleFile(file);
            }
            else {
                logger.info("File " + file.toURI() + " is not in domain. Skipping.");
            }

            // by definition we've completed this subtree
            return true;
        }
        else if (file.isDirectory() && depth >= 0) {
            // report the Folder itself
            if (inDomain(file.toURI().toString())) {
                crawlSingleFile(file);
            }
            else {
                logger.info("Directory " + file.toURI() + " is not in domain. Skipping.");
            }

            // Dont crawl into MacOSX bundles.
            if (OSUtils.isMac() && OSUtils.isMacOSXBundle(file))
                return true;

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
        boolean knownObject = accessData == null ? false : accessData.isKnownId(url);

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

                // If this is the root folder, add that info to the metadata
                if (file.equals(root)) {
                    dataObject.getMetadata().add(DATA.rootFolderOf, source.getID());
                }

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
            logger.warn("unable to access " + url, e);
        }
        catch (IOException e) {
            logger.warn("I/O error while processing " + url, e);
        }
    }
}
