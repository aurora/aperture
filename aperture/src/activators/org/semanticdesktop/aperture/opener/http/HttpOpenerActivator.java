/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.http;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;

public class HttpOpenerActivator implements BundleActivator {

	public static BundleContext bc;

	private DataOpenerFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		

		HttpOpenerActivator.bc = context;

		factory = new HttpOpenerFactory();
		ServiceRegistration registration = bc.registerService(DataOpenerFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
	}
}
