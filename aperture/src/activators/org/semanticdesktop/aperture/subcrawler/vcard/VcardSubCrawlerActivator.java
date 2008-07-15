/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.vcard;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;
import org.semanticdesktop.aperture.subcrawler.vcard.VcardSubCrawlerFactory;

public class VcardSubCrawlerActivator implements BundleActivator {

	public static BundleContext bc;

	private SubCrawlerFactory factory;

    private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		VcardSubCrawlerActivator.bc = context;
		factory = new VcardSubCrawlerFactory();
		registration = bc.registerService(SubCrawlerFactory.class.getName(), factory,new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
