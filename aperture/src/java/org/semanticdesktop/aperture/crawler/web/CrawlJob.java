/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.web;

/**
 * A CrawlJob is used to queue a request for retrieving the content of a URL.
 * 
 * <p>
 * Implementation note: Strings are used to model URLs, rather than java.net.URL, in order to allow the
 * use of schemes other than http(s) and file without requiring registering of a URLStreamHandler for
 * each scheme.
 */
public class CrawlJob {

    private String url;

    private int depth;

    /**
     * Schedule a URL for crawling. The depth indicates how deep the hypertext graph needs to be crawler.
     * A depth of 0 indicates that only this url needs to be crawled, 1 indicates that all directly
     * linked URLs also need to be crawled, etc. Use a negative value to indicate that the graph needs to
     * be crawled exhaustively.
     * 
     * @param url The URL to crawl.
     * @param depth The number of hops to crawl, starting from this URL, or a negative value to indicate
     *            that their is no depth limit.
     */
    public CrawlJob(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getURL() {
        return url;
    }

    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public String toString() {
    	return "{" + url + "," + depth + "}";
    }
}
