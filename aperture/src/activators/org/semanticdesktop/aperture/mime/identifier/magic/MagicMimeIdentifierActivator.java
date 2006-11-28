/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.LinkExtractorFactory;
import org.semanticdesktop.aperture.hypertext.linkextractor.html.HtmlLinkExtractorActivator;
import org.semanticdesktop.aperture.hypertext.linkextractor.html.HtmlLinkExtractorFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifierFactory;

public class MagicMimeIdentifierActivator implements BundleActivator {

	public static BundleContext bc;

	private MimeTypeIdentifierFactory factory;

	private ServiceReference reference;

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting bundle" + this.getClass().getName());

		MagicMimeIdentifierActivator.bc = context;

		factory = new MagicMimeTypeIdentifierFactory();
		ServiceRegistration registration = bc.registerService(MimeTypeIdentifierFactory.class.getName(), factory,
			new Hashtable());
		reference = registration.getReference();
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle" + this.getClass().getName());
		bc.ungetService(reference);
	}
}
