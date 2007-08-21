package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Aug 21 16:32:37 CEST 2007
 * input file: D:\workspace\aperture/doc/ontology/geo.rdfs
 * namespace: http://www.w3.org/2003/01/geo/wgs84_pos#
 */
public class GEO {

    /** Path to the ontology resource */
    public static final String GEO_RESOURCE_PATH = 
      GEO.class.getPackage().getName().replace('.', '/') + "/geo.rdfs";

    /**
     * Puts the GEO ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getGEOOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(GEO_RESOURCE_PATH, GEO.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + GEO_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for GEO */
    public static final URI NS_GEO = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#");
    /**
     * Type: Class <br/>
     * Label: SpatialThing  <br/>
     * Comment: Anything with spatial extent, i.e. size, shape, or position. e.g. people, places, bowling balls, as well as abstract areas like cubes.  <br/>
     */
    public static final URI SpatialThing = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing");
    /**
     * Type: Class <br/>
     * Label: Point  <br/>
     * Comment: A point, typically described using a coordinate system relative to Earth, such as WGS84. Uniquely identified by lat/long/alt. i.e.

spaciallyIntersects(P1, P2) :- lat(P1, LAT), long(P1, LONG), alt(P1, ALT),
  lat(P2, LAT), long(P2, LONG), alt(P2, ALT).

sameThing(P1, P2) :- type(P1, Point), type(P2, Point), spaciallyIntersects(P1, P2).  <br/>
     */
    public static final URI Point = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#Point");
    /**
     * Type: Property <br/>
     * Label: latitude  <br/>
     * Comment: The WGS84 latitude of a SpatialThing (decimal degrees).  <br/>
     * Domain: http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing  <br/>
     */
    public static final URI lat = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
    /**
     * Type: Property <br/>
     * Label: longitude  <br/>
     * Comment: The WGS84 longitude of a SpatialThing (decimal degrees).  <br/>
     * Domain: http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing  <br/>
     */
    public static final URI long_ = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#long");
    /**
     * Type: Property <br/>
     * Label: altitude  <br/>
     * Comment: The WGS84 altitude of a SpatialThing (decimal meters 
above the local reference ellipsoid).  <br/>
     * Domain: http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing  <br/>
     */
    public static final URI alt = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#alt");
    /**
     * Type: Property <br/>
     * Label: lat/long  <br/>
     * Comment: A comma-separated representation of a latitude, longitude coordinate.  <br/>
     */
    public static final URI lat_long = new URIImpl("http://www.w3.org/2003/01/geo/wgs84_pos#lat_long");
}
