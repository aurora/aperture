package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Feb 26 18:26:06 CET 2006
 * input file: doc/ontology/ical.rdfs
 * namespace: http://www.w3.org/2002/12/cal/ical#
 */
public class ICAL {
	public static final String NS = "http://www.w3.org/2002/12/cal/ical#";

    /**
     */
    public static final URI Value_CAL_ADDRESS = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_CAL-ADDRESS");

    /**
     */
    public static final URI Person = URIImpl.createURIWithoutChecking("http://xmlns.com/foaf/0.1/Person");

    /**
     * Label: VEVENT 
     * Comment: Provide a grouping of component properties that describe an event. 
     */
    public static final URI Vevent = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Vevent");

    /**
     * Label: VTODO 
     * Comment: Provide a grouping of calendar properties that describe a to-do. 
     */
    public static final URI Vtodo = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Vtodo");

    /**
     * Label: VJOURNAL 
     * Comment: Provide a grouping of component properties that describe a journal entry. 
     */
    public static final URI Vjournal = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Vjournal");

    /**
     * Label: VFREEBUSY 
     * Comment: Provide a grouping of component properties that describe either a request for free/busy time, describe a response to a request for free/busy time or describe a published set of busy time. 
     */
    public static final URI Vfreebusy = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Vfreebusy");

    /**
     * Label: VTIMEZONE 
     * Comment: Provide a grouping of component properties that defines a time zone. 
     */
    public static final URI Vtimezone = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Vtimezone");

    /**
     * Label: VALARM 
     * Comment: Provide a grouping of component properties that define an alarm. 
     */
    public static final URI Valarm = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Valarm");

    /**
     */
    public static final URI List_of_Float = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#List_of_Float");

    /**
     */
    public static final URI Value_DATE_TIME = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_DATE-TIME");

    /**
     */
    public static final URI Value_DATE = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_DATE");

    /**
     */
    public static final URI Value_DURATION = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_DURATION");

    /**
     */
    public static final URI Value_PERIOD = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_PERIOD");


    /**
     */
    public static final URI DomainOf_rrule = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#DomainOf_rrule");

    /**
     */
    public static final URI Value_RECUR = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#Value_RECUR");

    /**
     * Label: date 
     * Comment: date property of Dates. Sugar. 
     */
    public static final URI date = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#date");

    /**
     * Label: CALSCALE 
     * Comment: This property defines the calendar scale used for the calendar information specified in the iCalendar object. value type: TEXT 
     */
    public static final URI calscale = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#calscale");

    /**
     * Label: METHOD 
     * Comment: This property defines the iCalendar object method associated with the calendar object. value type: TEXT 
     */
    public static final URI method = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#method");

    /**
     * Label: PRODID 
     * Comment: This property specifies the identifier for the product that created the iCalendar object. value type: TEXT 
     */
    public static final URI prodid = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#prodid");

    /**
     * Label: VERSION 
     * Comment: This property specifies the identifier corresponding to the highest version number or the minimum and maximum range of the iCalendar specification that is required in order to interpret the iCalendar object. value type: TEXT 
     */
    public static final URI version = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#version");

    /**
     * Label: CATEGORIES 
     * Comment: This property defines the categories for a calendar component. value type: TEXT 
     */
    public static final URI categories = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#categories");

    /**
     * Label: CLASS 
     * Comment: This property defines the access classification for a calendar component. value type: TEXT 
     */
    public static final URI class_ = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#class");

    /**
     * Label: COMMENT 
     * Comment: This property specifies non-processing information intended to provide a comment to the calendar user. value type: TEXT 
     */
    public static final URI comment = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#comment");

    /**
     * Label: DESCRIPTION 
     * Comment: This property provides a more complete description of the calendar component, than that provided by the "SUMMARY" property. value type: TEXT 
     */
    public static final URI description = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#description");

    /**
     * Label: LOCATION 
     * Comment: The property defines the intended venue for the activity defined by a calendar component. value type: TEXT 
     */
    public static final URI location = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#location");

    /**
     * Label: PERCENT-COMPLETE 
     * Comment: This property is used by an assignee or delegatee of a to-do to convey the percent completion of a to-do to the Organizer. value type: INTEGER 
     */
    public static final URI percentComplete = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#percentComplete");

    /**
     * Label: PRIORITY 
     * Comment: The property defines the relative priority for a calendar component. value type: INTEGER 
     */
    public static final URI priority = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#priority");

    /**
     * Label: RESOURCES 
     * Comment: This property defines the equipment or resources anticipated for an activity specified by a calendar entity.. value type: TEXT 
     */
    public static final URI resources = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#resources");

    /**
     * Label: STATUS 
     * Comment: This property defines the overall status or confirmation for the calendar component. value type: TEXT 
     */
    public static final URI status = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#status");

    /**
     * Label: SUMMARY 
     * Comment: This property defines a short summary or subject for the calendar component. value type: TEXT 
     */
    public static final URI summary = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#summary");

    /**
     * Label: TRANSP 
     * Comment: This property defines whether an event is transparent or not to busy time searches. value type: TEXT 
     */
    public static final URI transp = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#transp");

    /**
     * Label: tzid 
     * Comment: This property specifies the text value that uniquely identifies the "VTIMEZONE" calendar component. value type: TEXT 
     */
    public static final URI tzid = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#tzid");

    /**
     * Label: TZNAME 
     * Comment: This property specifies the customary designation for a time zone description. value type: TEXT 
     */
    public static final URI tzname = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#tzname");

    /**
     * Label: TZOFFSETFROM 
     * Comment: This property specifies the offset which is in use prior to this time zone observance. value type: UTC-OFFSET 
     */
    public static final URI tzoffsetfrom = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#tzoffsetfrom");

    /**
     * Label: TZOFFSETTO 
     * Comment: This property specifies the offset which is in use in this time zone observance. value type: UTC-OFFSET 
     */
    public static final URI tzoffsetto = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#tzoffsetto");

    /**
     * Label: contact 
     * Comment: The property is used to represent contact information or alternately a reference to contact information associated with the calendar component. value type: TEXT 
     */
    public static final URI contact = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#contact");

    /**
     * Label: RELATED-TO 
     * Comment: The property is used to represent a relationship or reference between one calendar component and another. value type: TEXT 
     */
    public static final URI relatedTo = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#relatedTo");

    /**
     * Label: UID 
     * Comment: This property defines the persistent, globally unique identifier for the calendar component. value type: TEXT 
     */
    public static final URI uid = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#uid");

    /**
     * Label: ACTION 
     * Comment: This property defines the action to be invoked when an alarm is triggered. value type: TEXT 
     */
    public static final URI action = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#action");

    /**
     * Label: REPEAT 
     * Comment: This property defines the number of time the alarm should be repeated, after the initial trigger. value type: INTEGER 
     */
    public static final URI repeat = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#repeat");

    /**
     * Label: SEQUENCE 
     * Comment: This property defines the revision sequence number of the calendar component within a sequence of revisions. value type: integer 
     */
    public static final URI sequence = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#sequence");

    /**
     * Label: Any property name with a "X-" prefix 
     * Comment: This class of property provides a framework for defining non-standard properties. value type: TEXT 
     */
    public static final URI X_ = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#X-");

    /**
     * Label: REQUEST-STATUS 
     * Comment: This property defines the status code returned for a scheduling request. value type: TEXT 
     */
    public static final URI requestStatus = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#requestStatus");

    /**
     * Label: dateTime 
     * Comment: dateTime property of Dates. Sugar. 
     */
    public static final URI dateTime = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#dateTime");

    /**
     */
    public static final URI source = URIImpl.createURIWithoutChecking("http://purl.org/dc/elements/1.1/source");

    /**
     * Label: ATTACH 
     * Comment: The property provides the capability to associate a document object with a calendar component. default value type: URI 
     */
    public static final URI attach = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#attach");

    /**
     * Label: GEO 
     * Comment: This property specifies information related to the global position for the activity specified by a calendar component. value type: list of FLOAT 
     */
    public static final URI geo = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#geo");

    /**
     * Label: COMPLETED 
     * Comment: This property defines the date and time that a to-do was actually completed. value type: DATE-TIME 
     */
    public static final URI completed = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#completed");

    /**
     * Label: end 
     * Comment: This property specifies the date and time that a calendar component ends. default value type: DATE-TIME 
     */
    public static final URI dtend = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#dtend");

    /**
     * Label: DUE 
     * Comment: This property defines the date and time that a to-do is expected to be completed. default value type: DATE-TIME 
     */
    public static final URI due = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#due");

    /**
     * Label: start 
     * Comment: This property specifies when the calendar component begins. default value type: DATE-TIME 
     */
    public static final URI dtstart = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#dtstart");

    /**
     * Label: DURATION 
     * Comment: The property specifies a positive duration of time. value type: DURATION 
     */
    public static final URI duration = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#duration");

    /**
     * Label: FREEBUSY 
     * Comment: The property defines one or more free or busy time intervals. value type: PERIOD 
     */
    public static final URI freebusy = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#freebusy");

    /**
     * Label: TZURL 
     * Comment: The TZURL provides a means for a VTIMEZONE component to point to a network location that can be used to retrieve an up-to- date version of itself. value type: URI 
     */
    public static final URI tzurl = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#tzurl");

    /**
     * Label: attendee 
     * Comment: The property defines an "Attendee" within a calendar component. value type: CAL-ADDRESS 
     */
    public static final URI attendee = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#attendee");

    /**
     * Label: ORGANIZER 
     * Comment: The property defines the organizer for a calendar component. value type: CAL-ADDRESS 
     */
    public static final URI organizer = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#organizer");

    /**
     * Label: RECURRENCE-ID 
     * Comment: This property is used in conjunction with the "UID" and "SEQUENCE" property to identify a specific instance of a recurring "VEVENT", "VTODO" or "VJOURNAL" calendar component. The property value is the effective value of the "DTSTART" property of the recurrence instance. default value type: DATE-TIME 
     */
    public static final URI recurrenceId = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#recurrenceId");

    /**
     * Label: URL 
     * Comment: This property defines a Uniform Resource Locator (URL) associated with the iCalendar object. value type: URI 
     */
    public static final URI url = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#url");

    /**
     * Label: EXDATE 
     * Comment: This property defines the list of date/time exceptions for a recurring calendar component. default value type: DATE-TIME 
     */
    public static final URI exdate = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#exdate");

    /**
     * Label: EXRULE 
     * Comment: This property defines a rule or repeating pattern for an exception to a recurrence set. value type: RECUR 
     */
    public static final URI exrule = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#exrule");

    /**
     * Label: RDATE 
     * Comment: This property defines the list of date/times for a recurrence set. default value type: DATE-TIME 
     */
    public static final URI rdate = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#rdate");

    /**
     * Label: RRULE 
     * Comment: This property defines a rule or repeating pattern for recurring events, to-dos, or time zone definitions. value type: RECUR 
     */
    public static final URI rrule = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#rrule");

    /**
     * Label: TRIGGER 
     * Comment: This property specifies when an alarm will trigger. default value type: DURATION 
     */
    public static final URI trigger = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#trigger");

    /**
     * Label: CREATED 
     * Comment: This property specifies the date and time that the calendar information was created by the calendar user agent in the calendar store. Note: This is analogous to the creation date and time for a file in the file system. value type: DATE-TIME 
     */
    public static final URI created = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#created");

    /**
     * Label: creation time 
     * Comment: The property indicates the date/time that the instance of the iCalendar object was created. value type: DATE-TIME 
     */
    public static final URI dtstamp = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#dtstamp");

    /**
     * Label: last modified 
     * Comment: The property specifies the date and time that the information associated with the calendar component was last revised in the calendar store. Note: This is analogous to the modification date and time for a file in the file system. value type: DATE-TIME 
     */
    public static final URI lastModified = URIImpl.createURIWithoutChecking("http://www.w3.org/2002/12/cal/ical#lastModified");

}
