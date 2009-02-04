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
package org.semanticdesktop.demork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;

/**
 * @author ggrimnes
 *
 */
public class Utils {

	public Hashtable invertDict (Hashtable<String,String> dict) {
	    Hashtable<String,String> idict = new Hashtable<String,String>();
	    for (String key : dict.keySet()) 
	        idict.put(dict.get(key), key);
	    return idict;
	}
	

	public int hexcmp(String x, String y) {
	    try { 
	        int a = Integer.parseInt(x, 16);
	        int b = Integer.parseInt(y, 16);
	        if (a < b)  return -1;
	        if (a > b) return 1;
	        return 0;

	    } catch(Exception e) {
	        return x.compareTo(y);
	    }
	}

	
	//Everything below here is stolen from Jena:
	
	
	
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
