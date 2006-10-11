package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Wed Oct 11 11:25:19 CEST 2006
 * input file: doc/ontology/icaltzd.rdfs
 * namespace: http://www.w3.org/2002/12/cal/icaltzd#
 */
public class ICALTZD_GEN {
	public static final String NS = "http://www.w3.org/2002/12/cal/icaltzd#";

    /**
     * Label: VEVENT 
     * Comment: Provide a grouping of component properties that describe an event. 
     */
    public static final URI Vevent = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vevent");

    /**
     * Label: VTODO 
     * Comment: Provide a grouping of calendar properties that describe a to-do. 
     */
    public static final URI Vtodo = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vtodo");

    /**
     * Label: VJOURNAL 
     * Comment: Provide a grouping of component properties that describe a journal entry. 
     */
    public static final URI Vjournal = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vjournal");

    /**
     * Label: VFREEBUSY 
     * Comment: Provide a grouping of component properties that describe either a request for free/busy time, describe a response to a request for free/busy time or describe a published set of busy time. 
     */
    public static final URI Vfreebusy = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vfreebusy");

    /**
     * Label: VTIMEZONE 
     * Comment: Provide a grouping of component properties that defines a time zone. 
     */
    public static final URI Vtimezone = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vtimezone");

    /**
     * Label: VALARM 
     * Comment: Provide a grouping of component properties that define an alarm. 
     */
    public static final URI Valarm = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Valarm");

    /**
     */
    public static final URI List_of_Float = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#List_of_Float");

    /**
     */
    public static final URI Value_DURATION = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Value_DURATION");

    /**
     */
    public static final URI Value_PERIOD = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Value_PERIOD");

    /**
     */
    public static final URI Value_CAL_ADDRESS = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Value_CAL-ADDRESS");

    /**
     */
    public static final URI DomainOf_rrule = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#DomainOf_rrule");

    /**
     */
    public static final URI Value_RECUR = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Value_RECUR");

    /**
     */
    public static final URI Value_DATE = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Value_DATE");

    /**
     * Label: VCALENDAR 
     */
    public static final URI Vcalendar = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#Vcalendar");

    /**
     * Label: CALSCALE 
     * Comment: This property defines the calendar scale used for the calendar information specified in the iCalendar object. value type: TEXT 
     * Range: string 
     */
    public static final URI calscale = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#calscale");

    /**
     * Label: METHOD 
     * Comment: This property defines the iCalendar object method associated with the calendar object. value type: TEXT 
     * Range: string 
     */
    public static final URI method = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#method");

    /**
     * Label: PRODID 
     * Comment: This property specifies the identifier for the product that created the iCalendar object. value type: TEXT 
     * Range: string 
     */
    public static final URI prodid = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#prodid");

    /**
     * Label: VERSION 
     * Comment: This property specifies the identifier corresponding to the highest version number or the minimum and maximum range of the iCalendar specification that is required in order to interpret the iCalendar object. value type: TEXT 
     * Range: string 
     */
    public static final URI version = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#version");

    /**
     * Label: CATEGORIES 
     * Comment: This property defines the categories for a calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI categories = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#categories");

    /**
     * Label: CLASS 
     * Comment: This property defines the access classification for a calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI class_ = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#class");

    /**
     * Label: COMMENT 
     * Comment: This property specifies non-processing information intended to provide a comment to the calendar user. value type: TEXT 
     * Range: string 
     */
    public static final URI comment = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#comment");

    /**
     * Label: DESCRIPTION 
     * Comment: This property provides a more complete description of the calendar component, than that provided by the "SUMMARY" property. value type: TEXT 
     * Range: string 
     */
    public static final URI description = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#description");

    /**
     * Label: LOCATION 
     * Comment: The property defines the intended venue for the activity defined by a calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI location = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#location");

    /**
     * Label: PERCENT-COMPLETE 
     * Comment: This property is used by an assignee or delegatee of a to-do to convey the percent completion of a to-do to the Organizer. value type: INTEGER 
     * Domain: Vtodo 
     * Range: integer 
     */
    public static final URI percentComplete = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#percentComplete");

    /**
     * Label: PRIORITY 
     * Comment: The property defines the relative priority for a calendar component. value type: INTEGER 
     * Range: integer 
     */
    public static final URI priority = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#priority");

    /**
     * Label: RESOURCES 
     * Comment: This property defines the equipment or resources anticipated for an activity specified by a calendar entity.. value type: TEXT 
     * Range: string 
     */
    public static final URI resources = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#resources");

    /**
     * Label: STATUS 
     * Comment: This property defines the overall status or confirmation for the calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI status = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#status");

    /**
     * Label: SUMMARY 
     * Comment: This property defines a short summary or subject for the calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI summary = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#summary");

    /**
     * Label: COMPLETED 
     * Comment: This property defines the date and time that a to-do was actually completed. value type: DATE-TIME 
     * Domain: Vtodo 
     * Range: Value_DATE-TIME 
     */
    public static final URI completed = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#completed");

    /**
     * Label: DTEND 
     * Comment: This property specifies the date and time that a calendar component ends. default value type: DATE-TIME 
     */
    public static final URI dtend = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#dtend");

    /**
     * Label: DUE 
     * Comment: This property defines the date and time that a to-do is expected to be completed. default value type: DATE-TIME 
     * Domain: Vtodo 
     */
    public static final URI due = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#due");

    /**
     * Label: DTSTART 
     * Comment: This property specifies when the calendar component begins. default value type: DATE-TIME 
     */
    public static final URI dtstart = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#dtstart");

    /**
     * Label: TRANSP 
     * Comment: This property defines whether an event is transparent or not to busy time searches. value type: TEXT 
     * Domain: Vevent 
     * Range: string 
     */
    public static final URI transp = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#transp");

    /**
     * Label: TZID 
     * Comment: This property specifies the text value that uniquely identifies the "VTIMEZONE" calendar component. value type: TEXT 
     * Domain: Vtimezone 
     * Range: string 
     */
    public static final URI tzid = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#tzid");

    /**
     * Label: TZNAME 
     * Comment: This property specifies the customary designation for a time zone description. value type: TEXT 
     * Domain: Vtimezone 
     * Range: string 
     */
    public static final URI tzname = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#tzname");

    /**
     * Label: TZOFFSETFROM 
     * Comment: This property specifies the offset which is in use prior to this time zone observance. value type: UTC-OFFSET 
     * Range: string 
     */
    public static final URI tzoffsetfrom = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#tzoffsetfrom");

    /**
     * Label: TZOFFSETTO 
     * Comment: This property specifies the offset which is in use in this time zone observance. value type: UTC-OFFSET 
     * Domain: Vtimezone 
     * Range: string 
     */
    public static final URI tzoffsetto = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#tzoffsetto");

    /**
     * Label: CONTACT 
     * Comment: The property is used to represent contact information or alternately a reference to contact information associated with the calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI contact = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#contact");

    /**
     * Label: RECURRENCE-ID 
     * Comment: This property is used in conjunction with the "UID" and "SEQUENCE" property to identify a specific instance of a recurring "VEVENT", "VTODO" or "VJOURNAL" calendar component. The property value is the effective value of the "DTSTART" property of the recurrence instance. default value type: DATE-TIME 
     * Domain: DomainOf_rrule 
     */
    public static final URI recurrenceId = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#recurrenceId");

    /**
     * Label: RELATED-TO 
     * Comment: The property is used to represent a relationship or reference between one calendar component and another. value type: TEXT 
     * Range: string 
     */
    public static final URI relatedTo = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#relatedTo");

    /**
     * Label: UID 
     * Comment: This property defines the persistent, globally unique identifier for the calendar component. value type: TEXT 
     * Range: string 
     */
    public static final URI uid = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#uid");

    /**
     * Label: EXDATE 
     * Comment: This property defines the list of date/time exceptions for a recurring calendar component. default value type: DATE-TIME 
     * Domain: DomainOf_rrule 
     */
    public static final URI exdate = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#exdate");

    /**
     * Label: RDATE 
     * Comment: This property defines the list of date/times for a recurrence set. default value type: DATE-TIME 
     */
    public static final URI rdate = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#rdate");

    /**
     * Label: ACTION 
     * Comment: This property defines the action to be invoked when an alarm is triggered. value type: TEXT 
     * Range: string 
     */
    public static final URI action = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#action");

    /**
     * Label: REPEAT 
     * Comment: This property defines the number of time the alarm should be repeated, after the initial trigger. value type: INTEGER 
     * Domain: Valarm 
     * Range: integer 
     */
    public static final URI repeat = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#repeat");

    /**
     * Label: CREATED 
     * Comment: This property specifies the date and time that the calendar information was created by the calendar user agent in the calendar store. Note: This is analogous to the creation date and time for a file in the file system. value type: DATE-TIME 
     * Range: Value_DATE-TIME 
     */
    public static final URI created = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#created");

    /**
     * Label: DTSTAMP 
     * Comment: The property indicates the date/time that the instance of the iCalendar object was created. value type: DATE-TIME 
     * Range: Value_DATE-TIME 
     */
    public static final URI dtstamp = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#dtstamp");

    /**
     * Label: LAST-MODIFIED 
     * Comment: The property specifies the date and time that the information associated with the calendar component was last revised in the calendar store. Note: This is analogous to the modification date and time for a file in the file system. value type: DATE-TIME 
     * Range: Value_DATE-TIME 
     */
    public static final URI lastModified = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#lastModified");

    /**
     * Label: SEQUENCE 
     * Comment: This property defines the revision sequence number of the calendar component within a sequence of revisions. value type: integer 
     * Range: integer 
     */
    public static final URI sequence = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#sequence");

    /**
     * Label: Any property name with a "X-" prefix 
     * Comment: This class of property provides a framework for defining non-standard properties. value type: TEXT 
     * Range: string 
     */
    public static final URI X_ = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#X-");

    /**
     * Label: REQUEST-STATUS 
     * Comment: This property defines the status code returned for a scheduling request. value type: TEXT 
     * Range: string 
     */
    public static final URI requestStatus = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#requestStatus");

    /**
     * Label: FREQ 
     */
    public static final URI freq = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#freq");

    /**
     * Label: INTERVAL 
     */
    public static final URI interval = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#interval");

    /**
     * Label: UNTIL 
     */
    public static final URI until = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#until");

    /**
     * Label: COUNT 
     */
    public static final URI count = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#count");

    /**
     * Label: BYSECOND 
     */
    public static final URI bysecond = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#bysecond");

    /**
     * Label: BYMINUTE 
     */
    public static final URI byminute = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#byminute");

    /**
     * Label: BYHOUR 
     */
    public static final URI byhour = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#byhour");

    /**
     * Label: BYDAY 
     */
    public static final URI byday = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#byday");

    /**
     * Label: BYMONTH 
     */
    public static final URI bymonth = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#bymonth");

    /**
     * Label: BYYEARDAY 
     */
    public static final URI byyearday = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#byyearday");

    /**
     * Label: BYWEEKNO 
     */
    public static final URI byweekno = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#byweekno");

    /**
     * Label: WKST 
     */
    public static final URI wkst = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#wkst");

    /**
     * Label: BYSETPOS 
     */
    public static final URI bysetpos = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#bysetpos");

    /**
     * Label: ALTREP 
     * Comment: To specify an alternate text representation for the property value. 
     */
    public static final URI altrep = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#altrep");

    /**
     * Label: CN 
     * Comment: To specify the common name to be associated with the calendar user specified by the property. 
     */
    public static final URI cn = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#cn");

    /**
     * Label: CUTYPE 
     * Comment: To specify the type of calendar user specified by the property. 
     */
    public static final URI cutype = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#cutype");

    /**
     * Label: DELEGATED-FROM 
     * Comment: To specify the calendar users that have delegated their participation to the calendar user specified by the property. 
     */
    public static final URI delegatedFrom = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#delegatedFrom");

    /**
     * Label: DELEGATED-TO 
     * Comment: To specify the calendar users to whom the calendar user specified by the property has delegated participation. 
     */
    public static final URI delegatedTo = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#delegatedTo");

    /**
     * Label: DIR 
     * Comment: To specify reference to a directory entry associated with the calendar user specified by the property. 
     */
    public static final URI dir = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#dir");

    /**
     * Label: ENCODING 
     * Comment: To specify an alternate inline encoding for the property value. 
     */
    public static final URI encoding = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#encoding");

    /**
     * Label: FMTTYPE 
     * Comment: To specify the content type of a referenced object. 
     */
    public static final URI fmttype = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#fmttype");

    /**
     * Label: FBTYPE 
     * Comment: To specify the free or busy time type. 
     */
    public static final URI fbtype = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#fbtype");

    /**
     * Label: LANGUAGE 
     * Comment: To specify the language for text values in a property or property parameter. 
     */
    public static final URI language = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#language");

    /**
     * Label: MEMBER 
     * Comment: To specify the group or list membership of the calendar user specified by the property. 
     */
    public static final URI member = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#member");

    /**
     * Label: PARTSTAT 
     * Comment: To specify the participation status for the calendar user specified by the property. 
     */
    public static final URI partstat = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#partstat");

    /**
     * Label: RANGE 
     * Comment: To specify the effective range of recurrence instances from the instance specified by the recurrence identifier specified by the property. 
     */
    public static final URI range = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#range");

    /**
     * Label: RELATED 
     * Comment: To specify the relationship of the alarm trigger with respect to the start or end of the calendar component. 
     */
    public static final URI related = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#related");

    /**
     * Label: RELTYPE 
     * Comment: To specify the type of hierarchical relationship associated with the calendar component specified by the property. 
     */
    public static final URI reltype = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#reltype");

    /**
     * Label: ROLE 
     * Comment: To specify the participation role for the calendar user specified by the property. 
     */
    public static final URI role = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#role");

    /**
     * Label: RSVP 
     * Comment: To specify whether there is an expectation of a favor of a reply from the calendar user specified by the property value. 
     */
    public static final URI rsvp = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#rsvp");

    /**
     * Label: SENT-BY 
     * Comment: To specify the calendar user that is acting on behalf of the calendar user specified by the property. 
     */
    public static final URI sentBy = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#sentBy");

    /**
     */
    public static final URI value = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");

    /**
     */
    public static final URI source = new URIImpl("http://purl.org/dc/elements/1.1/source");

    /**
     * Label: STANDARD 
     */
    public static final URI standard = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#standard");

    /**
     * Label: DAYLIGHT 
     */
    public static final URI daylight = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#daylight");

    /**
     * Label: ATTACH 
     * Comment: The property provides the capability to associate a document object with a calendar component. default value type: URI 
     */
    public static final URI attach = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#attach");

    /**
     * Label: GEO 
     * Comment: This property specifies information related to the global position for the activity specified by a calendar component. value type: list of FLOAT 
     * Range: List_of_Float 
     */
    public static final URI geo = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#geo");

    /**
     * Label: DURATION 
     * Comment: The property specifies a positive duration of time. value type: DURATION 
     * Range: Value_DURATION 
     */
    public static final URI duration = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#duration");

    /**
     * Label: FREEBUSY 
     * Comment: The property defines one or more free or busy time intervals. value type: PERIOD 
     * Range: Value_PERIOD 
     */
    public static final URI freebusy = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#freebusy");

    /**
     * Label: TZURL 
     * Comment: The TZURL provides a means for a VTIMEZONE component to point to a network location that can be used to retrieve an up-to- date version of itself. value type: URI 
     */
    public static final URI tzurl = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#tzurl");

    /**
     * Label: ATTENDEE 
     * Comment: The property defines an "Attendee" within a calendar component. value type: CAL-ADDRESS 
     * Range: Value_CAL-ADDRESS 
     */
    public static final URI attendee = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#attendee");

    /**
     * Label: ORGANIZER 
     * Comment: The property defines the organizer for a calendar component. value type: CAL-ADDRESS 
     * Range: Value_CAL-ADDRESS 
     */
    public static final URI organizer = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#organizer");

    /**
     * Label: URL 
     * Comment: This property defines a Uniform Resource Locator (URL) associated with the iCalendar object. value type: URI 
     */
    public static final URI url = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#url");

    /**
     * Label: EXRULE 
     * Comment: This property defines a rule or repeating pattern for an exception to a recurrence set. value type: RECUR 
     * Range: Value_RECUR 
     */
    public static final URI exrule = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#exrule");

    /**
     * Label: RRULE 
     * Comment: This property defines a rule or repeating pattern for recurring events, to-dos, or time zone definitions. value type: RECUR 
     * Range: Value_RECUR 
     */
    public static final URI rrule = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#rrule");

    /**
     * Label: TRIGGER 
     * Comment: This property specifies when an alarm will trigger. default value type: DURATION 
     */
    public static final URI trigger = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#trigger");

    /**
     */
    public static final URI component = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#component");

    /**
     */
    public static final URI calAddress = new URIImpl("http://www.w3.org/2002/12/cal/icaltzd#calAddress");

}
