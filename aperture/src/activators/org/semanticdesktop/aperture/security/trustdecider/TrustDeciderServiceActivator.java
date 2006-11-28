/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.security.trustdecider.impl.TrustDeciderRegistryImpl;

public class TrustDeciderServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

	private ServiceReference reference;

	private TrustDeciderRegistry registry;

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		bc = context;
		registry = new TrustDeciderRegistryImpl();
		ServiceRegistration registration = bc.registerService(TrustDeciderRegistry.class.getName(), registry,
			new Hashtable());
		reference = registration.getReference();

		String filter = "(objectclass=" + TrustDeciderFactory.class.getName() + ")";
		bc.addServiceListener(this, filter);

		ServiceReference references[] = bc.getServiceReferences(null, filter);

		for (int i = 0; references != null && i < references.length; i++) {
			this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
		}

		System.out.println("Service registered: " + TrustDeciderRegistry.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
		System.out.println("Service unregistered: " + TrustDeciderRegistry.class.getName());
		reference = null;
		bc = null;
	}

	public void serviceChanged(ServiceEvent event) {
		TrustDeciderFactory factory;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			factory = (TrustDeciderFactory) TrustDeciderServiceActivator.bc.getService(event
					.getServiceReference());
			registry.add(factory);
			break;
		case ServiceEvent.MODIFIED:
			factory = (TrustDeciderFactory) TrustDeciderServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			registry.add(factory);
			break;
		case ServiceEvent.UNREGISTERING:
			factory = (TrustDeciderFactory) TrustDeciderServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			break;
		}
	}
}
