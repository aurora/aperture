/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConnectionPanel extends JPanel {

	private JLabel serverLabel = null;

	private JLabel usernameLabel = null;

	private JLabel passwordLabel = null;

	private JTextField serverField = null;

	private JTextField usernameField = null;

	private JPasswordField passwordField = null;

	private JCheckBox sslCheckBox = null;

	private JLabel messageLabel = null;

	private JLabel portLabel = null;

	private JTextField portField = null;

	/**
	 * This is the default constructor
	 */
	public ConnectionPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.gridy = 4;
		gridBagConstraints21.weightx = 1.0;
		gridBagConstraints21.insets = new Insets(10, 20, 0, 0);
		gridBagConstraints21.gridx = 1;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.anchor = GridBagConstraints.WEST;
		gridBagConstraints12.insets = new Insets(10, 0, 0, 0);
		gridBagConstraints12.gridy = 4;
		portLabel = new JLabel();
		portLabel.setText("Port (optional)");
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.weightx = 1.0D;
		gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints11.insets = new java.awt.Insets(0, 0, 0, 0);
		gridBagConstraints11.gridwidth = 2;
		gridBagConstraints11.gridy = 0;
		messageLabel = new JLabel();
		messageLabel.setText("Please enter your mail server and user credentials.");
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridwidth = 2;
		gridBagConstraints6.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints6.weightx = 1.0D;
		gridBagConstraints6.weighty = 1.0D;
		gridBagConstraints6.insets = new java.awt.Insets(30, 0, 0, 0);
		gridBagConstraints6.gridy = 5;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 3;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.insets = new java.awt.Insets(10, 20, 0, 0);
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 2;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.insets = new java.awt.Insets(30, 20, 0, 0);
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.insets = new java.awt.Insets(30, 20, 0, 0);
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints2.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints2.gridy = 3;
		passwordLabel = new JLabel();
		passwordLabel.setText("Password (optional)");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints1.insets = new java.awt.Insets(30, 0, 0, 0);
		gridBagConstraints1.gridy = 2;
		usernameLabel = new JLabel();
		usernameLabel.setText("Username (optional)");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
		gridBagConstraints.gridy = 1;
		serverLabel = new JLabel();
		serverLabel.setText("Server");
		this.setLayout(new GridBagLayout());
		this.setSize(439, 204);
		this.setName("connection");
		this.add(serverLabel, gridBagConstraints);
		this.add(usernameLabel, gridBagConstraints1);
		this.add(passwordLabel, gridBagConstraints2);
		this.add(getServerField(), gridBagConstraints3);
		this.add(getUsernameField(), gridBagConstraints4);
		this.add(getPasswordField(), gridBagConstraints5);
		this.add(getSslCheckBox(), gridBagConstraints6);
		this.add(messageLabel, gridBagConstraints11);
		this.add(portLabel, gridBagConstraints12);
		this.add(getPortField(), gridBagConstraints21);
	}

	/**
	 * This method initializes serverField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getServerField() {
		if (serverField == null) {
			serverField = new JTextField();
		}
		return serverField;
	}

	/**
	 * This method initializes usernameField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUsernameField() {
		if (usernameField == null) {
			usernameField = new JTextField();
		}
		return usernameField;
	}

	/**
	 * This method initializes passwordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
		}
		return passwordField;
	}

	/**
	 * This method initializes sslCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getSslCheckBox() {
		if (sslCheckBox == null) {
			sslCheckBox = new JCheckBox();
			sslCheckBox.setText("Use secure connection (SSL)");
		}
		return sslCheckBox;
	}

	public boolean checkInputComplete() {
		// make sure a host name is entered
		String hostname = getServerField().getText().trim();
		if (hostname.equals("")) {
			displayError("Please enter an IMAP mail server.", "Missing server");
			return false;
		}

		// make sure the host name can be resolved
		try {
			InetAddress.getByName(hostname);
		}
		catch (UnknownHostException e) {
			displayError("Unable to find the specified server. Please enter an existing IMAP server name.",
				"Unable to find server");
			return false;
		}

		// make sure a valid port is entered
		String port = getPortField().getText().trim();
		if (!port.equals("")) {
			try {
				Integer.parseInt(port);
			}
			catch (NumberFormatException e) {
				displayError("Please enter a valid port number", "Invalid port");
				return false;
			}
		}

		return true;
	}

	public Store getConnectedStore() {
		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties);

		Store store;
		try {
			store = session.getStore(getSslCheckBox().isSelected() ? "imaps" : "imap");
		}
		catch (NoSuchProviderException e) {
			e.printStackTrace();
			displayError("Internal error: " + e.getClass() + ".", "Internal error");
			return null;
		}

		String hostname = getServerField().getText().trim();
		String username = getUsernameField().getText().trim();
		String password = new String(getPasswordField().getPassword()).trim();
		
		String portText = getPortField().getText().trim();
		int port = -1;
		if (!portText.equals("")) {
			port = Integer.parseInt(portText);
		}
		
		if (username.equals("")) {
			username = null;
		}
		if (password.equals("")) {
			password = null;
		}

		try {
			store.connect(hostname, port, username, password);
		}
		catch (MessagingException e) {
			e.printStackTrace();
			displayError("Unable to connect to mail store.", "Unable to connect");
			return null;
		}

		return store;
	}

	private void displayError(String message, String title) {
		JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), message, title,
			JOptionPane.ERROR_MESSAGE);
	}

	public String getServer() {
		String server = getServerField().getText().trim();
		return server.equals("") ? null : server;
	}

	public String getUsername() {
		String username = getUsernameField().getText().trim();
		return username.equals("") ? null : username;
	}

	public String getPassword() {
		String password = new String(getPasswordField().getPassword());
		return password.equals("") ? null : password;
	}

	public boolean isSslSelected() {
		return getSslCheckBox().isSelected();
	}

	/**
	 * This method initializes portField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPortField() {
		if (portField == null) {
			portField = new JTextField();
		}
		return portField;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
