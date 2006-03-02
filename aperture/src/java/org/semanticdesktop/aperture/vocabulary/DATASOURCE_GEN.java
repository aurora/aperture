package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Tue Feb 28 13:50:28 CET 2006
 * input file: doc/ontology/source.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/source#
 */
public class DATASOURCE_GEN {
	public static final String NS = "http://aperture.semanticdesktop.org/ontology/source#";

    /**
     * Label: DataSource 
     */
    public static final URI DataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#DataSource");

    /**
     * Label: DataSourceDescription 
     * Comment: A human description of a datasource. Adds icons, texts etc. 
     */
    public static final URI DataSourceDescription = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#DataSourceDescription");

    /**
     * Label: FileSystemDataSource 
     */
    public static final URI FileSystemDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#FileSystemDataSource");

    /**
     * Label: IMAPDataSource 
     */
    public static final URI IMAPDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#IMAPDataSource");

    /**
     * Label: MicrosoftOutlookDataSource 
     */
    public static final URI MicrosoftOutlookDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#MicrosoftOutlookDataSource");

    /**
     * Label: Pattern 
     */
    public static final URI Pattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#Pattern");

    /**
     * Label: RegExpPattern 
     */
    public static final URI RegExpPattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#RegExpPattern");

    /**
     * Label: SubstringPattern 
     */
    public static final URI SubstringPattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#SubstringPattern");

    /**
     * Label: WebDataSource 
     */
    public static final URI WebDataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#WebDataSource");

    /**
     * Label: basepath 
     * Comment: basepath on the server. For IMAP this is the folder where the e-mails are stored. Some IMAP servers need this. 
     * Domain: IMAPDataSource 
     * Range: Literal 
     */
    public static final URI basepath = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#basepath");

    /**
     * Label: condition 
     * Comment: the condition for this pattern to match 
     * Domain: Pattern 
     * Range: Literal 
     */
    public static final URI condition = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#condition");

    /**
     * Label: connectionSecurity 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI connectionSecurity = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#connectionSecurity");

    /**
     * Label: dataSourceComment 
     * Comment: A comment about the datasource. 
     * Domain: DataSourceDescription 
     * Range: Literal 
     */
    public static final URI dataSourceComment = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#dataSourceComment");

    /**
     * Label: dataSourceName 
     * Comment: Name of the type of datasource. For example "Local File System". 
     * Domain: DataSourceDescription 
     * Range: Literal 
     */
    public static final URI dataSourceName = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#dataSourceName");

    /**
     * Label: describedDataSourceType 
     * Comment: The datasource described 
     * Domain: DataSourceDescription 
     * Range: Class 
     */
    public static final URI describedDataSourceType = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#describedDataSourceType");

    /**
     * Label: excludePattern 
     * Comment: Patterns to exclude from this datasource 
     * Domain: DataSource 
     * Range: Pattern 
     */
    public static final URI excludePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#excludePattern");

    /**
     * Label: hostname 
     * Comment: hostname of the server to connect to 
     * Domain: IMAPDataSource 
     * Range: Literal 
     */
    public static final URI hostname = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#hostname");

    /**
     * Label: includeEmbeddedResources 
     * Comment: Include embedded resources when crawling this datasource. 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI includeEmbeddedResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includeEmbeddedResources");

    /**
     * Label: includeHiddenResources 
     * Comment: Include hidden resources when crawling this datasource 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI includeHiddenResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includeHiddenResources");

    /**
     * Label: includePattern 
     * Comment: patterns to include in this datasource 
     * Domain: DataSource 
     * Range: Pattern 
     */
    public static final URI includePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includePattern");

    /**
     * Label: maximumDepth 
     * Comment: The maximum depth to crawl to in this datasource. 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#maximumDepth");

    /**
     * Label: maximumSize 
     * Comment: The maximum size (in bytes) for resources in this datasource. 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#maximumSize");

    /**
     * Label: name 
     * Comment: The readable name of the datasource 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI name = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#name");

    /**
     * Label: password 
     * Comment: The Password used to access this datasource. 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI password = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#password");

    /**
     * Label: rootFolder 
     * Comment: Root-Folder in the local filesystem. On Windows machines something like "C:\myfiles\" on unix machines like "/Users/paul/" 
     * Domain: FileSystemDataSource 
     * Range: Literal 
     */
    public static final URI rootFolder = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#rootFolder");

    /**
     * Label: rootUrl 
     * Comment: The root URL of this datasource. This may also be an indicator of how to create other URIs of this datasource 
     * Domain: DataSource 
     * Range: Literal 
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#rootUrl");

    /**
     * Label: username 
     * Domain: IMAPDataSource 
     * Range: Literal 
     */
    public static final URI username = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#username");

}
