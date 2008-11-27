/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;

public class OutlookAccessorFactory implements DataAccessorFactory {

	private static final Set SUPPORTED_SCHEMES = Collections.singleton("outlook");

	private OutlookAccessor accessor;

	public Set getSupportedSchemes() {
		return SUPPORTED_SCHEMES;
	}

	public DataAccessor get() {
		if (accessor == null) {
			accessor = new OutlookAccessor();
		}
		return accessor;
	}
}
