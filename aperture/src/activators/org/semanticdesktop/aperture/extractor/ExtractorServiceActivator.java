/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.impl.ExtractorRegistryImpl;

/**
 * Listens for changes in registered ExtractorFactories and FileExtractorFactories.
 * Needs two listeners for this (listeners are replaced)
 */
public class ExtractorServiceActivator implements BundleActivator, ServiceListener {

	public static BundleContext bc = null;

    private ServiceRegistration registration;

	private ExtractorRegistry registry;
    
	public void start(BundleContext context) throws Exception {
		
		bc = context;
		registry = new ExtractorRegistryImpl();
		registration = bc.registerService(ExtractorRegistry.class.getName(), registry,
			new Hashtable());
		

		String filter = "(objectclass=" + ExtractorFactory.class.getName() + ")";
        // ATTENTION: here we register THIS
		bc.addServiceListener(this, filter);

		ServiceReference references[] = bc.getServiceReferences(null, filter);

		for (int i = 0; references != null && i < references.length; i++) {
			this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
		}
		
		filter = "(objectclass=" + FileExtractorFactory.class.getName() + ")";
        // ATTENTION: here we register another object, otherwise this overrides above listener
        bc.addServiceListener(new ServiceListener() {
            public void serviceChanged(ServiceEvent event) {
                ExtractorServiceActivator.this.serviceChanged(event);                
            }
        }, filter);

        references = bc.getServiceReferences(null, filter);

        for (int i = 0; references != null && i < references.length; i++) {
            this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, references[i]));
        }

		
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        bc = null;
    }

	public void serviceChanged(ServiceEvent event) {
	    Object instance = null;
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			instance = ExtractorServiceActivator.bc.getService(event.getServiceReference());
			if (instance instanceof ExtractorFactory) {
			    registry.add((ExtractorFactory)instance);
			} else if (instance instanceof FileExtractorFactory) {
			    registry.add((FileExtractorFactory)instance);
			}
			break;
		case ServiceEvent.MODIFIED:
		    instance = ExtractorServiceActivator.bc.getService(event.getServiceReference());
            if (instance instanceof ExtractorFactory) {
                registry.remove((ExtractorFactory)instance);
                registry.add((ExtractorFactory)instance);
            } else if (instance instanceof FileExtractorFactory) {
                registry.remove((FileExtractorFactory)instance);
                registry.add((FileExtractorFactory)instance);
            }
			break;
		case ServiceEvent.UNREGISTERING:
		    instance = ExtractorServiceActivator.bc.getService(event.getServiceReference());
            if (instance instanceof ExtractorFactory) {
                registry.remove((ExtractorFactory)instance);
            } else if (instance instanceof FileExtractorFactory) {
                registry.remove((FileExtractorFactory)instance);
            }
			break;
		}
	}
}
