/*
 * Copyright (c) 2005 - 2007 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * <h1>Outlook Crawler</h1>
 * <p>
 * Crawler that wraps Microsoft Outlook. It is not very exhaustive and can not read all variables out of an
 * outlook system.
 * </p>
 * <h2>Order of crawling</h2>
 * <p>There have been requests to crawl e-mail after everything else, because e-mail takes very
 * long. 
 * To distinguish the e-mail folders to be crawled, we had to find a way to identify them.
 * DefaultItemType of e-mail folders is <code>0</code>, DefaultMessageClass is 
 * <code>"IPM.Note"</code>. This is insofar a problem, as also contacts and deleted items
 * folders have these properties. Hence we sort folders with DefaultItemType 0 later,
 * but not if their name contains "*ontacts" or "Kontakte" (which is a hack).
 * </p>
 * 
 * 
 * 
 * <h1>Outlook crashes</h1>
 * 
 * Endless history of outlook adapter crashes:
 * 
 * 
 * WORKAROUND : what about the threading and objects issues. I create hundreds of ActiveX Objects but never
 *             release() them. This might be dangerous. So i did something with the jacob thread, we will see
 *             what happens...
 * 
 * 17.8.2004: I had many creahsed during a presenta ComThread.doCoInitialize(0); This didn't help much, now i
 * have a EXCEPTION_ACCESS_VIOLATION anbd the VM crrahses.
 * 
 * Later: I tried the old Jacob.dll again. The 94.208 Bytes version. Without InitMta in the beginCall and
 * EndCall - they are now empty. interesting - it even recovers when an comfailexception occurs.
 * 
 * 30.8.2004: crashed from time to time. restarting outlook and gnowsis helps. probably threading problem
 * again.
 * 
 * 2.9.2004: changed to init: ComThread.startMainSTA(); beginCall: ComThread.InitSTA();
 * 
 * doesn't work changed jacob.dll to 106.496byte version again.
 * 
 * 2.9.2004 new approach-Always get Mapi: Now I changed the call in getResourceWrapper to always create the
 * Outlook.Application and get the namespace. Then it works. I will now try making the Application and
 * Namespace session dependent It looks like creating the MAPI more often could be the final solution.
 * 
 * result: worked quite fine, but if outlook is not running before gnowsis, it may crash.
 * 
 * 14.9.2004 again crashes. If it crashed once, I have to restart the computer to get it up again.
 * Interesting: access to the outlook root resource Folders property causes crash. Closing the Outlook-mapi on
 * session.close did not help!
 * 
 * I think I found a major bug today: The outlook root foler wrapper did release the outlookmapi object when
 * it was released. This looks bad. So I removed the outlook mapi from the Root-Wrapper and put null there
 * instead. looks better now.
 * 
 * 10.11.2004 again problems with coCreate. I try to call coInitialize again and do the ComThread.InitSTA();
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2003-2006
 * </p>
 * <p>
 * Organisation: DFKI.de, Aduna.Biz
 * </p>
 * @author Leo Sauermann
 * @version $Id$
 */

public class OutlookCrawler extends CrawlerBase implements DataOpener {

	/**
	 * Constants from outlook
	 * 
	 * 
	 * @author Sauermann
	 */
	public static class OLConst {

		public static final int olAppointmentItem = 1;

		public static final int olFolderCalendar = 9;

		public static final int olFolderContacts = 10;

	}

	/**
	 * how often did calls to oulook crash? if three times, start the ActiveX again.
	 */
	private static int crashed = 0;

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Namespace
	 */
	public final static String NS = "http://www.gnowsis.org/ont/msoutlook/0.1#";
	
	private OutlookAccessor accessor;
	
	private DomainBoundaries boundaries;

	/**
	 * default calendar timezone should be one of the timezones from the website.
	 */
	protected String calendarTimeZone = "http://www.w3.org/2002/12/cal/tzd/Europe/Vienna#tz";

	private boolean hasRedemption = false;

	Dispatch outlookApp;

	Dispatch outlookMapi;

	private HashMap params;

	/**
	 * this contains the start string of a outlook url
	 */
	private String uriPrefix;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.data.adapter.Adapter#beginCall()
	 */
	protected void beginCall() {
		if (source == null)
			throw new RuntimeException("cannot prepare crawler, datasource not set");
		
		// init com
		try {
			ComThread.doCoInitialize(0);
		}
		catch (Throwable t) {
			logger.error("Cannot init Com", t);
		}

		// check if redemption is here
		try {
			ActiveXComponent x = new ActiveXComponent("Redemption.SafeContactItem");
			x.safeRelease();
			hasRedemption = true;
			logger.info("found Oulook-Redemption and will use it to access information.");
		}
		catch (ComFailException x) {
			logger
					.warn("You will be bugged by MS-Outlook messages. To avoid that, download and install redemption from http://www.dimastr.com/redemption/");
			hasRedemption = false;
		}

		outlookApp = new ActiveXComponent("Outlook.Application");
		outlookMapi = Dispatch.call(outlookApp, "GetNamespace", "MAPI").toDispatch();
		
		// accessor
		accessor = new OutlookAccessor();
	}

	/**
	 * check if it would be wise to restart our outlook adaption
	 * 
	 * @param cause
	 */
	public void crashChecker(Throwable cause) {
		crashed++;
		if (crashed > 2) {
			crashed = 0;
			try {
				// initOutlook();
				// recursive call. Its ok, we have a counter.

			}
			catch (Exception x) {
				logger.warn("ComFailed. Tried to init Outlook MAPI ActiveX again. Failed.", x);
			}
		}
		else
			logger.warn("Outlook crashed. This is the " + crashed + " time. Will restart ActiveX on 3rd fail. ", cause);
	}

	/**
	 * structured crawling: crawl sub-containers and sub-items
	 * 
	 */
	private boolean crawlContainer(OutlookResource.Folder folder, OutlookResource parent) {
		logger.info("crawling folder: "+folder.getUri());
		// data of folder
		crawlSingleResource(folder, parent);

		// items inside folder
		boolean result = crawlSubItems(folder);
		if (result == false)
			return false;
		
		// subfolders
		result = crawlSubFolders(folder);
		return result;
	}

	/**
	 * crawling outlook. Each call/thread gets its own Outlook-Mapi object, which is passed around.
	 * 
	 * @return ExitCode
	 */
	protected ExitCode crawlObjects() {
		beginCall();
		try {
			OutlookResource.RootFolder root = new OutlookResource.RootFolder(this);
            try {
    			// init some other params
    			params = new HashMap(2);
    			// crawl the outlook tree
    			boolean crawlCompleted = crawlRoot(root);
    			// clean-up
    			params = null;
    			// determine the exit code
    			return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
            } finally {
                root.release();
            }
		}
		finally {
			endCall();
		}
	}


	/**
	 * crawl the sub-folders of folder
	 */
	private boolean crawlRoot(OutlookResource.RootFolder folder) {
		// crawl Root as such
		crawlSingleResource(folder, null);
		
		Variant v = Dispatch.get(folder.getResource(), "Folders");
		if (v == null)
			return true;

		Dispatch folders = v.toDispatch();
		try {
			int count = Dispatch.get(folders, "Count").toInt();
			if (count == 0)
				return true;

			int i = 1;
			for (; !stopRequested && i <= count; i++) {
				try {
					Dispatch dFolder = Dispatch.invoke(folders, "Item", Dispatch.Get,
						new Object[] { new Integer(i) }, new int[1]).toDispatch();
					OutlookResource.Folder subfolder = OutlookResource.createWrapperForFolder(this, dFolder);
					try {
						if (boundaries.inDomain(subfolder.getUri()))
							crawlContainer(subfolder, folder);
						else
							logger.info("not in domain, stepping over: "+subfolder.getUri());
					}
					finally {
						subfolder.release();
					}
				}
				catch (Exception ex) {
					logger.warn("Error while adding subfolders of " + folder.getUri(), ex);
				}
			}

			// scan has been completed when i has reached the end of the array successfully
			if (i <= count)
				return false;

		}
		finally {
			folders.safeRelease();
		}

		return true;
	}

	/**
	 * crawl an outlook resource, add the data of this resource itself.
	 * This does not go into sub-resources, etc. 
	 * @param parent can be null (for root)
	 */
	private void crawlSingleResource(OutlookResource resource, OutlookResource parent) {
		String uri = resource.getUri();
		logger.info("crawling resource "+uri);
		
		//		 register that we're processing this file
		handler.accessingObject(this, uri);
		deprecatedUrls.remove(uri);
		
		// see if this object has been encountered before (we must do this before applying the accessor!)
		boolean knownObject = accessData == null ? false : accessData.isKnownId(uri);
		
		//		 fetch a RDFContainer from the handler (note: is done for every
		RDFContainerFactory containerFactory = handler.getRDFContainerFactory(this, uri);
		try {
			DataObject dataObject = getAccessor().getDataObjectIfModifiedOutlook(
				uri, source, accessData, params, containerFactory, resource, parent);
			
			if (dataObject == null) {
				// the object was not modified
				handler.objectNotModified(this, uri);
				crawlReport.increaseUnchangedCount();
			}
			else {
				// we scanned a new or changed object
				if (knownObject) {
					handler.objectChanged(this, dataObject);
					crawlReport.increaseChangedCount();
				}
				else {
					handler.objectNew(this, dataObject);
					crawlReport.increaseNewCount();
				}
			}
		}
		catch (UrlNotFoundException e) {
			logger.warn("unable to access " + uri, e);
		}
		catch (IOException e) {
			logger.warn("I/O error while processing " + uri, e);
		}

	}

	/**
	 * crawl the sub-folders of folder
	 */
	private boolean crawlSubFolders(OutlookResource.Folder folder) {
		Variant v = Dispatch.get(folder.getResource(), "Folders");
		if (v == null)
			return true;

		Dispatch folders = v.toDispatch();
        // crawl these folders after all others, they are e-mail folders
        // this was requested by some users who needed to crawl appointments/contacts faster
        // Identifying e-mail folders to be crawled later:
        // see above in javadoc
        ArrayList<OutlookResource.Folder> crawlLater = new ArrayList<OutlookResource.Folder>();
		try {
			int count = Dispatch.get(folders, "Count").toInt();
			if (count == 0)
				return true;

            // crawl subfolders, remember things to crawl later
			int i = 1;
			for (; !stopRequested && i <= count; i++) {
				try {
					Dispatch dFolder = Dispatch.invoke(folders, "Item", Dispatch.Get,
						new Object[] { new Integer(i) }, new int[1]).toDispatch();
					OutlookResource.Folder subfolder = OutlookResource.createWrapperForFolder(this, dFolder);
                    
                    // determine if this is an email folder
                    int dtype = subfolder.getDefaultItemType();
                    boolean isEmail = false;
                    if (dtype == 0)
                    {
                        isEmail = true;
                        String name = subfolder.getName();
                        if (name != null) {
                            name = name.toLowerCase();
                            if ((name.contains("kontakte")||(name.contains("Contacts"))))
                                isEmail = false;
                        }
                    }
                    if (isEmail)
                        // remember mail-folders for later
                        crawlLater.add(subfolder);
                    else
                    {
    					try {
    						crawlContainer(subfolder, folder);
    					}
    					finally {
    						subfolder.release();
    					}
                    }
				}
				catch (Exception ex) {
					logger.info("Error while adding subfolders of " + folder.getUri(), ex);
				}
			}
            
            // now crawl the emails
            for (Iterator<OutlookResource.Folder> folderI = crawlLater.iterator(); 
                !stopRequested && folderI.hasNext(); )
            {
                OutlookResource.Folder f = folderI.next();
                folderI.remove();
                try {
                    crawlContainer(f, folder);
                } catch (Exception ex) {
                    logger.info("Error while adding subfolders of " + folder.getUri(), ex);
                }
                finally {
                    f.release();
                }
            }

			// scan has been completed when i has reached the end of the array successfully
			if (crawlLater.isEmpty() && (i <= count))
				return false;

		}
		finally {
            // superflous items
            if (!crawlLater.isEmpty())
                for (OutlookResource.Folder f : crawlLater) {
                    try {
                        f.release();
                    } catch (Exception x) {
                        logger.info("Error releasing " + f, x);
                    }
                }
			folders.safeRelease();
		}

		return true;
	}

	/**
	 * crawl the sub-folders of folder
	 */
	private boolean crawlSubItems(OutlookResource.Folder folder) {
		// get sub-things
		Dispatch subj = folder.getResource();

		Dispatch items = Dispatch.get(subj, "Items").toDispatch();
		if (items == null)
			return true;
		int count = Dispatch.get(items, "Count").toInt();
		if (count == 0)
			return true;

		try {
			try {
				int i = 1;
				for (; !stopRequested && i <= count; i++) {
					Dispatch dItem = Dispatch.invoke(items, "Item", Dispatch.Get,
						new Object[] { new Integer(i) }, new int[1]).toDispatch();
					if (dItem == null)
						continue;

					OutlookResource item = OutlookResource.createWrapperFor(this, dItem, logger);
                    if (item != null)
                    {
    					try {
    						crawlSingleResource(item, folder);
    					}
    					finally {
    						item.release();
    					}
                    }
				}
				// scan has been completed when i has reached the end of the array successfully
				if (i <= count)
					return false;
			}
			catch (Exception ex) {
				logger.warn("Error while adding subfolders of " + folder.getUri(), ex);
			}
		}
		finally {
			items.safeRelease();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnowsis.data.adapter.Adapter#endCall()
	 */
	protected void endCall() {
		outlookMapi.safeRelease();
		outlookMapi = null;
		outlookApp.safeRelease();
		outlookApp = null;
		accessor = null;
	}
	
	protected OutlookAccessor getAccessor() {
		if (accessor == null)
			throw new RuntimeException("accessor is null, call to beginCall() missing.");
		return accessor;
	}


	/**
	 * outlookMapi, the fabulous "default namespace" of outlook, useful to retrieve many things.
	 */
	protected Dispatch getOutlookMapi() {
		if (outlookMapi == null)
			throw new RuntimeException("outlook MAPI is null, call to beginCall() missing.");
		return outlookMapi;
	}

	public String getUriPrefix() {
		return uriPrefix;
	}

	/**
	 * can this OLAdapter be used in combination with redemption?
	 * 
	 * @return true, if redemption is installed
	 */
	public boolean hasRedemption() {
		return hasRedemption;
	}

	public void open(URI uri) throws IOException {
		logger.info("Outlook: opening uri " + uri);
		beginCall();
        OutlookResource resource = null;
		try {
			resource = OutlookResource.createWrapperFor(this, uri.toString(), logger);
			if (resource == null)
				throw new IOException("outlook: cannot found uri " + uri);
			try {
				Dispatch.call(((OutlookResource) resource).getResource(), "Display");

				// activate it, this may not work on most objects
				try {
					Dispatch.call(((OutlookResource) resource).getResource(), "Activate");
				}
				catch (Exception e) {
				}

			}
			catch (Exception ex) {
				throw new IOException("outlook: unable to display uri: " + uri + " reason: " + ex);
			}
		}
		finally {
            if (resource != null) {
                resource.release();
            }
			endCall();
		}
	}

	/**
	 * threading problems: for the actual thread there has to be a
	 * 
	 */
	// public void initOutlook() throws InitialisationException {
	//
	// try {
	// //ComThread.InitMTA();
	// outlookApp = new ActiveXComponent("Outlook.Application");
	// outlookMapi = Dispatch.call(outlookApp, "GetNamespace",
	// "MAPI").toDispatch();
	// } catch (Exception e) {
	// outlookApp = null;
	// outlookMapi = null;
	// throw new InitialisationException("Error creating Outlook ActiveX objects
	// :"+e.toString());
	// }
	// }
	/**
	 * the release of the Outlook Adapter ATTENTION: If the Outlook Adapter is not released properly, It will
	 * halt the whole application as the MainSTA has to be released. This is serious, as the MainSTA is
	 * running as Deamon thread.
	 */
	public void release() {
		// GarbageCollect the Adapters.
		System.gc();
		/*
		 * try { if (outlookApp != null) outlookApp.release(); if (outlookMapi != null) outlookMapi.release(); }
		 * catch (Exception x) { log.warn("Error releasing ms-outlook resources: "+x.toString()); }
		 */
		// outlookApp.release();
		// outlookMapi.release();
		// ComThread.Release();
		// ComThread.quitMainSTA();
		ComThread.quitMainSTA();
	}
	
	public void setDataSource(DataSource source) {
		super.setDataSource(source);

		// read uriprefix
		uriPrefix = ((OutlookDataSource)source).getRootUrl();
		if (uriPrefix == null) {
			uriPrefix = "outlook://";
			logger.warn("Outlook adapter missing the rootUrl property. Using " + uriPrefix + " instead.");
		} else
			logger.info("crawling outlook, uri prefix: "+uriPrefix);
		
		// domain boundaries
		boundaries = ConfigurationUtil.getDomainBoundaries(source.getConfiguration());
	}
}
