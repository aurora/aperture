/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.osgi;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierRegistry;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

/**
 * Example class that crawls a file system and puts all extracted metadata in a repository.
 */
public class ExampleFileCrawler {

	private static final Logger LOGGER = Logger.getLogger(ExampleFileCrawler.class.getName());

	public static final String IDENTIFY_MIME_TYPE_OPTION = "-identifyMimeType";

	public static final String EXTRACT_CONTENTS_OPTION = "-extractContents";

	public static final String VERBOSE_OPTION = "-verbose";

	private File rootFile;

	private File repositoryFile;

	private boolean identifyingMimeType = false;

	private boolean extractingContents = false;

	private boolean verbose = false;
	
	private CrawlerRegistry crawlerRegistry;
	private DataAccessorRegistry accessorRegistry;
	private ExtractorRegistry extractorRegistry;
	private MimeTypeIdentifierRegistry mimeIdentifierRegistry;
    private DataSourceRegistry dataSourceRegistry;

	public ExampleFileCrawler(CrawlerRegistry crawlerRegistry, DataAccessorRegistry accessorRegistry,
			ExtractorRegistry extractorRegistry, MimeTypeIdentifierRegistry mimeIdentifierRegistry,
            DataSourceRegistry dataSourceRegistry) {
		this.accessorRegistry = accessorRegistry;
		this.crawlerRegistry = crawlerRegistry;
		this.extractorRegistry = extractorRegistry;
		this.mimeIdentifierRegistry = mimeIdentifierRegistry;
        this.dataSourceRegistry = dataSourceRegistry;
	}

	public boolean isExtractingContents() {
		return extractingContents;
	}

	public boolean isIdentifyingMimeType() {
		return identifyingMimeType;
	}

	public File getRepositoryFile() {
		return repositoryFile;
	}

	public File getRootFile() {
		return rootFile;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setExtractingContents(boolean extractingContents) {
		this.extractingContents = extractingContents;
	}

	public void setIdentifyingMimeType(boolean identifyingMimeType) {
		this.identifyingMimeType = identifyingMimeType;
	}

	public void setRepositoryFile(File repositoryFile) {
		this.repositoryFile = repositoryFile;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void crawl(String rootDir, String repoFile) {
		System.out.println("Trying to crawl the dir: " + rootDir);
		System.out.println("RDF will be saved to: " + repoFile);
		setRootFile(new File(rootDir));
		setRepositoryFile(new File(repoFile));

		if (rootFile == null) {
			throw new IllegalArgumentException("root file cannot be null");
		}
		if (repositoryFile == null) {
			throw new IllegalArgumentException("repository file cannot be null");
		}

		// create a data source configuration
		Model model = null;
		try {
			model = new RepositoryModel(false);
		}
		catch (ModelException me) {
			throw new RuntimeException(me);
		}
		RDF2GoRDFContainer configuration = new RDF2GoRDFContainer(model, URIImpl
				.createURIWithoutChecking("source:testSource"));
		ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);

		// create the data source
		DataSourceFactory sourceFactory = (DataSourceFactory)dataSourceRegistry.get(DATASOURCE.FileSystemDataSource).iterator().next();
		DataSource source = sourceFactory.newInstance();
		source.setConfiguration(configuration);

		CrawlerHandler handler = null;

		Iterator it = mimeIdentifierRegistry.getAll().iterator();
		MimeTypeIdentifierFactory mimeIdentifierFactory = (MimeTypeIdentifierFactory) it.next();
		MimeTypeIdentifier mimeIdentifier = mimeIdentifierFactory.get();
		
		handler = new SimpleCrawlerHandler(mimeIdentifier,extractorRegistry,repositoryFile);
		
		// setup a crawler that can handle this type of DataSource
		it = crawlerRegistry.get(DATASOURCE.FileSystemDataSource).iterator();
		Crawler crawler = ((CrawlerFactory)it.next()).getCrawler(source);
		crawler.setDataAccessorRegistry(accessorRegistry);
		crawler.setCrawlerHandler(handler);

		// start crawling
		crawler.crawl();
	}

	private static void exitWithUsageMessage() {
		System.err.println("Usage: java " + ExampleFileCrawler.class.getName() + " ["
				+ IDENTIFY_MIME_TYPE_OPTION + "] [" + EXTRACT_CONTENTS_OPTION + "] [" + VERBOSE_OPTION
				+ "] rootDirectory repositoryFile");
		System.exit(-1);
	}

}
