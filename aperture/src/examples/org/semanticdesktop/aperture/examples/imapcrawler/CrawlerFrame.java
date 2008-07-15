/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Security;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.semanticdesktop.aperture.security.socketfactory.standard.StandardSocketFactory;
import org.semanticdesktop.aperture.security.trustdecider.dialog.TrustDeciderDialog;
import org.semanticdesktop.aperture.security.trustmanager.standard.StandardTrustManager;

public class CrawlerFrame extends JFrame {

	private static CrawlerFrame INSTANCE;

	// initialize the SocketFactory for SSL communication
	static {
		// determine the Java version we're running
		String versionString = System.getProperty("java.specification.version");
		double version = 1.0;
		try {
			version = Double.parseDouble(versionString);
		}
		catch (NumberFormatException e) {
			// ignore for now
			e.printStackTrace();
		}

		// set the SocketFactory class to use in a way that depends on the Java version
		if (version < 1.5) {
			// setting the trust manager for Java VMs < 1.5
			System.setProperty("mail.imaps.socketFactory.class", MySocketFactory.class.getName());
			System.setProperty("mail.imaps.socketFactory.fallback", "false");
		}
		else {
			// setting the trust manager for Java VMs >= 1.5
			Security.setProperty("ssl.SocketFactory.provider", MySocketFactory.class.getName());
		}
	}

	private JPanel jContentPane = null;

	private CrawlerWizard wizardPanel = null;

	/**
	 * This is the default constructor
	 */
	public CrawlerFrame() {
		super();
		initialize();
		installListeners();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(529, 390);
		this.setContentPane(getJContentPane());
		this.setTitle("Aperture IMAP Crawler");
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				shutDown();
			}
		});
	}

	private void installListeners() {
		getWizardPanel().getCancelButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				shutDown();
			}
		});
	}

	private void shutDown() {
		System.exit(0);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getWizardPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes wizardPanel
	 * 
	 * @return org.semanticdesktop.aperture.examples.imapcrawler.CrawlerWizard
	 */
	private CrawlerWizard getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new CrawlerWizard();
			wizardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
		}
		return wizardPanel;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				// change look and feel
				if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
					try {
						UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
					}
					catch (Exception e) {
						// ignore
					}
				}

				// set up a CrawlerFrame
				CrawlerFrame frame = new CrawlerFrame();
				INSTANCE = frame;
				frame.setVisible(true);
			}
		});
	}

	public static class MySocketFactory extends SSLSocketFactory {

		private SSLSocketFactory factory;

		public MySocketFactory() {
			try {
				SSLContext sslcontext = SSLContext.getInstance("TLS");

				// The trust manager will decide on which certificates are trusted and will store
				// decisions taken on certificates. Permanently accepted certificates are stored in an
				// imap certificates file in the working dir.
				StandardTrustManager trustManager = new StandardTrustManager(new File("imapcertificates"),
						"@pertur3".toCharArray());

				// When trust cannot be established immediately, the trust manager delegates the decision
				// to a trust decider, which in this form opens a dialog asking the user what to do
				TrustDeciderDialog trustDecider = new TrustDeciderDialog();
				trustDecider.setParent(CrawlerFrame.INSTANCE);
				trustManager.setTrustDecider(trustDecider);

				sslcontext.init(null, new TrustManager[] { trustManager }, null);
				factory = (SSLSocketFactory) sslcontext.getSocketFactory();
			}
			catch (Exception e) {
				System.err.println("Exception while instantiating a StandardSocketFactory");
                e.printStackTrace();
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

} // @jve:decl-index=0:visual-constraint="10,10"
