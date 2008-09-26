/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener.email;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.opener.DataOpener;
import org.semanticdesktop.aperture.opener.DataOpenerFactory;
import org.semanticdesktop.aperture.opener.http.HttpOpener;


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

