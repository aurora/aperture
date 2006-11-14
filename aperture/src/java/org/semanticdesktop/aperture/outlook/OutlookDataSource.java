/*
 * Copyright (c) 2005 - 2006 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A microsoft outlook datasource.
 * 
 * 
 * @author sauermann
 * $Id$
 */
public class OutlookDataSource extends DataSourceBase {

	public URI getType() {
		return DATASOURCE_GEN.MicrosoftOutlookDataSource;
	}

}

