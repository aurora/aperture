/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.rtf;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorActivator;
import org.semanticdesktop.aperture.extractor.presentations.PresentationsExtractorFactory;

public class RtfExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private ExtractorFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());

		RtfExtractorActivator.bc = context;

		factory = new RtfExtractorFactory();
		ServiceRegistration registration = bc.registerService(ExtractorFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
	}
}
