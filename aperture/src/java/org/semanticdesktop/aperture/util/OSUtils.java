/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gnowsis.util.AppleUtils;
import org.semanticdesktop.aperture.addressbook.AddressbookCrawler;

/**
 * 
 * @author grimnes
 */
public abstract class OSUtils {
	
	protected static final Logger log = Logger.getLogger(OSUtils.class.getName());
	
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	public static boolean isMac() { 
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}
	
	public static boolean isLinux() { 
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	/**
	 * Checks if the given directory is a MacOSX bundle. 
	 * This only makes sense on MaxOSX, and will always return false on other OS. 
	 * @param f - the directory to check
	 * @return
	 */
	public static boolean isMacOSXBundle(File f) {
		if (!isMac()) return false; 
		try {
			return AppleUtils.isBundle(f); 
		}
		catch (Throwable e) {
			log.log(Level.INFO, "Could not check if directoy was bundle, assuming not. ",e);
			return false;
		}
		
	}
}
