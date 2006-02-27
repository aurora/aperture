/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * A microsoft outlook datasource.
 * 
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookDataSource extends DataSourceBase {

	public URI getType() {
		return DATASOURCE.MicrosoftOutlookDataSource;
	}

}

