/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.file;

import java.util.Collections;
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

