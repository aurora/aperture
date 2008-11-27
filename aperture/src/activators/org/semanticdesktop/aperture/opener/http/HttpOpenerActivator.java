/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.http;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;

public class HttpOpenerActivator implements BundleActivator {

	public static BundleContext bc;

	private DataOpenerFactory factory;

	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		HttpOpenerActivator.bc = context;
		factory = new HttpOpenerFactory();
		registration = bc.registerService(DataOpenerFactory.class.getName(), factory,
			new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
