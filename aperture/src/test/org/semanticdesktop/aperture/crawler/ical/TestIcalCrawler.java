/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.net.URL;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

public class TestIcalCrawler extends ApertureTestBase {

    public static final String ICAL_TESTDATA_PATH 
            = DOCS_PATH + "icaltestdata/";
    private ValueFactory valueFactory;
    
    public void setUp() {
        valueFactory = new ValueFactoryImpl();
    }
    
    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// COMPONENT TESTS ////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    public void testCalendarComponent() throws Exception {
        Repository repository = readIcalFile("basicCalendar.ics");
        
        Resource calendarNode = findMainCalendarNode(repository);
        
        assertSingleValueProperty(repository,calendarNode,ICALTZD.calscale,
                "GREGORIAN");
        assertSingleValueProperty(repository,calendarNode,ICALTZD.version,
                "2.0");
        assertSingleValueProperty(repository,calendarNode,ICALTZD.method,
                "PUBLISH");
        
        // note that the comma is escaped in the original file, this is
        // compatible with the RFC 2445 4.3.11
        assertSingleValueProperty(repository,calendarNode,ICALTZD.prodid,
                "-//Apple Computer, Inc//iCal 1.0//EN");
        
        assertMultiValueProperty(repository,calendarNode,RDF.TYPE,
                DATA.DataObject);
        
        assertEquals(countStatements(repository),6);   
    }
    
    ////////////////////////////////////////////////////////////////////////
    //////////////////////////// PROPERTY TESTS ////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    public void testActionProperty() throws Exception {
        Repository repository 
                = readIcalFile("simplevevent.ics");
        Resource calendarNode 
                = findMainCalendarNode(repository);
        Resource veventNode 
                = findSingleNode(repository,calendarNode,ICALTZD.component);
        Resource valarmNode
                = findSingleNode(repository,veventNode,ICALTZD.component);
        assertSingleValueProperty(repository,valarmNode,ICALTZD.action,"AUDIO");
    }
    
    public void testAttendeeProperty() throws Exception {
        Repository repository 
                = readIcalFile("simplevevent.ics");
        Resource calendarNode 
                = findMainCalendarNode(repository);
        Resource veventNode 
                = findSingleNode(repository,calendarNode,ICALTZD.component);
        Resource attendeeBlankNode
                = findSingleNode(repository,veventNode,ICALTZD.attendee);
        assertSingleValueProperty(repository,attendeeBlankNode,ICALTZD.cn,
                "Libby Miller");
        assertSingleValueProperty(repository,attendeeBlankNode,
                ICALTZD.calAddress,"mailto:libby.miller@bristol.ac.uk");
        assertEquals(countOutgoingTriples(repository,attendeeBlankNode),2);
    }
    
    public void testCalScaleProperty() throws Exception {
        Repository repository = readIcalFile("basicCalendar.ics");
        Resource calendarNode = findMainCalendarNode(repository);
        assertSingleValueProperty(repository,calendarNode,ICALTZD.calscale,
                "GREGORIAN");        
    }
    
    public void testCategoriesProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.categories,
                "Miscellaneous");
    }
    
    public void testClassProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.class_,
                "PUBLIC");
    }
    
    public void testCommentProperty() throws Exception {
        Repository repository = readIcalFile("gkexample.ics");
        // the events in this file don't contain UID's, we will have
        // to find them by hand
        Resource veventWithComment = null;
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, RDF.TYPE, ICALTZD.Vevent);
        RStatement statement = null;
        while (iterator.hasNext()) {
            statement = iterator.next();
            Resource currentVevent = statement.getSubject();
            CloseableIterator commentIterator 
                    = repository
                      .getStatements(currentVevent, ICALTZD.comment, null);
            if (commentIterator.hasNext()) {
                veventWithComment = currentVevent;
            }
            commentIterator.close();
        }
        iterator.close();
        assertNotNull(veventWithComment);
        assertSingleValueProperty(repository,veventWithComment,ICALTZD.comment,
                "from G.Klyne - iCalendarExample.txt");
    }
    
    public void testDescriptionProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        Resource valarmNode = findSingleNode(repository, veventNode, 
                ICALTZD.component);
        assertSingleValueProperty(repository,valarmNode,ICALTZD.description,
                "Federal Reserve Board Meeting");
    }
    
    public void testDtEndPropertyUTCTimeNoValueParameter() throws Exception {
        Repository repository = readIcalFile("gkexample.ics");
        Resource veventWithDtStart = null;
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, RDF.TYPE, ICALTZD.Vevent);
        RStatement statement = null;
        while (iterator.hasNext()) {
            // we rely on the fact, that the first node returned by this iterator
            // will be the second one defined in the file
            statement = iterator.next();
            veventWithDtStart = statement.getSubject();
        }
        iterator.close();
        assertSingleValueProperty(repository,veventWithDtStart,ICALTZD.dtend,
                "2002-12-01T22:00:00Z",XMLSchema.DATETIME);
    }
    
    public void testDtEndPropertyDateValueParameter() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository, veventNode, ICALTZD.dtend, 
                "2002-07-06", XMLSchema.DATE);
    }
    
    public void testDtEndPropertyWithTimeZoneId() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230353Z-3895-69-1-0@jammer");
        assertSingleValueProperty(repository, veventNode, ICALTZD.dtend, 
                "2002-06-30T10:30:00", new URIImpl(
                "http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
    }
    
    public void testDtStampProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository, veventNode, ICALTZD.dtstamp, 
                "2002-06-30T23:04:45Z", XMLSchema.DATETIME);
    }
    
    public void testDtStartPropertyUTCTimeNoValueParameter() throws Exception {
        Repository repository = readIcalFile("gkexample.ics");
        Resource veventWithDtStart = null;
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, RDF.TYPE, ICALTZD.Vevent);
        RStatement statement = null;
        while (iterator.hasNext()) {
            // we rely on the fact, that the nodes returned by this iterator
            // will have the same order as their definitions in the file
            statement = iterator.next();
            veventWithDtStart = statement.getSubject();
        }
        iterator.close();
        assertSingleValueProperty(repository,veventWithDtStart,ICALTZD.dtstart,
                "2002-12-01T16:00:00Z",XMLSchema.DATETIME);
    }
    
    public void testDtStartPropertyDateValueParameter() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository, veventNode, ICALTZD.dtstart, 
                "2002-07-03", XMLSchema.DATE);
    }
    
    public void testDtStartPropertyWithTimeZoneId() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230353Z-3895-69-1-0@jammer");
        assertSingleValueProperty(repository, veventNode, ICALTZD.dtstart, 
                "2002-06-30T09:00:00", new URIImpl(
                "http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
    }
    
    public void testPercentCompleteProperty() throws Exception {
        Repository repository = readIcalFile("korganizer-jicaltest.ics");
        Resource veventNode = findComponentByUid(repository,
                "KOrganizer-1573136895.534");
        assertSingleValueProperty(repository,veventNode,ICALTZD.percentComplete,
                "0",XMLSchema.INTEGER);
    }
    
    public void testRecurrenceRule1() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository, 
                  "20020630T230353Z-3895-69-1-0@jammer");
        Resource recurrenceNode
                = findSingleNode(repository, veventNode, ICALTZD.rrule);
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.freq, "WEEKLY");
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.interval, "1", XMLSchema.INTEGER);
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.byday, "SU");
        assertEquals(countOutgoingTriples(repository, recurrenceNode),3);
    }
    
    public void testRecurrenceRule2() throws Exception {
        Repository repository = readIcalFile("gkexample.ics");
        Resource veventNode
                = findComponentByUid(repository, 
                  "20020630T230353Z-3895-69-1-0@antoni");
        Resource recurrenceNode
                = findSingleNode(repository, veventNode, ICALTZD.rrule);
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.freq, "WEEKLY");
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.byday, "SA,SU");
        assertEquals(countOutgoingTriples(repository, recurrenceNode),2);
    }
    
    public void testTriggerPropertyWithDefinedDateTimeType() throws Exception {
        Repository repository = readIcalFile("simplevevent.ics");
        Resource calendarNode 
                = findMainCalendarNode(repository);
        Resource veventNode 
                = findSingleNode(repository,calendarNode,ICALTZD.component);
        Resource valarmNode
                = findSingleNode(repository,veventNode,ICALTZD.component);
        Resource triggerBlankNode
                = findSingleNode(repository,valarmNode,ICALTZD.trigger);
        assertSingleValueProperty(repository,triggerBlankNode,ICALTZD.value,
                "2006-04-12T23:00:00Z",XMLSchema.DATETIME);
    }
    
    public void testTriggerPropertyWithSomeParams() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode 
                = findComponentByUid(repository, 
                  "20020630T230600Z-3895-69-1-16@jammer");
        Resource valarmNode
                = findSingleNode(repository,veventNode,ICALTZD.component);
        Resource triggerBlankNode
                = findSingleNode(repository,valarmNode,ICALTZD.trigger);
        assertSingleValueProperty(repository,triggerBlankNode,ICALTZD.value,
                "-PT15M",XMLSchema.DURATION);
        assertSingleValueProperty(repository,triggerBlankNode,ICALTZD.related,
                "START");
        assertEquals(countOutgoingTriples(repository, triggerBlankNode),2);
    }
    
    public void testTriggerPropertyWithoutDefinedType() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode 
                = findComponentByUid(repository, 
                  "20020630T230445Z-3895-69-1-7@jammer");
        Resource valarmNode
                = findSingleNode(repository,veventNode,ICALTZD.component);
        Resource triggerBlankNode
                = findSingleNode(repository,valarmNode,ICALTZD.trigger);
        assertSingleValueProperty(repository,triggerBlankNode,ICALTZD.value,
                "-PT30M",XMLSchema.DURATION);
        assertSingleValueProperty(repository,triggerBlankNode,ICALTZD.related,
                "START");
        assertEquals(countOutgoingTriples(repository, triggerBlankNode),2);
    }
    
    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// PARAMETER TESTS ////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    public void testCnParameter() throws Exception {
        Repository repository 
                = readIcalFile("simplevevent.ics");
        Resource calendarNode 
                = findMainCalendarNode(repository);
        Resource veventNode 
                = findSingleNode(repository,calendarNode,ICALTZD.component);
        Resource attendeeBlankNode
                = findSingleNode(repository,veventNode,ICALTZD.attendee);
        assertSingleValueProperty(repository,attendeeBlankNode,ICALTZD.cn,
                "Libby Miller");
    }
    
    public void testCuTypeParameter() throws Exception {
        Repository repository 
                = readIcalFile("cal01.ics");
        Resource veventNode 
                = findComponentByUid(repository, 
                  "20020630T230600Z-3895-69-1-16@jammer");
        Resource attendeeBlankNode
                = findOneOfMultipleNodes(repository,veventNode,ICALTZD.attendee);
        assertSingleValueProperty(repository,attendeeBlankNode,ICALTZD.cutype,
                "INDIVIDUAL");
    }
    
    ////////////////////////////////////////////////////////////////////////
    ///////////////////////// CONVENIENCE METHODS //////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    /** Crawls the ICAL file and returns the Repository with the generated
     *  RDF Tripples.
     *  @return Repository with generated RDF triples
     */
    private Repository readIcalFile(String fileName) {
        URL fileURL 
                = ClassLoader.getSystemResource(ICAL_TESTDATA_PATH + fileName);
        assertNotNull(fileURL);
        File file = new File(fileURL.getFile());
        assertTrue(file.canRead());
        SesameRDFContainer configurationContainer
                = new SesameRDFContainer(
                  new URIImpl("source:testsource"));
        ConfigurationUtil.setRootUrl(file.getAbsolutePath(), 
                configurationContainer);
        
        FileSystemDataSource fileSystemDataSource
                = new FileSystemDataSource();
        fileSystemDataSource.setConfiguration(configurationContainer);
        
        IcalTestSimpleCrawlerHandler testCrawlerHandler
                = new IcalTestSimpleCrawlerHandler();
        
        IcalCrawler icalCrawler = new IcalCrawler();
        icalCrawler.setDataSource(fileSystemDataSource);
        icalCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        icalCrawler.setCrawlerHandler(testCrawlerHandler);
        
        icalCrawler.crawl();
        return testCrawlerHandler.getRepository();
    }
    
    /**
     * Prints all triples in the repository onto the standard output.
     * 
     * @param repository
     *            The repository that contains the statements to be printed.
     */
    private void printStatements(Repository repository) {
        CloseableIterator<RStatement> iterator 
                = repository.extractStatements();
        while (iterator.hasNext()) {
            RStatement rstatement = iterator.next();
            System.out.println(rstatement.toString());
        }
        iterator.close();
    }
    
    /**
     * Counts the triples in the repository.
     * 
     * @param repository
     *            The Repository that contains the statements to be counted.
     * @return The number of statements.
     */
    private int countStatements(Repository repository) {
        CloseableIterator<RStatement> iterator 
                = repository.extractStatements();
        int numberOfStatements = 0;
        while (iterator.hasNext()) {
            numberOfStatements++;
            RStatement rstatement = iterator.next();
        }
        iterator.close();
        return numberOfStatements;
    }
    
    private int countOutgoingTriples(Repository repository, Resource resource) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(resource, null, null);
        int numberOfStatements = 0;
        while (iterator.hasNext()) {
            numberOfStatements++;
            RStatement rstatement = iterator.next();
        }
        iterator.close();
        return numberOfStatements;
    }
    
    private Resource findSingleNode(Repository repository, Resource parentNode, 
            URI predicate) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(parentNode, predicate, null);
        assertTrue(iterator.hasNext());
        RStatement statement = iterator.next();
        assertFalse(iterator.hasNext());
        iterator.close();
        Value value = statement.getObject();
        assertTrue(value instanceof Resource);
        return (Resource)value;
    }
    
    private Resource findOneOfMultipleNodes(Repository repository, 
            Resource parentNode, URI predicate) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(parentNode, predicate, null);
        assertTrue(iterator.hasNext());
        RStatement statement = iterator.next();
        iterator.close();
        Value value = statement.getObject();
        assertTrue(value instanceof Resource);
        return (Resource)value;
    }
    
    /**
     * Returns the main calendar node.
     * @param repository 
     * @return
     */
    private Resource findMainCalendarNode(Repository repository) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, RDF.TYPE, ICALTZD.Vcalendar);
        assertTrue(iterator.hasNext());
        RStatement statement = iterator.next();
        assertFalse(iterator.hasNext());
        iterator.close();
        return statement.getSubject();
    }
    
    private Resource findComponentByUid(Repository repository, String uid) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, ICALTZD.uid, null);
        boolean found = false;
        RStatement statement = null;
        while (iterator.hasNext()) {
             statement = iterator.next();
             if (statement.getObject().toString().equals(uid)) {
                 found = true;
                 break;
             }
        }
        iterator.close();
        assertTrue(found);
        return statement.getSubject();
    }
    
    private void assertSingleValueProperty(Repository repository, 
            Resource subject, URI predicate, String objectLabel) {
        assertSingleValueProperty(repository, subject, predicate, 
                valueFactory.createLiteral(objectLabel));
    }
    
    /**
     * Asserts that a given triple in the repository exists AND that it is the
     * only one with the given subject and predicate.
     * 
     * @param repository
     * @param subject
     * @param predicate
     * @param object
     */
    private void assertSingleValueProperty(Repository repository, 
            Resource subject, URI predicate, Value object) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(subject, predicate, null);
        assertTrue(iterator.hasNext());
        RStatement statement = iterator.next();
        assertFalse(iterator.hasNext());
        assertEquals(statement.getObject().toString(),object.toString());
        iterator.close();
    }
    
    /**
     * Asserts that a given triple in the repository exists AND that it is the
     * only one with the given subject and predicate AND that the object is
     * a literal with a given XSD datatype.
     * 
     * @param repository
     * @param subject
     * @param predicate
     * @param objectLabel
     * @param xsdDatatype
     */
    private void assertSingleValueProperty(Repository repository,
            Resource subject, URI predicate, String objectLabel, 
            URI xsdDatatype) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(subject, predicate, null);
        assertTrue(iterator.hasNext()); // statement exists
        RStatement statement = iterator.next();
        assertFalse(iterator.hasNext()); // it is the only one
        iterator.close();
        
        Value object = statement.getObject();
        assertTrue(object instanceof Literal); // the object is a literal
        Literal literal = (Literal)object;
        assertEquals(literal.getLabel(),objectLabel); // it's label is as given
        assertEquals(literal.getDatatype(),xsdDatatype); // and datatype as well
    }
    
    private void assertMultiValueProperty(Repository repository, 
            Resource subject, URI predicate, String stringLiteral) {
        assertMultiValueProperty(repository,subject,predicate,
                valueFactory.createLiteral(stringLiteral));
    }
    
    /**
     * Asserts that the given triple exists in the given repository. It doesn't
     * need to be the only one with the given subject and predicate.
     * 
     * @param repository
     * @param subject
     * @param predicate
     * @param value
     */
    private void assertMultiValueProperty(Repository repository, 
            Resource subject, URI predicate, Value value) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(subject, predicate, value);
        assertTrue(iterator.hasNext());
        iterator.close();
    }
    
    private Value toLit(String lit) {
        return valueFactory.createLiteral(lit);
    }
}



