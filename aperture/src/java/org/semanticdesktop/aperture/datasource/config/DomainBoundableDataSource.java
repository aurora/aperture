/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public abstract class DomainBoundableDataSource extends DataSourceBase implements DomainBoundable {

    /**
     * Returns the domain boundaries for this data source
     * 
     * @return the domain boundaries for this data source
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
    public DomainBoundaries getDomainBoundaries() {
        return ConfigurationUtil.getDomainBoundaries(getConfiguration());
    }

    /**
     * Sets the domain boundaries for this data source
     * 
     * @param domainBoundaries the domain boundaries, can be null in which case any previous setting will be
     *            removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
    public void setDomainBoundaries(DomainBoundaries domainBoundaries) {
        if (domainBoundaries == null) {
            DomainBoundaries emptyBoundaries = new DomainBoundaries();
            ConfigurationUtil.setDomainBoundaries(emptyBoundaries, getConfiguration());
        }
        else {
            ConfigurationUtil.setDomainBoundaries(domainBoundaries, getConfiguration());
        }
    }
}
