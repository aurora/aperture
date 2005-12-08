/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
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
