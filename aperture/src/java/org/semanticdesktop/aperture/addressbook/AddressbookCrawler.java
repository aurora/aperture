/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.crawler.impl.DefaultCrawlerRegistry;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public abstract class AddressbookCrawler extends CrawlerBase {

	private static final Logger LOGGER = Logger.getLogger(AddressbookCrawler.class.getName());
		
	protected ExitCode crawlObjects() {
		DataSource source = getDataSource();
		RDFContainer configuration = source.getConfiguration();
		String username = ConfigurationUtil.getUsername(configuration);
		String password = ConfigurationUtil.getPassword(configuration);

		boolean crawlCompleted = false;

		
		try {
		
			List people = crawlAddressbook();
			
			Set before=accessData.getStoredIDs();
			Set current=new HashSet();
			for (Iterator it=people.iterator(); it.hasNext();) {
				DataObject o=(DataObject) it.next();
				
				if (accessData.isKnownId(o.getID().toString())) { 
					handler.objectNotModified(this,o.getID().toString());
				} else {
					
					handler.objectNew(this,o);
				}
				current.add(o);
			}			
			
			//Blah - crawl objects, friends etc. 			
			
			//report deleted tags
			before.removeAll(current);
			for (Iterator i=before.iterator();i.hasNext();) {
				DataObject o=(DataObject) i.next();
				handler.objectRemoved(this,o.getID().toString());
			}

		} catch (Exception e) {
			LOGGER.log(Level.INFO,"Could not crawl tag-datasource.",e);
			return ExitCode.FATAL_ERROR;
		} 

		// determine the exit code
		return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
	}

	public abstract List crawlAddressbook() throws Exception; 

}

