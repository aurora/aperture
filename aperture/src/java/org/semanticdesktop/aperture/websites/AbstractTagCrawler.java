/**
 * 
 */
package org.semanticdesktop.aperture.websites;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.UpdateException;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


/**
 * @author grimnes
 *
 */
public abstract class AbstractTagCrawler extends CrawlerBase {
    
	Logger log=LoggerFactory.getLogger(AbstractTagCrawler.class);
	//Set<String> current;
	/** 
	 * @see org.semanticdesktop.aperture.crawler.base.CrawlerBase#crawlObjects()
	 */
	@Override
	@SuppressWarnings("unchecked")
    protected ExitCode crawlObjects() {
		DataSource localSource = getDataSource();
		RDFContainer configuration = localSource.getConfiguration();
		String username = ConfigurationUtil.getUsername(configuration);
		String password = ConfigurationUtil.getPassword(configuration);

		try {
		
			List<String> tags = crawlTags(username,password);
			
			//Set<String> before=accessData.getStoredIDs();
			//log.debug("Tags known before crawling:"+before);
			//current=new HashSet<String>();
			for (String t: tags) {
				URI turi=new URIImpl(t);
				//TODO: Can tags ever be changed, f.x. if new photos/bookmarks/whatever are added?
				if (accessData.isKnownId(t)) { 
				    reportUnmodifiedDataObject(t);
					//handler.objectNotModified(this,t);
				} else {
					accessData.put(t,AccessData.DATE_KEY,Long.toString(new Date().getTime()));
					//RDFContainer rdf=handler.getRDFContainerFactory(this,t).getRDFContainer(turi);
					RDFContainer rdf=getRDFContainerFactory(t).getRDFContainer(turi);
					DataObject o=new DataObjectBase(turi,localSource,rdf);
					rdf.add(RDF.type,NAO.Tag);
					
					// every tag is a root element
					rdf.add(NIE.rootElementOf,getDataSource().getID());
					//rdf.add(RDFS.LABEL,turi.getLocalName());
					rdf.add(RDFS.label,URLDecoder.decode(getShortName(turi.toString()),"utf-8"));
					//handler.objectNew(this,o);
					reportNewDataObject(o);
				}
				//current.add(t);
			}			
			
			crawlTheRest(username,password);
			
			//log.debug("Tags found this time: "+current);
			//report deleted tags
			//before.removeAll(current);
			//log.debug("Tags removed: "+before);
			//deprecatedUrls.removeAll(current);

		} catch (Exception e) {
			log.info("Could not crawl tag-datasource.",e);
			return ExitCode.FATAL_ERROR;
		} 
		
		

		// determine the exit code
		return stopRequested ? ExitCode.STOP_REQUESTED : ExitCode.COMPLETED;
	}
	

	/**
	 * crawl photos, etc
	 * return them to the crawlerhandler yourself
	 *
	 */
	protected void crawlTheRest(String username, String password) throws Exception
	{
		// override!
	}

	
	/**
	 * Report a new item to the crawlerhandler, this assumes items never change. 
	 * @param item
	 * @param tags
	 * @throws UnsupportedEncodingException 
	 * @throws UpdateException 
	 */
	protected void reportItem(Tag item, List<String> tags) throws UpdateException, UnsupportedEncodingException {
	    String uriString = item.getUri();
		//current.add(uriString);
		if (accessData.isKnownId(uriString)) {
			///handler.objectNotModified(this,uriString);
		    reportUnmodifiedDataObject(uriString);
		} else {
			accessData.put(uriString,AccessData.DATE_KEY,Long.toString(new Date().getTime()));
			URIImpl turi = new URIImpl(uriString);
			//RDFContainer rdf=handler.getRDFContainerFactory(this,uriString).getRDFContainer(turi);
			RDFContainer rdf= getRDFContainerFactory(uriString).getRDFContainer(turi);
			DataObject o=new DataObjectBase(turi,source,rdf);
			rdf.add(RDF.type,NAO.Tag);
			//rdf.add(RDFS.LABEL,item.getName());
			rdf.add(RDFS.label,URLDecoder.decode(item.getName(),"utf-8"));
			for (String tag: tags)
				rdf.add(NAO.hasTag,new URIImpl(tag));
			//handler.objectNew(this,o);
			reportNewDataObject(o);
		}
	}
	
	/** 
	 * Gets a list of the user's tags
	 * @param username
	 * @return a list of tags 
	 * @throws IOException
	 * @throws SAXException
	 * @throws SailUpdateException
	 * @throws ParserConfigurationException
	 */
	protected abstract List<String> crawlTags(String username, String password) throws Exception;

	/**
     * The passed uri identifies something on the web, probably a namespace. To
     * shorten this, parse the url for something like a localname. Returns the
     * last string after a '#' or a '/'.
     * 
     * @param uri
     *            a URI
     * @return a short name for it, for display.
     */
    private String getShortName(String uri) {
        if (uri.indexOf('#') > 0)
            uri = uri.substring(uri.lastIndexOf('#') + 1);
        else if (uri.indexOf('/') > 0)
            uri = uri.substring(uri.lastIndexOf('/') + 1);
        return uri;
    }
	
}
