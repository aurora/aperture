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
/**
 * 
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
