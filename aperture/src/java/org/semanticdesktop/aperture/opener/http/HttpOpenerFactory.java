/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.http;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;


public class HttpOpenerFactory implements DataOpenerFactory {

	public Set getSupportedSchemes() {
		return Collections.singleton("http");
	}

	public DataOpener get() {
		return new HttpOpener();
	}

}

