/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

/**
 * Utility methods for detecting the OS
 * 
 * @author grimnes
 */
public class PlatformUtil {
	public static boolean isMac() {
		return System.getProperty("os.name").contains("Mac");
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}
	
	public static boolean isLinux() { 
		return System.getProperty("os.name").equalsIgnoreCase("Linux");
	}
}

