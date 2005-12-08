/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.security.trustdecider.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A CertificateDialog displays a chain of Certificates.
 */
public class CertificateDialog {

    private JTable table;

    private JTextArea textArea;

    private Object[] msg, options;

    public CertificateDialog(Certificate[] chain) {
        if (chain.length > 0 && chain[0] instanceof X509Certificate) {
            // build the tree
            DefaultMutableTreeNode root = null;
            DefaultMutableTreeNode currentNode = null;

            for (int i = 0; i < chain.length; i++) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new CertificateInfo(
                        (X509Certificate) chain[i]));
                if (root == null) {
                    root = childNode;
                    currentNode = childNode;
                }
                else {
                    currentNode.add(childNode);
                    currentNode = childNode;
                }
            }

            final JTree tree = new JTree(root);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.putClientProperty("JTree.lineStyle", "Angled");
            tree.addTreeSelectionListener(new TreeSelectionListener() {

                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                            .getLastSelectedPathComponent();
                    if (node != null) {
                        CertificateInfo certInfo = (CertificateInfo) node.getUserObject();
                        showCertificate(certInfo.getCertificate());
                    }
                }
            });

            JScrollPane treePane = new JScrollPane(tree);
            treePane.setPreferredSize(new Dimension(200, 100));

            JLabel label = new JLabel("Certificate chain:");
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            JPanel treePanel = new JPanel();
            treePanel.setLayout(new BorderLayout());
            treePanel.add(treePane, BorderLayout.CENTER);
            treePanel.add(label, BorderLayout.NORTH);

            // build the table
            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String value = (String) table.getValueAt(row, 1);
                        textArea.setText(value);
                    }
                }
            });
            Dimension dim = table.getPreferredScrollableViewportSize();
            dim.setSize(dim.getWidth(), 120);
            table.setPreferredScrollableViewportSize(dim);
            JScrollPane tablePane = new JScrollPane(table);
            tablePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                    .createEmptyBorder(0, 0, 5, 0), tablePane.getBorder()));

            // create the text area
            textArea = new JTextArea();
            textArea.setLineWrap(false);
            textArea.setEditable(false);
            textArea.setColumns(40);
            textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
            JScrollPane textAreaPane = new JScrollPane(textArea);

            // create the panels
            JLabel contentsLabel = new JLabel("Selected certificate contents:");
            contentsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.add(tablePane, BorderLayout.CENTER);
            infoPanel.add(textAreaPane, BorderLayout.SOUTH);
            infoPanel.add(contentsLabel, BorderLayout.NORTH);

            JPanel totalPanel = new JPanel();
            totalPanel.setLayout(new BorderLayout());
            totalPanel.add(treePanel, BorderLayout.WEST);
            totalPanel.add(Box.createHorizontalStrut(25), BorderLayout.CENTER);
            totalPanel.add(infoPanel, BorderLayout.EAST);

            // create components for the show method
            msg = new Object[] { totalPanel };

            final JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Component c = (Component) e.getSource();
                    while (c != null) {
                        c = c.getParent();
                        if (c instanceof JOptionPane) {
                            ((JOptionPane) c).setValue(closeButton);
                            break;
                        }
                    }
                }
            });
            options = new Object[] { closeButton };

            // initialize contents
            showCertificate((X509Certificate) chain[0]);
        }
    }

    public void show(Component parent) {
        JOptionPane.showOptionDialog(parent, msg, "Certificate Info", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }

    private String formatDNString(String dnString) {
        int length = dnString.length();
        boolean inQuote = false;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = dnString.charAt(i);

            // Check if we are in quote
            if (c == '\"' || c == '\'') {
                inQuote = !inQuote;
            }

            if (c == ',' && !inQuote) {
                buffer.append(",\n");
            }
            else {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }

    private void showCertificate(X509Certificate cert) {
        String version = "V" + cert.getVersion();
        String serialNumber = "[" + cert.getSerialNumber() + "]";
        String sigAlg = "[" + cert.getSigAlgName() + "]";
        String issuer = formatDNString(cert.getIssuerDN().toString());
        String validity = "[From: " + cert.getNotBefore() + ",\n To: " + cert.getNotAfter() + "]";
        String subject = formatDNString(cert.getSubjectDN().toString());

        HexDumpEncoder encoder = new HexDumpEncoder();
        String sig = encoder.encodeBuffer(cert.getSignature());

        String[][] data = { { "Version", version }, { "Serial Number", serialNumber },
                { "Signature Algorithm", sigAlg }, { "Issuer", issuer }, { "Validity", validity },
                { "Subject", subject }, { "Signature", sig } };

        String[] columnNames = { "Field", "Value" };

        table.setModel(new DefaultTableModel(data, columnNames) {

            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });

        // Select last row by default
        table.setRowSelectionInterval(6, 6);
    }

    // code extracted from sun.misc.HexEncoder and sun.misc.CharacterEncoder
    private static class HexDumpEncoder {

        private static final int BYTES_PER_LINE = 16;

        private static final int BYTES_PER_ATOM = 1;

        private int offset;

        private int thisLineLength;

        private int currentByte;

        private byte thisLine[] = new byte[16];

        private PrintStream pStream;

        public String encodeBuffer(byte aBuffer[]) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);

            try {
                encodeBuffer(inStream, outStream);
            }
            catch (Exception IOException) {
                // This should never happen.
                throw new Error("encodeBuffer internal error");
            }

            return (outStream.toString());
        }

        private void encodeBuffer(InputStream inStream, OutputStream outStream) throws IOException {
            int j;
            int numBytes;
            byte tmpbuffer[] = new byte[BYTES_PER_LINE];

            encodeBufferPrefix(outStream);

            while (true) {
                numBytes = readFully(inStream, tmpbuffer);
                if (numBytes == 0) {
                    break;
                }

                encodeLinePrefix(outStream, numBytes);
                for (j = 0; j < numBytes; j += BYTES_PER_ATOM) {
                    if ((j + BYTES_PER_ATOM) <= numBytes) {
                        encodeAtom(outStream, tmpbuffer, j, BYTES_PER_ATOM);
                    }
                    else {
                        encodeAtom(outStream, tmpbuffer, j, (numBytes) - j);
                    }
                }

                encodeLineSuffix(outStream);
                if (numBytes < BYTES_PER_LINE) {
                    break;
                }
            }
        }

        private void encodeBufferPrefix(OutputStream o) throws IOException {
            offset = 0;
            pStream = new PrintStream(o);
        }

        private int readFully(InputStream in, byte buffer[]) throws java.io.IOException {
            for (int i = 0; i < buffer.length; i++) {
                int q = in.read();
                if (q == -1) {
                    return i;
                }
                buffer[i] = (byte) q;
            }
            return buffer.length;
        }

        private void encodeLinePrefix(OutputStream o, int len) throws IOException {
            hexDigit((byte) ((offset >>> 8) & 0xff));
            hexDigit((byte) (offset & 0xff));
            pStream.print(": ");
            currentByte = 0;
            thisLineLength = len;
        }

        private void hexDigit(byte x) {
            char c;

            c = (char) ((x >> 4) & 0xf);
            if (c > 9) {
                c = (char) ((c - 10) + 'A');
            }
            else {
                c = (char) (c + '0');
            }
            pStream.write(c);

            c = (char) (x & 0xf);
            if (c > 9) {
                c = (char) ((c - 10) + 'A');
            }
            else {
                c = (char) (c + '0');
            }
            pStream.write(c);
        }

        private void encodeAtom(OutputStream o, byte buf[], int off, int len) throws IOException {
            thisLine[currentByte] = buf[off];
            hexDigit(buf[off]);
            pStream.print(" ");
            currentByte++;
            if (currentByte == 8) {
                pStream.print("  ");
            }
        }

        private void encodeLineSuffix(OutputStream o) throws IOException {
            if (thisLineLength < 16) {
                for (int i = thisLineLength; i < 16; i++) {
                    pStream.print("   ");
                    if (i == 7) {
                        pStream.print("  ");
                    }
                }
            }

            pStream.print(" ");

            for (int i = 0; i < thisLineLength; i++) {
                if ((thisLine[i] < ' ') || (thisLine[i] > 'z')) {
                    pStream.print(".");
                }
                else {
                    pStream.write(thisLine[i]);
                }
            }

            pStream.println();
            offset += thisLineLength;
        }
    }

    private static class CertificateInfo {

        private X509Certificate cert;

        CertificateInfo(X509Certificate cert) {
            this.cert = cert;
        }

        public X509Certificate getCertificate() {
            return cert;
        }

        /**
         * Extrace CN from DN in the certificate.
         */
        private String extractAliasName(X509Certificate cert) {
            String subjectName = "Unknown subject";
            String issuerName = "Unknown issuer";

            // Extract CN from the DN for each certificate
            try {
                Principal principal = cert.getSubjectDN();
                Principal principalIssuer = cert.getIssuerDN();

                // Extract subject name
                String subjectDNName = principal.getName();
                String issuerDNName = principalIssuer.getName();

                // Extract subject name
                subjectName = extractFromQuote(subjectDNName, "CN=");

                if (subjectName == null) {
                    subjectName = extractFromQuote(subjectDNName, "O=");
                }

                if (subjectName == null) {
                    subjectName = "Unknown subject";
                }

                // Extract issuer name
                issuerName = extractFromQuote(issuerDNName, "CN=");

                if (issuerName == null) {
                    issuerName = extractFromQuote(issuerDNName, "O=");
                }

                if (issuerName == null) {
                    issuerName = "Unknown issuer";
                }
            }
            catch (Exception e) {
                // ignore
            }

            return subjectName + " (" + issuerName + ")";
        }

        private String extractFromQuote(String s, String prefix) {
            if (s == null) {
                return null;
            }

            // Search for issuer name
            int x = s.indexOf(prefix);
            int y = 0;

            if (x >= 0) {
                x = x + prefix.length();

                // Search for quote
                if (s.charAt(x) == '\"') {
                    // if quote is found, search another quote

                    // skip the first quote
                    x = x + 1;

                    y = s.indexOf('\"', x);
                }
                else {
                    // quote is not found, search for comma
                    y = s.indexOf(',', x);
                }

                if (y < 0) {
                    return s.substring(x);
                }
                else {
                    return s.substring(x, y);
                }
            }
            else {
                // No match
                return null;
            }
        }

        public String toString() {
            return extractAliasName((X509Certificate) cert);
        }
    }
}