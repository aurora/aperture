/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security.socketfactory.standard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManager;

/**
 * A SSLSocketFactory implementation that uses a StandardTrustManager as its TrustManager implementation.
 */
public class StandardSocketFactory extends SSLSocketFactory {

    private static final Logger LOGGER = Logger.getLogger(StandardSocketFactory.class.getName());

    private SSLSocketFactory factory;

    public StandardSocketFactory() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { new StandardTrustManager() }, null);
            factory = (SSLSocketFactory) sslcontext.getSocketFactory();
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while instantiating a StandardSocketFactory", e);
        }
    }

    public static SocketFactory getDefault() {
        return new StandardSocketFactory();
    }

	public Socket createSocket() throws IOException {
		return factory.createSocket();
	}

    public Socket createSocket(Socket socket, String host, int port, boolean flag) throws IOException {
        return factory.createSocket(socket, host, port, flag);
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return factory.createSocket(address, port, localAddress, localPort);
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return factory.createSocket(host, port);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        return factory.createSocket(host, port, localHost, localPort);
    }

    public Socket createSocket(String host, int port) throws IOException {
        return factory.createSocket(host, port);
    }

    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }
}
