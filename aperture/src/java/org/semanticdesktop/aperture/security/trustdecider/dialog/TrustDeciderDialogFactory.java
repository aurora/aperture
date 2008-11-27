/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.security.trustdecider.dialog;

import org.semanticdesktop.aperture.security.trustdecider.TrustDecider;
import org.semanticdesktop.aperture.security.trustdecider.TrustDeciderFactory;

/**
 * Hands our instances of the TrustDecider interface. 
 */
public class TrustDeciderDialogFactory implements TrustDeciderFactory {

    public TrustDecider get() {
        return new TrustDeciderDialog();
    }
}
