/**
 Gnowsis License 1.0

 Copyright (c) 2004, Leo Sauermann & DFKI German Research Center for Artificial Intelligence GmbH
 All rights reserved.

 This license is compatible with the BSD license http://www.opensource.org/licenses/bsd-license.php

 Redistribution and use in source and binary forms, 
 with or without modification, are permitted provided 
 that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the DFKI nor the names of its contributors 
 may be used to endorse or promote products derived from this software 
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 endOfLic**/
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundaries;
import org.semanticdesktop.aperture.opener.DataOpener;

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
 * <h1>Outlook crashes</h1>
 * 
 * Endless history of outlook adapter crashes:
 * 
 * 
 * @WORKAROUND : what about the threading and objects issues. I create hundreds of ActiveX Objects but never
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

	protected static Logger log = Logger.getLogger(OutlookCrawler.class.getName());

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
	private void beginCall() {
		if (source == null)
			throw new RuntimeException("cannot prepare crawler, datasource not set");
		
		// init com
		try {
			ComThread.doCoInitialize(0);
		}
		catch (Throwable t) {
			t.printStackTrace();
			log.severe("Cannot init Com: " + t);
		}

		// check if redemption is here
		try {
			ActiveXComponent x = new ActiveXComponent("Redemption.SafeContactItem");
			x.safeRelease();
			hasRedemption = true;
			log.config("found Oulook-Redemption and will use it to access information.");
		}
		catch (ComFailException x) {
			log.config("Redemption not available, you will be bugged by outlook messages.");
			log
					.warning("You will be bugged by MS-Outlook messages. To avoid that, download and install redemption from http://www.dimastr.com/redemption/");
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
				log.info("ComFailed. Tried to init Outlook MAPI ActiveX again. Failed. Reason: "
						+ x.toString());
			}
		}
		else
			log.info("Outlook crashed. This is the " + crashed + " time. Will restart ActiveX on 3rd fail. "
					+ cause.toString());

	}

	/**
	 * structured crawling: crawl sub-containers and sub-items
	 * 
	 */
	private boolean crawlContainer(OutlookResource.Folder folder, OutlookResource parent) {
		log.finer("crawling folder: "+folder.getUri());
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
	 * @return
	 */
	protected ExitCode crawlObjects() {
		beginCall();
		try {
			OutlookResource.RootFolder root = new OutlookResource.RootFolder(this);
			// init some other params
			params = new HashMap(2);
			// crawl the outlook tree
			boolean crawlCompleted = crawlRoot(root);
			// clean-up
			params = null;
			// determine the exit code
			return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
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
							log.finest("not in domain, stepping over: "+subfolder.getUri());
					}
					finally {
						subfolder.release();
					}
				}
				catch (Exception ex) {
					OutlookCrawler.log.log(Level.INFO, "Error while adding subfolders of " + folder.getUri()
							+ " : " + ex.getMessage(), ex);
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
		log.finest("crawling resource "+uri);
		
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
			log.log(Level.WARNING, "unable to access " + uri, e);
		}
		catch (IOException e) {
			log.log(Level.WARNING, "I/O error while processing " + uri, e);
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
						crawlContainer(subfolder, folder);
					}
					finally {
						subfolder.release();
					}
				}
				catch (Exception ex) {
					OutlookCrawler.log.log(Level.INFO, "Error while adding subfolders of " + folder.getUri()
							+ " : " + ex.getMessage(), ex);
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

					OutlookResource item = OutlookResource.createWrapperFor(this, dItem);
					try {
						crawlSingleResource(item, folder);
					}
					finally {
						item.release();
					}
				}
				// scan has been completed when i has reached the end of the array successfully
				if (i <= count)
					return false;
			}
			catch (Exception ex) {
				OutlookCrawler.log.info("Error while adding subfolders of " + folder.getUri() + " : "
						+ ex.getMessage());
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
	private void endCall() {
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
			throw new RuntimeException("accessor is null, call to beginCall() missing.");
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
		log.info("Outlook: opening uri " + uri);
		OutlookResource resource = OutlookResource.createWrapperFor(this, uri.toString());
		if (resource == null)
			throw new IOException("outlook: cannot found uri " + uri);

		beginCall();
		try {
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
		uriPrefix = ConfigurationUtil.getRootUrl(source.getConfiguration());
		if (uriPrefix == null) {
			uriPrefix = "gnowsis://localhost/outlook/";
			log.warning("Outlook adapter missing the rootUrl property. Using " + uriPrefix + " instead.");
		} else
			log.finer("crawling outlook, uri prefix: "+uriPrefix);
		
		// domain boundaries
		boundaries = ConfigurationUtil.getDomainBoundaries(source.getConfiguration());
	}
}
