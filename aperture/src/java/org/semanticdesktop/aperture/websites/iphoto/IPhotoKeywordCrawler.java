/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.websites.iphoto;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.websites.AbstractTagCrawler;

/**
 * 
 * @author grimnes
 */
public class IPhotoKeywordCrawler extends AbstractTagCrawler {

	private final static String BASEURI="urn:iphoto:keywords:";
	
	public IPhotoKeywordCrawler() {
	    super();
	}
	
	/**
	 * @param dataSource
	 */
	public IPhotoKeywordCrawler(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.websites.TagCrawler#crawlTags(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
    @Override
	protected List<String> crawlTags(String username, String password) throws Exception {
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
		
		
		String keywords;
		try {
			keywords=(String)m.invoke(appleutils,new Object[] { script } );
		}
		catch (Exception e) {
			throw new Exception("Could not execute applescript!",e);
		}
		
		List<String> r = new Vector<String>();
		for (String s: Arrays.asList(keywords.split("\r"))) {
			r.add(BASEURI+URLEncoder.encode(s,"utf-8"));
		}
		return r;
	}

	private String getScript() throws IOException {
		return FileUtil.readStreamAsUTF8(getClass().getResourceAsStream("iphotokeywords.applescript"));
	}
}
