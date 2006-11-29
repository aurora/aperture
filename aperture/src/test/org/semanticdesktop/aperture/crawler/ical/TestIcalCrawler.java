/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.impl.sesame2.ModelImplSesame;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

public class TestIcalCrawler extends ApertureTestBase {

	public static final String ICAL_TESTDATA_PATH = DOCS_PATH + "icaltestdata/";
	public static final String TEMP_FILE_NAME = "temp-calendar.ics";

	private ModelImplSesame model;
	private ModelImplSesame model2;
	
	public void setUp() {
		
	}
	
	public void tearDown() {
		if (model != null) {
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
		Model model = readIcalFile("basicCalendar.ics");

		Resource calendarNode = findMainCalendarNode(model);

		assertSingleValueProperty(model, calendarNode, ICALTZD.calscale, "GREGORIAN");
		assertSingleValueProperty(model, calendarNode, ICALTZD.version, "2.0");
		assertSingleValueProperty(model, calendarNode, ICALTZD.method, "PUBLISH");

		// note that the comma is escaped in the original file, this is
		// compatible with the RFC 2445 4.3.11
		assertSingleValueProperty(model, calendarNode, ICALTZD.prodid,
			"-//Apple Computer, Inc//iCal 1.0//EN");

		assertMultiValueProperty(model, calendarNode, RDF.type, DATA.DataObject);
		assertMultiValueProperty(model, calendarNode, RDF.type, ICALTZD.Vcalendar);
		assertEquals(countOutgoingTriples(model, calendarNode, RDF.type), 2);

		assertEquals(countStatements(model), 6);
	}
	
	public void testValarmComponent() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model,veventNode,ICALTZD.component);
		
		Resource triggerBNode = findSingleNode(model,valarmNode,ICALTZD.trigger);
		assertSingleValueProperty(model,valarmNode,ICALTZD.action, "DISPLAY");
		assertSingleValueProperty(model,valarmNode,ICALTZD.description, "Federal Reserve Board Meeting");
		assertSingleValueURIProperty(model,valarmNode,RDF.type,ICALTZD.Valarm.toString());
		assertEquals(countOutgoingTriples(model, valarmNode),4);
		
		assertSingleValueProperty(model,triggerBNode,ICALTZD.related,"START");
		assertSingleValueProperty(model,triggerBNode,ICALTZD.value,"-PT30M",XSD._duration);
	}

	public void testVeventComponent() throws Exception {
		Model model = readIcalFile("cal01.ics");

		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");

		assertMultiValueProperty(model, veventNode, RDF.type, DATA.DataObject);
		assertMultiValueProperty(model, veventNode, RDF.type, ICALTZD.Vevent);		
		assertEquals(countOutgoingTriples(model, veventNode, RDF.type), 2);
		
		assertSingleValueProperty(model, veventNode, ICALTZD.uid, "20020630T230353Z-3895-69-1-0@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtstamp, "2002-06-30T23:03:53Z", 
			XSD._dateTime);
		assertSingleValueProperty(model, veventNode, ICALTZD.dtstart, "2002-06-30T09:00:00", 
			URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
		assertSingleValueProperty(model, veventNode, ICALTZD.dtend, "2002-06-30T10:30:00", 
			URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
		assertSingleValueProperty(model, veventNode, ICALTZD.transp, "OPAQUE");
		assertSingleValueProperty(model, veventNode, ICALTZD.sequence, "2", XSD._integer);
		assertSingleValueProperty(model, veventNode, ICALTZD.summary, "Church");
		assertSingleValueProperty(model, veventNode, ICALTZD.class_, "PRIVATE");
	    
		Resource recurBlankNode = findSingleNode(model, veventNode, ICALTZD.rrule);
		assertSingleValueProperty(model, recurBlankNode, ICALTZD.freq, "WEEKLY");
		assertSingleValueProperty(model, recurBlankNode, ICALTZD.interval, "1", XSD._integer);
		assertSingleValueProperty(model, recurBlankNode, ICALTZD.byday, "SU");
		assertEquals(countOutgoingTriples(model, recurBlankNode),3);
		
		assertEquals(countOutgoingTriples(model, veventNode),11);
	}
	
	public void testVFreebusyComponent() throws Exception {
		Model model = readIcalFile("freebusy.ics");
		Resource vcalendarNode = findMainCalendarNode(model);
		Resource vfreebusyNode = findSingleNode(model, vcalendarNode, ICALTZD.component);
		
		assertMultiValueProperty(model, vfreebusyNode, RDF.type, DATA.DataObject);
		assertMultiValueProperty(model, vfreebusyNode, RDF.type, ICALTZD.Vfreebusy);
		assertEquals(countOutgoingTriples(model, vfreebusyNode,RDF.type),2);
		findSingleNode(model,vfreebusyNode, ICALTZD.organizer);
		findSingleNode(model,vfreebusyNode, ICALTZD.attendee);
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T050000Z/PT8H30M");
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T160000Z/PT5H30M");
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T223000Z/PT6H30M");
		assertEquals(countOutgoingTriples(model, vfreebusyNode,ICALTZD.freebusy),3);
		assertSingleValueURIProperty(model, vfreebusyNode, ICALTZD.url, 
				"http://host2.com/pub/busy/jpublic-01.ifb");
		assertSingleValueProperty(model, vfreebusyNode, ICALTZD.comment,
				"This iCalendar file contains busy time information forthe next three months.");
		assertEquals(countOutgoingTriples(model, vfreebusyNode),10);
	}
	
	public void testVJournalComponent() throws Exception {
		Model model = readIcalFile("korganizer-jicaltest-vjournal.ics");
		Resource vjournalNode = findComponentByUid(model, "KOrganizer-948365006.348");
		
		assertMultiValueProperty(model,vjournalNode,RDF.type,ICALTZD.Vjournal);
		assertMultiValueProperty(model,vjournalNode,RDF.type,DATA.DataObject);
		assertEquals(countOutgoingTriples(model,vjournalNode,RDF.type),2);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.created, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.uid, "KOrganizer-948365006.348");
		assertSingleValueProperty(model, vjournalNode, ICALTZD.sequence, "0", XSD._integer);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.lastModified, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.dtstamp, "2003-02-27T11:07:15Z",
			XSD._dateTime);
		findSingleNode(model, vjournalNode, ICALTZD.organizer);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.description, "journal\n");
		assertSingleValueProperty(model, vjournalNode, ICALTZD.class_, "PUBLIC");
		assertSingleValueProperty(model, vjournalNode, ICALTZD.priority, "3", XSD._integer);
		assertSingleValueProperty(model, vjournalNode, ICALTZD.dtstart, "2003-02-24",XSD._date);
		assertEquals(countOutgoingTriples(model,vjournalNode),12);
	}
	
	public void testVTimezoneComponent() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		
		assertTrue(vtimezoneNode instanceof URI);
		assertEquals(vtimezoneNode.toString(),"http://www.w3.org/2002/12/cal/tzd/America/New_York#tz");
		
		assertMultiValueProperty(model,vtimezoneNode,RDF.type,ICALTZD.Vtimezone);
		assertMultiValueProperty(model,vtimezoneNode,RDF.type,DATA.DataObject);
		assertEquals(countOutgoingTriples(model,vtimezoneNode,RDF.type),2);
		
		assertSingleValueProperty(model,vtimezoneNode,ICALTZD.tzid,
			"/softwarestudio.org/Olson_20011030_5/America/New_York");
		assertSingleValueURIProperty(model,vtimezoneNode,ICALTZD.tzurl,
			"http://timezones.r.us.net/tz/US-California-Los_Angeles");
		Resource standardNode = findSingleNode(model, vtimezoneNode, ICALTZD.standard);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, ICALTZD.daylight);
		assertEquals(countOutgoingTriples(model, vtimezoneNode),6);
		
		assertSingleValueProperty(model,standardNode,ICALTZD.tzoffsetfrom,"-0400");
		assertSingleValueProperty(model,standardNode,ICALTZD.tzoffsetto,"-0500");
		assertSingleValueProperty(model,standardNode,ICALTZD.tzname,"EST");
		assertSingleValueProperty(model,standardNode,ICALTZD.dtstart,"1970-10-25T02:00:00",
			XSD._dateTime);
		Resource standardRRuleNode = findSingleNode(model,standardNode,ICALTZD.rrule);
		assertEquals(countOutgoingTriples(model,standardNode),5);
		
		assertSingleValueProperty(model,daylightNode,ICALTZD.tzoffsetfrom,"-0500");
		assertSingleValueProperty(model,daylightNode,ICALTZD.tzoffsetto,"-0400");
		assertSingleValueProperty(model,daylightNode,ICALTZD.tzname,"EDT");
		assertSingleValueProperty(model,daylightNode,ICALTZD.dtstart,"1970-04-05T02:00:00",
			XSD._dateTime);
		Resource daylightRRuleNode = findSingleNode(model,daylightNode,ICALTZD.rrule);
		assertEquals(countOutgoingTriples(model,daylightNode),5);
		
		assertSingleValueProperty(model,standardRRuleNode,ICALTZD.freq,"YEARLY");
		assertSingleValueProperty(model,standardRRuleNode,ICALTZD.interval,"1", XSD._integer);
		assertSingleValueProperty(model,standardRRuleNode,ICALTZD.byday,"-1SU");
		assertSingleValueProperty(model,standardRRuleNode,ICALTZD.bymonth,"10");
		assertEquals(countOutgoingTriples(model,standardRRuleNode),4);
		
		assertSingleValueProperty(model,daylightRRuleNode,ICALTZD.freq,"YEARLY");
		assertSingleValueProperty(model,daylightRRuleNode,ICALTZD.interval,"1", XSD._integer);
		assertSingleValueProperty(model,daylightRRuleNode,ICALTZD.byday,"1SU");
		assertSingleValueProperty(model,daylightRRuleNode,ICALTZD.bymonth,"4");
		assertEquals(countOutgoingTriples(model,daylightRRuleNode),4);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// PROPERTY TESTS //////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	public void testActionProperty() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, ICALTZD.component);
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		assertSingleValueProperty(model, valarmNode, ICALTZD.action, "AUDIO");
	}

	public void testAttachProperty() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		assertSingleValueURIProperty(model, valarmNode, ICALTZD.attach, "uri:Ping");
	}

	public void testAttendeeProperty() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, ICALTZD.component);
		Resource attendeeBlankNode = findSingleNode(model, veventNode, ICALTZD.attendee);
		assertSingleValueProperty(model, attendeeBlankNode, ICALTZD.cn, "Libby Miller");
		assertSingleValueProperty(model, attendeeBlankNode, ICALTZD.calAddress,
			"mailto:libby.miller@bristol.ac.uk");
		assertEquals(countOutgoingTriples(model, attendeeBlankNode), 2);
	}

	public void testCalScaleProperty() throws Exception {
		Model model = readIcalFile("basicCalendar.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, ICALTZD.calscale, "GREGORIAN");
	}

	public void testCategoriesProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.categories, "Miscellaneous");
	}

	public void testClassProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.class_, "PUBLIC");
	}

	public void testCommentProperty() throws Exception {
		Model model = readIcalFile("calconnect7.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		assertSingleValueProperty(model, veventNode, ICALTZD.comment,
			"Another single instance reschedule - time only (+2 hrs)");
	}

	public void testCompletedProperty() throws Exception {
		Model model = readIcalFile("Todos1.ics");
		Resource veventNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, veventNode, ICALTZD.completed, "2003-11-25T13:00:00Z",
			XSD._dateTime);
	}

	public void testContactProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.contact,
			"Jim Dolittle, ABC Industries, +1-919-555-1234");
	}

	public void testCreatedProperty() throws Exception {
		Model model = readIcalFile("test-created.ics");
		Resource veventNode = findComponentByUid(model, "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
		assertSingleValueProperty(model, veventNode, ICALTZD.created, "2004-12-23T13:52:26",
			XSD._dateTime);
	}

	public void testDescriptionProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		assertSingleValueProperty(model, valarmNode, ICALTZD.description,
			"Federal Reserve Board Meeting");
	}

	public void testDtEndPropertyUTCTimeNoValueParameter() throws Exception {
		Model model = readIcalFile("gkexample.ics");
		Resource veventWithDtStart = null;
		ClosableIterable<Statement> iterable = model.findStatements(null, RDF.type, ICALTZD.Vevent);
		ClosableIterator<Statement> iterator = iterable.iterator();
		Statement statement = null;
		while (iterator.hasNext()) {
			// we rely on the fact, that the first node returned by this iterator
			// will be the second one defined in the file
			statement = iterator.next();
			veventWithDtStart = statement.getSubject();
		}
		iterator.close();
		assertSingleValueProperty(model, veventWithDtStart, ICALTZD.dtend, "2002-12-01T22:00:00Z",
			XSD._dateTime);
	}

	public void testDtEndPropertyDateValueParameter() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtend, "2002-07-06", XSD._date);
	}

	public void testDtEndPropertyWithTimeZoneId() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtend, "2002-06-30T10:30:00", URIImpl.createURIWithoutChecking(
				"http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
	}

	public void testDtStampProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtstamp, "2002-06-30T23:04:45Z",
			XSD._dateTime);
	}

	public void testDtStartPropertyUTCTimeNoValueParameter() throws Exception {
		Model model = readIcalFile("gkexample.ics");
		Resource veventWithDtStart = null;
		ClosableIterable<Statement> iterable = model.findStatements(Variable.ANY, RDF.type, ICALTZD.Vevent);
		ClosableIterator<Statement> iterator = iterable.iterator();
		Statement statement = null;
		while (iterator.hasNext()) {
			// we rely on the fact, that the nodes returned by this iterator
			// will have the same order as their definitions in the file
			statement = iterator.next();
			veventWithDtStart = statement.getSubject();
		}
		iterator.close();
		assertSingleValueProperty(model, veventWithDtStart, ICALTZD.dtstart, "2002-12-01T16:00:00Z",
			XSD._dateTime);
	}

	public void testDtStartPropertyDateValueParameter() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtstart, "2002-07-03", XSD._date);
	}

	public void testDtStartPropertyWithTimeZoneId() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.dtstart, "2002-06-30T09:00:00",
			URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
	}

	public void testDuePropertyUTCTimeNoValueParameter() throws Exception {
		Model model = readIcalFile("Todos1.ics");
		Resource veventNode = findComponentByUid(model, "7611710A-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, veventNode, ICALTZD.due, "2003-12-16T00:00:00Z",
			XSD._dateTime);
	}

	public void testDuePropertyDateValueParameter() throws Exception {
		Model model = readIcalFile("sunbird_sample.ics");
		Resource veventNode = findComponentByUid(model, "1E2C09FC-FBA7-11D7-B98C-000A958D1EFE");
		assertSingleValueProperty(model, veventNode, ICALTZD.due, "2003-10-18", XSD._date);
	}

	public void testDuePropertyWithTimeZoneId() throws Exception {
		Model model = readIcalFile("sunbird_sample.ics");
		Resource veventNode = findComponentByUid(model, "7A0EDDE6-FF8A-11D7-8061-000A958D1EFE");
		assertSingleValueProperty(model, veventNode, ICALTZD.due, "2003-10-23T00:00:00", URIImpl.createURIWithoutChecking(
				"http://www.w3.org/2002/12/cal/tzd/America/New_York#tz"));
	}

	public void testDurationProperty() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource veventNode = findComponentByUid(model, "EB825E41-23CE-11D7-B93D-003065B0C95E");
		Resource durationNode = findSingleNode(model, veventNode, ICALTZD.duration);
		assertSingleValueProperty(model, durationNode, ICALTZD.value, "PT1H", XSD._duration);
	}

	// This test should also be repeated three times (like the tests for
	// DUE and DTSTART) with varying types and VALUE parameters.
	public void testExDate() throws Exception {
		Model model = readIcalFile("tag-bug.ics");
		Resource veventNode = findComponentByUid(model, "78492d2f-aacd-40e3-80cc-4f078d1516e0");
		assertSingleValueProperty(model, veventNode, ICALTZD.exdate, "2002-02-25", XSD._date);
	}

	public void testExRule() throws Exception {
		Model model = readIcalFile("cal01-exrule.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource recurrenceNode = findSingleNode(model, veventNode, ICALTZD.exrule);
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.freq, "WEEKLY");
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.interval, "5", XSD._integer);
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.byday, "SU");
		assertEquals(countOutgoingTriples(model, recurrenceNode), 3);
	}

	public void testFreeBusyProperty() throws Exception {
		Model model = readIcalFile("freebusy.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource vfreebusyNode = findSingleNode(model, calendarNode, ICALTZD.component);
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T050000Z/PT8H30M");
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T160000Z/PT5H30M");
		assertMultiValueProperty(model, vfreebusyNode, ICALTZD.freebusy, "19971015T223000Z/PT6H30M");
		assertEquals(countOutgoingTriples(model, vfreebusyNode,ICALTZD.freebusy),3);
	}

	public void testGeoProperty() throws Exception {
		Model model = readIcalFile("geo1.ics");
		Resource veventNode = findComponentByUid(model, "CDC474D4-1393-11D7-9A2C-000393914268");
		Resource firstListNode = findSingleNode(model, veventNode, ICALTZD.geo);

		assertSingleValueProperty(model, firstListNode, RDF.first, "40.442673", XSD._double);
		assertEquals(countOutgoingTriples(model, firstListNode), 2);
		Resource secondListNode = findSingleNode(model, firstListNode, RDF.rest);
		assertSingleValueProperty(model, secondListNode, RDF.first, "-79.945815", XSD._double);
		assertSingleValueProperty(model, secondListNode, RDF.rest, RDF.nil);
		assertEquals(countOutgoingTriples(model, secondListNode), 2);
	}

	public void testLastModifiedProperty() throws Exception {
		Model model = readIcalFile("test-created.ics");
		Resource veventNode = findComponentByUid(model, "A0831EE4-73D1-11D9-B5C3-000393CD78B4");
		assertSingleValueProperty(model, veventNode, ICALTZD.lastModified, "2004-12-23T15:17:52",
			XSD._dateTime);
	}

	public void testLocationProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.location, "San Francisco");
	}

	public void testMethodProperty() throws Exception {
		Model model = readIcalFile("basicCalendar.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, ICALTZD.method, "PUBLISH");
	}

	public void testOrganizerProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource organizerNode = findSingleNode(model, veventNode, ICALTZD.organizer);
		assertSingleValueProperty(model, organizerNode, ICALTZD.cn, "Dan Connolly");
		assertSingleValueProperty(model, organizerNode, ICALTZD.calAddress, "MAILTO:connolly@w3.org");
	}

	public void testPercentCompleteProperty() throws Exception {
		Model model = readIcalFile("korganizer-jicaltest.ics");
		Resource veventNode = findComponentByUid(model, "KOrganizer-1573136895.534");
		assertSingleValueProperty(model, veventNode, ICALTZD.percentComplete, "0", XSD._integer);
	}

	public void testPriorityProperty() throws Exception {
		Model model = readIcalFile("Todos1.ics");
		Resource vtodoNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, vtodoNode, ICALTZD.priority, "2", XSD._integer);
	}

	public void testProdIdProperty() throws Exception {
		Model model = readIcalFile("Todos1.ics");
		Resource vcalendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, vcalendarNode, ICALTZD.prodid,
			"-//Apple Computer, Inc//iCal 1.5//EN");
	}

	public void testRecurrenceRule1() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@jammer");
		Resource recurrenceNode = findSingleNode(model, veventNode, ICALTZD.rrule);
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.freq, "WEEKLY");
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.interval, "1", XSD._integer);
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.byday, "SU");
		assertEquals(countOutgoingTriples(model, recurrenceNode), 3);
	}

	public void testRecurrenceRule2() throws Exception {
		Model model = readIcalFile("gkexample.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230353Z-3895-69-1-0@antoni");
		Resource recurrenceNode = findSingleNode(model, veventNode, ICALTZD.rrule);
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.freq, "WEEKLY");
		assertSingleValueProperty(model, recurrenceNode, ICALTZD.byday, "SA,SU");
		assertEquals(countOutgoingTriples(model, recurrenceNode), 2);
	}

	public void testRDateProperty() throws Exception {
		Model model = readIcalFile("calconnect9.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		assertMultiValueProperty(model, veventNode, ICALTZD.rdate, "20050425T090000/20050425T091500");
		assertMultiValueProperty(model, veventNode, ICALTZD.rdate, "20050426T090000/20050426T091500");
		assertMultiValueProperty(model, veventNode, ICALTZD.rdate, "20050427T090000/20050427T091500");
		assertMultiValueProperty(model, veventNode, ICALTZD.rdate, "20050428T090000/20050428T091500");
		assertMultiValueProperty(model, veventNode, ICALTZD.rdate, "20050429T090000/20050429T091500");
		assertEquals(countOutgoingTriples(model, veventNode, ICALTZD.rdate), 5);
	}

	public void testRecurrenceIdProperty() throws Exception {
		Model model = readIcalFile("calconnect7.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		Resource recurrenceBlankNode = findSingleNode(model, veventNode, ICALTZD.recurrenceId);
		assertSingleValueProperty(model, recurrenceBlankNode, ICALTZD.value, "2005-04-28T13:00:00Z",
			XSD._dateTime);
	}

	public void testRelatedToProperty() throws Exception {
		Model model = readIcalFile("calconnect7.ics");
		Resource veventNode = findComponentByUid(model,
			"6BA1ECA4D58B306C85256FDB0071B664-Lotus_Notes_Generated");
		assertSingleValueProperty(model, veventNode, ICALTZD.relatedTo,
			"<jsmith.part7.19960817T083000.xyzMail@host3.com>");
	}

	public void testRepeatProperty() throws Exception {
		Model model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		assertSingleValueProperty(model, valarmNode, ICALTZD.repeat, "3", XSD._integer);
	}

	public void testRequestStatusProperty() throws Exception {
		Model model = readIcalFile("incoming.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, ICALTZD.component);
		assertSingleValueProperty(model, veventNode, ICALTZD.requestStatus, "2.0;Success");
	}

	public void testResourcesProperty() throws Exception {
		Model model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		assertSingleValueProperty(model, veventNode, ICALTZD.resources, "EASEL,PROJECTOR,VCR");
	}

	public void testSequenceProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.sequence, "2", XSD._integer);
	}

	public void testStatusProperty() throws Exception {
		Model model = readIcalFile("Todos1.ics");
		Resource vtodoNode = findComponentByUid(model, "76116BB6-5338-11D8-A876-000A958826AA");
		assertSingleValueProperty(model, vtodoNode, ICALTZD.status, "COMPLETED");
	}

	public void testSummaryProperty() throws Exception {
		// we deliberately chose an example with disregarded params
		Model model = readIcalFile("php-flp.ics");
		Resource veventNode = findComponentByUid(model, "TPACTIDSTREAMTASKID");
		assertSingleValueProperty(model, veventNode, ICALTZD.summary,
			"TP for Act ID XXXXX -- Not important : Stream -- Task ID");
	}

	public void testTranspProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.transp, "OPAQUE");
	}

	public void testTriggerPropertyWithDefinedDateTimeType() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, ICALTZD.component);
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, ICALTZD.trigger);
		assertSingleValueProperty(model, triggerBlankNode, ICALTZD.value, "2006-04-12T23:00:00Z",
			XSD._dateTime);
	}

	public void testTriggerPropertyWithSomeParams() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, ICALTZD.trigger);
		assertSingleValueProperty(model, triggerBlankNode, ICALTZD.value, "-PT15M", XSD._duration);
		assertSingleValueProperty(model, triggerBlankNode, ICALTZD.related, "START");
		assertEquals(countOutgoingTriples(model, triggerBlankNode), 2);
	}

	public void testTriggerPropertyWithoutDefinedType() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		Resource valarmNode = findSingleNode(model, veventNode, ICALTZD.component);
		Resource triggerBlankNode = findSingleNode(model, valarmNode, ICALTZD.trigger);
		assertSingleValueProperty(model, triggerBlankNode, ICALTZD.value, "-PT30M", XSD._duration);
		assertSingleValueProperty(model, triggerBlankNode, ICALTZD.related, "START");
		assertEquals(countOutgoingTriples(model, triggerBlankNode), 2);
	}

	public void testTzidProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		assertSingleValueProperty(model, vtimezoneNode, ICALTZD.tzid,
			"/softwarestudio.org/Olson_20011030_5/America/New_York");
	}

	public void testTzNameProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, ICALTZD.daylight);
		assertSingleValueProperty(model, daylightNode, ICALTZD.tzname, "EDT");
	}

	public void testTzOffsetFromProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, ICALTZD.daylight);
		assertSingleValueProperty(model, daylightNode, ICALTZD.tzoffsetfrom, "-0500");
	}

	public void testTzOffsetToProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		Resource daylightNode = findSingleNode(model, vtimezoneNode, ICALTZD.daylight);
		assertSingleValueProperty(model, daylightNode, ICALTZD.tzoffsetto, "-0400");
	}

	public void testTzUrlProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource vtimezoneNode = findSingleTimezone(model);
		assertSingleValueURIProperty(model, vtimezoneNode, ICALTZD.tzurl,
			"http://timezones.r.us.net/tz/US-California-Los_Angeles");
	}

	public void testUidProperty() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230445Z-3895-69-1-7@jammer");
		assertSingleValueProperty(model, veventNode, ICALTZD.uid, "20020630T230445Z-3895-69-1-7@jammer");
	}

	public void testUrlProperty() throws Exception {
		Model model = readIcalFile("geo1.ics");
		Resource veventNode = findComponentByUid(model, "CDC474D4-1393-11D7-9A2C-000393914268");
		assertSingleValueURIProperty(model, veventNode, ICALTZD.url,
			"http://kanzaki.com/works/2004/cal/0406vocab.html");
	}

	public void testVersionProperty() throws Exception {
		Model model = readIcalFile("geo1.ics");
		Resource calendarNode = findMainCalendarNode(model);
		assertSingleValueProperty(model, calendarNode, ICALTZD.version, "2.0");
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// PARAMETER TESTS ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	public void testCnParameter() throws Exception {
		Model model = readIcalFile("simplevevent.ics");
		Resource calendarNode = findMainCalendarNode(model);
		Resource veventNode = findSingleNode(model, calendarNode, ICALTZD.component);
		Resource attendeeBlankNode = findSingleNode(model, veventNode, ICALTZD.attendee);
		assertSingleValueProperty(model, attendeeBlankNode, ICALTZD.cn, "Libby Miller");
	}

	public void testCuTypeParameter() throws Exception {
		Model model = readIcalFile("cal01.ics");
		Resource veventNode = findComponentByUid(model, "20020630T230600Z-3895-69-1-16@jammer");
		Resource attendeeBlankNode = findOneOfMultipleNodes(model, veventNode, ICALTZD.attendee);
		assertSingleValueProperty(model, attendeeBlankNode, ICALTZD.cutype, "INDIVIDUAL");
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
		InputStream fileStream = ClassLoader.getSystemResourceAsStream(ICAL_TESTDATA_PATH + fileName);
		assertNotNull(fileStream);
		File file = createTempFile(fileStream);
		assertTrue(file.canRead());
		RDFContainer configurationContainer = createSesameRDFContainer("source:testsource");
		ConfigurationUtil.setRootUrl(file.getAbsolutePath(), configurationContainer);

		IcalDataSource icalDataSource = new IcalDataSource();
		icalDataSource.setConfiguration(configurationContainer);

		IcalTestSimpleCrawlerHandler testCrawlerHandler = new IcalTestSimpleCrawlerHandler();

		IcalCrawler icalCrawler = new IcalCrawler();
		icalCrawler.setDataSource(icalDataSource);
		icalCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
		icalCrawler.setCrawlerHandler(testCrawlerHandler);

		icalCrawler.crawl();

		assertTrue(file.delete());
		model = testCrawlerHandler.getModel();
		model2 = (ModelImplSesame)configurationContainer.getModel();
		return testCrawlerHandler.getModel();
	}

	/**
	 * Counts the triples in the model.
	 * 
	 * @param model The Model that contains the statements to be counted.
	 * @return The number of statements.
	 */
	private int countStatements(Model model) throws ModelException {
		return model.size();
	}

	private int countOutgoingTriples(Model model, Resource resource) throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(resource, Variable.ANY, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		int numberOfStatements = 0;
		while (iterator.hasNext()) {
			numberOfStatements++;
			iterator.next();
		}
		iterator.close();
		return numberOfStatements;
	}

	private int countOutgoingTriples(Model model, Resource resource, URI predicate) throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(resource, predicate, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		int numberOfStatements = 0;
		while (iterator.hasNext()) {
			numberOfStatements++;
			iterator.next();
		}
		iterator.close();
		return numberOfStatements;
	}

	private Resource findSingleNode(Model model, Resource parentNode, URI predicate) throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(parentNode, predicate, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		Statement statement = iterator.next();
		assertFalse(iterator.hasNext());
		iterator.close();
		Node value = statement.getObject();
		assertTrue(value instanceof Resource);
		return (Resource) value;
	}

	private Resource findOneOfMultipleNodes(Model model, Resource parentNode, URI predicate)
			throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(parentNode, predicate, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		Statement statement = iterator.next();
		Node value = statement.getObject();
		assertTrue(value instanceof Resource);
		iterator.close();
		return (Resource) value;
	}

	private Resource findMainCalendarNode(Model model) throws ModelException {
		ClosableIterable<Statement> iterable 
				= model.findStatements(Variable.ANY, RDF.type, ICALTZD.Vcalendar);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		Statement statement = iterator.next();
		assertFalse(iterator.hasNext());
		iterator.close();
		return statement.getSubject();
	}

	private Resource findSingleTimezone(Model model) throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(Variable.ANY, RDF.type, ICALTZD.Vtimezone);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		Statement statement = iterator.next();
		assertFalse(iterator.hasNext());
		iterator.close();
		return statement.getSubject();
	}

	private Resource findComponentByUid(Model model, String uid) throws ModelException {
		ClosableIterable<Statement> iterable = model.findStatements(Variable.ANY, ICALTZD.uid, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
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
		ClosableIterable<Statement> iterable = model.findStatements(subject, predicate, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		Statement statement = iterator.next();
		assertFalse(iterator.hasNext());
		assertEquals(statement.getObject().toString(), object.toString());
		iterator.close();
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
		ClosableIterable<Statement> iterable = model.findStatements(subject, predicate, Variable.ANY);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext()); // statement exists
		Statement statement = iterator.next();
		assertFalse(iterator.hasNext()); // it is the only one
		iterator.close();

		Node object = statement.getObject();
		assertTrue(object instanceof DatatypeLiteral); // the object is a literal
		DatatypeLiteral literal = (DatatypeLiteral) object;
		assertEquals(literal.getValue(), objectLabel); // it's label is as given
		assertEquals(literal.getDatatype(), xsdDatatype); // and datatype as well
	}

	private void assertSingleValueURIProperty(Model model, Resource parentNode, URI predicate,
			String label) throws ModelException {
		Resource attachedUri = findSingleNode(model, parentNode, predicate);
		assertTrue(attachedUri instanceof URI);
		assertEquals(attachedUri.toString(), label);
		assertEquals(countOutgoingTriples(model, attachedUri), 0);
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
		ClosableIterable<Statement> iterable = model.findStatements(subject, predicate, value);
		ClosableIterator<Statement> iterator = iterable.iterator();
		assertTrue(iterator.hasNext());
		iterator.close();
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
