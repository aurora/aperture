/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.util.PlatformUtil;


public class FileOpener implements DataOpener {

	
	
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
	
	private boolean macopen(URI url) {
		try {
			Class macopener = Class.forName("com.apple.eio.FileManager");
			Method m = macopener.getMethod("openURL",new Class[] {String.class});
			m.invoke(null,new Object[] {url.toString()});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void windowsopen(URI uri) throws IOException {
		File f;
		try {
			f = new File(new java.net.URI(uri.toString()));
		}
		catch (URISyntaxException e) {
			throw new IOException("Could not parse URI: "+uri.toString()+" - "+e);
		}
		System.err.println(f.toString());
		if (f.isDirectory()) {
			//TODO: 
			Runtime.getRuntime().exec( new String [] { "cmd","/c","explorer", f.toString() });
		} else {
			
			Runtime.getRuntime().exec(new String [] { "cmd","/c",f.toString() });
		}
		
	}

	private void linuxopen(URI uri) {
		// TODO Auto-generated method stub
		throw new Error("FileOpener:linuxopen not yet implemented.");
	}
	
	public static void main(String args[]) throws IOException {
		FileOpener f=new FileOpener();
		if (PlatformUtil.isMac()) {
			f.open(new URIImpl("file:///Users/"));
			f.open(new URIImpl("file:///Users/"));
		} else if (PlatformUtil.isWindows()) {
			f.open(new URIImpl("file:/c:"));
			f.open(new URIImpl("file:/c:/windows/win.ini"));
		} else if (PlatformUtil.isLinux()) {
			throw new Error("Not yet.");
		} else {
			throw new Error("What weirdass OS are you running?");
		}
	}

}

