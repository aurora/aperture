/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook;

import org.ontoware.rdf2go.model.node.URI;
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

