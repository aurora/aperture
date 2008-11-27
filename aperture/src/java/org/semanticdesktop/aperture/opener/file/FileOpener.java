/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.util.OSUtils;


public class FileOpener implements DataOpener {

	
	
	public void open(URI uri) throws IOException {
		if (OSUtils.isMac()) {
			macopen(uri);
		} else if (OSUtils.isLinux()) {
			linuxopen(uri);
		} else if (OSUtils.isWindows()) {
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
		if (OSUtils.isMac()) {
			f.open(URIImpl.createURIWithoutChecking("file:///Users/"));
			f.open(URIImpl.createURIWithoutChecking("file:///Users/"));
		} else if (OSUtils.isWindows()) {
			f.open(URIImpl.createURIWithoutChecking("file:/c:"));
			f.open(URIImpl.createURIWithoutChecking("file:/c:/windows/win.ini"));
		} else if (OSUtils.isLinux()) {
			f.open(URIImpl.createURIWithoutChecking("file:///tmp"));
			f.open(URIImpl.createURIWithoutChecking("file:///etc/bash.bashrc"));
		} else {
			throw new Error("What weirdass OS are you running?");
		}
	}

}

