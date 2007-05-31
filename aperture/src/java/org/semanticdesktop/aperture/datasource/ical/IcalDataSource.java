/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.ical;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * A FileSystemDataSource defines a collection of Files residing on a local or shared drive.
 */
public class IcalDataSource extends DataSourceBase {

    public URI getType() {
        return DATASOURCE_GEN.IcalDataSource;
    } 
}
