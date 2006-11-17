/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkextractorServiceActivator;
import org.semanticdesktop.aperture.mime.identifier.MimeIdentifierServiceActivator;
import org.semanticdesktop.aperture.security.trustdecider.TrustDeciderServiceActivator;
import org.semanticdesktop.aperture.security.trustmanager.TrustManagerServiceActivator;

public class SupportingServicesActivator implements BundleActivator {

	public static BundleContext bc;
	
	private LinkextractorServiceActivator linkExctractorServiceActivator;
	private MimeIdentifierServiceActivator mimeIdentifierServiceActivator;
	private TrustDeciderServiceActivator trustDeciderServiceActivator;
	private TrustManagerServiceActivator trustManagerServiceActivator;
	
	public void start(BundleContext context) throws Exception {
		linkExctractorServiceActivator = new LinkextractorServiceActivator();
		linkExctractorServiceActivator.start(context);
		
		mimeIdentifierServiceActivator = new MimeIdentifierServiceActivator();
		mimeIdentifierServiceActivator.start(context);
		
		trustDeciderServiceActivator = new TrustDeciderServiceActivator();
		trustDeciderServiceActivator.start(context);
		
		trustManagerServiceActivator = new TrustManagerServiceActivator();
		trustManagerServiceActivator.start(context);
		
		bc = context;
	}

	public void stop(BundleContext context) throws Exception {
		linkExctractorServiceActivator.stop(context);
		linkExctractorServiceActivator = null;
		
		mimeIdentifierServiceActivator.stop(context);
		mimeIdentifierServiceActivator = null;
		
		trustDeciderServiceActivator.stop(context);
		trustDeciderServiceActivator = null;
		
		trustManagerServiceActivator.stop(context);
		trustManagerServiceActivator = null;
		
		bc = null;
	}
}
