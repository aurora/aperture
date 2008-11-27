/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;

public class HtmlLinkExtractorActivator implements BundleActivator {

	public static BundleContext bc;

	private LinkExtractorFactory factory;

	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		HtmlLinkExtractorActivator.bc = context;
		factory = new HtmlLinkExtractorFactory();
		registration = bc.registerService(LinkExtractorFactory.class.getName(), factory,
			new Hashtable());
	}
    
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
