/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.openrdf.repository.Repository;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.filesystem.FileSystemCrawler;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.filesystem.FileSystemDataSource;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

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

    public void crawl() {
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
        } catch (ModelException me) {
        	throw new RuntimeException(me);
        }
        RDF2GoRDFContainer configuration = new RDF2GoRDFContainer(model,URIImpl.createURIWithoutChecking("source:testSource"));
        ConfigurationUtil.setRootFolder(rootFile.getAbsolutePath(), configuration);

        // create the data source
        FileSystemDataSource source = new FileSystemDataSource();
        source.setConfiguration(configuration);
        
        CrawlerHandler handler = null;
        
        handler = new SimpleCrawlerHandler();
        
        // setup a crawler that can handle this type of DataSource
        FileSystemCrawler crawler = new FileSystemCrawler();
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setCrawlerHandler(handler);

        // start crawling
        crawler.crawl();
    }

    public static void main(String[] args) {
        // create a new ExampleFileCrawler instance
        ExampleFileCrawler crawler = new ExampleFileCrawler();
        
        // parse the command line options
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (IDENTIFY_MIME_TYPE_OPTION.equals(arg)) {
                crawler.setIdentifyingMimeType(true);
            }
            else if (EXTRACT_CONTENTS_OPTION.equals(arg)) {
                crawler.setExtractingContents(true);
            }
            else if (VERBOSE_OPTION.equals(arg)) {
                crawler.setVerbose(true);
            }
            else if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                exitWithUsageMessage();
            }
            else if (crawler.getRootFile() == null) {
                crawler.setRootFile(new File(arg));
            }
            else if (crawler.getRepositoryFile() == null) {
                crawler.setRepositoryFile(new File(arg));
            }
            else {
                exitWithUsageMessage();
            }
        }

        // check that all required fields are available
        if (crawler.getRootFile() == null || crawler.getRepositoryFile() == null) {
        	exitWithUsageMessage();
        }
        
        // start crawling and exit afterwards
        crawler.crawl();
    }

    private static void exitWithUsageMessage() {
        System.err.println("Usage: java " + ExampleFileCrawler.class.getName() + " [" + IDENTIFY_MIME_TYPE_OPTION
                + "] [" + EXTRACT_CONTENTS_OPTION + "] [" + VERBOSE_OPTION + "] rootDirectory repositoryFile");
        System.exit(-1);
    }
    
    private class SimpleCrawlerHandler implements CrawlerHandler, RDFContainerFactory {

    	protected Model model;
    	
    	protected int nrObjects;
    	
    	private MimeTypeIdentifier mimeTypeIdentifier;

        private ExtractorRegistry extractorRegistry;

        public SimpleCrawlerHandler() {
        	try {
            	model = new RepositoryModel(false);
            } catch (ModelException me) {
            	throw new RuntimeException(me);
            }
        	
            // create some identification and extraction components
            if (identifyingMimeType) {
                mimeTypeIdentifier = new MagicMimeTypeIdentifier();
            }
            if (extractingContents) {
                extractorRegistry = new DefaultExtractorRegistry();
            }
        }
        
        public void accessingObject(Crawler crawler, String url) {
            if (verbose) {
                System.out.println("Processing file " + nrObjects + ": " + url + "...");
            }
        }

        public void objectNew(Crawler dataCrawler, DataObject object) {
            nrObjects++;

            // process the contents on an InputStream, if available
            if (object instanceof FileDataObject) {
                try {
                    process((FileDataObject) object);
                }
                catch (IOException e) {
                    LOGGER.log(Level.WARNING, "IOException while processing " + object.getID(), e);
                }
                catch (ExtractorException e) {
                    LOGGER.log(Level.WARNING, "ExtractorException while processing " + object.getID(), e);
                }
            }
            
            object.dispose();
        }

        private void process(FileDataObject object) throws IOException, ExtractorException {
            // we cannot do anything when MIME type identification is disabled
            if (!identifyingMimeType) {
                return;
            }

            URI id = object.getID();

            // Create a buffer around the object's stream large enough to be able to reset the stream
            // after MIME type identification has taken place. Add some extra to the minimum array
            // length required by the MimeTypeIdentifier for safety.
            int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
            int bufferSize = Math.max(minimumArrayLength, 8192);
            BufferedInputStream buffer = new BufferedInputStream(object.getContent(), bufferSize);
            buffer.mark(minimumArrayLength + 10); // add some for safety

            // apply the MimeTypeIdentifier
            byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);
            String mimeType = mimeTypeIdentifier.identify(bytes, null, id);

            if (mimeType != null) {
                // add the mime type to the metadata
                RDFContainer metadata = object.getMetadata();
                metadata.add(DATA.mimeType, mimeType);

                // apply an Extractor if available
                if (extractingContents) {
                    buffer.reset();

                    Set extractors = extractorRegistry.get(mimeType);
                    if (!extractors.isEmpty()) {
                        ExtractorFactory factory = (ExtractorFactory) extractors.iterator().next();
                        Extractor extractor = factory.get();
                        extractor.extract(id, buffer, null, mimeType, metadata);
                    }
                }
            }
        }

		public void crawlStarted(Crawler crawler) {
			nrObjects = 0;
		}

		public void crawlStopped(Crawler crawler, ExitCode exitCode) {
		    try {
		        Writer writer = new BufferedWriter(new FileWriter(repositoryFile));
		        model.writeTo(writer,Syntax.RdfXml);
		        writer.close();
		
		        System.out.println("Crawled " + nrObjects + " objects (exit code: " + exitCode + ")");
		        System.out.println("Saved RDF model to " + repositoryFile);
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}

		public void objectChanged(Crawler dataCrawler, DataObject object) {
		    object.dispose();
		    printUnexpectedEventWarning("changed");
		}

		public void objectNotModified(Crawler crawler, String url) {
		    printUnexpectedEventWarning("unmodified");
		}

		public void objectRemoved(Crawler dataCrawler, String url) {
		    printUnexpectedEventWarning("removed");
		}

		public void clearStarted(Crawler crawler) {
		    printUnexpectedEventWarning("clearStarted");
		}

		public void clearingObject(Crawler crawler, String url) {
		    printUnexpectedEventWarning("clearingObject");
		}

		public void clearFinished(Crawler crawler, ExitCode exitCode) {
		    printUnexpectedEventWarning("clear finished");
		}

		public RDFContainerFactory getRDFContainerFactory(Crawler crawler, String url) {
		    return this;
		}

		public RDFContainer getRDFContainer(URI uri) {
			Model contextModel = null;
			try {
				contextModel = new RepositoryModel(uri, (Repository) model
						.getUnderlyingModelImplementation());
			}
			catch (ModelException me) {
				throw new RuntimeException(me);
			}
			RDF2GoRDFContainer container = new RDF2GoRDFContainer(contextModel, uri);
			return container;
		}

		protected void printUnexpectedEventWarning(String event) {
		     // as we don't keep track of access data in this example code, some events should never occur
		     LOGGER.warning("encountered unexpected event (" + event + ") with non-incremental crawler");
		}
    }
}
