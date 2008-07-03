/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.detector;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.datasource.DataSourceRegistry;
import org.semanticdesktop.aperture.detector.impl.DataSourceDetectorRegistryImpl;

/**
 * registers a DataSourceDetectorRegistry with OSGi.
 * listens if DataSourceDetectors are registered with OSGi and collects them.
 */
public class DataSourceDetectorServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

	private ServiceRegistration registration;

	private DataSourceDetectorRegistry registry;

	public void start(BundleContext context) throws Exception {
		
		bc = context;
		registry = new DataSourceDetectorRegistryImpl();
		registration = bc.registerService(DataSourceDetectorRegistry.class.getName(), registry,
			new Hashtable());

		String filter = "(objectclass=" + DataSourceDetector.class.getName() + ")";
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
	    DataSourceDetector detector;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
		    detector = (DataSourceDetector) DataSourceDetectorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.add(detector);
			break;
		case ServiceEvent.MODIFIED:
		    detector = (DataSourceDetector) DataSourceDetectorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(detector);
			registry.add(detector);
			break;
		case ServiceEvent.UNREGISTERING:
		    detector = (DataSourceDetector) DataSourceDetectorServiceActivator.bc.getService(event
					.getServiceReference());
			registry.remove(detector);
			break;
		}
	}


}
