/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum f?r K?nstliche Intelligenz DFKI GmbH.
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
	
	private void macopen(URI url) throws IOException {
		try {
			Class macopener = Class.forName("com.apple.eio.FileManager");
			Method m = macopener.getMethod("openURL",new Class[] {String.class});
			m.invoke(null,new Object[] {url.toString()});
		} catch (Exception e) {
			throw new IOException("Could not open file: "+url+" - "+e);
		}
	}

	private File URI2File(URI uri) throws IOException {
		File f;
		try {
			f = new File(new java.net.URI(uri.toString()));
		}
		catch (URISyntaxException e) {
			throw new IOException("Could not parse URI: "+uri.toString()+" - "+e);
		}
		return f;
	}
	
	private void windowsopen(URI uri) throws IOException {
		File f=URI2File(uri);
		
		if (f.isDirectory()) {
			//TODO: 
			Runtime.getRuntime().exec( new String [] { "cmd","/c","explorer", f.toString() });
		} else {
			
			Runtime.getRuntime().exec(new String [] { "cmd","/c",f.toString() });
		}
		
	}

	private void linuxopen(URI uri) throws IOException {
		File f=URI2File(uri);
		
		// TODO: I don't know how reliable this is. It's set correctly for kde/gnome on my machine :)
		if (System.getenv("DESKTOP_SESSION").toLowerCase().contains("kde")) {			
			//kde:		
			Runtime.getRuntime().exec(new String [] { "kfmclient","exec",f.toString()} );
		} else {
			//Default to gnome as it complains less if it's not running.
			//gnome: 
			Runtime.getRuntime().exec(new String [] { "gnome-open",uri.toString()} );
		}
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
			f.open(new URIImpl("file:///tmp"));
			f.open(new URIImpl("file:///etc/bash.bashrc"));
		} else {
			throw new Error("What weirdass OS are you running?");
		}
	}

}

