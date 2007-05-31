/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CoarsegrainedImplActivator implements BundleActivator {

	public static BundleContext bc;

	private CoreImplementationsActivator coreImplementationsActivator;

	private SupportingImplementationsActivator supportingImplementationsActivator;

	public void start(BundleContext context) throws Exception {
		coreImplementationsActivator = new CoreImplementationsActivator();
		coreImplementationsActivator.start(context);

		supportingImplementationsActivator = new SupportingImplementationsActivator();
		supportingImplementationsActivator.start(context);

		bc = context;
	}

	public void stop(BundleContext context) throws Exception {
		coreImplementationsActivator.stop(context);
		coreImplementationsActivator = null;
		
		supportingImplementationsActivator.stop(context);
		supportingImplementationsActivator = null;
		
		bc = null;
	}
}
