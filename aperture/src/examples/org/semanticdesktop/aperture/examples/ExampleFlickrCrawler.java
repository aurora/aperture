package org.semanticdesktop.aperture.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.websites.flickr.FlickrCrawler;
import org.semanticdesktop.aperture.websites.flickr.FlickrDataSource;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerHandler;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.NIE;

public class ExampleFlickrCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleFlickrCrawler.class.getName());

	private String username = null;
	
	public ExampleFlickrCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUsername(String user) {
		this.username = user;
	}
	
	public String getUsername() {
	    return username;
	}
	
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleFlickrCrawler crawler = new ExampleFlickrCrawler();
		List<String> remainingOptions = crawler.processCommonOptions(args);

		Iterator<String> iterator = remainingOptions.iterator();
		while (iterator.hasNext()) {
		    String arg = iterator.next();
		    if (crawler.getUsername() == null) {
		        crawler.setUsername(arg);
		    } else {
		        crawler.exitWithUsageMessage();
		    }
		}
		
        // start crawling and exit afterwards
        crawler.crawl();
    }

	private void crawl() throws Exception {
		 // create a data source configuration
        Model model = RDF2Go.getModelFactory().createModel();
        model.open();
        RDFContainer configuration = new RDFContainerImpl(model,new URIImpl("source:testSource"));

        // create the DataSource
        FlickrDataSource source = new FlickrDataSource();
        //set the Flickr Username
        source.setConfiguration(configuration);
        source.setUsername(username);
        
        // setup a crawler that can handle this type of DataSource
        FlickrCrawler crawler = new FlickrCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setAccessData(new AccessDataImpl());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();		
	}

    @Override
    protected String getSpecificExplanationPart() {
        return "  username     - your Flickr username";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "username";
    }
}
