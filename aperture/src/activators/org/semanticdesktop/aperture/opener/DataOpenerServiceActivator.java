/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.opener.impl.DataOpenerRegistryImpl;

public class DataOpenerServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

    private ServiceRegistration registration;

	private DataOpenerRegistry registry;

	public void start(BundleContext context) throws Exception {
		
		bc = context;
		registry = new DataOpenerRegistryImpl();
		registration = bc.registerService(DataOpenerRegistry.class.getName(), registry,
			new Hashtable());
		

		String filter = "(objectclass=" + DataOpenerFactory.class.getName() + ")";
		bc.addServiceListener(this, filter);

		ServiceReference references[] = bc.getServiceReferences(null, filter);

		for (int i = 0; references != null && i < references.length; i++) {
			this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
		}

		
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        bc = null;
    }

	public void serviceChanged(ServiceEvent event) {
		DataOpenerFactory factory;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			factory = (DataOpenerFactory) DataOpenerServiceActivator.bc.getService(event
					.getServiceReference());
			registry.add(factory);
			break;
		case ServiceEvent.MODIFIED:
			factory = (DataOpenerFactory) DataOpenerServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			registry.add(factory);
			break;
		case ServiceEvent.UNREGISTERING:
			factory = (DataOpenerFactory) DataOpenerServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			break;
		}
	}
}
