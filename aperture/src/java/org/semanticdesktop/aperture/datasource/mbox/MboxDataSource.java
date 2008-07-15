/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.mbox;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Node;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.config.DomainBoundableDataSource;
import org.semanticdesktop.aperture.util.ModelUtil;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Tue Jul 15 22:56:16 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/mbox/mboxDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2008/02/03/mboxds#MboxDataSource
 */
public class MboxDataSource extends DomainBoundableDataSource {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return MBOXDS.MboxDataSource;
    }

    /**
     * Returns the The path to the mbox file
     * 
     * @return the The path to the mbox file or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getMboxPath() {
          return getConfiguration().getString(MBOXDS.mboxPath);
     }

    /**
     * Sets the The path to the mbox file
     * 
     * @param mboxPath The path to the mbox file, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMboxPath(String mboxPath) {
         if ( mboxPath == null) {
             getConfiguration().remove(MBOXDS.mboxPath);
         } else {
             getConfiguration().put(MBOXDS.mboxPath,mboxPath);
         }
     }

    /**
     * Returns the Maximum size (in bytes) of the attachments that are to be reported by the crawler
     * 
     * @return the Maximum size (in bytes) of the attachments that are to be reported by the crawler or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Long getMaximumSize() {
          return getConfiguration().getLong(MBOXDS.maximumSize);
     }

    /**
     * Sets the Maximum size (in bytes) of the attachments that are to be reported by the crawler
     * 
     * @param maximumSize Maximum size (in bytes) of the attachments that are to be reported by the crawler, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumSize(Long maximumSize) {
         if ( maximumSize == null) {
             getConfiguration().remove(MBOXDS.maximumSize);
         } else {
             getConfiguration().put(MBOXDS.maximumSize,maximumSize);
         }
     }

    /**
     * Returns the Maximum depth of the crawl
     * 
     * @return the Maximum depth of the crawl or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Integer getMaximumDepth() {
          return getConfiguration().getInteger(MBOXDS.maximumDepth);
     }

    /**
     * Sets the Maximum depth of the crawl
     * 
     * @param maximumDepth Maximum depth of the crawl, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setMaximumDepth(Integer maximumDepth) {
         if ( maximumDepth == null) {
             getConfiguration().remove(MBOXDS.maximumDepth);
         } else {
             getConfiguration().put(MBOXDS.maximumDepth,maximumDepth);
         }
     }
}
