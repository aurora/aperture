/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;


public class HttpOpenerFactory implements DataOpenerFactory {
    
    public static final Set<String> SUPPORTEDSCHEMES = new HashSet<String>();
    static {
        SUPPORTEDSCHEMES.add("http");     
        SUPPORTEDSCHEMES.add("https");     
    }

	public Set getSupportedSchemes() {
		return SUPPORTEDSCHEMES;
	}

	public DataOpener get() {
		return new HttpOpener();
	}

}

