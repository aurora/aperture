/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:55:05 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/filesystem/filesystemDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#
 */
public class FILESYSTEMDS {

    /** Path to the ontology resource */
    public static final String FILESYSTEMDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/datasource/filesystem/filesystemDataSource.ttl";

    /**
     * Puts the FILESYSTEMDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getFILESYSTEMDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(FILESYSTEMDS_RESOURCE_PATH, FILESYSTEMDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + FILESYSTEMDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for FILESYSTEMDS */
    public static final URI NS_FILESYSTEMDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#");
    /**
     * Type: Class <br/>
     * Label: Filesystem Data Source  <br/>
     * Comment: A data source describing a folder or a filesystem containing files  <br/>
     */
    public static final URI FileSystemDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource");
    /**
     * Type: Property <br/>
     * Label: Root Folder  <br/>
     * Comment: Path to the root of the folder tree to be crawled  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rootFolder = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#rootFolder");
    /**
     * Type: Property <br/>
     * Label: Maximum Depth  <br/>
     * Comment: How many levels below the root folder should the crawled descend.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#maximumDepth");
    /**
     * Type: Property <br/>
     * Label: Maximum Size  <br/>
     * Comment: Maximum size (in bytes) of files reported by the crawler  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#long  <br/>
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#maximumSize");
    /**
     * Type: Property <br/>
     * Label: Include hidden resources  <br/>
     * Comment: Should the hidden files and folders be included in crawl results?  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI includeHiddenResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#includeHiddenResources");
    /**
     * Type: Property <br/>
     * Label: Follow symbolic links  <br/>
     * Comment: Should the crawler follow symbolic links?  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI followSymbolicLinks = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#followSymbolicLinks");
    /**
     * Type: Property <br/>
     * Label: Supress the addition of parent->child nie:hasPart triples to the folder metadata  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI suppressParentChildLinks = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/filesystemds#suppressParentChildLinks");
}
