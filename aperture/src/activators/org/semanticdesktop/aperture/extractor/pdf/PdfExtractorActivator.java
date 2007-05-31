/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

public class PdfExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private PdfExtractorFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		

		PdfExtractorActivator.bc = context;

		factory = new PdfExtractorFactory();
		ServiceRegistration registration = bc.registerService(ExtractorFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
	}
}
