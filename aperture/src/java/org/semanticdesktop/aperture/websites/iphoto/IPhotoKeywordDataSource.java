/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.websites.iphoto;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Tue Jul 15 22:54:52 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/websites/iphoto/iphotoDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/11/iphotods#IPhotoKeywordDataSource
 */
public class IPhotoKeywordDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return IPHOTODS.IPhotoKeywordDataSource;
    }
}
