/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.vocabulary;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:36:00 CEST 2008
 * input file: D:\workspace\aperture/doc/ontology/ncal.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#
 */
public class NCAL {

    /** Path to the ontology resource */
    public static final String NCAL_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/vocabulary/ncal.rdfs";

    /**
     * Puts the NCAL ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNCALOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NCAL_RESOURCE_PATH, NCAL.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NCAL_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NCAL */
    public static final URI NS_NCAL = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#");
    /**
     * Type: Class <br/>
     * Label: ParticipationStatus  <br/>
     * Comment: Participation Status. This class has been introduced to express the limited vocabulary of values for the ncal:partstat property. See the documentation of ncal:partstat for details.  <br/>
     */
    public static final URI ParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus");
    /**
     * Type: Class <br/>
     * Label: RecurrenceFrequency  <br/>
     * Comment: Frequency of a recurrence rule. This class has been introduced to express a limited set of allowed values for the ncal:freq property. See the documentation of ncal:freq for details.  <br/>
     */
    public static final URI RecurrenceFrequency = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency");
    /**
     * Type: Class <br/>
     * Label: Attendee  <br/>
     * Comment: An attendee of an event. This class has been introduced to serve as the range for ncal:attendee property. See documentation of ncal:attendee for details.  <br/>
     */
    public static final URI Attendee = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee");
    /**
     * Type: Class <br/>
     * Label: AttendeeOrOrganizer  <br/>
     * Comment: A common superclass for ncal:Attendee and ncal:Organizer.  <br/>
     */
    public static final URI AttendeeOrOrganizer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeOrOrganizer");
    /**
     * Type: Class <br/>
     * Label: Freebusy  <br/>
     * Comment: Provide a grouping of component properties that describe either a request for free/busy time, describe a response to a request for free/busy time or describe a published set of busy time.  <br/>
     */
    public static final URI Freebusy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Freebusy");
    /**
     * Type: Class <br/>
     * Label: UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo  <br/>
     */
    public static final URI UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfAlarmEventFreebusyJournalTodo  <br/>
     */
    public static final URI UnionOfAlarmEventFreebusyJournalTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventFreebusyJournalTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfEventFreebusyJournalTodo  <br/>
     */
    public static final URI UnionOfEventFreebusyJournalTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfTimezoneObservanceEventFreebusyTimezoneTodo  <br/>
     */
    public static final URI UnionOfTimezoneObservanceEventFreebusyTimezoneTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventFreebusyTimezoneTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfAlarmEventFreebusyTodo  <br/>
     */
    public static final URI UnionOfAlarmEventFreebusyTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventFreebusyTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfEventFreebusy  <br/>
     */
    public static final URI UnionOfEventFreebusy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusy");
    /**
     * Type: Class <br/>
     * Label: NcalDateTime  <br/>
     */
    public static final URI NcalDateTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime");
    /**
     * Type: Class <br/>
     * Label: NcalTimeEntity  <br/>
     * Comment: A time entity. Conceived as a common superclass for NcalDateTime and NcalPeriod. According to RFC 2445 both DateTime and Period can be interpreted in different timezones. The first case is explored in many properties. The second case is theoretically possible in ncal:rdate property. Therefore the timezone properties have been defined at this level.  <br/>
     */
    public static final URI NcalTimeEntity = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalTimeEntity");
    /**
     * Type: Class <br/>
     * Label: UnionOfAlarmEventJournalTodo  <br/>
     */
    public static final URI UnionOfAlarmEventJournalTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo");
    /**
     * Type: Class <br/>
     * Label: UnionParentClass  <br/>
     */
    public static final URI UnionParentClass = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionParentClass");
    /**
     * Type: Class <br/>
     * Label: RecurrenceIdentifier  <br/>
     * Comment: Recurrence Identifier. Introduced to provide a structure for the value of ncal:recurrenceId property. See the documentation of ncal:recurrenceId for details.  <br/>
     */
    public static final URI RecurrenceIdentifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifier");
    /**
     * Type: Class <br/>
     * Label: Journal  <br/>
     * Comment: Provide a grouping of component properties that describe a journal entry.  <br/>
     */
    public static final URI Journal = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Journal");
    /**
     * Type: Class <br/>
     * Label: UnionOfEventJournalTodo  <br/>
     */
    public static final URI UnionOfEventJournalTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfTimezoneObservanceEventJournalTimezoneTodo  <br/>
     */
    public static final URI UnionOfTimezoneObservanceEventJournalTimezoneTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventJournalTimezoneTodo");
    /**
     * Type: Class <br/>
     * Label: UnionOfEventJournalTimezoneTodo  <br/>
     */
    public static final URI UnionOfEventJournalTimezoneTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTimezoneTodo");
    /**
     * Type: Class <br/>
     * Label: AlarmAction  <br/>
     * Comment: Action to be performed on alarm. This class has been introduced to express the limited set of values of the ncal:action property. Please refer to the documentation of ncal:action for details.  <br/>
     */
    public static final URI AlarmAction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction");
    /**
     * Type: Class <br/>
     * Label: TriggerRelation  <br/>
     * Comment: The relation between the trigger and its parent calendar component. This class has been introduced to express the limited vocabulary for the ncal:related property. See the documentation for ncal:related for more details.  <br/>
     */
    public static final URI TriggerRelation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TriggerRelation");
    /**
     * Type: Class <br/>
     * Label: Timezone  <br/>
     * Comment: Provide a grouping of component properties that defines a time zone.  <br/>
     */
    public static final URI Timezone = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone");
    /**
     * Type: Class <br/>
     * Label: FreebusyType  <br/>
     * Comment: Type of a Freebusy indication. This class has been introduced to serve as a limited set of values for the ncal:fbtype property. See the documentation of ncal:fbtype for details.  <br/>
     */
    public static final URI FreebusyType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType");
    /**
     * Type: Class <br/>
     * Label: BydayRulePart  <br/>
     * Comment: Expresses the compound value of a byday part of a recurrence rule. It stores the weekday and the integer modifier. Inspired by RFC 2445 sec. 4.3.10  <br/>
     */
    public static final URI BydayRulePart = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#BydayRulePart");
    /**
     * Type: Class <br/>
     * Label: RecurrenceRule  <br/>
     */
    public static final URI RecurrenceRule = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule");
    /**
     * Type: Class <br/>
     * Label: Alarm  <br/>
     * Comment: Provide a grouping of component properties that define an alarm.  <br/>
     */
    public static final URI Alarm = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Alarm");
    /**
     * Type: Class <br/>
     * Label: UnionOfAlarmEventTodo  <br/>
     */
    public static final URI UnionOfAlarmEventTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventTodo");
    /**
     * Type: Class <br/>
     * Label: Event  <br/>
     * Comment: Provide a grouping of component properties that describe an event.  <br/>
     */
    public static final URI Event = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Event");
    /**
     * Type: Class <br/>
     * Label: UnionOfEventTodo  <br/>
     */
    public static final URI UnionOfEventTodo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo");
    /**
     * Type: Class <br/>
     * Label: FreebusyPeriod  <br/>
     * Comment: An aggregate of a period and a freebusy type. This class has been introduced to serve as a range of the ncal:freebusy property. See documentation for ncal:freebusy for details. Note that the specification of freebusy property states that the period is to be expressed using UTC time, so the timezone properties should NOT be used for instances of this class.  <br/>
     */
    public static final URI FreebusyPeriod = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyPeriod");
    /**
     * Type: Class <br/>
     * Label: NcalPeriod  <br/>
     * Comment: A period of time. Inspired by the PERIOD datatype specified in RFC 2445 sec. 4.3.9  <br/>
     */
    public static final URI NcalPeriod = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalPeriod");
    /**
     * Type: Class <br/>
     * Label: CalendarScale  <br/>
     * Comment: A calendar scale. This class has been introduced to provide the limited vocabulary for the ncal:calscale property.  <br/>
     */
    public static final URI CalendarScale = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarScale");
    /**
     * Type: Class <br/>
     * Label: AccessClassification  <br/>
     * Comment: Access classification of a calendar component. Introduced to express 
the set of values for the ncal:class property. The user may use instances
provided with this ontology or create his/her own with desired semantics.
See the documentation of ncal:class for details.  <br/>
     */
    public static final URI AccessClassification = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AccessClassification");
    /**
     * Type: Class <br/>
     * Label: Calendar  <br/>
     * Comment: A calendar. Inspirations for this class can be traced to the VCALENDAR component defined in RFC 2445 sec. 4.4, but it may just as well be used to represent any kind of Calendar.  <br/>
     */
    public static final URI Calendar = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar");
    /**
     * Type: Class <br/>
     * Label: TodoStatus  <br/>
     * Comment: A status of a calendar entity. This class has been introduced to express
the limited set of values for the ncal:status property. The user may
use the instances provided with this ontology or create his/her own.
See the documentation for ncal:todoStatus for details.  <br/>
     */
    public static final URI TodoStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus");
    /**
     * Type: Class <br/>
     * Label: CalendarUserType  <br/>
     * Comment: A calendar user type. This class has been introduced to express the limited vocabulary for the ncal:cutype property. See documentation of ncal:cutype for details.  <br/>
     */
    public static final URI CalendarUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType");
    /**
     * Type: Class <br/>
     * Label: AttachmentEncoding  <br/>
     * Comment: Attachment encoding. This class has been introduced to express the limited vocabulary of values for the ncal:encoding property. See the documentation of ncal:encoding for details.  <br/>
     */
    public static final URI AttachmentEncoding = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttachmentEncoding");
    /**
     * Type: Class <br/>
     * Label: EventStatus  <br/>
     * Comment: A status of an event. This class has been introduced to express
the limited set of values for the ncal:status property. The user may
use the instances provided with this ontology or create his/her own.
See the documentation for ncal:eventStatus for details.  <br/>
     */
    public static final URI EventStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#EventStatus");
    /**
     * Type: Class <br/>
     * Label: Attachment  <br/>
     * Comment: An object attached to a calendar entity. This class has been introduced to serve as a structured value of the ncal:attach property. See the documentation of ncal:attach for details.  <br/>
     */
    public static final URI Attachment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment");
    /**
     * Type: Class <br/>
     * Label: Trigger  <br/>
     * Comment: An alarm trigger. This class has been created to serve as the range of ncal:trigger property. See the documentation for ncal:trigger for more details.  <br/>
     */
    public static final URI Trigger = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Trigger");
    /**
     * Type: Class <br/>
     * Label: Organizer  <br/>
     * Comment: An organizer of an event. This class has been introduced to serve as a range of ncal:organizer property. See documentation of ncal:organizer for details.  <br/>
     */
    public static final URI Organizer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Organizer");
    /**
     * Type: Class <br/>
     * Label: Weekday  <br/>
     * Comment: Day of the week. This class has been created to provide the limited vocabulary for ncal:byday property. See the documentation for ncal:byday for details.  <br/>
     */
    public static final URI Weekday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday");
    /**
     * Type: Class <br/>
     * Label: CalendarDataObject  <br/>
     * Comment: A DataObject found in a calendar. It is usually interpreted as one of the calendar entity types (e.g. Event, Journal, Todo etc.)  <br/>
     */
    public static final URI CalendarDataObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarDataObject");
    /**
     * Type: Class <br/>
     * Label: RecurrenceIdentifierRange  <br/>
     * Comment: Recurrence Identifier Range. This class has been created to provide means to express the limited set of values for the ncal:range property. See documentation for ncal:range for details.  <br/>
     */
    public static final URI RecurrenceIdentifierRange = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifierRange");
    /**
     * Type: Class <br/>
     * Label: JournalStatus  <br/>
     * Comment: A status of a journal entry. This class has been introduced to express
the limited set of values for the ncal:status property. The user may
use the instances provided with this ontology or create his/her own.
See the documentation for ncal:journalStatus for details.  <br/>
     */
    public static final URI JournalStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#JournalStatus");
    /**
     * Type: Class <br/>
     * Label: RequestStatus  <br/>
     * Comment: Request Status. A class that was introduced to provide a structure for the value of ncal:requestStatus property. See documentation for ncal:requestStatus for details.  <br/>
     */
    public static final URI RequestStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RequestStatus");
    /**
     * Type: Class <br/>
     * Label: Todo  <br/>
     * Comment: Provide a grouping of calendar properties that describe a to-do.  <br/>
     */
    public static final URI Todo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Todo");
    /**
     * Type: Class <br/>
     * Label: TimeTransparency  <br/>
     * Comment: Time transparency. Introduced to provide a way to express
the limited vocabulary for the values of ncal:transp property.
See documentation of ncal:transp for details.  <br/>
     */
    public static final URI TimeTransparency = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimeTransparency");
    /**
     * Type: Class <br/>
     * Label: TimezoneObservance  <br/>
     */
    public static final URI TimezoneObservance = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance");
    /**
     * Type: Class <br/>
     * Label: AttendeeRole  <br/>
     * Comment: A role the attendee is going to play during an event. This class has been introduced to express the limited vocabulary for the values of ncal:role property. Please refer to the documentation of ncal:role for details.  <br/>
     */
    public static final URI AttendeeRole = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: declinedParticipationStatus  <br/>
     */
    public static final URI declinedParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#declinedParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: tentativeParticipationStatus  <br/>
     */
    public static final URI tentativeParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tentativeParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: acceptedParticipationStatus  <br/>
     */
    public static final URI acceptedParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#acceptedParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: needsActionParticipationStatus  <br/>
     */
    public static final URI needsActionParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#needsActionParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: delegatedParticipationStatus  <br/>
     */
    public static final URI delegatedParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#delegatedParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: inProcessParticipationStatus  <br/>
     */
    public static final URI inProcessParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#inProcessParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus <br/>
     * Label: completedParticipationStatus  <br/>
     */
    public static final URI completedParticipationStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#completedParticipationStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: yearly  <br/>
     */
    public static final URI yearly = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#yearly");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: minutely  <br/>
     */
    public static final URI minutely = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#minutely");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: weekly  <br/>
     */
    public static final URI weekly = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#weekly");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: secondly  <br/>
     */
    public static final URI secondly = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#secondly");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: hourly  <br/>
     */
    public static final URI hourly = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#hourly");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: monthly  <br/>
     */
    public static final URI monthly = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#monthly");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency <br/>
     * Label: daily  <br/>
     */
    public static final URI daily = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#daily");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction <br/>
     * Label: displayAction  <br/>
     */
    public static final URI displayAction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#displayAction");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction <br/>
     * Label: audioAction  <br/>
     */
    public static final URI audioAction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#audioAction");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction <br/>
     * Label: procedureAction  <br/>
     */
    public static final URI procedureAction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#procedureAction");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction <br/>
     * Label: emailAction  <br/>
     */
    public static final URI emailAction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#emailAction");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TriggerRelation <br/>
     * Label: startTriggerRelation  <br/>
     */
    public static final URI startTriggerRelation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#startTriggerRelation");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TriggerRelation <br/>
     * Label: endTriggerRelation  <br/>
     */
    public static final URI endTriggerRelation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#endTriggerRelation");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType <br/>
     * Label: busyTentativeFreebusyType  <br/>
     */
    public static final URI busyTentativeFreebusyType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#busyTentativeFreebusyType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType <br/>
     * Label: busyUnavailableFreebusyType  <br/>
     */
    public static final URI busyUnavailableFreebusyType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#busyUnavailableFreebusyType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType <br/>
     * Label: busyFreebusyType  <br/>
     */
    public static final URI busyFreebusyType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#busyFreebusyType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType <br/>
     * Label: freeFreebusyType  <br/>
     */
    public static final URI freeFreebusyType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#freeFreebusyType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarScale <br/>
     * Label: gregorianCalendarScale  <br/>
     */
    public static final URI gregorianCalendarScale = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#gregorianCalendarScale");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AccessClassification <br/>
     * Label: privateClassification  <br/>
     */
    public static final URI privateClassification = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#privateClassification");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AccessClassification <br/>
     * Label: publicClassification  <br/>
     */
    public static final URI publicClassification = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#publicClassification");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AccessClassification <br/>
     * Label: confidentialClassification  <br/>
     */
    public static final URI confidentialClassification = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#confidentialClassification");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus <br/>
     * Label: needsActionStatus  <br/>
     */
    public static final URI needsActionStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#needsActionStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus <br/>
     * Label: cancelledTodoStatus  <br/>
     */
    public static final URI cancelledTodoStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#cancelledTodoStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus <br/>
     * Label: inProcessStatus  <br/>
     */
    public static final URI inProcessStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#inProcessStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus <br/>
     * Label: completedStatus  <br/>
     */
    public static final URI completedStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#completedStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType <br/>
     * Label: groupUserType  <br/>
     */
    public static final URI groupUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#groupUserType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType <br/>
     * Label: roomUserType  <br/>
     */
    public static final URI roomUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#roomUserType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType <br/>
     * Label: individualUserType  <br/>
     */
    public static final URI individualUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#individualUserType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType <br/>
     * Label: resourceUserType  <br/>
     */
    public static final URI resourceUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#resourceUserType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType <br/>
     * Label: unknownUserType  <br/>
     */
    public static final URI unknownUserType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#unknownUserType");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttachmentEncoding <br/>
     * Label: base64Encoding  <br/>
     */
    public static final URI base64Encoding = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#base64Encoding");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttachmentEncoding <br/>
     * Label: _8bitEncoding  <br/>
     */
    public static final URI _8bitEncoding = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#_8bitEncoding");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#EventStatus <br/>
     * Label: tentativeStatus  <br/>
     */
    public static final URI tentativeStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tentativeStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#EventStatus <br/>
     * Label: confirmedStatus  <br/>
     */
    public static final URI confirmedStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#confirmedStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#EventStatus <br/>
     * Label: cancelledEventStatus  <br/>
     */
    public static final URI cancelledEventStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#cancelledEventStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: friday  <br/>
     */
    public static final URI friday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#friday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: wednesday  <br/>
     */
    public static final URI wednesday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#wednesday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: thursday  <br/>
     */
    public static final URI thursday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#thursday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: saturday  <br/>
     */
    public static final URI saturday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#saturday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: sunday  <br/>
     */
    public static final URI sunday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#sunday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: tuesday  <br/>
     */
    public static final URI tuesday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tuesday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday <br/>
     * Label: monday  <br/>
     */
    public static final URI monday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#monday");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifierRange <br/>
     * Label: thisAndPriorRange  <br/>
     */
    public static final URI thisAndPriorRange = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#thisAndPriorRange");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifierRange <br/>
     * Label: thisAndFutureRange  <br/>
     */
    public static final URI thisAndFutureRange = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#thisAndFutureRange");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#JournalStatus <br/>
     * Label: cancelledJournalStatus  <br/>
     */
    public static final URI cancelledJournalStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#cancelledJournalStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#JournalStatus <br/>
     * Label: finalStatus  <br/>
     */
    public static final URI finalStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#finalStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#JournalStatus <br/>
     * Label: draftStatus  <br/>
     */
    public static final URI draftStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#draftStatus");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimeTransparency <br/>
     * Label: transparentTransparency  <br/>
     */
    public static final URI transparentTransparency = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#transparentTransparency");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimeTransparency <br/>
     * Label: opaqueTransparency  <br/>
     */
    public static final URI opaqueTransparency = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#opaqueTransparency");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole <br/>
     * Label: reqParticipantRole  <br/>
     */
    public static final URI reqParticipantRole = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#reqParticipantRole");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole <br/>
     * Label: nonParticipantRole  <br/>
     */
    public static final URI nonParticipantRole = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#nonParticipantRole");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole <br/>
     * Label: chairRole  <br/>
     */
    public static final URI chairRole = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#chairRole");
    /**
     * Type: Instance of http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole <br/>
     * Label: optParticipantRole  <br/>
     */
    public static final URI optParticipantRole = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#optParticipantRole");
    /**
     * Type: Property <br/>
     * Label: partstat  <br/>
     * Comment: To specify the participation status for the calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.12. Originally this parameter had three sets of allowed values. Which set applied to a particular case - depended on the type of calendar entity this parameter occured in. (event, todo, journal entry). This would be awkward to model in RDF so a single ParticipationStatus class has been introduced. Terms of the values vocabulary are expressed as instances of this class. Users are advised to pay attention which instances they use.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ParticipationStatus  <br/>
     */
    public static final URI partstat = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#partstat");
    /**
     * Type: Property <br/>
     * Label: rrule  <br/>
     * Comment: This property defines a rule or repeating pattern for recurring events, to-dos, or time zone definitions. sec. 4.8.5.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventJournalTimezoneTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     */
    public static final URI rrule = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#rrule");
    /**
     * Type: Property <br/>
     * Label: descriptionAltRep  <br/>
     * Comment: Alternate representation of the calendar entity description. Introduced to cover 
the ALTREP parameter of the DESCRIPTION property. See 
documentation of ncal:description for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI descriptionAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#descriptionAltRep");
    /**
     * Type: Property <br/>
     * Label: lastModified  <br/>
     * Comment: The property specifies the date and time that the information associated with the calendar component was last revised in the calendar store. Note: This is analogous to the modification date and time for a file in the file system. Inspired by RFC 2445 sec. 4.8.7.3. Note that the RFC allows ONLY UTC time values for this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTimezoneTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI lastModified = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#lastModified");
    /**
     * Type: Property <br/>
     * Label: due  <br/>
     * Comment: This property defines the date and time that a to-do is expected to be completed. Inspired by RFC 2445 sec. 4.8.2.3  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Todo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     */
    public static final URI due = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#due");
    /**
     * Type: Property <br/>
     * Label: standard  <br/>
     * Comment: Links the timezone with the standard timezone observance. This property has no direct equivalent in the RFC 2445. It has been inspired by the structure of the Vtimezone component defined in sec.4.6.5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance  <br/>
     */
    public static final URI standard = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#standard");
    /**
     * Type: Property <br/>
     * Label: recurrenceIdDateTime  <br/>
     * Comment: The date and time of a recurrence identifier. Provided to express the actual value of the ncal:recurrenceId property. See documentation for ncal:recurrenceId for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifier  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     */
    public static final URI recurrenceIdDateTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#recurrenceIdDateTime");
    /**
     * Type: Property <br/>
     * Label: bydayWeekday  <br/>
     * Comment: Connects a BydayRulePath with a weekday.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#BydayRulePart  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday  <br/>
     */
    public static final URI bydayWeekday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bydayWeekday");
    /**
     * Type: Property <br/>
     * Label: relatedToChild  <br/>
     * Comment: The property is used to represent a relationship or reference between one calendar component and another. Inspired by RFC 2445 sec. 4.8.4.5. Originally this property had a RELTYPE parameter. It has been decided to introduce three different properties to express the values of that parameter. This property expresses the RELATED-TO property with RELTYPE=CHILD parameter.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedToChild = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#relatedToChild");
    /**
     * Type: Property <br/>
     * Label: ncalRelation  <br/>
     * Comment: A common superproperty for all types of ncal relations. It is not to be used directly.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI ncalRelation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ncalRelation");
    /**
     * Type: Property <br/>
     * Label: attachmentContent  <br/>
     * Comment: The uri of the attachment. Created to express the actual value of the ATTACH property defined in RFC 2445 sec. 4.8.1.1. This property expresses the BINARY datatype of that property. see ncal:attachmentUri for the URI datatype.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI attachmentContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#attachmentContent");
    /**
     * Type: Property <br/>
     * Label: member  <br/>
     * Comment: To specify the group or list membership of the calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.11. Originally this parameter had a value type of CAL-ADDRESS. This has been expressed as nco:Contact to promote integration between NCAL and NCO  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI member = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#member");
    /**
     * Type: Property <br/>
     * Label: role  <br/>
     * Comment: To specify the participation role for the calendar user specified by the property. Inspired by the RFC 2445 sec. 4.2.16. Originally this property had a limited vocabulary for values. The terms of that vocabulary have been expressed as instances of the AttendeeRole class.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeRole  <br/>
     */
    public static final URI role = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#role");
    /**
     * Type: Property <br/>
     * Label: commentAltRep  <br/>
     * Comment: Alternate representation of the comment. Introduced to cover 
the ALTREP parameter of the COMMENT property. See 
documentation of ncal:comment for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI commentAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#commentAltRep");
    /**
     * Type: Property <br/>
     * Label: triggerDateTime  <br/>
     * Comment: The exact date and time of the trigger. This property has been created to express the VALUE=DATE, and VALUE=DATE-TIME parameters of the TRIGGER property. See the documentation for ncal:trigger for more details  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Trigger  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI triggerDateTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#triggerDateTime");
    /**
     * Type: Property <br/>
     * Label: contactAltRep  <br/>
     * Comment: Alternate representation of the contact property. Introduced to cover 
the ALTREP parameter of the CONTACT property. See 
documentation of ncal:contact for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI contactAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#contactAltRep");
    /**
     * Type: Property <br/>
     * Label: fbtype  <br/>
     * Comment: To specify the free or busy time type. Inspired by RFC 2445 sec. 4.2.9. The RFC specified a limited vocabulary for the values of this property. The terms of this vocabulary have been expressed as instances of the FreebusyType class. The user can use instances provided with this ontology or create his own.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyPeriod  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyType  <br/>
     */
    public static final URI fbtype = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#fbtype");
    /**
     * Type: Property <br/>
     * Label: dtstamp  <br/>
     * Comment: The property indicates the date/time that the instance of the iCalendar object was created. Inspired by RFC 2445 sec. 4.8.7.1. Note that the RFC allows ONLY UTC values for this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI dtstamp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#dtstamp");
    /**
     * Type: Property <br/>
     * Label: transp  <br/>
     * Comment: Defines whether an event is transparent or not  to busy time searches. Inspired by RFC 2445 sec.4.8.2.7. Values for this property can be chosen from a limited vocabulary. To express this a TimeTransparency class has been introduced.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Event  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimeTransparency  <br/>
     */
    public static final URI transp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#transp");
    /**
     * Type: Property <br/>
     * Label: geo  <br/>
     * Comment: This property specifies information related to the global position for the activity specified by a calendar component. Inspired by RFC 2445 sec. 4.8.1.6  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point  <br/>
     */
    public static final URI geo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#geo");
    /**
     * Type: Property <br/>
     * Label: wkst  <br/>
     * Comment: The day that's counted as the start of the week. It is used to disambiguate the byweekno rule. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Weekday  <br/>
     */
    public static final URI wkst = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#wkst");
    /**
     * Type: Property <br/>
     * Label: fmttype  <br/>
     * Comment: To specify the content type of a referenced object. Inspired by RFC 2445 sec. 4.2.8. The value of this property should be an IANA-registered content type (e.g. application/binary)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fmttype = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#fmttype");
    /**
     * Type: Property <br/>
     * Label: until  <br/>
     * Comment: The UNTIL rule part defines a date-time value which bounds the recurrence rule in an inclusive manner. If the value specified by UNTIL is synchronized with the specified recurrence, this date or date-time becomes the last instance of the recurrence. If specified as a date-time value, then it MUST be specified in an UTC time format. If not present, and the COUNT rule part is also not present, the RRULE is considered to repeat forever.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI until = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#until");
    /**
     * Type: Property <br/>
     * Label: freebusy  <br/>
     * Comment: The property defines one or more free or busy time intervals. Inspired by RFC 2445 sec. 4.8.2.6. Note that the periods specified by this property can only be expressed with UTC times. Originally this property could have many comma-separated values. Please use a separate triple for each value.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Freebusy  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#FreebusyPeriod  <br/>
     */
    public static final URI freebusy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#freebusy");
    /**
     * Type: Property <br/>
     * Label: version  <br/>
     * Comment: This property specifies the identifier corresponding to the highest version number or the minimum and maximum range of the iCalendar specification that is required in order to interpret the iCalendar object. Defined in RFC 2445 sec. 4.7.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI version = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#version");
    /**
     * Type: Property <br/>
     * Label: dtend  <br/>
     * Comment: This property specifies the date and time that a calendar component ends. Inspired by RFC 2445 sec. 4.8.2.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusy  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     */
    public static final URI dtend = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#dtend");
    /**
     * Type: Property <br/>
     * Label: recurrenceId  <br/>
     * Comment: This property is used in conjunction with the "UID" and "SEQUENCE" property to identify a specific instance of a recurring "VEVENT", "VTODO" or "VJOURNAL" calendar component. The property value is the effective value of the "DTSTART" property of the recurrence instance. Inspired by the RFC 2445 sec. 4.8.4.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTimezoneTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifier  <br/>
     */
    public static final URI recurrenceId = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#recurrenceId");
    /**
     * Type: Property <br/>
     * Label: summary  <br/>
     * Comment: Defines a short summary or subject for the calendar component. Inspired by RFC 2445 sec 4.8.1.12 with the following reservations: the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language. For the ALTREP parameter use the summaryAltRep property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI summary = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#summary");
    /**
     * Type: Property <br/>
     * Label: duration  <br/>
     * Comment: The property specifies a positive duration of time. Inspired by RFC 2445 sec. 4.8.2.5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventFreebusyTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#duration  <br/>
     */
    public static final URI duration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#duration");
    /**
     * Type: Property <br/>
     * Label: dateTime  <br/>
     * Comment: Representation of a date an instance of NcalDateTime actually refers to. It's purpose is to express values in DATE-TIME datatype, as defined in RFC 2445 sec. 4.3.5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI dateTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#dateTime");
    /**
     * Type: Property <br/>
     * Label: prodid  <br/>
     * Comment: This property specifies the identifier for the product that created the iCalendar object. Defined in RFC 2445 sec. 4.7.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI prodid = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#prodid");
    /**
     * Type: Property <br/>
     * Label: triggerDuration  <br/>
     * Comment: The duration of a trigger. This property has been created to express the VALUE=DURATION parameter of the TRIGGER property. See documentation for ncal:trigger for more details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Trigger  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#duration  <br/>
     */
    public static final URI triggerDuration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#triggerDuration");
    /**
     * Type: Property <br/>
     * Label: sentBy  <br/>
     * Comment: To specify the calendar user that is acting on behalf of the calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.18. The original data type of this property was a mailto: URI. This has been changed to nco:Contact to promote integration between NCO and NCAL.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeOrOrganizer  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI sentBy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#sentBy");
    /**
     * Type: Property <br/>
     * Label: byminute  <br/>
     * Comment: Minute of recurrence. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI byminute = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#byminute");
    /**
     * Type: Property <br/>
     * Label: returnStatus  <br/>
     * Comment: Short return status. Inspired by the first element of the structured value of the REQUEST-STATUS property described in RFC 2445 sec. 4.8.8.2.

The short return status is a PERIOD character (US-ASCII decimal 46) separated 3-tuple of integers. For example, "3.1.1". The successive  levels of integers provide for a successive level of status code granularity.

The following are initial classes for the return status code. Individual iCalendar object methods will define specific return status codes for these classes. In addition, other classes for the return status code may be defined using the registration process defined later in this memo.

 1.xx - Preliminary success. This class of status of status code indicates that the request has request has been initially processed but that completion is pending.

2.xx -Successful. This class of status code indicates that the request was completed successfuly. However, the exact status code can indicate that a fallback has been taken.

3.xx - Client Error. This class of status code indicates that the request was not successful. The error is the result of either a syntax or a semantic error in the client formatted request. Request should not be retried until the condition in the request is corrected.

4.xx - Scheduling Error. This class of status code indicates that the request was not successful. Some sort of error occurred within the  calendaring and scheduling service, not directly related to the request itself.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RequestStatus  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI returnStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#returnStatus");
    /**
     * Type: Property <br/>
     * Label: date  <br/>
     * Comment: Date an instance of NcalDateTime refers to. It was conceived to express values in DATE datatype specified in RFC 2445 4.3.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#date  <br/>
     */
    public static final URI date = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#date");
    /**
     * Type: Property <br/>
     * Label: rsvp  <br/>
     * Comment: To specify whether there is an expectation of a favor of a reply from the calendar user specified by the property value. Inspired by RFC 2445 sec. 4.2.17  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI rsvp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#rsvp");
    /**
     * Type: Property <br/>
     * Label: dir  <br/>
     * Comment: Specifies a reference to a directory entry associated with the calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.6. Originally the data type of the value of this parameter was URI (Usually an LDAP URI). This has been expressed as rdfs:resource.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeOrOrganizer  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI dir = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#dir");
    /**
     * Type: Property <br/>
     * Label: bymonthday  <br/>
     * Comment: Day of the month when the event should recur. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bymonthday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bymonthday");
    /**
     * Type: Property <br/>
     * Label: method  <br/>
     * Comment: This property defines the iCalendar object method associated with the calendar object. Defined in RFC 2445 sec. 4.7.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI method = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#method");
    /**
     * Type: Property <br/>
     * Label: calscale  <br/>
     * Comment: This property defines the calendar scale used for the calendar information specified in the iCalendar object. Defined in RFC 2445 sec. 4.7.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarScale  <br/>
     */
    public static final URI calscale = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#calscale");
    /**
     * Type: Property <br/>
     * Label: organizer  <br/>
     * Comment: The property defines the organizer for a calendar component. Inspired by RFC 2445 sec. 4.8.4.3. Originally this property accepted many parameters. The Organizer class has been introduced to express them all. Note that NCAL is aligned with NCO. The actual value (of the CAL-ADDRESS type) is expressed as an instance of nco:Contact. Remember that the CN parameter has been removed from NCAL. Instead that value should be expressed using nco:fullname property of the above mentioned nco:Contact instance.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Organizer  <br/>
     */
    public static final URI organizer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#organizer");
    /**
     * Type: Property <br/>
     * Label: count  <br/>
     * Comment: How many times should an event be repeated. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI count = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#count");
    /**
     * Type: Property <br/>
     * Label: contact  <br/>
     * Comment: The property is used to represent contact information or alternately a reference to contact information associated with the calendar component. Inspired by RFC 2445 sec. 4.8.4.2 with the following reservations: the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language. For the ALTREP parameter use the contactAltRep property.RFC doesn't define any format for the string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#contact");
    /**
     * Type: Property <br/>
     * Label: bysetpos  <br/>
     * Comment: The BYSETPOS rule part specify values which correspond to the nth occurrence within the set of events specified by the rule. Valid values are 1 to 366 or -366 to -1. It MUST only be used in conjunction with another BYxxx rule part. For example "the last work day of the month" could be represented as: RRULE: FREQ=MONTHLY; BYDAY=MO, TU, WE, TH, FR; BYSETPOS=-1. Each BYSETPOS value can include a positive (+n) or negative (-n)  integer. If present, this indicates the nth occurrence of the  specific occurrence within the set of events specified by the rule. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bysetpos = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bysetpos");
    /**
     * Type: Property <br/>
     * Label: status  <br/>
     * Comment: Defines the overall status or confirmation for an Event. Based on the STATUS property defined in RFC 2445 sec. 4.8.1.11.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Event  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#EventStatus  <br/>
     */
    public static final URI eventStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#eventStatus");
    /**
     * Type: Property <br/>
     * Label: repeat  <br/>
     * Comment: This property defines the number of time the alarm should be repeated, after the initial trigger. Inspired by RFC 2445 sec. 4.8.6.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Alarm  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI repeat = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#repeat");
    /**
     * Type: Property <br/>
     * Label: periodEnd  <br/>
     * Comment: End of a period of time. Inspired by the second part of a structured value of a PERIOD datatype specified in RFC 2445 sec. 4.3.9. Note that a single NcalPeriod instance shouldn't have the periodEnd and periodDuration properties specified simultaneously.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalPeriod  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI periodEnd = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#periodEnd");
    /**
     * Type: Property <br/>
     * Label: uid  <br/>
     * Comment: This property defines the persistent, globally unique identifier for the calendar component. Inspired by the RFC 2445 sec 4.8.4.7  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI uid = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#uid");
    /**
     * Type: Property <br/>
     * Label: hasAlarm  <br/>
     * Comment: Links an event or a todo with a DataObject that can be interpreted as an alarm. This property has no direct equivalent in the RFC 2445. It has been provided to express this relation.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarDataObject  <br/>
     */
    public static final URI hasAlarm = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#hasAlarm");
    /**
     * Type: Property <br/>
     * Label: tzurl  <br/>
     * Comment: The TZURL provides a means for a VTIMEZONE component to point to a network location that can be used to retrieve an up-to- date version of itself. Inspired by RFC 2445 sec. 4.8.3.5. Originally the range of this property had been specified as URI.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI tzurl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tzurl");
    /**
     * Type: Property <br/>
     * Label: cutype  <br/>
     * Comment: To specify the type of calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.3. This parameter has a limited vocabulary. The terms that may serve as values for this property have been expressed as instances of CalendarUserType class. The user may use instances provided with this ontology or create his own.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarUserType  <br/>
     */
    public static final URI cutype = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#cutype");
    /**
     * Type: Property <br/>
     * Label: trigger  <br/>
     * Comment: This property specifies when an alarm will trigger. Inspired by RFC 2445 sec. 4.8.6.3 Originally the value of this property could accept two types : duration and date-time. To express this fact a Trigger class has been introduced. It also has a related property to account for the RELATED parameter.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Trigger  <br/>
     */
    public static final URI trigger = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#trigger");
    /**
     * Type: Property <br/>
     * Label: interval  <br/>
     * Comment: The INTERVAL rule part contains a positive integer representing how often the recurrence rule repeats. The default value is "1", meaning every second for a SECONDLY rule, or every minute for a MINUTELY rule, every hour for an HOURLY rule, every day for a DAILY rule, every week for a WEEKLY rule, every month for a MONTHLY rule andevery year for a YEARLY rule. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI interval = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#interval");
    /**
     * Type: Property <br/>
     * Label: sequence  <br/>
     * Comment: This property defines the revision sequence number of the calendar component within a sequence of revisions. Inspired by RFC 2445 sec. 4.8.7.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI sequence = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#sequence");
    /**
     * Type: Property <br/>
     * Label: rdate  <br/>
     * Comment: This property defines the list of date/times for a recurrence set. Inspired by RFC 2445 sec. 4.8.5.3. Note that RFC allows both DATE, DATE-TIME and PERIOD values for this property. That's why the range has been set to NcalTimeEntity.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventJournalTimezoneTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalTimeEntity  <br/>
     */
    public static final URI rdate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#rdate");
    /**
     * Type: Property <br/>
     * Label: bymonth  <br/>
     * Comment: Number of the month of the recurrence. Valid values are integers from 1 (January) to 12 (December). Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bymonth = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bymonth");
    /**
     * Type: Property <br/>
     * Label: involvedContact  <br/>
     * Comment: A contact of the Attendee or the organizer involved in an event or other calendar entity. This property has been introduced to express the actual value of the ATTENDEE and ORGANIZER properties. The contact will also represent the CN parameter of those properties. See documentation of ncal:attendee or ncal:organizer for more details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttendeeOrOrganizer  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI involvedContact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#involvedContact");
    /**
     * Type: Property <br/>
     * Label: bysecond  <br/>
     * Comment: Second of a recurrence. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bysecond = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bysecond");
    /**
     * Type: Property <br/>
     * Label: attachmentUri  <br/>
     * Comment: The uri of the attachment. Created to express the actual value of the ATTACH property defined in RFC 2445 sec. 4.8.1.1. This property expresses the default URI datatype of that property. see ncal:attachmentContents for the BINARY datatype.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI attachmentUri = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#attachmentUri");
    /**
     * Type: Property <br/>
     * Label: completed  <br/>
     * Comment: This property defines the date and time that a to-do was actually completed. Inspired by RFC 2445 sec. 4.8.2.1. Note that the RFC allows ONLY UTC time values for this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Todo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI completed = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#completed");
    /**
     * Type: Property <br/>
     * Label: locationAltRep  <br/>
     * Comment: Alternate representation of the event or todo location. 
Introduced to cover the ALTREP parameter of the LOCATION 
property. See documentation of ncal:location for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI locationAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#locationAltRep");
    /**
     * Type: Property <br/>
     * Label: status  <br/>
     * Comment: Defines the overall status or confirmation for a todo. Based on the STATUS property defined in RFC 2445 sec. 4.8.1.11.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Todo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TodoStatus  <br/>
     */
    public static final URI todoStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#todoStatus");
    /**
     * Type: Property <br/>
     * Label: exrule  <br/>
     * Comment: This property defines a rule or repeating pattern for an exception to a recurrence set. Inspired by RFC 2445 sec. 4.8.5.2.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     */
    public static final URI exrule = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#exrule");
    /**
     * Type: Property <br/>
     * Label: byweekno  <br/>
     * Comment: The number of the week an event should recur. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI byweekno = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#byweekno");
    /**
     * Type: Property <br/>
     * Label: component  <br/>
     * Comment: Links the Vcalendar instance with the calendar components. This property has no direct equivalent in the RFC specification. It has been introduced to express the containmnent relations.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Calendar  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#CalendarDataObject  <br/>
     */
    public static final URI component = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#component");
    /**
     * Type: Property <br/>
     * Label: priority  <br/>
     * Comment: The property defines the relative priority for a calendar component. Inspired by RFC 2445 sec. 4.8.1.9  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI priority = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#priority");
    /**
     * Type: Property <br/>
     * Label: tzid  <br/>
     * Comment: This property specifies the text value that uniquely identifies the "VTIMEZONE" calendar component. Inspired by RFC 2445 sec 4.8.3.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tzid = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tzid");
    /**
     * Type: Property <br/>
     * Label: attendee  <br/>
     * Comment: The property defines an "Attendee" within a calendar component. Inspired by RFC 2445 sec. 4.8.4.1. Originally this property accepted many parameters. The Attendee class has been introduced to express them all. Note that NCAL is aligned with NCO. The actual value (of the CAL-ADDRESS type) is expressed as an instance of nco:Contact. Remember that the CN parameter has been removed from NCAL. Instead that value should be expressed using nco:fullname property of the above mentioned nco:Contact instance. The RFC stated that whenever this property is attached to a Valarm instance, the Attendee cannot have any parameters apart from involvedContact.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventFreebusyJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     */
    public static final URI attendee = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#attendee");
    /**
     * Type: Property <br/>
     * Label: resources  <br/>
     * Comment: Defines the equipment or resources anticipated for an activity specified by a calendar entity. Inspired by RFC 2445 sec. 4.8.1.10 with the following reservations:  the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language. For the ALTREP parameter use the resourcesAltRep property. This property specifies multiple resources. The order is not important. it is recommended to introduce a separate triple for each resource.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI resources = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#resources");
    /**
     * Type: Property <br/>
     * Label: periodBegin  <br/>
     * Comment: Beginng of a period. Inspired by the first part of a structured value of the PERIOD datatype specified in RFC 2445 sec. 4.3.9  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalPeriod  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI periodBegin = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#periodBegin");
    /**
     * Type: Property <br/>
     * Label: ncalTimezone  <br/>
     * Comment: The timezone instance that should be used to interpret an NcalDateTime. The purpose of this property is similar to the TZID parameter specified in RFC 2445 sec. 4.2.19  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone  <br/>
     */
    public static final URI ncalTimezone = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#ncalTimezone");
    /**
     * Type: Property <br/>
     * Label: freq  <br/>
     * Comment: Frequency of a recurrence rule. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceFrequency  <br/>
     */
    public static final URI freq = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#freq");
    /**
     * Type: Property <br/>
     * Label: statusDescription  <br/>
     * Comment: Longer return status description. Inspired by the second part of the structured value of the REQUEST-STATUS property defined in RFC 2445 sec. 4.8.8.2  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RequestStatus  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI statusDescription = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#statusDescription");
    /**
     * Type: Property <br/>
     * Label: tzoffsetto  <br/>
     * Comment: This property specifies the offset which is in use in this time zone observance. nspired by RFC 2445 sec. 4.8.3.4. The original domain was underspecified. It said that this property must appear within a Timezone component. In this ontology a TimezoneObservance class has been introduced to clarify this specification. The original range was UTC-OFFSET. There is no equivalent among the XSD datatypes so plain string was chosen.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tzoffsetto = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tzoffsetto");
    /**
     * Type: Property <br/>
     * Label: description  <br/>
     * Comment: A more complete description of the calendar component, than  that provided by the ncal:summary property.Inspired by RFC 2445 sec. 4.8.1.5 with following reservations:  the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language. For the ALTREP parameter use the descriptionAltRep property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI description = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#description");
    /**
     * Type: Property <br/>
     * Label: percentComplete  <br/>
     * Comment: This property is used by an assignee or delegatee of a to-do to convey the percent completion of a to-do to the Organizer. Inspired by RFC 2445 sec. 4.8.1.8  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Todo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI percentComplete = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#percentComplete");
    /**
     * Type: Property <br/>
     * Label: bydayModifier  <br/>
     * Comment: A n integer modifier for the BYDAY rule part.    Each BYDAY value can also be preceded by a positive (+n) or negative  (-n) integer. If present, this indicates the nth occurrence of the specific day within the MONTHLY or YEARLY RRULE. For example, within a MONTHLY rule, +1MO (or simply 1MO) represents the first Monday within the month, whereas -1MO represents the last Monday of the month. If an integer modifier is not present, it means all days of this type within the specified frequency. For example, within a MONTHLY rule, MO represents all Mondays within the month. Inspired by RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#BydayRulePart  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI bydayModifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#bydayModifier");
    /**
     * Type: Property <br/>
     * Label: relatedToParent  <br/>
     * Comment: The property is used to represent a relationship or reference between one calendar component and another. Inspired by RFC 2445 sec. 4.8.4.5. Originally this property had a RELTYPE parameter. It has been decided that it is more natural to introduce three different properties to express the values of that parameter. This property expresses the RELATED-TO property with no RELTYPE parameter (the default value is PARENT), or with explicit RELTYPE=PARENT parameter.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedToParent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#relatedToParent");
    /**
     * Type: Property <br/>
     * Label: byyearday  <br/>
     * Comment: Day of the year the event should occur. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI byyearday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#byyearday");
    /**
     * Type: Property <br/>
     * Label: action  <br/>
     * Comment: This property defines the action to be invoked when an alarm is triggered. Inspired by RFC 2445 sec 4.8.6.1. Originally this property had a limited set of values. They are expressed as instances of the AlarmAction class.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Alarm  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AlarmAction  <br/>
     */
    public static final URI action = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#action");
    /**
     * Type: Property <br/>
     * Label: status  <br/>
     * Comment: Defines the overall status or confirmation for a journal entry. Based on the STATUS property defined in RFC 2445 sec. 4.8.1.11.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Journal  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#JournalStatus  <br/>
     */
    public static final URI journalStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#journalStatus");
    /**
     * Type: Property <br/>
     * Label: tzname  <br/>
     * Comment: Specifies the customary designation for a timezone description. Inspired by RFC 2445 sec. 4.8.3.2 The LANGUAGE parameter has been discarded. Please xml:lang literals to express languages. Original specification for the domain of this property stated that it must appear within the timezone component. In this ontology the TimezoneObservance class has been itroduced to clarify this specification.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tzname = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tzname");
    /**
     * Type: Property <br/>
     * Label: location  <br/>
     * Comment: Defines the intended venue for the activity defined by a calendar component. Inspired by RFC 2445 sec 4.8.1.7 with the following reservations:  the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language.  For the ALTREP parameter use the locationAltRep property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI location = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#location");
    /**
     * Type: Property <br/>
     * Label: tzoffsetfrom  <br/>
     * Comment: This property specifies the offset which is in use prior to this time zone observance. Inspired by RFC 2445 sec. 4.8.3.3. The original domain was underspecified. It said that this property must appear within a Timezone component. In this ontology a TimezoneObservance class has been introduced to clarify this specification. The original range was UTC-OFFSET. There is no equivalent among the XSD datatypes so plain string was chosen.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tzoffsetfrom = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#tzoffsetfrom");
    /**
     * Type: Property <br/>
     * Label: related  <br/>
     * Comment: To specify the relationship of the alarm trigger with respect to the start or end of the calendar component. Inspired by RFC 2445 4.2.14. The RFC has specified two possible values for this property ('START' and 'END') they have been expressed as instances of the TriggerRelation class.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Trigger  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TriggerRelation  <br/>
     */
    public static final URI related = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#related");
    /**
     * Type: Property <br/>
     * Label: range  <br/>
     * Comment: To specify the effective range of recurrence instances from the instance specified by the recurrence identifier specified by the property. It is intended to express the RANGE parameter specified in RFC 2445 sec. 4.2.13. The set of possible values for this property is limited. See also the documentation for ncal:recurrenceId for more details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifier  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceIdentifierRange  <br/>
     */
    public static final URI range = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#range");
    /**
     * Type: Property <br/>
     * Label: url  <br/>
     * Comment: This property defines a Uniform Resource Locator (URL) associated with the iCalendar object. Inspired by the RFC 2445 sec. 4.8.4.6. Original range had been specified as URI.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI url = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#url");
    /**
     * Type: Property <br/>
     * Label: byhour  <br/>
     * Comment: Hour of recurrence. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI byhour = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#byhour");
    /**
     * Type: Property <br/>
     * Label: encoding  <br/>
     * Comment: To specify an alternate inline encoding for the property value. Inspired by RFC 2445 sec. 4.2.7. Originally this property had a limited vocabulary. ('8BIT' and 'BASE64'). The terms of this vocabulary have been expressed as instances of the AttachmentEncoding class  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AttachmentEncoding  <br/>
     */
    public static final URI encoding = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#encoding");
    /**
     * Type: Property <br/>
     * Label: byday  <br/>
     * Comment: Weekdays the recurrence should occur. Defined in RFC 2445 sec. 4.3.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RecurrenceRule  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#BydayRulePart  <br/>
     */
    public static final URI byday = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#byday");
    /**
     * Type: Property <br/>
     * Label: comment  <br/>
     * Comment: Non-processing information intended to provide a comment to the calendar user. Inspired by RFC 2445 sec. 4.8.1.4 with the following reservations:  the LANGUAGE parameter has been discarded. Please use xml:lang literals to express language. For the ALTREP parameter use the commentAltRep property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventFreebusyJournalTimezoneTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI comment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#comment");
    /**
     * Type: Property <br/>
     * Label: created  <br/>
     * Comment: This property specifies the date and time that the calendar information was created by the calendar user agent in the calendar store. Note: This is analogous to the creation date and time for a file in the file system. Inspired by RFC 2445 sec. 4.8.7.1. Note that this property is a subproperty of nie:created. The domain of nie:created is nie:DataObject. It is not a superclass of UnionOf_Vevent_Vjournal_Vtodo, but since that union is conceived as an 'abstract' class, and in real-life all resources referenced by this property will also be DataObjects, than this shouldn't cause too much of a problem. Note that RFC allows ONLY UTC time values for this property.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI created = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#created");
    /**
     * Type: Property <br/>
     * Label: relatedToSibling  <br/>
     * Comment: The property is used to represent a relationship or reference between one calendar component and another. Inspired by RFC 2445 sec. 4.8.4.5. Originally this property had a RELTYPE parameter. It has been decided that it is more natural to introduce three different properties to express the values of that parameter. This property expresses the RELATED-TO property with RELTYPE=SIBLING parameter.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedToSibling = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#relatedToSibling");
    /**
     * Type: Property <br/>
     * Label: exdate  <br/>
     * Comment: This property defines the list of date/time exceptions for a recurring calendar component. Inspired by RFC 2445 sec. 4.8.5.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTimezoneTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     */
    public static final URI exdate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#exdate");
    /**
     * Type: Property <br/>
     * Label: categories  <br/>
     * Comment: Categories for a calendar component. Inspired by RFC 2445 sec 4.8.1.2 with the following reservations: The LANGUAGE parameter has been discarded. Please use xml:lang literals to express multiple languages. This property can specify multiple comma-separated categories. The order of categories doesn't matter. Please use a separate triple for each category.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI categories = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#categories");
    /**
     * Type: Property <br/>
     * Label: summaryAltRep  <br/>
     * Comment: Alternate representation of the comment. Introduced to cover 
the ALTREP parameter of the SUMMARY property. See 
documentation of ncal:summary for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI summaryAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#summaryAltRep");
    /**
     * Type: Property <br/>
     * Label: periodDuration  <br/>
     * Comment: Duration of a period of time. Inspired by the second part of a structured value of the PERIOD datatype specified in RFC 2445 sec. 4.3.9. Note that a single NcalPeriod instance shouldn't have the periodEnd and periodDuration properties specified simultaneously.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalPeriod  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#duration  <br/>
     */
    public static final URI periodDuration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#periodDuration");
    /**
     * Type: Property <br/>
     * Label: requestStatus  <br/>
     * Comment: This property defines the status code returned for a scheduling request. Inspired by RFC 2445 sec. 4.8.8.2. Original value of this property was a four-element structure. The RequestStatus class has been introduced to express it. In RFC 2445 this property could have the LANGUAGE parameter. This has been discarded in this ontology. Use xml:lang literals to express it if necessary.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventFreebusyJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RequestStatus  <br/>
     */
    public static final URI requestStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#requestStatus");
    /**
     * Type: Property <br/>
     * Label: class  <br/>
     * Comment: Defines the access classification for a calendar component. Inspired by RFC 2445 sec. 4.8.1.3 with the following reservations:  this property has limited vocabulary. Possible values are:  PUBLIC, PRIVATE and CONFIDENTIAL. The default is PUBLIC. Those values are expressed as instances of the AccessClassification class. The user may create his/her own if necessary.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#AccessClassification  <br/>
     */
    public static final URI class_ = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#class");
    /**
     * Type: Property <br/>
     * Label: dtstart  <br/>
     * Comment: This property specifies when the calendar component begins. Inspired by RFC 2445 sec. 4.8.2.4  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfTimezoneObservanceEventFreebusyTimezoneTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#NcalDateTime  <br/>
     */
    public static final URI dtstart = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#dtstart");
    /**
     * Type: Property <br/>
     * Label: requestStatusData  <br/>
     * Comment: Additional data associated with a request status. Inspired by the third part of the structured value for the REQUEST-STATUS property defined in RFC 2445 sec. 4.8.8.2 ("Textual exception data. For example, the offending property name and value or complete property line")  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#RequestStatus  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI requestStatusData = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#requestStatusData");
    /**
     * Type: Property <br/>
     * Label: delegatedFrom  <br/>
     * Comment: To specify the calendar users that have delegated their participation to the calendar user specified by the property. Inspired by RFC 2445 sec. 4.2.4. Originally the value type for this property was CAL-ADDRESS. This has been expressed as nco:Contact to promote integration between NCAL and NCO.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI delegatedFrom = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#delegatedFrom");
    /**
     * Type: Property <br/>
     * Label: delegatedTo  <br/>
     * Comment: To specify the calendar users to whom the calendar user specified by the property has delegated participation. Inspired by RFC 2445 sec. 4.2.5. Originally the value type for this parameter was CAL-ADDRESS. This has been expressed as nco:Contact to promote integration between NCAL and NCO.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attendee  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI delegatedTo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#delegatedTo");
    /**
     * Type: Property <br/>
     * Label: daylight  <br/>
     * Comment: Links a timezone with it's daylight observance. This property has no direct equivalent in the RFC 2445. It has been inspired by the structure of the Vtimezone component defined in sec.4.6.5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Timezone  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#TimezoneObservance  <br/>
     */
    public static final URI daylight = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#daylight");
    /**
     * Type: Property <br/>
     * Label: attach  <br/>
     * Comment: The property provides the capability to associate a document object with a calendar component. Defined in the RFC 2445 sec. 4.8.1.1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfAlarmEventJournalTodo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#Attachment  <br/>
     */
    public static final URI attach = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#attach");
    /**
     * Type: Property <br/>
     * Label: resourcesAltRep  <br/>
     * Comment: Alternate representation of the resources needed for an event or todo. Introduced to cover the ALTREP parameter of the resources property. See documentation for ncal:resources for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#UnionOfEventTodo  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI resourcesAltRep = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#resourcesAltRep");
}
