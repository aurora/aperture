/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.semanticdesktop.aperture.hypertext.linkextractor.html.HtmlLinkExtractorActivator;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeIdentifierActivator;
import org.semanticdesktop.aperture.security.trustdecider.dialog.DialogTrustDeciderActivator;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManagerActivator;

public class SupportingImplementationsActivator implements BundleActivator {

	public static BundleContext bc;
	
	private HtmlLinkExtractorActivator htmlLinkExtractorActivator;
	private MagicMimeIdentifierActivator magicMimeIdentifierActivator;
	private DialogTrustDeciderActivator dialogTrustDeciderActivator;
	private StandardTrustManagerActivator standardTrustManagerActivator;
	
	public void start(BundleContext context) throws Exception {
		htmlLinkExtractorActivator = new HtmlLinkExtractorActivator();
		htmlLinkExtractorActivator.start(context);
		
		magicMimeIdentifierActivator = new MagicMimeIdentifierActivator();
		magicMimeIdentifierActivator.start(context);
		
		dialogTrustDeciderActivator = new DialogTrustDeciderActivator();
		dialogTrustDeciderActivator.start(context);
		
		standardTrustManagerActivator = new StandardTrustManagerActivator();
		standardTrustManagerActivator.start(context);
		
		bc = context;

	}

	public void stop(BundleContext context) throws Exception {		
		htmlLinkExtractorActivator.stop(context);
		htmlLinkExtractorActivator = null;
		
		magicMimeIdentifierActivator.stop(context);
		magicMimeIdentifierActivator = null;
		
		dialogTrustDeciderActivator.stop(context);
		dialogTrustDeciderActivator = null;
		
		standardTrustManagerActivator.stop(context);
		standardTrustManagerActivator = null;
		
		bc = null;
	}
}
