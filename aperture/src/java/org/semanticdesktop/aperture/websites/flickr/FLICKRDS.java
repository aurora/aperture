/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.flickr;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Dec 03 00:32:23 CET 2008
 * input file: D:\ganymedeworkspace\aperture-trunk/src/java/org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl
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
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI targetFolder = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#targetFolder");
    /**
     * Type: Property <br/>
     * Label: Account the crawl  <br/>
     * Comment: The Flickr account that should be crawled  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI accountToCrawl = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#accountToCrawl");
    /**
     * Type: Property <br/>
     * Label: API Key  <br/>
     * Comment: The Flickr API key  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI apikey = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#apikey");
    /**
     * Type: Property <br/>
     * Label: Shared secret  <br/>
     * Comment: The shared secret associated with your Flicckr API key  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sharedSecret = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#sharedSecret");
}
