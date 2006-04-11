/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA_GEN;
import org.semanticdesktop.aperture.vocabulary.VCARD;
import org.semanticdesktop.demork.Demork;
import org.semanticdesktop.demork.Utils;
import org.semanticdesktop.demork.database.Cell;
import org.semanticdesktop.demork.database.Database;
import org.semanticdesktop.demork.database.Row;
import org.semanticdesktop.demork.database.Table;


/**
 * This is a crawler for the thunderbird address book. 
 * 
 * @author grimnes
 * $Id$
 */
public class ThunderbirdCrawler extends AddressbookCrawler {

	private final static String THUNDERBIRD_URI_BASE="urn:thunderbird:";
	
	static private List skipProperties=new Vector(); 
	static {
		skipProperties.add("PreferMailFormat");
		skipProperties.add("PopularityIndex");
		skipProperties.add("RecordKey");
		skipProperties.add("LastModifiedData");
	}
	
	
	public ThunderbirdCrawler(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}


	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.addressbook.AddressbookCrawler#crawlAddressbook()
	 */
	public List crawlAddressbook() throws Exception {
	
		String abookFile=ConfigurationUtil.getBasepath(getDataSource().getConfiguration());

		Demork demork=new Demork();
		
		String encoding=demork.getEncoding(abookFile);
		
		System.out.println(encoding);
		
		String mab = Utils.readWholeFileAsEncoding(abookFile,encoding);
		
		Database d = demork.inputMork(mab);
		
		List res=new Vector();
		
		for (Iterator i=d.tables.keySet().iterator(); i.hasNext();) {
			String key=(String)i.next();
			Table t=(Table) d.tables.get(key);
			for (Iterator j=t.rows.keySet().iterator();j.hasNext();) {
				String rowkey=(String) j.next();
				DataObject o = reportContact((Row)t.rows.get(rowkey));
				if (o!=null)
					res.add(o);
			}
		}
		return res;
	}
	

	private DataObject reportContact(Row row) {
		Hashtable values=new Hashtable();
		for (Iterator i=row.cells.iterator();i.hasNext();) {
			Cell c=(Cell) i.next();
			if (!skipProperties.contains(c.column) && !c.atom.equals("")) {
				values.put(c.column,c.atom);
			}
		}
		if (values.size()==0) return null;
		
		String uris=createURI(row.id);
		URI uri=new URIImpl(uris);
		
		RDFContainerFactory rdff = handler.getRDFContainerFactory(null,uris);
		RDFContainer rdf = rdff.getRDFContainer(uri);
		
		rdf.add(RDF.TYPE,VCARD.VCard);
		
		for (Iterator i=values.keySet().iterator();i.hasNext();) {
			String key=(String) i.next();
			String value=(String) values.get(key);
			
			if (key.equals("PrimaryEmail")) {
				//TODO: Which email?
				rdf.add(DATA_GEN.emailAddress,new LiteralImpl(value));
				rdf.add(VCARD.email,new LiteralImpl(value));
			} else if (key.equals("SecondEmail")) {
				//TODO: Which email?
				rdf.add(DATA_GEN.emailAddress,new LiteralImpl(value));
				rdf.add(VCARD.email,new LiteralImpl(value));
			} else if (key.equals("FirstName")) {
				rdf.add(VCARD.nameGiven,new LiteralImpl(value));
			} else if (key.equals("LastName")) {
				rdf.add(VCARD.nameFamily,new LiteralImpl(value));
			} else if (key.equals("DisplayName")) {
				// TODO: what if things don't have a fullname?
				rdf.add(RDFS.LABEL,new LiteralImpl(value));
				rdf.add(VCARD.fullname,new LiteralImpl(value));
			} else if (key.equals("NickName")) {
				rdf.add(VCARD.nameAdditional,new LiteralImpl(value));
			} else if (key.equals("WorkPhone")) {
				rdf.add(VCARD.telWork,new LiteralImpl(value));
			} else if (key.equals("HomePhone")) {
				rdf.add(VCARD.telHome,new LiteralImpl(value));
			} else if (key.equals("FaxNumber")) {
				rdf.add(VCARD.telFax,new LiteralImpl(value));
			} else if (key.equals("PagerNumber")) {
				rdf.add(VCARD.telPager,new LiteralImpl(value));
			} else if (key.equals("CellularNumber")) {
				rdf.add(VCARD.telCell,new LiteralImpl(value));
			} else if (key.equals("JobTitle")) {
				rdf.add(VCARD.title,new LiteralImpl(value));
			} else if (key.equals("Department")) {
				rdf.add(VCARD.org,new LiteralImpl(value));
			} else if (key.equals("Company")) {
				rdf.add(VCARD.org,new LiteralImpl(value));
			} else if (key.equals("_AimScreenName")) {
				rdf.add(VCARD.nameAdditional,new LiteralImpl(value));
			} else if (key.equals("WebPage1")) {
				rdf.add(DATA_GEN.homepage,new LiteralImpl(value));
			} else if (key.equals("WebPage2")) {
				rdf.add(DATA_GEN.homepage,new LiteralImpl(value));
			} else if (key.equals("Custom1")) {
				rdf.add(VCARD.note,new LiteralImpl(value));
			} else if (key.equals("Custom2")) {
				rdf.add(VCARD.note,new LiteralImpl(value));
			} else if (key.equals("Custom3")) {
				rdf.add(VCARD.note,new LiteralImpl(value));
			} else if (key.equals("Custom4")) {
				rdf.add(VCARD.note,new LiteralImpl(value));
			} else if (key.equals("Notes")) {
				rdf.add(VCARD.note,new LiteralImpl(value));
			} 
		}

		addAddress(values,rdf,"Home",VCARD.addressHome);
		addAddress(values,rdf,"Work",VCARD.addressWork);
		
		DataObjectBase object = new DataObjectBase(uri,null,rdf);
		
		return object;
	}

	private void addAddress(Hashtable values, RDFContainer rdf, String type, URI address) {
		/*
		} else if (key.equals("HomeAddress")) {
		} else if (key.equals("HomeAddress2")) {
		} else if (key.equals("HomeCity")) {
		HomeState, HomeZipCode, HomeCountry
		}*/
		URI a=new URIImpl(rdf.getDescribedUri().toString()+"_"+type+"Address");
		boolean ok=false;
		if (values.containsKey(type+"Address")) {
			ok=true;
			if (values.containsKey(type+"Address2")) {
				rdf.add(new StatementImpl(a,VCARD.streetAddress,new LiteralImpl(((String)values.get(type+"Address"))+values.get(type+"Address2"))));
			} else { 
				rdf.add(new StatementImpl(a,VCARD.streetAddress,new LiteralImpl((String) values.get(type+"Address"))));
			}
		}
		if (values.containsKey(type+"City")) {
			ok=true;
			rdf.add(new StatementImpl(a,VCARD.locality,new LiteralImpl(((String)values.get(type+"City")))));
		}
		if (values.containsKey(type+"Country")) {
			ok=true;
			rdf.add(new StatementImpl(a,VCARD.country,new LiteralImpl(((String)values.get(type+"Country")))));
		} 
		if (values.containsKey(type+"State")) {
			ok=true;
			rdf.add(new StatementImpl(a,VCARD.region,new LiteralImpl(((String)values.get(type+"City")))));
		}
		if (values.containsKey(type+"ZipCode")) {
			ok=true;
			rdf.add(new StatementImpl(a,VCARD.postalcode,new LiteralImpl(((String)values.get(type+"ZipCode")))));
		}
		if (ok) {
			rdf.add(address,a);
		}
	}

	private String createURI(String id) {
		return THUNDERBIRD_URI_BASE+"Person:"+id;
	}

	// Private worker as we are trying to force UTF-8. 
    private static String readWholeFile(InputStream is) throws IOException
    {
    		Reader r=new InputStreamReader(is,Charset.forName("utf-8").newDecoder());
        StringWriter sw = new StringWriter(1024);
        char buff[] = new char[1024];
        while (r.ready()) {
            int l = r.read(buff);
            if (l <= 0)
                break;
            sw.write(buff, 0, l);
        }
        r.close();
        sw.close();
        return sw.toString();  
    }
}

