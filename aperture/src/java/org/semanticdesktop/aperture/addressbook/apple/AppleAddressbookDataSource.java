/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.addressbook.apple;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Tue Jul 15 22:55:59 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/addressbook/apple/AppleAddressbookDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/appleaddresbookds#AppleAddressbookDataSource
 */
public class AppleAddressbookDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return APPLEADDRESSBOOKDS.AppleAddressbookDataSource;
    }
}
