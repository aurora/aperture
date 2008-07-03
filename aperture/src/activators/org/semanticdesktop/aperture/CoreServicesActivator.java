/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.accessor.AccessorServiceActivator;
import org.semanticdesktop.aperture.crawler.CrawlerServiceActivator;
import org.semanticdesktop.aperture.datasource.DataSourceServiceActivator;
import org.semanticdesktop.aperture.detector.DataSourceDetectorServiceActivator;
import org.semanticdesktop.aperture.extractor.ExtractorServiceActivator;
import org.semanticdesktop.aperture.opener.DataOpenerServiceActivator;
import org.semanticdesktop.aperture.rdf.RDFBundleActivator;

/**
 * An activator for the core services of aperture
 */
public class CoreServicesActivator implements BundleActivator {

	private static BundleContext bc;

	private AccessorServiceActivator accessorServiceActivator;

	private CrawlerServiceActivator crawlerServiceActivator;

	private DataSourceServiceActivator dataSourceServiceActivator;

	private ExtractorServiceActivator extractorServiceActivator;

	private DataOpenerServiceActivator dataOpenerServiceActivator;

	private RDFBundleActivator rdfBundleActivator;

    private DataSourceDetectorServiceActivator detectorServiceActivator;

	public void start(BundleContext context) throws Exception {
		accessorServiceActivator = new AccessorServiceActivator();
		accessorServiceActivator.start(context);
		
		crawlerServiceActivator = new CrawlerServiceActivator();
		crawlerServiceActivator.start(context);
		
		dataSourceServiceActivator = new DataSourceServiceActivator();
		dataSourceServiceActivator.start(context);
		
		extractorServiceActivator = new ExtractorServiceActivator();
		extractorServiceActivator.start(context);
		
		dataOpenerServiceActivator = new DataOpenerServiceActivator();
		dataOpenerServiceActivator.start(context);
		
		rdfBundleActivator = new RDFBundleActivator();
		rdfBundleActivator.start(context);
		
		detectorServiceActivator = new DataSourceDetectorServiceActivator();
		detectorServiceActivator.start(context);
		
		bc = context;
	}

	public void stop(BundleContext context) throws Exception {
		accessorServiceActivator.stop(context);
		accessorServiceActivator = null;
		
		crawlerServiceActivator.stop(context);
		crawlerServiceActivator = null;
		
		dataSourceServiceActivator.stop(context);
		dataSourceServiceActivator = null;
		
		extractorServiceActivator.stop(context);
		extractorServiceActivator = null;
		
		dataOpenerServiceActivator.stop(context);
		dataOpenerServiceActivator = null;
		
		rdfBundleActivator.stop(context);
		rdfBundleActivator = null;
		
		detectorServiceActivator.stop(context);
		detectorServiceActivator = null;
		
		bc = null;
	}
}
