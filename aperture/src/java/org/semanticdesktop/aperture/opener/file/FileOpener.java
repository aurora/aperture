/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.file;

import java.io.IOException;
import java.lang.reflect.Method;

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

	private void windowsopen(URI uri) {
		// TODO Auto-generated method stub
		throw new Error("FileOpener:windowsopen not yet implemented.");
	}

	private void linuxopen(URI uri) {
		// TODO Auto-generated method stub
		throw new Error("FileOpener:linuxopen not yet implemented.");
	}
	
	public static void main(String args[]) throws IOException {
		FileOpener f=new FileOpener();
		f.open(new URIImpl("http://www.google.com"));
		f.open(new URIImpl("file:///Users/grimnes/"));
	}

}

