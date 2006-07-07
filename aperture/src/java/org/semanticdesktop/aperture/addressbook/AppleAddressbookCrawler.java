/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.RepositoryUtil;
import org.semanticdesktop.aperture.vocabulary.VCARD;



/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class AppleAddressbookCrawler extends AddressbookCrawler {
	
	public static final String TYPE = "macosxaddressbook";

	public AppleAddressbookCrawler(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	
	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.addressbook.AddressbookCrawler#crawlAddressbook()
	 */
	public List crawlAddressbook() throws Exception {
		
		Object appleutils;
		Method m;
		try {
			Class apc = getClass().getClassLoader().loadClass("org.gnowsis.util.AppleUtils");
			appleutils = apc.newInstance();
			//System.err.println(apc.getClassLoader());
			m = apc.getMethod("applescript",new Class[] { String.class });
		}
		catch (Exception e1) {
			throw new Exception("Could not load AppleUtils library.",e1);
		}
		
		String script;
		try {
			script = getScript();
		}
		catch (IOException e) {
			throw new Exception("Could not read applescript resource",e);
		}
		
		
		String rdfxml;
		try {
			rdfxml=(String)m.invoke(appleutils,new Object[] { script } );
		}
		catch (Exception e) {
			throw new Exception("Could not execute applescript!",e);
		}
		
		//System.err.println(rdfxml);
		
		Repository rep=RepositoryUtil.createSimpleRepository();
		rep.add(new StringReader(rdfxml),"urn:mac:addressbook",RDFFormat.RDFXML);
		
		List res=new Vector();
		
		CloseableIterator i;
		for (i=rep.getStatements(null,RDF.TYPE,VCARD.VCard); i.hasNext(); ) {
			RStatement s=(RStatement) i.next();
			URI uri=new URIImpl(s.getSubject().toString());
			
			// get relevant triples
			RDFContainer dorep = handler.getRDFContainerFactory(this,uri.toString()).getRDFContainer(uri);
			//pretty
			((Repository)dorep.getModel()).add(RepositoryUtil.getCBD(s.getSubject(),rep,true));
			res.add(new DataObjectBase(uri,source,dorep));
		}
		i.close();
		
		return res;
	}

	private String getScript() throws IOException {
		return FileUtil.readStreamAsUTF8(getClass().getResourceAsStream("addressbook.applescript"));
	}

}

