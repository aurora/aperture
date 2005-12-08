/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager.standard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A CertificateStore that can save its certificates to and load them from a file.
 * 
 * <p>
 * PersistentCertificateStore internally used a KeyStore to contain its Certificates and optionally
 * applies a password during load and save. See the KeyStore API documentation for information on how
 * this password is applied.
 * 
 * @see java.security.KeyStore
 */
public class PersistentCertificateStore implements CertificateStore {

    private static final Logger LOGGER = Logger.getLogger(PersistentCertificateStore.class.getName());

    private File certificatesFile;

    private KeyStore keyStore;

    private char[] password;

    public PersistentCertificateStore(File certificatesFile, char[] password) {
        this.certificatesFile = certificatesFile;
        this.password = password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }
    
    public void load() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {

                public Object run() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
                        IOException {
                    // initialize the KeyStore instance
                    if (keyStore == null) {
                        keyStore = KeyStore.getInstance("JKS");
                        keyStore.load(null, null);
                    }

                    // load the certificates, if they exist
                    if (certificatesFile.exists()) {
                        FileInputStream stream = new FileInputStream(certificatesFile);
                        BufferedInputStream buffer = new BufferedInputStream(stream);

                        keyStore.load(buffer, password);

                        buffer.close();
                        stream.close();
                    }

                    return null;
                }
            });
        }
        catch (PrivilegedActionException e) {
            Exception ex = e.getException();

            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
            else if (ex instanceof CertificateException) {
                throw (CertificateException) ex;
            }
            else if (ex instanceof KeyStoreException) {
                throw (KeyStoreException) ex;
            }
            else if (ex instanceof NoSuchAlgorithmException) {
                throw (NoSuchAlgorithmException) ex;
            }
            else {
                LOGGER.log(Level.SEVERE, "Unrecognized nested exception, ignoring", e);
            }
        }
    }

    public void save() throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {

                public Object run() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
                        IOException {
                    FileOutputStream stream = new FileOutputStream(certificatesFile);
                    BufferedOutputStream buffer = new BufferedOutputStream(stream);

                    keyStore.store(buffer, password);

                    buffer.close();
                    stream.close();

                    return null;
                }
            });
        }
        catch (PrivilegedActionException e) {
            Exception ex = e.getException();

            if (ex instanceof IOException) {
                throw (IOException) ex;
            }
            else if (ex instanceof CertificateException) {
                throw (CertificateException) ex;
            }
            else if (ex instanceof KeyStoreException) {
                throw (KeyStoreException) ex;
            }
            else if (ex instanceof NoSuchAlgorithmException) {
                throw (NoSuchAlgorithmException) ex;
            }
            else {
                LOGGER.log(Level.SEVERE, "Unrecognized nested exception, ignoring", e);
            }
        }
    }

    public void add(Certificate certificate) throws KeyStoreException {
        if (!contains(certificate)) {
            Random rand = new Random();
            String alias = null;

            // loop until we have a unique alias that is not yet present in the store
            do {
                alias = "pcscert" + rand.nextLong();
            }
            while (keyStore.getCertificate(alias) != null);

            keyStore.setCertificateEntry(alias, certificate);
        }
    }

    public void remove(Certificate certificate) throws KeyStoreException {
        String alias = keyStore.getCertificateAlias(certificate);
        if (alias != null) {
            keyStore.deleteEntry(alias);
        }
    }

    public boolean contains(Certificate certificate) throws KeyStoreException {
        // a certificate alias is only returned when there is a match
        String alias = keyStore.getCertificateAlias(certificate);
        return alias != null;
    }

    public boolean verify(Certificate certificate) throws KeyStoreException {
        // no-op
        return false;
    }

    public Iterator iterator() throws KeyStoreException {
        HashSet set = new HashSet();
        Enumeration aliases = keyStore.aliases();

        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            set.add(keyStore.getCertificate(alias));
        }

        return set.iterator();
    }
}
