/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.rdf;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RDFBundleActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		// this bundle only exposes api, it doesn't register or use any services
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
//		 this bundle only exposes api, it doesn't register or use any services
	}

}
