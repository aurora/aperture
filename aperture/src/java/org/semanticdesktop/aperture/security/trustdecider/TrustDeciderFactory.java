/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider;

/**
 * A TrustDeciderFactory hands outs TrustDecider instances. 
 */
public interface TrustDeciderFactory {

    /**
     * Gets a TrustDecider.
     */
    public TrustDecider get();
}
