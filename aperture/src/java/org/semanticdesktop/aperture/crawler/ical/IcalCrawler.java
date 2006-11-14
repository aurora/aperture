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
import java.util.Collection;
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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.vocabulary.ICALTZD;

/**
 * A Crawler implementation for crawling ical calendar sources modeled by a FileSystemDataSource.
 * 
 * <p>
 * The algorithm that decides if an object has been changed or not is inefficient. It may be optimized in
 * future...
 * </p>
 */
public class IcalCrawler extends CrawlerBase {

	private static final Logger LOGGER = Logger.getLogger(IcalCrawler.class.getName());

	/** The file from which we read. */
	private File icalFile;

	/**
	 * A flag that indicates if we want to introduce real blank nodes (true), or anonymous random uris
	 * (false). Default is false.
	 */
	private boolean realBlankNodes;

	/**
	 * The prefix, common to all ical product namespaces. Forms the basis from which the
	 * extendedNamespaceIsGenerated
	 * 
	 * @see IcalCrawler#generateExtendedNamespace(String)
	 */
	private static final String productNamespacePrefix = "http://www.w3.org/2002/12/cal/prod";

	/**
	 * Address of Dan Connoly's timezone database. Used in all properties with the TZID parameter. The name of
	 * the timezone (e.g. Europe/Warsaw) is added to this prefix to form the datatype of a value (e.g.
	 * &lt;dtstart rdf:datatype="http://www.w3.org/2002/12/cal/tzd"&gt;2006-10-20T10:21:00&lt;/dtstart&gt;)
	 */
	private static final String timezoneNamespacePrefix = "http://www.w3.org/2002/12/cal/tzd";

	/**
	 * The actual namespace for extended properties. The default value is simply the productNamespacePrefix.
	 * It will be used, if there is no prodid in the current file.
	 * 
	 * @see IcalCrawler#generateExtendedNameSpace(String)
	 */
	private String extendedNameSpace = productNamespacePrefix;

	/**
	 * The base URI of current calendar instance.
	 */
	private String baseuri;

	public IcalCrawler() {
		
	}

	/**
	 * The main method that performs the actual crawl. Reads the file path from the data source configuration.
	 * 
	 * @return The ExitCode
	 */
	protected ExitCode crawlObjects() {
		// fetch the source and its configuration
		DataSource source = getDataSource();
		IcalDataSource icalDataSource = null;
		try {
			icalDataSource = (IcalDataSource) source;
		}
		catch (ClassCastException e) {
			LOGGER.log(Level.SEVERE, "unsupported data source type", e);
			return ExitCode.FATAL_ERROR;
		}

		RDFContainer configuration = icalDataSource.getConfiguration();

		// determine the root file
		String icalFilePath = ConfigurationUtil.getRootUrl(configuration);
		realBlankNodes = checkRealBlankNodes(configuration);
		if (icalFilePath == null) {
			// treat this as an error rather than an "empty source" to prevent
			// information loss
			LOGGER.log(Level.SEVERE, "missing iCalendar file path specification");
			return ExitCode.FATAL_ERROR;
		}

		icalFile = new File(icalFilePath);
		if (!icalFile.exists()) {
			LOGGER.log(Level.SEVERE, "iCalendar file does not exist: '" + icalFile + "'");
			return ExitCode.FATAL_ERROR;
		}

		if (!icalFile.canRead()) {
			LOGGER.log(Level.SEVERE, "iCalendar file cannot be read: '" + icalFile + "'");
			return ExitCode.FATAL_ERROR;
		}

		try {
			baseuri = "file://" + icalFile.getCanonicalPath() + "#";
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Couldn't get the canonical path " + "for the iCalFile", e);
			return ExitCode.FATAL_ERROR;
		}

		// crawl the ical file
		return crawlIcalFile(icalFile);
	}

	private boolean checkRealBlankNodes(RDFContainer configuration) {
		Boolean bool = configuration.getBoolean(ICALTZD.realBlankNodes);
		if (bool == null || bool.booleanValue() == false) {
			return false;
		}
		else {
			return true;
		}
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
			LOGGER.log(Level.SEVERE, "Couldn't find the calendar file", fnfe);
			return ExitCode.FATAL_ERROR;
		}
		catch (ParserException pe) {
			LOGGER.log(Level.SEVERE, "Couldn't parse the calendar file", pe);
			return ExitCode.FATAL_ERROR;
		}
		catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, "Input/Output error while parsing " + "the calendar file", ioe);
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

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////// ABSTRACT BUSINESS METHODS ///////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Crawls the calendar.
	 * 
	 * Generates a DataObject for the Calendar itself and passes it to the crawlerHandler. Then continues to
	 * crawl the entire component list.
	 * 
	 * @param calendar The calendar object.
	 * 
	 * @see #generateExtendedNameSpace(String)
	 */
	protected void crawlCalendar(Calendar calendar) {
		URI uri = generateCalendarUri();
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(uri);
		rdfContainer.add(RDF.type, ICALTZD.Vcalendar);

		// we have to process the prodid first, to compute the extendedNamespace
		// before any extended properties come on the propertyList

		ProdId prodId = calendar.getProductId();
		if (prodId != null) {
			generateExtendedNameSpace(prodId.getValue());
			extendedNameSpace = generateExtendedNameSpace(prodId.getValue());
		}

		PropertyList propertyList = calendar.getProperties();
		crawlPropertyList(propertyList, rdfContainer.getDescribedUri(), rdfContainer);

		passComponentToHandler(rdfContainer);

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
	private void crawlSingleComponent(Component component, Resource parentNode, RDFContainer rdfContainer) {
		if (component.getName().equals(Component.VALARM)) {
			crawlVAlarmComponent(component, parentNode, rdfContainer);
		}
		else if (component.getName().equals(Component.VEVENT)) {
			crawlVEventComponent(component, parentNode);
		}
		else if (component.getName().equals(Component.VFREEBUSY)) {
			crawlVFreebusyComponent(component, parentNode);
		}
		else if (component.getName().equals(Component.VJOURNAL)) {
			crawlVJournalComponent(component, parentNode);
		}
		else if (component.getName().equals(Component.VTIMEZONE)) {
			crawlVTimezoneComponent(component, parentNode);
		}
		else if (component.getName().equals(Component.VTODO)) {
			crawlVTodoComponent(component, parentNode);
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
			LOGGER.log(Level.SEVERE, "Unknown component name: " + component.getName());
		}
	}

	/**
	 * Crawls a single property. Checks the name of the property and dispatches it to an appropriate
	 * property-handling method.
	 * 
	 * @param property The property to be crawled.
	 * @param rdfContainer The rdfContainer to store the generated statements in.
	 */
	private void crawlSingleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
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
			crawlStatusProperty(property, parentNode, rdfContainer);
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
			LOGGER.log(Level.SEVERE, "Unknown property name: " + property.getName());
		}
	}

	/**
	 * Crawls a single parameter. Checks the name of the parameter and dispatches it to an appropriate
	 * parameter-handling method.
	 * 
	 * @param property The parameter to be crawled.
	 * @param rdfContainer The rdfContainer to store the generated statement in.
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
	private void crawlComponentList(ComponentList componentList, RDFContainer rdfContainer) {
		Iterator it = componentList.iterator();
		while (it.hasNext()) {
			Component component = (Component) it.next();
			crawlSingleComponent(component, rdfContainer.getDescribedUri(), rdfContainer);
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
		crawlPropertyList(propertyList, parentNode, rdfContainer);
	}

	/**
	 * Iterates over a propertyList, attaches those properties to a given parentNode, add stores the resulting
	 * triples in a given RDFContainer.
	 * 
	 * @param propertyList The property list to be crawled.
	 * @param parentNode The node, the property values should be attached to.
	 * @param rdfContainer The container to store the generated statements in.
	 */
	private void crawlPropertyList(PropertyList propertyList, Resource parentNode, RDFContainer rdfContainer) {
		Iterator it = propertyList.iterator();
		while (it.hasNext()) {
			Property property = (Property) it.next();
			crawlSingleProperty(property, parentNode, rdfContainer);
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
	 * Crawls a single VAlarm component. Attaches it to the parent vevent or vtodo with a ical:component link.
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
	 *  _:VeventNode icaltzd:component _:ValarmBNode .
	 *  _:ValarmBNode rdf:type icaltzd:valarm .
	 *  _:ValarmBNode property1 property1Value ;
	 *                ... ;
	 *                propertyN propertyNValue .
	 * </pre>
	 * 
	 * @param component The Valarm to be crawled.
	 * @param rdfContainer The container to store the generated statements in.
	 */
	protected void crawlVAlarmComponent(Component component, Resource parentNode, RDFContainer rdfContainer) {
		VAlarm valarm = (VAlarm) component;
		URI valarmParentNode = generateAnonymousComponentUri(component);
		crawlPropertyList(valarm, valarmParentNode, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, valarmParentNode);
		addStatement(rdfContainer, valarmParentNode, RDF.type, ICALTZD.Valarm);
	}

	/**
	 * Crawls the vevent component. Attaches it to the givent parent node with an ical:component link.
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
	 *  &lt;&quot;&amp;baseuriVCalendar&quot;&gt; icaltzd:component &lt;&quot;&amp;baseuri20020630T230353Z-3895-69-1-0@jammer&quot;&gt;
	 *  &lt;&quot;&amp;baseuri20020630T230353Z-3895-69-1-0@jammer&quot;&gt; rdf:type icaltzd:Vevent
	 *  &lt;&quot;&amp;baseuri20020630T230353Z-3895-69-1-0@jammer&quot;&gt; property1 property1value ; 
	 *                                                   ... ; 
	 *                                                   propertyN propertyNValue ;
	 *                                                   icalzdt:component _:ValarmBNode
	 * </pre>
	 * 
	 * @param component
	 * @param parentNode
	 */
	protected void crawlVEventComponent(Component component, Resource parentNode) {
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
		rdfContainer.add(RDF.type, ICALTZD.Vevent);
		VEvent vevent = (VEvent) component;
		crawlPropertyList(vevent, rdfContainer);
		ComponentList alarmList = vevent.getAlarms();
		crawlComponentList(alarmList, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
		passComponentToHandler(rdfContainer);
	}

	/**
	 * Crawls a single VFreebusy component.
	 * 
	 * <p>
	 * Unsupported by fromIcal.py at the time of writing (2006-10-17)
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
	 *  (note that there is no UID in this component, so the URI has to be generated otherwise)
	 *  
	 *  n3:
	 *  &lt;&quot;&amp;baseuriVCalendar&quot;&gt; icaltzd:component &lt;&quot;&amp;baseurihashOfAllProperties&quot;&gt;
	 *  &lt;&quot;&amp;baseurihashOfAllProperties&quot;&gt; rdf:type icaltzd:vfreebusy ;
	 *                                   property1 property1Value ;
	 *                                   ... ;
	 *                                   propertyN propertyNValue .
	 * </pre>
	 * 
	 * @see #generateComponentUri(Component)
	 */
	protected void crawlVFreebusyComponent(Component component, Resource parentNode) {
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
		VFreeBusy vfreebusy = (VFreeBusy) component;
		rdfContainer.add(RDF.type, ICALTZD.Vfreebusy);
		crawlPropertyList(vfreebusy, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
		passComponentToHandler(rdfContainer);
	}

	/**
	 * Crawls a single VJournal component.
	 * 
	 * <p>
	 * Unsupported by fromIcal.py at the time of writing (2006-10-17)
	 * 
	 * <pre>
	 *  ical:
	 *  BEGIN:VJOURNAL
	 *  CREATED
	 *   :20030227T110715Z
	 *  UID
	 *   :KOrganizer-948365006.348
	 *  SEQUENCE
	 *  :0
	 *  LAST-MODIFIED
	 *   :20030227T110715Z
	 *  DTSTAMP
	 *   :20030227T110715Z
	 *  ORGANIZER
	 *   :MAILTO:nobody@nowhere
	 *  DESCRIPTION
	 *   :journal\n
	 *  CLASS
	 *   :PUBLIC
	 *  PRIORITY
	 *   :3
	 *  DTSTART
	 *   ;VALUE=DATE
	 *   :20030224
	 *  END:VJOURNAL
	 *  
	 *  (note that there is no UID in this component, so the URI has to be generated otherwise)
	 *  
	 *  n3:
	 *  &lt;&quot;&amp;baseuriVCalendar&quot;&gt; icaltzd:component &lt;&quot;&amp;baseuriKOrganizer-948365006.348&quot;&gt;
	 *  &lt;&quot;&amp;baseuriKOrganizer-948365006.348&quot;&gt; rdf:type icaltzd:vjournal ;
	 *                                        property1 property1Value ;
	 *                                        ... ;
	 *                                        propertyN propertyNValue .
	 * </pre>
	 * 
	 * @see #generateComponentUri(Component)
	 */
	protected void crawlVJournalComponent(Component component, Resource parentNode) {
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
		rdfContainer.add(RDF.type, ICALTZD.Vjournal);
		VJournal vjournal = (VJournal) component;
		crawlPropertyList(vjournal, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
		passComponentToHandler(rdfContainer);
	}

	/**
	 * Crawls a single VTimezone component.
	 * 
	 * <p>
	 * Note the the URI for this component.
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
	 *  &lt;&quot;&amp;baseuriVCalendar&quot;&gt; icaltzd:component &lt;&quot;http://www.w3.org/2002/12/cal/tzd/America/New_York#tz&quot;&gt;
	 *  &lt;&quot;&amp;baseuriKOrganizer-948365006.348&quot;&gt; rdf:type icaltzd:vjournal ;
	 *                                        property1 property1Value ;
	 *                                        ... ;
	 *                                        propertyN propertyNValue .
	 * </pre>
	 * 
	 * @see #generateComponentUri(Component)
	 */
	protected void crawlVTimezoneComponent(Component component, Resource parentNode) {
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
		rdfContainer.add(RDF.type, ICALTZD.Vtimezone);
		VTimeZone vtimezone = (VTimeZone) component;
		crawlPropertyList(vtimezone, rdfContainer);
		ComponentList observances = vtimezone.getObservances();
		crawlComponentList(observances, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
		passComponentToHandler(rdfContainer);
	}

	/**
	 * Crawls a single VTodo component.
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
	 *  &lt;&quot;&amp;baseuriVCalendar&quot;&gt; icaltzd:component &lt;&quot;&amp;baseuriKOrganizer-948365006.348&quot;&gt;
	 *  &lt;&quot;&amp;baseuriKOrganizer-948365006.348&quot;&gt; rdf:type icaltzd:vjournal ;
	 *                                        property1 property1Value ;
	 *                                        ... ;
	 *                                        propertyN propertyNValue .
	 * </pre>
	 * 
	 * @see #generateComponentUri(Component)
	 */
	protected void crawlVTodoComponent(Component component, Resource parentNode) {
		RDFContainer rdfContainer = prepareDataObjectRDFContainer(component);
		rdfContainer.add(RDF.type, ICALTZD.Vtodo);
		VToDo vtodo = (VToDo) component;
		crawlPropertyList(vtodo, rdfContainer);
		ComponentList alarmList = vtodo.getAlarms();
		crawlComponentList(alarmList, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.component, rdfContainer.getDescribedUri());
		passComponentToHandler(rdfContainer);
	}

	/** experimental components are unsupported at the moment */
	protected void crawlExperimentalComponent(Component component, Resource parentNode) {
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
	 */
	protected void crawlStandardObservance(Component component, Resource parentNode, RDFContainer rdfContainer) {
		Resource standardParentNode = generateAnonymousNode(rdfContainer);
		crawlPropertyList(component, standardParentNode, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.standard, standardParentNode);
	}

	/**
	 * Crawls a single daylight timezone observance component.
	 * 
	 * @see #crawlVTimezoneComponent(Component, Resource)
	 */
	protected void crawlDaylightObservance(Component component, Resource parentNode, RDFContainer rdfContainer) {
		Resource daylightParentNode = generateAnonymousNode(rdfContainer);
		crawlPropertyList(component, daylightParentNode, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.daylight, daylightParentNode);
	}

	// ///////////////////////////////////////// PROPERTIES ///////////////////////////////////////////////

	/**
	 * Crawls the ACTION property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:calscale<br>
	 * 
	 * <pre>
	 *   ical:
	 *   ACTION:AUDIO
	 *   
	 *   n3:
	 *   _:ValarmNode icaltzd:action AUDIO
	 *   
	 * </pre>
	 */
	protected void crawlActionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.action, property.getValue());
	}

	/**
	 * Crawls the ATTACH property.<br>
	 * Possible parameters: FMTTYPE (DISREGARDED), VALUE<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:attach<br>
	 * 
	 * <pre>
	 *   ical:
	 *   ATTACH;VALUE=URI:Ping
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:attach &lt;&quot;uri://Ping&quot;&gt;
	 *   
	 * </pre>
	 * 
	 * Note that "Ping" is treated as an URI. This "uri://" prefix is a workaround. Sesame doesn't accept
	 * malformed uris.
	 * 
	 */
	protected void crawlAttachProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.URI);
		addStatement(rdfContainer, parentNode, ICALTZD.attach, propertyValue);
	}

	/**
	 * Crawls the ATTENDEE property.<br>
	 * Possible parameters: numerous<br>
	 * Treatment: blank node<br>
	 * 1st link: icaltzd:attendee<br>
	 * 2nd link: icaltzd:calAddress<br>
	 * 
	 * <pre>
	 *   ical:
	 *   ATTENDEE;RSVP=TRUE;ROLE=REQ-PARTICIPANT:MAILTO:jsmith@host.com
	 *   
	 *   n3:
	 *   &lt;#Vevent-URI&gt; icaltzd:attendee _:anon .
	 *   _:anon icaltzd:rsvp TRUE;
	 *          icaltzd:role REQ-PARTICIPANT;
	 *          icaltzd:caladdress MAILTO:jsmith@jhost.com
	 *   
	 * </pre>
	 * 
	 * @param property
	 * @param parentNode
	 * @param rdfContainer
	 */
	protected void crawlAttendeeProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource blankNode = crawlParameterList(property, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.attendee, blankNode);
		addStatement(rdfContainer, blankNode, ICALTZD.calAddress, property.getValue());
	}

	/**
	 * Crawls the CALSCALE property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:calscale<br>
	 * 
	 * <pre>
	 *   ical:
	 *   CALSCALE:GREGORIAN
	 *   
	 *   n3:
	 *   _:VcalendarNode icaltzd:calscale GREGORIAN
	 * </pre>
	 */
	protected void crawlCalScaleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.calscale, property.getValue());
	}

	/**
	 * Crawls the CATEGORIES property.<br>
	 * Possible parameters: LANGUAGE (DISGREGARDED)<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:categories<br>
	 * 
	 * <pre>
	 *   ical:
	 *   CATEGORIES:APPOINTMENT,EDUCATION
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:categories APPOINTMENT,EDUCATION
	 * </pre>
	 * 
	 * @param property
	 * @param parentNode
	 * @param rdfContainer
	 */
	protected void crawlCategoriesProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.categories, property.getValue());
	}

	/**
	 * Crawls the CLASS property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:class_<br>
	 * 
	 * <pre>
	 *   ical:
	 *   CLASS:PUBLIC
	 *   
	 *   n3:
	 *   _:VcalendarNode icaltzd:class PUBLIC
	 * </pre>
	 */
	protected void crawlClassProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.class_, property.getValue());
	}

	/**
	 * Crawls the COMMENT property.<br>
	 * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:comment<br>
	 * 
	 * <pre>
	 *   ical:
	 *   COMMENT:The meeting really needs to include both ourselves
	 *     and the customer. We can't hold this  meeting without them.
	 *     As a matter of fact\, the venue for the meeting ought to be at
	 *     their site. - - John
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:comment 
	 *     &quot;&quot;&quot;The meeting really needs to include both ourselves
	 *     and the customer. We can't hold this  meeting without them.
	 *     As a matter of fact\, the venue for the meeting ought to be at
	 *     their site. - - John&quot;&quot;&quot; 
	 * </pre>
	 */
	protected void crawlCommentProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.comment, property.getValue());
	}

	/**
	 * Crawls the COMPLETED property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:completed<br>
	 * 
	 * <pre>
	 *   ical:
	 *   COMPLETED:19971210T080000Z
	 *   
	 *   n3:
	 *   _:VTodoNode icaltzd:completed &quot;1997-12-10T08:00:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsddatetime&quot;&gt;
	 * </pre>
	 */
	protected void crawlCompletedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.completed, propertyValue);
	}

	/**
	 * Crawls the CONTACT property.<br>
	 * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:contact<br>
	 * 
	 * Not mentioned in the works of the rdf ical group. Unsupported by fromIcal.py at the time of writing
	 * (2006-10-17)
	 * 
	 * <pre>
	 *   ical:
	 *   CONTACT:Jim Dolittle\, ABC Industries\, +1-919-555-1234
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:contact 
	 *     &quot;&quot;&quot;Jim Dolittle\, ABC Industries\, +1-919-555-1234&quot;&quot;&quot; 
	 * </pre>
	 */
	protected void crawlContactProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.contact, property.getValue());
	}

	/**
	 * Crawls the CREATED property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:created<br>
	 * 
	 * Note the conversion to the XSD time format
	 * 
	 * <pre>
	 *   ical:
	 *   CREATED:19971210T080000
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:created &quot;1997-12-10T08:00:00&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 * </pre>
	 */
	protected void crawlCreatedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.created, propertyValue);
	}

	/**
	 * Crawls the DESCRIPTION property.<br>
	 * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:description<br>
	 * 
	 * <pre>
	 *   ical:
	 *   DESCRIPTION:Meeting to provide technical review for &quot;Phoenix&quot;
	 *    design.\n Happy Face Conference Room. Phoenix design team
	 *    MUST attend this meeting.\n RSVP to team leader.
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:description 
	 *     &quot;&quot;&quot;Meeting to provide technical review for &quot;Phoenix&quot;
	 *    design.\n Happy Face Conference Room. Phoenix design team
	 *    MUST attend this meeting.\n RSVP to team leader.&quot;&quot;&quot; 
	 * </pre>
	 */
	protected void crawlDescriptionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.description, property.getValue());
	}

	/**
	 * Crawls the DTEND property.<br>
	 * Possible parameters: VALUE, TZID<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:dtstamp<br>
	 * 
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <pre>
	 *    ical:
	 *    DTEND:19980118T073000Z
	 *    
	 *    n3:
	 *    _:VeventNode icaltzd:dtend &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 *    
	 *    ical:
	 *    DTEND:VALUE=DATE;20020703
	 *    
	 *    n3:
	 *    _:VeventNode icaltzd:dtend &quot;2002-07-03&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *    
	 *    ical:
	 *    DTEND;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
	 *     20020630T090000
	 *     
	 *    n3:
	 *    _:VeventNode icaltzd:dtend
	 *     &quot;2002-06-30T09:00:00&quot;&circ;&circ;&lt;&quot;&amp;tzd/America/New_York#tz&quot;&gt;
	 *    &lt;pre&gt;
	 *    
	 * @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 * 
	 */
	protected void crawlDtEndProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.dtend, propertyValue);
	}

	/**
	 * Crawls the DTSTAMP property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:dtstamp<br>
	 * 
	 * <pre>
	 *   ical:
	 *   DTSTAMP:19971210T080000Z
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:dtstamp &quot;1997-12-10T08:00:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 * </pre>
	 * 
	 * Note the conversion to the XSD time format
	 */
	protected void crawlDtStampProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.dtstamp, propertyValue);
	}

	/**
	 * Crawls the DTSTART property.<br>
	 * Possible parameters: VALUE, TZID<br>
	 * Treatment: direct link<br>
	 * 1st link: icaltzd:dtstamp<br>
	 * 
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <pre>
	 *   ical:
	 *   DTSTART:19980118T073000Z
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:dtstart &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 *   
	 *   ical:
	 *   DTSTART:VALUE=DATE;20020703
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:dtstart &quot;2002-07-03&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   
	 *   ical:
	 *   DTSTART;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
	 *    20020630T090000
	 *    
	 *   n3:
	 *   _:VeventNode icaltzd:dtstart
	 *    &quot;2002-06-30T09:00:00&quot;&circ;&circ;&lt;&quot;&amp;tzd/America/New_York#tz&quot;&gt;
	 *   &lt;pre&gt;
	 *   
	 *  @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 * 
	 */
	protected void crawlDtStartProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.dtstart, propertyValue);
	}

	/**
	 * Crawls the DUE property. <br>
	 * Possible parameters: VALUE, TZID<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:due
	 * 
	 * <p>
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <pre>
	 *   ical:
	 *   DUE:19980118T073000Z
	 *   
	 *   n3:
	 *   _:VTodoNode icaltzd:due &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 *   
	 *   ical:
	 *   DUE:VALUE=DATE;20020703
	 *   
	 *   n3:
	 *   _:VTodoNode icaltzd:due &quot;2002-07-03&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   
	 *   ical:
	 *   DUE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
	 *    20020630T090000
	 *    
	 *   n3:
	 *   _:VTodoNode icaltzd:due
	 *    &quot;2002-06-30T09:00:00&quot;&circ;&circ;&lt;&quot;&amp;tzd/America/New_York#tz&quot;&gt;
	 *   &lt;pre&gt;
	 *   
	 *   @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 * 
	 */
	protected void crawlDueProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.due, propertyValue);
	}

	/**
	 * Crawls the DURATION property. <br>
	 * Possible parameters: none<br>
	 * Treatment: blank node <br>
	 * 1st link: icaltzd:duration <br>
	 * 2nd link: icaltzd:value
	 * 
	 * Note that according to the examples this should be a resource. That's why it introduces a blank node,
	 * even though it has no parameters.
	 * 
	 * <pre>
	 *   ical:
	 *   DURATION:PT1H0M0S
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:duration _:durationNode
	 *   _:durationNode icaltzd:value &quot;PT1H0M0S&quot;&circ;&circ;&lt;&quot;&amp;xsd#duration&quot;&gt;
	 * </pre>
	 */
	protected void crawlDurationProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DURATION);
		Resource durationNode = generateAnonymousNode(rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.duration, durationNode);
		addStatement(rdfContainer, durationNode, ICALTZD.value, propertyValue);
	}

	/**
	 * Crawls the EXDATE property. <br>
	 * Possible parameters: VALUE, TZID<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:exdate
	 * 
	 * <p>
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <p>
	 * The ical definition allows multiple values on this property. We disregard it and support only singluar
	 * values.
	 * 
	 * <pre>
	 *   ical:
	 *   EXDATE:19980118T073000Z
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:exdate &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 *   
	 *   ical:
	 *   EXDATE:VALUE=DATE;20020703
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:exdate &quot;2002-07-03&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   
	 *   ical:
	 *   EXDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
	 *    20020630T090000
	 *    
	 *   n3:
	 *   _:VEventNode icaltzd:due
	 *    &quot;2002-06-30T09:00:00&quot;&circ;&circ;&lt;&quot;&amp;tzd/America/New_York#tz&quot;&gt;
	 *   &lt;pre&gt;
	 * </pre>
	 * 
	 * @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 * 
	 */
	protected void crawlExDateProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.exdate, propertyValue);
	}

	/**
	 * Crawls the EXRULE property. <br> 
	 * Possible parameters: none<br>
	 * Treatment: direct link to a blank node<br>
	 * 1st link: icaltzd:rrule<br>
	 * <p>
	 * This property has RECUR value type. This neccessitates an introduction of an intermediate blank node.
	 * The reccurrence parameters are attached to this intermediate blank node (as literals with appropriate
	 * datatype)
	 * 
	 * <p>
	 * Note that this property hasn't been mentioned in the works of rdf ical group since it isn't supported
	 * by many calendaring applications.
	 * 
	 * <pre>
	 *   ical:
	 *   EXRULE:FREQ=YEARLY;INTERVAL=5;BYDAY=-1SU;BYMONTH=10
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:exrule _:exruleNode
	 *   _:exruleNode icaltzd:bymonth 10;
	 *               icaltzd:freq    YEARLY;
	 *               icaltzd:interval 5&circ;&circ;&lt;&quot;&amp;xsdinteger&quot;&gt;
	 *               icaltzd:bymonth -1SU
	 * </pre>
	 * 
	 * @see #crawlRecur(String, Resource, RDFContainer)
	 */
	protected void crawlExRuleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource rruleBlankNode = generateAnonymousNode(rdfContainer);
		crawlRecur(property.getValue(), rruleBlankNode, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.exrule, rruleBlankNode);
	}

	/**
	 * Crawls the FREEBUSY property. <br>
	 * Possible parameters: FBTYPE (DISREGARDED), VALUE<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:freebusy<br>
	 * 
	 * <p>
	 * Note that this property supports multiple values.
	 * 
	 * <p>
	 * This mapping has been 'borrowed' from ical2rdf.pl since fromIcal.py doesn't support the VFreebusy
	 * component and the FREEBUSY property at the time of writing (2006-10-17).
	 * 
	 * <pre>
	 *   ical:
	 *   FREEBUSY;VALUE=PERIOD:19971015T050000Z/PT8H30M,
	 *    19971015T160000Z/PT5H30M,19971015T223000Z/PT6H30M
	 *   
	 *   n3:
	 *   _:VFreebusyComponentNode icaltzd:freebusy &quot;19971015T050000Z/PT8H30M&quot; .
	 *   _:VFreebusyComponentNode icaltzd:freebusy &quot;19971015T160000Z/PT5H30M&quot; .
	 *   _:VFreebusyComponentNode icaltzd:freebusy &quot;19971015T223000Z/PT6H30M&quot; .
	 * </pre>
	 */
	protected void crawlFreeBusyProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		List<Node> valueList = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.PERIOD);
		addMultipleStatements(rdfContainer, parentNode, ICALTZD.freebusy, valueList);
	}

	/**
	 * Crawls the GEO property. <br>
	 * Possible parameters: none<br>
	 * Treatment: rdf list <br>
	 * 1st link: icaltzd:geo
	 * 
	 * <p>
	 * The value of this property is translated into an rdf list of literals. The mapping based on the example
	 * from http://www.w3.org/2002/12/cal/test/geo1.rdf
	 * 
	 * <pre>
	 *   ical:
	 *   GEO:40.442673;-79.945815
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:geo _:firstListNode .
	 *   _firstListNode  rdf:first 40.442673&circ;&circ;&lt;&quot;&amp;xsddouble&quot;&gt; ;
	 *                   rdf:rest _:secondListNode .
	 *   _secondListNode rdf:first -79.945815 &circ;&circ;&lt;&quot;&amp;xsddouble&quot;&gt; ; 
	 *                   rdf:rest &lt;&quot;&amp;rdfnil&quot;&gt; .
	 * </pre>
	 */
	protected void crawlGeoProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		String[] valueTokens = property.getValue().split(";");
		Literal latitudeLiteral = rdfContainer.getValueFactory().createLiteral(valueTokens[0], XSD._double);
		Literal longitudeLiteral = rdfContainer.getValueFactory().createLiteral(valueTokens[1], XSD._double);
		Resource firstListNode = generateAnonymousNode(rdfContainer);
		Resource secondListNode = generateAnonymousNode(rdfContainer);

		addStatement(rdfContainer, firstListNode, RDF.first, latitudeLiteral);
		addStatement(rdfContainer, firstListNode, RDF.rest, secondListNode);
		addStatement(rdfContainer, secondListNode, RDF.first, longitudeLiteral);
		addStatement(rdfContainer, secondListNode, RDF.rest, RDF.nil);
		addStatement(rdfContainer, parentNode, ICALTZD.geo, firstListNode);
	}

	/**
	 * Crawls the LAST-MODIFIED property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:lastModified
	 * 
	 * <pre>
	 *   ical:
	 *   LAST-MODIFIED:20041223T151752
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:lastModified 
	 *        &quot;2004-12-23T15:17:52&quot;&circ;&circ;&lt;&quot;&amp;xsddatetime&quot;&gt;
	 * </pre>
	 */
	protected void crawlLastModifiedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.lastModified, propertyValue);
	}

	/**
	 * Crawls the LAST-MODIFIED property. <br>
	 * Possible parameters: ALTREP, LANGUAGE (DISREGARDED)<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:location
	 * 
	 * <pre>
	 *   ical:
	 *   LOCATION:San Francisco
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:location &quot;San Francisco&quot; .
	 * </pre>
	 */
	protected void crawlLocationProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.location, property.getValue());
	}

	/**
	 * Crawls the METHOD property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:method
	 * 
	 * <pre>
	 *   ical:
	 *   METHOD:PUBLISH
	 *   
	 *   n3:
	 *   _:VcalendarNode icaltzd:method &quot;PUBLISH&quot; .
	 * </pre>
	 */
	protected void crawlMethodProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.method, property.getValue());
	}

	/**
	 * Crawls the ORGANIZER property. <br>
	 * Possible parameters: numerous<br>
	 * Treatment: blank node <br>
	 * 1st link: icaltzd:organizer <br>
	 * 2nd link: icaltzd:caladdress
	 * 
	 * <pre>
	 *   ical:
	 *   ORGANIZER;CN=JohnSmith:MAILTO:jsmith@host1.com
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:organizer _:organizerNode .
	 *   _:organizerNode icaltzd:cn &quot;JohnSmith&quot; .
	 *   _:organizerNode icaltzd:calAddress &quot;MAILTO:jsmith@host1.com&quot;
	 * </pre>
	 */
	protected void crawlOrganizerProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource blankNode = crawlParameterList(property, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.organizer, blankNode);
		addStatement(rdfContainer, blankNode, ICALTZD.calAddress, property.getValue());
	}

	/**
	 * Crawls the PERCENT-COMPLETE property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:percentComplete
	 * 
	 * <pre>
	 *   ical:
	 *   PERCENT-COMPLETE:39
	 *   
	 *   n3:
	 *   _:VtodoNode icaltzd:percentComplete &quot;39&quot;&circ;&circ;&lt;&quot;&amp;xsd#integer&quot;&gt;
	 * </pre>
	 */
	protected void crawlPercentCompleteProperty(Property property, Resource parentNode,
			RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
		addStatement(rdfContainer, parentNode, ICALTZD.percentComplete, propertyValue);
	}

	/**
	 * Crawls the PRIORITY property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:priority
	 * 
	 * <pre>
	 *   ical:
	 *   PRIORITY:2
	 *   
	 *   n3:
	 *   _:VtodoNode icaltzd:priority &quot;2&quot;&circ;&circ;&lt;&quot;&amp;xsd#integer&quot;&gt;
	 * </pre>
	 */
	protected void crawlPriorityProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
		addStatement(rdfContainer, parentNode, ICALTZD.priority, propertyValue);
	}

	/**
	 * Crawls the PRODID property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:prodid
	 * 
	 * <pre>
	 *   ical:
	 *   PRODID:-//Apple Computer\, Inc//iCal 1.5//EN
	 *   
	 *   n3:
	 *   _:VcalendarNode icaltzd:prodid 
	 *        &quot;-//Apple Computer\, Inc//iCal 1.5//EN&quot;
	 * </pre>
	 */
	protected void crawlProdIdProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.prodid, property.getValue());
	}

	/**
	 * Crawls the RDATE property. <br>
	 * Possible parameters: VALUE, TZID<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:rdate
	 * 
	 * <p>
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <pre>
	 *   ical:
	 *   RDATE:19980118T073000Z
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:rdate &quot;1998-01-18T07:30:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 *   
	 *   ical:
	 *   RDATE:VALUE=DATE;20020703
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:rdate &quot;2002-07-03&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   
	 *   ical:
	 *   RDATE;TZID=/softwarestudio.org/Olson_20011030_5/America/New_York:
	 *    20020630T090000
	 *    
	 *   n3:
	 *   _:VEventNode icaltzd:rdate
	 *    &quot;2002-06-30T09:00:00&quot;&circ;&circ;&lt;&quot;&amp;tzd/America/New_York#tz&quot;&gt;
	 *    
	 *   ical: 
	 *   RDATE;VALUE=DATE:19970304,19970504,19970704,19970904
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:rdate &quot;1997-03-04&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   _:VEventNode icaltzd:rdate &quot;1997-05-04&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   _:VEventNode icaltzd:rdate &quot;1997-07-04&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   _:VEventNode icaltzd:rdate &quot;1997-09-04&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;
	 *   &lt;pre&gt;
	 *   
	 *   @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 * 
	 */
	protected void crawlRDateProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		List<Node> valueList = getMultipleRdfPropertyValues(rdfContainer, property, IcalDataType.DATE_TIME);
		addMultipleStatements(rdfContainer, parentNode, ICALTZD.rdate, valueList);
	}

	/**
	 * Crawls the RECURRENCE-ID property. <br>
	 * Possible parameters: VALUE, TZID, RANGE<br>
	 * Treatment: blank node <br>
	 * 1st link: icaltzd:rdate <br>
	 * 2nd link: icaltzd:value
	 * 
	 * <p>
	 * The dates and date-times are converted to xmlschema form. The timezones are expressed in datatypes. The
	 * value literal gets a datatype, whose URI points to the VTimezone object defined in the timezone
	 * database under http://www.w3.org/2002/12/cal/tzd/
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *   ical:
	 *   RECURRENCE-ID;VALUE=DATE:19960401
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:recurrenceId _:recurrenceIdBlankNode .
	 *   _:recurrenceIdBlankNode icaltzd:value &quot;1996-04-01&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt;.
	 *  
	 *  
	 *   ical:
	 *   RECURRENCE-ID;RANGE=THISANDFUTURE:19960120T120000Z
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:recurrenceId _:recurrenceIdBlankNode .
	 *   _:recurrenceIdBlankNode icaltzd:value 
	 *                            &quot;1996-01-20T12:00:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#date&quot;&gt; .
	 *   _:recurrenceIdBlankNode icaltzd:range &quot;THISANDFUTURE&quot; .
	 *  </pre>
	 * 
	 * @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan's Timezone Database</a>
	 */
	protected void crawlRecurrenceIdProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource recurrenceIdBlankNode = crawlParameterList(property, rdfContainer);
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DATE_TIME);
		addStatement(rdfContainer, parentNode, ICALTZD.recurrenceId, recurrenceIdBlankNode);
		addStatement(rdfContainer, recurrenceIdBlankNode, ICALTZD.value, propertyValue);
	}

	/**
	 * Crawls the RELATED-TO property. <br>
	 * Possible parameters: RELTYPE (DISREGARDED)<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:relatedTo
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *   ical:
	 *   RELATED-TO:&lt;19960401-080045-4000F192713-0052@host1.com&gt;
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:relatedTo 
	 *        &quot;&lt;19960401-080045-4000F192713-0052@host1.com&gt;&quot; .
	 *  
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlRelatedToProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.relatedTo, property.getValue());
	}

	/**
	 * Crawls the REPEAT property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:repeat <br>
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *   ical:
	 *   REPEAT:3
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:repeat 3&circ;&circ;&lt;&quot;&amp;xsdinteger&quot;&gt; .
	 *  
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlRepeatProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
		addStatement(rdfContainer, parentNode, ICALTZD.repeat, propertyValue);
	}

	/**
	 * Crawls the REQUEST-STATUS property. <br>
	 * Possible parameters: LANGUAGE (DISREGARDED)<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:requestStatus <br>
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *   ical:
	 *   REQUEST-STATUS:4.1;Event conflict. Date/time is busy.
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:requestStatus
	 *            &quot;4.1;Event conflict. Date/time is busy.&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlRequestStatusProperty(Property property, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.requestStatus, property.getValue());
	}

	/**
	 * Crawls the RESOURCES property. <br>
	 * Possible parameters: LANGUAGE, ALTREP (DISREGARDED)<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:resources <br>
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *   ical:
	 *   RESOURCES:EASEL,PROJECTOR,VCR
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:resources &quot;EASEL,PROJECTOR,VCR&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlResourcesProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.resources, property.getValue());
	}

	/**
	 * Crawls the RRULE property.<br>
	 * Possible parameters: none<br>
	 * Treatment: direct link to a recur blank node<br>
	 * 1st link: icaltzd:rrule<br>
	 * <p>
	 * 
	 * This property has RECUR value type. This neccessitates an introduction of an intermediate blank node.
	 * The reccurrence parameters are attached to this intermediate blank node (as literals with appropriate
	 * datatype)
	 * 
	 * <pre>
	 *   
	 *   ical:
	 *   RRULE:FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:rrule _:rruleNode
	 *   _:rruleNode icaltzd:bymonth 10;
	 *               icaltzd:freq    YEARLY;
	 *               icaltzd:interval 1&circ;&circ;&lt;&quot;&amp;xsdinteger&quot;&gt;
	 *               icaltzd:bymonth -1SU
	 * </pre>
	 * 
	 * @see #crawlRecur(String, Resource, RDFContainer)
	 */
	protected void crawlRRuleProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource rruleBlankNode = generateAnonymousNode(rdfContainer);
		crawlRecur(property.getValue(), rruleBlankNode, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.rrule, rruleBlankNode);
	}

	/**
	 * Crawls the SEQUENCE property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:sequence <br>
	 * 
	 * <pre>
	 *   ical:
	 *   SEQUENCE:20
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:sequence 20&circ;&circ;&lt;&quot;&amp;xsdinteger&quot;&gt; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlSequenceProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.INTEGER);
		addStatement(rdfContainer, parentNode, ICALTZD.sequence, propertyValue);
	}

	/**
	 * Crawls the STATUS property. <br>
	 * Possible parameters: none<br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:status <br>
	 * 
	 * <pre>
	 *   ical:
	 *   STATUS:COMPLETED
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:status &quot;COMPLETED&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlStatusProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.status, property.getValue());
	}

	/**
	 * Crawls the SUMMARY property. <br>
	 * Possible parameters: ALTREP, LANGUAGE (DISREGARDED) <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:summary <br>
	 * 
	 * <pre>
	 *   ical:
	 *   SUMMARY:Department Party
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:summary &quot;Department Party&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlSummaryProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.summary, property.getValue());
	}

	/**
	 * Crawls the TRANSP property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:transp <br>
	 * 
	 * <pre>
	 *   ical:
	 *   TRANSP:OPAQUE
	 *   
	 *   n3:
	 *   _:VEventNode icaltzd:transp &quot;OPAQUE&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTranspProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.transp, property.getValue());
	}

	/**
	 * Crawls the TRIGGER property. <br>
	 * Possible parameters: numerous<br>
	 * Treatment: blank node <br>
	 * 1st link: icaltzd:trigger
	 * 
	 * This is a multi-valued property. Possible types are DURATION (default) and DATE-TIME
	 * 
	 * <pre>
	 *   ical:
	 *   TRIGGER;RELATED=START:-PT30M
	 *   
	 *   n3:
	 *   _:ValarmNode icaltzd:trigger _:triggerNode . 
	 *   _:triggerNode icaltzd:related START .
	 *   _:triggerNode icaltzd:value &quot;-PT30M&quot;&circ;&circ;&lt;&quot;&amp;xsd#duration&quot;&gt;
	 *   
	 *   ical:
	 *   TRIGGER;VALUE=DATE-TIME:20060412T230000Z
	 *   
	 *   n3:
	 *   _:ValarmNode icaltzd:trigger _:triggerNode . 
	 *   _:triggerNode icaltzd:value &quot;2006-04-12T23:00:00Z&quot;&circ;&circ;&lt;&quot;&amp;xsd#datetime&quot;&gt;
	 * </pre>
	 * 
	 * Note that the date-time value is converted from the ical form, to the form defined in the xsd datetime
	 * datatype specification.
	 */
	protected void crawlTriggerProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Resource triggerBlankNode = crawlParameterList(property, rdfContainer);
		addStatement(rdfContainer, parentNode, ICALTZD.trigger, triggerBlankNode);
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.DURATION);
		addStatement(rdfContainer, triggerBlankNode, ICALTZD.value, propertyValue);
	}

	/**
	 * Crawls the TZID property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:tzid <br>
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
	 *   _:VTimezoneNode icaltzd:tzid 
	 *        &quot;/softwarestudio.org/Olson_20011030_5/America/New_York&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTzidProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.tzid, property.getValue());
	}

	/**
	 * Crawls the TZNAME property. <br>
	 * Possible parameters: LANGUAGE (DISREGARDED) <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:tzname <br>
	 * 
	 * <pre>
	 *   ical:
	 *   TZNAME:EDT
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:tzname 
	 *        &quot;/softwarestudio.org/Olson_20011030_5/America/New_York&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTzNameProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.tzname, property.getValue());
	}

	/**
	 * Crawls the TZOFFSETFROM property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:tzoffsetfrom <br>
	 * 
	 * <pre>
	 *   ical:
	 *   TZOFFSETFROM:-0500
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:tzoffsetfrom &quot;-0500&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTzOffsetFromProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.tzoffsetfrom, property.getValue());
	}

	/**
	 * Crawls the TZOFFSETTO property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:tzoffsetto <br>
	 * 
	 * <pre>
	 *   ical:
	 *   TZOFFSETTO:+1000
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:tzoffsetto &quot;+1000&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTzOffsetToProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.tzoffsetto, property.getValue());
	}

	/**
	 * Crawls the TZURL property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:tzurl <br>
	 * 
	 * <p>
	 * Note that the value of this property is an URI:
	 * 
	 * <p>
	 * This property hasn't been mentioned in the works of rdf ical group.
	 * 
	 * <pre>
	 *      
	 *   ical:
	 *   TZURL:http://timezones.r.us.net/tz/US-California-Los_Angeles
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:tzurl 
	 *        &lt;&quot;http://timezones.r.us.net/tz/US-California-Los_Angeles&quot;&gt; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlTzUrlProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.URI);
		addStatement(rdfContainer, parentNode, ICALTZD.tzurl, propertyValue);
	}

	/**
	 * Crawls the UID property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:uid <br>
	 * 
	 * <pre>
	 *      
	 *   ical:
	 *   UID:20020630T230445Z-3895-69-1-7@jammer
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:uid &quot;20020630T230445Z-3895-69-1-7@jammer&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlUidProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.uid, property.getValue());
	}

	/**
	 * Crawls the URL property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:url <br>
	 * 
	 * <pre>
	 *      
	 *   ical:
	 *   URL:http://abc.com/pub/calendars/jsmith/mytime.ics
	 *   
	 *   n3:
	 *   _:VTimezoneNode icaltzd:url 
	 *        &lt;&quot;http://abc.com/pub/calendars/jsmith/mytime.ics&quot;&gt; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlUrlProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		Node propertyValue = getRdfPropertyValue(rdfContainer, property, IcalDataType.URI);
		addStatement(rdfContainer, parentNode, ICALTZD.url, propertyValue);
	}

	/**
	 * Crawls the VERSION property. <br>
	 * Possible parameters: none <br>
	 * Treatment: direct link <br>
	 * 1st link: icaltzd:version <br>
	 * 
	 * <pre>
	 *      
	 *   ical:
	 *   VERSION:2.0
	 *   
	 *   n3:
	 *   _:VCalendarNode icaltzd:version &quot;2.0&quot; .
	 *   &lt;pre&gt;
	 * 
	 */
	protected void crawlVersionProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.version, property.getValue());
	}

	/**
	 * Extended properties are disregarded at the moment.
	 */
	protected void crawlXtendedProperty(Property property, Resource parentNode, RDFContainer rdfContainer) {
	// don't do anything
	}

	// //////////////////////////////////////// PARAMETERS ////////////////////////////////////////////////

	protected void crawlAltRepParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.altrep, parameter.getValue());
	}

	/**
	 * Crawls the CN parameter. All parameters introduce a single triple attached to the blank node for the
	 * property in question.
	 * 
	 * <pre>
	 *   ical:
	 *   ORGANIZER;CN=&quot;John Smith&quot;:MAILTO:jsmith@host.com
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:organizer _:organizerBNode .
	 *   _:organizerBNode icaltzd:cn John Smith ;
	 *                    icaltzd:calAddress MAILTO:jsmith@host.com
	 * </pre>
	 */
	protected void crawlCnParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.cn, parameter.getValue());
	}

	/**
	 * Crawls the CUTYPE parameter. All parameters introduce a single triple attached to the blank node for
	 * the property in question.
	 * 
	 * <pre>
	 *   ical:
	 *   ATTENDEE;CUTYPE=GROUP:MAILTO:ietf-calsch@imc.org
	 *   
	 *   n3:
	 *   _:VeventNode icaltzd:attendee _:attendeeBNode .
	 *   _:attendeeBNode icaltzd:cutype GROUP ;
	 *                   icaltzd:calAddress MAILTO:ietf-calsch@imc.org
	 * </pre>
	 * 
	 */
	protected void crawlCuTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.cutype, parameter.getValue());
	}

	protected void crawlDelegatedFromParameter(Parameter parameter, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.delegatedFrom, parameter.getValue());
	}

	protected void crawlDelegatedToParameter(Parameter parameter, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.delegatedTo, parameter.getValue());
	}

	protected void crawlDirParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.dir, parameter.getValue());
	}

	protected void crawlEncodingParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.encoding, parameter.getValue());
	}

	protected void crawlFbTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.fbtype, parameter.getValue());
	}

	protected void crawlFmtTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.fmttype, parameter.getValue());
	}

	protected void crawlLanguageParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.language, parameter.getValue());
	}

	protected void crawlMemberParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.member, parameter.getValue());
	}

	protected void crawlPartStatParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.partstat, parameter.getValue());
	}

	protected void crawlRangeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.range, parameter.getValue());
	}

	protected void crawlRelatedParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.related, parameter.getValue());
	}

	protected void crawlRelTypeParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.reltype, parameter.getValue());
	}

	protected void crawlRoleParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.role, parameter.getValue());
	}

	protected void crawlRsvpParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.rsvp, parameter.getValue());
	}

	protected void crawlSentByParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.sentBy, parameter.getValue());
	}

	/**
	 * Note that this parameter is ignored in the icaltzd ontology. It is treated differently. If a DATE-TIME
	 * property (like DTSTART) has this parameter it receives a datatype, whose uri points to the VTimezone
	 * object in Dan Connoly's timezone resource.
	 * 
	 * @see <a href="http://www.w3.org/2002/12/cal/tzd/">Dan Connoly's Timezone Database</a>
	 * 
	 * @param parameter
	 * @param parentNode
	 * @param rdfContainer
	 */
	protected void crawlTzidParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
	// do nothing
	}

	/**
	 * This parameter is ignored in the icaltzd ontology. It is treated differently.
	 * 
	 * @see #getRdfPropertyValue(RDFContainer, Property, String)
	 */
	protected void crawlValueParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
	// do nothing
	}

	/** Extended parameters are disregarded at the moment */
	protected void crawlXParameter(Parameter parameter, Resource parentNode, RDFContainer rdfContainer) {
	// do nothing
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////// RECURRENCE RULES ////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	protected void crawlRecur(String recurString, Resource rruleBlankNode, RDFContainer rdfContainer) {
		String[] recurTokens = recurString.split("[=;]");
		for (int i = 0; i < recurTokens.length; i += 2) {
			crawlRecurrenceParam(recurTokens[i], recurTokens[i + 1], rruleBlankNode, rdfContainer);
		}
	}

	protected void crawlRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		if (name.equals("FREQ")) {
			crawlFreqRecurrenceParam(name, value, parentNode, rdfContainer);
		}
		else if (name.equals("UNTIL")) {
			crawlUntilRecurrenceParam(name, value, parentNode, rdfContainer);
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
	}

	protected void crawlFreqRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.freq, value);
	}

	protected void crawlUntilRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.until, value);
	}

	/** note that it is the only recurrence parameter that introduces a typed interval */
	protected void crawlIntervalRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		Literal literal = rdfContainer.getValueFactory().createLiteral(value, XSD._integer);
		addStatement(rdfContainer, parentNode, ICALTZD.interval, literal);
	}

	protected void crawlBySecondRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.bysecond, value);
	}

	protected void crawlByMinuteRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.byminute, value);
	}

	protected void crawlByHourRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.byhour, value);
	}

	protected void crawlByDayRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.byday, value);
	}

	protected void crawlByMonthdayRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.bymonthday, value);
	}

	protected void crawlByYeardayRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.byyearday, value);
	}

	protected void crawlByWeeknoRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.byweekno, value);
	}

	protected void crawlByMonthRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.bymonth, value);
	}

	protected void crawlBySetposRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.bysetpos, value);
	}

	protected void crawlWkstRecurrenceParam(String name, String value, Resource parentNode,
			RDFContainer rdfContainer) {
		addStatement(rdfContainer, parentNode, ICALTZD.wkst, value);
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
		addStatement(rdfContainer, subject, predicate, rdfContainer.getValueFactory().createLiteral(object));
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
	private void passComponentToHandler(RDFContainer metadata) {
		DataObject dataObject = new DataObjectBase(metadata.getDescribedUri(), getDataSource(), metadata);
		String id = metadata.getDescribedUri().toString();

		if (accessData == null) {
			handler.objectNew(this, dataObject);
		}
		else if (!accessData.isKnownId(id)) {
			updateAccessData(metadata);
			handler.objectNew(this, dataObject);
		}
		else if (isChanged(metadata)) {
			updateAccessData(metadata);
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
	private void updateAccessData(RDFContainer metadata) {
		String id = metadata.getDescribedUri().toString();
		String hashOfAllLiteralProperties = hashOfProperties(metadata);
		accessData.put(id, "visited", "true");
		accessData.put(id, "hash", hashOfAllLiteralProperties);
	}

	/**
	 * Computes a hash value of all properties of the central URI of the given RDF container. I know it's
	 * inefficient but the RDFContainer interface doesn't have any search functionality :-) (Antoni Mylka
	 * 20.10.2006)
	 */
	private String hashOfProperties(RDFContainer metadata) {
		StringBuffer sumOfAllProperties = new StringBuffer("");
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.action);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.attach);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.attendee);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.calscale);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.categories);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.class_);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.comment);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.completed);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.contact);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.created);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.description);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.dtend);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.dtstamp);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.dtstart);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.due);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.duration);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.exdate);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.exrule);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.freebusy);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.geo);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.lastModified);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.location);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.method);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.organizer);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.percentComplete);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.priority);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.prodid);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.rdate);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.recurrenceId);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.relatedTo);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.repeat);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.requestStatus);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.resources);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.rrule);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.sequence);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.status);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.summary);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.transp);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.trigger);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.tzid);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.tzname);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.tzoffsetfrom);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.tzoffsetto);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.tzurl);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.uid);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.url);
		appendPropertyValue(sumOfAllProperties, metadata, ICALTZD.version);
		return sha1Hash(sumOfAllProperties.toString());
	}

	private void appendPropertyValue(StringBuffer buffer, RDFContainer metadata, URI predicate) {
		Collection propertyValues = metadata.getAll(predicate);
		for (Object valueObject : propertyValues) {
			Node value = (Node) valueObject;
			appendSinglePropertyValue(buffer, metadata, predicate, value);
		}
	}

	private void appendSinglePropertyValue(StringBuffer buffer, RDFContainer metadata, URI predicate,
			Node value) {
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
		}
	}

	/**
	 * Determines if the given object has been changed or not.
	 * 
	 * <p>
	 * It compares the hash of the current metadata object with the hash of the old metadata object.
	 * 
	 * @param metadata
	 * @return
	 */
	private boolean isChanged(RDFContainer metadata) {
		String id = metadata.getDescribedUri().toString();
		String newHash = hashOfProperties(metadata);
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
		return URIImpl.createURIWithoutChecking(baseuri + "VCalendar");
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
			return generateTimeZoneURI(component);
		}
		Property uidProperty = component.getProperty(Property.UID);
		if (uidProperty != null) {
			return URIImpl.createURIWithoutChecking(baseuri + uidProperty.getValue());
		}
		else {
			return generateSumOfAllPropertiesURI(component);
		}
	}

	/**
	 * Generated the URI for a timezone component. Uses the TZID property to compute a URI for the timezone
	 * component in the Timezone database of Dan Connoly.
	 * 
	 * @param component The VTimezone component for which the URI should be generated.
	 * @return The generated URI.
	 */
	private URI generateTimeZoneURI(Component component) {
		Property tzidProperty = component.getProperty(Property.TZID);
		if (tzidProperty != null) {
			return createTimeZoneDatatypeURI(tzidProperty.getValue());
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
	private URI generateSumOfAllPropertiesURI(Component component) {
		StringBuffer sumOfAllProperties = new StringBuffer("");
		PropertyList propertyList = component.getProperties();
		Iterator it = propertyList.iterator();
		while (it.hasNext()) {
			Property property = (Property) it.next();
			sumOfAllProperties.append(property.getValue());
		}
		String result = baseuri + sha1Hash(sumOfAllProperties.toString());
		return URIImpl.createURIWithoutChecking(result);
	}

	/**
	 * Generates a URI for an anonymous component. This one is used for embedded components - like valarms.
	 * 
	 * @param component
	 * @return
	 */
	private URI generateAnonymousComponentUri(Component component) {
		String result = baseuri + component.getName() + "-" + java.util.UUID.randomUUID().toString();
		return URIImpl.createURIWithoutChecking(result);
	}
	
	private URI createTimeZoneDatatypeURI(String value) {
		int lastSlashPosition = value.lastIndexOf("/");
		if (lastSlashPosition == -1) {
			// completely unknown naming scheme for timezones
			return URIImpl.createURIWithoutChecking("timezone://" + value);
		}
		int oneBeforeLastSlashPosition = value.lastIndexOf("/", lastSlashPosition - 1);
		if (oneBeforeLastSlashPosition == -1) {
			// this is to support simple TZID=Europe/London identifiers
			return URIImpl.createURIWithoutChecking(timezoneNamespacePrefix + value + "#tz");
		}
		else {
			// this is to support full Olson identifiers like:
			// TZID=/softwarestudio.org/Olson_20011030_5/America/New_York
			String timezoneName = value.substring(oneBeforeLastSlashPosition);
			return URIImpl.createURIWithoutChecking(timezoneNamespacePrefix + timezoneName + "#tz");
		}
	}

	private Resource generateAnonymousNode(RDFContainer rdfContainer) {
		if (realBlankNodes) {
			return rdfContainer.getValueFactory().createBlankNode();
		}
		else {
			return rdfContainer.getValueFactory().createURI(baseuri + java.util.UUID.randomUUID().toString());
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////// CONVERSION OF ICAL PROPERTY VALUES INTO RDF NODES /////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private List<Node> getMultipleRdfPropertyValues(RDFContainer rdfContainer, Property property,
			String defaultType) {
		String totalPropertyValue = property.getValue();
		if (totalPropertyValue == null) {
			return null;
		}
		String[] valuesArray = totalPropertyValue.split(",");
		List<Node> resultList = new LinkedList<Node>();
		for (int i = 0; i < valuesArray.length; i++) {
			Node currentValue = getRdfPropertyValue(rdfContainer, valuesArray[i], 
				property.getParameter(Parameter.TZID), property.getParameter(Parameter.VALUE), defaultType);
			resultList.add(currentValue);
		}
		return resultList;
	}

	private Node getRdfPropertyValue(RDFContainer rdfContainer, Property property, String defaultType) {
		return getRdfPropertyValue(rdfContainer, property.getValue(), property.getParameter(Parameter.TZID),
			property.getParameter(Parameter.VALUE), defaultType);
	}

	private Node getRdfPropertyValue(RDFContainer rdfContainer, String propertyValue, Parameter tzidParameter,
			Parameter valueParameter, String defaultType) {
		// timezones as datatypes ...
		if (tzidParameter != null
				&& (valueParameter == null && defaultType.equals(IcalDataType.DATE_TIME) || valueParameter != null
						&& valueParameter.getValue().equals(IcalDataType.DATE_TIME))) {
			return getDateTimeWithTimeZone(rdfContainer, propertyValue, tzidParameter);
		}

		// return values of the type URI as RDF URI's
		if (tzidParameter == null
				&& (valueParameter == null && defaultType.equals(IcalDataType.URI) || valueParameter != null
						&& valueParameter.getValue().equals(IcalDataType.URI))) {
			return tryToCreateAnUri(rdfContainer, propertyValue);
		}

		Literal literal = null;
		URI datatypeURI = null;
		String rdfPropertyValue = null;
		if (valueParameter != null) {
			String valueParameterString = valueParameter.getValue();
			datatypeURI = convertValueParameterToXSDDatatype(valueParameterString);
			rdfPropertyValue = convertIcalValueToXSDValue(propertyValue, valueParameterString);
			literal = rdfContainer.getValueFactory().createLiteral(rdfPropertyValue, datatypeURI);
		}
		else if (defaultType != null) {
			datatypeURI = convertValueParameterToXSDDatatype(defaultType);
			rdfPropertyValue = convertIcalValueToXSDValue(propertyValue, defaultType);
			literal = rdfContainer.getValueFactory().createLiteral(rdfPropertyValue, datatypeURI);
		}
		else {
			literal = rdfContainer.getValueFactory().createLiteral(propertyValue);
		}
		return literal;
	}

	/**
	 * Tries to create an URI from the given string. Introduced to support 'URI's' like 'Ping'.
	 * 
	 * @param propertyValue The string that the URI should be created from.
	 * @return The created URI.
	 */
	private Node tryToCreateAnUri(RDFContainer rdfContainer, String propertyValue) {
		// first let's try the easy way;
		URI uri = null;
		// ugly hack, Sesame doesn't accept uris without colons, but java.net.URI does
		// we have to check it manually
		if (propertyValue.indexOf(':') == -1) {
			uri = rdfContainer.getValueFactory().createURI("uri:" + propertyValue);
		} else {
			try {
				uri = rdfContainer.getValueFactory().createURI(propertyValue);
			}
			catch (Exception e) {
				// oops...
				uri = rdfContainer.getValueFactory().createURI(baseuri + propertyValue);
			}
		}
		return uri;
	}

	private Node getDateTimeWithTimeZone(RDFContainer rdfContainer, String icalValue, 
			Parameter tzidParameter) {
		String rdfPropertyValue = convertIcalDateTimeToXSDDateTime(icalValue);
		URI timezoneDatatypeURI = createTimeZoneDatatypeURI(tzidParameter.getValue());
		Node result = rdfContainer.getValueFactory().createLiteral(rdfPropertyValue, timezoneDatatypeURI);
		return result;
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
			datatypeURI = XSD._duration;
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
			LOGGER.severe("Unknown value parameter: " + valueString);
		}
		return datatypeURI;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////// NAMESPACE FOR EXTENDED ICAL ELEMENTS ////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Generates the extendedNameSpace that will be used for all extended properties throughout the calendar.
	 * <p>
	 * The code has been 'borrowed' from the mimedir-parser available from
	 * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
	 * 
	 * @see http://rdfig.xmlhack.com/2003/02/26/2003-02-26.html#1046279854.884486
	 * @see http://ilrt.org/discovery/chatlogs/rdfig/2003-02-26.html#T17-21-04
	 * @see http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
	 */
	private String generateExtendedNameSpace(String prodid) {
		String processed = processSpaces(prodid.substring(3));
		String sha1 = sha1Hash(prodid.toString());
		String result = productNamespacePrefix + "/" + processed.substring(0, 10) + "_"
				+ sha1.substring(0, 5);
		return result;
	}

	/**
	 * Processes the string for use in the generation of extendedNamespace. URI
	 * <p>
	 * The code has been 'borrowed' from the mimedir-parser available from
	 * http://ilrt.org/discovery/2003/02/cal/mimedir-parser/
	 * 
	 * @see generateExtendedNameSpace(String prodid)
	 * @param name The product id to be processed.
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
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(string.getBytes());
			byte[] digest = md.digest();
			BigInteger integer = new BigInteger(1, digest);
			return integer.toString(16);
		}
		catch (Exception e) {
			return null;
		}
	}
}
