/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Range;
import net.fortuna.ical4j.model.parameter.RelType;
import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Crawler implementation for crawling ical files. The file to be crawled is configured with an instance of
 * IcalDataSource. See the documentation of appropriate methods for details on the form of the generated RDF
 * data. Note that the n3 examples in the documentation contain blank nodes for clarity purposes, the actual
 * RDF data will use randomly generated resources created with the
 * {@link UriUtil#generateRandomResource(Model)} method, which may or may not be blank nodes. See the
 * documentation for {@link UriUtil#generateRandomResource(Model)} for more details.
 */
public class IcalCrawler extends CrawlerBase {

    /**
     * URI of the xsd:yearMonthDuration datatype. This constant will be removed when an appropriate constant
     * appears in the XSD class from RDF2Go.
     * 
     * @deprecated
     */
    public static final URI XSD_YEAR_MONTH_DURATION = new URIImpl(XSD.XSD_NS + "yearMonthDuration");

    /**
     * URI of the xsd:dayTimeDuration datatype. This constant will be removed when an appropriate constant
     * appears in the XSD class from RDF2Go.
     * 
     * @deprecated
     */
    public static final URI XSD_DAY_TIME_DURATION = new URIImpl(XSD.XSD_NS + "dayTimeDuration");
    
    /** Logger */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /** 
     * The base URI of current calendar instance. URIs of all calendar components are derived from this
     * one.
     * @see #createBaseUri(File)
     */
    private String baseuri;

    /** Default constructor. */
    public IcalCrawler() {
        // empty... for the time being.
    }

    /**
     * The main method that performs the actual crawl. Reads the file path from the data source configuration.
     * 
     * @return The ExitCode
     */
    protected ExitCode crawlObjects() {
        IcalDataSource icalDataSource = null;
        try {
            icalDataSource = (IcalDataSource) source;
        }
        catch (ClassCastException e) {
            logger.error("unsupported data source type", e);
            return ExitCode.FATAL_ERROR;
        }

        // determine the root file
        String icalFilePath = icalDataSource.getRootUrl();
        if (icalFilePath == null) {
            // treat this as an error rather than an "empty source" to prevent
            // information loss
            logger.warn("missing iCalendar file path specification");
            return ExitCode.FATAL_ERROR;
        }

        File icalFile = new File(icalFilePath);
        if (!icalFile.exists()) {
            logger.warn("iCalendar file does not exist: '" + icalFile + "'");
            return ExitCode.FATAL_ERROR;
        }

        if (!icalFile.canRead()) {
            logger.warn("iCalendar file cannot be read: '" + icalFile + "'");
            return ExitCode.FATAL_ERROR;
        }

        try {
            baseuri = createBaseUri(icalFile);
        }
        catch (IOException e) {
            logger.error("Couldn't get the canonical path " + "for the iCalFile", e);
            return ExitCode.FATAL_ERROR;
        }

        // crawl the ical file
        return crawlIcalFile(icalFile);
    }

    /**
     * Creates the base URI from the ical file.
     * @param icalFile the file with the ical information
     * @return the string with the base uri
     * @throws IOException if a canonical path cannot be generated for this file
     */
    private String createBaseUri(File icalFile) throws IOException 
    {
        //String result = "file:///";
        //result += icalFile.getCanonicalPath();
        //result += "#";
        //result = result.replaceAll("\\\\", "/");
        //result = result.replaceAll(" ", "%20");
        //return result;
        return icalFile.getCanonicalFile().toURI() + "#";
    }

    /**
     * Crawls the ical file. Parses the file, creates the calendar object and proceeds to crawl it.
     * 
     * @param icalFile The file to be crawled.
     * 
     * @return The exit code. Possible values are:
     *         <ul>
     *         <li> ExitCode.FATAL_ERROR - some error occured (see the logs for details)
     *         <li> ExitCode.COMPLETED - crawl successfully completed.
     *         </ul>
     */

    private ExitCode crawlIcalFile(File icalFile) {
        FileReader fin = null;
        CalendarBuilder builder = null;
        Calendar calendar = null;
        try {
            // This is necessary to support files where content lines are split between multiple
            // physical lines
            System.setProperty("ical4j.unfolding.relaxed", "true");

            // This is necessary to support files generated by Lotus Notes
            // They contain uris with '<' and '>' that have to be removed
            // before conversion to java.net.URI
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);

            fin = new FileReader(icalFile);
            builder = new CalendarBuilder();
            calendar = builder.build(fin);
            crawlCalendar(calendar);
            return ExitCode.COMPLETED;
        }
        catch (FileNotFoundException fnfe) {
            logger.warn("Couldn't find the calendar file", fnfe);
            return ExitCode.FATAL_ERROR;
        }
        catch (ParserException pe) {
            logger.warn("Couldn't parse the calendar file", pe);
            return ExitCode.FATAL_ERROR;
        }
        catch (IOException ioe) {
            logger.warn("Input/Output error while parsing " + "the calendar file", ioe);
            return ExitCode.FATAL_ERROR;
        }
        finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    // we can't do anything...
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// ABSTRACT BUSINESS METHODS ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Crawls the calendar.
     * 
     * Generates a DataObject for the Calendar itself and passes it to the crawlerHandler. Then continues to
     * crawl the entire component list.
     * 
     * @param calendar The calendar object.
     */
    private void crawlCalendar(Calendar calendar) {
        URI uri = generateCalendarUri();
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(uri);
        rdfContainer.add(RDF.type, NCAL.Calendar);

        PropertyList propertyList = calendar.getProperties();
        crawlPropertyList(propertyList, rdfContainer.getDescribedUri(), rdfContainer, null);

        rdfContainer.add(NIE.rootElementOf,getDataSource().getID());
        
        passComponentToHandler(rdfContainer, null);

        ComponentList componentList = calendar.getComponents();
        crawlComponentList(componentList, rdfContainer);
    }

    /**
     * Crawls a single calendar component. Checks the name of the component and dispatches it to the proper
     * component-handling method.
     * 
     * @param component The component to crawl.
     * @param parentNode The root node of the generated subtree.
     * @param rdfContainer The container where the resulting triples should be stored. Applicable to embedded
     *            components i.e. those that don't create a new DataObject. The "main" components ignore this
     *            parameter and create their own RDFContainers.
     */
    private void crawlSingleComponent(Component component, Resource parentNode, RDFContainer rdfContainer) throws ModelException {
        if (component.getName().equals(Component.VALARM)) {
            crawlAlarmComponent(component, parentNode, rdfContainer);
        }
        else if (component.getName().equals(Component.VEVENT)) {
            crawlEventComponent(component, parentNode);
        }
        else if (component.getName().equals(Component.VFREEBUSY)) {
            crawlFreebusyComponent(component, parentNode);
        }
        else if (component.getName().equals(Component.VJOURNAL)) {
            crawlJournalComponent(component, parentNode);
        }
        else if (component.getName().equals(Component.VTIMEZONE)) {
            crawlTimezoneComponent(component, parentNode);
        }
        else if (component.getName().equals(Component.VTODO)) {
            crawlTodoComponent(component, parentNode);
        }
        else if (component.getName().equals(Observance.STANDARD)) {
            crawlStandardObservance(component, parentNode, rdfContainer);
        }
        else if (component.getName().equals(Observance.DAYLIGHT)) {
            crawlDaylightObservance(component, parentNode, rdfContainer);
        }
        else if (component.getName().startsWith(Component.EXPERIMENTAL_PREFIX)) {
            crawlExperimentalComponent(component, parentNode);
        }
        else {
            logger.warn("Unknown component name: " + component.getName());
        }
    }

    /**
     * Crawls a single property. Checks the name of the property and dispatches it to an appropriate
     * property-handling method.
     * 
     * @param property The property to be crawled.
     * @param rdfContainer The rdfContainer to store the generated statements in.
     * @param component the calendar component the property belongs to. This parameter is relevant only for
     *            the STATUS property which has different sets of possible values depending on the component
     *            it is attached to, as such it is modelled with three different RDF properties. Other ICAL
     *            properties are modelled with a single RDF property regardless of the component they are
     *            attached to.
     */
    private void crawlSingleProperty(Property property, Resource parentNode, RDFContainer rdfContainer, Component component) throws ModelException {
        String propertyName = property.getName();

        if (propertyName.equals(Property.ACTION)) {
            crawlActionProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.ATTACH)) {
            crawlAttachProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.ATTENDEE)) {
            crawlAttendeeProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.CALSCALE)) {
            crawlCalScaleProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.CATEGORIES)) {
            crawlCategoriesProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.CLASS)) {
            crawlClassProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.COMMENT)) {
            crawlCommentProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.COMPLETED)) {
            crawlCompletedProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.CONTACT)) {
            crawlContactProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.CREATED)) {
            crawlCreatedProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DESCRIPTION)) {
            crawlDescriptionProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DTEND)) {
            crawlDtEndProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DTSTAMP)) {
            crawlDtStampProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DTSTART)) {
            crawlDtStartProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DUE)) {
            crawlDueProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.DURATION)) {
            crawlDurationProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.EXDATE)) {
            crawlExDateProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.EXRULE)) {
            crawlExRuleProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.FREEBUSY)) {
            crawlFreeBusyProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.GEO)) {
            crawlGeoProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.LAST_MODIFIED)) {
            crawlLastModifiedProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.LOCATION)) {
            crawlLocationProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.METHOD)) {
            crawlMethodProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.ORGANIZER)) {
            crawlOrganizerProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.PERCENT_COMPLETE)) {
            crawlPercentCompleteProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.PRIORITY)) {
            crawlPriorityProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.PRODID)) {
            crawlProdIdProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.RDATE)) {
            crawlRDateProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.RECURRENCE_ID)) {
            crawlRecurrenceIdProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.RELATED_TO)) {
            crawlRelatedToProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.REPEAT)) {
            crawlRepeatProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.REQUEST_STATUS)) {
            crawlRequestStatusProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.RESOURCES)) {
            crawlResourcesProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.RRULE)) {
            crawlRRuleProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.SEQUENCE)) {
            crawlSequenceProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.STATUS)) {
            crawlStatusProperty(property, parentNode, rdfContainer, component);
        }
        else if (propertyName.startsWith(Property.SUMMARY)) {
            crawlSummaryProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TRANSP)) {
            crawlTranspProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TRIGGER)) {
            crawlTriggerProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TZID)) {
            crawlTzidProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TZNAME)) {
            crawlTzNameProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TZOFFSETFROM)) {
            crawlTzOffsetFromProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TZOFFSETTO)) {
            crawlTzOffsetToProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.TZURL)) {
            crawlTzUrlProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.UID)) {
            crawlUidProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.URL)) {
            crawlUrlProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.VERSION)) {
            crawlVersionProperty(property, parentNode, rdfContainer);
        }
        else if (propertyName.startsWith(Property.EXPERIMENTAL_PREFIX)) {
            crawlXtendedProperty(property, parentNode, rdfContainer);
        }
        else {
            logger.warn("Unknown property name: " + property.getName());
        }
    }

    /**
     * Crawls a single parameter. Checks the name of the parameter and dispatches it to an appropriate
     * parameter-handling method.
     * 
     * @param property The parameter to be crawled.
     * @param rdfContainer The rdfContainer to store the generated statements in.
     */
    private void crawlSingleParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String parameterName = parameter.getName();
        if (parameterName.equals(Parameter.ALTREP)) {
            crawlAltRepParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.CN)) {
            crawlCnParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.CUTYPE)) {
            crawlCuTypeParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.DELEGATED_FROM)) {
            crawlDelegatedFromParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.DELEGATED_TO)) {
            crawlDelegatedToParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.DIR)) {
            crawlDirParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.ENCODING)) {
            crawlEncodingParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.FBTYPE)) {
            crawlFbTypeParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.FMTTYPE)) {
            crawlFmtTypeParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.LANGUAGE)) {
            crawlLanguageParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.MEMBER)) {
            crawlMemberParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.PARTSTAT)) {
            crawlPartStatParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.RANGE)) {
            crawlRangeParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.RELATED)) {
            crawlRelatedParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.RELTYPE)) {
            crawlRelTypeParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.ROLE)) {
            crawlRoleParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.RSVP)) {
            crawlRsvpParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.SENT_BY)) {
            crawlSentByParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.TZID)) {
            crawlTzidParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.equals(Parameter.VALUE)) {
            crawlValueParameter(parameter, parentNode, rdfContainer);
        }
        else if (parameterName.startsWith(Parameter.EXPERIMENTAL_PREFIX)) {
            crawlXParameter(parameter, parentNode, rdfContainer);
        }
        else {
            logger.warn("Unknown parameter name: '" + parameterName + "'");
        }
    }

    /**
     * Crawls the component list.
     * 
     * The components are attached to the root URI of the given container.
     * 
     * @param componentList The component list to crawl.
     * @param rdfContainer The container to store the resulting triples.
     */
    @SuppressWarnings("unchecked")
    private void crawlComponentList(ComponentList componentList, RDFContainer rdfContainer) {
        Iterator it = componentList.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            try {
                crawlSingleComponent(component, rdfContainer.getDescribedUri(), rdfContainer);
            }
            catch (ModelException e) {
                logger.warn("ModelException while processing single component, skipping component", e);
            }
        }
    }

    /**
     * Iterates over the properties of a given component and adds those properties to a given container. The
     * properties are attached to the root URI of the container.
     * 
     * @param component The component, whose properties are to be crawled.
     * @param rdfContainer The rdfContainer to store the generated statements in.
     */
    private void crawlPropertyList(Component component, RDFContainer rdfContainer) {
        crawlPropertyList(component, rdfContainer.getDescribedUri(), rdfContainer);
    }

    /**
     * Iterates over the properties of a given component, and adds those properties to a given RDFContainer.
     * The properties are attached to a given parent node.
     * 
     * @param component The component, whose properties are to be crawled.
     * @param parentNode The node, the property values should be attached to.
     * @param rdfContainer The container to store the generated statements in.
     */
    private void crawlPropertyList(Component component, Resource parentNode, RDFContainer rdfContainer) {
        PropertyList propertyList = component.getProperties();
        crawlPropertyList(propertyList, parentNode, rdfContainer, component);
    }

    /**
     * Iterates over a propertyList, attaches those properties to a given parentNode, add stores the resulting
     * triples in a given RDFContainer.
     * 
     * @param propertyList The property list to be crawled.
     * @param parentNode The node, the property values should be attached to.
     * @param rdfContainer The container to store the generated statements in.
     * @param component the calendar component the properties belong to (or null in case of of properties that
     *            apply directly to the VCALENDAR object)
     */
    @SuppressWarnings("unchecked")
    private void crawlPropertyList(PropertyList propertyList, Resource parentNode, RDFContainer rdfContainer, Component component) {
        Iterator it = propertyList.iterator();
        while (it.hasNext()) {
            Property property = (Property) it.next();
            
            try {
                crawlSingleProperty(property, parentNode, rdfContainer, component);
            }
            catch (ModelException e) {
                logger.warn("ModelException while handling single property, skipping property", e);
            }
        }
    }

    /**
     * Crawls the parameter list of a given property.
     * 
     * @param property The property whose parameter list we would like to crawl.
     * @param rdfContainer The container to store the generated statements in.
     */
    private Resource crawlParameterList(Property property, RDFContainer rdfContainer) {
        Resource propertyBlankNode = generateAnonymousNode(rdfContainer);
        ParameterList parameterList = property.getParameters();
        crawlParameterList(parameterList, propertyBlankNode, rdfContainer);
        return propertyBlankNode;
    }

    /**
     * Crawls the given parameter list.
     * 
     * @param parameterList The list of parameters to be crawled.
     * @param parentNode The node, the property values should be attached to.
     * @param rdfContainer The container to store the generated statements in.
     */
    @SuppressWarnings("unchecked")
    private void crawlParameterList(ParameterList parameterList, Resource parentNode,
            RDFContainer rdfContainer) {
        Iterator it = parameterList.iterator();
        while (it.hasNext()) {
            Parameter parameter = (Parameter) it.next();
            crawlSingleParameter(parameter, parentNode, rdfContainer);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////// SPECIFIC BUSINESS METHODS //////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////// COMPONENTS //////////////////////////////////////////////

    /**
     * Crawls a single VAlarm component. Attaches it to the parent vevent or vtodo with a ncal:hasAlarm link.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VALARM
     *  TRIGGER;RELATED=START:-PT30M
     *  ACTION:DISPLAY
     *  DESCRIPTION:Federal Reserve Board Meeting
     *  END:VALARM
     *  
     *  n3:
     *  _:EventNode  ncal:hasAlarm [ 
     *        a ncal:Alarm ;
     *        ncal:action ncal:displayAction ;
     *        ncal:description "Federal Reserve Board Meeting" ;
     *        ncal:trigger [
     *            a ncal:Trigger ;
     *            ncal:related ncal:startTriggerRelation ;
     *            ncal:triggerDuration "-PT30M"^^xsd:duration 
     *        ] 
     * ].
     * </pre>
     * 
     * @param parentNode the node of the parent component, the hasAlarm link will be attached to this node
     * @param component The Valarm to be crawled.
     * @param rdfContainer The container to store the generated statements in.
     */
    public void crawlAlarmComponent(Component component, Resource parentNode, RDFContainer rdfContainer) {
        VAlarm valarm = (VAlarm) component;
        URI valarmParentNode = generateAnonymousComponentUri(component);
        crawlPropertyList(valarm, valarmParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.hasAlarm, valarmParentNode);
        addStatement(rdfContainer, valarmParentNode, RDF.type, NCAL.Alarm);
        addStatement(rdfContainer, valarmParentNode, RDF.type, NCAL.CalendarDataObject);
    }

    /**
     * Crawls the vevent component. Attaches it to the given parent node with an ncal:component link.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VEVENT
     *  UID:20020630T230353Z-3895-69-1-0@jammer
     *  DTSTAMP:20020630T230353Z
     *  DTSTART;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *   20020630T090000
     *  DTEND;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *   20020630T103000  
     *  TRANSP:OPAQUE
     *  SEQUENCE:2
     *  SUMMARY:Church
     *  CLASS:PRIVATE
     *  RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=SU
     *  END:VEVENT
     *  
     *  n3:
     *  _:eventNode a ncal:Event ;
     *      ncal:uid &quot;20020630T230353Z-3895-69-1-0@jammer&quot; ;
     *      ncal:dtstamp &quot;2002-06-30T23:03:53Z&quot;&circ;&circ;xsd:dateTime ;
     *      ncal:dtstart [
     *          a ncal:NcalDateTime ;
     *          ncal:dateTime &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *          ncal:ncalTimezone &lt;file://path/to/file.ics/America/New_York&gt; 
     *      ] ;
     *      ncal:dtend [
     *          a ncal:NcalDateTime ;
     *          ncal:dateTime &quot;2002-06-30T10:30:00&quot;&circ;&circ;xsd:dateTime ;
     *          ncal:ncalTimezone &lt;file://path/to/file.ics/America/New_York&gt;
     *      ] ;
     *      ncal:transp ncal:OpaqueTransparency ;
     *      ncal:sequence &quot;2&quot;&circ;&circ;xsd:integer ;
     *      ncal:summary &quot;Church&quot; ;
     *      ncal:class ncal:PrivateClassification ;
     *      ncal:rrule [
     *          a ncal:RecurrenceRule ;
     *          ncal:freq ncal:weekly ;
     *          ncal:interval &quot;1&quot;&circ;&circ;xsd:integer ;
     *          ncal:byday [
     *              a ncal:BydayRulePart ;
     *              ncal:bydayWeekday ncal:Sunday
     *          ]
     *      ] .
     * </pre>
     * 
     * @param parentNode the node of the parent component, the ncal:component link will be attached to this
     *            node
     * @param component the component to be crawled
     */
    public void crawlEventComponent(Component component, Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.type, NCAL.Event);
        VEvent vevent = (VEvent) component;
        crawlPropertyList(vevent, rdfContainer);
        ComponentList alarmList = vevent.getAlarms();
        crawlComponentList(alarmList, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.component, rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer, component);
    }

    /**
     * Crawls a single VFreebusy component.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VFREEBUSY
     *  ORGANIZER:MAILTO:jane_doe@host1.com
     *  ATTENDEE:MAILTO:john_public@host2.com
     *  DTSTAMP:19970901T100000Z
     *  FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
     *   19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
     *  URL:http://host2.com/pub/busy/jpublic-01.ifb
     *  COMMENT:This iCalendar file contains busy time information for
     *   the next three months.
     *  END:VFREEBUSY
     *  
     *  n3:
     *  _:freebusyNode a ncal:Freebusy ;
     *      ncal:organizer [
     *          a ncal:Organizer ;
     *          ncal:involvedContact [
     *              a nco:PersonContact ;
     *              nco:hasEmailAddress [
     *                  a nco:EmailAddress ;
     *                  nco:emailAddress "jane_doe@host1.com"
     *              ]
     *          ]
     *      ] ;
     *      ncal:attendee [
     *          a ncal:Attendee ;
     *          ncal:involvedContact [
     *              a nco:PersonContact ;
     *              nco:hasEmailAddress [
     *                  a nco:EmailAddress ;
     *                  nco:emailAddress "john_public@host2.com"
     *              ]
     *          ]
     *      ];
     *      ncal:dtstamp "1997-09-01T10:00:00Z"^^xsd:dateTime;
     *      ncal:freebusy [
     *          a ncal:FreebusyPeriod ;
     *          ncal:periodBegin "1997-10-15T05:00:00Z"^^xsd:dateTime ;
     *          ncal:periodDuration "PT8H30M"^^xsd:dayTimeDuration 
     *      ] ;
     *      ncal:freebusy [
     *          a ncal:FreebusyPeriod ;
     *          ncal:periodBegin "1997-10-15T16:00:00Z"^^xsd:dateTime ;
     *          ncal:periodDuration "PT5H30M"^^xsd:dayTimeDuration 
     *      ] ;
     *      ncal:freebusy [
     *          a ncal:FreebusyPeriod ;
     *          ncal:periodBegin "1997-10-15T23:00:00Z"^^xsd:dateTime ;
     *          ncal:periodDuration "PT6H30M"^^xsd:dayTimeDuration 
     *      ] ;
     *      ncal:url <http://host2.com/pub/busy/jpublic-01.ifb> ;
     *      ncal:comment """This iCalendar file contains busy time information for the next three months.""" .
     * &lt;http://host2.com/pub/busy/jpublic-01.ifb&gt; a rdfs:Resource
     * 
     * </pre>
     * 
     * @param parentNode the node of the parent component, the ncal:component link will be attached to this
     *            node
     * @param component the component to be crawled
     * 
     * @see #generateComponentUri(Component)
     */
    public void crawlFreebusyComponent(Component component, Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        VFreeBusy vfreebusy = (VFreeBusy) component;
        rdfContainer.add(RDF.type, NCAL.Freebusy);
        crawlPropertyList(vfreebusy, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.component, rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer, component);
    }

    /**
     * Crawls a single VJournal component.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VJOURNAL
     *  CREATED:20030227T110715Z
     *  UID:KOrganizer-948365006.348
     *  SEQUENCE:0
     *  LAST-MODIFIED:20030227T110715Z
     *  DTSTAMP:20030227T110715Z
     *  ORGANIZER:MAILTO:nobody@nowhere
     *  DESCRIPTION:journal\n
     *  CLASS:PUBLIC
     *  PRIORITY:3
     *  DTSTART;VALUE=DATE:20030224
     *  END:VJOURNAL
     *  
     *  n3:
     *  _:journalNode a ncal:Journal ;
     *      ncal:created "2003-02-27T11:07:15Z"^^xsd:dateTime ;
     *      ncal:uid "KOrganizer-948365006.348" ;
     *      ncal:sequence "0"^^xsd:integer ;
     *      ncal:lastModified "2003-02-27T11:07:15Z"^^xsd:dateTime ; 
     *      ncal:dtstamp "2003-02-27T11:07:15Z"^^xsd:dateTime ;
     *      ncal:organizer [
     *          a ncal:Organizer ;
     *          ncal:involvedContact [
     *              a nco:PersonContact ;
     *              nco:hasEmailAddress [
     *                  a nco:EmailAddress ;
     *                  nco:emailAddress "nobody@nowhere"
     *              ]
     *          ]
     *      ] ;
     *      ncal:description "journal" ;
     *      ncal:class ncal:PublicClassification ;
     *      ncal:priority "3"^^xsd:integer ;
     *      ncal:dtstart [
     *          a ncal:NcalDateTime ;
     *          ncal:date "2003-02-24"^^xsd:date
     *      ] .
     * </pre>
     * 
     * @param parentNode the node of the parent component, the ncal:component link will be attached to this
     *            node
     * @param component the component to be crawled
     * 
     * @see #generateComponentUri(Component)
     */
    public void crawlJournalComponent(Component component, Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.type, NCAL.Journal);
        VJournal vjournal = (VJournal) component;
        crawlPropertyList(vjournal, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.component, rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer, component);
    }

    /**
     * Crawls a single VTimezone component.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VTIMEZONE
     *  TZID:/softwarestudio.org/Olson_20011030_5/America/New_York
     *  TZURL:http://timezones.r.us.net/tz/US-California-Los_Angeles
     *  BEGIN:STANDARD
     *  TZOFFSETFROM:-0400
     *  TZOFFSETTO:-0500
     *  TZNAME:EST
     *  DTSTART:19701025T020000
     *  RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10
     *  END:STANDARD
     *  BEGIN:DAYLIGHT
     *  TZOFFSETFROM:-0500
     *  TZOFFSETTO:-0400
     *  TZNAME:EDT
     *  DTSTART:19700405T020000
     *  RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=1SU;BYMONTH=4
     *  END:DAYLIGHT
     *  END:VTIMEZONE
     *  
     *  n3:
     *  _:timezoneNode a ncal:Timezone ;
     *      ncal:tzid "/softwarestudio.org/Olson_20011030_5/America/New_York" ;
     *      ncal:tzurl <http://timezones.r.us.net/tz/US-California-Los_Angeles> ;
     *      ncal:standard [
     *          a ncal:TimezoneObservance ;
     *          ncal:tzoffsetfrom "-0400" ;
     *          ncal:tzoffsetto "-0500" ;
     *          ncal:tzname "EST" ;
     *          ncal:dtstart [
     *              a ncal:NcalDateTime ;
     *              ncal:dateTime "1970-10-25T02:00:00"^^xsd:dateTime 
     *          ] ;
     *          ncal:rrule [
     *              a ncal:RecurrenceRule ;
     *              ncal:freq ncal:yearly ;
     *              ncal:interval "1"^^xsd:integer ;
     *              ncal:byday [
     *                  a ncal:BydayRulePart ;
     *                  ncal:bydayWeekday ncal:Sunday ;
     *                  ncal:bydayModifier "-1"^^xsd:integer
     *              ];
     *              ncal:bymonth "10"^^xsd:integer
     *          ]
     *      ] ;
     *      ncal:daylight [
     *          a ncal:TimezoneObservance ;
     *          ncal:tzoffsetfrom "-0500" ;
     *          ncal:tzoffsetto "-0400" ;
     *          ncal:tzname "EDT" ;
     *          ncal:dtstart [
     *              a ncal:NcalDateTime ;
     *              ncal:dateTime "1970-04-05T02:00:00"^^xsd:dateTime 
     *          ] ;
     *          ncal:rrule [
     *              a ncal:RecurrenceRule ;
     *              ncal:freq ncal:yearly ;
     *              ncal:interval "1"^^xsd:integer ;
     *              ncal:byday [
     *                  a ncal:BydayRulePart ;
     *                  ncal:bydayWeekday ncal:Sunday ;
     *                  ncal:bydayModifier "1"^^xsd:integer
     *              ];
     *              ncal:bymonth "4"^^xsd:integer
     *          ]
     *      ] .
     *      
     * &lt;http://timezones.r.us.net/tz/US-California-Los_Angeles&gt; a rdfs:Resource .      
     * </pre>
     * 
     * @param parentNode the node of the parent component, the ncal:component link will be attached to this
     *            node
     * @param component the component to be crawled
     * 
     * @see #generateComponentUri(Component)
     */
    public void crawlTimezoneComponent(Component component, Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.type, NCAL.Timezone);
        VTimeZone vtimezone = (VTimeZone) component;
        crawlPropertyList(vtimezone, rdfContainer);
        ComponentList observances = vtimezone.getObservances();
        crawlComponentList(observances, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.component, rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer, component);
    }

    /**
     * Crawls a single VTodo component.
     * 
     * <pre>
     *  ical:
     *  BEGIN:VTODO
     *  PRIORITY:1
     *  DTSTAMP:20040130T152344Z
     *  UID:7611710A-5338-11D8-A876-000A958826AA
     *  SEQUENCE:3
     *  URL;VALUE=URI:http://www.w3.org/2004/01/ideas/
     *  STATUS:COMPLETED
     *  DTSTART;TZID=Europe/Rome:20031217T133610
     *  SUMMARY:project page
     *  COMPLETED:20040129T230000Z
     *  DUE:20031216T000000Z
     *  END:VTODO
     *  
     *  n3:
     *  _:vtodoNode a ncal:Todo ;
     *      ncal:priority "1"^^xsd:integer ;
     *      ncal:dtstamp "2004-01-30T15:23:44Z"^^xsd:dateTime ;
     *      ncal:uid "7611710A-5338-11D8-A876-000A958826AA" ;
     *      ncal:sequence "3"^^xsd:integer ;
     *      ncal:url &lt;http://www.w3.org/2004/01/ideas/&gt; ;
     *      ncal:status ncal:completedStatus ;
     *      ncal:dtstart [
     *          a ncal:NcalDateTime ;
     *          ncal:dateTime "2003-12-17T13:36:10"^^xsd:dateTime ;
     *          ncal:ncalTimezone &lt;http://path/to/file.ics/Europe/Rome&gt;
     *      ];
     *      ncal:summary "project page" ;
     *      ncal:completed "2004-01-29T23:00:00Z"^^xsd:dateTime ;
     *      ncal:due "2003-12-16T00-00-00Z"^^xsd:dateTime .
     *      
     * &lt;http://www.w3.org/2004/01/ideas/&gt; a rdfs:Resource .
     * </pre>
     * 
     * @param parentNode the node of the parent component, the ncal:component link will be attached to this
     *            node
     * @param component the component to be crawled
     * 
     * @see #generateComponentUri(Component)
     */
    public void crawlTodoComponent(Component component, Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.type, NCAL.Todo);
        VToDo vtodo = (VToDo) component;
        crawlPropertyList(vtodo, rdfContainer);
        ComponentList alarmList = vtodo.getAlarms();
        crawlComponentList(alarmList, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.component, rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer, component);
    }

    /** 
     * Experimental components are unsupported at the moment.
     * @param parentNode 
     * @param component  
     */
    public void crawlExperimentalComponent(Component component, Resource parentNode) {
    // RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
    // rdfContainer.add(RDF.type, extendedNameSpace + component.getName());
    // crawlPropertyList(component, rdfContainer);
    // addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
    // passComponentToHandler(rdfContainer);
    }

    /**
     * Crawls a single Standard timezone observance component.
     * 
     * @see #crawlVTimezoneComponent(Component, Resource)
     * @param parentNode the node of the parent component, the ncal:standard link will be attached to this
     *            node
     * @param component the component to be crawled
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlStandardObservance(Component component, Resource parentNode, RDFContainer rdfContainer)  {
        Resource standardParentNode = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, standardParentNode, RDF.type, NCAL.TimezoneObservance);
        crawlPropertyList(component, standardParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.standard, standardParentNode);
    }

    /**
     * Crawls a single daylight timezone observance component.
     * 
     * @param parentNode the node of the parent component, the ncal:daylight link will be attached to this
     *            node
     * @param component the component to be crawled
     * @param rdfContainer the container to store the generated statements in
     * @see #crawlVTimezoneComponent(Component, Resource)
     */
    public void crawlDaylightObservance(Component component, Resource parentNode, RDFContainer rdfContainer) {
        Resource daylightParentNode = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, daylightParentNode, RDF.type, NCAL.TimezoneObservance);
        crawlPropertyList(component, daylightParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.daylight, daylightParentNode);
    }

    // ///////////////////////////////////////// PROPERTIES ///////////////////////////////////////////////

    /**
     * Crawls the ACTION property.<br>
     * 
     * <pre>
     *   ical:
     *   ACTION:AUDIO
     *   
     *   n3:
     *   _:ValarmNode ncal:action ncal:AudioAction .
     *   
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlActionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        String value = property.getValue();
        if (value.equals(Action.AUDIO.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.action, NCAL.audioAction);
        } else if (value.equals(Action.DISPLAY.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.action, NCAL.displayAction);
        } else if (value.equals(Action.EMAIL.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.action, NCAL.emailAction);
        } else if (value.equals(Action.PROCEDURE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.action, NCAL.procedureAction);
        } else {
            logger.warn("Unknown action property value: " + value);
        }
    }

    /**
     * Crawls the ATTACH property.<br>
     * 
     * <pre>
     * ical:
     * ATTACH;VALUE=URI:http://www.w3.org/index.html
     *   
     * n3:
     * _:VeventNode ncal:attach [
     *    a ncal:Attachment ;
     *    ncal:attachmentUri &lt;http://www.w3.org/index.html&gt;
     * ] .
     * &lt;http://www.w3.org/index.html&gt; a rdfs:Resource .
     *   
     * ical:
     * ATTACH;FMTYPE=IMAGE/JPEG;ENCODING=BASE64;VALUE=BINARY:MIICajC
     *  CAdOgAwIBAgICBEUwDQYJKoZIhvcNAQEEBQAwdzELMAkGA1UEBhMCVVMxLDA
     *  qBgNVBAoTI05ldHNjYXBlIENvbW11bmljYXRpb25zIENvcnBvcmF0aW9uMRw
     *  &lt;...remainder of "BASE64" encoded binary data...&gt;
     *  
     * n3:
     * _:veventNode ncal:attach [
     *      a ncal:Attachment ;
     *      ncal:fmttype "image/jpeg" ;
     *      ncal:encoding ncal:base64Encoding ;
     *      # The attachment content is usually not expressed in the RDF.
     *      # It is converted to a separate DataObject and can be processed with 
     *      # with a MimeTypeIdentifier and an extractor.
     *      # The same node can be interpreted as an appropriate subclass of 
     *      # nie:InformationElement and be annotated all kinds of other properties
     *      # characteristic to that particular kind of InformationElement.
     * ] .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in 
     * 
     */
    public void crawlAttachProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        URI attachmentURI = generateAttachmentUri(rdfContainer.getDescribedUri());        
        Parameter valueParameter = property.getParameter(Parameter.VALUE);
        String propertyValue = property.getValue();
        addStatement(rdfContainer, parentNode, NCAL.attach, attachmentURI);
        if (valueParameter == null || valueParameter.equals(Value.URI)) {
            URI uri = tryToCreateAnUri(rdfContainer,propertyValue);
            addStatement(rdfContainer, attachmentURI, RDF.type, NCAL.Attachment);
            addStatement(rdfContainer, attachmentURI, NCAL.attachmentUri, uri);
            addStatement(rdfContainer, uri, RDF.type, RDFS.Resource);
        }
        else if (valueParameter.equals(Value.BINARY)) {
            RDFContainer attachmentContainer = prepareDataObjectRDFContainer(attachmentURI);
            addStatement(attachmentContainer, attachmentURI, RDF.type, NCAL.Attachment);
            crawlParameterList(property, attachmentContainer);
            Attach attach = (Attach) property;
            passAttachmentToHandler(attachmentContainer, attach.getBinary());
        }
    }

    /**
     * Crawls the ATTENDEE property.<br>
     * 
     * <pre>
     *   ical:
     *   ATTENDEE;CN=John Smith;RSVP=TRUE;ROLE=REQ-PARTICIPANT:MAILTO:jsmith@host.com
     *   
     *   n3:
     *   _:eventNode ncal:attendee [
     *      a ncal:Attendee ;
     *      ncal:involvedContact [
     *          a nco:PersonContact ;
     *          nco:fullname "John Smith" ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jsmith@host.com"
     *          ]
     *      ]
     *      ncal:rsvp "true" ;
     *      ncal:role ncal:reqParticipantRole 
     *   ] .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlAttendeeProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource blankNode = crawlParameterList(property, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.attendee, blankNode);
        processAttendeeOrOrganizer(property, blankNode, rdfContainer);
        addStatement(rdfContainer, blankNode, RDF.type, NCAL.Attendee);
    }

    /**
     * Crawls the CALSCALE property.<br>
     * 
     * <pre>
     *   ical:
     *   CALSCALE:GREGORIAN
     *   
     *   n3:
     *   _:CalendarNode ncal:calscale ncal:GregorianCalendarScale .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCalScaleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        String value = property.getValue();
        if (value.equals(CalScale.GREGORIAN.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.calscale, NCAL.gregorianCalendarScale);
        }
    }

    /**
     * Crawls the CATEGORIES property.<br>
     * 
     * <pre>
     *   ical:
     *   CATEGORIES:APPOINTMENT,EDUCATION
     *   
     *   n3:
     *   _:VeventNode ncal:categories "APPOINTMENT,EDUCATION" .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCategoriesProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.categories, property.getValue());
    }

    /**
     * Crawls the CLASS property.<br>
     * 
     * <pre>
     *   ical:
     *   CLASS:PUBLIC
     *   
     *   n3:
     *   _:VcalendarNode ncal:class ncal:PublicClassification .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlClassProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        String value = property.getValue();
        if (value.equals(Clazz.CONFIDENTIAL.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.class_, NCAL.confidentialClassification);
        } else if (value.equals(Clazz.PRIVATE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.class_, NCAL.privateClassification);
        } else if (value.equals(Clazz.PUBLIC.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.class_, NCAL.publicClassification);
        } else { 
            logger.warn("Unknown CLASS property value: " + value);
        }
    }

    /**
     * Crawls the COMMENT property.<br>
     * 
     * <pre>
     *   ical:
     *   COMMENT:The meeting really needs to include both ourselves
     *     and the customer. We can't hold this  meeting without them.
     *     As a matter of fact\, the venue for the meeting ought to be at
     *     their site. - - John
     *   
     *   n3:
     *   _:VeventNode ncal:comment 
     *     &quot;&quot;&quot;The meeting really needs to include both ourselves
     *     and the customer. We can't hold this  meeting without them.
     *     As a matter of fact\, the venue for the meeting ought to be at
     *     their site. - - John&quot;&quot;&quot; . 
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCommentProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.comment, property.getValue());
    }

    /**
     * Crawls the COMPLETED property.<br>
     * 
     * <pre>
     *   ical:
     *   COMPLETED:19971210T080000Z
     *   
     *   n3:
     *   _:VTodoNode ncal:completed &quot;1997-12-10T08:00:00Z&quot;&circ;&circ;xsd:dateTime .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCompletedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.completed, propertyValue);
    }

    /**
     * Crawls the CONTACT property.<br>
     * 
     * <pre>
     *   ical:
     *   CONTACT:Jim Dolittle\, ABC Industries\, +1-919-555-1234
     *   
     *   n3:
     *   _:VeventNode ncal:contact 
     *     &quot;&quot;&quot;Jim Dolittle\, ABC Industries\, +1-919-555-1234&quot;&quot;&quot; . 
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlContactProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.contact, property.getValue());
    }

    /**
     * Crawls the CREATED property.<br>
     * 
     * Note the conversion to the XSD time format
     * 
     * <pre>
     *   ical:
     *   CREATED:19971210T080000
     *   
     *   n3:
     *   _:VeventNode icaltzd:created &quot;1997-12-10T08:00:00&quot;&circ;&circ;xsd:dateTime .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCreatedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.created, propertyValue);
    }

    /**
     * Crawls the DESCRIPTION property.<br>
     * 
     * <pre>
     *   ical:
     *   DESCRIPTION:Meeting to provide technical review for &quot;Phoenix&quot;
     *    design.\n Happy Face Conference Room. Phoenix design team
     *    MUST attend this meeting.\n RSVP to team leader.
     *   
     *   n3:
     *   _:VeventNode ncal:description 
     *     &quot;&quot;&quot;Meeting to provide technical review for &quot;Phoenix&quot;
     *    design.\n Happy Face Conference Room. Phoenix design team
     *    MUST attend this meeting.\n RSVP to team leader.&quot;&quot;&quot; . 
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDescriptionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.description, property.getValue());
    }

    /**
     * Crawls the DTEND property.<br>
     * 
     * The dates and date-times are converted to xmlschema form. 
     * 
     * <pre>
     *    ical:
     *    DTEND:19980118T073000Z
     *    
     *    n3:
     *    _:VeventNode ncal:dtend [
     *         a ncal:NcalDateTime ;
     *         ncal:dateTime &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;xsd:dateTime 
     *    ] .
     *    
     *    ical:
     *    DTEND:VALUE=DATE;20020703
     *    
     *    n3:
     *    _:VeventNode ncal:dtend [
     *         a ncal:NcalDateTime ;
     *         ncal:date &quot;2002-07-03&quot;&circ;&circ;xsd:date 
     *    ] .
     *    
     *    ical:
     *    DTEND;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *     20020630T090000
     *     
     *    n3:
     *    _:VeventNode ncal:dtend [
     *         &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *         ncal:ncalTimezone 
     *           &lt;this-calendar-baseuri/softwarestudio.org/Olson_20011030_5/America/New_York&gt;
     *    ] .
     *    </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDtEndProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.dtend, propertyValue);
    }

    /**
     * Crawls the DTSTAMP property.<br>
     * 
     * <pre>
     *   ical:
     *   DTSTAMP:19971210T080000Z
     *   
     *   n3:
     *   _:VeventNode ncal:dtstamp &quot;1997-12-10T08:00:00Z&quot;&circ;&circ;xsd:dateTime .
     * </pre>
     * 
     * Note the conversion to the XSD time format
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDtStampProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.dtstamp, propertyValue);
    }

    
    /**
     * Crawls the DTSTART property.<br>
     * 
     * The dates and date-times are converted to xmlschema form. 
     * 
     * <pre>
     *    ical:
     *    DTSTART:19980118T073000Z
     *    
     *    n3:
     *    _:VeventNode ncal:dtstart [
     *         a ncal:NcalDateTime ;
     *         ncal:dateTime &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;xsd:dateTime 
     *    ] .
     *    
     *    ical:
     *    DTSTART:VALUE=DATE;20020703
     *    
     *    n3:
     *    _:VeventNode ncal:dtend [
     *         a ncal:NcalDateTime ;
     *         ncal:date &quot;2002-07-03&quot;&circ;&circ;xsd:date 
     *    ] .
     *    
     *    ical:
     *    DTSTART;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *     20020630T090000
     *     
     *    n3:
     *    _:VeventNode ncal:dtend [
     *         &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *         ncal:ncalTimezone 
     *           &lt;this-calendar-baseuri/softwarestudio.org/Olson_20011030_5/America/New_York&gt;
     *    ] .
     *    </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDtStartProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.dtstart, propertyValue);
    }

    /**
     * Crawls the DTSTART property.<br>
     * 
     * The dates and date-times are converted to xmlschema form. 
     * 
     * <pre>
     *    ical:
     *    DTSTART:19980118T073000Z
     *    
     *    n3:
     *    _:VeventNode ncal:dtstart [
     *         a ncal:NcalDateTime ;
     *         ncal:dateTime &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;xsd:dateTime 
     *    ] .
     *    
     *    ical:
     *    DTSTART:VALUE=DATE;20020703
     *    
     *    n3:
     *    _:VeventNode ncal:dtstart [
     *         a ncal:NcalDateTime ;
     *         ncal:date &quot;2002-07-03&quot;&circ;&circ;xsd:date 
     *    ] .
     *    
     *    ical:
     *    DTSTART;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *     20020630T090000
     *     
     *    n3:
     *    _:VeventNode ncal:dtstart [
     *         &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *         ncal:ncalTimezone 
     *           &lt;this-calendar-baseuri/softwarestudio.org/Olson_20011030_5/America/New_York&gt;
     *    ] .
     *    </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDueProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.due, propertyValue);
    }

    /**
     * Crawls the DURATION property. <br>
     * 
     * <pre>
     *   ical:
     *   DURATION:PT1H0M0S
     *   
     *   n3:
     *   _:VeventNode ncal:duration &quot;PT1H0M0S&quot;&circ;&circ;xsd:duration .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDurationProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DURATION);
        addStatement(rdfContainer, parentNode, NCAL.duration, propertyValue);
    }

    /**
     * Crawls the EXDATE property.<br>
     * 
     * The dates and date-times are converted to xmlschema form. 
     * 
     * <pre>
     *    ical:
     *    EXDATE:19980118T073000Z
     *    
     *    n3:
     *    _:VeventNode ncal:exdate [
     *         a ncal:NcalDateTime ;
     *         ncal:dateTime &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;xsd:dateTime 
     *    ] .
     *    
     *    ical:
     *    EXDATE:VALUE=DATE;20020703
     *    
     *    n3:
     *    _:VeventNode ncal:exdate [
     *         a ncal:NcalDateTime ;
     *         ncal:date &quot;2002-07-03&quot;&circ;&circ;xsd:date 
     *    ] .
     *    
     *    ical:
     *    EXDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *     20020630T090000
     *     
     *    n3:
     *    _:VeventNode ncal:exdate [
     *         &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *         ncal:ncalTimezone 
     *           &lt;this-calendar-baseuri/softwarestudio.org/Olson_20011030_5/America/New_York&gt;
     *    ] .
     *    </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlExDateProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        List<Node> valueList = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addMultipleStatements(rdfContainer, parentNode, NCAL.exdate, valueList);
    }

    /**
     * Crawls the EXRULE property. <br> 

     * <p>
     * This property has RECUR value type. This necessitates an introduction of an intermediate node.
     * The recurrence parameters are attached to this intermediate node (as literals with appropriate
     * datatypes)
     * 
     * 
     * <pre>
     *   ical:
     *   EXRULE:FREQ=YEARLY;INTERVAL=5;BYDAY=-1SU;BYMONTH=10
     *   
     *   n3:
     *   _:VeventNode ncal:exrule [
     *         a ncal:RecurrenceRule ;
     *         ncal:freq ncal:yearly ;
     *         ncal:interval "5"^^xsd:integer ;
     *         ncal:byday [
     *             a ncal:BydayRulePart ;
     *             ncal:bydayWeekday ncal:sunday ;
     *             ncal:bydayModifier "-1"^^xsd:integer 
     *         ] ;
     *         ncal:bymonth "10"^^xsd:integer ;
     *  ].
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     * @see #crawlRecur(String, Resource, RDFContainer)
     */
    public void crawlExRuleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource rruleBlankNode = generateAnonymousNode(rdfContainer);
        crawlRecur(property.getValue(), rruleBlankNode, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.exrule, rruleBlankNode);
    }

    /**
     * Crawls the FREEBUSY property. <br>
     * 
     * <p>
     * Note that this property supports multiple values.
     * 
     * 
     * <pre>
     *   ical:
     *   FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
     *    19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
     *   
     *   n3:
     *   _:VFreebusyComponentNode ncal:freebusy [
     *         a ncal:FreebusyPeriod ;
     *         ncal:periodBegin "1997-10-15T05:00:00Z"^^xsd:dateTime;
     *         ncal:periodDuration "PT8H30M"^^xsd:dayTimeDuration
     *   ] .
     *   _:VFreebusyComponentNode ncal:freebusy [
     *         a ncal:FreebusyPeriod ;
     *         ncal:periodBegin "1997-10-15T16:00:00Z"^^xsd:dateTime;
     *         ncal:periodDuration "PT5H30M"^^xsd:dayTimeDuration
     *   ] .
     *   _:VFreebusyComponentNode ncal:freebusy [
     *         a ncal:FreebusyPeriod ;
     *         ncal:periodBegin "1997-10-15T22:30:00Z"^^xsd:dateTime;
     *         ncal:periodDuration "PT6H30M"^^xsd:dayTimeDuration
     *   ] .
     *   
     *   
     *   ical:
     *   FREEBUSY;FBTYPE=BUSY:19980415T133000Z/19980415T170000Z
     *   
     *   n3:
     *   _:VFreebusyComponentNode ncal:freebusy [
     *         a ncal:FreebusyPeriod ;
     *         ncal:fbtype ncal:busyFreebusyType ;
     *         ncal:periodBegin "1998-04-15T13:30:00Z"^^xsd:dateTime;
     *         ncal:periodEnd "1998-04-15T17:00:00Z"^^xsd:dateTime
     *   ] .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlFreeBusyProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        List<Node> valueList = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.PERIOD);
        for (Node node :valueList) {
            Resource resource = node.asResource();
            addStatement(rdfContainer,parentNode, NCAL.freebusy,resource);
            addStatement(rdfContainer,resource,RDF.type,NCAL.FreebusyPeriod);
        }
    }

    /**
     * Crawls the GEO property. <br>
     * Note that the geo prefix refers to the http://www.w3.org/2003/01/geo/wgs84_pos# namespace
     * 
     * <pre>
     *   ical:
     *   GEO:40.442673;-79.945815
     *   
     *   n3:
     *   _:VEventNode ncal:geo [
     *         a geo:Point ;
     *         geo:lat "40.442673"^^xsd:decimal ;
     *         geo:long "-79.945815"^^xsd:decimal
     *   ] .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlGeoProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        String[] valueTokens = property.getValue().split(";");
        Literal latitudeLiteral = rdfContainer.getModel().createDatatypeLiteral(valueTokens[0], XSD._decimal);
        Literal longitudeLiteral = rdfContainer.getModel().createDatatypeLiteral(valueTokens[1], XSD._decimal);
        Resource geoPointNode = generateAnonymousNode(rdfContainer);

        addStatement(rdfContainer, geoPointNode, RDF.type, GEO.Point);
        addStatement(rdfContainer, geoPointNode, GEO.lat, latitudeLiteral);
        addStatement(rdfContainer, geoPointNode, GEO.long_, longitudeLiteral);
        addStatement(rdfContainer, parentNode, NCAL.geo, geoPointNode);
    }

    /**
     * Crawls the LAST-MODIFIED property. <br>
     * 
     * <pre>
     *   ical:
     *   LAST-MODIFIED:20041223T151752
     *   
     *   n3:
     *   _:VeventNode ncal:lastModified &quot;2004-12-23T15:17:52&quot;&circ;&circ;xsd:dateTime .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlLastModifiedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.lastModified, propertyValue);
    }

    /**
     * Crawls the LOCATION property. <br>
     * 
     * <pre>
     *   ical:
     *   LOCATION:San Francisco
     *      
     *   n3:
     *   _:VeventNode ncal:location &quot;San Francisco&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlLocationProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.location, property.getValue());
    }

    /**
     * Crawls the METHOD property. <br>
     * 
     * <pre>
     *   ical:
     *   METHOD:PUBLISH
     *   
     *   n3:
     *   _:VcalendarNode ncal:method &quot;PUBLISH&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlMethodProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.method, property.getValue());
    }

    /**
     * Crawls the ORGANIZER property. <br>
     * 
     * <pre>
     *   ical:
     *   ORGANIZER;CN=JohnSmith:MAILTO:jsmith@host1.com
     *   
     *   n3:
     *   _:VeventNode ncal:organizer [
     *         a ncal:Organizer ;
     *             ncal:involvedContact [
     *             a nco:Contact ;
     *             nco:fullname "JohnSmith" ;
     *             nco:hasEmailAddress [
     *                 a nco:EmailAddress ;
     *                 nco:emailAddress "jsmith@host1.com"
     *             ]
     *         ] 
     *   ] . 
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlOrganizerProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource blankNode = crawlParameterList(property, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.organizer, blankNode);
        processAttendeeOrOrganizer(property, blankNode, rdfContainer);
        addStatement(rdfContainer, blankNode, RDF.type, NCAL.Organizer);
    }

    /**
     * Crawls the PERCENT-COMPLETE property. <br>
     * 
     * <pre>
     *   ical:
     *   PERCENT-COMPLETE:39
     *   
     *   n3:
     *   _:VtodoNode ncal:percentComplete &quot;39&quot;&circ;&circ;xsd:integer .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlPercentCompleteProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
        addStatement(rdfContainer, parentNode, NCAL.percentComplete, propertyValue);
    }

    /**
     * Crawls the PRIORITY property. <br>
     * 
     * <pre>
     *   ical:
     *   PRIORITY:2
     *   
     *   n3:
     *   _:VtodoNode ncal:priority &quot;2&quot;&circ;&circ;xsd:integer .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlPriorityProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
        addStatement(rdfContainer, parentNode, NCAL.priority, propertyValue);
    }

    /**
     * Crawls the PRODID property. <br>
     * 
     * <pre>
     *   ical:
     *   PRODID:-//Apple Computer\, Inc//iCal 1.5//EN
     *   
     *   n3:
     *   _:VcalendarNode ncal:prodid &quot;-//Apple Computer\, Inc//iCal 1.5//EN&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlProdIdProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.prodid, property.getValue());
    }

    /**
     * Crawls the RDATE property. <br>
     * 
     * <p>
     * The dates and date-times are converted to xmlschema form. 
     * 
     * <pre>
     *   ical:
     *   RDATE:19980118T073000Z
     *   
     *   n3:
     *   _:VEventNode ncal:rdate [
     *         a ncal:NcalDateTime ;
     *         &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;xsd:datetime
     *   ] .
     *   
     *   ical:
     *   RDATE:VALUE=DATE;20020703
     *   
     *   n3:
     *   _:VEventNode ncal:rdate [
     *         a ncal:NcalDateTime ;
     *         ncal:date &quot;2007-07-03&circ;&circ;xsd:date
     *   ] .
     *   
     *   ical:
     *   RDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *    20020630T090000
     *        
     *   n3:
     *    _:VeventNode ncal:rdate [
     *         &quot;2002-06-30T09:00:00&quot;&circ;&circ;xsd:dateTime ;
     *         ncal:ncalTimezone 
     *           &lt;this-calendar-baseuri/softwarestudio.org/Olson_20011030_5/America/New_York&gt;
     *    ] . 
     *    
     *    
     *   ical: 
     *   RDATE;VALUE=DATE:19970304,19970504,19970704,19970904
     *   
     *   n3:
     *   _:VEventNode 
     *         ncal:rdate [
     *              a ncal:NcalDateTime ;
     *              ncal:date &quot;1997-03-04&circ;&circ;xsd:date
     *          ] ;
     *          ncal:rdate [
     *              a ncal:NcalDateTime ;
     *              ncal:date &quot;1997-05-04&circ;&circ;xsd:date
     *          ] ;
     *          ncal:rdate [
     *              a ncal:NcalDateTime ;
     *              ncal:date &quot;1997-07-04&circ;&circ;xsd:date
     *          ] ;
     *          ncal:rdate [
     *              a ncal:NcalDateTime ;
     *              ncal:date &quot;1997-09-04&circ;&circ;xsd:date
     *          ] .
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRDateProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        List<Node> valueList = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addMultipleStatements(rdfContainer, parentNode, NCAL.rdate, valueList);
    }

    /**
     * Crawls the RECURRENCE-ID property. <br>
     * 
     * <p>
     * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. 
     * 
     * <pre>
     *   ical:
     *   RECURRENCE-ID;VALUE=DATE:19960401
     *   
     *   n3:
     *   _:VEventNode ncal:recurrenceId [
     *         a ncal:RecurrenceIdentifier ;
     *         ncal:recurrenceIdDateTime [
     *             a ncal:NcalDateTime ;
     *             ncal:date "1998-04-01"^^xsd:date
     *         ]
     *   ] .
     *  
     *  
     *   ical:
     *   RECURRENCE-ID;RANGE=THISANDFUTURE:19960120T120000Z
     *   
     *   n3:
     *   _VEventNode ncal:recurrenceId [
     *         a ncal:RecurrenceIdentifier ;
     *         ncal:range ncal:thisAndFutureRange ;
     *         ncal:recurrenceIdDateTime [
     *             a ncal:NcalDateTime ;
     *             ncal:dateTime "1996-01-20T12:00:00Z"^xsd:dateTime
     *         ]
     *   ] .
     *  </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRecurrenceIdProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource recurrenceIdBlankNode = crawlParameterList(property, rdfContainer);
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.NCAL_DATE_TIME);
        addStatement(rdfContainer, parentNode, NCAL.recurrenceId, recurrenceIdBlankNode);
        addStatement(rdfContainer, recurrenceIdBlankNode, NCAL.recurrenceIdDateTime, propertyValue);
        addStatement(rdfContainer, recurrenceIdBlankNode, RDF.type, NCAL.RecurrenceIdentifier);
    }

    /**
     * Crawls the RELATED-TO property. <br>
     * 
     * <pre>
     *   ical:
     *   RELATED-TO:&lt;19960401-080045-4000F192713-0052@host1.com&gt;
     *   
     *   n3:
     *   _:VEventNode ncal:relatedToParent 
     *        &quot;&lt;19960401-080045-4000F192713-0052@host1.com&gt;&quot; .
     *  
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRelatedToProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Parameter reltypeParameter = property.getParameter(Parameter.RELTYPE);
        if (reltypeParameter == null) {
            addStatement(rdfContainer, parentNode, NCAL.relatedToParent, property.getValue());
            return;
        }
        String reltype = reltypeParameter.getValue();
        if (reltype.equals(RelType.CHILD.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.relatedToChild, property.getValue());
        } else if (reltype.equals(RelType.PARENT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.relatedToParent, property.getValue());
        } else if (reltype.equals(RelType.SIBLING.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.relatedToSibling, property.getValue());
        } else {
            logger.warn("Unkown RELTYPE parameter value: " + reltype);
        }
    }

    /**
     * Crawls the REPEAT property. <br>
     * 
     * <pre>
     *   ical:
     *   REPEAT:3
     *   
     *   n3:
     *   _:VEventNode ncal:repeat 3&circ;&circ;xsd:integer .
     *  
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRepeatProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
        addStatement(rdfContainer, parentNode, NCAL.repeat, propertyValue);
    }

    /**
     * Crawls the REQUEST-STATUS property. <br>
     * 
     * <pre>
     *   ical:
     *   REQUEST-STATUS:4.1;Event conflict. Date/time is busy.
     *   
     *   n3:
     *   _:VEventNode ncal:requestStatus [
     *         a ncal:RequestStatus;
     *         ncal:returnStatus "4.1" ;
     *         ncal:statusDescription "Event conflict. Date/time is busy."
     *   ]
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     * 
     */
    public void crawlRequestStatusProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        String value = property.getValue();
        String [] parts = value.split(";");
        if (parts.length == 0) {
            return;
        }
        Resource requestStatus = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, requestStatus, RDF.type, NCAL.RequestStatus);
        addStatement(rdfContainer, requestStatus, NCAL.returnStatus,parts[0]);
        addStatement(rdfContainer, parentNode, NCAL.requestStatus, requestStatus);
        if (parts.length >= 2) {
            addStatement(rdfContainer, requestStatus, NCAL.statusDescription,parts[1]);
        }
        
        if (parts.length >= 3) {
            String result = parts[2];
            for (int i = 3; i<parts.length; i++) {
                result += ";" + parts[i];
            }
            addStatement(rdfContainer, requestStatus, NCAL.requestStatusData, result);
        }
    }

    /**
     * Crawls the RESOURCES property. <br>
     * Note that this property allows for multiple comma-separated values
     * <pre>
     *   ical:
     *   RESOURCES:EASEL,PROJECTOR,VCR
     *   
     *   n3:
     *   _:VEventNode ncal:resources "EASEL", "PROJECTOR", "VCR" .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlResourcesProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        List<Node> values = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.TEXT);
        addMultipleStatements(rdfContainer, parentNode, NCAL.resources, values);
    }

    /**
     * Crawls the RRULE property.<br>
     * <p>
     * 
     * This property has RECUR value type. This neccessitates an introduction of an intermediate node.
     * The reccurrence parameters are attached to this intermediate node (as literals with appropriate
     * datatypes)
     * 
     * <pre>
     *   
     *   ical:
     *   RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10
     *   
     *   n3:
     *   _:VeventNode ncal:rrule [
     *         a ncal:RecurrenceRule ;
     *         ncal:freq ncal:yearly ;
     *         ncal:interval "5"^^xsd:integer ;
     *         ncal:byday [
     *             a ncal:BydayRulePart ;
     *             ncal:bydayWeekday ncal:sunday ;
     *             ncal:bydayModifier "-1"^^xsd:integer 
     *         ] ;
     *         ncal:bymonth "10"^^xsd:integer ;
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     * 
     * @see #crawlRecur(String, Resource, RDFContainer)
     */
    public void crawlRRuleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource rruleBlankNode = generateAnonymousNode(rdfContainer);
        crawlRecur(property.getValue(), rruleBlankNode, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.rrule, rruleBlankNode);
    }

    /**
     * Crawls the SEQUENCE property. <br>
     * 
     * <pre>
     *   ical:
     *   SEQUENCE:20
     *   
     *   n3:
     *   _:VEventNode ncal:sequence 20&circ;&circ;xsd:integer .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlSequenceProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
        addStatement(rdfContainer, parentNode, NCAL.sequence, propertyValue);
    }

    /**
     * Crawls the STATUS property. <br>
     * Note that this property has different sets of possible values depending on the component it is attached
     * to. This is modelled as three different RDF properties (ncal:eventStatus, ncal:journalStatus and 
     * ncal:todoStatus). Each of them has a different domain and range. Consult the NCAL documentation for
     * more details.
     * 
     * <pre>
     *   ical:
     *   STATUS:COMPLETED
     *   
     *   n3:
     *   _:VEventNode ncal:eventStatus ncal:completedStatus .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     * @param component the component this property is attached to
     */
    public void crawlStatusProperty(Property property, Resource parentNode, RDFContainer rdfContainer, Component component) {
        String value = property.getValue();
        if (component == null) {
            logger.warn("Trying to crawl the status propperty with a null component argument");
        }
        if (value.equals(Status.VEVENT_CANCELLED.getValue()) || 
            value.equals(Status.VJOURNAL_CANCELLED.getValue()) || 
            value.equals(Status.VTODO_CANCELLED.getValue())) {
            if (component.getName().equals(Component.VEVENT)) {
                addStatement(rdfContainer, parentNode, NCAL.eventStatus, NCAL.cancelledEventStatus);
            } else if (component.getName().equals(Component.VJOURNAL)) {
                addStatement(rdfContainer, parentNode, NCAL.journalStatus, NCAL.cancelledJournalStatus);
            } else if (component.getName().equals(Component.VTODO)) {
                addStatement(rdfContainer, parentNode, NCAL.todoStatus, NCAL.cancelledTodoStatus);
            } else {
                logger.warn("Unknown component has an event status: " + component.getName());
            }
        } else if (value.equals(Status.VEVENT_CONFIRMED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.eventStatus, NCAL.confirmedStatus);
        } else if (value.equals(Status.VEVENT_TENTATIVE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.eventStatus, NCAL.tentativeStatus);
        } else if (value.equals(Status.VJOURNAL_DRAFT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.journalStatus, NCAL.draftStatus);
        } else if (value.equals(Status.VJOURNAL_FINAL.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.journalStatus, NCAL.finalStatus);
        } else if (value.equals(Status.VTODO_COMPLETED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.todoStatus, NCAL.completedStatus);
        } else if (value.equals(Status.VTODO_IN_PROCESS.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.todoStatus, NCAL.inProcessStatus);
        } else if (value.equals(Status.VTODO_NEEDS_ACTION.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.todoStatus, NCAL.needsActionStatus);
        } else {
            logger.warn("Unknown value of the STATUS property: " + value);
        }
    }

    /**
     * Crawls the SUMMARY property. <br>
     * 
     * <pre>
     *   ical:
     *   SUMMARY:Department Party
     *   
     *   n3:
     *   _:VEventNode ncal:summary &quot;Department Party&quot; .
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlSummaryProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.summary, property.getValue());
    }

    /**
     * Crawls the TRANSP property. <br>
     * 
     * <pre>
     *   ical:
     *   TRANSP:OPAQUE
     *   
     *   n3:
     *   _:VEventNode ncal:transp ncal:OpaqueTransparency .
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTranspProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        String value = property.getValue();
        if (value.equals(Transp.OPAQUE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.transp, NCAL.opaqueTransparency);
        } else if (value.equals(Transp.TRANSPARENT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.transp, NCAL.transparentTransparency);
        } else {
            logger.warn("Unknown TRANSP property value: " + value);
        }
    }

    /**
     * Crawls the TRIGGER property. <br>
     * 
     * <pre>
     *   ical:
     *   TRIGGER;RELATED=START:-PT30M
     *   
     *   n3:
     *   _:ValarmNode ncal:trigger [
     *         a ncal:Trigger ;
     *         ncal:related ncal:startTriggerRelation ;
     *         ncal:triggerDuration "-PT30M"^^xsd:dayTimeDuration 
     *   ] .
     *   
     *   ical:
     *   TRIGGER;VALUE=DATE-TIME:20060412T230000Z
     *   
     *   n3:
     *   _:ValarmNode ncal:trigger [
     *         a ncal:Trigger ;
     *         ncal:triggerDateTime "2006-04-12T23:00:00Z"^^xsd:dateTime
     *   ] .
     * </pre>
     * 
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in 
     */
    public void crawlTriggerProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Resource triggerBlankNode = crawlParameterList(property, rdfContainer);
        addStatement(rdfContainer, parentNode, NCAL.trigger, triggerBlankNode);
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DURATION);
        Parameter valueParameter = property.getParameter(Parameter.VALUE);
        if (valueParameter == null || valueParameter.getValue().equals(IcalDataType.DURATION)) {
            addStatement(rdfContainer, triggerBlankNode, NCAL.triggerDuration, propertyValue);
        } else if (valueParameter.getValue().equals(IcalDataType.DATE_TIME)) {
            addStatement(rdfContainer, triggerBlankNode, NCAL.triggerDateTime, propertyValue);
        } else {
            logger.warn("Unknown VALUE parameter for the TRIGGER property: " + valueParameter.getValue());
        }
        addStatement(rdfContainer, triggerBlankNode, RDF.type, NCAL.Trigger);
    }

    /**
     * Crawls the TZID property. <br>
     * 
     * <p>
     * Note that this is a property, that occurs within the VTIMEZONE component. It is something completely
     * different from the TZID parameter.
     * 
     * <pre>
     *   ical:
     *   TZID:/softwarestudio.org/Olson_20011030_5/America/New_York
     *   
     *   n3:
     *   _:VTimezoneNode ncal:tzid 
     *        &quot;/softwarestudio.org/Olson_20011030_5/America/New_York&quot; .
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzidProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.tzid, property.getValue());
    }

    /**
     * Crawls the TZNAME property. <br>
     * 
     * <pre>
     *   ical:
     *   TZNAME:EDT
     *   
     *   n3:
     *   _:VTimezoneNode ncal:tzname 
     *        &quot;/softwarestudio.org/Olson_20011030_5/America/New_York&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzNameProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.tzname, property.getValue());
    }

    /**
     * Crawls the TZOFFSETFROM property. <br>
     * 
     * <pre>
     *   ical:
     *   TZOFFSETFROM:-0500
     *   
     *   n3:
     *   _:VTimezoneNode ncal:tzoffsetfrom &quot;-0500&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzOffsetFromProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.tzoffsetfrom, property.getValue());
    }

    /**
     * Crawls the TZOFFSETTO property. <br>
     * 
     * <pre>
     *   ical:
     *   TZOFFSETTO:+1000
     *   
     *   n3:
     *   _:VTimezoneNode ncal:tzoffsetto &quot;+1000&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzOffsetToProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.tzoffsetto, property.getValue());
    }

    /**
     * Crawls the TZURL property. <br>
     * 
     * <p>
     * Note that the value of this property is an URI:
     * 
     * <pre>
     *   ical:
     *   TZURL:http://timezones.r.us.net/tz/US-California-Los_Angeles
     *   
     *   n3:
     *   _:VTimezoneNode ncal:tzurl 
     *        &lt;http://timezones.r.us.net/tz/US-California-Los_Angeles&gt; .
     *   &lt;http://timezones.r.us.net/tz/US-California-Los_Angeles&gt; a rdfs:Resource .
     *   </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzUrlProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.URI);
        addStatement(rdfContainer, parentNode, NCAL.tzurl, propertyValue);
    }

    /**
     * Crawls the UID property. <br>
     * 
     * <pre>
     *   ical:
     *   UID:20020630T230445Z-3895-69-1-7@jammer
     *   
     *   n3:
     *   _:VTimezoneNode ncal:uid &quot;20020630T230445Z-3895-69-1-7@jammer&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlUidProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.uid, property.getValue());
    }

    /**
     * Crawls the URL property. <br>
     * 
     * <pre>
     *   ical:
     *   URL:http://abc.com/pub/calendars/jsmith/mytime.ics
     *   
     *   n3:
     *   _:VTimezoneNode ncal:url 
     *        &lt;http://abc.com/pub/calendars/jsmith/mytime.ics&gt; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlUrlProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.URI);
        addStatement(rdfContainer, parentNode, NCAL.url, propertyValue);
    }

    /**
     * Crawls the VERSION property. <br>
     * 
     * <pre>
     *   ical:
     *   VERSION:2.0
     *   
     *   n3:
     *   _:VCalendarNode ncal:version &quot;2.0&quot; .
     * </pre>
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlVersionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.version, property.getValue());
    }

    /**
     * Extended properties are disregarded at the moment.
     * @param property the Property instance to be crawled
     * @param parentNode the parent component node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlXtendedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
    // don't do anything
    }

    // //////////////////////////////////////// PARAMETERS ////////////////////////////////////////////////

    /**
     * The altrep parameter is ignored it should be represented as appropriate altRep properties
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlAltRepParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // do nothing
    }

    /**
     * The CN parameter is ignored. It is covered by nco:fullname property of the nco:Contact instance
     * referenced by the appropriate ncal:Organizer or ncal:Attendee instance with a ncal:involvedContact link
     * @see #crawlOrganizerProperty(Property, Resource, RDFContainer)
     * @see #crawlAttendeeProperty(Property, Resource, RDFContainer)
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCnParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // do nothing
    }

    /**
     * Crawls the CUTYPE parameter. 
     * 
     * <pre>
     *   ical:
     *   ATTENDEE;CUTYPE=GROUP:MAILTO:ietf-calsch@imc.org
     *   
     *   n3:
     *   _:VeventNode ncal:attendee [
     *         a ncal:Attendee ;
     *         ncal:cutype ncal:groupUserType ;
     *         ncal:involvedContact [
     *             a nco:Contact ;
     *             nco:hasEmailAddress [
     *                 a nco:EmailAddress ;
     *                 nco:emailAddress "ietf-calsch@imc.org"
     *             ]
     *         ]
     *   ] . 
     * </pre>
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlCuTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(CuType.GROUP.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.cutype, NCAL.groupUserType);
        } else if (value.equals(CuType.INDIVIDUAL.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.cutype, NCAL.individualUserType);
        } else if (value.equals(CuType.RESOURCE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.cutype, NCAL.resourceUserType);
        } else if (value.equals(CuType.ROOM.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.cutype, NCAL.roomUserType);
        } else if (value.equals(CuType.UNKNOWN.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.cutype, NCAL.unknownUserType);
        } else {
            logger.warn("Unknown CUTYPE parameter value: " + value);
        }
    }

    /**
     * Crawls the DELEGATED-TO parameter
     * 
     * <pre>
     * ical:
     * ATTENDEE;DELEGATED-TO="MAILTO:jdoe@host.com","MAILTO:jqpublic@
     *  host.com":MAILTO:jsmith@host.com
     *  
     * n3:
     * _:veventNode ncal:attendee [
     *      a ncal:Attendee ;
     *      ncal:delegatedTo [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jdoe@host.com"
     *          ] 
     *      ];
     *      ncal:delegatedTo [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jqpublic@host.com"
     *          ] 
     *      ];
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jsmith@host.com"
     *          ]
     *      ]
     * ] .
     * </pre>
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDelegatedToParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // FIXME fix this so that it actually conforms to the ontology
        addStatement(rdfContainer, parentNode, NCAL.delegatedTo, parameter.getValue());
    }

    /**
     * Crawls the DELEGATED-FROM parameter
     * 
     * <pre>
     * ical:
     * ATTENDEE;DELEGATED-FROM=&quot;MAILTO:jsmith@host.com&quot;:MAILTO:
     *  jdoe@host.com
     *  
     * n3:
     * _:veventNode ncal:attendee [
     *      a ncal:Attendee ;
     *      ncal:delegatedFrom [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress &quot;jsmith@host.com&quot;
     *          ] 
     *      ];
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress &quot;jdoe@host.com&quot;
     *          ]
     *      ]
     * ] .
     * </pre>
     * 
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDelegatedFromParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // FIXME fix this so that it actually conforms to the ontology
        addStatement(rdfContainer, parentNode, NCAL.delegatedFrom, parameter.getValue());
    }

    /**
     * Crawls the DIR parameter
     * 
     * <pre>
     * ical:
     * ORGANIZER;DIR="ldap://host.com:6666/o=eDABC%20Industries,c=3DUS??
     *  (cn=3DBJim%20Dolittle)":MAILTO:jimdo@host1.com
     *  
     * n3:
     * _:veventNode ncal:organizer [
     *      a ncal:Organizer ;
     *      ncal:dir &lt;ldap://host.com:6666/o=eDABC%20Industries,c=3DUS??(cn=3DBJim%20Dolittle)&gt; ;
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress &quot;jimdo@host1.com&quot;
     *          ]
     *      ]
     * ] .
     * </pre>
     * 
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlDirParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // FIXME fix this so that it actually conforms to the ontology
        addStatement(rdfContainer, parentNode, NCAL.dir, parameter.getValue());
    }

    /**
     * Crawls the ENCODING parameter.
     * 
     * @see #crawlAttachProperty(Property, Resource, RDFContainer) for examples
     * 
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlEncodingParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(Encoding.BASE64.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.encoding, NCAL.base64Encoding);
        } else if (value.equals(Encoding.EIGHT_BIT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.encoding, NCAL._8bitEncoding);
        } else {
            logger.warn("Unknown encoding type: " + value);
        }
    }

    /**
     * Crawls the FBTYPE parameter.
     * 
     * @see #crawlFreeBusyProperty(Property, Resource, RDFContainer) for examples
     * 
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlFbTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.fbtype, parameter.getValue());
    }

    /**
     * Crawls the FMTTYPE parameter. The value of this parameter is converted to lower case.
     * 
     * @see #crawlAttachProperty(Property, Resource, RDFContainer)
     * 
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    private void crawlFmtTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.fmttype, parameter.getValue().toLowerCase());
    }

    /**
     * The language parameter is ignored. It should be expressed as a language tag in a literal
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlLanguageParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // do nothing
    }

    /**
     * Crawls the MEMBER parameter
     * 
     * <pre>
     * ical:
     * ATTENDEE;MEMBER="MAILTO:projectA@host.com","MAILTO:projectB@host.
     *  com":MAILTO:janedoe@host.com
     *  
     * n3:
     * _:veventNode ncal:attendee [
     *      a ncal:Attendee ;
     *      ncal:member [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "projectA@host.com"
     *          ] 
     *      ];
     *      ncal:member [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "projectB@host.com"
     *          ] 
     *      ];
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "janedoe@host.com"
     *          ]
     *      ]
     * ] .
     * </pre>
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlMemberParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // FIXME fix this so that it actually conforms to the ontology
        addStatement(rdfContainer, parentNode, NCAL.member, parameter.getValue());
    }

    /**
     * Crawls the PARTSTAT parameter. 
     * 
     * <pre>
     * ical:
     * ATTENDEE;PARTSTAT=DECLINED:MAILTO:jsmith@host.com
     * 
     * n3:
     * _:veventNode ncal:attendee [
     *      a ncal:Attendee ;
     *      ncal:partstat ncal:declinedParticipationStatus ;
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jsmith@host.com"
     *          ]
     *      ]
     * ] .
     * </pre>
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlPartStatParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(PartStat.ACCEPTED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.acceptedParticipationStatus);
        } else if (value.equals(PartStat.COMPLETED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.completedParticipationStatus);
        } else if (value.equals(PartStat.DECLINED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.declinedParticipationStatus);
        } else if (value.equals(PartStat.DELEGATED.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.delegatedParticipationStatus);
        } else if (value.equals(PartStat.IN_PROCESS.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.inProcessParticipationStatus);
        } else if (value.equals(PartStat.NEEDS_ACTION.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.needsActionParticipationStatus);
        } else if (value.equals(PartStat.TENTATIVE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.partstat, NCAL.tentativeParticipationStatus);
        } else {
            logger.warn("Unknown PARTSTAT parameter value: " + value);
        }
    }

    /**
     * Crawls the RANGE parameter.
     * 
     * @see #crawlRecurrenceIdProperty(Property, Resource, RDFContainer) for examples
     *  
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRangeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(Range.THISANDFUTURE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.range, NCAL.thisAndFutureRange);
        } else if (value.equals(Range.THISANDPRIOR.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.range, NCAL.thisAndPriorRange);
        } else {
            logger.warn("Unknown RANGE parameter value: " + value);
        }
    }

    /**
     * Crawls the RELATED parameter.
     * 
     * @see #crawlTriggerProperty(Property, Resource, RDFContainer) for examples
     *  
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRelatedParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(Related.START.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.related, NCAL.startTriggerRelation);
        } else if (value.equals(Related.END.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.related, NCAL.endTriggerRelation);
        } else {
            logger.warn("Unknown RELATED parameter value: " + value);
        }
    }

    /**
     * The reltype parameter is ignored, it should be covered by the relatedTo property. Three possible
     * values of the RELTYPE parameter are expressed as three different properties: ncal:relatedToParent
     * ncal:relatedToSibling and ncal:relatedToChild.
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */ 
    public void crawlRelTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        //addStatement(rdfContainer, parentNode, NCAL.reltype, parameter.getValue());
    }

    /**
     * Crawls the ROLE parameter.
     * 
     * @see #crawlAttendeeProperty(Property, Resource, RDFContainer) for examples
     *  
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRoleParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(Role.CHAIR.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.role, NCAL.chairRole);
        } else if (value.equals(Role.NON_PARTICIPANT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.role, NCAL.nonParticipantRole);
        } else if (value.equals(Role.OPT_PARTICIPANT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.role, NCAL.optParticipantRole);
        } else if (value.equals(Role.REQ_PARTICIPANT.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.role, NCAL.reqParticipantRole);
        } else {
            logger.warn("Unknown ROLE parameter value: " + value);
        }
    }

    /**
     * Crawls the RSVP parameter.
     * 
     * @see #crawlAttendeeProperty(Property, Resource, RDFContainer) for examples
     *  
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlRsvpParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        String value = parameter.getValue();
        if (value.equals(Rsvp.TRUE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.rsvp, true);
        } else if (value.equals(Rsvp.FALSE.getValue())) {
            addStatement(rdfContainer, parentNode, NCAL.rsvp, false);
        } else {
            logger.warn("Unknown RSVP parameter value: " + value);
        }
    }

    /**
     * Crawls the SENT-BY parameter.
     * <pre>
     * ical:
     * ORGANIZER;SENT-BY:"MAILTO:sray@host.com":MAILTO:jsmith@host.com
     * 
     * n3:
     * _:veventNode ncal:organizer [
     *      a ncal:Organizer;
     *      ncal:sentBy [
     *          a nco:Contact;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "sray@host.com"  
     *          ]
     *      ] ;
     *      ncal:involvedContact [
     *          a nco:Contact ;
     *          nco:hasEmailAddress [
     *              a nco:EmailAddress ;
     *              nco:emailAddress "jsmith@host.com" 
     *          ]
     *      ]
     * ] .
     * </pre>
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlSentByParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
        // FIXME fix this so that it actually conforms to the ontology
        addStatement(rdfContainer, parentNode, NCAL.sentBy, parameter.getValue());
    }

    /**
     * Note that this parameter is ignored in the NCAL ontology. It is treated differently. If a value of
     * a property that has TZID parameter is expressed as an instance of the NcalDateTime with the 
     * appropriate ncalTimezone property.
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlTzidParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
    // do nothing
    }

    /**
     * This parameter is ignored. Each property that accepts VALUE parameter is expressed as an instance
     * of an appropriate class. See the 
     * 
     * @see #getRdfPropertyValue(RDFContainer, Property, String)
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlValueParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
    // do nothing
    }

    /** 
     * Extended parameters are disregarded at the moment.
     * @param parameter the Parameter instance to be crawled
     * @param parentNode the parent node
     * @param rdfContainer the container to store the generated statements in
     */
    public void crawlXParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
    // do nothing
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////// RECURRENCE RULES ////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void crawlRecur(String recurString, Resource rruleBlankNode, RDFContainer rdfContainer) {
        String[] recurTokens = recurString.split("[=;]");
        for (int i = 0; i < recurTokens.length; i += 2) {
            try {
                crawlRecurrenceParam(recurTokens[i], recurTokens[i + 1], rruleBlankNode, rdfContainer);
            }
            catch (ModelException e) {
                logger.warn("ModelException while processing recurrence param, skipping param", e);
            }
        }
        addStatement(rdfContainer, rruleBlankNode, RDF.type, NCAL.RecurrenceRule);
    }

    private void crawlRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) throws ModelException {
        if (name.equals("FREQ")) {
            crawlFreqRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("UNTIL")) {
            crawlUntilRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("COUNT")) {
            crawlCountRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("INTERVAL")) {
            crawlIntervalRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYSECOND")) {
            crawlBySecondRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYMINUTE")) {
            crawlByMinuteRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYHOUR")) {
            crawlByHourRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYDAY")) {
            crawlByDayRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYMONTHDAY")) {
            crawlByMonthdayRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYYEARDAY")) {
            crawlByYeardayRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYWEEKNO")) {
            crawlByWeeknoRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYMONTH")) {
            crawlByMonthRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("BYSETPOS")) {
            crawlBySetposRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else if (name.equals("WKST")) {
            crawlWkstRecurrenceParam(name, value, parentNode, rdfContainer);
        }
        else {
            logger.warn("Unknown recurrence param name " + name);
        }
    }

    private void crawlFreqRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        if (value.equals(Recur.YEARLY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.yearly);
        } else if (value.equals(Recur.MONTHLY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.monthly);
        } else if (value.equals(Recur.WEEKLY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.weekly);
        } else if (value.equals(Recur.DAILY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.daily);
        } else if (value.equals(Recur.HOURLY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.hourly);
        } else if (value.equals(Recur.MINUTELY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.minutely);
        } else if (value.equals(Recur.SECONDLY)) {
            addStatement(rdfContainer, parentNode, NCAL.freq, NCAL.secondly);
        } else {
            logger.warn("Unknown FREQ recurrence rule parameter value: " + value);
        }
    }

    private void crawlUntilRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.until, value);
    }
    
    private void crawlIntervalRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) throws ModelException {
        Literal literal = rdfContainer.getValueFactory().createLiteral(value, XSD._integer);
        addStatement(rdfContainer, parentNode, NCAL.interval, literal);
    }
    
    private void crawlCountRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) throws ModelException {
        Literal literal = rdfContainer.getValueFactory().createLiteral(value, XSD._integer);
        addStatement(rdfContainer, parentNode, NCAL.count, literal);
    }

    private void crawlBySecondRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.bysecond, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlByMinuteRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.byminute, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlByHourRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.byhour, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    /**
     * Crawls the byday recurrence param. Creates an instance of the BydayRulePart class with appropriate
     * properties.
     *
     * <pre>
     *   ical:
     *   RRULE:FREQ=MONTHLY;BYDAY=2MO,-1TU
     *   
     *   n3:
     *   _:VeventNode ncal:rrule [
     *      a ncal:RecurrenceRule ;
     *      ncal:freq ncal:Monthly ;
     *      ncal:byday [
     *          a ncal:BydayRulePart ;
     *          ncal:bydayWeekday ncal:Monday
     *          ncal:bydayModifier 2 
     *      ] ;
     *      ncal:byday [
     *          a ncal:BydayRulePart ;
     *          ncal:bydayWeekday ncal:Tuesday
     *          ncal:bydayModifier -1 
     *      ] 
     *   ] .
     * </pre>
     * 
     * @param name
     * @param value
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlByDayRecurrenceParam(String name, String originalValue, Resource parentNode,
            RDFContainer rdfContainer) throws ModelException {
        String[] array = originalValue.split(",");
        for (String value : array) {
            Resource bydayRulePartNode = generateAnonymousNode(rdfContainer);
            String twoLastCharacters = value.substring(value.length() - 2, value.length());
            URI weekdayURI = null;
            if (twoLastCharacters.equals("MO")) {
                weekdayURI = NCAL.monday;
            }
            else if (twoLastCharacters.equals("TU")) {
                weekdayURI = NCAL.tuesday;
            }
            else if (twoLastCharacters.equals("WE")) {
                weekdayURI = NCAL.wednesday;
            }
            else if (twoLastCharacters.equals("TH")) {
                weekdayURI = NCAL.thursday;
            }
            else if (twoLastCharacters.equals("FR")) {
                weekdayURI = NCAL.friday;
            }
            else if (twoLastCharacters.equals("SA")) {
                weekdayURI = NCAL.saturday;
            }
            else if (twoLastCharacters.equals("SU")) {
                weekdayURI = NCAL.sunday;
            }
            else {
                logger.warn("Unknown day of the week: " + value);
                return;
            }
            int number = 0;
            boolean numberPresent = false;
            if (value.length() > 2) {
                String numberPart = value.substring(0, value.length() - 2);
                try {
                    number = Integer.parseInt(numberPart);
                    numberPresent = true;
                }
                catch (NumberFormatException nfe) {
                    return;
                }
            }
            addStatement(rdfContainer, bydayRulePartNode, NCAL.bydayWeekday, weekdayURI);
            if (numberPresent) {
                addStatement(rdfContainer, bydayRulePartNode, NCAL.bydayModifier, rdfContainer.getModel()
                        .createDatatypeLiteral(String.valueOf(number), XSD._integer));
            }
            addStatement(rdfContainer, bydayRulePartNode, RDF.type, NCAL.BydayRulePart);
            addStatement(rdfContainer, parentNode, NCAL.byday, bydayRulePartNode);
        }
    }

    private void crawlByMonthdayRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.bymonthday, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlByYeardayRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.byyearday, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlByWeeknoRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.byweekno, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlByMonthRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.bymonth, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlBySetposRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        try {
            int number = Integer.parseInt(value);
            addStatement(rdfContainer, parentNode, NCAL.bysetpos, number);
        } catch (NumberFormatException nfe) {
            // nothing to do
        }
    }

    private void crawlWkstRecurrenceParam(String name, String value, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, NCAL.wkst, value);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// CONVENIENCE METHODS ////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Prepare an RDF container and notify the handler about accessing the new DataObject.
     * 
     * @param component The component about which the information will be gathered.
     * @return the container, where the statements about the given uri should be stored
     */
    private RDFContainer prepareDataObjectRDFContainer(Component component) {
        URI uri = generateComponentUri(component);
        return prepareDataObjectRDFContainer(uri);
    }

    /**
     * Prepare an RDF container and notify the handler about accessing the new DataObject.
     * 
     * @param uri The central URI for the new RDFContainer
     * @return the container, where the statements about the given uri should be stored
     */
    private RDFContainer prepareDataObjectRDFContainer(URI uri) {
        // register that we're processing this calendar component
        handler.accessingObject(this, uri.toString());
        // remove it from the deprecated URI's list, so it won't be reported as removed after crawling
        deprecatedUrls.remove(uri.toString());
        RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, uri.toString());
        RDFContainer rdfContainer = containerFactory.getRDFContainer(uri);
        rdfContainer.add(RDF.type,NCAL.CalendarDataObject);
        return rdfContainer;
    }

    /**
     * Builds a statement from the provided ingredients and adds it to the given rdfContainer. Treats the
     * given string as an untyped string literal.
     * 
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, Resource subject, URI predicate, String object) {
        try {
            addStatement(rdfContainer, subject, predicate, rdfContainer.getValueFactory().createLiteral(object));
        }
        catch (ModelException e) {
            logger.warn("ModelException while creating literal, skipping statement", e);
        }
    }
    
    /**
     * Builds a statement from the provided ingredients and adds it to the given rdfContainer. Treats the
     * given integer as a typed literal with the xsd:integer datatype
     * 
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, Resource subject, URI predicate, int object) {
        try {
            addStatement(rdfContainer, subject, predicate, rdfContainer.getValueFactory().createLiteral(object));
        }
        catch (ModelException e) {
            logger.warn("ModelException while creating literal, skipping statement", e);
        }
    }
    
    /**
     * Builds a statement from the provided ingredients and adds it to the given rdfContainer. Treats the
     * given integer as a typed literal with the xsd:boolean datatype
     * 
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, Resource subject, URI predicate, boolean object) {
        try {
            addStatement(rdfContainer, subject, predicate, rdfContainer.getValueFactory().createLiteral(object));
        }
        catch (ModelException e) {
            logger.warn("ModelException while creating literal, skipping statement", e);
        }
    }

    /**
     * Builds a statement from the provided ingredients and adds it to the given rdfContainer.
     * 
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, Resource subject, URI predicate, Node object) {
        Statement statement = rdfContainer.getValueFactory().createStatement(subject, predicate, object);
        rdfContainer.add(statement);
    }

    private void addMultipleStatements(RDFContainer rdfContainer, Resource parentNode, URI predicate,
            List<Node> valueList) {
        for (Node value : valueList) {
            addStatement(rdfContainer, parentNode, predicate, value);
        }
    }

    private void processAttendeeOrOrganizer(Property property, Resource attendeeOrOrganizerNode, RDFContainer rdfContainer) {
        Resource contactBlankNode = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, attendeeOrOrganizerNode, NCAL.involvedContact, contactBlankNode);
        addStatement(rdfContainer, contactBlankNode, RDF.type, NCO.PersonContact);
        Resource emailAddressBlankNode = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, contactBlankNode, NCO.hasEmailAddress, emailAddressBlankNode);
        addStatement(rdfContainer, emailAddressBlankNode, RDF.type, NCO.EmailAddress);
        // we cut off the "mailto:" prefix
        addStatement(rdfContainer, emailAddressBlankNode, NCO.emailAddress, property.getValue().substring(7));
        Parameter cnParameter = property.getParameter(Parameter.CN);
        if (cnParameter != null) {
            String cn = cnParameter.getValue();
            addStatement(rdfContainer, contactBlankNode, NCO.fullname, cn);
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// METHODS RESPONSIBLE FOR INCREMENTAL CRAWLING ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a DataObject that encapsulates the given metadata and passes it to the CrawlerHandler. The main
     * URI for the DataObject is extracted from the metadata. This method checks the AccessData to determine
     * if this object has already been encountered and if it has been changed or not.
     * 
     * @param metadata The RDFContainer that contains triples to be passed to the handler.
     */
    private void passComponentToHandler(RDFContainer metadata, Component component) {
        DataObject dataObject = new DataObjectBase(metadata.getDescribedUri(), getDataSource(), metadata);
        String id = metadata.getDescribedUri().toString();
        if (accessData == null) {
            handler.objectNew(this, dataObject);
        }
        else if (!accessData.isKnownId(id)) {
            if (component == null) {
                // this can only happen if a vcalendar is passed
                updateAccessData(metadata, hashOfProperties(metadata));
            } else {
                updateAccessData(metadata, component);
            }
            handler.objectNew(this, dataObject);
        }
        else if (isChanged(metadata, component)) {
            if (component == null) {
                // this can only happen if a vcalendar is passed
                updateAccessData(metadata, hashOfProperties(metadata));
            } else {
                updateAccessData(metadata, component);
            }
            handler.objectChanged(this, dataObject);
        }
        else {
            handler.objectNotModified(this, id);
            dataObject.dispose();
        }
    }
    
    private void passAttachmentToHandler(RDFContainer metadata, byte[] bytes) {
        String sha1Hash = sha1Hash(bytes);
        InputStream stream = new ByteArrayInputStream(bytes);
        FileDataObject dataObject = new FileDataObjectBase(metadata.getDescribedUri(), source, metadata,
                stream);
        String id = metadata.getDescribedUri().toString();

        if (accessData == null) {
            handler.objectNew(this, dataObject);
        }
        else if (!accessData.isKnownId(id)) {
            updateAccessData(metadata,sha1Hash);
            handler.objectNew(this, dataObject);
        }
        else if (isAttachmentChanged(metadata,sha1Hash)) {
            updateAccessData(metadata,sha1Hash);
            handler.objectChanged(this, dataObject);
        }
        else {
            handler.objectNotModified(this, id);
            dataObject.dispose();
        }
    }    

    /**
     * Updates the accessData with the current state of the given object.
     * 
     * @param metadata The RDFContainer with metadata about the object to be updated.
     */
    private void updateAccessData(RDFContainer metadata, Component component) {
        updateAccessData(metadata, sha1Hash(component.toString()));
    }
    
    private void updateAccessData(RDFContainer metadata, String hash) {
        String id = metadata.getDescribedUri().toString();
        accessData.put(id, "visited", "true");
        accessData.put(id, "hash", hash);
    }

    private String hashOfProperties(RDFContainer metadata) {
        StringBuffer buffer = new StringBuffer("");
        appendPropertyValue(buffer, metadata, NCAL.method);
        appendPropertyValue(buffer, metadata, NCAL.prodid);
        appendPropertyValue(buffer, metadata, NCAL.version);
        appendPropertyValue(buffer, metadata, NCAL.calscale);
        return sha1Hash(buffer.toString());
    }
    
    @SuppressWarnings("unchecked")
    private void appendPropertyValue(StringBuffer buffer, RDFContainer metadata, URI predicate) {
        Collection<Node> propertyValues = metadata.getAll(predicate);
        for (Node value : propertyValues) {
            appendSinglePropertyValue(buffer, predicate, value);
        }
    }

    private void appendSinglePropertyValue(StringBuffer buffer, URI predicate, Node value) {
        if (value instanceof Literal) {
            URI datatype = null;
            String label = ((Literal) value).getValue();
            if (value instanceof DatatypeLiteral) {
                datatype = ((DatatypeLiteral)value).getDatatype();
            }
            if (buffer.length() > 0) {
                buffer.append("#");
            }
            buffer.append(predicate.toString());
            buffer.append("#");
            buffer.append(label);
            if (datatype != null) {
                buffer.append("#");
                buffer.append(datatype.toString());
            }
        } else if (value instanceof URI) {
            if (buffer.length() > 0) {
                buffer.append("#");
            }
            buffer.append(predicate.toString());
            buffer.append("#");
            buffer.append(value.toString());
        }
    }

    /**
     * Determines if the given object has been changed or not.
     * 
     * <p>
     * It compares the hash of the current metadata object with the hash of the old metadata object.
     * 
     * @param metadata
     * @return true if the metadata in the rdfcontainer differs from the one stored in AccessData
     *        false otherwise
     */
    private boolean isChanged(RDFContainer metadata, Component component) {
        String id = metadata.getDescribedUri().toString();
        String newHash = ((component == null) ? hashOfProperties(metadata) : sha1Hash(component.toString()));
        String oldHash = accessData.get(id, "hash");
        if (oldHash == null) {
            return true;
        }
        else {
            return !oldHash.equals(newHash);
        }
    }
    
    private boolean isAttachmentChanged(RDFContainer metadata, String newHash) {
        String id = metadata.getDescribedUri().toString();
        String oldHash = accessData.get(id, "hash");
        if (oldHash == null) {
            return true;
        }
        else {
            return !oldHash.equals(newHash);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// METHODS THAT GENERATE VARIOUS URIS /////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the URI of the current calendar. It used in methods that crawl particular components to add the
     * triple, that binds the component with the calendar.
     * 
     * @return the URI of the current calendar
     */
    private URI generateCalendarUri() {
        return new URIImpl(baseuri + "Calendar");
    }
    
    /**
     * Generates an appropriate URI for the component. Timezones received an URI that points to the Dan
     * Connolly timezone database. For other components it uses the UID field if present. If not, generates a
     * hash-value from existing field values.
     * 
     * @param component The component, for which the URI should be generated.
     * @return The generated URI.
     */
    private URI generateComponentUri(Component component) {
        // special treatment of timezones:
        if (component instanceof VTimeZone) {
            return generateTimeZoneURI((VTimeZone)component);
        }
        Property uidProperty = component.getProperty(Property.UID);
        if (uidProperty != null) {
            return new URIImpl(baseuri + uidProperty.getValue());
        }
        else {
            return generateSumOfAllPropertiesURI(component);
        }
    }

    /**
     * Generated the URI for a timezone component. 
     * 
     * @param component The VTimezone component for which the URI should be generated.
     * @return The generated URI.
     */
    private URI generateTimeZoneURI(VTimeZone component) {
        Property tzidProperty = component.getProperty(Property.TZID);
        if (tzidProperty != null) {
            return createTimeZoneURI(tzidProperty.getValue());
        }
        else {
            return generateSumOfAllPropertiesURI(component);
        }
    }

    /**
     * Generates a URI for the component as a hash value of the sum of all existing property values.
     * 
     * @param component The component for which the URI should be generated.
     * @return The generated URI.
     */
    @SuppressWarnings("unchecked")
    private URI generateSumOfAllPropertiesURI(Component component) {
        StringBuffer sumOfAllProperties = new StringBuffer("");
        PropertyList propertyList = component.getProperties();
        Iterator it = propertyList.iterator();
        while (it.hasNext()) {
            Property property = (Property) it.next();
            sumOfAllProperties.append(property.getValue());
        }
        String result = baseuri + sha1Hash(sumOfAllProperties.toString());
        return new URIImpl(result);
    }

    /**
     * Generates a URI for an anonymous component. This one is used for embedded components - like valarms.
     * 
     * @param component
     * @return a URI for an anonymous calendar componetn (Valarm or a timezone observance).
     */
    private URI generateAnonymousComponentUri(Component component) {
        String result = baseuri + component.getName() + "-" + java.util.UUID.randomUUID().toString();
        return new URIImpl(result);
    }
    
    private URI createTimeZoneURI(String tzidParamValue) {
        return new URIImpl(baseuri + tzidParamValue);
    }

    private Resource generateAnonymousNode(RDFContainer rdfContainer) {
        return UriUtil.generateRandomResource(rdfContainer.getModel());
    }
    
    private URI generateAttachmentUri(URI describedUri) {
        return new URIImpl(describedUri.toString() + "/attachment");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////// CONVERSION OF ICAL PROPERTY VALUES INTO RDF NODES /////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The most important method from this group. Those below are only convenience methods.
     */
    private Node getRdfPropertyValue(RDFContainer rdfContainer, String propertyValue,
            Parameter actualTzidParameter, Parameter actualValueParameter, String defaultType) {
    
        //NcalDateTime
        if (defaultType.equals(IcalDataType.NCAL_DATE_TIME)) {
            return getNcalDateTime(rdfContainer,propertyValue,actualTzidParameter,actualValueParameter);
        }

        // return values of the type URI as RDF URI's
        if (actualValueParameter == null && defaultType.equals(IcalDataType.URI) || 
            actualValueParameter != null && actualValueParameter.getValue().equals(IcalDataType.URI)) {
            URI uri = tryToCreateAnUri(rdfContainer, propertyValue);
            addStatement(rdfContainer, uri, RDF.type, RDFS.Resource);
            return uri;
        }
        
        if (actualValueParameter == null && defaultType.equals(IcalDataType.PERIOD) || 
            actualValueParameter != null && actualValueParameter.getValue().equals(IcalDataType.PERIOD)) {
            return createPeriod(rdfContainer, propertyValue);
        }

        Literal literal = null;
        URI datatypeURI = null;
        String rdfPropertyValue = null;
        if (actualValueParameter != null) {
            String valueParameterString = actualValueParameter.getValue();
            datatypeURI = convertValueParameterToXSDDatatype(valueParameterString);
            rdfPropertyValue = convertIcalValueToXSDValue(propertyValue, valueParameterString);
            literal = rdfContainer.getModel().createDatatypeLiteral(rdfPropertyValue, datatypeURI);
        }
        else if (defaultType != null) {
            datatypeURI = convertValueParameterToXSDDatatype(defaultType);
            rdfPropertyValue = convertIcalValueToXSDValue(propertyValue, defaultType);
            literal = rdfContainer.getModel().createDatatypeLiteral(rdfPropertyValue, datatypeURI);
        }
        else {
            literal = rdfContainer.getModel().createPlainLiteral(propertyValue);
        }
        return literal;
    }
    
    private Node createPeriod(RDFContainer rdfContainer, String propertyValue) {
        String [] values = propertyValue.split("/");
        if (values.length != 2) {
            return null;
        }
        Resource periodResource = generateAnonymousNode(rdfContainer);
        addStatement(rdfContainer, periodResource, RDF.type, NCAL.NcalPeriod);
        addStatement(rdfContainer, periodResource, NCAL.periodBegin, rdfContainer.getModel()
                .createDatatypeLiteral(convertIcalDateTimeToXSDDateTime(values[0]), XSD._dateTime));
        // woe be to those that try to feed faulty Ical files to this crawler
        if (values[1].contains("P")) {
            addStatement(rdfContainer, periodResource, NCAL.periodDuration, rdfContainer.getModel()
                    .createDatatypeLiteral(convertIcalDurationToXSDDayTimeDuration(values[1]), XSD_DAY_TIME_DURATION));
        }
        else {
            addStatement(rdfContainer, periodResource, NCAL.periodEnd, rdfContainer.getModel()
                .createDatatypeLiteral(convertIcalDateTimeToXSDDateTime(values[1]), XSD._dateTime));
        }
        return periodResource;
    }

    private Node getNcalDateTime(RDFContainer rdfContainer, String icalValue, Parameter tzidParameter,
            Parameter valueParameter) {
        Resource ncalDateTimeNode = generateAnonymousNode(rdfContainer); 
        if (valueParameter == null || valueParameter.getValue().equals(IcalDataType.DATE_TIME)) {
            String rdfPropertyValue = convertIcalDateTimeToXSDDateTime(icalValue);
            addStatement(rdfContainer, ncalDateTimeNode, RDF.type, NCAL.NcalDateTime);
            addStatement(rdfContainer, ncalDateTimeNode, NCAL.dateTime,
                rdfContainer.getModel().createDatatypeLiteral(rdfPropertyValue, XSD._dateTime));
            if (tzidParameter != null) {
                URI timezoneURI = createTimeZoneURI(tzidParameter.getValue());
                addStatement(rdfContainer, ncalDateTimeNode, NCAL.ncalTimezone, timezoneURI);
                // the following line shouldn't be necessary if every file contained a timezone definition
                // the RFC says that it MUST be so, but alas we don't live in an ideal world
                addStatement(rdfContainer, timezoneURI, RDF.type, NCAL.Timezone);
            }
        } else if (valueParameter.getValue().equals(IcalDataType.DATE)) {
            String rdfPropertyValue = convertIcalDateToXSDDate(icalValue);
            addStatement(rdfContainer, ncalDateTimeNode, RDF.type, NCAL.NcalDateTime);
            addStatement(rdfContainer, ncalDateTimeNode, NCAL.date,
                rdfContainer.getModel().createDatatypeLiteral(rdfPropertyValue, XSD._date));
        } else if (valueParameter.getValue().equals(IcalDataType.PERIOD)) {
            return createPeriod(rdfContainer, icalValue);
        }
        return ncalDateTimeNode;
    }
   
    private List<Node> getMultipleRdfPropertyValues(RDFContainer rdfContainer, Property property,
            String defaultType) {
        String totalPropertyValue = property.getValue();
        if (totalPropertyValue == null) {
            return null;
        }
        String[] valuesArray = totalPropertyValue.split(",");
        List<Node> resultList = new LinkedList<Node>();
        for (int i = 0; i < valuesArray.length; i++) {
            Node currentValue = getRdfPropertyValue(rdfContainer, valuesArray[i], property
                    .getParameter(Parameter.TZID), property.getParameter(Parameter.VALUE), defaultType);
            resultList.add(currentValue);
        }
        return resultList;
    }

    private Node getRdfPropertyValue(RDFContainer rdfContainer, Property property, String defaultType) {
        return getRdfPropertyValue(rdfContainer, property.getValue(), property.getParameter(Parameter.TZID),
            property.getParameter(Parameter.VALUE), defaultType);
    }
    
    /**
     * Tries to create an URI from the given string. Introduced to support 'URI's' like 'Ping'.
     * 
     * @param propertyValue The string that the URI should be created from.
     * @return The created URI.
     */
    private URI tryToCreateAnUri(RDFContainer rdfContainer, String propertyValue) {
        // first let's try the easy way;
        URI uri = null;
        // ugly hack, Sesame doesn't accept uris without colons, but java.net.URI does
        // we have to check it manually
        if (propertyValue.indexOf(':') == -1) {
            uri = rdfContainer.getModel().createURI("uri:" + propertyValue);
        } else {
            try {
                uri = rdfContainer.getModel().createURI(propertyValue);
            }
            catch (Exception e) {
                // oops...
                uri = rdfContainer.getModel().createURI(baseuri + propertyValue);
            }
        }
        return uri;
    }

    private String convertIcalValueToXSDValue(String value, String icalDataType) {
        if (icalDataType == null) {
            return value;
        }
        else if (icalDataType.equals(IcalDataType.DATE_TIME)) {
            return convertIcalDateTimeToXSDDateTime(value);
        }
        else if (icalDataType.equals(IcalDataType.DATE)) {
            return convertIcalDateToXSDDate(value);
        }
        else {
            return value;
        }
    }
    
    
    
    private Pattern icalDurationPattern = Pattern.compile(
        "(\\+|-)?P((\\d+W)|(\\d+D(T\\d+H(\\d+M(\\d+S)?)?)?)|(T\\d+H(\\d+M(\\d+S)?)?))");
    
    /**
     * Converts the ICAL duration to XSD duration. Ical supports week-based
     * durations, xsd doesn't.
     * @param string the ical duration
     * @return the xsd duration
     */
    public String convertIcalDurationToXSDDayTimeDuration(String string) {
        
        if (!icalDurationPattern.matcher(string).matches()) {
            throw new RuntimeException("wrong duration format");
        }
        
        boolean positive = true;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        
        if (string.charAt(0) == '-') {
            positive = false;
            // we cut the - and P from the beginning
            string = string.substring(2);
        } else if (string.charAt(0) == '+') {
            // we cut the + and P from the beginnig
            string = string.substring(2);
        } else {
            // we cut the P from the beginning
            string = string.substring(1);
        }
        StringTokenizer tokenizer = new StringTokenizer(string,"+-PWDTHMS",true);
        while (tokenizer.hasMoreTokens()) {
            String numberToken = tokenizer.nextToken();
            if (numberToken.equals("T")) {
                continue;
            }
            String unitNameToken = tokenizer.nextToken();
            if (unitNameToken.equals("W")) {
                weeks += Integer.parseInt(numberToken);
            } else if (unitNameToken.equals("D")) {
                days += Integer.parseInt(numberToken);
            } else if (unitNameToken.equals("H")) {
                hours += Integer.parseInt(numberToken);
            } else if (unitNameToken.equals("M")) {
                minutes += Integer.parseInt(numberToken);
            } else if (unitNameToken.equals("S")) {
                seconds += Integer.parseInt(numberToken);
            } 
        }
        
        long totalNumberOfSeconds 
            = seconds 
            + 60 * minutes 
            + 3600 * hours 
            + 24 * 3600 * days 
            + 7 * 24 * 3600 * weeks;
        
        String resultString = "";
        if (totalNumberOfSeconds == 0) {
            resultString = "P0S";
        } else {
            resultString = ((positive) ? "" : "-") 
                + "P";
            if (totalNumberOfSeconds / (24*3600) != 0) {
                resultString += "" + (totalNumberOfSeconds / (24*3600)) + "D"; 
            }
            totalNumberOfSeconds %= 24*3600;
            if (totalNumberOfSeconds != 0) {
                resultString += "T";
            }
            if (totalNumberOfSeconds / 3600 != 0) {
                resultString += "" + (totalNumberOfSeconds / 3600) + "H";
            }
            totalNumberOfSeconds %= 3600;
            if (totalNumberOfSeconds / 60 != 0) {
                resultString += "" + (totalNumberOfSeconds / 60) + "M";
            }
            totalNumberOfSeconds %= 60;
            if (totalNumberOfSeconds != 0) {
                resultString += "" + (totalNumberOfSeconds) + "S";
            }
        }
        return resultString;
    }

    /**
     * Converts the ical date (YYYYMMDD) to an XSD Date (YYYY-MM-DD)
     * 
     * @param icalDate The ical date to convert.
     * @return The XSD date.
     */
    private String convertIcalDateToXSDDate(String icalDate) {
        if (icalDate.length() != 8) {
            throw new IllegalArgumentException("Invalid ical date: " + icalDate);
        }
        String year = icalDate.substring(0, 4);
        String month = icalDate.substring(4, 6);
        String day = icalDate.substring(6, 8);
        return year + "-" + month + "-" + day;
    }

    /**
     * Converts the ical date (YYYYMMDD) to an XSD Date (YYYY-MM-DD)
     * 
     * @param icalDate The ical date to convert.
     * @return The XSD date.
     */
    private String convertIcalDateTimeToXSDDateTime(String icalDateTime) {
        if (icalDateTime.length() < 15 || icalDateTime.length() > 16) {
            throw new IllegalArgumentException("Invalid ical datetime: " + icalDateTime);
        }
        String date = convertIcalDateToXSDDate(icalDateTime.substring(0, 8));
        // we omit the 'T' letter in the middle
        String hour = icalDateTime.substring(9, 11);
        String minute = icalDateTime.substring(11, 13);
        String second = icalDateTime.substring(13, 15);
        String z = (icalDateTime.length() == 16) ? "Z" : "";
        return date + "T" + hour + ":" + minute + ":" + second + z;
    }

    private URI convertValueParameterToXSDDatatype(String valueString) {
        URI datatypeURI = null;
        if (valueString.equalsIgnoreCase("TEXT")) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase("BINARY")) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase("BOOLEAN")) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase(IcalDataType.CAL_ADDRESS)) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase(IcalDataType.DATE)) {
            datatypeURI = XSD._date;
        }
        else if (valueString.equalsIgnoreCase(IcalDataType.DATE_TIME)) {
            datatypeURI = XSD._dateTime;
        }
        else if (valueString.equalsIgnoreCase(IcalDataType.DURATION)) {
            datatypeURI = XSD_DAY_TIME_DURATION;
        }
        else if (valueString.equalsIgnoreCase("FLOAT")) {
            datatypeURI = XSD._float;
        }
        else if (valueString.equals(IcalDataType.INTEGER)) {
            datatypeURI = XSD._integer;
        }
        else if (valueString.equalsIgnoreCase(IcalDataType.PERIOD)) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase("RECUR")) {
            datatypeURI = null;
        }
        else if (valueString.equalsIgnoreCase("TIME")) {
            datatypeURI = XSD._time;
        }
        else if (valueString.equalsIgnoreCase("URI")) {
            datatypeURI = XSD._anyURI;
        }
        else if (valueString.equalsIgnoreCase("UTC-OFFSET")) {
            datatypeURI = null;
        }
        else {
            logger.warn("Unknown value parameter: " + valueString);
        }
        return datatypeURI;
    }
    
    /**
     * Computes the SHA1 hash for the given string.
     * <p>
     * The code has been 'borrowed' from the mimedir-parser available from
     * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
     * 
     * @param string The string for which we'd like to get the SHA1 hash.
     * @return The generated SHA1 hash
     */
    private String sha1Hash(String string) {
        try {
            return sha1Hash(string.getBytes());
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private String sha1Hash(byte [] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(bytes);
            byte[] digest = md.digest();
            BigInteger integer = new BigInteger(1, digest);
            return integer.toString(16);
        }
        catch (Exception e) {
            return null;
        }
    }
}
