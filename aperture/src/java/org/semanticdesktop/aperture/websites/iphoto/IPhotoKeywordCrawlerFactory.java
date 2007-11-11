/**
 Gnowsis License 1.0

 Copyright (c) 2004, Leo Sauermann & DFKI German Research Center for Artificial Intelligence GmbH
 All rights reserved.

 This license is compatible with the BSD license http://www.opensource.org/licenses/bsd-license.php

 Redistribution and use in source and binary forms, 
 with or without modification, are permitted provided 
 that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the DFKI nor the names of its contributors 
 may be used to endorse or promote products derived from this software 
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 endOfLic**/
/**
 * 
 */
package org.semanticdesktop.aperture.websites.iphoto;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * 
 * @author grimnes
 */
public class IPhotoKeywordCrawlerFactory implements CrawlerFactory {
	
	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
	 */
	@SuppressWarnings("unchecked")
    public Set getSupportedTypes() {
		return Collections.singleton(IPHOTODS.IPhotoKeywordDataSource);
	}

	/**
	 * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
	 */
	public Crawler getCrawler(DataSource dataSource) {
	    return new IPhotoKeywordCrawler(dataSource);
	}

}
