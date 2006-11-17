/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ExtractorUtilActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
	// TODO Auto-generated method stub
		System.out.println("Starting bundle" + this.getClass().getName());

	}

	public void stop(BundleContext context) throws Exception {
	// TODO Auto-generated method stub
		System.out.println("Stopping bundle" + this.getClass().getName());
	}

}
