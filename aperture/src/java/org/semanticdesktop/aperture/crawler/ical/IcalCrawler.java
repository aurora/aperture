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
            baseuri = icalFile.getCanonicalPath() + "#";
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't get the canonical path " +
                    "for the iCalFile",e);
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
     * @param propsedValueURI
     *            The default URI for the link that will connect the
     *            generated blank node with the actual value of the property.
     *            It is used if there is no VALUE parameter. If it is NULL and
     *            the VALUE parameter is unspecified - the property value will
     *            be attached to the blank node with a default ICALTZD.value
     *            link.
     */
    private Value crawlParameterList(Property property,
            RDFContainer rdfContainer, URI proposedValueURI) {
        ParameterList parameterList = property.getParameters();
        if (parameterList.size() == 0) {
            return valueFactory.createLiteral(property.getValue()); 
        } else {
            String propertyValue = property.getValue();
            URI propertyBlankNode = generateAnonymousNode();
            if (propertyValue != null) {
                Parameter valueParameter = property
                        .getParameter(Parameter.VALUE);
                URI valuePropertyURI = null;
                if (valueParameter != null) {
                    String valueString = valueParameter.getValue();
                    if (valueString.equalsIgnoreCase("TEXT")) {
                        // special case: if the VALUE:TEXT is the only parameter
                        // we don't introduce an intermediary blank node for
                        property.getParameters().remove(valueParameter);
                        if (property.getParameters().size() == 0) {
                            return valueFactory
                                    .createLiteral(property.getValue());
                        } 
                    }
                    valuePropertyURI = convertValueTypeToURI(valueString);
                } else if (proposedValueURI != null) {
                    valuePropertyURI = proposedValueURI;
                } else {
                    valuePropertyURI = ICALTZD.value;
                }
                addStatement(rdfContainer, propertyBlankNode, valuePropertyURI,
                        propertyValue);            
            }
            // at the end we may crawl the parameter list to add the remaining
            // parameters
            crawlParameterList(parameterList, propertyBlankNode, rdfContainer);
            return propertyBlankNode;
        }
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

    /**
     * Crawls the ACTION property.
     * Only non-standard parameters can be specified on this property. This
     * method introduces a single triple.
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
    
    private void crawlAttachProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        addStatement(rdfContainer, parentNode, ICALTZD.attach, 
                property.getValue());
    }
    
    /**
     * This property may contain parameters. It introduces a blank node. The
     * value of this property is connected to the blank node with the
     * icaltzd:caladdress link. This blank node is connected to the parent
     * node with the icaltzd:attendee link.
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
       Value value = crawlParameterList(property, rdfContainer, 
               ICALTZD.calAddress);
       addStatement(rdfContainer,parentNode,ICALTZD.attendee,value);
    }
    
    /**
     * Crawls the CALSCALE property.
     * Only non-standard parameters can be specified on this property. This
     * method introduces a single triple.
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
     * Crawls the CATEGORIES property.
     * The LANGUAGE parameter may be specified on this property.
     * We DISREGARD this parameter and introduce a single triple.
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
     * Crawls the CLASS property.
     * Only non-standard parameters can be specified on this property. This
     * method introduces a single triple.
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
     * Crawls the COMMENT property.
     * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
     * Treatment: direct link
     * 1st link: icaltzd:comment
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
    
    private void crawlCompletedProperty(Property property,Resource parentNode,
            RDFContainer rdfContainer) {
        Literal literal = valueFactory.createLiteral(property.getValue(),
                ICALTZD.Value_DATETIME);
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
                ICALTZD.Value_DATETIME);
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
                ICALTZD.Value_DATETIME);
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
                ICALTZD.Value_DATETIME);
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
        Value propertyValue = crawlParameterList(property,rdfContainer,
                ICALTZD.value);
        addStatement(rdfContainer, parentNode, extendedPropertyURI,
                    propertyValue);
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
        // TODO implement the tzid parameter crawling
    }
    
    /**
     * Note that this parameter is ignored in the icaltzd ontology. It is 
     * treated differently. The algorithm is as follows: 
     * <p>
     * <ul>
     *      <li>if the parameter value is equal to TEXT
     *          <ul>
     *          <li>if this is the only parameter for this property
     *              <ul>
     *              <li>we don't introduce any blank nodes, the value of the
     *                  property is attached to the parent node with a direct
     *                  link
     *              </ul>
     *          <li>else
     *              <ul>
     *              <li>we introduce a blank node and attach the value of the
     *                  property to that blank node with a ICALTZD.value link
     *              </ul>
     *          </ul>
     *       <li>else
     *           <ul>
     *           <li> we introduce a blank node, attach the remaining parameters
     *                to that blank node, then we attach the actual property
     *                value (if it exists) with a link, whose URI is returned
     *                by the convertValueTypeToURI method
     *           </ul>
     * </ul>  
     * @param parameter
     * @param parentNode
     * @param rdfContainer
     */
    private void crawlValueParameter(Parameter parameter, Resource parentNode,
            RDFContainer rdfContainer) {
        // we don't add any statements, the value parameter is expressed by
        // various Value_... properties
        // addStatement(rdfContainer,parentNode,
        //        ICALTZD.value,parameter.getValue());
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
    
    private URI convertValueTypeToURI(String valueString) {
        URI valuePropertyURI = null;
        if (valueString.equalsIgnoreCase("TEXT")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("BINARY")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("BOOLEAN")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("CAL-ADDRESS")) {
            valuePropertyURI = ICALTZD.calAddress;
        } else if (valueString.equalsIgnoreCase("DATE")) {
            valuePropertyURI = ICALTZD.Value_DATE;
        } else if (valueString.equalsIgnoreCase("DATE-TIME")) {
            valuePropertyURI = ICALTZD.Value_DATETIME;
        } else if (valueString.equalsIgnoreCase("DURATION")) {
            valuePropertyURI = ICALTZD.Value_DURATION;
        } else if (valueString.equalsIgnoreCase("FLOAT")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equals("INTEGER")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("PERIOD")) {
            valuePropertyURI = ICALTZD.Value_PERIOD;
        } else if (valueString.equalsIgnoreCase("RECUR")) {
            valuePropertyURI = ICALTZD.Value_RECUR;
        } else if (valueString.equalsIgnoreCase("TIME")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("URI")) {
            valuePropertyURI = ICALTZD.value;
        } else if (valueString.equalsIgnoreCase("UTC-OFFSET")) {
            valuePropertyURI = ICALTZD.value;
        } else {
            LOGGER.severe("Unknown value parameter: " + valueString);
        }
        return valuePropertyURI;
    }
}
