package org.semanticdesktop.aperture.websites.delicious;

import java.util.Collections;
import java.util.Set;

import org.semanticdesktop.aperture.crawler.Crawler;
import org.semanticdesktop.aperture.crawler.CrawlerFactory;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A factory of del.icio.us crawlers
 */
public class DeliciousCrawlerFactory implements CrawlerFactory {

    /**
     * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getSupportedTypes()
     */
    @SuppressWarnings("unchecked")
    public Set getSupportedTypes() {
        return Collections.singleton(DELICIOUSDS.DeliciousDataSource);
    }

    /**
     * @see org.semanticdesktop.aperture.crawler.CrawlerFactory#getCrawler(org.semanticdesktop.aperture.datasource.DataSource)
     */
    public Crawler getCrawler(DataSource dataSource) {
        return new DeliciousCrawler(dataSource);

    }
}
