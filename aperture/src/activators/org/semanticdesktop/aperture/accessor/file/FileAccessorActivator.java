/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;

public class FileAccessorActivator implements BundleActivator {

	public static BundleContext bc;

	private FileAccessorFactory factory;

	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {

		FileAccessorActivator.bc = context;

		factory = new FileAccessorFactory();
		registration = bc.registerService(DataAccessorFactory.class.getName(), factory,
			new Hashtable());
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}
}
