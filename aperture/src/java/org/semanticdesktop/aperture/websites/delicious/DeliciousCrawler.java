package org.semanticdesktop.aperture.websites.delicious;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.FilterAccessData;
import org.semanticdesktop.aperture.crawler.web.CrawlJob;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NAO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.websites.AbstractTagCrawler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dfki.util.xml.XML;
import de.dfki.util.xml.XML.DOM;

/**
 * A del.icio.us crawlers
 */
public class DeliciousCrawler extends AbstractTagCrawler {

    /** URL to the tags API */
	public static final String TAGS_API="del.icio.us/api/tags/get";
	/** URL to the posts API */
	public static final String POSTS_API="del.icio.us/api/posts/all";
	
	private WebAccessData wad= null;
    private LinkedList<CrawlJob> jobsQueue;
    private HashMap<String, CrawlJob> jobsMap;
	
	/**
	 * @param dataSource
	 */
	public DeliciousCrawler(DataSource dataSource) {
		super();
		setDataSource(dataSource);
		
		  // initialize variables
        jobsQueue = new LinkedList<CrawlJob>();
        jobsMap = new HashMap<String, CrawlJob>(1024);
       
        
	}

	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.websites.TagCrawler#crawlTags(java.lang.String)
	 */
	@Override
	protected List<String> crawlTags(String username, String password) throws java.lang.Exception {
		
		//String url="http://"+username+":"+password+"@"+TAGS_API;
		String url="http://"+TAGS_API;
		System.out.println(url);
		List<String> res=new Vector<String>();

		Credentials c=new UsernamePasswordCredentials(username,password);
		HttpClient hc=new HttpClient();
		hc.getState().setCredentials(AuthScope.ANY,c);
		
		HttpMethod method=new GetMethod(url);
		method.setDoAuthentication(true);
		
		int http_code=hc.executeMethod(method);
		if (http_code!=HttpURLConnection.HTTP_OK) {
			throw new Exception("HTTP Method did not return OK for url '"+url+"' code: "+http_code);
		}
		
		DOM dom = XML.dom();
		Document d = dom.readFromInputStream(method.getResponseBodyAsStream());
		NodeList l = d.getElementsByTagName("tag");
		int i=0;
		while(l.item(i)!=null) {
			Element e=(Element)l.item(i);
			res.add("http://del.icio.us/"+username+"/"+e.getAttribute("tag"));
			i++;
		}
		
		return res;
		
	}
	/**
	 * Crawl POSTS from del.icio.us
	 * @throws Exception 
	 * @throws SAXException 
	 */
	@Override
	protected void crawlTheRest(String username, String pass) throws SAXException, Exception{
	  
        String url="http://"+POSTS_API;
        System.out.println(url);
        DataSource localSource = getDataSource();
        
        Credentials c=new UsernamePasswordCredentials(username,pass);
        HttpClient hc=new HttpClient();
        hc.getState().setCredentials(AuthScope.ANY,c);
        
        HttpMethod method=new GetMethod(url);
        method.setDoAuthentication(true);
        
        int http_code=hc.executeMethod(method);
        if (http_code!=HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP Method did not return OK for url '"+url+"' code: "+http_code);
        }
        
        DOM dom = XML.dom();
        Document d = dom.readFromInputStream(method.getResponseBodyAsStream());
        
        
        NodeList l = d.getElementsByTagName("post");
        
        int i=0;
        while(l.item(i)!=null) {
            Element e=(Element)l.item(i);
            // posts.add("http://del.icio.us/"+username+"/"+e.getAttribute("href"));
            String postHref = e.getAttribute("href");
            
            URI postURI = new URIImpl(postHref);
            
            // add website
            if(accessData.isKnownId(postHref)){
                reportUnmodifiedDataObject(postHref);
            }else{
                accessData.put(postHref, AccessData.DATE_KEY, Long.toString(new Date().getTime()));
                RDFContainer rdf = getRDFContainerFactory(postHref).getRDFContainer(postURI);
              
                
                DataAccessor accessor = getDataAccessor(postHref);
                RDFContainerFactory containerFactory = getRDFContainerFactory(postHref);
                //wad = new WebAccessData(accessData);
                DataObject o = accessor.getDataObject(postHref, source, null, containerFactory);
                
              
                
                rdf.add(RDF.type, NFO.Website);
              
                //add hasTag
               
                String tags = e.getAttribute("tag");
                for(String tag : tags.split(" ")){
                    rdf.add(NAO.hasTag,new URIImpl("http://del.icio.us/"+username+"/"+tag));
                };
                reportNewDataObject(o);
            }
            
            i++;
        }
 
            
	}
	
	 private DataAccessor getDataAccessor(String url) {
	        // determine the scheme
	        int index = url.indexOf(':');
	        if (index <= 0) {
	            return null;
	        }
	        String scheme = url.substring(0, index);

	        // fetch a DataAccessor for this scheme
	        Set factories = accessorRegistry.get(scheme);
	        if (factories.isEmpty()) {
	            return null;
	        }
	        DataAccessorFactory factory = (DataAccessorFactory) factories.iterator().next();
	        return factory.get();
	    }
	 
	 private class WebAccessData extends FilterAccessData {

	        public WebAccessData(AccessData accessData) {
	            super(accessData);
	        }

	        public void put(String id, String key, String value) {
	            // Make sure the original URL is not accessed anymore. We need to do this using such a complicated
	            // approach (using a wrapped AccessData instance) because there may be several redirection steps
	            // between the URL passed to the DataAccessor and the URL of the final DataObject.
	            if (REDIRECTS_TO_KEY.equals(key)) {
	                // do this with the id rather than the value: processingQueue depends on this in order to be
	                // able to do a crawledUrls.contains on the last URL in the redirection chain
	                //crawledUrls.add(id); // this is obviously not needed, if we use AccessData, then crawledUrls is null
	                //deprecatedUrls.remove(id);
	                touch(id);

	                CrawlJob job = jobsMap.remove(id);
	                if (job != null) {
	                    jobsQueue.remove(job);
	                }
	            }

	            super.put(id, key, value);
	        }
	    }
}
