/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PdfExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private PdfExtractorFactory factory;

    private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		

		PdfExtractorActivator.bc = context;

		factory = new PdfExtractorFactory();
		registration = bc.registerService(ExtractorFactory.class.getName(), factory,
			new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
