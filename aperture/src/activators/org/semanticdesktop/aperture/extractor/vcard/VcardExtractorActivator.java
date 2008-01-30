/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.vcard;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class VcardExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private ExtractorFactory factory;

    private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		

		VcardExtractorActivator.bc = context;

		factory = new VcardExtractorFactory();
		registration = bc.registerService(ExtractorFactory.class.getName(), factory,
			new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }

}
