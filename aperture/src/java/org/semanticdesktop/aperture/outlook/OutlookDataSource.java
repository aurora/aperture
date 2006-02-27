/*
 * Copyright (c) 2005 - 2006 Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
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

