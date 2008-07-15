/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager.standard;

import java.io.File;

import javax.net.ssl.TrustManager;

import org.semanticdesktop.aperture.security.trustdecider.TrustDecider;
import org.semanticdesktop.aperture.security.trustmanager.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A StandardTrustManagerFactory hands out StandardTrustManager instances.
 */
public class StandardTrustManagerFactory implements TrustManagerFactory {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private StandardTrustManager trustManager;

    private TrustDecider trustDecider;

    private File persistentStorageFile;

    private char[] persistentStoragePassword;

    public void setTrustDecider(TrustDecider trustDecider) {
        this.trustDecider = trustDecider;
    }

    public TrustDecider getTrustDecider() {
        return trustDecider;
    }

    public void setPersistentStorageFile(File file) {
        // only do something when the value is changed
        if ((file == null && persistentStorageFile != null) || !file.equals(persistentStorageFile)) {
            // change the value
            this.persistentStorageFile = file;

            // throw away the shared trust manager
            trustManager = null;
        }
    }

    public File getPersistentStorageFile() {
        return persistentStorageFile;
    }

    public void setPersistentStoragePassword(char[] password) {
        this.persistentStoragePassword = password;
    }

    public TrustManager get() {
        synchronized (this) {
            if (trustManager == null) {
                try {
                    trustManager = new StandardTrustManager(persistentStorageFile, persistentStoragePassword);
                    trustManager.setTrustDecider(trustDecider);
                }
                catch (Exception e) {
                    logger.error("Exception while creating trust manager", e);
                }
            }

            return trustManager;
        }
    }
}
