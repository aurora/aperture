package org.semanticdesktop.aperture.websites.flickr;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Aug 22 19:06:51 CEST 2007
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/websites/flickr/flickrDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#
 */
public class FLICKRDS {

    /** Path to the ontology resource */
    public static final String FLICKRDS_RESOURCE_PATH = 
      FLICKRDS.class.getPackage().getName().replace('.', '/') + "/flickrDataSource.ttl";

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
    public static final URI ItemsAndTagsCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#ItemsAndTagsCrawlType");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType <br/>
     */
    public static final URI ItemsOnlyCrawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#ItemsOnlyCrawlType");
    /**
     * Type: Property <br/>
     * Label: Crawl type  <br/>
     * Comment: Type of crawl  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#FlickrDataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#CrawlType  <br/>
     */
    public static final URI crawlType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/flickrds#crawlType");
}
