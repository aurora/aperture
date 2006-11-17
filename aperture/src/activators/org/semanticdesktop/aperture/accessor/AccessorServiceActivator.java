/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;


public class AccessorServiceActivator implements BundleActivator {

	public static BundleContext bc = null;
	
	private ServiceRegistration registration;
	
	private ServiceReference reference;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		bc = context;
		DataAccessorRegistry registry = new DataAccessorRegistryImpl();
		registration = bc.registerService(DataAccessorRegistry.class.getName(), registry, new Hashtable());
		reference = registration.getReference();
		System.out.println("Service registered: " + DataAccessorRegistry.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
		System.out.println("Service unregistered: " + DataAccessorRegistry.class.getName());
		registration = null;
		reference = null;
		bc = null;
	}
}

