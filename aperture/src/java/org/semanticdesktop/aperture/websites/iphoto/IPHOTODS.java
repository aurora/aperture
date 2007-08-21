package org.semanticdesktop.aperture.websites.iphoto;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Mon Aug 13 17:51:22 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/websites/iphoto/iphotoDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#
 */
public class IPHOTODS {

    /** Path to the ontology resource */
    public static final String IPHOTODS_RESOURCE_PATH = 
      IPHOTODS.class.getPackage().getName().replace('.', '/') + "/iphotoDataSource.ttl";

    /**
     * Puts the IPHOTODS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getIPHOTODSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(IPHOTODS_RESOURCE_PATH, IPHOTODS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + IPHOTODS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for IPHOTODS */
    public static final URI NS_IPHOTODS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#");
    /**
     * Type: Class <br/>
     */
    public static final URI IPhotoKeywordDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#IPhotoKeywordDataSource");
}
