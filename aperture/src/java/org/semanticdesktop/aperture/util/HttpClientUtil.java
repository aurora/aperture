/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

/**
 * Utility methods for clients dealing with HTTP communication.
 */
public class HttpClientUtil {

    /**
     * Encodes a string according to RFC 3986 : Uniform Resource locators (URL). According to this spec,
     * any characters outside the range 0x20 - 0x7E must be escaped because they are not printable
     * characters. Within the range a number of characters are deemed unsafe or are marked as reserved.
     * In short: According to the spec only the alphanumerics and the special characters from -._~,
     * can be left unencoded. 
     * 
     * @param s The String to encode.
     * @param buffer The buffer to store the encoded String in.
     */
    public static void formUrlEncode(String s, StringBuilder buffer) {
        formUrlEncode(s, buffer, null);
    }

    /**
     * Encodes a string according to RFC 3986 : Uniform Resource locators (URL). According to this spec,
     * any characters outside the range 0x20 - 0x7E must be escaped because they are not printable
     * characters. Within the range a number of characters are deemed unsafe or are marked as reserved.
     * In short: According to the spec only the alphanumerics and the special characters from -._~,
     * can be left unencoded. 
     * 
     * @param s The String to encode.
     * @return An encoded version of the specified String.
     */
    public static String formUrlEncode(String s) {
        StringBuilder result = new StringBuilder(s.length() + 10);
        formUrlEncode(s, result,null);
        return result.toString();
    }
    
    /**
     * Does the same as HttpClientUtil.formUrlEncode (i.e. RFC 1738) except for some characters that are to be
     * left as they are.
     * 
     * @param string the string to be encoded
     * @param charsToLeave a string containing characters that will not be escaped. An example value is "/"
     *            useful for slashes, that are to be left alone in imap folder uris according to RFC 2192
     * @return the encoded folder name
     */
    public static String formUrlEncode(String string, String charsToLeave) {
        StringBuilder result = new StringBuilder(string.length() + 10);
        formUrlEncode(string, result,charsToLeave);
        return result.toString();
    }
    
    /**
     * Decodes an url-encoded string. This method basically substitutes all '+' signs with a space and all
     * '%'-escape sequences with proper character values - according to the UTF16 encoding. 
     * 
     * @param string the string to be decoded
     * @return the decoded version
     */
    public static String formUrlDecode(String string) {
        int length = string.length();
        StringBuilder buffer = new StringBuilder(length + 10);

        int i = 0;
        while (i < length) {
            char c = string.charAt(i);
            if (c == '+') {
                buffer.append(' ');
                i++;
            }
            else if (c == '%') {
                int start = i;
                String character = "";
                do {
                    i += 3;
                } while (i < length && string.charAt(i) == '%'); // remeber to check if i < length
                try {
                    character = URLDecoder.decode(string.substring(start,i),"UTF-8");
                } catch (Exception e) {
                    // gulp!
                }
                buffer.append(character);
            } else {
                buffer.append((char)c);
                i++;
            }
        }
        return buffer.toString();
    }

    /**
     * Sets a request property on the supplied connection indicating that a server can respond with
     * gzip-encoded data if it wants to.
     */
    public static void setAcceptGZIPEncoding(URLConnection conn) {
        conn.setRequestProperty("Accept-Encoding", "gzip");
    }

    /**
     * Gets the InputStream for reading the response from a server. This method handles any
     * encoding-related decoding of the data, e.g. gzip.
     */
    public static InputStream getInputStream(URLConnection conn) throws IOException {
        InputStream responseStream = conn.getInputStream();

        if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
            responseStream = new GZIPInputStream(responseStream);
        }

        return responseStream;
    }
    
    private static void formUrlEncode(String s, StringBuilder buffer, String charsToLeave) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            
            if (c == ' ') {
                // replace all spaces with a '+'
                buffer.append('+');
            }
            else {
                int cInt = (int) c;
                if (cInt >= 48 && cInt <= 57 || // numbers
                    cInt >= 65 && cInt <= 90 || // uppercase
                    cInt >= 97 && cInt <= 122 || // lowercase
                    cInt == 45 || cInt == 95 || cInt == 46 || cInt == 126 || //hyphen, underscode, dot, tilde
                    (charsToLeave != null && charsToLeave.indexOf(c) != -1)) { 
                    buffer.append(c);
                }
                else {
                    String hexVal = null;
                    try {
                        hexVal = URLEncoder.encode(String.valueOf((char)c), "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) {
                        // this will not happen
                    }

                    // ensure use of two characters
                    if (hexVal.length() == 1) {
                        buffer.append('0');
                    }

                    buffer.append(hexVal);
                }
            }
        }
    }
}
