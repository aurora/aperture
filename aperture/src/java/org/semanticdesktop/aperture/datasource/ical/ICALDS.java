package org.semanticdesktop.aperture.datasource.ical;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Mon Aug 13 17:51:24 CEST 2007
 * input file: D:\workspace\aperture-nie/src/java/org/semanticdesktop/aperture/datasource/ical/icalDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#
 */
public class ICALDS {

    /** Path to the ontology resource */
    public static final String ICALDS_RESOURCE_PATH = 
      ICALDS.class.getPackage().getName().replace('.', '/') + "/icalDataSource.ttl";

    /**
     * Puts the ICALDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getICALDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(ICALDS_RESOURCE_PATH, ICALDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + ICALDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for ICALDS */
    public static final URI NS_ICALDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#");
    /**
     * Type: Class <br/>
     */
    public static final URI IcalDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#IcalDataSource");
    /**
     * Type: Property <br/>
     * Label: Root URL  <br/>
     * Comment: URL of the ical file to be crawled.  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#IcalDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/icalds#rootUrl");
}
