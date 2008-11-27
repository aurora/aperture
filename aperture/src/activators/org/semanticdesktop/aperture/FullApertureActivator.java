/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activates the full aperture bundle.
 */
public class FullApertureActivator implements BundleActivator {

	public static BundleContext bc = null;

	private CoarsegrainedCoreActivator coreActivator;

	private CoarsegrainedImplActivator implActivator;

	public void start(BundleContext context) throws Exception {
		coreActivator = new CoarsegrainedCoreActivator();
		coreActivator.start(context);
		
		implActivator = new CoarsegrainedImplActivator();
		implActivator.start(context);
		
		bc = context;
	}

	public void stop(BundleContext context) throws Exception {
		coreActivator.stop(context);
		coreActivator = null;
		
		implActivator.stop(context);
		implActivator = null;
		
		bc = null;		
	}
}
