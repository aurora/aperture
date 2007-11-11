package org.semanticdesktop.aperture.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
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
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.websites.delicious.DeliciousCrawler;
import org.semanticdesktop.aperture.websites.delicious.DeliciousDataSource;


public class ExampleDeliciousCrawler extends AbstractExampleCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(ExampleDeliciousCrawler.class.getName());

	private String username = null;
	private String password;
	
	public ExampleDeliciousCrawler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setUserName(String user) {
		this.username = user;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public String getUserName() {
	    return username;
	}
	
	public String getPassword() {
	    return password;
	}
	
	public static void main(String[] args) throws Exception {
        // create a new ExampleWebCrawler instance
		ExampleDeliciousCrawler crawler = new ExampleDeliciousCrawler();
		
		List<String> remainingOptions = crawler.processCommonOptions(args);
		
		Iterator<String> iterator = remainingOptions.iterator();
		
		while (iterator.hasNext()) {
		    String arg = iterator.next();
		    if (arg.startsWith("-")) {
                System.err.println("Unknown option: " + arg);
                crawler.exitWithUsageMessage();
            } else if (crawler.getUserName() == null) {
                crawler.setUserName(arg);
            } else if (crawler.getPassword() == null) {
                crawler.setPassword(arg);
            }
            else {
                crawler.exitWithUsageMessage();
            }
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
        DeliciousDataSource source = new DeliciousDataSource();
        source.setConfiguration(configuration);
        //set the Flickr Username
        source.setUsername(username);
        source.setPassword(password);
        
        // setup a crawler that can handle this type of DataSource
        DeliciousCrawler crawler = new DeliciousCrawler(source);
        crawler.setDataSource(source);
        crawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
        crawler.setAccessData(new AccessDataImpl());
        crawler.setCrawlerHandler(getHandler());

        // start crawling
        crawler.crawl();
	}

    @Override
    protected String getSpecificExplanationPart() {
        return "   username - your del.icio.us user name\n" +
        "   password - your del.icio.us password";
    }

    @Override
    protected String getSpecificSyntaxPart() {
        return "username password";
    }
}
