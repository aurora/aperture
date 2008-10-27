/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.ValueFactory;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * <h1>Outlook Resource</h1>
 * <p>
 * For each outlook class, a outlook resource class exists. Here we implement methods to get save versions of
 * the classes, map them to RDFS, etc. Also the real extraction process happens here.
 * 
 * It would be bad to have each resourceWrapper in its own file, too many files, so this is for shortness of
 * code.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Organisation: Gnowsis.com
 * </p>
 * 
 * @author Leo Sauermann (leo@gnowsis.com)
 * @version $Id$
 */
public abstract class OutlookResource {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
	/**
	 * <p>
	 * Copyright: Copyright (c) 2003
	 * </p>
	 * <p>
	 * Organisation: Gnowsis.com
	 * </p>
	 * 
	 * @author Leo Sauermann (leo@gnowsis.com)
	 * @version 0.1
	 */
	public static class Appointment extends OutlookResourceSave {

		public static String ITEMTYPE = "appointment";

		public Appointment(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeAppointmentItem";
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			// save dispatch
			Dispatch resource = getSaveResource();

			// type is already added by AccessData.

			// add label
            
			addPropertyIfNotNull(rdf, NIE.title, resource, "Subject");
			// body
			addPropertyIfNotNull(rdf, NIE.plainTextContent, resource, "Body");

			// uid
			// this is handled in addData() of superclass
			//addPropertyIfNotNull(rdf, NIE.identifier, resource, "EntryID");

			// dtstamp, this is UTZ, so no Timezone?
			addDateIfNotNull(rdf, NCAL.dtstamp, resource, "CreationTime");
			addDateIfNotNull(rdf, NCAL.lastModified, resource, "LastModificationTime");
			
			// dtstart in local time, lets assume you are all in vienna
			addNcalDateTimeIfNotNull(rdf, NCAL.dtstart, resource, "Start");
			addNcalDateTimeIfNotNull(rdf, NCAL.dtend, resource, "End");
			// location
			addPropertyIfNotNull(rdf, NCAL.location, resource, "Location");
            
            // organizer and attendees
            String organizer = getLiteralOf(resource, "Organizer");
            if (organizer != null)
            {
                URI organizerUri = rdf.getModel().createURI(
                    rdf.getDescribedUri()+"-organizer");
                rdf.add(NCAL.organizer, organizerUri);
                rdf.getModel().addStatement(organizerUri, RDF.type, NCAL.Organizer);
                rdf.getModel().addStatement(organizerUri, NCO.fullname, organizer);
            }
            
            String optionalAttendees = getLiteralOf(resource, "OptionalAttendees");
            logger.debug("optional: "+optionalAttendees);
            // using the save-resource here, this may break! then use getResource() instead..
            addRecipientsIfNotNull(rdf, getResource(), "Recipients", this);
		}

		public URI getType() {
			return NCAL.Event;
		}
	}

	/**
	 * wraps an outlook calendar folder as vcalendar - leap of faith to do this.
	 * 
	 * 
	 * @author Sauermann
	 */
	public static class Calendar extends Folder {

		public static final String ITEMTYPE = "calendar";

		public Calendar(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

	}

	public static class Contact extends OutlookResourceSave {

		public static final String ITEMTYPE = "contact";

		public Contact(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeContactItem";
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			// save dispatch
			Dispatch resource = getSaveResource();
			// title
			addPropertyIfNotNull(rdf, NIE.title, resource, "Subject");

			// email(s)
			Model model = rdf.getModel();
			URI uri = rdf.getDescribedUri();
			addEmailAddressIfNotNull(model, uri, resource, "Email1Address");
			addEmailAddressIfNotNull(model, uri, resource, "Email2Address");
			addEmailAddressIfNotNull(model, uri, resource, "Email3Address");
			addPropertyIfNotNull(rdf, NCO.websiteUrl, resource, "WebPage");

			// VCARD
			addPropertyIfNotNull(rdf, NCO.nameFamily, resource, "LastName");
			addPropertyIfNotNull(rdf, NCO.nameGiven, resource, "FirstName");
			addPropertyIfNotNull(rdf, NCO.nameAdditional, resource, "Title");
			addPropertyIfNotNull(rdf, NCO.fullname, resource, "Fullname");
			addPropertyIfNotNull(rdf, NCO.title, resource, "Title");
			addPropertyIfNotNull(rdf, NCO.note, getSaveResource(), "Body");
			addTelephoneNumberIfNotNull(model, uri, resource, "BusinessTelephoneNumber", "work", NCO.PhoneNumber);
			addTelephoneNumberIfNotNull(model, uri, resource, "HomeTelephoneNumber", "home", NCO.PhoneNumber);
			addTelephoneNumberIfNotNull(model, uri, resource, "MobileTelephoneNumber", "cell", NCO.CellPhoneNumber);
			


			        

			// Address(es)
			// Business Address
			readAddress(resource, "BusinessAddress", rdf, "work");
			// Home Address
			readAddress(resource, "HomeAddress", rdf, "home");
			// MailingAddress
			readAddress(resource, "MailingAddress", rdf, "mailing");
			// Other
			readAddress(resource, "OtherAddress", rdf, "other");
			
			// Affiliation and Organization
			String companies = getLiteralOf(resource, "Companies");
	        String companyName = getLiteralOf(resource, "CompanyName");
	        String jobTitle = getLiteralOf(resource, "JobTitle");
			if ((companyName != null)||(jobTitle != null)||(companies != null)) {
			    try {
                    URI affiliationResource = rdf.getValueFactory().createURI(getUri() + "_" + "Affiliation");
                    addStatement(rdf, uri, NCO.hasAffiliation, affiliationResource);
                    addStatement(rdf, affiliationResource, RDF.type, NCO.Affiliation);
                    if (jobTitle != null)
                        addStatement(rdf, affiliationResource, NCO.title, jobTitle);
                    if ((companyName != null)||(companies != null))
                    {
                        Resource organizationContactResource = rdf.getValueFactory().createURI(getUri() + "_" + "Organization");
                        addStatement(rdf, affiliationResource, NCO.org, organizationContactResource);
                        addStatement(rdf, organizationContactResource, RDF.type, NCO.OrganizationContact);
                        if (companies != null)
                            addStatement(rdf, organizationContactResource, NCO.fullname, companies);
                        if (companyName != null)
                            addStatement(rdf, organizationContactResource, NCO.fullname, companyName);
                    }
                }
                catch (ModelException e) {
                   throw new ModelRuntimeException(e);
                }
			}
		}

		public URI getType() {
			return NCO.Contact;
		}

		/**
		 * read the prefixed address from the person. if it exists, add it
		 * 
		 * @param d dispatch with person
		 * @param prefix "business" etc
		 */
		public void readAddress(Dispatch d, String prefix, RDFContainer rdf, String comment) {
			Variant var = Dispatch.get(d, prefix + "City");
			ValueFactory vf = rdf.getValueFactory();
			String city = (var == null) ? null : var.getString();
			var = Dispatch.get(d, prefix + "Country");
			String country = (var == null) ? null : var.getString();
			var = Dispatch.get(d, prefix + "PostOfficeBox");
			String pobox = (var == null) ? null : var.getString();
			var = Dispatch.get(d, prefix + "PostalCode");
			String plz = (var == null) ? null : var.getString();
			var = Dispatch.get(d, prefix + "State");
			String state = (var == null) ? null : var.getString();
			var = Dispatch.get(d, prefix + "Street");
			String street = (var == null) ? null : var.getString();

			if ((city != null) && (city.length() == 0))
				city = null;
			if ((country != null) && (country.length() == 0))
				country = null;
			if ((pobox != null) && (pobox.length() == 0))
				pobox = null;
			if ((plz != null) && (plz.length() == 0))
				plz = null;
			if ((state != null) && (state.length() == 0))
				state = null;
			if ((street != null) && (street.length() == 0))
				street = null;

			if ((city != null) || (country != null) || (pobox != null) || (plz != null) || (state != null)
					|| (street != null)) {
                try {
                    // create address
                    URI address = rdf.getValueFactory().createURI(getUri() + "_" + prefix);
                    rdf.add(vf.createStatement(address, RDF.type, NCO.PostalAddress));

                    if (city != null)
                        rdf.add(vf.createStatement(address, NCO.locality, vf.createLiteral(city)));
                    if (country != null)
                        rdf.add(vf.createStatement(address, NCO.country, vf.createLiteral(country)));
                    if (pobox != null)
                        rdf.add(vf.createStatement(address, NCO.pobox, vf.createLiteral(pobox)));
                    if (plz != null)
                        rdf.add(vf.createStatement(address, NCO.postalcode, vf.createLiteral(plz)));
                    if (state != null)
                        rdf.add(vf.createStatement(address, NCO.region, vf.createLiteral(state)));
                    if (street != null)
                        rdf.add(vf.createStatement(address, NCO.streetAddress, vf.createLiteral(street)));
                     
                    if (comment != null) {
                        addStatement(rdf, address, NCO.contactMediumComment, comment);
                    }
                    
                    // add the ano-statement to the result
                    rdf.add(NCO.hasPostalAddress, address);
                }
                catch (ModelException e) {
                    logger.error("ModelException while adding statements", e);
                }
			}
		}
	}

	public static class DistList extends OutlookResourceSave {

		public static final String ITEMTYPE = "distlist";

		public DistList(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeDistList";
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
		// TODO Auto-generated method stub

		}

		public URI getType() {
			return NCO.ContactGroup;
		}
	}

	public static class Document extends OutlookResource {

		public static final String ITEMTYPE = "document";

		public Document(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			Dispatch resource = getResource();
			addPropertyIfNotNull(rdf, NIE.title, resource, "Subject");
			addPropertyIfNotNull(rdf, NIE.plainTextContent, resource, "Body");
		}

		public URI getType() {
			return NFO.TextDocument;
		}
	}

	public static class Folder extends OutlookResource {

		public static final String ITEMTYPE = "folder";

		public Folder(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

		public Folder(OutlookCrawler crawler, Dispatch resource, String itemtype) {
			super(crawler, resource, itemtype);
		}

		public Folder(OutlookCrawler crawler, String uri, Dispatch resource, String itemType) {
			super(crawler, uri, resource, itemType);
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			addPropertyIfNotNull(rdf, NIE.title, getResource(), "Name");
		}

		public long getLastModified() {
			return new Date().getTime();
		}

		public URI getType() {
			return NFO.Folder;
		}
        
        /**
         * The oItemType constant identifying the standard type of element stored in this
         * folder. Examples are {@link OlObjectClass#olAppointment} or
         * {@link OlObjectClass#olMail}.
         * @return default item type
         */
        public int getDefaultItemType() {
            int result = Dispatch.get(getResource(), "DefaultItemType").toInt();
            return result;
        }
        
        /**
         * The oItemType constant identifying the standard type of element stored in this
         * folder. Examples are {@link OlObjectClass#olAppointment} or
         * {@link OlObjectClass#olMail}.
         * @return the default message class
         */
        public String getDefaultMessageClass() {
            Variant v = Dispatch.get(getResource(), "DefaultMessageClass");
            return v.toString();
        }
        
        /**
         * the name of the folder
         * @return the name or null
         */
        public String getName() {
            String s = getLiteralOf(getResource(), "Name");
            return s;
        }

		public boolean isFolder() {
			return true;
		}

	}

	public static class Mail extends OutlookResourceSave {

		public static final String ITEMTYPE = "mail";

		public Mail(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeMailItem";
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			Dispatch resource = getSaveResource();
			ValueFactory vf = rdf.getValueFactory();
			
			addPropertyIfNotNull(rdf, NMO.messageSubject, resource, "Subject");
			addDateIfNotNull(rdf, NMO.receivedDate, resource, "ReceivedTime");
			addDateIfNotNull(rdf, NMO.sentDate, resource, "SentOn");
			addPropertyIfNotNull(rdf, NMO.plainTextMessageContent, resource, "Body");

			String name = getLiteralOf(getSaveResource(), "SenderName");
			String mailbox = getLiteralOf(getSaveResource(), "SenderEmailAddress");
			if (!(name == null && mailbox == null)) {
                try {
                    URI from = vf.createURI(getUri() + "_FROM");
                    rdf.add(vf.createStatement(from, RDF.type, NCO.Contact));
                    if (name != null)
                        rdf.add(vf.createStatement(from, NCO.fullname, vf.createLiteral(name)));
                    if (mailbox != null) {
                        Resource emailAddressResource = UriUtil.generateRandomResource(rdf.getModel());
                        addStatement(rdf, from, NCO.hasEmailAddress, emailAddressResource);
                        addStatement(rdf, emailAddressResource, RDF.type, NCO.EmailAddress);
                        addStatement(rdf, emailAddressResource, NCO.emailAddress, vf.createLiteral(mailbox));
                    }
                    rdf.add(NMO.from, from);
                }
                catch (ModelException e) {
                    logger.error("ModelException while adding statements", e);
                }
			}

			// FIXME: Redemption seems to have a bug, so i use getResource() here.
			// if recipients would be retrieved from saveMailItem, its Items method is broken
            addRecipientsIfNotNull(rdf, getResource(), "Recipients", this);
		}

		public URI getType() {
			return NMO.Email;
		}

	}

	public static class Note extends OutlookResource {

		public static final String ITEMTYPE = "note";

		public Note(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			Dispatch resource = getResource();
			addPropertyIfNotNull(rdf, NIE.title, resource, "Subject");
			addPropertyIfNotNull(rdf, NIE.plainTextContent, resource, "Body");
		}

		public URI getType() {
		    // TODO get back here after introducing nfo:Note
			return NFO.TextDocument;
		}
	}

	/**
	 * a OutlookResource that has a "save" object from redemption. In the constructor, set the
	 * saveRedemptionClass field to the name of the redemption class.
	 */
	public abstract static class OutlookResourceSave extends OutlookResource {

        protected Logger logger = LoggerFactory.getLogger(getClass());
        
		protected String saveRedemptionClass = null;

		private Dispatch saveResource = null;

		protected OutlookResourceSave(OutlookCrawler crawler, Dispatch resource, String itemtype) {
			super(crawler, resource, itemtype);
		}

		public OutlookResourceSave(OutlookCrawler crawler, String url, Dispatch resource, String itemtype) {
			super(crawler, url, resource, itemtype);
		}

		/**
		 * return a save-contact using redemption. If redemption does not work, this returns the normal
		 * contact. Use this method to explicitly try to access more.
		 * 
		 * @return a save contact or the normal resource, if redemption is not available
		 */
		public Dispatch getSaveResource() {
			if (saveResource == null) {
				if (getOLCrawler().hasRedemption()) {
					try {
						saveResource = new ActiveXComponent(saveRedemptionClass);
					}
					catch (ComFailException x) {
						logger.warn("Redemption error, cannot get redemption object for "
								+ saveRedemptionClass);
						return null;
					}
					// connect the saveitem to the outlook item
					Dispatch.put(saveResource, "Item", getResource());
				}
				else
					return getResource();
			}
			return saveResource;
		}

		public void release() {
			if (saveResource != null) {
				saveResource.safeRelease();
			}
			super.release();
		}
	}

	public static class RootFolder extends OutlookResource {

		public static final String ITEMTYPE = "root";

		public RootFolder(OutlookCrawler crawler) {
			super(crawler, crawler.getUriPrefix() + ITEMTYPE, crawler.getOutlookMapi(), ITEMTYPE);
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			rdf.add(NIE.title, "Outlook root folder");
		}

		public long getLastModified() {
			return 0;
		}

		public URI getType() {
			return NFO.Folder;
		}

		public boolean isFolder() {
			return true;
		}

	}

	public static class Task extends OutlookResourceSave {

		public static final String ITEMTYPE = "task";

		public Task(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeTaskItem";
		}

		protected void addDetailData(RDFContainer rdf) throws IOException {
			Dispatch resource = getSaveResource();
			addPropertyIfNotNull(rdf, NIE.title, resource, "Subject");
			addPropertyIfNotNull(rdf, NIE.plainTextContent, resource, "Body");
			
			// task-specific
			addDateIfNotNull(rdf, NCAL.completed, resource, "DateCompleted");
			addNcalDateTimeIfNotNull(rdf, NCAL.due, resource, "DueDate");
		}

		public URI getType() {
			return NCAL.Todo;
		}

	}

	private static String createUri(OutlookCrawler crawler, Dispatch resource, String itemType) {
		if (crawler == null)
			throw new RuntimeException("passed in a null crawler for createUri");
		if (resource == null)
			throw new RuntimeException("passed in a null resource for createUri");
		return crawler.getUriPrefix() + itemType + "/" + Dispatch.get(resource, "EntryID").getString();
	}


	


    /**
	 * Factory method to create Wrappers. this looks at the passed Dispatch and sees what type it is and
	 * creates an according OutlookResource subclass
     * @return the wrapper or null, if the resource cannot be wrapped.
	 */
	public static OutlookResource createWrapperFor(OutlookCrawler crawler, Dispatch resource, Logger logger)  {
		try {
			// Item is the desired Item, do something about it
			int classID = Dispatch.get(resource, "Class").toInt();

			// Check out what kind of Item
			switch (classID) {
			// no breaks needed here, return ends the switch
			case OlObjectClass.olAppointment:
				return new OutlookResource.Appointment(crawler, resource);
			case OlObjectClass.olContact:
				return new OutlookResource.Contact(crawler, resource);
			case OlObjectClass.olDistributionList:
				return new OutlookResource.DistList(crawler, resource);
			case OlObjectClass.olDocument:
				return new OutlookResource.Document(crawler, resource);
			case OlObjectClass.olFolder:
				return createWrapperForFolder(crawler, resource);
			case OlObjectClass.olMail:
				return new OutlookResource.Mail(crawler, resource);
			case OlObjectClass.olNamespace:
				return new OutlookResource.RootFolder(crawler);
			case OlObjectClass.olNote:
				return new OutlookResource.Note(crawler, resource);
			case OlObjectClass.olTask:
				return new OutlookResource.Task(crawler, resource);
			case OlObjectClass.olJournal:
				return null; // we don't handle journal
			case OlObjectClass.olMeetingRequest:
				return null; // we don't handle meeting requests.
			case 0:
				return null;
			default:
				throw new UrlNotFoundException("no RDF mapping defined for Outlook.classID '"
						+ Integer.toString(classID) + "'. Item Ignored.");
			}

		}
        catch (UrlNotFoundException ex ) {
            logger.info("Outlook Cannot wrap resource: " + ex);
            return null;
        }
		catch (Exception ex) {
			logger.warn("Error creating Resourcewrapper: " + ex);
			return null;
		}
	}

	/**
	 * create a outlook resource wrapper for a uri Parse the uri and return the resource wrapper or null, if uri
	 * points to nowhere
	 * 
	 * This interface is primarily used to get the objects from the Jena/RDF side of the world
	 * 
	 * @return Resourcewrapper or null
	 */
	public static OutlookResource createWrapperFor(OutlookCrawler crawler, String uri, Logger logger){
		// Parts
		String itemType;
		String itemIdentity;
		String uriPrefix = crawler.getUriPrefix();

		try {

			if (!uri.startsWith(uriPrefix))
				throw new Exception("uri '" + uri + "' is not starting with '" + uriPrefix
						+ "'");

			// path inside outlook
			String pathS = uri.substring(uriPrefix.length());

			/**
			 * java 1.3.1 compability
			 */
			String[] path = pathS.split("/");

			/**
			 * Parts: 0="id"|"folders"|"folder" 1=value
			 */
			itemType = path[0];
			if (path.length > 1)
				itemIdentity = path[1];
			else
				itemIdentity = null;


		}
		catch (Exception ex) {
            if (logger.isInfoEnabled()) {
                logger.info("uri '" + uri + "' cannot be parsed." + ex.toString());
            }
			return null;
		}

		// now that the path is parsed, get the resource
		try {
			/**
			 * Appointment, Contact, DistList, Document, Mail, Note, Task can be created using the Namespace
			 * and the other createResource method
			 */
			if (itemType.equals(OutlookResource.Appointment.ITEMTYPE)
					|| itemType.equals(OutlookResource.Contact.ITEMTYPE)
					|| itemType.equals(OutlookResource.DistList.ITEMTYPE)
					|| itemType.equals(OutlookResource.Document.ITEMTYPE)
					|| itemType.equals(OutlookResource.Mail.ITEMTYPE)
					|| itemType.equals(OutlookResource.Note.ITEMTYPE)
					|| itemType.equals(OutlookResource.Task.ITEMTYPE)) {
				Variant variant = Dispatch.call(crawler.getOutlookMapi(), "GetItemFromID", itemIdentity);
				if (variant == null)
					throw new Exception("GetItemFromID returned null for id " + itemIdentity);
				Dispatch item = variant.toDispatch();
				OutlookResource wrapper = createWrapperFor(crawler, item, logger);

				return wrapper;
			}
			
			// The RootFolder
			if (itemType.equals(OutlookResource.RootFolder.ITEMTYPE))
				return new OutlookResource.RootFolder(crawler);

			// a single Folder
			if (itemType.equals(OutlookResource.Folder.ITEMTYPE) || itemType.equals(OutlookResource.Calendar.ITEMTYPE)) {
				return OutlookResource.createWrapperForFolder(crawler, itemIdentity, itemType, logger);
			}

		}
		catch (ComFailException ex) {
			// this may mean that outlook crashed. count this and try to start
			// up
			crawler.crashChecker(ex);
		}
		catch (Exception ex) {
            if (logger.isInfoEnabled()) {
                logger.info("Cannot resolve outlook uri '" + uri + "': " + ex.toString(), ex);
            }
		}
		return null;
	}

	/**
	 * Factory method to create Wrappers for Folder Dispatchs. this looks at the passed Dispatch and sees what
	 * type it is and creates an according OLFolder or other resource
	 */
	public static OutlookResource.Folder createWrapperForFolder(OutlookCrawler crawler, Dispatch folder) {
		// check if folder is a calendar
		int defaultItemType = Dispatch.get(folder, "DefaultItemType").toInt();
		if (defaultItemType == OutlookCrawler.OLConst.olAppointmentItem)
			return new OutlookResource.Calendar(crawler, folder);
		else
			return new OutlookResource.Folder(crawler, folder);
	}

	/**
	 * Factory method to create Wrappers for Folder Dispatchs. this looks at the passed Dispatch and sees what
	 * type it is and creates an according OLFolder or other resource
	 */
	public static OutlookResource.Folder createWrapperForFolder(OutlookCrawler crawler, String itemId,
			String expectedItemType, Logger logger) throws Exception {
		Variant variant = Dispatch.call(crawler.getOutlookMapi(), "GetItemFromID", itemId);
		if (variant == null)
			throw new Exception("GetItemFromID returned null for id " + itemId);
		Dispatch item = variant.toDispatch();
		OutlookResource.Folder folder = createWrapperForFolder(crawler, item);
		if (!expectedItemType.equals(folder.getItemType())) {
			String msg = "created wrapper for item " + itemId + "  expected " + expectedItemType
					+ " but got " + folder.getItemType();
			logger.warn(msg);
			throw new Exception(msg);
		}
		return folder;
	}

	private OutlookCrawler crawler;

	private String itemType;

	private Dispatch resource = null;

	private String uri;

	/**
	 * Special constructor for subclasses for a little convenience. argh, in Delphi i would have done this
	 * even easier.
	 * 
	 * @param crawler crawler
	 * @param resource Outlook resource that has to implement "EntryID" for identifier
	 * @param itemType itemtype for path
	 * @throws Exception
	 */
	protected OutlookResource(OutlookCrawler crawler, Dispatch resource, String itemType) {
		this(crawler, createUri(crawler, resource, itemType), resource, itemType);
	}

	/**
	 * Normal constructor with url and resource. Subclasses have to build the url themselves
	 * 
	 * @param crawler Crawlercrawler
	 * @param uri uri of the resource
	 * @param resource object of the resource
	 * @param itemType itemtype for path
	 * @throws Exception
	 */
	protected OutlookResource(OutlookCrawler crawler, String uri, Dispatch resource, String itemType) {
		if (crawler == null)
			throw new RuntimeException("crawler must not be null for OutlookResource");
		this.crawler = crawler;
		this.uri = uri;
		this.resource = resource;
		this.itemType = itemType;
	}
	
	/**
	 * Add the data about this object.
	 * This adds basic data (such as the ItemId) and then calls
	 * addDetailData to get the individual data special for the
	 * subclasses.
	 * @param rdf the container to add the rdf to
	 * @throws IOException if reading is not possible
	 */
	public void addData(RDFContainer rdf) throws IOException {
	    // rare case that it does not have an entryid: the root folder
	    String entryid;
	    try {
    	    Variant entryidv = Dispatch.get(resource, "EntryID");
    	    entryid = (entryidv != null) ? entryidv.getString() : null;
	    } catch (Exception x) {
	        entryid = null;
	    }
	    if (entryid == null)  {
	        // should never happen, but can happen with root-folder
	        if (!(this instanceof OutlookResource.RootFolder))
	            logger.error("cannot get id of "+getUri()+", it is null. type "+getType());
	    }
	    else {
	        rdf.add(NAO.identifier, OutlookCrawler.ITEMID_IDENTIFIERPREFIX + entryid);
	    }
	    addDetailData(rdf);
	}

    /**
     * Add the detail data about this object.
     * @param rdf the container to add the rdf to
     * @throws IOException if reading is not possible
     */
	protected abstract void addDetailData(RDFContainer rdf) throws IOException;

	/** ******************* END OF OutlookResource ****************************** */

	protected void addDateIfNotNull(RDFContainer rdf, URI property, Dispatch resource, String dispName) {
		Date date = getDateOf(resource, dispName);
		if (date != null) {
			rdf.add(property, date);
		}
	}
	
	protected void addNcalDateTimeIfNotNull(RDFContainer rdf, URI property, Dispatch resource, String dispName) {
	    Date date = getDateOf(resource, dispName);
	    if (date != null) {
            Resource ncalDateTimeResource = UriUtil.generateRandomResource(rdf.getModel());
            rdf.add(property, ncalDateTimeResource);
            addStatement(rdf, ncalDateTimeResource, RDF.type, NCAL.NcalDateTime);
            addStatement(rdf, ncalDateTimeResource, NCAL.dateTime, DateUtil.dateTime2String(date), XSD._dateTime);
        }
	}
	
	protected void addEmailAddressIfNotNull(Model model, Resource parentNode, Dispatch resource, String dispName) {
	    String addressString = getLiteralOf(resource, dispName);
        if (addressString != null)
        {
    	    Resource emailAddressNode = UriUtil.generateRandomResource(model);
    	    model.addStatement(parentNode,NCO.hasEmailAddress,emailAddressNode);
    	    model.addStatement(emailAddressNode, RDF.type, NCO.EmailAddress);
    	    model.addStatement(emailAddressNode, NCO.emailAddress, addressString);
        }
	}
	
	protected void addTelephoneNumberIfNotNull(Model model, Resource parentNode, Dispatch resource, String dispName, String comment, URI type) {
        String telephoneString = getLiteralOf(resource, dispName);
        if (telephoneString != null)
        {
            Resource telephoneNumberNode = UriUtil.generateRandomResource(model);
            model.addStatement(parentNode,NCO.hasPhoneNumber,telephoneNumberNode);
            model.addStatement(telephoneNumberNode, RDF.type, type);
            model.addStatement(telephoneNumberNode, NCO.phoneNumber, telephoneString);
            if (comment != null) {
                model.addStatement(telephoneNumberNode, NCO.contactMediumComment, comment);
            }
        }
    }

	/**
	 * protected helper method. Extract the value of the DispName from the disp d. If it is not null, create a
	 * new triple of the resource r with (r,p, (string)).
	 */
	protected void addPropertyIfNotNull(RDFContainer rdf, URI property, Dispatch disp, String dispName) {
		String s = getLiteralOf(disp, dispName);
		if (s != null)
			rdf.add(property, s);
	}
	
	protected void addStatement(RDFContainer rdf, Resource subject, URI predicate, String label, URI datatype) {
	    addStatement(rdf,subject,predicate,rdf.getModel().createDatatypeLiteral(label, datatype));
	}
	
	protected void addStatement(RDFContainer rdf, Resource subject, URI predicate, String label) {
        addStatement(rdf,subject,predicate,rdf.getModel().createPlainLiteral(label));
    }
	
    protected void addStatement(RDFContainer rdf, Resource subject, URI predicate, Node object) {
        rdf.getModel().addStatement(subject, predicate, object);
    }
    
    /**
     * Add the recipients of an e-mail or an appointment
     * @param rdf the parent rdf container. Properties will be added linked to this URI
     * @param parentResource the parent node to add to 
     * @param resource the recipients resource
     * @param dispName diplay name
     */
    protected void addRecipientsIfNotNull(RDFContainer rdf, Dispatch resource, String dispName,
            OutlookResource parentResource) {
        if (parentResource == null)
            throw new NullPointerException("Error: parentResource=null");
        Variant recipients = Dispatch.get(resource, dispName);
        ValueFactory vf = rdf.getValueFactory();
        if (recipients != null) {
            Dispatch recipientsD = recipients.toDispatch();
            int count = Dispatch.get(recipientsD, "Count").toInt();
            logger.info("adding e-mail, found recipients: "+count);
            
            // int dispId = Dispatch.getIDOfName(folders, "Item");
            for (int i = 1; i <= count; i++) {
                try {
                    Dispatch recipient = Dispatch.invoke(recipientsD, "Item", Dispatch.Get,
                        new Object[] { new Integer(i) }, new int[1]).toDispatch();
                    String type = getLiteralOf(recipient, "Type");
                    String name = getLiteralOf(recipient, "Name");
                    String mailbox = getLiteralOf(recipient, "Address");
                    if (!(name == null && mailbox == null)) {
                        URI rec = vf.createURI(getUri() + "_recipient" + i);
                        rdf.add(vf.createStatement(rec, RDF.type, NCO.Contact));
                        if (name != null)
                            rdf.add(vf.createStatement(rec, NCO.fullname, vf.createLiteral(name)));
                        if (mailbox != null) {
                            Resource emailAddressResource = UriUtil.generateRandomResource(rdf.getModel());
                            addStatement(rdf, rec, NCO.hasEmailAddress, emailAddressResource);
                            addStatement(rdf, emailAddressResource, RDF.type, NCO.EmailAddress);
                            addStatement(rdf, emailAddressResource, NCO.emailAddress, vf.createLiteral(mailbox));
                        }

                        // mail
                        if (parentResource instanceof OutlookResource.Mail)
                        {
                            if (type.equals(Integer.toString(OlObjectClass.olTo))) {
                                rdf.add(NMO.to, rec);
                            }
                            else if (type.equals(Integer.toString(OlObjectClass.olCC))) {
                                rdf.add(NMO.cc, rec);
                            }
                            else if (type.equals(Integer.toString(OlObjectClass.olBCC))) {
                                rdf.add(NMO.bcc, rec);
                            } 
                            else 
                            {
                                logger.warn("cannot connect mail recipient type '"+type+"', using NMO.to instead");
                                rdf.add(NMO.to, rec);
                            }
                        } else if (parentResource instanceof OutlookResource.Appointment)
                        // appointment
                        {
                            // roles: ncal:chairRole, ncal:nonParticipantRole, ncal:optParticipantRole, ncal:reqParticipantRole
                            if (type.equals(Integer.toString(OlObjectClass.olOptional))) {
                                rdf.add(NCAL.attendee, rec);
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Attendee));
                                rdf.add(vf.createStatement(rec, NCAL.role, NCAL.optParticipantRole));
                            }
                            else if (type.equals(Integer.toString(OlObjectClass.olOrganizer))) {
                                rdf.add(NCAL.attendee, rec);
                                rdf.add(NCAL.organizer, rec);
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Attendee));
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Organizer));
                                rdf.add(vf.createStatement(rec, NCAL.role, NCAL.chairRole));
                            }
                            else if (type.equals(Integer.toString(OlObjectClass.olRequired))) {
                                rdf.add(NCAL.attendee, rec);
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Attendee));
                                rdf.add(vf.createStatement(rec, NCAL.role, NCAL.reqParticipantRole));
                            }
                            else if (type.equals(Integer.toString(OlObjectClass.olResource))) {
                                rdf.add(NCAL.attendee, rec);
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Attendee));
                                rdf.add(vf.createStatement(rec, RDFS.comment, vf.createLiteral("resource")));
                            }
                            else 
                            {
                                logger.warn("cannot connect Appointment recipient type '"+type+"', using NCAL.attendee instead");
                                rdf.add(NCAL.attendee, rec);
                                rdf.add(vf.createStatement(rec, RDF.type, NCAL.Attendee));
                            }
                        } else 
                        {
                            logger.warn("cannot add recipients for type '"+parentResource.getClass()+
                                "': I only understand OutlookResource.Appointment or OutlookResource.Mail. " +
                                "Using connection NIE:hasLogicalPart to connect "+rdf.getDescribedUri()+" to "+rec);
                            rdf.add(NIE.hasLogicalPart, rec);
                            
                        }
                            
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

	/**
	 * finalizer for releasing the activeX
	 * 
	 * //TODO This one runs in its own thread and is therefore dangerous to COM. I had to comment out the
	 *       release method. Perhaps I will come up with a solution sometime
	 */
	protected void finalize() throws Throwable {
		if (resource != null) {
		    logger.warn("This resource was not released, but in finalize: " + getUri());
			release();
		}
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticdesktop.aperture.accessor.DataAccessor#getDataObjectIfModified(java.lang.String,
	 *      org.semanticdesktop.aperture.datasource.DataSource,
	 *      org.semanticdesktop.aperture.accessor.AccessData, java.util.Map,
	 *      org.semanticdesktop.aperture.accessor.RDFContainerFactory)
	 */
	public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
			Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
		return null;
	}

	/**
	 * protected helper method to extract literal values from Dispatchs
	 * 
	 * @param disp the Dispatch Object to extract from
	 * @param dispName
	 * @return a string or null if the string cannot be returned
	 */
	protected Date getDateOf(Dispatch disp, String dispName) {

		// Get Variant
		try {
			Variant var = Dispatch.get(disp, dispName);
			if ((var == null) || (var.isNull()))
				return null;
			else if (var.getvt() == Variant.VariantDate) {
				VariantDate vDat = new VariantDate(var.toDate());
				Date dDat = vDat.getDate();
				return dDat;
			}
			else
				throw new RuntimeException("cannot read " + dispName
						+ " from dispatch, does not return a Date-Value");

		}
		catch (ComFailException x) {
			logger.warn("Error on com for dispname " + dispName + ": " + x);
			return null;
		}
	}

	public String getItemType() {
		return itemType;
	}

	/**
	 * get the last modification time of this resource. Return the standard Java date value (milliseconds
	 * since epoch)
	 */
	public long getLastModified() {
		Variant var;
		try {
			var = Dispatch.get(resource, "LastModificationTime");
		}
		catch (ComFailException x) {
			logger.warn("cannot read last-modification time of " + getUri() + " of type " + getItemType());
			return 0;
		}
		if ((var == null) || (var.isNull())) {
			logger.warn("cannot read LastModificationTime, no value");
			return 0;
		}
		else if (var.getvt() == Variant.VariantDate) {
			double d = var.getDate();
			VariantDate vDat = new VariantDate(var.toDate());
			if (logger.isInfoEnabled()) {
				logger.info("This has lastModified: " + DateUtil.dateTime2String(vDat.getDate())
						+ " with double " + Double.toString(d));
			}
			return vDat.getDate().getTime();
		}
		else {
			logger.warn("cannot read LastModificationTime, type is not date but: " + var.getvt());
			return 0;
		}
	}

	/**
	 * protected helper method to extract literal values from Dispatchs
	 * 
	 * @param disp the Dispatch Object to extract from
	 * @param dispName
	 * @return a string or null if the string cannot be returned
	 */
	protected String getLiteralOf(Dispatch disp, String dispName) {

		// Get Variant
		try {
			String s = null;
			Variant var = Dispatch.get(disp, dispName);
			if ((var == null) || (var.isNull()))
				s = null;
			else if (var.getvt() == Variant.VariantDate) {
				VariantDate vDat = new VariantDate(var.toDate());
				Date dDat = vDat.getDate();
				s = DateUtil.dateTime2String(dDat);
			}
			else
				s = var.toString();

			// empty strings are null
			if ((s != null) && (s.length() == 0))
				s = null;
			return s;

		}
		catch (ComFailException x) {
			logger.warn("Error on com for dispname " + dispName + ": " + x);
			return null;
		}
	}

	/**
	 * get the crawler that hosts this resource
	 * 
	 * @return  the crawler that hosts this resource
	 */
	public OutlookCrawler getOLCrawler() {
		return crawler;
	}

	/*
	 * public static class Recipient extends OutlookResource { public static final String ITEMTYPE =
	 * "recipient"; public Recipient(OLCrawler crawler, Dispatch resource) throws Exception { super(crawler,
	 * resource, ITEMTYPE); } }
	 */

	public Dispatch getResource() {
		return resource;
	}

	public abstract URI getType();

	public String getUri() {
		return uri;
	}

	public boolean isFolder() {
		return false;
	}

	/**
	 * release
	 */
	public void release() {
		if (resource != null)
			resource.safeRelease();
		resource = null;

	}

}

/*
 * $Log$
 * Revision 1.11  2007/07/10 15:05:29  leo_sauermann
 * Fixed the example aperture crawler, found a memory leak :-/
 *
 * Revision 1.10  2007/03/08 22:03:40  cfmfluit
 * replaced java.util.logging-based logging with SLF4J
 *
 * Revision 1.9  2007/03/08 13:23:03  cfmfluit
 * ValueFactory now throws ModelExceptions, rather than letting the implementation log the exception and return null. Adapted all classes using a ValueFactory accordingly. This fixes potential NPEs when the ValueFactory methods fail and the returned null values would be used accidentally to e.g. add a statement.
 *
 * Revision 1.8  2006/11/29 14:34:22  mylka
 * Merged three dublin core vocabularies with the data vocabulary into one single DATA class...
 *
 * Revision 1.7  2006/11/29 11:01:37  mylka
 * Changed 'Dublin Core' properties from the data vocabulary to their 'real' Dublin Core equivalents.
 *
 * Revision 1.6  2006/11/14 16:13:30  mylka
 * The Great Merge.
 *
 * Revision 1.4.2.1  2006/10/28 13:20:39  mylka
 * Aperture RDF2Go first draft...
 *
 * Revision 1.4  2006/10/20 17:43:00  mylka
 * removed the umlauts from the copyright message.
 *
 * Revision 1.3  2006/03/02 10:42:46  gromgull
 * Moved generated classes to _GEN and created new classes extending these with additional constants.
 *
 * Revision 1.2  2006/02/27 14:36:30  leo_sauermann
 * corrected license: (C) DFKI, OSL 3.0
 *
 * Revision 1.1  2006/02/27 14:05:48  leo_sauermann
 * Implemented First version of Outlook. Added the vocabularyWriter for ease of vocabulary and some launch configs to run it. Added new dependencies (jacob)
 * Revision 1.11 2005/04/29 12:53:00 sauermann changes for evaluation
 * 
 * Revision 1.10 2005/04/01 17:55:03 sauermann date updates to
 * http://lists.w3.org/Archives/Public/www-rdf-calendar/2005Feb/0016.html
 * 
 * Revision 1.9 2005/03/01 13:59:46 sauermann *** empty log message ***
 * 
 * Revision 1.8 2005/02/09 16:06:33 sauermann bugfixes: person missed parent
 * 
 * Revision 1.7 2005/02/08 13:22:05 maus *** empty log message ***
 * 
 * Revision 1.6 2005/02/08 08:45:23 sauermann *** empty log message ***
 * 
 * Revision 1.5 2005/02/03 14:31:48 jshen *** empty log message ***
 * 
 * Revision 1.4 2005/02/03 14:24:59 jshen *** empty log message ***
 * 
 * Revision 1.3 2005/02/02 17:41:26 jshen new outlook crawler with GNOMAIL vocabulary
 * 
 * Revision 1.2 2005/02/02 08:58:35 sauermann updated to changes in gnowsis
 * 
 * Revision 1.1 2005/01/13 13:16:04 sauermann project restructuring
 * 
 * Revision 1.2 2004/11/25 14:04:48 sauermann *** empty log message ***
 * 
 * Revision 1.1 2004/11/22 14:43:47 sauermann init
 * 
 * Revision 1.9 2004/09/20 11:41:57 sauermann User Interface updates and bugfixes made in Vienna
 * 
 * Revision 1.8 2004/09/09 15:38:32 kiesel - added CVS tags
 * 
 */
