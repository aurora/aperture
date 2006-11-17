/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorRegistry;
import org.semanticdesktop.aperture.hypertext.linkextractor.impl.LinkExtractorRegistryImpl;

public class LinkextractorServiceActivator implements BundleActivator {

	public static BundleContext bc = null;
	
	private ServiceRegistration registration;
	
	private ServiceReference reference;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		bc = context;
		LinkExtractorRegistry registry = new LinkExtractorRegistryImpl();
		registration = bc.registerService(LinkExtractorRegistry.class.getName(), registry, new Hashtable());
		reference = registration.getReference();
		System.out.println("Service registered: " + LinkExtractorRegistry.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
		System.out.println("Service unregistered: " + LinkExtractorRegistry.class.getName());
		registration = null;
		reference = null;
		bc = null;
	}

}
