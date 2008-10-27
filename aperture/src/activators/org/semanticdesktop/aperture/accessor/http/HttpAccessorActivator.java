/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.http;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;


public class HttpAccessorActivator implements BundleActivator {

	public static BundleContext bc;

	private HttpAccessorFactory factory;

	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		HttpAccessorActivator.bc = context;
		factory = new HttpAccessorFactory();
		registration = bc.registerService(DataAccessorFactory.class.getName(), factory,
			new Hashtable());
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
