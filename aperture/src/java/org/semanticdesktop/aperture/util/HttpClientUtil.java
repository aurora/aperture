/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

/**
 * Utility methods for clients dealing with HTTP communication.
 */
public class HttpClientUtil {

    /**
     * Encodes a string according to RFC 1738 : Uniform Resource locators (URL). According to this spec,
     * any characters outside the range 0x20 - 0x7E must be escaped because they are not printable
     * characters. Within the range a number of characters are deemed unsafe or are marked as reserved.
     * In short: According to the spec only the alphanumerics and the special characters from $-_.+!*'(),
     * can be left unencoded. To be save this method will encode all characters that are not
     * alphanumerics.
     * @param s The String to encode.
     * @param buffer The buffer to store the encoded String in.
     */
    public static void formUrlEncode(String s, StringBuilder buffer) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);

            // Only characters in the range 48 - 57 (numbers), 65 - 90 (upper case letters), 97 - 122
            // (lower case letters) can be left unencoded. The rest needs to be escaped.

            if (c == ' ') {
                // replace all spaces with a '+'
                buffer.append('+');
            }
            else {
                int cInt = (int) c;
                if (cInt >= 48 && cInt <= 57 || cInt >= 65 && cInt <= 90 || cInt >= 97 && cInt <= 122) {
                    // alphanumeric character
                    buffer.append(c);
                }
                else {
                    // escape all non-alphanumerics
                    buffer.append('%');
                    String hexVal = Integer.toHexString((int) c);

                    // ensure use of two characters
                    if (hexVal.length() == 1) {
                        buffer.append('0');
                    }

                    buffer.append(hexVal);
                }
            }
        }
    }

    /**
     * Encodes a string according to RFC 1738 : Uniform Resource locators (URL). According to this spec,
     * any characters outside the range 0x20 - 0x7E must be escaped because they are not printable
     * characters. Within the range a number of characters are deemed unsafe or are marked as reserved.
     * In short: According to the spec only the alphanumerics and the special characters from $-_.+!*'(),
     * can be left unencoded. To be save this method will encode all characters that are not
     * alphanumerics.
     * @param s The String to encode.
     * @return An encoded version of the specified String.
     */
    public static String formUrlEncode(String s) {
        StringBuilder result = new StringBuilder(s.length() + 10);
        formUrlEncode(s, result);
        return result.toString();
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
}
