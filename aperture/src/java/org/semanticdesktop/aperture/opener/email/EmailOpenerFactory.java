/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.email;

import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpenerFactory;


public class EmailOpenerFactory implements DataOpenerFactory {

    public EmailOpener get() {
        return new EmailOpener();
    }

    public Set getSupportedSchemes() {
        Set set = new HashSet<String>();
        set.add("email");
        set.add("msgid");
        set.add("imap");
        return set;
    }

    
}

