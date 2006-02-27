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
/*
 * Created on 08.09.2004
 *
 * 
 */
package org.semanticdesktop.aperture.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to set all loggers to "blow out everything"
 * 
 * @author Sauermann
 */
public class LogUtil {

	/**
	 * 
	 * set all loggers to "blow out everything"
	 * 
	 */

	private static boolean isFull = false;
	
	public static boolean blockLoggingForMajorTests = false;

	private static boolean addedConsoleHandler = false;

	private static void addConsoleHandler() {
		if (addedConsoleHandler)
			return;
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
		addedConsoleHandler = true;
	}

	/**
	 * set all loggers to "blow out everything"
	 */
	public static void setFullLogging() {
		
		if (blockLoggingForMajorTests || isFull)
			return;
		setLogging(Level.ALL);
		isFull = true;
	}

	public static void setLogging(Level level) {
		if (blockLoggingForMajorTests)
			return;
		Logger.global.setLevel(level);
		addConsoleHandler();
		Logger.getLogger("org.gnowsis").setLevel(level);
		Logger.getLogger("org.gnogno").setLevel(level);
		Logger.getLogger("org.openrdf").setLevel(level);
		Logger.getLogger("org.semanticdesktop").setLevel(level);
	}

	/**
	 * set all loggers to "blow out everything" into this file. It will limit
	 * the logged classes to "org.*".
	 * 
	 * @throws IOException
	 *             if there are IO problems opening the files.
	 * @throws SecurityException
	 *             if a security manager exists and if the caller does not have
	 *             LoggingPermission("control").
	 * 
	 */
	public static void setLoggingInto(File file, Level level)
			throws SecurityException, IOException {
		if (blockLoggingForMajorTests)
			return;
		Logger.global.setLevel(level);
		addConsoleHandler();
		Logger.getLogger("org.openrdf").setLevel(level);
		Logger.getLogger("org.semanticdesktop").setLevel(level);
		Handler handler = new FileHandler(file.getName(), false);
		handler.setLevel(Level.ALL);
		Handler[] ha = Logger.global.getHandlers();
		for (int i = 0; i < ha.length; i++) {
			Logger.global.removeHandler(ha[i]);
		}
		Logger.global.addHandler(handler);
		Logger.getLogger("org.gnowsis").setLevel(level);
		Logger.getLogger("org.gnogno").setLevel(level);
		Logger.getLogger("org.openrdf").setLevel(level);
		Logger.getLogger("org.semanticdesktop").setLevel(level);
	}
	
	/**
	 * call this method during TestAll.setup or so, 
	 * before you start millions of JUnit tests.
	 * Most of our JUnit tests call {@link #setFullLogging()} during their
	 * setup, this method here will effectively block that.
	 *
	 */
	public static void blockLoggingForMajorTests()
	{
		blockLoggingForMajorTests = true;
	}

}

/*
 * $Log$
 * Revision 1.1  2006/02/27 14:05:46  leo_sauermann
 * Implemented First version of Outlook. Added the vocabularyWriter for ease of vocabulary and some launch configs to run it. Added new dependencies (jacob)
 * Revision 1.3 2004/09/09 15:38:34 kiesel - added
 * CVS tags
 * 
 */
