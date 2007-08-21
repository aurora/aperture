/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustmanager.standard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.security.trustdecider.Decision;
import org.semanticdesktop.aperture.security.trustdecider.TrustDecider;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;

public class TestStandardTrustManager extends ApertureTestBase {

    private static final String DUMMY_KEYSTORE_RESOURCE = "org/semanticdesktop/aperture/security/trustmanager/standard/aperturetestkeystore";

    private static final char[] DUMMY_KEYSTORE_PASSWORD = "aperturetest".toCharArray();
    
    public void testRootCertificates() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        // get a KeyStore containing all root certificates
        String keyStoreFile = System.getProperty("java.home") + File.separator + "lib" + File.separator
                + "security" + File.separator + "cacerts";
        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(keyStoreFile));

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(buffer, null);

        buffer.close();

        // get a new StandardTrustManager
        StandardTrustManager trustManager = getTrustManager();

        // check that it accepts all root certificates
        Enumeration aliases = keyStore.aliases();
        if (!aliases.hasMoreElements()) {
            throw new IllegalArgumentException("empty KeyStore");
        }

        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = certificate;

            // if a root certificate was rejected, this would throw an Exception
            trustManager.checkServerTrusted(chain, certificate.getPublicKey().getAlgorithm());
        }
    }

    public void testSelfSignedCertificate() throws CertificateException, KeyStoreException,
            NoSuchAlgorithmException, IOException {
        // get a self-signed certificate as a certificate chain
        X509Certificate[] chain = getSelfSignedCertificateChain();

        // check that it is rejected by the StandardTrustManager
        StandardTrustManager trustManager = getTrustManager();
        try {
            trustManager.checkServerTrusted(chain, chain[0].getPublicKey().getAlgorithm());
        }
        catch (CertificateException e) {
            return;
        }

        // the previous block should have resulted in an exception
        fail();
    }

    public void testPreApprovedSelfSignedCertificate() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException {
        // get a self-signed certificate as a certificate chain
        X509Certificate[] chain = getSelfSignedCertificateChain();

        // copy the same keystore to a temporary file
        InputStream stream = ResourceUtil.getInputStream(DUMMY_KEYSTORE_RESOURCE,TestStandardTrustManager.class);
        File tmpFile = File.createTempFile("keystore", null);
        IOUtil.writeStream(stream, tmpFile);
        stream.close();

        // get a new StandardTrustManager that uses the latter keystore for persistent storage
        StandardTrustManagerFactory factory = new StandardTrustManagerFactory();
        factory.setPersistentStorageFile(tmpFile);
        factory.setPersistentStoragePassword("aperturetest".toCharArray());
        StandardTrustManager trustManager = (StandardTrustManager) factory.get();
        
        // check that the certificate is accepted by this trust manager
        boolean checkFailed = false;
        try {
            trustManager.checkServerTrusted(chain, chain[0].getPublicKey().getAlgorithm());
        }
        catch (CertificateException e) {
            checkFailed = true;
        }
        
        // clean-up (needs to happen before assert statement, in case it fails)
        tmpFile.delete();
        
        assertFalse(checkFailed);
    }
    
    public void testTrustDeciderApplication() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        // create a StandardTrustDecider with an empty keystore
        File tmpFile = File.createTempFile("keystore", null);
        tmpFile.delete(); // make sure it doesn't exist right away or the StandardTM will try to load it
        StandardTrustManagerFactory factory = new StandardTrustManagerFactory();
        factory.setPersistentStorageFile(tmpFile);
        factory.setPersistentStoragePassword(DUMMY_KEYSTORE_PASSWORD);
        StandardTrustManager trustManager = (StandardTrustManager) factory.get();
        
        // check that a self-signed certificate is rejected
        X509Certificate[] chain = getSelfSignedCertificateChain();
        boolean checkFailed = false;
        
        try {
            trustManager.checkServerTrusted(chain, chain[0].getPublicKey().getAlgorithm());
        }
        catch (CertificateException e) {
            checkFailed = true;
        }
        
        assertTrue(checkFailed);
        
        // install a TrustDecider that accepts everything
        trustManager.setTrustDecider(new TrustDecider() {
            public Decision decide(X509Certificate[] chain, boolean rootCANotValid, boolean timeNotValid) {
                return Decision.TRUST_ALWAYS;
            }
        });
        
        // check that the self-signed certificate is now accepted
        trustManager.checkServerTrusted(chain, chain[0].getPublicKey().getAlgorithm());
        
        // check that the keystore is now non-empty (i.e. the previously accepted certificate is stored)
        long size = tmpFile.length();
        tmpFile.delete();
        assertTrue(size > 10l); // use an arbitrary file size that's still smaller than what a single
    }

    private StandardTrustManager getTrustManager() {
        StandardTrustManagerFactory factory = new StandardTrustManagerFactory();
        return (StandardTrustManager) factory.get();
    }

    private X509Certificate[] getSelfSignedCertificateChain() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        // load a dummy keystore
        InputStream stream = ResourceUtil.getInputStream(DUMMY_KEYSTORE_RESOURCE,TestStandardTrustManager.class);
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(stream, DUMMY_KEYSTORE_PASSWORD);
        stream.close();

        // return the self-signed certificate that it contains as a certificate chain
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate("aperturetest");
        return new X509Certificate[] { certificate };
    }
}
