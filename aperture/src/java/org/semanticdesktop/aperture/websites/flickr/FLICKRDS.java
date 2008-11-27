/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.flickr;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Sep 10 18:58:36 CEST 2008
 * input file: /Users/ck/Entwicklung/nepomuk/workspace2/aperture/src/java/org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#
 */
public class FLICKRDS {

    /** Path to the ontology resource */
    public static final String FLICKRDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl";

    /**
     * Puts the FLICKRDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getFLICKRDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(FLICKRDS_RESOURCE_PATH, FLICKRDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + FLICKRDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for FLICKRDS */
    public static final URI NS_FLICKRDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#");
    /**
     * Type: Class <br/>
     * Label: Flickr Data Source  <br/>
     * Comment: Describes a Flickr account  <br/>
     */
    public static final URI FlickrDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource");
    /**
     * Type: Class <br/>
     */
    public static final URI CrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType <br/>
     */
    public static final URI MetadataAndPicturesCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#MetadataAndPicturesCrawlType");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType <br/>
     */
    public static final URI MetadataOnlyCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#MetadataOnlyCrawlType");
    /**
     * Type: Property <br/>
     * Label: Crawl type  <br/>
     * Comment: Type of crawl  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType  <br/>
     */
    public static final URI crawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#crawlType");
    /**
     * Type: Property <br/>
     * Label: Photo Folder  <br/>
     * Comment: Path to the folder where local copies of your photos will be stored  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FileSystemDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI targetFolder = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#targetFolder");
}
