package org.semanticdesktop.aperture.examples;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
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
import org.semanticdesktop.aperture.tagcrawlers.AbstractTagCrawler;
import org.semanticdesktop.aperture.tagcrawlers.bibsonomy.BibsonomyCrawler;
import org.semanticdesktop.aperture.tagcrawlers.bibsonomy.BibsonomyDataSource;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.TAGGING;

public class ExampleBibsonomyCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleBibsonomyCrawler.class.getName());

	private String username = null;
	
	public ExampleBibsonomyCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUserName(String user) {
		this.username = user;
	}
	
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleBibsonomyCrawler crawler = new ExampleBibsonomyCrawler();
		List<String> remainingOptions = crawler.processCommonOptions(args);
		
		if (remainingOptions.size() == 1) {
		    crawler.setUserName(remainingOptions.get(0));
		} else {
		    crawler.exitWithUsageMessage();
		}
		
        // start crawling and exit afterwards
        crawler.crawl();
    }

	private void crawl() {
		 // create a data source configuration
	    Model model = RDF2Go.getModelFactory().createModel();
	    model.open();
	    RDFContainer configuration = new RDFContainerImpl(model, new URIImpl("source:testSource"));
        
        // create the DataSource
        BibsonomyDataSource source = new BibsonomyDataSource();
        //set the Bibsonomy Username
        source.setConfiguration(configuration);
        source.setUsername(username);
        source.setCrawlType(BibsonomyDataSource.CrawlType.ItemsAndTagsCrawlType);
        
        
        // setup a crawler that can handle this type of DataSource
        BibsonomyCrawler crawler = new BibsonomyCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setAccessData(new AccessDataImpl());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();
	}

    @Override
    protected String getSpecificExplanationPart() {
        return "    username - your bibsonomy user name";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "username";
        
    }
}
