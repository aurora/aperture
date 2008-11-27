/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.accessor.impl.DataAccessorRegistryImpl;

public class AccessorServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

	private ServiceRegistration registration;

	private DataAccessorRegistry registry;

	public void start(BundleContext context) throws Exception {
		
		bc = context;
		registry = new DataAccessorRegistryImpl();
		registration = bc.registerService(DataAccessorRegistry.class.getName(), registry,
			new Hashtable());

		String filter = "(objectclass=" + DataAccessorFactory.class.getName() + ")";
		bc.addServiceListener(this, filter);

		ServiceReference references[] = bc.getServiceReferences(null, filter);

		for (int i = 0; references != null && i < references.length; i++) {
			this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
		}

		
	}

	public void stop(BundleContext context) throws Exception {
		registration.unregister();
        registration = null;
		bc = null;
	}

	public void serviceChanged(ServiceEvent event) {
		DataAccessorFactory factory;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			factory = (DataAccessorFactory) AccessorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.add(factory);
			break;
		case ServiceEvent.MODIFIED:
			factory = (DataAccessorFactory) AccessorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			registry.add(factory);
			break;
		case ServiceEvent.UNREGISTERING:
			factory = (DataAccessorFactory) AccessorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			break;
		}
	}
}
