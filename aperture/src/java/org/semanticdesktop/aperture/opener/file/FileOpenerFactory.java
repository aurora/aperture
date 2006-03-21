/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.file;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;


public class FileOpenerFactory implements DataOpenerFactory {

	public Set getSupportedSchemes() {
		return Collections.singleton("file");
	}

	public DataOpener get() {
		return new FileOpener();
	}

}

