/*
 * Copyright (c) 2005 - 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.vocabulary.DATA_GEN;
import org.semanticdesktop.aperture.vocabulary.ICAL;
import org.semanticdesktop.aperture.vocabulary.VCARD;

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

	/**
	 * 
	 * 
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

		protected void addData(RDFContainer rdf) throws IOException {
			// save dispatch
			Dispatch resource = getSaveResource();

			// type is already added by AccessData.

			// add label
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");
			// body
			addPropertyIfNotNull(rdf, DATA_GEN.fullText, resource, "Body");

			// uid
			addPropertyIfNotNull(rdf, DATA_GEN.msOLUID, resource, "EntryID");

			// dtstamp, this is UTZ, so no Timezone?
			addDateIfNotNull(rdf, ICAL.dtstamp, resource, "CreationTime");
			addDateIfNotNull(rdf, ICAL.lastModified, resource, "LastModificationTime");
			// dtstart in local time, lets assume you are all in vienna
			addDateIfNotNull(rdf, ICAL.dtstart, resource, "Start");
			addDateIfNotNull(rdf, ICAL.dtend, resource, "End");
			addDateIfNotNull(rdf, ICAL.dtstamp, resource, "CreationTime");
			// location
			addPropertyIfNotNull(rdf, ICAL.location, resource, "Location");
		}

		public URI getType() {
			return ICAL.Vevent;
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

		protected void addData(RDFContainer rdf) throws IOException {
			// save dispatch
			Dispatch resource = getSaveResource();
			// title
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");

			// email(s)
			addPropertyIfNotNull(rdf, DATA_GEN.emailAddress, resource, "Email1Address");
			addPropertyIfNotNull(rdf, DATA_GEN.emailAddress, resource, "Email2Address");
			addPropertyIfNotNull(rdf, DATA_GEN.emailAddress, resource, "Email3Address");
			addPropertyIfNotNull(rdf, DATA_GEN.homepage, resource, "WebPage");

			// VCARD
			addPropertyIfNotNull(rdf, VCARD.nameFamily, resource, "LastName");
			addPropertyIfNotNull(rdf, VCARD.nameGiven, resource, "FirstName");
			addPropertyIfNotNull(rdf, VCARD.nameAdditional, resource, "Title");
			addPropertyIfNotNull(rdf, VCARD.fullname, resource, "Fullname");
			addPropertyIfNotNull(rdf, VCARD.org, resource, "Companies");
			addPropertyIfNotNull(rdf, VCARD.title, resource, "Title");
			addPropertyIfNotNull(rdf, VCARD.note, getSaveResource(), "Body");
			addPropertyIfNotNull(rdf, VCARD.telWork, resource, "BusinessTelephoneNumber");
			addPropertyIfNotNull(rdf, VCARD.telHome, resource, "HomeTelephoneNumber");
			addPropertyIfNotNull(rdf, VCARD.telCell, resource, "MobileTelephoneNumber");

			// Address(es)
			// Business Address
			readAddress(resource, "BusinessAddress", rdf, VCARD.addressWork);
			// Home Address
			readAddress(resource, "HomeAddress", rdf, VCARD.addressHome);
			// MailingAddress
			readAddress(resource, "MailingAddress", rdf, VCARD.addressPostal);
			// Other
			readAddress(resource, "OtherAddress", rdf, VCARD.address);
		}

		public URI getType() {
			return VCARD.VCard;
		}

		/**
		 * read the prefixed address from the person. if it exists, add it
		 * 
		 * @param d dispatch with person
		 * @param prefix "business" etc
		 */
		public void readAddress(Dispatch d, String prefix, RDFContainer rdf, URI addressRelation) {
			Variant var = Dispatch.get(d, prefix + "City");
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
				// create address
				URI address = new URIImpl(getUri() + "_" + prefix);
				rdf.add(new StatementImpl(address, RDF.TYPE, VCARD.Address));
				rdf.add(new StatementImpl(address, RDF.TYPE, VCARD.Address));

				if (city != null)
					rdf.add(new StatementImpl(address, VCARD.locality, new LiteralImpl(city)));
				if (country != null)
					rdf.add(new StatementImpl(address, VCARD.country, new LiteralImpl(country)));
				if (pobox != null)
					rdf.add(new StatementImpl(address, VCARD.pobox, new LiteralImpl(pobox)));
				if (plz != null)
					rdf.add(new StatementImpl(address, VCARD.postalcode, new LiteralImpl(plz)));
				if (state != null)
					rdf.add(new StatementImpl(address, VCARD.region, new LiteralImpl(state)));
				if (street != null)
					rdf.add(new StatementImpl(address, VCARD.streetAddress, new LiteralImpl(street)));
				// add the ano-statement to the result
				rdf.add(addressRelation, address);
			}
		}
	}

	public static class DistList extends OutlookResourceSave {

		public static final String ITEMTYPE = "distlist";

		public DistList(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
			saveRedemptionClass = "Redemption.SafeDistList";
		}

		protected void addData(RDFContainer rdf) throws IOException {
		// TODO Auto-generated method stub

		}

		public URI getType() {
			return DATA_GEN.MSOLDistList;
		}
	}

	public static class Document extends OutlookResource {

		public static final String ITEMTYPE = "document";

		public Document(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

		protected void addData(RDFContainer rdf) throws IOException {
			Dispatch resource = getResource();
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");
			addPropertyIfNotNull(rdf, DATA_GEN.fullText, resource, "Body");
		}

		public URI getType() {
			return DATA_GEN.Document;
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

		protected void addData(RDFContainer rdf) throws IOException {
			addPropertyIfNotNull(rdf, DATA_GEN.title, getResource(), "Name");

		}

		public long getLastModified() {
			return new Date().getTime();
		}

		public URI getType() {
			return DATA_GEN.FolderDataObject;
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

		protected void addData(RDFContainer rdf) throws IOException {
			Dispatch resource = getSaveResource();

			addPropertyIfNotNull(rdf, DATA_GEN.subject, resource, "Subject");
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");
			addDateIfNotNull(rdf, DATA_GEN.receivedDate, resource, "ReceivedTime");
			addDateIfNotNull(rdf, DATA_GEN.sentDate, resource, "SentOn");
			addPropertyIfNotNull(rdf, DATA_GEN.fullText, resource, "Body");

			String name = getLiteralOf(getSaveResource(), "SenderName");
			String mailbox = getLiteralOf(getSaveResource(), "SenderEmailAddress");
			if (!(name == null && mailbox == null)) {
				URI from = new URIImpl(getUri() + "_FROM");
				rdf.add(new StatementImpl(from, RDF.TYPE, DATA_GEN.Agent));
				if (name != null)
					rdf.add(new StatementImpl(from, DATA_GEN.name, new LiteralImpl(name)));
				if (mailbox != null) {
					rdf.add(new StatementImpl(from, DATA_GEN.emailAddress, new LiteralImpl(mailbox)));
				}
				rdf.add(DATA_GEN.from, from);
			}

			// FIXME: Redemption seems to have a bug, so i use getResource() here.
			// if recipients would be retrieved from saveMailItem, its Items method is broken
			Variant recipients = Dispatch.get(getResource(), "Recipients");
			if (recipients != null) {
				Dispatch recipientsD = recipients.toDispatch();
				int count = Dispatch.get(recipientsD, "Count").toInt();
				log.finest("adding e-mail, found recipients: "+count);
				
				// int dispId = Dispatch.getIDOfName(folders, "Item");
				for (int i = 1; i <= count; i++) {
					try {
						Dispatch recipient = Dispatch.invoke(recipientsD, "Item", Dispatch.Get,
							new Object[] { new Integer(i) }, new int[1]).toDispatch();
						String type = getLiteralOf(recipient, "Type");
						name = getLiteralOf(recipient, "Name");
						mailbox = getLiteralOf(recipient, "Address");
						if (!(name == null && mailbox == null)) {
							URI rec = new URIImpl(getUri() + "_recipient" + i);
							rdf.add(new StatementImpl(rec, RDF.TYPE, DATA_GEN.Agent));
							if (name != null)
								rdf.add(new StatementImpl(rec, DATA_GEN.name, new LiteralImpl(name)));
							if (mailbox != null) {
								rdf.add(new StatementImpl(rec, DATA_GEN.emailAddress, new LiteralImpl(mailbox)));
							}

							if (type.equals(Integer.toString(OlObjectClass.olTo))) {
								rdf.add(DATA_GEN.to, rec);
							}
							else if (type.equals(Integer.toString(OlObjectClass.olCC))) {
								rdf.add(DATA_GEN.cc, rec);
							}
							else if (type.equals(Integer.toString(OlObjectClass.olBCC))) {
								rdf.add(DATA_GEN.bcc, rec);
							}
						}
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		public URI getType() {
			return DATA_GEN.Email;
		}

	}

	public static class Note extends OutlookResource {

		public static final String ITEMTYPE = "note";

		public Note(OutlookCrawler crawler, Dispatch resource) {
			super(crawler, resource, ITEMTYPE);
		}

		protected void addData(RDFContainer rdf) throws IOException {
			Dispatch resource = getResource();
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");
			addPropertyIfNotNull(rdf, DATA_GEN.fullText, resource, "Body");
		}

		public URI getType() {
			return DATA_GEN.MSOLNote;
		}
	}

	/**
	 * a OutlookResource that has a "save" object from redemption. In the constructor, set the
	 * saveRedemptionClass field to the name of the redemption class.
	 */
	public abstract static class OutlookResourceSave extends OutlookResource {

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
						OutlookCrawler.log.fine("Redemption error, cannot get redemption object for "
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

		protected void addData(RDFContainer rdf) throws IOException {
			rdf.add(DATA_GEN.title, "Outlook root folder");
		}

		public long getLastModified() {
			return 0;
		}

		public URI getType() {
			return DATA_GEN.FolderDataObject;
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

		protected void addData(RDFContainer rdf) throws IOException {
			Dispatch resource = getSaveResource();
			addPropertyIfNotNull(rdf, DATA_GEN.title, resource, "Subject");
			addPropertyIfNotNull(rdf, DATA_GEN.fullText, resource, "Body");
			
			// task-specific
			addDateIfNotNull(rdf, DATA_GEN.msolCompletedDate, resource, "DateCompleted");
			addDateIfNotNull(rdf, DATA_GEN.msolDueDate, resource, "DueDate");
		}

		public URI getType() {
			return DATA_GEN.MSOLTask;
		}

	}

	protected static Logger log = Logger.getLogger(OutlookResource.class.getName());
	
	
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
	 */
	public static OutlookResource createWrapperFor(OutlookCrawler crawler, Dispatch resource) {
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
				throw new UrlNotFoundException("Outlook: unknown Outlook.classID '"
						+ Integer.toString(classID) + "'");
			}

		}
		catch (Exception ex) {
			OutlookCrawler.log.info("Error creating Resourcewrapper: " + ex);
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
	public static OutlookResource createWrapperFor(OutlookCrawler crawler, String uri){
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
			log.fine("uri '" + uri + "' cannot be parsed." + ex.toString());
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
				OutlookResource wrapper = createWrapperFor(crawler, item);

				return wrapper;
			}
			
			// The RootFolder
			if (itemType.equals(OutlookResource.RootFolder.ITEMTYPE))
				return new OutlookResource.RootFolder(crawler);

			// a single Folder
			if (itemType.equals(OutlookResource.Folder.ITEMTYPE) || itemType.equals(OutlookResource.Calendar.ITEMTYPE)) {
				return OutlookResource.createWrapperForFolder(crawler, itemIdentity, itemType);
			}

		}
		catch (ComFailException ex) {
			// this may mean that outlook crashed. count this and try to start
			// up
			crawler.crashChecker(ex);
		}
		catch (Exception ex) {
			log.info("Can not resolve outlook uri '" + uri + "': " + ex.toString());
			if (log.isLoggable(Level.FINE))
				ex.printStackTrace();
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
			String expectedItemType) throws Exception {
		Variant variant = Dispatch.call(crawler.getOutlookMapi(), "GetItemFromID", itemId);
		if (variant == null)
			throw new Exception("GetItemFromID returned null for id " + itemId);
		Dispatch item = variant.toDispatch();
		OutlookResource.Folder folder = createWrapperForFolder(crawler, item);
		if (!expectedItemType.equals(folder.getItemType())) {
			String msg = "created wrapper for item " + itemId + "  expected " + expectedItemType
					+ " but got " + folder.getItemType();
			OutlookCrawler.log.warning(msg);
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
	 * @param itemType itemType for path
	 * @throws Exception
	 */
	protected OutlookResource(OutlookCrawler crawler, Dispatch resource, String itemType) {
		this(crawler, createUri(crawler, resource, itemType), resource, itemType);
	}

	/**
	 * Normal constructor with url and resource. Subclasses have to build the url themselves
	 * 
	 * @param crawler Crawlercrawler
	 * @param uri url of the resource
	 * @param resource object of the resource
	 * @param itemType itemType for path
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
	 * add more data about this object
	 */
	protected abstract void addData(RDFContainer rdf) throws IOException;

	/** ******************* END OF OutlookResource ****************************** */

	protected void addDateIfNotNull(RDFContainer rdf, URI property, Dispatch resource, String dispName) {
		Date date = getDateOf(resource, dispName);
		if (date != null) {
			rdf.add(property, date);
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

	/**
	 * finalizer for releasing the activeX
	 * <p>
	 * TODO This one runs in its own thread and is therefore dangerous to COM. I had to comment out the
	 *       release method. Perhaps I will come up with a solution sometime
	 * </p>
	 */
	protected void finalize() throws Throwable {
		if (resource != null) {
			release();
			OutlookCrawler.log.finer("finalize released " + getUri());
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
		log.finest("get data of " + url);
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
			OutlookCrawler.log.finer("Error on com for dispname " + dispName + ": " + x);
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
			log.severe("cannot read last-modification time of " + getUri() + " of type " + getItemType());
			return 0;
		}
		if ((var == null) || (var.isNull())) {
			log.warning("cannot read LastModificationTime, no value");
			return 0;
		}
		else if (var.getvt() == Variant.VariantDate) {
			double d = var.getDate();
			VariantDate vDat = new VariantDate(var.toDate());
			if (log.isLoggable(Level.FINEST)) {
				log.finest("This has lastModified: " + DateUtil.dateTime2String(vDat.getDate())
						+ " with double " + Double.toString(d));
			}
			return vDat.getDate().getTime();
		}
		else {
			log.warning("cannot read LastModificationTime, type is not date but: " + var.getvt());
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
			OutlookCrawler.log.finer("Error on com for dispname " + dispName + ": " + x);
			return null;
		}
	}

	/**
	 * get the crawler that hosts this resource
	 * 
	 * @return The OutlookCrawler
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
 * Revision 1.5  2006/10/31 16:53:47  mylka
 * The javadoc creation doesn't make any warnings
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
