package org.semanticdesktop.nepomuk.nrl.inference.utils;


import info.aduna.xml.XMLUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.semanticdesktop.nepomuk.nrl.inference.exceptions.WrappedIOException;




public class Util {
	 static Charset utf8 = null ;
	    static {
	        try {
	            utf8 = Charset.forName("utf-8") ;
	        } catch (Throwable ex)
	        {
	            Logger.getLogger(Util.class.getName()).warning("Failed to get charset for UTF-8") ;
	        }
	    }
	
	/** Given an absolute URI, determine the split point between the namespace part
     * and the localname part.
     * If there is no valid localname part then the length of the
     * string is returned.
     * The algorithm tries to find the longest NCName at the end
     * of the uri, not immediately preceeded by the first colon
     * in the string.
     * @param uri
     * @return the index of the first character of the localname
     */
    public static int splitNamespace(String uri) {
        char ch;
        int lg = uri.length();
        if (lg == 0)
            return 0;
        int j;
        int i;
        for (i = lg - 1; i >= 1; i--) {
            ch = uri.charAt(i);
            if (notNameChar(ch)) break;
        }
        for (j = i + 1; j < lg; j++) {
            ch = uri.charAt(j);
            if (XMLUtil.isNCNameChar(ch)) {
                if (uri.charAt(j - 1) == ':'
                    && uri.lastIndexOf(':', j - 2) == -1)
                    continue; // split "mailto:me" as "mailto:m" and "e" !
                else
                    break;
            }
        }
        return j;
    }

    /**
	 answer true iff this is not a legal NCName character, ie, is
	 a possible split-point start.
	 */
	public static boolean notNameChar(char ch) {
		return !XMLUtil.isNCNameChar(ch);
	}

	public static BufferedReader readerFromURL(String urlStr) {
		try {
			return asBufferedUTF8(new URL(urlStr).openStream());
		} catch (java.net.MalformedURLException e) { // Try as a plain
														// filename.
			try {
				return asBufferedUTF8(new FileInputStream(urlStr));
			} catch (FileNotFoundException f) {
				throw new WrappedIOException(f);
			}
		} catch (IOException e) {
			throw new WrappedIOException(e);

		}
	}
    /** Create a reader that uses UTF-8 encoding */ 
    
    static public Reader asUTF8(InputStream in) {
        return new InputStreamReader(in, utf8.newDecoder());
    }
	/** Create a buffered reader that uses UTF-8 encoding */

	static public BufferedReader asBufferedUTF8(InputStream in) {
		return new BufferedReader(asUTF8(in));
	}
}
