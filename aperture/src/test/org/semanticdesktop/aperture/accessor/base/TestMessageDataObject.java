/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.util.ResourceUtil;

/**
 * Tests for the {@link MessageDataObjectBase} class
 */
public class TestMessageDataObject extends ApertureTestBase {

    private MessageDataObjectBase getTestInstance() throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "/mail-multipart-test.eml", getClass());
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, stream);
        URI id = new URIImpl("file://somefile.eml");
        MessageDataObjectBase obj = new MessageDataObjectBase(id, null, createRDFContainer(id), message);
        return obj;
    }
}