/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mp3;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.FileExtractorFactory;

public class Mp3FileExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private MP3FileExtractorFactory factory;

    private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		

		Mp3FileExtractorActivator.bc = context;

		factory = new MP3FileExtractorFactory();
		registration = bc.registerService(FileExtractorFactory.class.getName(), factory,
			new Hashtable());
	}

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
