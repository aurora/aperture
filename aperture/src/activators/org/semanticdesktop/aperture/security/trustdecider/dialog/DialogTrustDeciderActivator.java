/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.security.trustdecider.dialog;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.security.trustdecider.TrustDeciderFactory;

public class DialogTrustDeciderActivator implements BundleActivator {

	public static BundleContext bc;

	private TrustDeciderFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		

		DialogTrustDeciderActivator.bc = context;

		factory = new TrustDeciderDialogFactory();
		ServiceRegistration registration = bc.registerService(TrustDeciderFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		
		bc.ungetService(reference);
	}

}
