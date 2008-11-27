/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.semanticdesktop.aperture.detector.DataSourceDescription;
import org.semanticdesktop.aperture.detector.DataSourceDetector;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.OSUtils;
import org.semanticdesktop.aperture.util.RegistryReader;
import org.semanticdesktop.aperture.util.UriUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Detects if you have Thunderbird installed and searches for the 
 * address book file.
 * @author grimnes
 * @author sauermann
 */
public class ThunderbirdAddressbookDetector implements DataSourceDetector {
	
    private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String URI="urn:nepomuk:software:thunderbird";
	
	/* (non-Javadoc)
	 * @see org.gnowsis.util.Detector#getURI()
	 */
	public String getURI() {
		return URI;
	}
	
	/**
	 * Parse an thunderbird ini file
	 * 
	 * TODO: do this without sucking
	 */
	private Hashtable<String,Object> readIni(File file) {
		Hashtable<String,Object> res=new Hashtable<String,Object>();
		String section="none";
		List<String> sections=new Vector<String>();
		
		BufferedReader in=null;
		try {
			in=new BufferedReader(new FileReader(file));
			
			String line=in.readLine();
			while(line!=null) {
				if (line.length()>0 && line.charAt(0)!=';') {
					if (line.charAt(0)=='[') {
						section=line.substring(1,line.length()-1);
						sections.add(section);
					} else { 
						int i=line.indexOf('=');
						if (i==-1) 
							throw new ParseException("Could not parse ini file. Expected section or variable, got: "+line,0);
						
						res.put(section+"_"+line.substring(0,i),line.substring(i+1,line.length()));
					} 
				}
				line=in.readLine();
			}
			
			res.put("sections",sections);
			
		} catch (Exception e) {
			e.printStackTrace();
			return res;
		}
		
		return res;
	}
	
	private List<String> getLinuxThunderbirdDirs() { 
		String home=System.getProperty("user.home");
		List<String> res=new Vector<String>();
		res.add(home+File.separator+".mozilla-thunderbird");
		res.add(home+File.separator+".mozilla/thunderbird");
		res.add(home+File.separator+".thunderbird");
		return res;
	}
	
	/**
	 * @return list of typical paths were thunderbird stores its data
	 */
	private List<String> getWinThunderbirdDirs() {
		String home=System.getProperty("user.home");
		List<String> res=new Vector<String>();
		
		// read the folders from the registry
		String path = RegistryReader.getCurrentUserAppDataFolderPath(); 
		if (path != null) res.add(path+"\\Thunderbird");
		path = RegistryReader.getCurrentUserLocalAppDataFolderPath();
        if (path != null) res.add(path+"\\Thunderbird");

        // well, these are know to work sometimes as backup
		res.add(home+File.separator+"Application Data\\Thunderbird");
		res.add(home+File.separator+"Local Settings\\Application Data\\Thunderbird");
		res.add(home+File.separator+"Anwendungsdaten\\Thunderbird");
		res.add(home+File.separator+"Lokale Einstellungen\\Anwendungsdaten\\Thunderbird");
		// If in rare cases the registry fails, we may also evaluate these candidates: 
		//    http://www.winehq.com/pipermail/wine-cvs/2006-January/020079.html
		return res;
	}

	/**
	 * @return
	 */
	private List<String> getMacThunderbirdDirs() {
		String home=System.getProperty("user.home");
		List<String> res=new Vector<String>();
		res.add(home+File.separator+"Library/Thunderbird");
		return res;
	}
	
	/**
	 * Searches inside Returns the path 
	 * to the users profile directory
	 */
	public List<DataSourceDescription> detect() throws Exception {
		log.debug("Detecting thunderbird.");
		
		List<String> paths;
		if (OSUtils.isLinux()) {
			paths=getLinuxThunderbirdDirs();
		} else if (OSUtils.isMac()) {
			paths=getMacThunderbirdDirs();
		} else if (OSUtils.isWindows()) {
			paths=getWinThunderbirdDirs();
		}
		else {
			throw new Exception("Your operating system '"
			    +System.getProperty("os.name")+"' aint supported yet.");
		}
		
		File thunderbird=null;
		for(String p:paths) {		
			thunderbird=new File(p);
			if (thunderbird.exists()) break; 
		}
			
		if (!thunderbird.exists()) {
			log.debug("No thunderbird directory found.");
			return Collections.emptyList();
		}
		
		Hashtable<String, Object> ini = readIni(new File(thunderbird,"profiles.ini"));
		
		String defaultProfile=null;
		int i=0;
		while (((List)ini.get("sections")).contains("Profile"+i)) {
			if (ini.containsKey("Profile"+i+"_Default")) {
				defaultProfile=(String) ini.get("Profile"+i+"_Path");
				break;
			}
			i++;
		}
		if (defaultProfile==null) {
			if (i==1) {
				defaultProfile=(String) ini.get("Profile0_Path");
			} else {
				log.debug("Found thunderbird directory - but could not find default folder.");
				return Collections.emptyList();
			}
		}
		
		ThunderbirdAddressbookDataSource ds = new ThunderbirdAddressbookDataSource();
		Model m=RDF2Go.getModelFactory().createModel();
		m.open();
		ds.setConfiguration(new RDFContainerImpl(m, UriUtil.generateRandomURI(m)));
		ds.setName("Thunderbird Addressbook");
		ds.setComment("Contacts from your Thunderbird Addressbook.");
		ds.setThunderbirdAddressbookPath(thunderbird.getAbsolutePath()+File.separator+defaultProfile+File.separator+"abook.mab");
		ArrayList<DataSourceDescription> result = new ArrayList<DataSourceDescription>(1);
		result.add(new DataSourceDescription(ds));
		return result;
	}

    public org.ontoware.rdf2go.model.node.URI getSupportedType() {
        return THUNDERBIRDADDRESSBOOKDS.ThunderbirdAddressbookDataSource;
    }

	

}
