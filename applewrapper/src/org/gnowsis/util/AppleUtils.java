/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.gnowsis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JMenu;

import apple.laf.AquaSystemIcon;

import com.apple.cocoa.foundation.NSAppleEventDescriptor;
import com.apple.cocoa.foundation.NSAppleScript;
import com.apple.cocoa.foundation.NSArray;
import com.apple.cocoa.foundation.NSMutableDictionary;
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * @author grimnes
 *
 */
public class AppleUtils {
	
	private class App extends ApplicationAdapter {
		@Override
		public void handleQuit(ApplicationEvent event) {
			
			if (quit!=null) {
				event.setHandled(false);
				try {
					quit.invoke(quito,new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		@Override
		public void handleAbout(ApplicationEvent event) {
			if (about!=null)
				try {
					event.setHandled(true);
					about.invoke(abouto,new Object[]{});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		@Override
		public void handlePreferences(ApplicationEvent event) {
			if (pref!=null)
				try {
					event.setHandled(true);
					pref.invoke(prefo,new Object[]{});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Oh, i'm so pretty, so PRETTY! SO PRETTY! 
	 * that the city should give me it's key! 
	 */
	{ 
		try {
			addURL(new File("/System/Library/Java/").toURL());
		} catch (Exception e) {
			System.err.println("Could not add cocoa libraries to classpath.");
			e.printStackTrace();
		}
	} 
	
	final static private String [] bundleextension=new String [] { 
			".app",".bundle",".framework",".kext",".mpkg", "mdimporter",".nib",".pbproj",
			".pkg",".plugin",".prefPane",".rtfd",".saver",".slideSaver",".wdgt",".webarchive",".xcode",".xcodeproj",
			".key",".pages" };
		
	
	/**
	 * This returns true if the file parameter is a "bundle"
	 * I've found no mac library to detect this, so it's based on file extension, which is well shitty.
	 *  
	* .app - Application bundle (com.apple.application-â€‹bundle)
    * .bundle - Generic bundle (com.apple.bundle)
    * .framework - Framework bundle (com.apple.framework)
    * .kext - Kernel EXTension?
    * .mpkg - see Archives, Disk Images, Compression
    * .mdimporter - Spotlight Metadata Importer (com.apple.metadata-â€‹importer)
    * .nib - NeXT Interface Builder
    * .pbproj - ProjectBuilder project (also openable by XCode; see also .xcode)
    * .pkg - see Archives, Disk Images, Compression
    * .plugin - Plugin bundle (com.apple.plugin)
    * .prefPane - System Preferences pane bundle
    * .rtfd - See Text Files
    * .saver - Screensaver bundle
    * .slideSaver - Slideshow screensaver bundle (with embedded images)
    * .wdgt - Dashboard widget (com.apple.dashboard-â€‹widget)
    * .webarchive - Safari web archive
    * .xcode - XCode project (version 2.0 and earlier)
    * .xcodeproj - XCode project (version 2.1 and later) 
    * 
    * .pages - pages document
    * .key - keynot document
    * 
	 * @param f
	 * @throws IOException
	 */
	public static boolean isBundle(File f) throws IOException {
		if (f.isDirectory()) { 
			String filename=f.getName();
			for (String e : bundleextension) {
				if (filename.endsWith(e)) return true;
			}
		}
		return false;
	}
	
	private static void addURL(URL u) throws IOException {
		
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
	 
		try {
			Method method = sysclass.getDeclaredMethod("addURL",new Class[]{URL.class});
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}//end try catch
			
	}//end method
	/**
	 * @author grimnes
	 *
	 */
	public class AppleScriptException extends Exception {

		/**
		 * generated by eclipse 
		 */
		private static final long serialVersionUID = -5426568970373973484L;
		
		private Map<String, String> error;
		private String msg;

		/**
		 * @param string
		 * @param err
		 */
		public AppleScriptException(String string, Map<String, String> err) {
			super(string);
			msg=string;
			error=err;
		}

		/**
		 * @param string
		 */
		public AppleScriptException(String string) {
			msg=string;
			error=new Hashtable<String,String>();
		}

		public String toString() {
			return msg+": "+error.toString();
		}
	}

	private Method pref, about;
	private Object prefo, abouto;
	private Application a;
	private Method quit;
	private Object quito;
	private App app;
	
	public AppleUtils() {
		app=new App();
		a=Application.getApplication();
		a.addAboutMenuItem();
		a.addPreferencesMenuItem();
		a.setEnabledAboutMenu(false);
		a.setEnabledPreferencesMenu(false);
		
		a.addApplicationListener(app);
		
	}
	
	public void registerPreferences(Method preferences, Object object) {
		pref=preferences;
		prefo=object;
		a.setEnabledPreferencesMenu(true);
	}
	
	public void registerAbout(Method about, Object object) {
		abouto=object;
		this.about=about;
		a.setEnabledAboutMenu(true);
	}
	
	public void registerQuit(Method quit, Object object) {
		this.quit=quit;
		quito=object;
	}

	
	
	public void addDockMenu(JMenu menu) {	
		AquaSystemIcon a=new AquaSystemIcon("kake");
		//apple.awt.
	}

	
	
	public static ClassLoader getAppleClassLoader() throws MalformedURLException {
		return new URLClassLoader(new URL[] { new File("/System/Library/Java").toURL() });
	}
	
	public String applescript(String script) throws AppleScriptException { 
		NSAppleScript nsa=new NSAppleScript(script);
		NSMutableDictionary errors =
		     new NSMutableDictionary();
		NSAppleEventDescriptor res = nsa.execute(errors);
		if (errors.allKeys().count()>0) {
			//we cannot return a NSObject, so make up a string representation instead.
			Map<String,String> err=new Hashtable<String,String>();
			NSArray a=errors.allKeys();
			for (int i=0;i<a.count();i++) {
				Object k=a.objectAtIndex(i);
				err.put(k.toString(),errors.objectForKey(k).toString()); 
			}
			throw new AppleScriptException("Error while executing applescript.",err);
		}
		
		return res.stringValue();
	}
	
	public static void main(String [] args) throws Exception {
		AppleUtils a=new AppleUtils();
		/* String script="";
		script=readWholeFileAsUTF8("/Users/grimnes/2006/03/applescript/address3.applescript");
		System.err.println("Executing: "+script);
		System.err.println(a.applescript(script)); */
		
		System.err.println(AppleUtils.isBundle(new File("/Users/grimnes")));
		System.err.println(AppleUtils.isBundle(new File("/Applications/Net/Firefox.app")));
		System.err.println(AppleUtils.isBundle(new File("/Users/grimnes/Documents/presentations/TRB - Nepomuk.key")));
		System.err.println(AppleUtils.isBundle(new File("/Users/grimnes/foaf-pimo.xml")));
		
	}
	
	/** Read a whole file as UTF-8
     * @param filename
     * @return String
     * @throws IOException
     * Stolen from Jena 
     **/
	public static String readWholeFileAsUTF8(String filename) throws IOException {
        return readWholeFileAsEncoding(filename,"utf-8") ;
    }
	
	public static String readWholeFileAsEncoding(String filename, String encoding) throws IOException { 
		InputStream in = new FileInputStream(filename) ;
		Reader r = new BufferedReader(asEncoding(in,encoding),1024) ;
		StringWriter sw = new StringWriter(1024);
		char buff[] = new char[1024];
		while (r.ready()) {
			int l = r.read(buff);
			if (l <= 0)
				break;
			sw.write(buff, 0, l);
		}
		r.close();
		sw.close();
		return sw.toString();  
	}
	
	 /**
	 * @param in
	 * @param encoding
	 * @return
	 */
	private static Reader asEncoding(InputStream in, String encoding) {
		Charset charset=Charset.forName(encoding);
		
		return new InputStreamReader(in, charset.newDecoder());
	}


}
