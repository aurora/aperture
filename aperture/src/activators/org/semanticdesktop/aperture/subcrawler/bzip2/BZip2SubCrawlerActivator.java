/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.bzip2;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;

public class BZip2SubCrawlerActivator implements BundleActivator {

	public static BundleContext bc;

	private SubCrawlerFactory factory;

    private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		BZip2SubCrawlerActivator.bc = context;
		factory = new BZip2SubCrawlerFactory();
		registration = bc.registerService(SubCrawlerFactory.class.getName(), factory,new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
