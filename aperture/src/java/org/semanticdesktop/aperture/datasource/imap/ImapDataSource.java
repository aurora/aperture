/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.datasource.imap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.DomainBoundableDataSource;
import org.semanticdesktop.aperture.rdf.MultipleValuesException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.ModelUtil;

/**
 * Data source class file. Created by org.semanticdesktop.aperture.util.DataSourceClassGenerator on Mon Oct 20 11:57:12 CEST 2008
 * input file: D:\ganymedeworkspace\aperture-trunk/src/java/org/semanticdesktop/aperture/datasource/imap/imapDataSource.ttl
 * class uri: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ImapDataSource
 */
public class ImapDataSource extends DomainBoundableDataSource {

    /**
     * @see DataSource#getType()
     */
    public URI getType() {
        return IMAPDS.ImapDataSource;
    }

    /**
     * Returns the The host name of the IMAP server
     * 
     * @return the The host name of the IMAP server or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getHostname() {
          return getConfiguration().getString(IMAPDS.hostname);
     }

    /**
     * Sets the The host name of the IMAP server
     * 
     * @param hostname The host name of the IMAP server, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setHostname(String hostname) {
         if ( hostname == null) {
             getConfiguration().remove(IMAPDS.hostname);
         } else {
             getConfiguration().put(IMAPDS.hostname,hostname);
         }
     }

    /**
     * Returns the The port number where the IMAP server is listening for connections
     * 
     * @return the The port number where the IMAP server is listening for connections or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Integer getPort() {
          return getConfiguration().getInteger(IMAPDS.port);
     }

    /**
     * Sets the The port number where the IMAP server is listening for connections
     * 
     * @param port The port number where the IMAP server is listening for connections, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setPort(Integer port) {
         if ( port == null) {
             getConfiguration().remove(IMAPDS.port);
         } else {
             getConfiguration().put(IMAPDS.port,port);
         }
     }

    /**
     * Returns the Username used for authentication in a data source
     * 
     * @return the Username used for authentication in a data source or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getUsername() {
          return getConfiguration().getString(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username);
     }

    /**
     * Sets the Username used for authentication in a data source
     * 
     * @param username Username used for authentication in a data source, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setUsername(String username) {
         if ( username == null) {
             getConfiguration().remove(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username);
         } else {
             getConfiguration().put(org.semanticdesktop.aperture.vocabulary.DATASOURCE.username,username);
         }
     }

    /**
     * Returns the The Password used to access this datasource.
     * 
     * @return the The Password used to access this datasource. or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getPassword() {
          return getConfiguration().getString(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password);
     }

    /**
     * Sets the The Password used to access this datasource.
     * 
     * @param password The Password used to access this datasource., can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setPassword(String password) {
         if ( password == null) {
             getConfiguration().remove(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password);
         } else {
             getConfiguration().put(org.semanticdesktop.aperture.vocabulary.DATASOURCE.password,password);
         }
     }

    /**
     * Returns a collection of all values of The base path of the IMAP data source
     * 
     * @return a collection of all values of The base path of the IMAP data source the collection may be empty if no values have been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Collection<String> getAllBasepaths() {
          Collection<Node> collection = (Collection<Node>)getConfiguration().getAll(IMAPDS.basepath);
          List<String> result = new LinkedList<String>();
          for (Node node : collection) {
              String object = (String)ModelUtil.convertNode(node,String.class);
              if (object != null) {
                   result.add(object);
              }
          }
          return result;
     }

    /**
     * Sets the The base path of the IMAP data source
     * 
     * @param basepath The base path of the IMAP data source, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     * @throws MultipleValuesException if this property had more that one value before this method was called
     */
     public void setBasepath(String basepath) {
         if ( basepath == null) {
             getConfiguration().remove(IMAPDS.basepath);
         } else {
             getConfiguration().put(IMAPDS.basepath,basepath);
         }
     }

    /**
     * Sets all The base path of the IMAP data sources at once
     * 
     * @param basepath The base path of the IMAP data source, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setAllBasepaths(List<String> basepath) {
         if ( basepath == null) {
             ModelUtil.removeAllPropertyValues(getConfiguration().getModel(),getConfiguration().getDescribedUri(),IMAPDS.basepath);
         } else {
             ModelUtil.removeAllPropertyValues(getConfiguration().getModel(),getConfiguration().getDescribedUri(),IMAPDS.basepath);
             for(String value : basepath) {
                 getConfiguration().add(IMAPDS.basepath,value);
             }
         }
     }

    /**
     * Returns the Should the inbox itself be included in the crawl results?
     * 
     * @return the Should the inbox itself be included in the crawl results? or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getIncludeInbox() {
          return getConfiguration().getBoolean(IMAPDS.includeInbox);
     }

    /**
     * Sets the Should the inbox itself be included in the crawl results?
     * 
     * @param includeInbox Should the inbox itself be included in the crawl results?, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setIncludeInbox(Boolean includeInbox) {
         if ( includeInbox == null) {
             getConfiguration().remove(IMAPDS.includeInbox);
         } else {
             getConfiguration().put(IMAPDS.includeInbox,includeInbox);
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
          return getConfiguration().getLong(IMAPDS.maximumSize);
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
             getConfiguration().remove(IMAPDS.maximumSize);
         } else {
             getConfiguration().put(IMAPDS.maximumSize,maximumSize);
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
          return getConfiguration().getInteger(IMAPDS.maximumDepth);
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
             getConfiguration().remove(IMAPDS.maximumDepth);
         } else {
             getConfiguration().put(IMAPDS.maximumDepth,maximumDepth);
         }
     }

    /**
     * Returns the Should the crawler ignore the UID validity? THIS OPTION MAY BE DANGEROUS, USE AT YOUR OWN RISK, ONLY ON FAULTY SERVERS THAT DON'T PERSIST EMAIL IDs, ONLY IF YOU DON'T DELETE ANY EMAILS AND ONLY IF YOU REALLY NEED IT!!!! IF YOU DO DELETE AN EMAIL FROM SUCH A SERVER, THE CRAWLER WILL RETURN WRONG RESULTS
     * 
     * @return the Should the crawler ignore the UID validity? THIS OPTION MAY BE DANGEROUS, USE AT YOUR OWN RISK, ONLY ON FAULTY SERVERS THAT DON'T PERSIST EMAIL IDs, ONLY IF YOU DON'T DELETE ANY EMAILS AND ONLY IF YOU REALLY NEED IT!!!! IF YOU DO DELETE AN EMAIL FROM SUCH A SERVER, THE CRAWLER WILL RETURN WRONG RESULTS or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public Boolean getIgnoreUidValidity() {
          return getConfiguration().getBoolean(IMAPDS.ignoreUidValidity);
     }

    /**
     * Sets the Should the crawler ignore the UID validity? THIS OPTION MAY BE DANGEROUS, USE AT YOUR OWN RISK, ONLY ON FAULTY SERVERS THAT DON'T PERSIST EMAIL IDs, ONLY IF YOU DON'T DELETE ANY EMAILS AND ONLY IF YOU REALLY NEED IT!!!! IF YOU DO DELETE AN EMAIL FROM SUCH A SERVER, THE CRAWLER WILL RETURN WRONG RESULTS
     * 
     * @param ignoreUidValidity Should the crawler ignore the UID validity? THIS OPTION MAY BE DANGEROUS, USE AT YOUR OWN RISK, ONLY ON FAULTY SERVERS THAT DON'T PERSIST EMAIL IDs, ONLY IF YOU DON'T DELETE ANY EMAILS AND ONLY IF YOU REALLY NEED IT!!!! IF YOU DO DELETE AN EMAIL FROM SUCH A SERVER, THE CRAWLER WILL RETURN WRONG RESULTS, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setIgnoreUidValidity(Boolean ignoreUidValidity) {
         if ( ignoreUidValidity == null) {
             getConfiguration().remove(IMAPDS.ignoreUidValidity);
         } else {
             getConfiguration().put(IMAPDS.ignoreUidValidity,ignoreUidValidity);
         }
     }

    /**
     * Enum of possible values of the connectionSecurity property
     */
     public static enum ConnectionSecurity {
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#PLAIN*/
         PLAIN,
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#SSL*/
         SSL,
         /** Constant representing http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#SSL_NO_CERT*/
         SSL_NO_CERT;

         public static ConnectionSecurity fromUri(URI uri) {
             if (uri == null) {
                 return null;
             }
             else if (uri.equals(IMAPDS.PLAIN)) {
                 return PLAIN;
             }
             else if (uri.equals(IMAPDS.SSL)) {
                 return SSL;
             }
             else if (uri.equals(IMAPDS.SSL_NO_CERT)) {
                 return SSL_NO_CERT;
             }
             else {
                 return null;
             }
         }
         public URI toUri() {
             if (this.equals(PLAIN)) {
                 return IMAPDS.PLAIN;
             }
             else if (this.equals(SSL)) {
                 return IMAPDS.SSL;
             }
             else if (this.equals(SSL_NO_CERT)) {
                 return IMAPDS.SSL_NO_CERT;
             }
             else {
                 return null;
             }
         }
     }

    /**
     * Returns the The level of security for the connection
     * 
     * @return the The level of security for the connection or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public ConnectionSecurity getConnectionSecurity() {
          return ConnectionSecurity.fromUri(getConfiguration().getURI(IMAPDS.connectionSecurity));
     }


    /**
     * Sets the The level of security for the connection
     * 
     * @param connectionSecurity The level of security for the connection, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setConnectionSecurity(ConnectionSecurity connectionSecurity) {
         if ( connectionSecurity == null) {
             getConfiguration().remove(IMAPDS.connectionSecurity);
         } else {
             getConfiguration().put(IMAPDS.connectionSecurity,connectionSecurity.toUri());
         }
     }

    /**
     * Returns the The path to the ssl keyfile
     * 
     * @return the The path to the ssl keyfile or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getSslFileName() {
          return getConfiguration().getString(IMAPDS.sslFileName);
     }

    /**
     * Sets the The path to the ssl keyfile
     * 
     * @param sslFileName The path to the ssl keyfile, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setSslFileName(String sslFileName) {
         if ( sslFileName == null) {
             getConfiguration().remove(IMAPDS.sslFileName);
         } else {
             getConfiguration().put(IMAPDS.sslFileName,sslFileName);
         }
     }

    /**
     * Returns the Keyphrase for the SSL keyfile
     * 
     * @return the Keyphrase for the SSL keyfile or null if no value has been set
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public String getSslFilePassword() {
          return getConfiguration().getString(IMAPDS.sslFilePassword);
     }

    /**
     * Sets the Keyphrase for the SSL keyfile
     * 
     * @param sslFilePassword Keyphrase for the SSL keyfile, can be null in which case any previous setting will be removed
     * @throws NullPointerException if no configuration has been set, use
     *             {@link #setConfiguration(RDFContainer)} before calling this method
     */
     public void setSslFilePassword(String sslFilePassword) {
         if ( sslFilePassword == null) {
             getConfiguration().remove(IMAPDS.sslFilePassword);
         } else {
             getConfiguration().put(IMAPDS.sslFilePassword,sslFilePassword);
         }
     }
}
