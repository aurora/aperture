/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.hypertext.linkextractor.impl.LinkExtractorRegistryImpl;

public class LinkextractorServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

	private ServiceReference reference;

	private LinkExtractorRegistry registry;

	public void start(BundleContext context) throws Exception {
		
		bc = context;
		registry = new LinkExtractorRegistryImpl();
		ServiceRegistration registration = bc.registerService(LinkExtractorRegistry.class.getName(), registry,
			new Hashtable());
		reference = registration.getReference();

		String filter = "(objectclass=" + LinkExtractorFactory.class.getName() + ")";
		bc.addServiceListener(this, filter);

		ServiceReference references[] = bc.getServiceReferences(null, filter);

		for (int i = 0; references != null && i < references.length; i++) {
			this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
		}

		
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
		
		reference = null;
		bc = null;
	}

	public void serviceChanged(ServiceEvent event) {
		LinkExtractorFactory factory;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			factory = (LinkExtractorFactory) LinkextractorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.add(factory);
			break;
		case ServiceEvent.MODIFIED:
			factory = (LinkExtractorFactory) LinkextractorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			registry.add(factory);
			break;
		case ServiceEvent.UNREGISTERING:
			factory = (LinkExtractorFactory) LinkextractorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(factory);
			break;
		}
	}

}
