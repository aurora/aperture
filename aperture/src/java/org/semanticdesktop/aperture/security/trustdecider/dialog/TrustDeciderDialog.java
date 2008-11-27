/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.security.trustdecider.dialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.semanticdesktop.aperture.security.trustdecider.Decision;
import org.semanticdesktop.aperture.security.trustdecider.TrustDecider;
import org.semanticdesktop.aperture.util.ResourceUtil;

/**
 * A TrustDeciderDialog implements a TrustDecider by presenting the supplied Certificate chain to the
 * user and asking him whether and how it should be trusted.
 */
public class TrustDeciderDialog implements TrustDecider {

    private static final String RESOURCE_PACKAGE = "org/semanticdesktop/aperture/security/trustdecider/dialog/";

    private static final String WARNING_ICON_RESOURCE = RESOURCE_PACKAGE + "warning.png";

    private static final String CHECK_ICON_RESOURCE = RESOURCE_PACKAGE + "check.png";

    private Component parent;
    
    public void setParent(Component parent) {
        this.parent = parent;
    }
    
    public Component getParent() {
        return parent;
    }
    
    public Decision decide(final X509Certificate[] chain, boolean rootCANotValid, boolean timeNotValid) {
        int result = -1;

        // check if the certificate is a x.509 certificate
        int last = chain.length - 1;
        if (chain[0] instanceof X509Certificate && chain[last] instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) chain[0];
            X509Certificate cert2 = (X509Certificate) chain[last];

            Principal prinSubject = cert.getSubjectDN();
            Principal prinIssuer = cert2.getIssuerDN();

            // extract the subject name
            String subjectDNName = prinSubject.getName();
            String subjectName = null;

            int i = subjectDNName.indexOf("CN=");
            int j = 0;

            if (i < 0) {
                subjectName = "Unknown subject";
            }
            else {
                try {
                    // shift to the beginning of the "CN=" text
                    i = i + 3;

                    // check if it begins with a quote
                    if (subjectDNName.charAt(i) == '\"') {
                        // skip the quote
                        i = i + 1;

                        // search for another quote
                        j = subjectDNName.indexOf('\"', i);
                    }
                    else {
                        // no quote, so search for comma
                        j = subjectDNName.indexOf(',', i);
                    }

                    if (j < 0) {
                        subjectName = subjectDNName.substring(i);
                    }
                    else {
                        subjectName = subjectDNName.substring(i, j);
                    }
                }
                catch (Throwable e) {
                    subjectName = "Unknown subject";
                }
            }

            // Extract issuer name
            String issuerDNName = prinIssuer.getName();
            String issuerName = null;

            i = issuerDNName.indexOf("O=");
            j = 0;

            if (i < 0) {
                issuerName = "Unknown issuer";
            }
            else {
                try {
                    // shift to the beginning of the "O=" text
                    i = i + 2;

                    // check if it begins with a quote
                    if (issuerDNName.charAt(i) == '\"') {
                        // Skip the quote
                        i = i + 1;

                        // search for another quote
                        j = issuerDNName.indexOf('\"', i);
                    }
                    else {
                        // no quote, so search for comma
                        j = issuerDNName.indexOf(',', i);
                    }

                    if (j < 0) {
                        issuerName = issuerDNName.substring(i);
                    }
                    else {
                        issuerName = issuerDNName.substring(i, j);
                    }
                }
                catch (Throwable e) {
                    issuerName = "Unknown issuer";
                }
            }

            // construct dialog message
            ArrayList dialogMsgArray = new ArrayList();
            dialogMsgArray.add("Do you want to accept the certificate from " + subjectName);
            dialogMsgArray.add("for the purpose of exchanging encrypted information?");
            dialogMsgArray.add(" ");
            dialogMsgArray.add("Publisher authenticity verified by: " + issuerName);
            dialogMsgArray.add(" ");

            if (rootCANotValid) {
                JLabel label = new JLabel(
                        "The security certificate was issued by a company that is not trusted.");
                label.setIcon(getImageIcon(WARNING_ICON_RESOURCE));
                dialogMsgArray.add(label);
            }
            else {
                JLabel label = new JLabel("The security certificate was issued by a company that is trusted.");
                label.setIcon(getImageIcon(CHECK_ICON_RESOURCE));
                dialogMsgArray.add(label);
            }

            if (timeNotValid) {
                JLabel label = new JLabel("The security certificate has expired or is not yet valid.");
                label.setIcon(getImageIcon(WARNING_ICON_RESOURCE));
                dialogMsgArray.add(label);
            }
            else {
                JLabel label = new JLabel("The security certificate has not expired and is still valid.");
                label.setIcon(getImageIcon(CHECK_ICON_RESOURCE));
                dialogMsgArray.add(label);
            }

            dialogMsgArray.add(" ");

            dialogMsgArray
                    .add("<html><b>Caution:</b> " + subjectName + " asserts that this content is safe.");
            dialogMsgArray.add("You should only accept this content if you trust " + subjectName
                    + " to make that assertion.");

            JButton buttons[] = new JButton[4];
            buttons[0] = new JButton("Accept for Session");
            buttons[1] = new JButton("Always Accept");
            buttons[2] = new JButton("Deny");
            buttons[3] = new JButton("View Certificate(s)");
            final JButton viewCertButton = buttons[3];

            for (int index = 0; index < buttons.length; index++) {
                buttons[index].addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        Component c = (Component) ae.getSource();
                        JButton thisButton = (JButton) c;

                        if (thisButton == viewCertButton) {
                            // show the certificate dialog
                            CertificateDialog certificateDialog = new CertificateDialog(chain);
                            certificateDialog.show(parent);
                        }
                        else {
                            while (c != null) {
                                c = c.getParent();
                                if (c instanceof JOptionPane) {
                                    ((JOptionPane) c).setValue(thisButton);
                                    break;
                                }
                            }
                        }
                    }
                });
            }

            // show the trust decider dialog
            result = JOptionPane.showOptionDialog(parent, dialogMsgArray.toArray(), "Security Alert",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[2]);
        }

        // convert the pressed button to a Decision
        if (result == 0) {
            return Decision.TRUST_THIS_SESSION;
        }
        else if (result == 1) {
            return Decision.TRUST_ALWAYS;
        }
        else if (result == 2) {
            return Decision.DISTRUST;
        }
        else {
            return null;
        }
    }
    
    private ImageIcon getImageIcon(String resourceName) {
        ImageIcon result = null;

        URL resourceURL = ResourceUtil.getURL(resourceName,TrustDeciderDialog.class);
        if (resourceURL != null) {
            result = new ImageIcon(resourceURL);
        }
        
        return result;
    }
}
