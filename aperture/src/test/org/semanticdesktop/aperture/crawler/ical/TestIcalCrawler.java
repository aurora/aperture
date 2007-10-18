/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class TestIcalCrawler extends ApertureTestBase {
    
	private static final URI SOURCE_TESTSOURCE = new URIImpl("source:testsource");
    public static final String ICAL_TESTICAL_PATH = DOCS_PATH + "icaltestdata/";
	public static final String TEMP_FILE_NAME = "temp-calendar.ics";

	private Model model;
	private Model model2;
	
	public void setUp() {
		// nothing needed
	}
	
	public void tearDown() {
		if (model != null) {
		    validate(model,true, SOURCE_TESTSOURCE,true);
		    
			model.close();
		}
		model = null;
		if (model2 != null) {
			model2.close();
		}
		model2 = null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// COMPONENT TESTS ///////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void testCalendarComponent() throws Exception {
	    model = readIcalFile("basicCalendar.ics");

		Resource calendarNode = findMainCalendarNode(model);
		    
		assertSingleValueProperty(model, calendarNode, NCAL.calscale, NCAL.gregorianCalendarScale);
		assertSingleValueProperty(model, calendarNode, NCAL.version, "2.0");
		assertSingleValueProperty(model, calendarNode, NCAL.method, "PUBLISH");

		// note that the comma is escaped in the original file, this is
		// compatible with the RFC 2445 4.3.11
		assertSingleValueProperty(model, calendarNode, NCAL.prodid,
			"-//Apple Computer, Inc//iCal 1.0//EN");

		assertMultiValueProperty(model, calendarNode, RDF.type, NIE.DataObject);
		assertMultiValueProperty(model, calendarNode, RDF.type, NCAL.CalendarDataObject);
		assertMultiValueProperty(model, calendarNode, RDF.type, NCAL.Calendar);
		assertEquals(countOutgoingTriples(model, calendarNode, RDF.type), 3);

		assertEquals(countStatements(model), 8);
	}
	
	public void testAlarmComponent() throws Exception {
	    model = readIcalFile("cal01.ics");
	    
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model,veventNode,NCAL.hasAlarm);
		
		assertSingleValueURIProperty(model,valarmNode,NCAL.action, NCAL.displayAction.toString());
		assertSingleValueProperty(model,valarmNode,NCAL.description, "Federal Reserve Board Meeting");
		assertMultiValueProperty(model,valarmNode,RDF.type,NCAL.Alarm);
		assertMultiValueProperty(model,valarmNode,RDF.type,NCAL.CalendarDataObject);
		assertEquals(countOutgoingTriples(model, valarmNode),5);
		
		Resource triggerBNode = findSingleNode(model,valarmNode,NCAL.trigger);
		assertSingleValueURIProperty(model,triggerBNode,NCAL.related,NCAL.startTriggerRelation.toString());
		assertSingleValueProperty(model,triggerBNode,NCAL.triggerDuration,"-PT30M",IcalCrawler.XSD_DAY_TIME_DURATION);
	}

	public void testVeventComponent() throws Exception {
	    model = readIcalFile("cal01.ics");

	    Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");

		assertMultiValueProperty(model, veventNode, RDF.type, NIE.DataObject);
		assertMultiValueProperty(model, veventNode, RDF.type, NCAL.CalendarDataObject);
		assertMultiValueProperty(model, veventNode, RDF.type, NCAL.Event);				
		assertSingleValueProperty(model, veventNode, NCAL.uid, "20020630T230353Z-3895-69-1-0@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.dtstamp, "2002-06-30T23:03:53Z", XSD._dateTime);
        assertSingleValueURIProperty(model, veventNode, NCAL.transp, NCAL.opaqueTransparency.toString());
        assertSingleValueProperty(model, veventNode, NCAL.sequence, "2", XSD._integer);
        assertSingleValueProperty(model, veventNode, NCAL.summary, "Church");
        assertSingleValueURIProperty(model, veventNode, NCAL.class_, NCAL.privateClassification.toString());
        Resource dtStartNcalDatetimeNode = findSingleNode(model, veventNode, NCAL.dtstart);
        Resource dtEndDatetimeNode = findSingleNode(model, veventNode, NCAL.dtend);
        Resource recurBlankNode = findSingleNode(model, veventNode, NCAL.rrule);
        assertEquals(countOutgoingTriples(model, veventNode, RDF.type), 3);
        assertEquals(countOutgoingTriples(model, veventNode),12);
        
        assertSingleValueProperty(model, dtStartNcalDatetimeNode, NCAL.dateTime, "2002-06-30T09:00:00",XSD._dateTime);
		Resource timezoneNode = findSingleNode(model, dtStartNcalDatetimeNode, NCAL.ncalTimezone);
		assertTrue(timezoneNode.toString().contains("/softwarestudio.org/Olson_20011030_5/America/New_York"));
		
        assertSingleValueProperty(model, dtEndDatetimeNode, NCAL.dateTime, "2002-06-30T10:30:00",XSD._dateTime);
        timezoneNode = findSingleNode(model, dtStartNcalDatetimeNode, NCAL.ncalTimezone);
        assertTrue(timezoneNode.toString().contains("/softwarestudio.org/Olson_20011030_5/America/New_York"));
		
		assertSingleValueURIProperty(model, recurBlankNode, NCAL.freq, NCAL.weekly.toString());
		assertSingleValueProperty(model, recurBlankNode, NCAL.interval, "1", XSD._integer);
		assertSingleValueURIProperty(model, recurBlankNode, RDF.type, NCAL.RecurrenceRule.toString());
		Resource bydayRulePartNode = findSingleNode(model, recurBlankNode, NCAL.byday);
		assertEquals(countOutgoingTriples(model, recurBlankNode),4);
		
		assertSingleValueURIProperty(model, bydayRulePartNode, NCAL.bydayWeekday, NCAL.sunday.toString());
		assertSingleValueURIProperty(model, bydayRulePartNode, RDF.type, NCAL.BydayRulePart.toString());
		assertEquals(countOutgoingTriples(model, bydayRulePartNode),2);
	}
	
	public void testVFreebusyComponent() throws Exception {
	    model = readIcalFile("freebusy.ics");
	    
		Resource vcalendarNode = findMainCalendarNode(model);
		Resource vfreebusyNode = findSingleNode(model, vcalendarNode, NCAL.component);
		
		assertMultiValueProperty(model, vfreebusyNode, RDF.type, NIE.DataObject);
		assertMultiValueProperty(model, vfreebusyNode, RDF.type, NCAL.Freebusy);
		assertMultiValueProperty(model, vfreebusyNode, RDF.type, NCAL.CalendarDataObject);
		assertEquals(countOutgoingTriples(model, vfreebusyNode,RDF.type),3);
		findSingleNode(model,vfreebusyNode, NCAL.organizer);
		findSingleNode(model,vfreebusyNode, NCAL.attendee);
		
		assertSparqlQuery(model, "" +
				"PREFIX xsd: <" + XSD.XSD_NS + "> " +
				"SELECT ?v1 ?v2 " +
				"WHERE " +
                "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
                "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
                "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
                "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
                "      FILTER(regex(str(?v1),\"1997-10-15T05:00:00Z\") &&" +
                "             regex(str(?v2),\"PT8H30M\") && " +
                "             datatype(?v1) = xsd:dateTime &&" +
                "             datatype(?v2) = xsd:dayTimeDuration)}");
        
        assertSparqlQuery(model, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> " +
            "SELECT ?v1 ?v2 " +
            "WHERE " +
            "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
            "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
            "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
            "      FILTER(regex(str(?v1),\"1997-10-15T16:00:00Z\") &&" +
            "             regex(str(?v2),\"PT5H30M\") && " +
            "             datatype(?v1) = xsd:dateTime &&" +
            "             datatype(?v2) = xsd:dayTimeDuration)}");
        
        assertSparqlQuery(model, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> " +
            "SELECT ?v1 ?v2 " +
            "WHERE " +
            "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
            "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
            "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
            "      FILTER(regex(str(?v1),\"1997-10-15T22:30:00Z\") &&" +
            "             regex(str(?v2),\"PT6H30M\") && " +
            "             datatype(?v1) = xsd:dateTime &&" +
            "             datatype(?v2) = xsd:dayTimeDuration)}");

		assertEquals(countOutgoingTriples(model, vfreebusyNode,NCAL.freebusy),3);
		
		assertSingleValueURIProperty(model, vfreebusyNode, NCAL.url, 
				"http://host2.com/pub/busy/jpublic-01.ifb");
		assertSingleValueProperty(model, vfreebusyNode, NCAL.comment,
				"This iCalendar file contains busy time information forthe next three months.");
		assertEquals(countOutgoingTriples(model, vfreebusyNode),11);
	}
	
	private void assertMultiValueIntermediateNodeProperty(Model model3, Resource vfreebusyNode, URI firstProperty,
	        URI nodeType, URI secondProperty, String value) {
        String sparqlQuery = "SELECT ?value WHERE " +
        		"{ " + vfreebusyNode.toSPARQL() + " " + firstProperty.toSPARQL() + " ?x ." +
        		"      ?x " + secondProperty.toSPARQL() + " ?value . FILTER(regex(str(?value),\""+ value +"\"))}";
        QueryResultTable table = model.sparqlSelect(sparqlQuery);
        ClosableIterator<QueryRow> queryIterator = table.iterator();
        assertTrue(queryIterator.hasNext());
        queryIterator.close();
    }

    public void testVJournalComponent() throws Exception {
	    model = readIcalFile("korganizer-jicaltest-vjournal.ics");
		Resource vjournalNode = findComponentByUid(model, "KOrganizer-948365006.348");
		
		assertMultiValueProperty(model,vjournalNode,RDF.type,NCAL.Journal);
		assertMultiValueProperty(model,vjournalNode,RDF.type,NIE.DataObject);
        assertMultiValueProperty(model,vjournalNode,RDF.type,NCAL.CalendarDataObject);
		assertEquals(countOutgoingTriples(model,vjournalNode,RDF.type),3);
        
		assertSingleValueProperty(model, vjournalNode, NCAL.created, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		assertSingleValueProperty(model, vjournalNode, NCAL.uid, "KOrganizer-948365006.348");
		assertSingleValueProperty(model, vjournalNode, NCAL.sequence, "0", XSD._integer);
		assertSingleValueProperty(model, vjournalNode, NCAL.lastModified, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		assertSingleValueProperty(model, vjournalNode, NCAL.dtstamp, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		findSingleNode(model, vjournalNode, NCAL.organizer);
		assertSingleValueProperty(model, vjournalNode, NCAL.description, "journal\n");
		assertSingleValueProperty(model, vjournalNode, NCAL.class_, NCAL.publicClassification);
		assertEquals(countOutgoingTriples(model,vjournalNode),11);
	}
	
	public void testVTimezoneComponent() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		
		assertTrue(vtimezoneNode instanceof URI);
		assertTrue(vtimezoneNode.toString().contains("America/New_York"));
		
		assertMultiValueProperty(model,vtimezoneNode,RDF.type,NCAL.Timezone);
		assertMultiValueProperty(model,vtimezoneNode,RDF.type,NIE.DataObject);
        assertMultiValueProperty(model,vtimezoneNode,RDF.type,NCAL.CalendarDataObject);
		assertEquals(countOutgoingTriples(model,vtimezoneNode,RDF.type),3);
		assertSingleValueProperty(model,vtimezoneNode,NCAL.tzid,
			"/softwarestudio.org/Olson_20011030_5/America/New_York");
		assertSingleValueURIProperty(model,vtimezoneNode,NCAL.tzurl,
			"http://timezones.r.us.net/tz/US-California-Los_Angeles");
		Resource standardObservanceNode = findSingleNode(model, vtimezoneNode, NCAL.standard);
		Resource daylightObservanceNode = findSingleNode(model, vtimezoneNode, NCAL.daylight);
		assertEquals(countOutgoingTriples(model, vtimezoneNode),7);
		
        assertSingleValueProperty(model,standardObservanceNode,RDF.type,NCAL.TimezoneObservance);
		assertSingleValueProperty(model,standardObservanceNode,NCAL.tzoffsetfrom,"-0400");
		assertSingleValueProperty(model,standardObservanceNode,NCAL.tzoffsetto,"-0500");
		assertSingleValueProperty(model,standardObservanceNode,NCAL.tzname,"EST");
        assertNcalDateTime(model,standardObservanceNode,NCAL.dtstart,"1970-10-25T02:00:00");
		Resource standardRRuleNode = findSingleNode(model,standardObservanceNode,NCAL.rrule);
		assertEquals(countOutgoingTriples(model,standardObservanceNode),6);
		
        assertSingleValueProperty(model,daylightObservanceNode,RDF.type,NCAL.TimezoneObservance);
		assertSingleValueProperty(model,daylightObservanceNode,NCAL.tzoffsetfrom,"-0500");
		assertSingleValueProperty(model,daylightObservanceNode,NCAL.tzoffsetto,"-0400");
		assertSingleValueProperty(model,daylightObservanceNode,NCAL.tzname,"EDT");
		assertNcalDateTime(model,daylightObservanceNode,NCAL.dtstart,"1970-04-05T02:00:00");
		Resource daylightRRuleNode = findSingleNode(model,daylightObservanceNode,NCAL.rrule);
		assertEquals(countOutgoingTriples(model,daylightObservanceNode),6);
		
        assertSingleValueProperty(model,standardRRuleNode,RDF.type, NCAL.RecurrenceRule);
		assertSingleValueProperty(model,standardRRuleNode,NCAL.freq, NCAL.yearly);
		assertSingleValueProperty(model,standardRRuleNode,NCAL.interval,"1", XSD._integer);
		assertBydayRulePart(model,standardRRuleNode,NCAL.sunday,"-1");
		assertSingleValueProperty(model,standardRRuleNode,NCAL.bymonth,"10", XSD._integer);
		assertEquals(countOutgoingTriples(model,standardRRuleNode),5);
		
        assertSingleValueProperty(model,daylightRRuleNode,RDF.type, NCAL.RecurrenceRule);
		assertSingleValueProperty(model,daylightRRuleNode,NCAL.freq, NCAL.yearly);
		assertSingleValueProperty(model,daylightRRuleNode,NCAL.interval,"1", XSD._integer);
        assertBydayRulePart(model,daylightRRuleNode,NCAL.sunday,"1");
		assertSingleValueProperty(model,daylightRRuleNode,NCAL.bymonth,"4", XSD._integer);
		assertEquals(countOutgoingTriples(model,daylightRRuleNode),5);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// PROPERTY TESTS //////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

    public void testActionProperty() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		assertSingleValueProperty(model, valarmNode, NCAL.action, NCAL.audioAction);
	}

	public void testAttachProperty() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
        Resource attachmentNode = findSingleNode(model, valarmNode, NCAL.attach);
		assertSingleValueURIProperty(model, attachmentNode, NCAL.attachmentUri, "http://www.w3.org/index.html");
        Resource pingUri = findSingleNode(model,attachmentNode, NCAL.attachmentUri);
        assertSingleValueURIProperty(model,pingUri, RDF.type, RDFS.Resource.toString());
	}

	public void testAttendeeProperty() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
        Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource attendeeBlankNode = findSingleNode(model, veventNode, NCAL.attendee);
        
        assertSingleValueProperty(model, attendeeBlankNode, RDF.type, NCAL.Attendee);
        Resource contactNode = findSingleNode(model, attendeeBlankNode, NCAL.involvedContact);
        assertEquals(countOutgoingTriples(model, attendeeBlankNode), 2);
        
        assertSingleValueProperty(model, contactNode, NCO.fullname, "Libby Miller");
        Resource emailNode = findSingleNode(model, contactNode, NCO.hasEmailAddress);
        assertSingleValueProperty(model, emailNode, NCO.emailAddress, "libby.miller@bristol.ac.uk");
		
	}

	public void testCalScaleProperty() throws Exception {
	    model = readIcalFile("basicCalendar.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, NCAL.calscale, NCAL.gregorianCalendarScale);
	}

	public void testCategoriesProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.categories, "Miscellaneous");
	}

	public void testClassProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.class_, NCAL.publicClassification);
	}

	public void testCommentProperty() throws Exception {
	    model = readIcalFile("calconnect7.ics");
        
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		assertSingleValueProperty(model, veventNode, NCAL.comment,
			"Another single instance reschedule - time only (+2 hrs)");
	}

	public void testCompletedProperty() throws Exception {
	    model = readIcalFile("Todos1.ics");
		Resource veventNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, veventNode, NCAL.completed, "2003-11-25T13:00:00Z",
			XSD._dateTime);
	}

	public void testContactProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.contact,
			"Jim Dolittle, ABC Industries, +1-919-555-1234");
	}

	public void testCreatedProperty() throws Exception {
	    model = readIcalFile("test-created.ics");
		Resource veventNode = findComponentByUid(model, "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
		assertSingleValueProperty(model, veventNode, NCAL.created, "2004-12-23T13:52:26",
			XSD._dateTime);
	}

	public void testDescriptionProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		assertSingleValueProperty(model, valarmNode, NCAL.description,
			"Federal Reserve Board Meeting");
	}

	public void testDtEndPropertyUTCTimeNoValueParameter() throws Exception {
	    model = readIcalFile("gkexample.ics");
		Resource veventWithDtStart = null;
        ClosableIterator<? extends Statement> iterator = model.findStatements(null, RDF.type, NCAL.Event);
		Statement statement = null;
		while (iterator.hasNext()) {
			// we rely on the fact, that the first node returned by this iterator
			// will be the second one defined in the file
			statement = iterator.next();
			veventWithDtStart = statement.getSubject();
		}
		iterator.close();
		Resource ncalDateTimeResource = findSingleNode(model, veventWithDtStart, NCAL.dtend);
		assertSingleValueProperty(model, ncalDateTimeResource, NCAL.dateTime, "2002-12-01T22:00:00Z",
			XSD._dateTime);
	}

	public void testDtEndPropertyDateValueParameter() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.dtend);
		assertSingleValueProperty(model, ncalDateTime, NCAL.date, "2002-07-06", XSD._date);
	}

	public void testDtEndPropertyWithTimeZoneId() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.dtend);
		assertSingleValueProperty(model, ncalDateTime, NCAL.dateTime, "2002-06-30T10:30:00", XSD._dateTime);
		Resource timezone = findSingleNode(model, ncalDateTime, NCAL.ncalTimezone);
		assertTrue(timezone.toString().contains("America/New_York"));
	}

	public void testDtStampProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.dtstamp, "2002-06-30T23:04:45Z",
			XSD._dateTime);
	}

	public void testDtStartPropertyUTCTimeNoValueParameter() throws Exception {
	    model = readIcalFile("gkexample.ics");
		Resource veventWithDtStart = null;
        ClosableIterator<? extends Statement> iterator = model.findStatements(Variable.ANY, RDF.type, NCAL.Event);
		Statement statement = null;
		while (iterator.hasNext()) {
			// we rely on the fact, that the nodes returned by this iterator
			// will have the same order as their definitions in the file
			statement = iterator.next();
			veventWithDtStart = statement.getSubject();
		}
		iterator.close();
		Resource node = findSingleNode(model, veventWithDtStart, NCAL.dtstart);
		assertSingleValueProperty(model, node, NCAL.dateTime, "2002-12-01T16:00:00Z",
			XSD._dateTime);
	}

	public void testDtStartPropertyDateValueParameter() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource resource = findSingleNode(model, veventNode, NCAL.dtstart);
		assertSingleValueProperty(model, resource, NCAL.date, "2002-07-03", XSD._date);
	}

	public void testDtStartPropertyWithTimeZoneId() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.dtstart);		
		assertSingleValueProperty(model, ncalDateTime, NCAL.dateTime, "2002-06-30T09:00:00", XSD._dateTime);
        Resource timezone = findSingleNode(model, ncalDateTime, NCAL.ncalTimezone);
        assertTrue(timezone.toString().contains("America/New_York"));
	}

	public void testDuePropertyUTCTimeNoValueParameter() throws Exception {
	    model = readIcalFile("Todos1.ics");
		Resource veventNode = findComponentByUid(model, "7611710A-5338-11D8-A876-000A958826AA");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.due);
		assertSingleValueProperty(model, ncalDateTime, NCAL.dateTime, "2003-12-16T00:00:00Z",
			XSD._dateTime);
	}

	public void testDuePropertyDateValueParameter() throws Exception {
	    model = readIcalFile("sunbird_sample.ics");
		Resource veventNode = findComponentByUid(model, "1E2C09FC-FBA7-11D7-B98C-000A958D1EFE");
		Resource ncaldateTime = findSingleNode(model, veventNode, NCAL.due);
		assertSingleValueProperty(model, ncaldateTime, NCAL.date, "2003-10-18", XSD._date);
	}

	public void testDuePropertyWithTimeZoneId() throws Exception {
	    model = readIcalFile("sunbird_sample.ics");
		Resource veventNode = findComponentByUid(model, "7A0EDDE6-FF8A-11D7-8061-000A958D1EFE");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.due);      
        assertSingleValueProperty(model, ncalDateTime, NCAL.dateTime, "2003-10-23T00:00:00", XSD._dateTime);
        Resource timezone = findSingleNode(model, ncalDateTime, NCAL.ncalTimezone);
        assertTrue(timezone.toString().contains("America/New_York"));
	}

	public void testDurationProperty() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		assertSingleValueProperty(model, veventNode, NCAL.duration, "PT1H", IcalCrawler.XSD_DAY_TIME_DURATION);
	}

	// This test should also be repeated three times (like the tests for
	// DUE and DTSTART) with varying types and VALUE parameters.
	public void testExDate() throws Exception {
	    model = readIcalFile("tag-bug.ics");
		Resource veventNode = findComponentByUid(model, "78492d2f-aacd-40e3-80cc-4f078d1516e0");
		Resource ncalDateTime = findSingleNode(model, veventNode, NCAL.exdate);
		assertSingleValueProperty(model, ncalDateTime, NCAL.date, "2002-02-25", XSD._date);
	}

	public void testExRule() throws Exception {
	    model = readIcalFile("cal01-exrule.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource recurrenceNode = findSingleNode(model, veventNode, NCAL.exrule);
		assertSingleValueProperty(model, recurrenceNode, RDF.type, NCAL.RecurrenceRule);
		assertSingleValueProperty(model, recurrenceNode, NCAL.freq, NCAL.weekly);
		assertSingleValueProperty(model, recurrenceNode, NCAL.interval, "5", XSD._integer);
		Resource bydayRulePart = findSingleNode(model, recurrenceNode, NCAL.byday);
		assertSingleValueProperty(model, bydayRulePart, NCAL.bydayWeekday, NCAL.sunday);
		assertEquals(countOutgoingTriples(model, recurrenceNode), 4);
	}

	public void testFreeBusyProperty() throws Exception {
	    model = readIcalFile("freebusy.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource vfreebusyNode = findSingleNode(model, calendarNode, NCAL.component);
		
		
		assertSparqlQuery(model, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> " +
            "SELECT ?v1 ?v2 " +
            "WHERE " +
            "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
            "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
            "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
            "      FILTER(regex(str(?v1),\"1997-10-15T05:00:00Z\") &&" +
            "             regex(str(?v2),\"PT8H30M\") && " +
            "             datatype(?v1) = xsd:dateTime &&" +
            "             datatype(?v2) = xsd:dayTimeDuration)}");
    
    assertSparqlQuery(model, "" +
        "PREFIX xsd: <" + XSD.XSD_NS + "> " +
        "SELECT ?v1 ?v2 " +
        "WHERE " +
        "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
        "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
        "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
        "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
        "      FILTER(regex(str(?v1),\"1997-10-15T16:00:00Z\") &&" +
        "             regex(str(?v2),\"PT5H30M\") && " +
        "             datatype(?v1) = xsd:dateTime &&" +
        "             datatype(?v2) = xsd:dayTimeDuration)}");
    
    assertSparqlQuery(model, "" +
        "PREFIX xsd: <" + XSD.XSD_NS + "> " +
        "SELECT ?v1 ?v2 " +
        "WHERE " +
        "{ " + vfreebusyNode.toSPARQL() + " " + NCAL.freebusy.toSPARQL() + " ?x ." +
        "      ?x " + RDF.type.toSPARQL() + " " + NCAL.FreebusyPeriod.toSPARQL() + " . " +
        "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
        "      ?x " + NCAL.periodDuration.toSPARQL() + " ?v2 . " +
        "      FILTER(regex(str(?v1),\"1997-10-15T22:30:00Z\") &&" +
        "             regex(str(?v2),\"PT6H30M\") && " +
        "             datatype(?v1) = xsd:dateTime &&" +
        "             datatype(?v2) = xsd:dayTimeDuration)}");
		
		assertEquals(countOutgoingTriples(model, vfreebusyNode,NCAL.freebusy),3);
	}

	public void testGeoProperty() throws Exception {
	    model = readIcalFile("geo1.ics");
		Resource veventNode = findComponentByUid(model, "CDC474D4-1393-11D7-9A2C-000393914268");
		Resource geoPointNode = findSingleNode(model, veventNode, NCAL.geo);

		assertSingleValueProperty(model, geoPointNode, RDF.type, GEO.Point);
		assertSingleValueProperty(model, geoPointNode, GEO.lat, "40.442673", XSD._decimal);
		assertSingleValueProperty(model, geoPointNode, GEO.long_, "-79.945815", XSD._decimal);
		assertEquals(countOutgoingTriples(model, geoPointNode), 3);
	}

	public void testLastModifiedProperty() throws Exception {
	    model = readIcalFile("test-created.ics");
		Resource veventNode = findComponentByUid(model, "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
		assertSingleValueProperty(model, veventNode, NCAL.lastModified, "2004-12-23T15:17:52",
			XSD._dateTime);
	}

	public void testLocationProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.location, "San Francisco");
	}

	public void testMethodProperty() throws Exception {
	    model = readIcalFile("basicCalendar.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, NCAL.method, "PUBLISH");
	}

	public void testOrganizerProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource organizerNode = findSingleNode(model, veventNode, NCAL.organizer);
		//assertSingleValueProperty(model, organizerNode, NCAL.cn, "Dan Connolly");
		//assertSingleValueProperty(model, organizerNode, NCAL.calAddress, "MAILTO:connolly@w3.org");
	}

	public void testPercentCompleteProperty() throws Exception {
	    model = readIcalFile("korganizer-jicaltest.ics");
		Resource veventNode = findComponentByUid(model, "KOrganizer-1573136895.534");
		assertSingleValueProperty(model, veventNode, NCAL.percentComplete, "0", XSD._integer);
	}

	public void testPriorityProperty() throws Exception {
	    model = readIcalFile("Todos1.ics");
		Resource vtodoNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, vtodoNode, NCAL.priority, "2", XSD._integer);
	}

	public void testProdIdProperty() throws Exception {
	    model = readIcalFile("Todos1.ics");
		Resource vcalendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, vcalendarNode, NCAL.prodid,
			"-//Apple Computer, Inc//iCal 1.5//EN");
	}

	public void testRecurrenceRule1() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource recurrenceNode = findSingleNode(model, veventNode, NCAL.rrule);
		assertSingleValueProperty(model, recurrenceNode, RDF.type, NCAL.RecurrenceRule);
		assertSingleValueProperty(model, recurrenceNode, NCAL.freq, NCAL.weekly);
		assertSingleValueProperty(model, recurrenceNode, NCAL.interval, "1", XSD._integer);
		Resource bydayRulePart = findSingleNode(model, recurrenceNode, NCAL.byday);
		assertSingleValueProperty(model, bydayRulePart, NCAL.bydayWeekday, NCAL.sunday);
		assertEquals(countOutgoingTriples(model, recurrenceNode), 4);
	}

	public void testRecurrenceRule2() throws Exception {
	    model = readIcalFile("gkexample.ics");
	    
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@antoni");
		Resource recurrenceNode = findSingleNode(model, veventNode, NCAL.rrule);
		assertSingleValueProperty(model, recurrenceNode, RDF.type, NCAL.RecurrenceRule);
        assertSingleValueProperty(model, recurrenceNode, NCAL.freq, NCAL.weekly);
        assertEquals(countOutgoingTriples(model, recurrenceNode), 4);
        
        assertSparqlQuery(model, "" +
            "SELECT ?x " +
            "WHERE " +
            "{ " + recurrenceNode.toSPARQL() + " " + NCAL.byday.toSPARQL() + " ?x . " +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.BydayRulePart.toSPARQL() + " . " +
            "      ?x " + NCAL.bydayWeekday.toSPARQL() + " " + NCAL.sunday.toSPARQL() + " . }");
        
        assertSparqlQuery(model, "" +
            "SELECT ?x " +
            "WHERE " +
            "{ " + recurrenceNode.toSPARQL() + " " + NCAL.byday.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.BydayRulePart.toSPARQL() + " . " +
            "      ?x " + NCAL.bydayWeekday.toSPARQL() + " " + NCAL.saturday.toSPARQL() + " . }");
	}

	public void testRDateProperty() throws Exception {
	    model = readIcalFile("calconnect9.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		
		assertPeriod(model, veventNode, NCAL.rdate, "2005-04-25T09:00:00", "2005-04-25T09:15:00");
		assertPeriod(model, veventNode, NCAL.rdate, "2005-04-26T09:00:00", "2005-04-26T09:15:00");
		assertPeriod(model, veventNode, NCAL.rdate, "2005-04-27T09:00:00", "2005-04-27T09:15:00");
		assertPeriod(model, veventNode, NCAL.rdate, "2005-04-28T09:00:00", "2005-04-28T09:15:00");
		assertPeriod(model, veventNode, NCAL.rdate, "2005-04-29T09:00:00", "2005-04-29T09:15:00");
		assertEquals(countOutgoingTriples(model, veventNode, NCAL.rdate), 5);
	}
	
	private void assertPeriod(Model model, Resource resource, URI property, String begin, String end) {
	    assertSparqlQuery(model, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> " +
            "SELECT ?v1 ?v2 " +
            "WHERE " +
            "{ " + resource.toSPARQL() + " " + property.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.NcalPeriod.toSPARQL() + " . " +
            "      ?x " + NCAL.periodBegin.toSPARQL() + " ?v1 . " +
            "      ?x " + NCAL.periodEnd.toSPARQL() + " ?v2 . " +
            "      FILTER(regex(str(?v1),\"" + begin + "\") &&" +
            "             regex(str(?v2),\"" + end + "\") && " +
            "             datatype(?v1) = xsd:dateTime &&" +
            "             datatype(?v2) = xsd:dateTime)}");
	}

	public void testRecurrenceIdProperty() throws Exception {
	    model = readIcalFile("calconnect7.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		Resource recurrenceBlankNode = findSingleNode(model, veventNode, NCAL.recurrenceId);
		Resource ncalDateTime = findSingleNode(model, recurrenceBlankNode, NCAL.recurrenceIdDateTime);
		assertSingleValueProperty(model, ncalDateTime, NCAL.dateTime, "2005-04-28T13:00:00Z",
			XSD._dateTime);
	}

	public void testRelatedToProperty() throws Exception {
	    model = readIcalFile("calconnect7.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		assertSingleValueProperty(model, veventNode, NCAL.relatedToParent,
			"<jsmith.part7.19960817T083000.xyzMail@host3.com>");
	}

	public void testRepeatProperty() throws Exception {
	    model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		assertSingleValueProperty(model, valarmNode, NCAL.repeat, "3", XSD._integer);
	}

	public void testRequestStatusProperty() throws Exception {
	    model = readIcalFile("incoming.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, NCAL.component);
		Resource requestStatus = findSingleNode(model, veventNode, NCAL.requestStatus);
		assertSingleValueProperty(model, requestStatus, NCAL.returnStatus, "2.0");
		assertSingleValueProperty(model, requestStatus, NCAL.statusDescription, "Success");
	}

	public void testResourcesProperty() throws Exception {
	    model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		assertMultiValueProperty(model, veventNode, NCAL.resources, "EASEL");
		assertMultiValueProperty(model, veventNode, NCAL.resources, "PROJECTOR");
		assertMultiValueProperty(model, veventNode, NCAL.resources, "VCR");
		assertEquals(countOutgoingTriples(model, veventNode, NCAL.resources),3);
	}

	public void testSequenceProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.sequence, "2", XSD._integer);
	}

	public void testStatusProperty() throws Exception {
	    model = readIcalFile("Todos1.ics");
		Resource vtodoNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, vtodoNode, NCAL.todoStatus, NCAL.completedStatus);
	}

	public void testSummaryProperty() throws Exception {
		// we deliberately chose an example with disregarded params
	    model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		assertSingleValueProperty(model, veventNode, NCAL.summary,
			"TP for Act ID XXXXX -- Not important : Stream -- Task ID");
	}

	public void testTranspProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.transp, NCAL.opaqueTransparency);
	}

	public void testTriggerPropertyWithDefinedDateTimeType() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, NCAL.trigger);
		assertSingleValueProperty(model, triggerBlankNode, NCAL.triggerDateTime, "2006-04-12T23:00:00Z",
			XSD._dateTime);
	}

	public void testTriggerPropertyWithSomeParams() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, NCAL.trigger);
		assertSingleValueProperty(model, triggerBlankNode, RDF.type, NCAL.Trigger);
		assertSingleValueProperty(model, triggerBlankNode, NCAL.triggerDuration, "-PT15M", IcalCrawler.XSD_DAY_TIME_DURATION);
		assertSingleValueProperty(model, triggerBlankNode, NCAL.related, NCAL.startTriggerRelation);
		assertEquals(countOutgoingTriples(model, triggerBlankNode), 3);
	}

	public void testTriggerPropertyWithoutDefinedType() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, NCAL.hasAlarm);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, NCAL.trigger);
		assertSingleValueProperty(model, triggerBlankNode, RDF.type, NCAL.Trigger);
		assertSingleValueProperty(model, triggerBlankNode, NCAL.triggerDuration, "-PT30M", IcalCrawler.XSD_DAY_TIME_DURATION);
		assertSingleValueProperty(model, triggerBlankNode, NCAL.related, NCAL.startTriggerRelation);
		assertEquals(countOutgoingTriples(model, triggerBlankNode), 3);
	}

	public void testTzidProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		assertSingleValueProperty(model, vtimezoneNode, NCAL.tzid,
			"/softwarestudio.org/Olson_20011030_5/America/New_York");
	}

	public void testTzNameProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, NCAL.daylight);
		assertSingleValueProperty(model, daylightNode, NCAL.tzname, "EDT");
	}

	public void testTzOffsetFromProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, NCAL.daylight);
		assertSingleValueProperty(model, daylightNode, NCAL.tzoffsetfrom, "-0500");
	}

	public void testTzOffsetToProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, NCAL.daylight);
		assertSingleValueProperty(model, daylightNode, NCAL.tzoffsetto, "-0400");
	}

	public void testTzUrlProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		assertSingleValueURIProperty(model, vtimezoneNode, NCAL.tzurl,
			"http://timezones.r.us.net/tz/US-California-Los_Angeles");
	}

	public void testUidProperty() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, NCAL.uid, "20020630T230445Z-3895-69-1-7@jammer");
	}

	public void testUrlProperty() throws Exception {
	    model = readIcalFile("geo1.ics");
		Resource veventNode = findComponentByUid(model, "CDC474D4-1393-11D7-9A2C-000393914268");
		assertSingleValueURIProperty(model, veventNode, NCAL.url,
			"http://kanzaki.com/works/2004/cal/0406vocab.html");
	}

	public void testVersionProperty() throws Exception {
	    model = readIcalFile("geo1.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, NCAL.version, "2.0");
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// PARAMETER TESTS ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	public void testCnParameter() throws Exception {
	    model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource attendeeBlankNode = findSingleNode(model, veventNode, NCAL.attendee);
		Resource contact = findSingleNode(model, attendeeBlankNode, NCAL.involvedContact);
		assertSingleValueProperty(model, contact, NCO.fullname, "Libby Miller");
	}

	public void testCuTypeParameter() throws Exception {
	    model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource attendeeBlankNode = findOneOfMultipleNodes(model, veventNode, NCAL.attendee);
		assertSingleValueProperty(model, attendeeBlankNode, NCAL.cutype, NCAL.individualUserType);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// CONVENIENCE METHODS ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Crawls the ICAL file and returns the Model with the generated RDF Tripples.
	 * 
	 * @return Model with generated RDF triples
	 */
	private Model readIcalFile(String fileName) throws Exception {
		InputStream fileStream = ClassLoader.getSystemResourceAsStream(ICAL_TESTICAL_PATH + fileName);
		assertNotNull(fileStream);
		File file = createTempFile(fileStream);
		assertTrue(file.canRead());
		
		IcalDataSource icalDataSource = new IcalDataSource();
		RDFContainer configurationContainer = createRDFContainer(SOURCE_TESTSOURCE);
		icalDataSource.setConfiguration(configurationContainer);
		
		
		icalDataSource.setRootUrl(file.getAbsolutePath());

		IcalTestSimpleCrawlerHandler testCrawlerHandler = new IcalTestSimpleCrawlerHandler();

		IcalCrawler icalCrawler = new IcalCrawler();
		icalCrawler.setDataSource(icalDataSource);
		icalCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
		icalCrawler.setCrawlerHandler(testCrawlerHandler);

		icalCrawler.crawl();

		assertTrue(file.delete());
		model = testCrawlerHandler.getModel();
		model2 = configurationContainer.getModel();
		return model;
	}

	/**
	 * Counts the triples in the model.
	 * 
	 * @param model The Model that contains the statements to be counted.
	 * @return The number of statements.
	 */
	private long countStatements(Model model) throws ModelException {
		return model.size();
	}

	private int countOutgoingTriples(Model model, Resource resource) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(resource, Variable.ANY, Variable.ANY);
    		int numberOfStatements = 0;
    		while (iterator.hasNext()) {
    			numberOfStatements++;
    			iterator.next();
    		}
    		iterator.close();
    		return numberOfStatements;
        } finally {
            closeIterator(iterator);
        }
	}

	private int countOutgoingTriples(Model model, Resource resource, URI predicate) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(resource, predicate, Variable.ANY);
    		int numberOfStatements = 0;
    		while (iterator.hasNext()) {
    			numberOfStatements++;
    			iterator.next();
    		}
    		iterator.close();
    		return numberOfStatements;
        } finally {
            closeIterator(iterator);
        }
	}

	private Resource findSingleNode(Model model, Resource parentNode, URI predicate) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(parentNode, predicate, Variable.ANY);
    		assertTrue(iterator.hasNext());
    		Statement statement = iterator.next();
    		assertFalse(iterator.hasNext());
    		iterator.close();
    		Node value = statement.getObject();
    		assertTrue(value instanceof Resource);
    		return (Resource) value;
        } finally {
            closeIterator(iterator);
        }
	}

	private Resource findOneOfMultipleNodes(Model model, Resource parentNode, URI predicate)
			throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(parentNode, predicate, Variable.ANY);
    		assertTrue(iterator.hasNext());
    		Statement statement = iterator.next();
    		Node value = statement.getObject();
    		assertTrue(value instanceof Resource);
    		iterator.close();
    		return (Resource) value;
        } finally {
            closeIterator(iterator);
        }
	}
	
	private void closeIterator(ClosableIterator<? extends Object> iterator) {
	    if (iterator != null) {
	        iterator.close();
	    }
	}

	private Resource findMainCalendarNode(Model model) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, RDF.type, NCAL.Calendar);
    		assertTrue(iterator.hasNext());
    		Statement statement = iterator.next();
    		assertFalse(iterator.hasNext());
    		iterator.close();
    		return statement.getSubject();
        } finally {
            closeIterator(iterator);
        }
	}

	private Resource findSingleTimezone(Model model) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, RDF.type, NCAL.Timezone);
    		assertTrue(iterator.hasNext());
    		Statement statement = iterator.next();
    		assertFalse(iterator.hasNext());
    		iterator.close();
    		return statement.getSubject();
        } finally {
            closeIterator(iterator);
        }
	}

	private Resource findComponentByUid(Model model, String uid) throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(Variable.ANY, NCAL.uid, Variable.ANY);
    		boolean found = false;
    		Statement statement = null;
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
        } finally {
            closeIterator(iterator);
        }
	}

	private void assertSingleValueProperty(Model model, Resource subject, URI predicate,
			String objectLabel) throws ModelException {
		assertSingleValueProperty(model, subject, predicate, model.createPlainLiteral(objectLabel));
	}

	/**
	 * Asserts that a given triple in the model exists AND that it is the only one with the given subject
	 * and predicate.
	 * 
	 * @param model
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	private void assertSingleValueProperty(Model model, Resource subject, URI predicate, Node object)
			throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
    		assertTrue(iterator.hasNext());
    		Statement statement = iterator.next();
    		assertFalse(iterator.hasNext());
    		assertEquals(statement.getObject().toString(), object.toString());
    		iterator.close();
        } finally {
            closeIterator(iterator);
        }
	}

	/**
	 * Asserts that a given triple in the model exists AND that it is the only one with the given subject
	 * and predicate AND that the object is a literal with a given XSD datatype.
	 * 
	 * @param model
	 * @param subject
	 * @param predicate
	 * @param objectLabel
	 * @param xsdDatatype
	 */
	private void assertSingleValueProperty(Model model, Resource subject, URI predicate,
			String objectLabel, URI xsdDatatype) throws ModelException {
	    
        ClosableIterator<? extends Statement> iterator = null;
        try {
            iterator = model.findStatements(subject, predicate, Variable.ANY);
    		assertTrue(iterator.hasNext()); // statement exists
    		Statement statement = iterator.next();
    		assertFalse(iterator.hasNext()); // it is the only one
    		iterator.close();
    		Node object = statement.getObject();
            assertTrue(object instanceof DatatypeLiteral); // the object is a literal
            DatatypeLiteral literal = (DatatypeLiteral) object;
            assertEquals(literal.getValue(), objectLabel); // it's label is as given
            assertEquals(literal.getDatatype(), xsdDatatype); // and datatype as well
        } finally {
            closeIterator(iterator);
        }

		
	}

	private void assertSingleValueURIProperty(Model model, Resource parentNode, URI predicate,
			String label) throws ModelException {
		Resource attachedUri = findSingleNode(model, parentNode, predicate);
		assertTrue(attachedUri instanceof URI);
		assertEquals(attachedUri.toString(), label);
	}

	/**
	 * Asserts that the given triple exists in the given model. It doesn't need to be the only one with
	 * the given subject and predicate.
	 * 
	 * @param model
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	private void assertMultiValueProperty(Model model, Resource subject, URI predicate,
			String valueLabel) throws ModelException {
		assertMultiValueProperty(model, subject, predicate, model.createPlainLiteral(valueLabel));
	}

	/**
	 * Asserts that the given triple exists in the given model. It doesn't need to be the only one with
	 * the given subject and predicate.
	 * 
	 * @param model
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	private void assertMultiValueProperty(Model model, Resource subject, URI predicate, Node value) 
			throws ModelException {
        ClosableIterator<? extends Statement> iterator = null;
        try {
        iterator = model.findStatements(subject, predicate, value);
		assertTrue(iterator.hasNext());
		iterator.close();
        } finally {
            closeIterator(iterator);
        }
	}
    
    private void assertNcalDateTime(Model testModel, Resource resource, URI property, String dateTimeValue) {
        assertSparqlQuery(testModel, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> " +
            "SELECT ?v1  " +
            "WHERE " +
            "{ " + resource.toSPARQL() + " " + property.toSPARQL() + " ?x ." +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.NcalDateTime.toSPARQL() + " . " +
            "      ?x " + NCAL.dateTime.toSPARQL() + " ?v1 . " +
            "      FILTER(regex(str(?v1),\"" + dateTimeValue + "\") &&" +
            "             datatype(?v1) = xsd:dateTime )}");
    }
    
    private void assertBydayRulePart(Model testModel, Resource resource, URI weekdayURI, String modifierValue) {
        assertSparqlQuery(testModel, "" +
            "PREFIX xsd: <" + XSD.XSD_NS + "> \n" +
            "SELECT ?x  \n" +
            "WHERE \n" +
            "{ " + resource.toSPARQL() + " " + NCAL.byday.toSPARQL() + " ?x .\n" +
            "      ?x " + RDF.type.toSPARQL() + " " + NCAL.BydayRulePart.toSPARQL() + " . \n" +
            "      ?x " + NCAL.bydayWeekday.toSPARQL() + " " + weekdayURI.toSPARQL() + " . \n" +
            ((modifierValue != null) ? 
                    "?x " + NCAL.bydayModifier.toSPARQL() + " ?y \n" +
                    " FILTER(regex(str(?y),\"" + modifierValue + "\") && datatype(?y) = xsd:integer )\n" : "\n") +
            "}");
    }
    
    private void assertSparqlQuery(Model model, String query) {
        ClosableIterator<QueryRow> queryIterator = null;
        QueryResultTable table = model.sparqlSelect(query);
        try {
        queryIterator = table.iterator();
        
        assertTrue(queryIterator.hasNext());
        queryIterator.close();
        } finally {
            closeIterator(queryIterator);
        }
    }
	
	public File createTempFile(InputStream fis) throws Exception {
		File outFile = File.createTempFile("temp", ".ics");
		outFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
		return outFile;
	}
}
