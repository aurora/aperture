/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum f�r K�nstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;


/**
 * 
 * 
 * @author grimnes
 * $Id$
 */
public class AddressbookDataSource extends DataSourceBase {

	protected static URI type=DATASOURCE.AddressbookDataSource;
	
	public URI getType() {
		return type;
	}


}

