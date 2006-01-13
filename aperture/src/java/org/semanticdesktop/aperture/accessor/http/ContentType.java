/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContentType {

	/**
     * Extracts the mime type from a content type header.
     */
	public static String getMimeType(String contentType) {
		ContentType ct = new ContentType(contentType);
		return ct.getMimeType();
	}

	public static final String CHARSET_KEY = "charset";

	private String mimeType;

    private HashMap parameterMap;

	/**
     * Creates a new ContentType object for the header field value.
     * 
     * @param contentType A Content-Type header field value.
     */
	public ContentType(String contentType) {
		parameterMap = new HashMap(4);
		processContentType(contentType);
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getParameter(String key) {
		return (String) parameterMap.get(key.toLowerCase());
	}

	public Map getParameters() {
		return Collections.unmodifiableMap(parameterMap);
	}

	public String getCharset() {
		return getParameter(CHARSET_KEY);
	}

	private void processContentType(String contentType) {
		// Check if there are any parameters in the value
		int semiColonIdx = contentType.indexOf(';');

		if (semiColonIdx == -1) {
			// Content-Type field only specifies MIME type
			mimeType = contentType.trim();
		}
		else {
			// Content-Type field specifies extra parameters
			mimeType = contentType.substring(0, semiColonIdx).trim();

			int nextSemiColonIdx = contentType.indexOf(';', semiColonIdx + 1);
			while (nextSemiColonIdx != -1) {
				processParameter( contentType.substring(semiColonIdx + 1, nextSemiColonIdx) );

				semiColonIdx = nextSemiColonIdx;
				nextSemiColonIdx = contentType.indexOf(';', semiColonIdx + 1);
			}

			processParameter(contentType.substring(semiColonIdx + 1));
		}
	}

	/**
     * Parses a Content-Type parameter. If the parameter specifies a character set, then this character
     * set is stored in the parameter map.
     */
	private void processParameter(String param) {
		// the equals sign separates the key from the value:
		int equalsIdx = param.indexOf('=');

		if (equalsIdx >= 1) {
			String key = param.substring(0, equalsIdx).trim().toLowerCase();
			String value = param.substring(equalsIdx + 1).trim();

			parameterMap.put(key, value);
		}
		else {
			// Parameter is not a key-value pair, assume that it specifies the
			// character encoding, if none was set yet, as this is how some web
			// servers report it, e.g.: text/html;ISO-8859-1
			if (!parameterMap.containsKey(CHARSET_KEY)) {
				parameterMap.put(CHARSET_KEY, param.trim());
			}
		}
	}
}
