/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;


/**
 * This is a superclass of all addressbook crawlers. 
 * Modified dates are not used, but a checksum is computed to determine if entries are changed.
 * Return addressbook enties are expected to use VCard vocabulary.
 * 
 * @author grimnes
 * $Id$
 */
public abstract class AddressbookCrawler extends CrawlerBase {

	protected static final Logger LOGGER = Logger.getLogger(AddressbookCrawler.class.getName());
	protected static final String ADDRESSBOOK_CHECKSUM_KEY="ADDRESSBOOK_CHECKSUM";
		
	protected ExitCode crawlObjects() {
		DataSource source = getDataSource();
		RDFContainer configuration = source.getConfiguration();
		//String username = ConfigurationUtil.getUsername(configuration);
		//String password = ConfigurationUtil.getPassword(configuration);

		boolean crawlCompleted = false;

		
		try {
		
			List people = crawlAddressbook();
			
			Set current=new HashSet();
			for (Iterator it=people.iterator(); it.hasNext();) {
				DataObject o=(DataObject) it.next();
				String sum=computeChecksum(o);
				
				if (accessData.isKnownId(o.getID().toString())) {
					if (accessData.get(o.getID().toString(),ADDRESSBOOK_CHECKSUM_KEY).equals(sum)) {
						handler.objectNotModified(this,o.getID().toString());
					} else { 
						handler.objectChanged(this,o);
					}
				} else {
					accessData.put(o.getID().toString(),ADDRESSBOOK_CHECKSUM_KEY,sum);
					handler.objectNew(this,o);
				}
				current.add(o);
			}			
			
			//Blah - crawl objects, friends etc. 			
			
			//remove found tags from list of tags to be deleted
			deprecatedUrls.removeAll(current);
			
			crawlCompleted=true;
			
		} catch (Exception e) {
			LOGGER.log(Level.INFO,"Could not crawl addressbook-datasource.",e);
			return ExitCode.FATAL_ERROR;
		} 

		// determine the exit code
		return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
	}

	/**
	 * Compute a MD5 checksum of the values of this addressbook entry. 
	 * This is based on immediate RDF properties, and might cause two problems: 
	 * 1. BlankNodes will always have different IDs and will mess up the id.
	 * 2. if this has sub elements (i.e. addresses), this might return the same although the address might have changed. 
	 * 
	 * TODO: FIX THIS! (then again, traversing whole tree might be slow) 
	 * 
	 * @param o - the dataobject to checksum
	 * @return a md5 hex-digest as a string.
	 */
	private String computeChecksum(DataObject o) {

		MessageDigest md;
		try {
			md=MessageDigest.getInstance("md5");
		}
		catch (NoSuchAlgorithmException e) {
			LOGGER.warning("MD5 not available - using no checksum.");
			
			return "nochecksum";
		}
		//hack hack
		RDFContainer rdf=o.getMetadata();
		Repository rep=(Repository)rdf.getModel();
		
		// List all properties
		List predValues=new Vector();
		CloseableIterator i = rep.getStatements(rdf.getDescribedUri(),null,null);
		while(i.hasNext()) {
			Statement s=(Statement) i.next();
			if (s.getObject() instanceof BNode) {
				LOGGER.warning("BlankNodes messes up checksum generation!");
			}
			predValues.add(s.getPredicate().toString()+s.getObject().toString());
		}
		i.close();
		
		// sort them...
		Collections.sort(predValues);
		
		//DAMN java 1.4
		for (Iterator it=predValues.iterator();it.hasNext();)
			md.update(((String)it.next()).getBytes());	
		
		StringBuilder digest=new StringBuilder("");
		byte[] dig=md.digest();
		for (int j=0;j<dig.length;j++) {
			digest.append(Integer.toHexString(dig[j]));
	
		}
		
		return digest.toString();
	}
	public abstract List crawlAddressbook() throws Exception; 

}

