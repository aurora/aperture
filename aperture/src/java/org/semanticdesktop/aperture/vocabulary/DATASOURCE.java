package org.semanticdesktop.aperture.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Sun Feb 26 15:35:00 CET 2006
 * input file: doc/ontology/source.rdfs
 * namespace: http://aperture.semanticdesktop.org/ontology/source#
 */
public class DATASOURCE {
	public static final String NS = "http://aperture.semanticdesktop.org/ontology/source#";

    /**
     * Label: DataSource 
     */
    public static final URI DataSource = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#DataSource");

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
     * Label: condition 
     * Comment: the condition for this pattern to match 
     */
    public static final URI condition = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#condition");

    /**
     * Label: connectionSecurity 
     */
    public static final URI connectionSecurity = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#connectionSecurity");

    /**
     * Label: excludePattern 
     * Comment: Patterns to exclude from this datasource 
     */
    public static final URI excludePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#excludePattern");

    /**
     * Label: includeEmbeddedResources 
     * Comment: Include embedded resources when crawling this datasource. 
     */
    public static final URI includeEmbeddedResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includeEmbeddedResources");

    /**
     * Label: includeHiddenResources 
     * Comment: Include hidden resources when crawling this datasource 
     */
    public static final URI includeHiddenResources = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includeHiddenResources");

    /**
     * Label: includePattern 
     * Comment: patterns to include in this datasource 
     */
    public static final URI includePattern = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#includePattern");

    /**
     * Label: maximumDepth 
     * Comment: The maximum depth to crawl to in this datasource. 
     */
    public static final URI maximumDepth = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#maximumDepth");

    /**
     * Label: maximumSize 
     * Comment: The maximum size (in bytes) for resources in this datasource. 
     */
    public static final URI maximumSize = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#maximumSize");

    /**
     * Label: password 
     * Comment: The Password used to access this datasource. 
     */
    public static final URI password = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#password");

    /**
     * Label: rootUrl 
     * Comment: The root URL of this datasource. 
     */
    public static final URI rootUrl = new URIImpl("http://aperture.semanticdesktop.org/ontology/source#rootUrl");

}
