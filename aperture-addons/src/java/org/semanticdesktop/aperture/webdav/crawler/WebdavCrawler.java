package org.semanticdesktop.aperture.webdav.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.webdav.lib.WebdavResource;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.crawler.base.CrawlerBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.web.WebDataSource;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 *
 * Licensed under the Open Software License version 3.0..
 */

/**
 * @author Patrick Ernst
 * <p>
 * <b>A crawler for WebDav directories</b>
 * <p>
 * For crawling a webdav hierarchy you need the host of the webDav server and 
 * the {@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#setPath path} to the resources you'd like to crawl. 
 * Furthermore you can specify a {@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#setPassword password} 
 * and a {@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#setUsername username} to access the desired resources.
 * <p>
 * <b>Notes:</b>
 * <ul>
 * <li>The host address of the webDAV server has to end with / . </li>
 * <li>This implementation is very similar to the existing FileSystemCrawler class </li>
 * </ul>
 * 
 */
public class WebdavCrawler extends CrawlerBase {
	
	/** The password. */
	private String password;
	
	/** The username. */
	private String username;
	
	/** The maximum size. */
	private long maximumSize;
	
	/** To ignore hidden files. */
	private boolean ignoreHiddenFiles;
	
	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/** The accessor factory. */
	private DataAccessorFactory accessorFactory;
	
	/** The host of the webdav server used for authentication */
	private WebDataSource source;
	
	/** The crawler's starting point */
	private WebdavResource root;
	
	/** The path to the desired resources/files */
	private String path;
	
	/** The params. */
	private HashMap params;

	
	
	
	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}


	/**
	 * Sets the path.
	 * 
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}


	/**
	 * Sets the password.
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * Sets the username.
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/* (non-Javadoc)
	 * @see org.semanticdesktop.aperture.crawler.base.CrawlerBase#crawlObjects()
	 */
	protected ExitCode crawlObjects() {
		DataSource dataSource = getDataSource();
		if(!(dataSource instanceof WebDataSource)){
			logger.error("wrong data source type");
			return ExitCode.FATAL_ERROR;
		}
		source = (WebDataSource)dataSource;
		try {
			URL url = new URL(source.getRootUrl());
			if(url.getProtocol().toLowerCase().equals("http")){
				HttpURL hrl = new HttpURL(url.toString());
				if(username.trim().length() != 0){
					hrl.setUser(username);
					hrl.setPassword(password);
				}
				root = new WebdavResource(hrl);
				//sets the path relative to root
				root.setPath(root.getPath() + path);
			} else if(url.getProtocol().toLowerCase().equals("https")){
				HttpsURL hrl = new HttpsURL(url.toString());
				if(username.trim().length() != 0){
					hrl.setUser(username);
					hrl.setPassword(password);
				}
				root = new WebdavResource(hrl);
				//sets the path relative to root
				root.setPath(root.getPath() + path);
			} else {
				logger.error("Unknown Protocol");
				return ExitCode.FATAL_ERROR;
			}
		} catch (Exception e) {
			logger.error("WebdavResource Accessfailure");
			e.printStackTrace();
			return ExitCode.FATAL_ERROR;
		} 
		

       
		Integer i = source.getMaximumDepth();
		int maxdepth = i == null ? Integer.MAX_VALUE : i.intValue();
		
		Long l = source.getMaximumSize();
		maximumSize = l == null ? Long.MAX_VALUE : l.longValue();
		
		params = new HashMap(2);
		getAccessorFactory();
		
		
		boolean crawlCompleted = crawlWebDavHierarchy(root, maxdepth);
		
		params = null;
		
		
		//close the connection
		try {
			root.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return crawlCompleted ? ExitCode.COMPLETED : ExitCode.STOP_REQUESTED;
	}
	
	
	/**
	 * Gets the accessor factory.
	 * 
	 * @return the accessor factory
	 */
	private void getAccessorFactory(){
		if(accessorRegistry == null){
			throw new IllegalStateException("DataAccessorRegistry not set");
		}
		
		Set factories = accessorRegistry.get("webdavFile");
		
		if(factories != null && !factories.isEmpty()){
			accessorFactory = (DataAccessorFactory) factories.iterator().next();
		} else {
			throw new IllegalStateException("Could not retrieve a webdav File data accessor");
		}
	}

	/**
	 * Crawls a webdav hierarchy starting at a root resource. 
	 * Checks if the resource is a file or directory and calls the adequate function.
	 * 
	 * <ul>
	 * <li>{@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#iterateOverWebdavResourceContent  iterateOverWebdavResourceContent} if it's a directory </li>
	 * <li>{@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#crawlWebdavResourceSingleFile  crawlWebdavResourceSingleFile} if it's a file </li>
	 * </ul>
	 * 
	 * @param webdavResource the webdav resource
	 * @param depth the depth
	 * 
	 * @return true, if the path was completely crawled
	 */
	private boolean crawlWebDavHierarchy(WebdavResource webdavResource, int depth){
		
		if(!webdavResource.isCollection() && depth >= 0){
			if(inDomain(webdavResource.getHttpURL().toString()) && !webdavResource.isLocked()){
				crawlWebdavResourceSingleFile(webdavResource);
			} 
			else {
				logger.info("Resource " + webdavResource.toString() + " is not in domain. Skipping.");
			}
			return true;
		} else if (webdavResource.isCollection() && depth >= 0){
			
			//workaround for an error, that occurs if a path to a folder don't end with /
			if(!webdavResource.getPath().endsWith("/")){
				try {
					webdavResource.setPath(webdavResource.getPath() + "/");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//report the Folder
			if(inDomain(webdavResource.getHttpURL().toString())){
				crawlWebdavResourceSingleFile(webdavResource);
			}
			if(depth > 0 && inDomain(webdavResource.getHttpURL().toString())){
				return this.iterateOverWebdavResourceContent(webdavResource, depth);
			} else {
				return true;
			}
		}
		else {
			return true;
		}
	}
	
	/**
	 * Iterates over a webdav folder by determining all subfolders and files. 
	 * For every located resource the function {@link  org.semanticdesktop.aperture.webdav.crawler.WebdavCrawler#crawlWebDavHierarchy  crawlWebDavHierarchy} 
	 * will be called to check if it's a file or directory.
	 * 
	 * @param webdavResource the webdav folder
	 * @param depth the depth
	 * 
	 * @return true, if successful
	 */
	private boolean iterateOverWebdavResourceContent(WebdavResource webdavResource, int depth){
		WebdavResource[] resources = null;
		try {
			resources = webdavResource.listWebdavResources();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resources == null){
			return true;
		}
		
		
		int i = 0;
		for(; !stopRequested && i < resources.length;i++){
			WebdavResource nestedResource = resources[i];
			
			if(ignoreHiddenFiles && nestedResource.getIsHidden()){
				try {
					nestedResource.close();
					resources[i] = null;
				} catch (IOException e) {	
					e.printStackTrace();
					continue;
				}
				continue;
			}
			
			boolean scanCompleted = crawlWebDavHierarchy(nestedResource, depth - 1);
			
			try {
				nestedResource.close();
				resources[i] = null;
			} catch (IOException e) {	
				e.printStackTrace();
				continue;
			}
			if(!scanCompleted){
				return false;
			}
		}
		
		return i == resources.length;
	}
	
	/**
	 * Crawls a single webdav file and reports it to the registered DataSourceListeners.
	 * 
	 * @param webdavResource the webdav file
	 */
	private void crawlWebdavResourceSingleFile(WebdavResource webdavResource){
		String url = webdavResource.getHttpURL().toString();
		reportAccessingObject(url);
		
		boolean knownObject = accessData == null ? false : accessData.isKnownId(url);
		
		RDFContainerFactory containerFactory = getRDFContainerFactory(url);
		
		DataAccessor accessor = accessorFactory.get();
		
		params.put("webdavFile", webdavResource);
		
		try{
			DataObject dataObject = accessor.getDataObjectIfModified(url, source, accessData, params, containerFactory);

			if(dataObject == null){
				reportUnmodifiedDataObject(url);
			} else {
				if(webdavResource.equals(root)){
					dataObject.getMetadata().add(NIE.rootElementOf, source.getID());

				}
				
				if(knownObject){
					reportModifiedDataObject(dataObject);
				} else {
					reportNewDataObject(dataObject);
				}
			}
		} catch(UrlNotFoundException e){
			logger.warn("unable to access " + url, e);
		} catch (IOException e){
			logger.warn("I/O error while processing " + url, e);
		}
	}

}
