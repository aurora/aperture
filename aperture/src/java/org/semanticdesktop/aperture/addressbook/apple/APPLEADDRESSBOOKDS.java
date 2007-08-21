package org.semanticdesktop.aperture.addressbook.apple;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Aug 21 16:33:01 CEST 2007
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/addressbook/apple/AppleAddressbookDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/appleaddresbookds#
 */
public class APPLEADDRESSBOOKDS {

    /** Path to the ontology resource */
    public static final String APPLEADDRESSBOOKDS_RESOURCE_PATH = 
      APPLEADDRESSBOOKDS.class.getPackage().getName().replace('.', '/') + "/AppleAddressbookDataSource.ttl";

    /**
     * Puts the APPLEADDRESSBOOKDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getAPPLEADDRESSBOOKDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(APPLEADDRESSBOOKDS_RESOURCE_PATH, APPLEADDRESSBOOKDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + APPLEADDRESSBOOKDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for APPLEADDRESSBOOKDS */
    public static final URI NS_APPLEADDRESSBOOKDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/appleaddresbookds#");
    /**
     * Type: Class <br/>
     * Label: Apple Addresbook Data Source  <br/>
     * Comment: Describes an apple addresbook  <br/>
     */
    public static final URI AppleAddressbookDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/appleaddresbookds#AppleAddressbookDataSource");
}
