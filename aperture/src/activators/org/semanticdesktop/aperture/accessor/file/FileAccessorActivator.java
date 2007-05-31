/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.DataAccessorFactory;

public class FileAccessorActivator implements BundleActivator {

	public static BundleContext bc;

	private FileAccessorFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {

		FileAccessorActivator.bc = context;

		factory = new FileAccessorFactory();
		ServiceRegistration registration = bc.registerService(DataAccessorFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
	}
}
