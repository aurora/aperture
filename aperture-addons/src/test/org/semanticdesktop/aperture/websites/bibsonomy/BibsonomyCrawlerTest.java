package org.semanticdesktop.aperture.websites.bibsonomy;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.SWRC;

/**
 * Tests the bibtex-to-rdf conversion
 * 
 */
public class BibsonomyCrawlerTest extends ApertureTestBase {

    private BibTex b;
    private Post<BibTex> p;
    private RDFContainer cont;
    
    public void setUp() {
        b = new BibTex();
        p = new Post<BibTex>();
        p.setResource(b);
        cont = createRDFContainer(new URIImpl("uri:test"));
    }
    
    public void tearDown() {
        cont.dispose();
    }

    /**
     * Tests if the uri of the bibtex entry is generated correctly
     */
    public void testBibtexUriCreation() {
        b.setIntraHash("2f1be76c864a34e2636013adbfdf342c5");
        p.setUser(new User("antoni"));
        assertEquals("http://www.bibsonomy.org/api/users/antoni/posts/2f1be76c864a34e2636013adbfdf342c5",
            BibsonomyCrawler.getBibtexUri(p, b).toString());
    }

    /**
     * Tests if the bibtex entry types are correctly converted to the appropriate SWRC publication types
     */
    public void testBibtexEntryTypeConversion() {
        testBibtexEntryType("article", SWRC.Article);
        testBibtexEntryType("book", SWRC.Book);
        testBibtexEntryType("booklet", SWRC.Booklet);
        testBibtexEntryType("inbook", SWRC.InBook);
        testBibtexEntryType("incollection", SWRC.InCollection);
        testBibtexEntryType("inproceedings", SWRC.InProceedings);
        testBibtexEntryType("manual", SWRC.Manual);
        testBibtexEntryType("mastersthesis", SWRC.MasterThesis);
        testBibtexEntryType("misc", SWRC.Misc);
        testBibtexEntryType("phdthesis", SWRC.PhDThesis);
        testBibtexEntryType("proceedings", SWRC.Proceedings);
        testBibtexEntryType("techreport", SWRC.TechnicalReport);
        testBibtexEntryType("unpublished", SWRC.Unpublished);
    }
    
    /**
     * Tests if the tag information is extracted correctly
     */
    public void testTags() {
        p.addTag("cool");
        p.addTag("great");
        p.addTag("awesome");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        assertSparqlQuery(obj.getMetadata().getModel(), "" +
            "SELECT ?x  \n" +
            "WHERE \n" +
            "{ ?x " + NAO.hasTag.toSPARQL() + " <http://www.bibsonomy.org/api/tags/cool> .\n" +
            "  <http://www.bibsonomy.org/api/tags/cool> " + RDF.type.toSPARQL() + " " + NAO.Tag.toSPARQL() + " .\n" +
            "  ?x " + NAO.hasTag.toSPARQL() + " <http://www.bibsonomy.org/api/tags/great> .\n" +
            "  <http://www.bibsonomy.org/api/tags/great> " + RDF.type.toSPARQL() + " " + NAO.Tag.toSPARQL() + " . \n" +
            "  ?x " + NAO.hasTag.toSPARQL() + " <http://www.bibsonomy.org/api/tags/awesome> .\n" +
            "  <http://www.bibsonomy.org/api/tags/awesome> " + RDF.type.toSPARQL() + " " + NAO.Tag.toSPARQL() + " .}");
    }
    
    /**
     * Test if the authors are extracted correctly, as a collection of nco:Contact entries.
     */
    public void testAuthors() {
        b.setAuthor("Antoni Mylka and Leo Sauermann and Christiaan Fluit");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        assertSparqlQuery(obj.getMetadata().getModel(), "" +
            "SELECT ?first \n" +
            "WHERE \n" +
            "{ ?x " + SWRC.author.toSPARQL() + " ?first .\n" +
            "  ?first " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?first " + RDF.first.toSPARQL() + " ?firstContact .\n " +
            "  ?firstContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?firstContact " + NCO.fullname.toSPARQL() + " \"Antoni Mylka\" .\n" +
            "  ?first " + RDF.rest.toSPARQL() + " ?second .\n" +
            "  ?second " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?second " + RDF.first.toSPARQL() + " ?secondContact .\n" +
            "  ?secondContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?secondContact " + NCO.fullname.toSPARQL() + " \"Leo Sauermann\" .\n" +
            "  ?second " + RDF.rest.toSPARQL() + " ?third .\n" +
            "  ?third " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?third " + RDF.first.toSPARQL() + " ?thirdContact .\n" +
            "  ?thirdContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?thirdContact " + NCO.fullname.toSPARQL() + " \"Christiaan Fluit\" .\n" +
            "  ?third " + RDF.rest.toSPARQL() + " " + RDF.nil.toSPARQL() + " .}");
    }
    
    /**
     * Tests if the SWRC is extracted correctly for a bibtex entry with only a single YEAR property
     */
    public void testYear() {
        b.setYear("2008");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        Date date = obj.getMetadata().getDate(SWRC.date);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        assertEquals(2008,cal.get(Calendar.YEAR));
        assertEquals(1,cal.get(Calendar.MONTH));
        assertEquals(1,cal.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * Tests if the SWRC is extracted correctly for a bibtex entry with only a single MONTH property.
     * A month without a year doesn't make sense, so nothing should be extracted
     */
    public void testMonthAlone() {
        b.setMonth("5");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        assertNull(obj.getMetadata().getDate(SWRC.date));
    }
    
    /**
     * Tests if the SWRC is extracted correctly for a bibtex entry with only a full date, i.e.
     * YEAR, MONTH and DAY
     */
    public void testFullDate() {
        b.setYear("2008");
        b.setMonth("5");
        b.setDay("23");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        Date date = obj.getMetadata().getDate(SWRC.date);
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        assertEquals(2008,cal.get(Calendar.YEAR));
        assertEquals(5,cal.get(Calendar.MONTH));
        assertEquals(23,cal.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * Test if the editors are extracted correctly, as a collection of nco:Contact entries.
     */
    public void testEditors() {
        b.setEditor("Antoni Mylka and Leo Sauermann and Christiaan Fluit");
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        assertSparqlQuery(obj.getMetadata().getModel(), "" +
            "SELECT ?first \n" +
            "WHERE \n" +
            "{ ?x " + SWRC.editor.toSPARQL() + " ?first .\n" +
            "  ?first " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?first " + RDF.first.toSPARQL() + " ?firstContact .\n " +
            "  ?firstContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?firstContact " + NCO.fullname.toSPARQL() + " \"Antoni Mylka\" .\n" +
            "  ?first " + RDF.rest.toSPARQL() + " ?second .\n" +
            "  ?second " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?second " + RDF.first.toSPARQL() + " ?secondContact .\n" +
            "  ?secondContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?secondContact " + NCO.fullname.toSPARQL() + " \"Leo Sauermann\" .\n" +
            "  ?second " + RDF.rest.toSPARQL() + " ?third .\n" +
            "  ?third " + RDF.type.toSPARQL() + " " + RDF.List.toSPARQL() + " .\n" +
            "  ?third " + RDF.first.toSPARQL() + " ?thirdContact .\n" +
            "  ?thirdContact " + RDF.type.toSPARQL() + " " + NCO.Contact.toSPARQL() + " . \n" +
            "  ?thirdContact " + NCO.fullname.toSPARQL() + " \"Christiaan Fluit\" .\n" +
            "  ?third " + RDF.rest.toSPARQL() + " " + RDF.nil.toSPARQL() + " .}");
    }

    private void testBibtexEntryType(String entryType, URI swrcType) {
        b.setType(entryType);
        
        DataObject obj = BibsonomyCrawler.convertBibtexEntryToDataObject(cont, p, b);
        assertTrue(obj.getMetadata().getAll(RDF.type).contains(swrcType));
        obj.dispose();
        cont = createRDFContainer("uri:test");
    }

}
