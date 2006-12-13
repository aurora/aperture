/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.impl.sesame2.ModelImplSesame;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.ModelUtil;
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
	public List<DataObject> crawlAddressbook() throws Exception {
		
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
		
		Model model = createSimpleModel();
		model.readFrom(new StringReader(rdfxml), Syntax.RdfXml);
		
		List<DataObject> res=new Vector<DataObject>();
		
		ClosableIterator<? extends Statement> i = null;
		try {
			ClosableIterable<? extends Statement> iterable = model.findStatements(Variable.ANY,RDF.type,VCARD.VCard);
			i = iterable.iterator();
			while (i.hasNext()) {
				Statement s = i.next();
				URI uri = URIImpl.createURIWithoutChecking(s.getSubject().toString());
			
				// get relevant triples
				RDFContainer dorep = handler.getRDFContainerFactory(this,uri.toString()).getRDFContainer(uri);
				//pretty
				List<Statement> statementList = ModelUtil.getCBD(s.getSubject(),model,true); 
				dorep.getModel().addAll(statementList.iterator());
				res.add(new DataObjectBase(uri,source,dorep));
			}
		} catch (ModelException me) {
			LOGGER.log(Level.SEVERE,"Exception while crawling the mac addressbook",me);
		} finally {
			if (i!=null) {
				i.close();
			}
		}
		
		return res;
	}

	private String getScript() throws IOException {
		return FileUtil.readStreamAsUTF8(getClass().getResourceAsStream("addressbook.applescript"));
	}
	
	private Model createSimpleModel() {
		try {
			return new ModelImplSesame(false);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Couldn't create a simple model",e);
			return null;
		}
		
	}

}

