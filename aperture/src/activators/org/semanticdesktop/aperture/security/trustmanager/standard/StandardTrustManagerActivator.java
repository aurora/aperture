/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.security.trustmanager.standard;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.security.trustmanager.TrustManagerFactory;

public class StandardTrustManagerActivator implements BundleActivator {

	public static BundleContext bc;

	private TrustManagerFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		

		StandardTrustManagerActivator.bc = context;

		factory = new StandardTrustManagerFactory();
		ServiceRegistration registration = bc.registerService(TrustManagerFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
	}

}
