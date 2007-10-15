package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Mon Oct 15 17:27:40 CEST 2007
 * input file: D:\workspace\aperture/doc/ontology/source2.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#
 */
public class DATASOURCE {

    /** Path to the ontology resource */
    public static final String DATASOURCE_RESOURCE_PATH = 
      DATASOURCE.class.getPackage().getName().replace('.', '/') + "/source2.rdfs";

    /**
     * Puts the DATASOURCE ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getDATASOURCEOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(DATASOURCE_RESOURCE_PATH, DATASOURCE.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + DATASOURCE_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for DATASOURCE */
    public static final URI NS_DATASOURCE = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#");
    /**
     * Type: Class <br/>
     * Label: DataSourceDescription  <br/>
     * Comment: A human-readable description of a datasource. Adds icons, texts etc.  <br/>
     */
    public static final URI DataSourceDescription = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#DataSourceDescription");
    /**
     * Type: Class <br/>
     * Label: Pattern  <br/>
     */
    public static final URI Pattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#Pattern");
    /**
     * Type: Class <br/>
     * Label: RegExpPattern  <br/>
     */
    public static final URI RegExpPattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#RegExpPattern");
    /**
     * Type: Class <br/>
     * Label: SubstringPattern  <br/>
     */
    public static final URI SubstringPattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#SubstringPattern");
    /**
     * Type: Class <br/>
     * Label: STARTS_WITH  <br/>
     */
    public static final URI STARTS_WITH = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#STARTS_WITH");
    /**
     * Type: Class <br/>
     * Label: ENDS_WITH  <br/>
     */
    public static final URI ENDS_WITH = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#ENDS_WITH");
    /**
     * Type: Class <br/>
     * Label: CONTAINS  <br/>
     */
    public static final URI CONTAINS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#CONTAINS");
    /**
     * Type: Class <br/>
     * Label: DOES_NOT_CONTAIN  <br/>
     */
    public static final URI DOES_NOT_CONTAIN = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#DOES_NOT_CONTAIN");
    /**
     * Type: Property <br/>
     * Label: condition  <br/>
     * Comment: the condition for this pattern to match  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#Pattern  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI condition = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#condition");
    /**
     * Type: Property <br/>
     * Label: Data Source Comment  <br/>
     * Comment: A comment about the datasource.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI dataSourceComment = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#dataSourceComment");
    /**
     * Type: Property <br/>
     * Label: Data Source Name  <br/>
     * Comment: Name of the type of datasource. For example "Local File System".  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI dataSourceName = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#dataSourceName");
    /**
     * Type: Property <br/>
     * Label: describedDataSourceType  <br/>
     * Comment: The datasource described  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#DataSourceDescription  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     */
    public static final URI describedDataSourceType = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#describedDataSourceType");
    /**
     * Type: Property <br/>
     * Label: Exclusion Pattern  <br/>
     * Comment: Pattern to exclude from this datasource  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#Pattern  <br/>
     */
    public static final URI excludePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#excludePattern");
    /**
     * Type: Property <br/>
     * Label: Inclusion Pattern  <br/>
     * Comment: Pattern to include in this datasource  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#Pattern  <br/>
     */
    public static final URI includePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#includePattern");
    /**
     * Type: Property <br/>
     * Label: Timeout  <br/>
     * Comment: The timeout between two consecutive crawls (in miliseconds)  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/source#DataSource  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI timeout = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#timeout");
    /**
     * Type: Property <br/>
     * Label: Username  <br/>
     * Comment: Username used for authentication in a data source  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI username = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#username");
    /**
     * Type: Property <br/>
     * Label: Password  <br/>
     * Comment: The Password used to access this datasource.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI password = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/source#password");
}
