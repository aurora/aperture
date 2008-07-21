/**
 * 
 */
package org.semanticdesktop.aperture.websites.bibsonomy;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.vocabulary.TAGGING;
import org.semanticdesktop.aperture.websites.AbstractTagCrawler;
import org.semanticdesktop.aperture.websites.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BibsonomyCrawler extends AbstractTagCrawler {

    private static final String BIBSONOMY_URL = "http://www.bibsonomy.org/rss/user/";

    private static final String TAG_URL = "http://www.bibsonomy.org/user/";

    private static final String TAXO_NS = "http://purl.org/rss/1.0/modules/taxonomy/";

    private Logger log = LoggerFactory.getLogger(BibsonomyCrawler.class);

    private Map<Tag, List<String>> links;

    /**
     * @param dataSource
     */
    public BibsonomyCrawler(DataSource dataSource) {
        super();
        BibsonomyDataSource source = (BibsonomyDataSource)dataSource;
        setDataSource(source);
    }

    @Override
    protected List<String> crawlTags(String username, String password) throws Exception {

        if (username == null || username.equals(""))
            throw new IllegalArgumentException("Username may not be empty");

        // Model m=ModelFactory.createDefaultModel();
        // m.read(BIBSONOMY_URL+username);

        URL url = new URL(BIBSONOMY_URL + username + "?items=10000");

        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        model.readFrom(url.openStream(), Syntax.RdfXml);

        /*
         * CloseableIterator<RStatement> i = rep.extractStatements(); while (i.hasNext())
         * System.err.println(i.next()); i.close();
         */
        String q = 
            "PREFIX rss: <http://purl.org/rss/1.0/> \n" + 
            "PREFIX taxo: <" + TAXO_NS + "> \n" + 
            "PREFIX rdf: <" + RDF.RDF_NS + "> \n" + 
            "SELECT ?i ?l ?t \n" + 
            "WHERE { ?i rss:title ?l . \n" + 
            "        ?i taxo:topics ?blank . \n" + 
            "        ?blank ?predicate ?t . \n" + 
            "        FILTER ( ?predicate != rdf:type ) } \n";

        QueryResultTable table = null;
        ClosableIterator<QueryRow> iterator = null;
        Set<String> resSet = new HashSet<String>();
        try {
            table = model.sparqlSelect(q);
            iterator = table.iterator();

            
            links = new HashMap<Tag, List<String>>();

            while (iterator.hasNext()) {
                QueryRow row = iterator.next();
                Tag li = new Tag(row.getValue("i").toString(), row.getValue("l").toString(), TAGGING.Link
                        .toString());

                List<String> tags = links.get(li);
                if (tags == null) {
                    tags = new Vector<String>();
                    links.put(li, tags);
                }
                String tag = row.getValue("t").toString();
                tags.add(tag);
                resSet.add(tag);

            }
        }
        finally {
            closeIterator(iterator);
        }
        List<String> res = new Vector<String>(resSet);
        return res;
    }

    private void closeIterator(ClosableIterator<? extends Object> iterator) {
        if (iterator != null) {
            try {
                iterator.close();
            }
            catch (Exception e) {
                log.warn("Coudln't close an iterator", e);
            }
        }

    }

    protected void crawlTheRest(String username, String password) throws Exception {
        // crawl the rest
        BibsonomyDataSource.CrawlType crawlType = ((BibsonomyDataSource)source).getCrawlType();
        if (crawlType != null && 
            (crawlType.equals(BibsonomyDataSource.CrawlType.TagsAndItemsCrawlType))) {
            for (Entry<Tag, List<String>> e : links.entrySet()) {
                reportItem(e.getKey(), e.getValue());
            }
        }
    }

    public void postTag(String url, String title, String description, List<Tag> tags) {

        String postURL = "http://www.bibsonomy.org/bookmark_posting_process";

        // Params: url, description, extended, tags

    }

}
