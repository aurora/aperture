/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.accessor;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * A general interface for DataObjects that encapsulate an instance of a javax.mail.internet.MimeMessage
 */
public interface MessageDataObject extends DataObject {

    /**
     * Gets an instance of the MimeMessage encapsulated by this DataObject.
     * 
     * @return an instance of the MimeMessage encapsulated by this MessageDataObject
     */
    public MimeMessage getMimeMessage();
    
    /**
     * Sets the MimeMessage that is to be encapsulated by this DataObject.
     * @param message the MimeMessage to set
     */
    public void setMimeMessage(MimeMessage message);
}
