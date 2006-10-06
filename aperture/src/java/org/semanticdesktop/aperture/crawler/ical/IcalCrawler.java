/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Iterator;
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

    /**
     * The prefix, common to all ical product namespaces. Forms the basis from
     * which the extendedNamespaceIsGenerated
     * 
     * @see IcalCrawler#generateExtendedNamespace(String)
     */
    private static final String productNamespacePrefix = 
            "http://www.w3.org/2002/12/cal/prod";
    
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
        if (icalFilePath == null) {
            // treat this as an error rather than an "empty source" to prevent
            // information loss
            LOGGER.log(Level.SEVERE,
                    "missing iCalendar file path specification");
            return ExitCode.FATAL_ERROR;
        }
        baseuri = icalFilePath + "#";
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

        // crawl the ical file
        return crawlIcalFile(icalFile);
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
        String uri = generateCalendarUri();
        RDFContainer rdfContainer = prepareDataObjectRDFContainer(uri);
        rdfContainer.add(RDF.TYPE, ICALTZD.Vcalendar);

        // we have to process the prodid first, to compute the extendedNamespace
        // before any extended properties come on the propertyList

        ProdId prodId = calendar.getProductId();
        if (prodId != null) {
            rdfContainer.add(ICALTZD.prodid, prodId.getValue());
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
     * Crawls the parameter list of a given property.
     * 
     * @param property
     *            The property whose parameter list we would like to crawl.
     * @param parentNode
     *            The node to which the generated RDF subtree should be
     *            attached.
     * @param parametersRDFContainer
     *            The RDFContainer to store the triples in.
     */
    private void crawlParameterList(Property property,
            Resource parentNode, RDFContainer parametersRDFContainer) {
        ParameterList parameterList = property.getParameters();
        crawlParameterList(parameterList,parentNode,parametersRDFContainer);
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
     * Crawls a single VAlarm component.
     * 
     * @param component
     * @param rdfContainer
     */
    private void crawlVAlarmComponent(Component component, Resource parentNode,
            RDFContainer rdfContainer) {
        VAlarm valarm = (VAlarm) component;
        URI valarmParentNode = generateAnonymousNode();
        crawlPropertyList(valarm, valarmParentNode,rdfContainer);
        addStatement(rdfContainer,parentNode,ICALTZD.Valarm,valarmParentNode);
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
        URI standardParentNode = generateAnonymousNode();
        crawlPropertyList(component, standardParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.standard,
                standardParentNode);
    }
    
    private void crawlDaylightObservance(Component component,
            Resource parentNode, RDFContainer rdfContainer) {
        URI daylightParentNode = generateAnonymousNode();
        crawlPropertyList(component, daylightParentNode, rdfContainer);
        addStatement(rdfContainer, parentNode, ICALTZD.daylight,
                daylightParentNode);
    }
    
    /////////////////////////// PROPERTIES /////////////////////////////////
    
    private void crawlActionProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.action, 
                property.getValue());
    }
    
    private void crawlAttachProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.attach, 
                property.getValue());
    }
    
    private void crawlAttendeeProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the attendee property crawling
    }
    
    private void crawlCalScaleProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.calscale,
                property.getValue());
    }
    
    private void crawlCategoriesProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.categories,
                property.getValue());
    }
    
    private void crawlClassProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.class_,
                property.getValue());

    }
    
    private void crawlCommentProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.comment,
                property.getValue());

    }
    
    private void crawlCompletedProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(property.getValue(),
                ICALTZD_ADD_VOCABULARY.Value_DATETIME);
        addStatement(rdfContainer, parentNode, ICALTZD.comment,literal);
    }
    
    private void crawlContactProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.contact,
                property.getValue());
    }
    
    private void crawlCreatedProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(property.getValue(),
                ICALTZD_ADD_VOCABULARY.Value_DATETIME);
        addStatement(rdfContainer, parentNode, ICALTZD.created, literal);
    }
    
    private void crawlDescriptionProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.description,
                property.getValue());
    }
    
    private void crawlDtEndProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the dtend property crawling
    }
    
    private void crawlDtStampProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(property.getValue(),
                ICALTZD_ADD_VOCABULARY.Value_DATETIME);
        addStatement(rdfContainer, parentNode, ICALTZD.dtstamp, literal);
    }
    
    private void crawlDtStartProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the date property crawling
    }
    
    private void crawlDueProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the due property crawling
    }
    
    private void crawlDurationProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the duration property crawling
    }
    
    private void crawlExDateProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the exdate property crawling
    }
    
    private void crawlExRuleProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the exrule property crawling
    }
    
    private void crawlFreeBusyProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the freebusy property crawling
    }
    
    private void crawlGeoProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the geo property crawling
    }
    
    private void crawlLastModifiedProperty(Property property,
            Resource parentNode, RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(property.getValue(),
                ICALTZD_ADD_VOCABULARY.Value_DATETIME);
        addStatement(rdfContainer, parentNode, ICALTZD.lastModified, literal);
    }
    
    private void crawlLocationProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.location,
               property.getValue());
    }
    
    private void crawlMethodProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.method,
                property.getValue());
    }
    
    private void crawlOrganizerProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the organizer property crawling
    }
    
    /**
     * Crawls the percentComplete property. The RFC states that the type
     * of the property is INTEGER, and allowed values are between 0 and 100
     * inclusive. The ontology doesn't define the range for this property.
     * We leave it as an untyped literal.
     * 
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlPercentCompleteProperty(Property property,
            Resource parentNode, RDFContainer rdfContainer) {
        addStatementUntyped(rdfContainer,parentNode,ICALTZD.percentComplete, 
                property.getValue());
    }
    
    /**
     * Crawls the priority property. The RFC states that the type
     * of the property is INTEGER, and allowed values are between 0 and 9
     * inclusive. The ontology doesn't define the range for this property.
     * We leave it as an untyped literal.
     * 
     * @param property
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlPriorityProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatementUntyped(rdfContainer,parentNode,ICALTZD.priority, 
                property.getValue());
    }
    
    private void crawlProdIdProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.prodid,
                property.getValue());
    }
    
    private void crawlRDateProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        //  TODO implement the rdate property crawling
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
    
    private void crawlRRuleProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the rrule property crawling
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
    
    /** Trigger - Multityped property. */
    private void crawlTriggerProperty(Property property, Resource parentNode,
            RDFContainer rdfContainer) {
        URI triggerBNode = generateAnonymousNode();
        URI valueURI = 
            new URIImpl(ICALTZD.NS + property.getParameter(Parameter.VALUE));
        addStatement(rdfContainer,triggerBNode,valueURI,property.getValue());
        addStatement(rdfContainer,parentNode,ICALTZD.trigger,triggerBNode);
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
        URI extendedPropertyURI = new URIImpl(extendedNameSpace + "#"
                + property.getName());
        ParameterList parameterList = property.getParameters();
        if (parameterList.size() == 0) {
            addStatement(rdfContainer, parentNode, extendedPropertyURI,
                    property.getValue());
        } else {
            URI propertyBlankNode = generateAnonymousNode();
            crawlParameterList(parameterList, propertyBlankNode, rdfContainer);
            String propertyValue = property.getValue();
            if (propertyValue != null) {
                Parameter valueParameter = property
                        .getParameter(Parameter.VALUE);
                URI valuePropertyURI = null;
                if (valueParameter != null) {
                    // TODO implement the switch that would choose the correct
                    // URI for the property value
                    valuePropertyURI = new URIImpl(ICALTZD.value + "_"
                            + valueParameter.getValue());
                } else {
                    valuePropertyURI = ICALTZD.value;
                }
                addStatement(rdfContainer, propertyBlankNode, valuePropertyURI,
                        propertyValue);
            }
            addStatement(rdfContainer, parentNode, extendedPropertyURI,
                    propertyBlankNode);
        }
    }
    
    //////////////////////////// PARAMETERS ////////////////////////////////
    
    private void crawlAltRepParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the AltRep parameter crawling
    }
    
    private void crawlCnParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the cn parameter crawling
    }
    
    private void crawlCuTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the cutype parameter crawling
    }
    
    private void crawlDelegatedFromParameter(Parameter parameter,
            Resource parentNode, RDFContainer rdfContainer) {
        // TODO implement the delegatedfrom parameter crawling
    }
    
    private void crawlDelegatedToParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the delegatedto parameter crawling
    }
    
    private void crawlDirParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the dir parameter crawling
    }
    
    private void crawlEncodingParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the encoding parameter crawling
    }
    
    private void crawlFbTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the fbtype parameter crawling
    }
    
    private void crawlFmtTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the fmttype parameter crawling
    }
    
    private void crawlLanguageParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the language parameter crawling
    }
    
    private void crawlMemberParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the member parameter crawling
    }
    
    private void crawlPartStatParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the partstat parameter crawling
    }
    
    private void crawlRangeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the range parameter crawling
    }
    
    private void crawlRelatedParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the related parameter crawling
    }
    
    private void crawlRelTypeParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the reltype parameter crawling
    }
    
    private void crawlRoleParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the role parameter crawling
    }
    
    private void crawlRsvpParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the rsvp parameter crawling
    }
    
    private void crawlSentByParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the sentby parameter crawling
    }
    
    /**
     * Note that this parameter is ignored. In the tzid ontology
     * @param parameter
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlTzidParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the tzid parameter crawling
    }
    
    private void crawlValueParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer,parentNode,ICALTZD.value,parameter.getValue());
    }
    
    private void crawlXParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // TODO implement the extended parameter crawling
    }
    
    ////////////////////////////////////////////////////////////////////////
    /////////////////////// CONVENIENCE METHODS ////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the URI of the current calendar. It used in methods that crawl
     * particular components to add the triple, that binds the component with
     * the calendar.
     * 
     * 
     * @param calendar
     * @return the URI of the current calendar
     */
    private String generateCalendarUri() {
        return "VCalendar:" + icalFile.getAbsolutePath();
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
        String uri = generateComponentUri(component);
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
    private RDFContainer prepareDataObjectRDFContainer(String uri) {
        // register that we're processing this calendar component
        handler.accessingObject(this, uri);
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
    private RDFContainer prepareRDFContainer(String uri) {
        // fetch a RDFContainerFactory from the handler (note: this is done for
        // every component)
        RDFContainerFactory containerFactory = handler.getRDFContainerFactory(
                this, uri);
        RDFContainer rdfContainer = containerFactory
                .getRDFContainer(new URIImpl(uri));
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
     * given rdfContainer. Treats the given string as an typed string literal.
     * Uses the appropriate XMLSchema String datatype.
     * @param rdfContainer
     * @param subject
     * @param predicate
     * @param object
     */
    private void addStatement(RDFContainer rdfContainer, 
            Resource subject, URI predicate, String object) {
        addStatement(rdfContainer,subject,predicate,
                valueFactory.createLiteral(object,XMLSchema.STRING));
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
    private String generateComponentUri(Component component) {
        Property uidProperty = component.getProperty(Property.UID);
        if (uidProperty != null) {
            String result = baseuri + component.getName() + "-" + uidProperty.getValue();
            return result; 
        } else {
            String sumOfAllProperties = "";
            PropertyList propertyList = component.getProperties();
            Iterator it = propertyList.iterator();
            while (it.hasNext()) {
                Property property = (Property) it.next();
                sumOfAllProperties += property.getValue();
            }
            return baseuri + component.getName() + "-"
                    + sha1Hash(sumOfAllProperties);
        }
    }
    
    private URI generateAnonymousNode() {
        return new URIImpl(baseuri + java.util.UUID.randomUUID().toString());
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
}
