/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.util.UtilActivator;
import org.semanticdesktop.aperture.vocabulary.VocabularyActivator;

public class CoarsegrainedCoreActivator implements BundleActivator {

	public static BundleContext bc;

	private CoreServicesActivator coreServicesActivator;

	private SupportingServicesActivator supportingServicesActivator;

	private VocabularyActivator vocabularyActivator;

	private UtilActivator utilActivator;

	public void start(BundleContext context) throws Exception {
		coreServicesActivator = new CoreServicesActivator();
		coreServicesActivator.start(context);
		
		supportingServicesActivator = new SupportingServicesActivator();
		supportingServicesActivator.start(context);
		
		vocabularyActivator = new VocabularyActivator();
		vocabularyActivator.start(context);
		
		utilActivator = new UtilActivator();
		utilActivator.start(context);
		
		bc = context;		
	}

	public void stop(BundleContext context) throws Exception {
		coreServicesActivator.stop(context);
		coreServicesActivator = null;
		
		supportingServicesActivator.stop(context);
		supportingServicesActivator = null;
		
		vocabularyActivator.stop(context);
		vocabularyActivator = null;
		
		utilActivator.stop(context);
		utilActivator = null;
		
		bc = null;
	}
}
