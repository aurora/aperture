/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 
 * @author grimnes
 */
public abstract class OSUtils {
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	public static boolean isMac() { 
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}
	
	public static boolean isLinux() { 
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	public static boolean isMacOSXBundle(File f) {
		Class apc;
		try {
			apc = OSUtils.class.getClassLoader().loadClass("org.gnowsis.util.AppleUtils");
			Method m = apc.getMethod("isBundle",new Class[] { File.class });
			return ((Boolean)m.invoke(null,new Object[] { f })).booleanValue();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
