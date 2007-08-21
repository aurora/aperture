package org.semanticdesktop.aperture.websites.delicious;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.websites.AbstractTagCrawler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	
	/**
	 * @param dataSource
	 */
	public DeliciousCrawler(DataSource dataSource) {
		super();
		setDataSource(dataSource);
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



}
