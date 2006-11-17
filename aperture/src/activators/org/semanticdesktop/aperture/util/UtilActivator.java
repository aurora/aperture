/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class UtilActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());
		// this bundle only exposes api, the activator doesn't have to da anything
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		// this bundle only exposes api, the activator doesn't have to do anything
	}

}
