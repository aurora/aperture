/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.websites.bibsonomy;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Node;
import org.semanticdesktop.aperture.util.ModelUtil;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.base.DataSourceBase;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.util.ModelUtil;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Wed Nov 19 17:40:42 CET 2008
 * input file: src/org/semanticdesktop/aperture/websites/bibsonomy/bibsonomyDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2008/11/14/bibsonomyds#BibsonomyDataSource
 */
public class BibsonomyDataSource extends DataSourceBase {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return BIBSONOMYDS.BibsonomyDataSource;
    }

    /**
     * Returns the The username associated with the API key. Necessary for programmatic access to bibsonomy. It can be different from the name of the account that is to be crawled.
     * 
     * @return the The username associated with the API key. Necessary for programmatic access to bibsonomy. It can be different from the name of the account that is to be crawled. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getApiusername() {
          return getConfiguration().getString(BIBSONOMYDS.apiusername);
     }

    /**
     * Sets the The username associated with the API key. Necessary for programmatic access to bibsonomy. It can be different from the name of the account that is to be crawled.
     * 
     * @param apiusername The username associated with the API key. Necessary for programmatic access to bibsonomy. It can be different from the name of the account that is to be crawled., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setApiusername(String apiusername) {
         if ( apiusername == null) {
             getConfiguration().remove(BIBSONOMYDS.apiusername);
         } else {
             getConfiguration().put(BIBSONOMYDS.apiusername,apiusername);
         }
     }

    /**
     * Returns the The API key
     * 
     * @return the The API key or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getApikey() {
          return getConfiguration().getString(BIBSONOMYDS.apikey);
     }

    /**
     * Sets the The API key
     * 
     * @param apikey The API key, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setApikey(String apikey) {
         if ( apikey == null) {
             getConfiguration().remove(BIBSONOMYDS.apikey);
         } else {
             getConfiguration().put(BIBSONOMYDS.apikey,apikey);
         }
     }

    /**
     * Returns the The name of the account that is to be crawled
     * 
     * @return the The name of the account that is to be crawled or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getCrawledusername() {
          return getConfiguration().getString(BIBSONOMYDS.crawledusername);
     }

    /**
     * Sets the The name of the account that is to be crawled
     * 
     * @param crawledusername The name of the account that is to be crawled, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setCrawledusername(String crawledusername) {
         if ( crawledusername == null) {
             getConfiguration().remove(BIBSONOMYDS.crawledusername);
         } else {
             getConfiguration().put(BIBSONOMYDS.crawledusername,crawledusername);
         }
     }
}
