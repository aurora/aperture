/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.imap;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Jul 15 22:55:25 CEST 2008
 * input file: D:\workspace\aperture/src/java/org/semanticdesktop/aperture/datasource/imap/imapDataSource.ttl
 * namespace: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#
 */
public class IMAPDS {

    /** Path to the ontology resource */
    public static final String IMAPDS_RESOURCE_PATH = 
      "org/semanticdesktop/aperture/datasource/imap/imapDataSource.ttl";

    /**
     * Puts the IMAPDS ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getIMAPDSOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(IMAPDS_RESOURCE_PATH, IMAPDS.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + IMAPDS_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.Turtle);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for IMAPDS */
    public static final URI NS_IMAPDS = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#");
    /**
     * Type: Class <br/>
     * Label: IMAP Account Data Source  <br/>
     * Comment: Describes a mailbox accessible with the IMAP protocol  <br/>
     */
    public static final URI ImapDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ImapDataSource");
    /**
     * Type: Class <br/>
     * Comment: Type of connection security, instances of this class serve as values for the connectionSecurity property   <br/>
     */
    public static final URI ConnectionSecurity = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ConnectionSecurity");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ConnectionSecurity <br/>
     */
    public static final URI PLAIN = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#PLAIN");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ConnectionSecurity <br/>
     */
    public static final URI SSL = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#SSL");
    /**
     * Type: Instance of http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ConnectionSecurity <br/>
     */
    public static final URI SSL_NO_CERT = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#SSL_NO_CERT");
    /**
     * Type: Property <br/>
     * Label: Host name  <br/>
     * Comment: The host name of the IMAP server  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI hostname = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#hostname");
    /**
     * Type: Property <br/>
     * Label: Port number  <br/>
     * Comment: The port number where the IMAP server is listening for connections  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI port = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#port");
    /**
     * Type: Property <br/>
     * Label: Base Path  <br/>
     * Comment: The base path of the IMAP data source  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI basepath = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#basepath");
    /**
     * Type: Property <br/>
     * Label: Include Inbox?  <br/>
     * Comment: Should the inbox itself be included in the crawl results?  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#boolean  <br/>
     */
    public static final URI includeInbox = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#includeInbox");
    /**
     * Type: Property <br/>
     * Label: Maximum Depth  <br/>
     * Comment: Maximum depth of the crawl  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#maximumDepth");
    /**
     * Type: Property <br/>
     * Label: Maximum Size  <br/>
     * Comment: Maximum size (in bytes) of the attachments that are to be reported by the crawler  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#long  <br/>
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#maximumSize");
    /**
     * Type: Property <br/>
     * Label: Connection security  <br/>
     * Comment: The level of security for the connection  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#ConnectionSecurity  <br/>
     */
    public static final URI connectionSecurity = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#connectionSecurity");
    /**
     * Type: Property <br/>
     * Label: SSL File Name  <br/>
     * Comment: The path to the ssl keyfile  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sslFileName = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#sslFileName");
    /**
     * Type: Property <br/>
     * Label: SSL File Password  <br/>
     * Comment: Keyphrase for the SSL keyfile  <br/>
     * Domain: http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#IMAPDataSource  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sslFilePassword = new URIImpl("http://aperture.semanticdesktop.org/ontology/2007/08/12/imapds#sslFilePassword");
}
