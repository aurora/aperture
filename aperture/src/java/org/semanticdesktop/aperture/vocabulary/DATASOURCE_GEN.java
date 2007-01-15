package org.semanticdesktop.aperture.vocabulary;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Mon Jan 15 13:48:42 CET 2007
 * input file: doc/ontology/source.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/source#
 */
public interface DATASOURCE_GEN {
	public static final String NS_DATASOURCE_GEN = "http://aperture.semanticdesktop.org/ontology/source#";

    /**
     * Label: AddressbookDataSource 
     */
    public static final URI AddressbookDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#AddressbookDataSource");

    /**
     * Label: AppleAddressbookDataSource 
     */
    public static final URI AppleAddressbookDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#AppleAddressbookDataSource");

    /**
     * Label: DataSource 
     */
    public static final URI DataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#DataSource");

    /**
     * Label: DataSourceDescription 
     * Comment: A human-readable description of a datasource. Adds icons, texts etc. 
     */
    public static final URI DataSourceDescription = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#DataSourceDescription");

    /**
     * Label: FileSystemDataSource 
     */
    public static final URI FileSystemDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#FileSystemDataSource");

    /**
     * Label: IMAPDataSource 
     */
    public static final URI IMAPDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource");

    /**
     * Label: IcalDataSource 
     */
    public static final URI IcalDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#IcalDataSource");

    /**
     * Label: MicrosoftOutlookDataSource 
     */
    public static final URI MicrosoftOutlookDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#MicrosoftOutlookDataSource");

    /**
     * Label: Pattern 
     */
    public static final URI Pattern = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#Pattern");

    /**
     * Label: RegExpPattern 
     */
    public static final URI RegExpPattern = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#RegExpPattern");

    /**
     * Label: SubstringPattern 
     */
    public static final URI SubstringPattern = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#SubstringPattern");

    /**
     * Label: TaggingDataSource 
     */
    public static final URI TaggingDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#TaggingDataSource");

    /**
     * Label: ThunderbirdAddressbookDataSource 
     */
    public static final URI ThunderbirdAddressbookDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#ThunderbirdAddressbookDataSource");

    /**
     * Label: WebDataSource 
     */
    public static final URI WebDataSource = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#WebDataSource");

    /**
     * Label: basepath 
     * Comment: basepath on the server. For IMAP this is the folder where the e-mails are stored. Some IMAP servers need this. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI basepath = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#basepath");

    /**
     * Label: condition 
     * Comment: the condition for this pattern to match 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#Pattern 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI condition = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#condition");

    /**
     * Label: connectionSecurity 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI connectionSecurity = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#connectionSecurity");

    /**
     * Label: dataSourceComment 
     * Comment: A comment about the datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSourceDescription 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI dataSourceComment = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#dataSourceComment");

    /**
     * Label: dataSourceName 
     * Comment: Name of the type of datasource. For example "Local File System". 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSourceDescription 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI dataSourceName = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#dataSourceName");

    /**
     * Label: describedDataSourceType 
     * Comment: The datasource described 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSourceDescription 
     * Range: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     */
    public static final URI describedDataSourceType = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#describedDataSourceType");

    /**
     * Label: encryptedPassword 
     * Comment: This is a utility field that implementations can use to store encrypted password for added security. Note that aperture does not make use of this, and no restrictions on what encryption can be used are implied... ROT13 if you want. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI encryptedPassword = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#encryptedPassword");

    /**
     * Label: excludePattern 
     * Comment: Patterns to exclude from this datasource 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://aperture.semanticdesktop.org/ontology/source#Pattern 
     */
    public static final URI excludePattern = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#excludePattern");

    /**
     * Label: flavour 
     * Comment: Some datasource have different flavours, for example the AddressbookDataSource has versions for Thunderbird, Mail.app, etc. This property specifies which one to use. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI flavour = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#flavour");

    /**
     * Label: hostname 
     * Comment: hostname of the server to connect to 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI hostname = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#hostname");

    /**
     * Label: includeEmbeddedResources 
     * Comment: Include embedded resources when crawling this datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI includeEmbeddedResources = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#includeEmbeddedResources");

    /**
     * Label: includeHiddenResources 
     * Comment: Include hidden resources when crawling this datasource 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI includeHiddenResources = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#includeHiddenResources");

    /**
     * Label: includeInbox 
     * Comment: if this is true always include the inbox when crawling - regardless of the basepath setting. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI includeInbox = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#includeInbox");

    /**
     * Label: includePattern 
     * Comment: patterns to include in this datasource 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://aperture.semanticdesktop.org/ontology/source#Pattern 
     */
    public static final URI includePattern = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#includePattern");

    /**
     * Label: maximumDepth 
     * Comment: The maximum depth to crawl to in this datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI maximumDepth = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#maximumDepth");

    /**
     * Label: maximumSize 
     * Comment: The maximum size (in bytes) for resources in this datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI maximumSize = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#maximumSize");

    /**
     * Label: name 
     * Comment: The readable name of the datasource 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI name = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#name");

    /**
     * Label: password 
     * Comment: The Password used to access this datasource. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI password = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#password");

    /**
     * Label: port 
     * Comment: port of the server to connect to 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI port = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#port");

    /**
     * Label: rootFolder 
     * Comment: Root-Folder in the local filesystem. On Windows machines something like "C:\myfiles\" on unix machines like "/Users/paul/" 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#FileSystemDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI rootFolder = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#rootFolder");

    /**
     * Label: rootUrl 
     * Comment: The root URL of this datasource. This may also be an indicator of how to create other URIs of this datasource 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI rootUrl = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#rootUrl");

    /**
     * Label: sslFileName 
     * Comment: The name of the file to be used for accepted SSL certificates. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI sslFileName = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#sslFileName");

    /**
     * Label: sslFilePassword 
     * Comment: The password used for encrypting the file with accepted ssl certificates. 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI sslFilePassword = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#sslFilePassword");

    /**
     * Label: timeout 
     * Comment: The timeout between two consecutive crawls (in miliseconds) 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#DataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI timeout = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#timeout");

    /**
     * Label: username 
     * Comment: http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource 
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal 
     */
    public static final URI username = URIImpl.createURIWithoutChecking("http://aperture.semanticdesktop.org/ontology/source#username");

}
