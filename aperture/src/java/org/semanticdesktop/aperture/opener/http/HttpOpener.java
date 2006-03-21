/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum f?r K?nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.http;

import java.io.IOException;
import java.lang.reflect.Method;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.util.PlatformUtil;


public class HttpOpener implements DataOpener {

	public void open(URI uri) throws IOException {
		if (PlatformUtil.isMac()) {
			macopen(uri);
		} else if (PlatformUtil.isLinux()) {
			linuxopen(uri);
		} else if (PlatformUtil.isWindows()) {
			windowsopen(uri);
		} else { 
			//Hmm, so what OS is this then? 
			throw new IOException("Unsupported OS:"+System.getProperty("os.name"));
		}
	}
	
	private void windowsopen(URI uri) throws IOException {
		Runtime.getRuntime().exec( new String [] { "rundll32", "url.dll,FileProtocolHandler",uri.toString() });
	}
	
	private void linuxopen(URI uri) throws IOException {
		// TODO: I don't know how reliable this is. It's set correctly for kde/gnome on my machine :)
		if (System.getenv("DESKTOP_SESSION").toLowerCase().contains("kde")) {			
			//kde:		
			Runtime.getRuntime().exec(new String [] { "kfmclient","exec",uri.toString()} );
		} else {
			//Default to gnome as it complains less if it's not running.
			Runtime.getRuntime().exec(new String [] { "gnome-open",uri.toString()} );
		}		
	}

	private void macopen(URI url) throws IOException {
		try {
			Class macopener = Class.forName("com.apple.eio.FileManager");
			Method m = macopener.getMethod("openURL",new Class[] {String.class});
			m.invoke(null,new Object[] {url.toString()});
		} catch (Exception e) {
			throw new IOException("Could not open URI: "+url+" - "+e);
		}
	}
	
	public static void main(String args[]) throws IOException {
		HttpOpener ho=new HttpOpener();
		ho.open(new URIImpl("http://www.google.com"));
	}
	
}

