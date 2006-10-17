/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.ProdId;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

/**
 * A Crawler implementation for crawling ical calendar sources modeled by a
 * FileSystemDataSource. This version reports all objects as new.
 */
public class IcalCrawler extends CrawlerBase {

    private static final Logger LOGGER = Logger.getLogger(IcalCrawler.class
            .getName());

    /** The file from which we read. */
    private File icalFile;

    /** A flag that indicates if we want to introduce real blank nodes (true),
     * or anonymous random uris (false). Default is false.
     */
    private boolean realBlankNodes;
    
    /**
     * The prefix, common to all ical product namespaces. Forms the basis from
     * which the extendedNamespaceIsGenerated
     * 
     * @see IcalCrawler#generateExtendedNamespace(String)
     */
    private static final String productNamespacePrefix 
            = "http://www.w3.org/2002/12/cal/prod";
    
    private static final String timezoneNamespacePrefix 
            = "http://www.w3.org/2002/12/cal/tzd";
    
    /**
     * The actual namespace for extended properties. The default value is simply
     * the productNamespacePrefix. It will be used, if there is no prodid in the
     * current file.
     * 
     * @see IcalCrawler#generateExtendedNameSpace(String)
     */
    private String extendedNameSpace = productNamespacePrefix;

    /**
     * The Random object used to create anonymous node id's
     */
    private ValueFactory valueFactory;
    private String baseuri;
    
    public IcalCrawler() {
        valueFactory = new ValueFactoryImpl();
    }
    
    /**
     * The main method that performs the actual crawl.
     * Reads the file path from the data source configuration.
     * 
     * @return The ExitCode
     */
    protected ExitCode crawlObjects() {
        // fetch the source and its configuration
        DataSource source = getDataSource();
        RDFContainer configuration = source.getConfiguration();

        // determine the root file
        String icalFilePath = ConfigurationUtil.getRootUrl(configuration);
        realBlankNodes = checkRealBlankNodes(configuration);
        if (icalFilePath == null) {
            // treat this as an error rather than an "empty source" to prevent
            // information loss
            LOGGER.log(Level.SEVERE,
                    "missing iCalendar file path specification");
            return ExitCode.FATAL_ERROR;
        }
        
        icalFile = new File(icalFilePath);
        if (!icalFile.exists()) {
            LOGGER.log(Level.SEVERE, "iCalendar file does not exist: '"
                    + icalFile + "'");
            return ExitCode.FATAL_ERROR;
        }

        if (!icalFile.canRead()) {
            LOGGER.log(Level.SEVERE, "iCalendar file cannot be read: '"
                    + icalFile + "'");
            return ExitCode.FATAL_ERROR;
        }
        
        try {
            baseuri = "file://" + icalFile.getCanonicalPath() + "#";
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't get the canonical path " +
                    "for the iCalFile",e);
            return ExitCode.FATAL_ERROR;
        }
        

        // crawl the ical file
        return crawlIcalFile(icalFile);
    }
    
    private boolean checkRealBlankNodes(RDFContainer configuration) {
        Boolean bool = configuration.getBoolean(ICALTZD.realBlankNodes);
        if (bool == null || bool.booleanValue() == false) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Crawls the ical file.
     * Parses the file, creates the calendar object and proceeds to crawl it.
     * 
     * @param icalFile The file to be crawled.
     * 
     * @return The exit code. Possible values are:
     * <ul>
     * <li> ExitCode.FATAL_ERROR - some error occured (see the logs for details)
     * <li> ExitCode.COMPLETED - crawl successfully completed.
     * </ul>
     */

    private ExitCode crawlIcalFile(File icalFile) {
        FileReader fin = null;
        CalendarBuilder builder = null;
        Calendar calendar = null;
        try {
            System.setProperty("ical4j.unfolding.relaxed", "true");
            fin = new FileReader(icalFile);
            builder = new CalendarBuilder();
            calendar = builder.build(fin);
        } catch (FileNotFoundException fnfe) {
            LOGGER.log(Level.SEVERE, "Couldn't find the calendar file", fnfe);
            return ExitCode.FATAL_ERROR;
        } catch (ParserException pe) {
            LOGGER.log(Level.SEVERE, "Couldn't parse the calendar file", pe);
            return ExitCode.FATAL_ERROR;
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Input/Output error while parsing "
                    + "the calendar file", ioe);
            return ExitCode.FATAL_ERROR;
        }

        crawlCalendar(calendar);

        return ExitCode.COMPLETED;
    }

    ////////////////////////////////////////////////////////////////////////
    ///////////////////// ABSTRACT BUSINESS METHODS ////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Crawls the calendar.
     * 
     * Generates a DataObject for the Calendar itself and passes it to the
     * crawlerHandler. Then continues to crawl the entire component list.
     * 
     * @param calendar
     *            The calendar object.
     * 
     * @see generateExtendedNamespace(String prodid)
     */
    private void crawlCalendar(Calendar calendar) {
        URI uri = generateCalendarUri();
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(uri);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vcalendar);

        // we have to process the prodid first, to compute the extendedNamespace
        // before any extended properties come on the propertyList

        ProdId prodId = calendar.getProductId();
        if (prodId != null) {
            generateExtendedNameSpace(prodId.getValue());
            extendedNameSpace = generateExtendedNameSpace(prodId.getValue());
        }
        
        PropertyList propertyList = calendar.getProperties();
        crawlPropertyList(propertyList,rdfContainer.getDescribedUri(),
                rdfContainer);
        
        passComponentToHandler(rdfContainer);
        
        ComponentList componentList = calendar.getComponents();
        crawlComponentList(componentList,rdfContainer);
    }

    /**
     * Crawls a single calendar component.
     * 
     * @param component
     *            The component to crawl.
     * @param parentNode
     *            The root node of the generated subtree.
     * @param rdfContainer
     *            The container where the resulting triples should be stored.
     *            Applicable to embedded components i.e. those that don't create
     *            a new DataObject. The "main" components ignore this parameter
     *            and create their own RDFContainers.
     */
    private void crawlSingleComponent(Component component, Resource parentNode,
            RDFContainer rdfContainer) {
        if (component.getName().equals(Component.VALARM)) {
            crawlVAlarmComponent(component, parentNode, rdfContainer);
        } else if (component.getName().equals(Component.VEVENT)) {
            crawlVEventComponent(component, parentNode);
        } else if (component.getName().equals(Component.VFREEBUSY)) {
            crawlVFreebusyComponent(component, parentNode);
        } else if (component.getName().equals(Component.VJOURNAL)) {
            crawlVJournalComponent(component, parentNode);
        } else if (component.getName().equals(Component.VTIMEZONE)) {
            crawlVTimezoneComponent(component, parentNode);
        } else if (component.getName().equals(Component.VTODO)) {
            crawlVTodoComponent(component, parentNode);
        } else if (component.getName().equals(Observance.STANDARD)) {
            crawlStandardObservance(component,parentNode,rdfContainer);
        } else if (component.getName().equals(Observance.DAYLIGHT)) {
            crawlDaylightObservance(component,parentNode,rdfContainer);
        } else if (component.getName()
                .startsWith(Component.EXPERIMENTAL_PREFIX)) {
            crawlExperimentalComponent(component, parentNode);
        } else {
            LOGGER.log(Level.SEVERE, "Unknown component name: "
                    + component.getName());
        }
    }
    
    /**
     * Crawls a single property. Checks the name of the property and 
     * dispatches it to an appropriate property-handling method.
     * 
     * @param property
     * @param rdfContainer
     */
    private void crawlSingleProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        String propertyName = property.getName();
    
        if (propertyName.equals(Property.ACTION)) {
            crawlActionProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.ATTACH)) {
            crawlAttachProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.ATTENDEE)) {
            crawlAttendeeProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.CALSCALE)) {
            crawlCalScaleProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.CATEGORIES)) {
            crawlCategoriesProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.CLASS)) {
            crawlClassProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.COMMENT)) {
            crawlCommentProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.COMPLETED)) {
            crawlCompletedProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.CONTACT)) {
            crawlContactProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.CREATED)) {
            crawlCreatedProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DESCRIPTION)) {
            crawlDescriptionProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DTEND)) {
            crawlDtEndProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DTSTAMP)) {
            crawlDtStampProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DTSTART)) {
            crawlDtStartProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DUE)) {
            crawlDueProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.DURATION)) {
            crawlDurationProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.EXDATE)) {
            crawlExDateProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.EXRULE)) {
            crawlExRuleProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.FREEBUSY)) {
            crawlFreeBusyProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.GEO)) {
            crawlGeoProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.LAST_MODIFIED)) {
            crawlLastModifiedProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.LOCATION)) {
            crawlLocationProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.METHOD)) {
            crawlMethodProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.ORGANIZER)) {
            crawlOrganizerProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.PERCENT_COMPLETE)) {
            crawlPercentCompleteProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.PRIORITY)) {
            crawlPriorityProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.PRODID)) {
            crawlProdIdProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.RDATE)) {
            crawlRDateProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.RECURRENCE_ID)) {
            crawlRecurrenceIdProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.RELATED_TO)) {
            crawlRelatedToProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.REPEAT)) {
            crawlRepeatProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.REQUEST_STATUS)) {
            crawlRequestStatusProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.RESOURCES)) {
            crawlResourcesProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.RRULE)) {
            crawlRRuleProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.SEQUENCE)) {
            crawlSequenceProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.STATUS)) {
            crawlStatusProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.SUMMARY)) {
            crawlSummaryProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TRANSP)) {
            crawlTranspProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TRIGGER)) {
            crawlTriggerProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TZID)) {
            crawlTzidProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TZNAME)) {
            crawlTzNameProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TZOFFSETFROM)) {
            crawlTzOffsetFromProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TZOFFSETTO)) {
            crawlTzOffsetToProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.TZURL)) {
            crawlTzUrlProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.UID)) {
            crawlUidProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.URL)) {
            crawlUrlProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.VERSION)) {
            crawlVersionProperty(property, parentNode, rdfContainer);
        } else if (propertyName.startsWith(Property.EXPERIMENTAL_PREFIX)){
            crawlXtendedProperty(property, parentNode, rdfContainer);
        } else {
            LOGGER.log(Level.SEVERE, "Unknown property name: "
                    + property.getName());
        }        
    }
    
    private void crawlSingleParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        String parameterName = parameter.getName();
        if (parameterName.equals(Parameter.ALTREP)) {
            crawlAltRepParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.CN)) {
            crawlCnParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.CUTYPE)) {
            crawlCuTypeParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.DELEGATED_FROM)) {
            crawlDelegatedFromParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.DELEGATED_TO)) {
            crawlDelegatedToParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.DIR)) {
            crawlDirParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.ENCODING)) {
            crawlEncodingParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.FBTYPE)) {
            crawlFbTypeParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.FMTTYPE)) {
            crawlFmtTypeParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.LANGUAGE)) {
            crawlLanguageParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.MEMBER)) {
            crawlMemberParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.PARTSTAT)) {
            crawlPartStatParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.RANGE)) {
            crawlRangeParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.RELATED)) {
            crawlRelatedParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.RELTYPE)) {
            crawlRelTypeParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.ROLE)) {
            crawlRoleParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.RSVP)) {
            crawlRsvpParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.SENT_BY)) {
            crawlSentByParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.TZID)) {
            crawlTzidParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.equals(Parameter.VALUE)) {
            crawlValueParameter(parameter, parentNode, rdfContainer);
        } else if (parameterName.startsWith(Parameter.EXPERIMENTAL_PREFIX)) {
            crawlXParameter(parameter, parentNode, rdfContainer);
        } else {
            LOGGER.severe("Unknown parameter name: '" + parameterName + "'");
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
    private void crawlComponentList(ComponentList componentList,
            RDFContainer rdfContainer) {
        Iterator it = componentList.iterator();
        while (it.hasNext()) {
            Component component = (Component) it.next();
            crawlSingleComponent(component, rdfContainer.getDescribedUri(),
                    rdfContainer);
        }
    }
    
    /**
     * Iterates over the properties of a given component and adds those 
     * properties to a given container.
     * 
     * @param component
     * @param rdfContainer
     */
    private void crawlPropertyList(Component component,
            RDFContainer rdfContainer) {
        crawlPropertyList(component, rdfContainer.getDescribedUri(),
                rdfContainer);
    }
    
    /**
     * Iterates over the properties of a given component, and adds those
     * properties to a given RDFContainer. The properties are attached to
     * a given parent node.
     * 
     * @param propertyList
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlPropertyList(Component component, Resource parentNode,
            RDFContainer rdfContainer) {
        PropertyList propertyList = component.getProperties();
        crawlPropertyList(propertyList,parentNode,rdfContainer);
    }
    
    /**
     * Iterates over a propertyList, attaches those properties to a given
     * parentNode, add stores the resulting triples in a given RDFContainer.
     * 
     * @param propertyList
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlPropertyList(PropertyList propertyList, 
            Resource parentNode, RDFContainer rdfContainer) {
        Iterator it = propertyList.iterator();
        while (it.hasNext()) {
            Property property = (Property) it.next();
            crawlSingleProperty(property, parentNode, rdfContainer);
        }
    }
    
    /**
     * Crawls the parameter list of a given property. Note that there is
     * specific treatment for the VALUE parameter. 
     * 
     * @see convertValueTypeToURI
     * 
     * @param property
     *            The property whose parameter list we would like to crawl.
     * @param parentNode
     *            The node to which the generated RDF subtree should be
     *            attached.
     * @param defaultValueType
     *            The default ical type for the property value.
     */
    private Resource crawlParameterList(Property property,
            RDFContainer rdfContainer) {
        Resource propertyBlankNode = generateAnonymousNode();
        ParameterList parameterList = property.getParameters();
        crawlParameterList(parameterList, propertyBlankNode, rdfContainer);
        return propertyBlankNode;
    }
    
    /**
     * Iterates over a parameter list and adds those parameters to a given
     * RDFContainer.
     * 
     * @param parameterList
     * @param parentNode
     * @param parametersRDFContainer
     */
    private void crawlParameterList(ParameterList parameterList,
            Resource parentNode, RDFContainer parametersRDFContainer) {
        Iterator it = parameterList.iterator();
        while (it.hasNext()) {
            Parameter parameter = (Parameter) it.next();
            crawlSingleParameter(parameter, parentNode, parametersRDFContainer);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    ///////////////////// SPECIFIC BUSINESS METHODS ////////////////////////
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////// COMPONENTS //////////////////////////////////

    /**
     * Crawls a single VAlarm component. Attaches it to the parent vevent with
     * a ical:component link.
     * 
     * @param component
     * @param rdfContainer
     */
    private void crawlVAlarmComponent(Component component, Resource parentNode,
            RDFContainer rdfContainer) {
        VAlarm valarm = (VAlarm) component;
        URI valarmParentNode = generateAnonymousComponentUri(component);
        crawlPropertyList(valarm, valarmParentNode,rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.component,valarmParentNode);
    }

    private void crawlVEventComponent(Component component, 
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vevent);
        VEvent vevent = (VEvent) component;
        crawlPropertyList(vevent, rdfContainer);
        ComponentList alarmList = vevent.getAlarms();
        crawlComponentList(alarmList, rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }

    /**
     * Crawls a single VFreebusy component. 
     * 
     * <p>
     * Unsupported by fromIcal.py at the time of writing (2006-10-17)
     */
    private void crawlVFreebusyComponent(Component component,
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        VFreeBusy vfreebusy = (VFreeBusy)component;
        rdfContainer.add(RDF.TYPE, ICALTZD.Vfreebusy);
        crawlPropertyList(vfreebusy,rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }

    private void crawlVJournalComponent(Component component,
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vjournal);
        VJournal vjournal = (VJournal)component;
        crawlPropertyList(vjournal,rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }

    private void crawlVTimezoneComponent(Component component,
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vtimezone);
        VTimeZone vtimezone = (VTimeZone)component;
        crawlPropertyList(vtimezone, rdfContainer);
        ComponentList observances = vtimezone.getObservances();
        crawlComponentList(observances,rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }

    private void crawlVTodoComponent(Component component,
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vtodo);
        VToDo vtodo = (VToDo)component;
        crawlPropertyList(vtodo, rdfContainer);
        ComponentList alarmList = vtodo.getAlarms();
        crawlComponentList(alarmList,rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }

    private void crawlExperimentalComponent(Component component,
            Resource parentNode) {
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
        rdfContainer.add(RDF.TYPE, extendedNameSpace + component.getName());
        crawlPropertyList(component,rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.component,
                rdfContainer.getDescribedUri());
        passComponentToHandler(rdfContainer);
    }
    
    private void crawlStandardObservance(Component component,
            Resource parentNode, RDFContainer rdfContainer) {
        Resource standardParentNode = generateAnonymousNode();
        crawlPropertyList(component, standardParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.standard,
                standardParentNode);
    }
    
    private void crawlDaylightObservance(Component component,
            Resource parentNode, RDFContainer rdfContainer) {
        Resource daylightParentNode = generateAnonymousNode();
        crawlPropertyList(component, daylightParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.daylight,
                daylightParentNode);
    }
    
    /////////////////////////// PROPERTIES /////////////////////////////////

    /**
     * Crawls the ACTION property.<br>
     * Possible parameters: none<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:calscale<br>
     * <pre>
     * ical:
     * ACTION:AUDIO
     * 
     * n3:
     * _:ValarmNode icaltzd:action AUDIO
     * 
     * </pre>
     */
    private void crawlActionProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.action, 
                property.getValue());
    }
    
    /**
     * Crawls the ATTACH property.<br>
     * 
     * THIS PROPERTY IS OFFICIALY DISREGARDED BY THE RDF SCHEMA
     */
    private void crawlAttachProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        
    }
    
    /**
     * Crawls the ATTENDEE property.<br>
     * Possible parameters: numerous<br>
     * Treatment: blank node<br>
     * 1st link: icaltzd:attendee<br>
     * 2nd link: icaltzd:valaddress<br>
     * 
     * <pre>
     * ical:
     * ATTENDEE;RSVP=TRUE;ROLE=REQ-PARTICIPANT:MAILTO:jsmith@host.com
     * 
     * n3:
     * <#Vevent-URI> icaltzd:attendee _:anon .
     * _:anon icaltzd:rsvp TRUE;
     *        icaltzd:role REQ-PARTICIPANT;
     *        icaltzd:caladdress MAILTO:jsmith@jhost.com
     * 
     * </pre>
     * 
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlAttendeeProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
       Resource blankNode = crawlParameterList(property, rdfContainer);
       addStatement(rdfContainer,parentNode,ICALTZD.attendee,blankNode);
       addStatement(rdfContainer,blankNode,ICALTZD.calAddress,
               property.getValue());
    }
    
    /** 
     * Crawls the CALSCALE property.<br>
     * Possible parameters: none<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:calscale<br>
     * 
     * <pre>
     * ical:
     * CALSCALE:GREGORIAN
     * 
     * n3:
     * _:VcalendarNode icaltzd:calscale GREGORIAN
     * </pre>
     */
    private void crawlCalScaleProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.calscale,
                property.getValue());
    }
    
    /**
     * Crawls the CATEGORIES property.<br>
     * Possible parameters: LANGUAGE (DISGREGARDED)<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:categories<br>
     * 
     * <pre>
     * ical:
     * CATEGORIES:APPOINTMENT,EDUCATION
     * 
     * n3:
     * _:VeventNode icaltzd:categories APPOINTMENT,EDUCATION
     * </pre>
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlCategoriesProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.categories,
                property.getValue());
    }
    
    /**
     * Crawls the CLASS property.<br>
     * Possible parameters: none<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:class_<br>
     * <pre>
     * ical:
     * CLASS:PUBLIC
     * 
     * n3:
     * _:VcalendarNode icaltzd:class PUBLIC
     * </pre>
     */
    private void crawlClassProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.class_,
                property.getValue());
    }
    
    /**
     * Crawls the COMMENT property.<br>
     * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:comment<br>
     * 
     * <pre>
     * ical:
     * COMMENT:The meeting really needs to include both ourselves
     *   and the customer. We can't hold this  meeting without them.
     *   As a matter of fact\, the venue for the meeting ought to be at
     *   their site. - - John
     * 
     * n3:
     * _:VeventNode icaltzd:comment 
     *   """The meeting really needs to include both ourselves
     *   and the customer. We can't hold this  meeting without them.
     *   As a matter of fact\, the venue for the meeting ought to be at
     *   their site. - - John""" 
     * </pre>
     */
    private void crawlCommentProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.comment,
                property.getValue());
    }
    
    /**
     * Crawls the COMPLETED property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:completed
     * 
     * <pre>
     * ical:
     * COMPLETED:19971210T080000Z
     * 
     * n3:
     * _:VTodoNode icaltzd:completed "1997-12-10T08:00:00Z"^^<"&xsd;#datetime">
     * </pre>
     */
    private void crawlCompletedProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.completed, propertyValue);
    }
    
    /**
     * Crawls the CONTACT property.<br>
     * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
     * Treatment: direct link<br>
     * 1st link: icaltzd:contact<br>
     * 
     * Not mentioned in the works of the rdf ical group. Unsupported by 
     * fromIcal.py at the time of writing (2006-10-17)
     * 
     * <pre>
     * ical:
     * CONTACT:Jim Dolittle\, ABC Industries\, +1-919-555-1234
     * 
     * n3:
     * _:VeventNode icaltzd:contact 
     *   """Jim Dolittle\, ABC Industries\, +1-919-555-1234""" 
     * </pre>
     */
    private void crawlContactProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.contact,
                property.getValue());
    }
    
    /**
     * Crawls the CREATED property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:created
     * 
     * Note the conversion to the XSD time format
     * 
     * <pre>
     * ical:
     * CREATED:19971210T080000
     * 
     * n3:
     * _:VeventNode icaltzd:created "1997-12-10T08:00:00"^^<"&xsd;#datetime">
     * </pre>
     */
    private void crawlCreatedProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.created, propertyValue);
    }
    
    /**
     * Crawls the DESCRIPTION property.
     * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
     * Treatment: direct link
     * 1st link: icaltzd:description
     * 
     * <pre>
     * ical:
     * DESCRIPTION:Meeting to provide technical review for "Phoenix"
     *  design.\n Happy Face Conference Room. Phoenix design team
     *  MUST attend this meeting.\n RSVP to team leader.
     * 
     * n3:
     * _:VeventNode icaltzd:description 
     *   """Meeting to provide technical review for "Phoenix"
     *  design.\n Happy Face Conference Room. Phoenix design team
     *  MUST attend this meeting.\n RSVP to team leader.""" 
     * </pre>
     */
    private void crawlDescriptionProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.description,
                property.getValue());
    }
    
    /**
     * Crawls the DTEND property.
     * Possible parameters: VALUE, TZID<br>
     * Treatment: direct link
     * 1st link: icaltzd:dtstamp
     * 
     * The dates and date-times are converted to xmlschema form. The timezones
     * are expressed in datatypes. The value literal gets a datatype, whose
     * URI points to the VTimezone object defined in the timezone database
     * under http://www.w3.org/2002/12/cal/tzd/
     * 
     * @see http://www.w3.org/2002/12/cal/tzd/
     * 
     * <pre>
     * ical:
     * DTEND:19980118T073000Z
     * 
     * n3:
     * _:VeventNode icaltzd:dtend "1998-01-18T07:30:00Z"^^<"&xsd;#datetime">
     * 
     * ical:
     * DTEND:VALUE=DATE;20020703
     * 
     * n3:
     * _:VeventNode icaltzd:dtend "2002-07-03"^^<"&xsd;#date">
     * 
     * ical:
     * DTEND;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *  20020630T090000
     *  
     * n3:
     * _:VeventNode icaltzd:dtend
     *  "2002-06-30T09:00:00"^^<"&tzd;/America/New_York#tz">
     * <pre>
     */
    private void crawlDtEndProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.dtend, propertyValue);
    }
    
    /**
     * Crawls the DTSTAMP property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:dtstamp
     * 
     * <pre>
     * ical:
     * DTSTAMP:19971210T080000Z
     * 
     * n3:
     * _:VeventNode icaltzd:dtstamp "1997-12-10T08:00:00Z"^^<"&xsd;#datetime">
     * </pre>
     * 
     * Note the conversion to the XSD time format
     */
    private void crawlDtStampProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.dtstamp, propertyValue);
    }
    
    /**
     * Crawls the DTSTART property.
     * Possible parameters: VALUE, TZID<br>
     * Treatment: direct link
     * 1st link: icaltzd:dtstamp
     * 
     * The dates and date-times are converted to xmlschema form. The timezones
     * are expressed in datatypes. The value literal gets a datatype, whose
     * URI points to the VTimezone object defined in the timezone database
     * under http://www.w3.org/2002/12/cal/tzd/
     * 
     * @see http://www.w3.org/2002/12/cal/tzd/
     * 
     * <pre>
     * ical:
     * DTSTART:19980118T073000Z
     * 
     * n3:
     * _:VeventNode icaltzd:dtstart "1998-01-18T07:30:00Z"^^<"&xsd;#datetime">
     * 
     * ical:
     * DTSTART:VALUE=DATE;20020703
     * 
     * n3:
     * _:VeventNode icaltzd:dtstart "2002-07-03"^^<"&xsd;#date">
     * 
     * ical:
     * DTSTART;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *  20020630T090000
     *  
     * n3:
     * _:VeventNode icaltzd:dtstart
     *  "2002-06-30T09:00:00"^^<"&tzd;/America/New_York#tz">
     * <pre>
     */
    private void crawlDtStartProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
            = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.dtstart, propertyValue);
    }
    
    /**
     * Crawls the DUE property.
     * Possible parameters: VALUE, TZID<br>
     * Treatment: direct link
     * 1st link: icaltzd:due
     * 
     * The dates and date-times are converted to xmlschema form. The timezones
     * are expressed in datatypes. The value literal gets a datatype, whose
     * URI points to the VTimezone object defined in the timezone database
     * under http://www.w3.org/2002/12/cal/tzd/
     * 
     * @see http://www.w3.org/2002/12/cal/tzd/
     * 
     * <pre>
     * ical:
     * DUE:19980118T073000Z
     * 
     * n3:
     * _:VTodoNode icaltzd:due "1998-01-18T07:30:00Z"^^<"&xsd;#datetime">
     * 
     * ical:
     * DUE:VALUE=DATE;20020703
     * 
     * n3:
     * _:VTodoNode icaltzd:due "2002-07-03"^^<"&xsd;#date">
     * 
     * ical:
     * DUE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *  20020630T090000
     *  
     * n3:
     * _:VTodoNode icaltzd:due
     *  "2002-06-30T09:00:00"^^<"&tzd;/America/New_York#tz">
     * <pre>
     */
    private void crawlDueProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.due, propertyValue);
    }
    
    /**
     * Crawls the DURATION property.
     * Possible parameters: none<br>
     * Treatment: blank node
     * 1st link: icaltzd:duration
     * 2nd link: icaltzd:value
     * 
     * Note that according to the examples this should be a resource. That's
     * why it introduces a blank node, even though it has no parameters.
     * 
     * <pre>
     * ical:
     * DURATION:PT1H0M0S
     * 
     * n3:
     * _:VeventNode icaltzd:duration _:durationNode
     * _:durationNode icaltzd:value "PT1H0M0S"^^<"&xsd;#duration">
     * </pre>
     */
    private void crawlDurationProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DURATION);
        Resource durationNode 
                = generateAnonymousNode();
        addStatement(rdfContainer, parentNode, ICALTZD.duration, durationNode);
        addStatement(rdfContainer, durationNode, ICALTZD.value, propertyValue);
    }
    
    /**
     * Crawls the EXDATE property.
     * Possible parameters: VALUE, TZID<br>
     * Treatment: direct link
     * 1st link: icaltzd:exdate
     * 
     * <p>
     * The dates and date-times are converted to xmlschema form. The timezones
     * are expressed in datatypes. The value literal gets a datatype, whose
     * URI points to the VTimezone object defined in the timezone database
     * under http://www.w3.org/2002/12/cal/tzd/
     * 
     * <p>
     * The ical definition allows multiple values on this property. We 
     * disregard it and support only singluar values.
     * 
     * @see http://www.w3.org/2002/12/cal/tzd/
     * 
     * <pre>
     * ical:
     * EXDATE:19980118T073000Z
     * 
     * n3:
     * _:VEventNode icaltzd:exdate "1998-01-18T07:30:00Z"^^<"&xsd;#datetime">
     * 
     * ical:
     * EXDATE:VALUE=DATE;20020703
     * 
     * n3:
     * _:VEventNode icaltzd:exdate "2002-07-03"^^<"&xsd;#date">
     * 
     * ical:
     * EXDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *  20020630T090000
     *  
     * n3:
     * _:VEventNode icaltzd:due
     *  "2002-06-30T09:00:00"^^<"&tzd;/America/New_York#tz">
     * <pre>
     */
    private void crawlExDateProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.exdate, propertyValue);
    }
    
    /**
     * Crawls the EXRULE property.
     * Possible parameters: none<br>
     * Treatment: direct link to a blank node<br>
     * 1st link: icaltzd:rrule<br>
     * <p> 
     * This property has RECUR value type. This neccessitates an introduction
     * of an intermediate blank node. The reccurrence parameters are attached
     * to this intermediate blank node (as literals with appropriate datatype)
     * 
     * <p>
     * Note that this property hasn't been mentioned in the works of rdf ical
     * group since it isn't supported by many calendaring applications.
     * 
     * <pre>
     * ical:
     * EXRULE:FREQ=YEARLY;INTERVAL=5;BYDAY=-1SU;BYMONTH=10
     * 
     * n3:
     * _:VeventNode icaltzd:exrule _:exruleNode
     * _:exruleNode icaltzd:bymonth 10;
     *             icaltzd:freq    YEARLY;
     *             icaltzd:interval 5^^<"&xsd;integer">
     *             icaltzd:bymonth -1SU
     * </pre>
     * 
     * @see crawlRecur()
     */
    private void crawlExRuleProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Resource rruleBlankNode = generateAnonymousNode();
        crawlRecur(property.getValue(),rruleBlankNode,rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.exrule,rruleBlankNode);
    }
    
    /**
     * Crawls the FREEBUSY property.
     * Possible parameters: FBTYPE, VALUE<br>
     * Treatment: blank node
     * 1st link: icaltzd:freebusy
     * 2nd link: icaltzd:value
     * 
     * This mapping has been 'borrowed' from ical2rdf.pl since fromIcal.py
     * doesn't support the VFreebusy component and the FREEBUSY property
     * at the time of writing (2006-10-17).
     * 
     * <pre>
     * ical:
     * FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
     *  19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
     * 
     * n3:
     * _:VFreebusyComponentNode icaltzd:freebusy _:freebusyPropertyNode
     * _:freebusyPropertyComponent icaltzd:value 
     *  """19971015T050000Z/PT8H30M,19971015T160000Z/PT5H30M,
     *  19971015T223000Z/PT6H30M"""
     * </pre>
     */
    private void crawlFreeBusyProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.PERIOD);
        addStatement(rdfContainer, parentNode, ICALTZD.freebusy, propertyValue);
    }
    
    /**
     * Crawls the GEO property.
     * Possible parameters: none<br>
     * Treatment: rdf list
     * 1st link: icaltzd:geo
     * 
     * <p>
     * The value of this property is translated into an rdf list of literals.
     * The mapping based on the example from 
     * http://www.w3.org/2002/12/cal/test/geo1.rdf
     * 
     * <pre>
     * ical:
     * GEO:40.442673;-79.945815
     * 
     * n3:
     * _:VEventNode icaltzd:geo _:firstListNode .
     * _firstListNode  rdf:first 40.442673^^<"&xsd;double"> ;
     *                 rdf:rest _:secondListNode .
     * _secondListNode rdf:first -79.945815 ^^<"&xsd;double"> ; 
     *                 rdf:rest <"&rdf;nil"> .
     * </pre>
     */
    private void crawlGeoProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        String [] valueTokens 
                = property.getValue().split(";");        
        Literal latitudeLiteral 
                = valueFactory.createLiteral(valueTokens[0],XMLSchema.DOUBLE);
        Literal longitudeLiteral
                = valueFactory.createLiteral(valueTokens[1],XMLSchema.DOUBLE);        
        Resource firstListNode
                = generateAnonymousNode();
        Resource secondListNode
                = generateAnonymousNode();
        
        addStatement(rdfContainer, firstListNode, RDF.FIRST, latitudeLiteral);
        addStatement(rdfContainer, firstListNode, RDF.REST, secondListNode);
        addStatement(rdfContainer, secondListNode, RDF.FIRST, longitudeLiteral);
        addStatement(rdfContainer, secondListNode, RDF.REST, RDF.NIL);
        addStatement(rdfContainer, parentNode, ICALTZD.geo,firstListNode);
    }
    
    /**
     * Crawls the LAST-MODIFIED property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:lastModified
     * 
     * <pre>
     * ical:
     * LAST-MODIFIED:20041223T151752
     * 
     * n3:
     * _:VeventNode icaltzd:lastModified 
     *      "2004-12-23T15:17:52"^^<"&xsd;datetime">
     * </pre>
     */
    private void crawlLastModifiedProperty(Property property,
            Resource parentNode, RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.DATE_TIME);
        addStatement(rdfContainer, parentNode, ICALTZD.lastModified, 
                propertyValue);
    }
    
    /**
     * Crawls the LAST-MODIFIED property.
     * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
     * Treatment: direct link
     * 1st link: icaltzd:location
     * 
     * <pre>
     * ical:
     * LOCATION:San Francisco
     * 
     * n3:
     * _:VeventNode icaltzd:location "San Francisco" .
     * </pre>
     */
    private void crawlLocationProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.location,
               property.getValue());
    }
    
    /**
     * Crawls the METHOD property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:method
     * 
     * <pre>
     * ical:
     * METHOD:PUBLISH
     * 
     * n3:
     * _:VcalendarNode icaltzd:method "PUBLISH" .
     * </pre>
     */
    private void crawlMethodProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.method,
                property.getValue());
    }
    
    /**
     * Crawls the ORGANIZER property.
     * Possible parameters: numerous<br>
     * Treatment: blank node
     * 1st link: icaltzd:organizer
     * 2nd link: icaltzd:caladdress
     * 
     * <pre>
     * ical:
     * ORGANIZER;CN=JohnSmith:MAILTO:jsmith@host1.com
     * 
     * n3:
     * _:VeventNode icaltzd:organizer _:organizerNode .
     * _:organizerNode icaltzd:cn "JohnSmith" .
     * _:organizerNode icaltzd:calAddress "MAILTO:jsmith@host1.com"
     * </pre>
     */
    private void crawlOrganizerProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Resource blankNode = crawlParameterList(property, rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.organizer,blankNode);
        addStatement(rdfContainer,blankNode,ICALTZD.calAddress,
                property.getValue());
    }
    
    /**
     * Crawls the PERCENT-COMPLETE property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:percentComplete
     * 
     * <pre>
     * ical:
     * PERCENT-COMPLETE:39
     * 
     * n3:
     * _:VtodoNode icaltzd:percentComplete "39"^^<"&xsd;#integer">
     * </pre>
     */
    private void crawlPercentCompleteProperty(Property property,
            Resource parentNode, RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.INTEGER);
        addStatement(rdfContainer,parentNode,ICALTZD.percentComplete, 
                propertyValue);
    }
    
    /**
     * Crawls the PRIORITY property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:priority
     * 
     * <pre>
     * ical:
     * PRIORITY:2
     * 
     * n3:
     * _:VtodoNode icaltzd:priority "2"^^<"&xsd;#integer">
     * </pre>
     */
    private void crawlPriorityProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Value propertyValue 
                = getRdfPropertyValue(property, IcalDataType.INTEGER);
        addStatement(rdfContainer,parentNode,ICALTZD.priority, 
                propertyValue);
    }
    
    /**
     * Crawls the PRODID property.
     * Possible parameters: none<br>
     * Treatment: direct link
     * 1st link: icaltzd:prodid
     * 
     * <pre>
     * ical:
     * PRODID:-//Apple Computer\, Inc//iCal 1.5//EN
     * 
     * n3:
     * _:VcalendarNode icaltzd:prodid 
     *      "-//Apple Computer\, Inc//iCal 1.5//EN"
     * </pre>
     */
    private void crawlProdIdProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.prodid,
                property.getValue());
    }
    
    /**
     * Crawls the RDATE property.
     * Possible parameters: VALUE, TZID<br>
     * Treatment: direct link
     * 1st link: icaltzd:rdate
     * 
     * <p>
     * The dates and date-times are converted to xmlschema form. The timezones
     * are expressed in datatypes. The value literal gets a datatype, whose
     * URI points to the VTimezone object defined in the timezone database
     * under http://www.w3.org/2002/12/cal/tzd/
     * 
     * @see http://www.w3.org/2002/12/cal/tzd/
     * 
     * <pre>
     * ical:
     * RDATE:19980118T073000Z
     * 
     * n3:
     * _:VEventNode icaltzd:rdate "1998-01-18T07:30:00Z"^^<"&xsd;#datetime">
     * 
     * ical:
     * RDATE:VALUE=DATE;20020703
     * 
     * n3:
     * _:VEventNode icaltzd:rdate "2002-07-03"^^<"&xsd;#date">
     * 
     * ical:
     * RDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
     *  20020630T090000
     *  
     * n3:
     * _:VEventNode icaltzd:due
     *  "2002-06-30T09:00:00"^^<"&tzd;/America/New_York#tz">\
     *  
     * ical: 
     * RDATE;VALUE=DATE:19970304,19970504,19970704,19970904
     * 
     * n3:
     * _:VEventNode icaltzd:rdate "1997-03-04"^^<"&xsd;#date">
     * _:VEventNode icaltzd:rdate "1997-05-04"^^<"&xsd;#date">
     * _:VEventNode icaltzd:rdate "1997-07-04"^^<"&xsd;#date">
     * _:VEventNode icaltzd:rdate "1997-09-04"^^<"&xsd;#date">
     * <pre>
     */
    private void crawlRDateProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        List<Value> valueList
                = getMultipleRdfPropertyValues(property,IcalDataType.DATE_TIME);
        addMultipleStatements(rdfContainer,parentNode, ICALTZD.rdate, valueList);
    }
    
    private void crawlRecurrenceIdProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the recurrenceid property crawling
    }
    
    private void crawlRelatedToProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.relatedTo,
                property.getValue());
    }
    
    private void crawlRepeatProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the repeat property crawling
    }
    
    private void crawlRequestStatusProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.requestStatus,
                property.getValue());
    }
    
    private void crawlResourcesProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.resources,
                property.getValue());
    }
    
    /**
     * Crawls the RRULE property.
     * Possible parameters: experimental<br>
     * Treatment: blank node<br>
     * 1st link: icaltzd:rrule<br>
     * <p>
     * 
     * This property has RECUR value type. This neccessitates an introduction
     * of an intermediate blank node. The reccurrence parameters are attached
     * to this intermediate blank node (as literals with appropriate datatype)
     * 
     * <pre>
     * 
     * ical:
     * RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10
     * 
     * n3:
     * _:VeventNode icaltzd:rrule _:rruleNode
     * _:rruleNode icaltzd:bymonth 10;
     *             icaltzd:freq    YEARLY;
     *             icaltzd:interval 1^^<"&xsd;integer">
     *             icaltzd:bymonth -1SU
     * </pre>
     * 
     * @see crawlRecur()
     */
    private void crawlRRuleProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        Resource rruleBlankNode = generateAnonymousNode();
        crawlRecur(property.getValue(),rruleBlankNode,rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.rrule,rruleBlankNode);
    }

    /**
     * Crawls the priority property. The RFC states that the type
     * of the property is a non-negative INTEGER. The ontology doesn't define
     * the range for this property. We leave it as an untyped literal.
     * 
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlSequenceProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatementUntyped(rdfContainer, parentNode, ICALTZD.status,
                property.getValue());
    }
    
    private void crawlStatusProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.status,
                property.getValue());

    }
    
    private void crawlSummaryProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.summary,
                property.getValue());

    }
    
    private void crawlTranspProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.transp,
                property.getValue());
    }
    
    /**
     * Crawls the TRIGGER property.
     * Possible parameters: numerous<br>
     * Treatment: blank node (always)
     * 1st link: icaltzd:trigger
     * 
     * This is a multi-valued property. Possible types are DURATION (default)
     * and DATE-TIME
     * 
     * <pre>
     * ical:
     * TRIGGER;RELATED=START:-PT30M
     * 
     * n3:
     * _:ValarmNode icaltzd:trigger _:triggerNode . 
     * _:triggerNode icaltzd:related START .
     * _:triggerNode icaltzd:value "-PT30M"^^<"&xsd;#duration">
     * 
     * ical:
     * TRIGGER;VALUE=DATE-TIME:20060412T230000Z
     * 
     * n3:
     * _:ValarmNode icaltzd:trigger _:triggerNode . 
     * _:triggerNode icaltzd:value "2006-04-12T23:00:00Z"^^<"&xsd;#datetime">
     * </pre>
     * Note that the date-time value is converted from the ical form, to the
     * form defined in the xsd datetime datatype specification.
     */
    private void crawlTriggerProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        Resource triggerValue = crawlParameterList(property,rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.trigger,triggerValue);
        Value propertyValue 
                = getRdfPropertyValue(property,IcalDataType.DURATION);
        addStatement(rdfContainer,triggerValue,ICALTZD.value,propertyValue);
    }
   
    private void crawlTzidProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.tzid,
                property.getValue());
    }
    
    private void crawlTzNameProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.tzname,
                property.getValue());
    }
    
    private void crawlTzOffsetFromProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.tzoffsetfrom,
                property.getValue());
    }
    
    private void crawlTzOffsetToProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.tzoffsetto,
                property.getValue());
    }
    
    private void crawlTzUrlProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the tzurl property crawling
    }
    
    private void crawlUidProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.uid,
                property.getValue());
    }
    
    private void crawlUrlProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the url property crawling
    }
    
    private void crawlVersionProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.version,
                property.getValue());

    }
    
    private void crawlXtendedProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        /*URI extendedPropertyURI = new URIImpl(extendedNameSpace + "#"
                + property.getName());
        Value propertyBlankNode = crawlParameterList(property,rdfContainer);
        
        addStatement(rdfContainer, parentNode, extendedPropertyURI,
                    propertyValue);*/
    }
    
    //////////////////////////// PARAMETERS ////////////////////////////////
    
    private void crawlAltRepParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.altrep,
                parameter.getValue());
    }
    
    /**
     * Crawls the CN parameter.
     * All parameters introduce a single triple attached to the blank node for
     * the property in question.
     * 
     * <pre>
     * ical:
     * ORGANIZER;CN="John Smith":MAILTO:jsmith@host.com
     * 
     * n3:
     * _:VeventNode icaltzd:organizer _:organizerBNode .
     * _:organizerBNode icaltzd:cn John Smith ;
     *                  icaltzd:calAddress MAILTO:jsmith@host.com
     * </pre>
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlCnParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.cn,parameter.getValue());
    }
    
    /**
     * Crawls the CUTYPE parameter.
     * All parameters introduce a single triple attached to the blank node for
     * the property in question.
     * 
     * <pre>
     * ical:
     * ATTENDEE;CUTYPE=GROUP:MAILTO:ietf-calsch@imc.org
     * 
     * n3:
     * _:VeventNode icaltzd:attendee _:attendeeBNode .
     * _:attendeeBNode icaltzd:cutype GROUP ;
     *                 icaltzd:calAddress MAILTO:ietf-calsch@imc.org
     * </pre>
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlCuTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.cutype,
                parameter.getValue());
    }
    
    private void crawlDelegatedFromParameter(Parameter parameter,
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.delegatedFrom,
                parameter.getValue());
    }
    
    private void crawlDelegatedToParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.delegatedTo,
                parameter.getValue());
    }
    
    private void crawlDirParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.dir,
                parameter.getValue());
    }
    
    private void crawlEncodingParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.encoding,
                parameter.getValue());
    }
    
    private void crawlFbTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.fbtype,
                parameter.getValue());
    }
    
    private void crawlFmtTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.fmttype,
                parameter.getValue());
    }
    
    private void crawlLanguageParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.language,
                parameter.getValue());
    }
    
    private void crawlMemberParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.member,
                parameter.getValue());
    }
    
    private void crawlPartStatParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.partstat,
                parameter.getValue());
    }
    
    private void crawlRangeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.range,
                parameter.getValue());
    }
    
    private void crawlRelatedParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.related,
                parameter.getValue());
    }
    
    private void crawlRelTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.reltype,
                parameter.getValue());
    }
    
    private void crawlRoleParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.role,
                parameter.getValue());
    }
    
    private void crawlRsvpParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.role,
                parameter.getValue());
    }
    
    private void crawlSentByParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.role,
                parameter.getValue());
    }
    
    /**
     * Note that this parameter is ignored in the icaltzd ontology. It is
     * treated differently. If a DATE-TIME property (like DTSTART) has this
     * parameter it receives a datatype, whose uri points to the VTimezone
     * object in Dan Connoly's timezone resource.
     * 
     * @seeAlso http://www.w3.org/2002/12/cal/tzd/
     * 
     * @param parameter
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlTzidParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // do nothing
    }
    
    /**
     * Note that this parameter is ignored in the icaltzd ontology. It is 
     * treated differently.
     * 
     * @see getRdfPropertyValue
     * @param parameter
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlValueParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // do nothing
    }
    
    private void crawlXParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the extended parameter crawling
    }
    
    ////////////////////////////////////////////////////////////////////////
    ///////////////////////// RECURRENCE RULES /////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    private void crawlRecur(String recurString, Resource rruleBlankNode, 
            RDFContainer rdfContainer) {
        String[] recurTokens = recurString.split("[=;]");
        for (int i = 0; i<recurTokens.length; i += 2) {
            crawlRecurrenceParam(recurTokens[i],recurTokens[i+1],rruleBlankNode,
                    rdfContainer);
        }
    }
    
    private void crawlRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        if (name.equals("FREQ")) {
            crawlFreqRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("UNTIL")) {
            crawlUntilRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("INTERVAL")) {
            crawlIntervalRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYSECOND")) {
            crawlBySecondRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYMINUTE")) {
            crawlByMinuteRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYHOUR")) {
            crawlByHourRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYDAY")) {
            crawlByDayRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYMONTHDAY")) {
            crawlByMonthdayRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYYEARDAY")) {
            crawlByYeardayRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYWEEKNO")) {
            crawlByWeeknoRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYMONTH")) {
            crawlByMonthRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("BYSETPOS")) {
            crawlBySetposRecurrenceParam(name,value,parentNode,rdfContainer);
        } else if (name.equals("WKST")) {
            crawlWkstRecurrenceParam(name,value,parentNode,rdfContainer);
        }
    }
    
    private void crawlFreqRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.freq,value);
    }
    
    private void crawlUntilRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.until,value);
    }

    private void crawlIntervalRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(value, XMLSchema.INTEGER);
        addStatement(rdfContainer,parentNode,ICALTZD.interval,literal);
    }

    private void crawlBySecondRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.bysecond,value);
    }

    private void crawlByMinuteRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.byminute,value);
    }

    private void crawlByHourRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.byhour,value);
        
    }

    private void crawlByDayRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.byday,value);
    }

    private void crawlByMonthdayRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.bymonthday,value);
    }

    private void crawlByYeardayRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.byyearday,value);        
    }

    private void crawlByWeeknoRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.byweekno,value);
    }

    private void crawlByMonthRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.bymonth,value);   
    }

    private void crawlBySetposRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.bysetpos,value);
    }

    private void crawlWkstRecurrenceParam(String name, String value, 
            Resource parentNode, RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.wkst,value);        
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////// CONVENIENCE METHODS ////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the URI of the current calendar. It used in methods that crawl
     * particular components to add the triple, that binds the component with
     * the calendar.
     * 
     * @return the URI of the current calendar
     */
    private URI generateCalendarUri() {
        return new URIImpl(baseuri + "VCalendar");
    }
    
    
    /**
     * Prepare an RDF container and notify the handler about accessing the new
     * DataObject. 
     * 
     * @param component
     *            The component about which the information will be gathered.
     * @return the container, where the statements about the given uri should be
     *         stored
     */
    private RDFContainer prepareDataObjectRDFContainer(Component component) {
        URI uri = generateComponentUri(component);
        return prepareDataObjectRDFContainer(uri);
    }

    /**
     * Prepare an RDF container and notify the handler about accessing the new
     * DataObject. This container may be used to crawl "normal" calendar
     * components, those that occur on the component list by themselves, and
     * will be returned as full-blown data objects. (VEvents, VTimezones etc.)
     * 
     * @param uri
     *            The central URI for the new RDFContainer
     * @return the container, where the statements about the given uri should be
     *         stored
     */
    private RDFContainer prepareDataObjectRDFContainer(URI uri) {
        // register that we're processing this calendar component
        handler.accessingObject(this, uri.toString());
        return prepareRDFContainer(uri);
    }

    /**
     * Prepare an RDFContainer without notifying the handler about accessing the
     * new object. This container may be used to crawl embedded calendar
     * components i.e. those that don't occur on the component list by
     * themselves, but are embedded within other components such as VAlarms, or
     * VTimezone observances.
     * 
     * @param uri
     *            The central URI for the new RDFContainer.
     * @return the container where the statements about the given uri should be
     *         stored
     */
    private RDFContainer prepareRDFContainer(URI uri) {
        // fetch a RDFContainerFactory from the handler (note: this is done for
        // every component)
        RDFContainerFactory containerFactory 
                = handler.getRDFContainerFactory(this, uri.toString());
        RDFContainer rdfContainer = containerFactory
                .getRDFContainer(uri);
        return rdfContainer;
    }
    
    /**
     * Builds a statement from the provided ingredients and adds it to the
     * given rdfContainer. Treats the given string as an untyped literal.
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatementUntyped(RDFContainer rdfContainer, 
            Resource subject, URI predicate, String object) {
        addStatement(rdfContainer,subject,predicate,valueFactory.createLiteral(object));
    }
    
    /**
     * Builds a statement from the provided ingredients and adds it to the
     * given rdfContainer. Treats the given string as an untyped string literal.
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, 
            Resource subject, URI predicate, String object) {
        addStatement(rdfContainer,subject,predicate,
                valueFactory.createLiteral(object));
    }
    
    /**
     * Builds a statement from the provided ingredients and adds it to the
     * given rdfContainer.
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, Resource subject,
            URI predicate, Value object) {
        Statement statement = new StatementImpl(subject, predicate, object);
        rdfContainer.add(statement);
    }
    
    private void addMultipleStatements(RDFContainer rdfContainer, 
            Resource parentNode, URI predicate, List<Value> valueList) {
        for (Value value : valueList) {
            addStatement(rdfContainer,parentNode,predicate,value);
        }
    }

    /**
     * Creates a DataObject that encapsulates the given metadata and passes it
     * to the CrawlerHandler. The main URI for the DataObject is extracted from
     * the metadata.
     * 
     * @param metadata
     *            The RDFContainer that contains triples to be passed to the
     *            handler.
     */
    private void passComponentToHandler(RDFContainer metadata) {
        DataObject dataObject = new DataObjectBase(metadata.getDescribedUri(),
                getDataSource(), metadata);
        handler.objectNew(this, dataObject);
    }
    
    /**
     * Generates an appropriate URI for the component. Uses the UID field if
     * present. If not, generates a hash-value from existing fields.
     * 
     * @param component
     *            the component, for which the URI should be generated.
     * @return the string with the URI.
     */
    private URI generateComponentUri(Component component) {
        Property uidProperty = component.getProperty(Property.UID);
        String result = null;
        if (uidProperty != null) {
            result = baseuri + component.getName() + "-" + uidProperty.getValue(); 
        } else {
            String sumOfAllProperties = "";
            PropertyList propertyList = component.getProperties();
            Iterator it = propertyList.iterator();
            while (it.hasNext()) {
                Property property = (Property) it.next();
                sumOfAllProperties += property.getValue();
            }
            result = baseuri + component.getName() + "-"
                    + sha1Hash(sumOfAllProperties);
        }
        return new URIImpl(result);
    }
    
    /**
     * Generates a URI for an anonymous component. This one is used for embedded
     * components - like valarms. 
     * @param component
     * @return
     */
    private URI generateAnonymousComponentUri(Component component) {
        String result = baseuri + component.getName() + "-" 
                + java.util.UUID.randomUUID().toString();
        return new URIImpl(result);
    }
    
    private Resource generateAnonymousNode() {
        if (realBlankNodes) {
            return valueFactory.createBNode(
                   java.util.UUID.randomUUID().toString());
        } else {
            return valueFactory.createURI(
                   baseuri + java.util.UUID.randomUUID().toString());
        }
        
    }
    
    /**
     * Generates the extendedNameSpace that will be used for all extended
     * properties throughout the calendar.
     * <p>
     * The code has been 'borrowed' from the mimedir-parser available from
     * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
     * 
     * <p>
     * For more information see the links below
     * 
     * <p>
     * 
     * @see http://rdfig.xmlhack.com/2003/02/26/2003-02-26.html#1046279854.884486<br>
     * @see http://ilrt.org/discovery/chatlogs/rdfig/2003-02-26.html#T17-21-04<br>
     * @see http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
     * 
     * 
     */
    private String generateExtendedNameSpace(String prodid) {
        String processed = processSpaces(prodid.substring(3));
        String sha1 = sha1Hash(prodid.toString());
        String result = productNamespacePrefix + "/"
                + processed.substring(0, 10) + "_" + sha1.substring(0, 5);
        return result;
    }

    /**
     * Processes the string for use in the generation of extendedNamespace. URI
     * <p>
     * The code has been 'borrowed' from the mimedir-parser available from
     * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
     * 
     * @see generateExtendedNameSpace(String prodid)
     * @param name
     *            The product id to be processed.
     * @return The processed product id string.
     */
    private String processSpaces(String name) {
        while (name.indexOf(" ") != -1) {
            int i = name.indexOf(" ");
            String s = name.substring(0, i) + "_" + name.substring(i + 1);
            name = s;
        }
        while (name.indexOf("//") != -1) {
            int j = name.indexOf("//");
            String s = name.substring(0, j) + "_" + name.substring(j + 2);
            name = s;
        }
        return name;
    }

    /**
     * Computes the SHA1 hash for use in generation of extendedNamespace URI.
     * <p>
     * The code has been 'borrowed' from the mimedir-parser available from
     * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
     * 
     * @see generateExtendedNameSpace(String prodid);
     * @param string
     *            The string for which we'd like to get the SHA1 hash.
     * @return The generated SHA1 hash
     */

    private String sha1Hash(String string) {

        string = string.toLowerCase();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(string.getBytes());
            byte[] digest = md.digest();
            BigInteger integer = new BigInteger(1, digest);
            return integer.toString(16);
        } catch (Exception e) {
            return null;
        }
    }
    
    List<Value> getMultipleRdfPropertyValues(Property property,
            String defaultType) {
        String totalPropertyValue = property.getValue();
        if (totalPropertyValue == null) {
            return null;
        }
        String [] valuesArray = totalPropertyValue.split(",");
        List<Value> resultList = new LinkedList<Value>();
        for (int i = 0; i < valuesArray.length; i++) {
            Value currentValue 
                    = getRdfPropertyValue(
                      valuesArray[i],
                      property.getParameter(Parameter.TZID),
                      property.getParameter(Parameter.VALUE),
                      defaultType);
            resultList.add(currentValue);
        }
        return resultList;
    }
    
    private Value getRdfPropertyValue(Property property,String defaultType) {
        return getRdfPropertyValue(
                property.getValue(),
                property.getParameter(Parameter.TZID),
                property.getParameter(Parameter.VALUE),
                defaultType);
    }
    
    private Value getRdfPropertyValue(
            String propertyValue,Parameter tzidParameter, 
            Parameter valueParameter,String defaultType) {
        if (tzidParameter != null && (
            valueParameter == null 
            && defaultType.equals(IcalDataType.DATE_TIME) 
            || valueParameter != null
            && valueParameter.getValue().equals(IcalDataType.DATE_TIME))){
            return getDateTimeWithTimeZone(propertyValue,tzidParameter);
        }
                
        // Parameter valueParameter = property.getParameter(Parameter.VALUE);        
        Literal literal = null;
        URI datatypeURI = null;
        String rdfPropertyValue = null;
        if (valueParameter != null) {
            String valueParameterString 
                    = valueParameter.getValue();
            datatypeURI
                    = convertValueParameterToXSDDatatype(valueParameterString);
            rdfPropertyValue 
                    = convertIcalValueToXSDValue(propertyValue,
                      valueParameterString);
            literal = valueFactory.createLiteral(rdfPropertyValue,datatypeURI);
        } else if (defaultType != null) {
            datatypeURI 
                    = convertValueParameterToXSDDatatype(defaultType);
            rdfPropertyValue 
                    = convertIcalValueToXSDValue(propertyValue,
                      defaultType);
            literal = valueFactory.createLiteral(rdfPropertyValue,datatypeURI);
        } else {
            literal = valueFactory.createLiteral(propertyValue);
        }   
        return literal;
    }
    
    private Value getDateTimeWithTimeZone(String icalValue, 
            Parameter tzidParameter) {
        String rdfPropertyValue 
                = convertIcalDateTimeToXSDDateTime(icalValue);
        URI timezoneDatatypeURI 
                = createTimeZoneDatatypeURI(tzidParameter.getValue());
        Value result 
                = valueFactory.createLiteral(rdfPropertyValue, 
                  timezoneDatatypeURI);
        return result;
    }

    private URI createTimeZoneDatatypeURI(String value) {
        int lastSlashPosition 
                = value.lastIndexOf("/");
        if (lastSlashPosition == -1) {
            // completely unknown naming scheme for timezones
            return valueFactory.createURI("timezone://" + value);
        }
        int oneBeforeLastSlashPosition 
                = value.lastIndexOf("/", lastSlashPosition - 1);
        if (oneBeforeLastSlashPosition == -1) {
            // this is to support simple TZID=Europe/London identifiers
            return valueFactory.createURI(timezoneNamespacePrefix
                   + value + "#tz");
        } else {
            // this is to support full Olson identifiers like:
            // TZID=/softwarestudio.org/Olson_20011030_5/America/New_York
            String timezoneName 
                    = value.substring(oneBeforeLastSlashPosition);
            return valueFactory.createURI(timezoneNamespacePrefix 
                    + timezoneName + "#tz"); 
        }
    }

    private String convertIcalValueToXSDValue(String value, 
            String icalDataType) {
        if (icalDataType == null) {
            return value;
        } else if (icalDataType.equals(IcalDataType.DATE_TIME)) {
            return convertIcalDateTimeToXSDDateTime(value);
        } else if (icalDataType.equals(IcalDataType.DATE)) {
            return convertIcalDateToXSDDate(value);
        } else {
            return value;
        }
    }
    
    /**
     * Converts the ical date (YYYYMMDD) to an XSD Date (YYYY-MM-DD)
     * @param icalDate The ical date to convert.
     * @return The XSD date.
     */
    private String convertIcalDateToXSDDate(String icalDate) {
        if (icalDate.length() != 8) {
            throw new IllegalArgumentException("Invalid ical date: " + icalDate);
        }
        String year = icalDate.substring(0,4);
        String month = icalDate.substring(4,6);
        String day = icalDate.substring(6,8);
        return year + "-" + month + "-" + day;
    }
    
    /**
     * Converts the ical date (YYYYMMDD) to an XSD Date (YYYY-MM-DD)
     * @param icalDate The ical date to convert.
     * @return The XSD date.
     */
    
    private String convertIcalDateTimeToXSDDateTime(String icalDateTime) {
        if (icalDateTime.length() < 15 || icalDateTime.length() > 16) {
            throw new IllegalArgumentException("Invalid ical date: " + 
                    icalDateTime);
        }
        String date = convertIcalDateToXSDDate(icalDateTime.substring(0,8));
        // we omit the 'T' letter in the middle
        String hour = icalDateTime.substring(9,11);
        String minute = icalDateTime.substring(11,13);
        String second = icalDateTime.substring(13,15);
        String z = (icalDateTime.length() == 16) ? "Z" : "";
        return date + "T" + hour + ":" + minute + ":" + second + z;
    }
    
    private URI convertValueParameterToXSDDatatype(String valueString) {
        URI datatypeURI = null;
        if (valueString.equalsIgnoreCase("TEXT")) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase("BINARY")) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase("BOOLEAN")) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase(IcalDataType.CAL_ADDRESS)) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase(IcalDataType.DATE)) {
            datatypeURI = XMLSchema.DATE;
        } else if (valueString.equalsIgnoreCase(IcalDataType.DATE_TIME)) {
            datatypeURI = XMLSchema.DATETIME;
        } else if (valueString.equalsIgnoreCase(IcalDataType.DURATION)) {
            datatypeURI = XMLSchema.DURATION;
        } else if (valueString.equalsIgnoreCase("FLOAT")) {
            datatypeURI = XMLSchema.FLOAT;
        } else if (valueString.equals(IcalDataType.INTEGER)) {
            datatypeURI = XMLSchema.INTEGER;
        } else if (valueString.equalsIgnoreCase(IcalDataType.PERIOD)) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase("RECUR")) {
            datatypeURI = null;
        } else if (valueString.equalsIgnoreCase("TIME")) {
            datatypeURI = XMLSchema.TIME;
        } else if (valueString.equalsIgnoreCase("URI")) {
            datatypeURI = XMLSchema.ANYURI;
        } else if (valueString.equalsIgnoreCase("UTC-OFFSET")) {
            datatypeURI = null;
        } else {
            LOGGER.severe("Unknown value parameter: " + valueString);
        }
        return datatypeURI;
    }
}
