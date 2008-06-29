package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Jun 29 15:41:38 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/tagging.rdfs
 * namespace: http://aperture.sourceforge.net/ontologies/taggingl#
 */
public class TAGGING {

    /** Path to the ontology resource */
    public static final String TAGGING_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/tagging.rdfs";

    /**
     * Puts the TAGGING ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getTAGGINGOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(TAGGING_RESOURCE_PATH, TAGGING.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + TAGGING_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for TAGGING */
    public static final URI NS_TAGGING = new URIImpl("http://aperture.sourceforge.net/ontologies/taggingl#");
    /**
     * Type: Class <br/>
     * Label: Item  <br/>
     * Comment: An item that may be tagged, links, photos, etc.  <br/>
     */
    public static final URI Item = new URIImpl("http://aperture.sourceforge.net/ontologies/tagging#Item");
    /**
     * Type: Class <br/>
     * Label: Link  <br/>
     */
    public static final URI Link = new URIImpl("http://aperture.sourceforge.net/ontologies/tagging#Link");
    /**
     * Type: Class <br/>
     * Label: Photo  <br/>
     */
    public static final URI Photo = new URIImpl("http://aperture.sourceforge.net/ontologies/tagging#Photo");
    /**
     * Type: Class <br/>
     * Label: Tag  <br/>
     * Comment: A Tag.  <br/>
     */
    public static final URI Tag = new URIImpl("http://aperture.sourceforge.net/ontologies/tagging#Tag");
    /**
     * Type: Property <br/>
     * Label: hasTag  <br/>
     * Comment: this item was tagged with the this tag  <br/>
     * Domain: http://aperture.sourceforge.net/ontologies/tagging#Item  <br/>
     * Range: http://aperture.sourceforge.net/ontologies/tagging#Tag  <br/>
     */
    public static final URI hasTag = new URIImpl("http://aperture.sourceforge.net/ontologies/tagging#hasTag");
}
