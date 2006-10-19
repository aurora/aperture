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
        assertMultiValueProperty(repository,calendarNode,RDF.TYPE,
                ICALTZD.Vcalendar);
        assertEquals(countOutgoingTriples(repository, calendarNode, RDF.TYPE),2);
        
        assertEquals(countStatements(repository),6);   
    }
    
    public void testVeventComponent() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        
        Resource veventNode = findComponentByUid(repository, 
                "20020630T230445Z-3895-69-1-7@jammer");
        
        assertMultiValueProperty(repository, veventNode,RDF.TYPE,
                DATA.DataObject);
        assertMultiValueProperty(repository, veventNode,RDF.TYPE,
                ICALTZD.Vevent);
        assertEquals(countOutgoingTriples(repository, veventNode, RDF.TYPE),2);
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
    
    public void testAttachProperty() throws Exception {
        Repository repository 
                = readIcalFile("simplevevent.ics");
        Resource veventNode 
                = findComponentByUid(repository, 
                  "EB825E41-23CE-11D7-B93D-003065B0C95E");
        Resource valarmNode
                = findSingleNode(repository, veventNode, ICALTZD.component);
        assertSingleValueURIProperty(repository,valarmNode,ICALTZD.attach,
                "uri://Ping");
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
        Repository repository = readIcalFile("calconnect7.ics");
        Resource veventNode = findComponentByUid(repository,
                "6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
        assertSingleValueProperty(repository,veventNode,ICALTZD.comment,
                "Another single instance reschedule - time only (+2 hrs)");
    }
    
    public void testCompletedProperty() throws Exception {
        Repository repository = readIcalFile("Todos1.ics");
        Resource veventNode = findComponentByUid(repository,
                "76116BB6-5338-11D8-A876-000A958826AA");
        assertSingleValueProperty(repository, veventNode, ICALTZD.completed, 
                "2003-11-25T13:00:00Z", XMLSchema.DATETIME);
    }
    
    public void testContactProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode = findComponentByUid(repository,
                "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.contact,
                "Jim Dolittle, ABC Industries, +1-919-555-1234");
    }
    
    public void testCreatedProperty() throws Exception {
        Repository repository = readIcalFile("test-created.ics");
        Resource veventNode = findComponentByUid(repository,
                "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
        assertSingleValueProperty(repository,veventNode,ICALTZD.created,
                "2004-12-23T13:52:26",XMLSchema.DATETIME);
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
    
    public void testDuePropertyUTCTimeNoValueParameter() throws Exception {
        Repository repository = readIcalFile("Todos1.ics");
        Resource veventNode = findComponentByUid(repository,
                "7611710A-5338-11D8-A876-000A958826AA");
        assertSingleValueProperty(repository,veventNode,ICALTZD.due,
                "2003-12-16T00:00:00Z",XMLSchema.DATETIME);
    }
    
    public void testDuePropertyDateValueParameter() throws Exception {
        Repository repository = readIcalFile("sunbird_sample.ics");
        Resource veventNode = findComponentByUid(repository,
                "1E2C09FC-FBA7-11D7-B98C-000A958D1EFE");
        assertSingleValueProperty(repository, veventNode, ICALTZD.due, 
                "2003-10-18", XMLSchema.DATE);
    }
    
    public void testDuePropertyWithTimeZoneId() throws Exception {
        Repository repository = readIcalFile("sunbird_sample.ics");
        Resource veventNode = findComponentByUid(repository,
                "7A0EDDE6-FF8A-11D7-8061-000A958D1EFE");
        assertSingleValueProperty(repository, veventNode, ICALTZD.due, 
                "2003-10-23T00:00:00", new URIImpl(
                "http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
    }
    
    public void testDurationProperty() throws Exception {
        Repository repository 
                = readIcalFile("simplevevent.ics");
        Resource veventNode 
                = findComponentByUid(repository,
                  "EB825E41-23CE-11D7-B93D-003065B0C95E");
        Resource durationNode 
                = findSingleNode(repository, veventNode, ICALTZD.duration);
        assertSingleValueProperty(repository, durationNode, ICALTZD.value, 
                "PT1H", XMLSchema.DURATION);
    }
    
    
    // This test should also be repeated three times (like the tests for 
    // DUE and DTSTART) with varying types and VALUE parameters.
    public void testExDate() throws Exception {
        Repository repository = readIcalFile("tag-bug.ics");
        Resource veventNode = findComponentByUid(repository,
                "78492d2f-aacd-40e3-80cc-4f078d1516e0");
        assertSingleValueProperty(repository,veventNode,ICALTZD.exdate,
                "2002-02-25",XMLSchema.DATE);
    }
    
    public void testExRule() throws Exception {
        Repository repository = readIcalFile("cal01-exrule.ics");
        Resource veventNode
                = findComponentByUid(repository, 
                  "20020630T230353Z-3895-69-1-0@jammer");
        Resource recurrenceNode
                = findSingleNode(repository, veventNode, ICALTZD.exrule);
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.freq, "WEEKLY");
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.interval, "5", XMLSchema.INTEGER);
        assertSingleValueProperty(repository, recurrenceNode,
                ICALTZD.byday, "SU");
        assertEquals(countOutgoingTriples(repository, recurrenceNode),3);
    }
    
    public void testFreeBusyProperty() throws Exception {
        Repository repository = readIcalFile("freebusy.ics");
        Resource calendarNode
                = findMainCalendarNode(repository);
        Resource vfreebusyNode
                = findSingleNode(repository, calendarNode, ICALTZD.component);
        assertSingleValueProperty(repository,vfreebusyNode,
                ICALTZD.freebusy,"19971015T050000Z/PT8H30M,"
                + "19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M");
    }
    
    public void testGeoProperty() throws Exception {
        Repository repository = readIcalFile("geo1.ics");
        Resource veventNode
                = findComponentByUid(repository, 
                  "CDC474D4-1393-11D7-9A2C-000393914268");
        Resource firstListNode 
                = findSingleNode(repository,veventNode, ICALTZD.geo);
        
        assertSingleValueProperty(repository, firstListNode, RDF.FIRST,
                "40.442673",XMLSchema.DOUBLE);
        assertEquals(countOutgoingTriples(repository, firstListNode),2);
        Resource secondListNode
                = findSingleNode(repository,firstListNode,RDF.REST);
        assertSingleValueProperty(repository,secondListNode,RDF.FIRST,
                "-79.945815",XMLSchema.DOUBLE);
        assertSingleValueProperty(repository,secondListNode,RDF.REST,RDF.NIL);
        assertEquals(countOutgoingTriples(repository,secondListNode),2);
    }
    
    public void testLastModifiedProperty() throws Exception {
        Repository repository = readIcalFile("test-created.ics");
        Resource veventNode 
                = findComponentByUid(repository,
                  "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
        assertSingleValueProperty(repository,veventNode,ICALTZD.lastModified,
                "2004-12-23T15:17:52",XMLSchema.DATETIME);
    }
    
    public void testLocationProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.location,
                "San Francisco");
    }
    
    public void testMethodProperty() throws Exception {
        Repository repository = readIcalFile("basicCalendar.ics");
        Resource calendarNode
                = findMainCalendarNode(repository);
        assertSingleValueProperty(repository,calendarNode,ICALTZD.method,
                "PUBLISH");
    }
    
    public void testOrganizerProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "20020630T230600Z-3895-69-1-16@jammer");
        Resource organizerNode
                = findSingleNode(repository, veventNode, ICALTZD.organizer);
        assertSingleValueProperty(repository, organizerNode, ICALTZD.cn, 
                "Dan Connolly");
        assertSingleValueProperty(repository, organizerNode, ICALTZD.calAddress,
                "MAILTO:connolly@w3.org");
    }
    
    public void testPercentCompleteProperty() throws Exception {
        Repository repository = readIcalFile("korganizer-jicaltest.ics");
        Resource veventNode 
                = findComponentByUid(repository,"KOrganizer-1573136895.534");
        assertSingleValueProperty(repository,veventNode,ICALTZD.percentComplete,
                "0",XMLSchema.INTEGER);
    }
    
    public void testPriorityProperty() throws Exception {
        Repository repository = readIcalFile("Todos1.ics");
        Resource vtodoNode
                = findComponentByUid(repository,
                  "76116BB6-5338-11D8-A876-000A958826AA");
        assertSingleValueProperty(repository,vtodoNode,ICALTZD.priority,"2",
                XMLSchema.INTEGER);
    }
    
    public void testProdIdProperty() throws Exception {
        Repository repository = readIcalFile("Todos1.ics");
        Resource vcalendarNode
                = findMainCalendarNode(repository);
        assertSingleValueProperty(repository,vcalendarNode,ICALTZD.prodid,
                "-//Apple Computer, Inc//iCal 1.5//EN");
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
    
    public void testRDateProperty() throws Exception {
        Repository repository = readIcalFile("calconnect9.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
        assertMultiValueProperty(repository,veventNode,ICALTZD.rdate,
                "20050425T090000/20050425T091500");
        assertMultiValueProperty(repository,veventNode,ICALTZD.rdate,
                "20050426T090000/20050426T091500");
        assertMultiValueProperty(repository,veventNode,ICALTZD.rdate,
                "20050427T090000/20050427T091500");
        assertMultiValueProperty(repository,veventNode,ICALTZD.rdate,
                "20050428T090000/20050428T091500");
        assertMultiValueProperty(repository,veventNode,ICALTZD.rdate,
                "20050429T090000/20050429T091500");
        assertEquals(
                countOutgoingTriples(repository, veventNode,ICALTZD.rdate),5);
    }
    
    public void testRecurrenceIdProperty() throws Exception {
        Repository repository = readIcalFile("calconnect7.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
        Resource recurrenceBlankNode 
                = findSingleNode(repository, veventNode, ICALTZD.recurrenceId);
        assertSingleValueProperty(repository, recurrenceBlankNode, 
                ICALTZD.value,"2005-04-28T13:00:00Z",XMLSchema.DATETIME);
    }
    
    public void testRelatedToProperty() throws Exception {
        Repository repository = readIcalFile("calconnect7.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
        assertSingleValueProperty(repository, veventNode, 
                ICALTZD.relatedTo,
                "<jsmith.part7.19960817T083000.xyzMail@host3.com>");
    }
    
    public void testRepeatProperty() throws Exception {
        Repository repository = readIcalFile("php-flp.ics");
        Resource veventNode
                = findComponentByUid(repository,"TPACTIDSTREAMTASKID");
        Resource valarmNode
                = findSingleNode(repository, veventNode, ICALTZD.component);
        assertSingleValueProperty(repository, valarmNode, ICALTZD.repeat,
                "3",XMLSchema.INTEGER);
    }
    
    public void testRequestStatusProperty() throws Exception {
        Repository repository = readIcalFile("incoming.ics");
        Resource calendarNode
                = findMainCalendarNode(repository);
        Resource veventNode
                = findSingleNode(repository,calendarNode,ICALTZD.component);
        assertSingleValueProperty(repository, veventNode, ICALTZD.requestStatus,
                "2.0;Success");
    }
    
    public void testResourcesProperty() throws Exception {
        Repository repository = readIcalFile("php-flp.ics");
        Resource veventNode
                = findComponentByUid(repository,"TPACTIDSTREAMTASKID");
        assertSingleValueProperty(repository, veventNode, ICALTZD.resources,
                "EASEL,PROJECTOR,VCR");
    }
    
    public void testSequenceProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.sequence,"2",
                XMLSchema.INTEGER);
    }
    
    public void testStatusProperty() throws Exception {
        Repository repository = readIcalFile("Todos1.ics");
        Resource vtodoNode
                = findComponentByUid(repository,
                  "76116BB6-5338-11D8-A876-000A958826AA");
        assertSingleValueProperty(repository,vtodoNode,ICALTZD.status,
                "COMPLETED");
    }
    
    public void testSummaryProperty() throws Exception {
        // we deliberately chose an example with disregarded params
        Repository repository = readIcalFile("php-flp.ics");
        Resource veventNode 
                = findComponentByUid(repository,"TPACTIDSTREAMTASKID");
        assertSingleValueProperty(repository,veventNode,ICALTZD.summary,
                "TP for Act ID XXXXX -- Not important : Stream -- Task ID");
    }
    
    public void testTranspProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.transp,
                "OPAQUE");
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
    
    public void testTzidProperty() throws Exception {
        Repository repository = readIcalFile("cal01.ics");
        Resource vtimezoneNode = findSingleTimezone(repository);
        assertSingleValueProperty(repository,vtimezoneNode,ICALTZD.tzid,
                "/softwarestudio.org/Olson_20011030_5/America/New_York");
    }
    
    public void testTzNameProperty() throws Exception {
        Repository repository 
                = readIcalFile("cal01.ics");
        Resource vtimezoneNode 
                = findSingleTimezone(repository);
        Resource daylightNode 
                = findSingleNode(repository,vtimezoneNode,ICALTZD.daylight);
        assertSingleValueProperty(repository,daylightNode,ICALTZD.tzname,"EDT");
    }
    
    public void testTzOffsetFromProperty() throws Exception {
        Repository repository 
                = readIcalFile("cal01.ics");
        Resource vtimezoneNode 
                = findSingleTimezone(repository);
        Resource daylightNode 
                = findSingleNode(repository,vtimezoneNode,ICALTZD.daylight);
        assertSingleValueProperty(repository,daylightNode,ICALTZD.tzoffsetfrom,
                "-0500");
    }
    
    public void testTzOffsetToProperty() throws Exception {
        Repository repository 
                = readIcalFile("cal01.ics");
        Resource vtimezoneNode 
                = findSingleTimezone(repository);
        Resource daylightNode 
                = findSingleNode(repository,vtimezoneNode,ICALTZD.daylight);
        assertSingleValueProperty(repository,daylightNode,ICALTZD.tzoffsetto,
                "-0400");
    }
    
    public void testTzUrlProperty() throws Exception {
        Repository repository
                = readIcalFile("cal01.ics");
        Resource vtimezoneNode
                = findSingleTimezone(repository);
        assertSingleValueURIProperty(repository,vtimezoneNode,ICALTZD.tzurl,
                "http://timezones.r.us.net/tz/US-California-Los_Angeles");
    }
    
    public void testUidProperty() throws Exception {
        Repository repository
                = readIcalFile("cal01.ics");
        Resource veventNode
                = findComponentByUid(repository, 
                  "20020630T230445Z-3895-69-1-7@jammer");
        assertSingleValueProperty(repository,veventNode,ICALTZD.uid,
                "20020630T230445Z-3895-69-1-7@jammer");
    }
    
    public void testUrlProperty() throws Exception {
        Repository repository
                = readIcalFile("geo1.ics");
        Resource veventNode
                = findComponentByUid(repository,
                  "CDC474D4-1393-11D7-9A2C-000393914268");
        assertSingleValueURIProperty(repository,veventNode,ICALTZD.url,
                "http://kanzaki.com/works/2004/cal/0406vocab.html");
    }
    
    public void testVersionProperty() throws Exception {
        Repository repository
                = readIcalFile("geo1.ics");
        Resource calendarNode
                = findMainCalendarNode(repository);
        assertSingleValueProperty(repository,calendarNode,ICALTZD.version,"2.0");
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
            iterator.next();
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
            iterator.next();
        }
        iterator.close();
        return numberOfStatements;
    }
    
    private int countOutgoingTriples(Repository repository, Resource resource,
            URI predicate) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(resource, predicate, null);
        int numberOfStatements = 0;
        while (iterator.hasNext()) {
            numberOfStatements++;
            iterator.next();
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
    
    private Resource findSingleTimezone(Repository repository) {
        CloseableIterator<RStatement> iterator
                = repository.getStatements(null, RDF.TYPE, ICALTZD.Vtimezone);
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
    
    private void assertSingleValueURIProperty(Repository repository,
            Resource parentNode, URI predicate, String label) {
        Resource attachedUri
                = findSingleNode(repository,parentNode,predicate);
        assertTrue(attachedUri instanceof URI);
        assertEquals(attachedUri.toString(),label);
        assertEquals(countOutgoingTriples(repository, attachedUri),0);
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
            Resource subject, URI predicate, String valueLabel) {
        assertMultiValueProperty(repository,subject,predicate,
                valueFactory.createLiteral(valueLabel));
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
}



