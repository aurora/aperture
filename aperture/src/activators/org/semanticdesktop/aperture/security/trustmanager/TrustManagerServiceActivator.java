/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.opener.DataOpenerRegistry;
import org.semanticdesktop.aperture.security.trustmanager.impl.TrustManagerRegistryImpl;

public class TrustManagerServiceActivator implements BundleActivator {

	public static BundleContext bc = null;
	
	private ServiceRegistration registration;
	
	private ServiceReference reference;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		bc = context;
		TrustManagerRegistry registry = new TrustManagerRegistryImpl();
		registration = bc.registerService(TrustManagerRegistry.class.getName(), registry, new Hashtable());
		reference = registration.getReference();
		System.out.println("Service registered: " + TrustManagerRegistry.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
		System.out.println("Service unregistered: " + TrustManagerRegistry.class.getName());
		registration = null;
		reference = null;
		bc = null;
	}
}
