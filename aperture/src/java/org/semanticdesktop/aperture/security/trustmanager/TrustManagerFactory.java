/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager;

import javax.net.ssl.TrustManager;

/**
 * A TrustManagerFactory hands out instances of TrustManager.
 */
public interface TrustManagerFactory {

    /**
     * Return a new or shared TrustManager instance.
     * 
     * @return A TrustManager.
     */
    public TrustManager get();
}
