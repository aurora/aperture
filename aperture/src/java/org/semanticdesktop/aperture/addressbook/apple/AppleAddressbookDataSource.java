/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.apple;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class AppleAddressbookDataSource extends DataSourceBase {
	
	public URI getType() {
		return DATASOURCE.AppleAddressbookDataSource;
	}


}

