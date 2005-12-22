/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.imapcrawler;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class FolderPanel extends JPanel {

    private static final String WAIT_MESSAGE = "Retrieving folders, please wait...";

    private static final String ERROR_MESSAGE = "An error occurred while retrieving the folders.";

    private static final Comparator FOLDER_COMPARATOR = new FolderComparator();
    
    private JLabel explanationLabel = null;

    private JScrollPane treePane = null;

    private JTree folderTree = null;

    /**
     * This is the default constructor
     */
    public FolderPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        gridBagConstraints.gridy = 0;
        explanationLabel = new JLabel();
        explanationLabel.setText("Please select the mail folder to crawl:");
        this.setLayout(new GridBagLayout());
        this.setSize(300, 200);
        this.setName("folder selection");
        this.add(explanationLabel, gridBagConstraints);
        this.add(getTreePane(), gridBagConstraints1);
    }

    /**
     * This method initializes treePane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTreePane() {
        if (treePane == null) {
            treePane = new JScrollPane();
            treePane.setViewportView(getFolderTree());
        }
        return treePane;
    }

    /**
     * This method initializes folderTree
     * 
     * @return javax.swing.JTree
     */
    private JTree getFolderTree() {
        if (folderTree == null) {
            folderTree = new JTree();
            folderTree.setModel(null);
            folderTree.setShowsRootHandles(true);
            folderTree.setCellRenderer(new FolderRenderer());
            folderTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        return folderTree;
    }

    public void setStore(Store store) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(WAIT_MESSAGE);
        DefaultTreeModel model = new DefaultTreeModel(root);
        getFolderTree().setModel(model);

        Thread loaderThread = new LoaderThread(store);
        loaderThread.setPriority(Thread.MIN_PRIORITY);
        loaderThread.start();
    }

    public boolean checkInputComplete() {
        TreePath selection = getFolderTree().getSelectionPath();
        if (selection == null) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this),
                    "Please select a mail folder.", "Incomplete input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
            return true;
        }
    }

    public String getFolder() {
        TreePath selection = getFolderTree().getSelectionPath();
        if (selection == null) {
            return null;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection.getLastPathComponent();
        Folder folder = (Folder) node.getUserObject();
        return folder.getFullName();
    }

    private class LoaderThread extends Thread {

        private Store store;

        public LoaderThread(Store store) {
            this.store = store;
        }

        public void run() {
            DefaultMutableTreeNode rootNode;

            try {
                Folder rootFolder = store.getDefaultFolder();
                rootNode = createTree(rootFolder);
                store.close();

            }
            catch (MessagingException e) {
                rootNode = new DefaultMutableTreeNode(ERROR_MESSAGE);
                e.printStackTrace();
            }

            final DefaultMutableTreeNode finalNode = rootNode;
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    DefaultTreeModel model = new DefaultTreeModel(finalNode);

                    JTree tree = getFolderTree();
                    tree.setModel(model);
                    tree.setRootVisible(!finalNode.toString().equals(""));
                    
                    if (tree.getRowCount() > 0) {
                        tree.expandRow(0);
                        tree.setSelectionRow(0);
                    }

                    setCursor(null);
                }
            });
        }

        private DefaultMutableTreeNode createTree(Folder rootFolder) throws MessagingException {
            DefaultMutableTreeNode result = new DefaultMutableTreeNode(rootFolder);
            createTree(rootFolder, result);
            return result;
        }

        private void createTree(Folder folder, DefaultMutableTreeNode parentNode) throws MessagingException {
            // retrieve all subfolders
            Folder[] subFolders = folder.list();
            
            // sort them alphabetically
            Arrays.sort(subFolders, FOLDER_COMPARATOR);
            
            // create subtrees for them
            for (int i = 0; i < subFolders.length; i++) {
                Folder subFolder = subFolders[i];

                if (subFolder.exists() && subFolder.isSubscribed()) {
                    DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(subFolder);
                    parentNode.add(subNode);

                    createTree(subFolder, subNode);
                }
            }
        }
    }

    private static class FolderRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            FolderRenderer result = (FolderRenderer) super.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            if (userObject == WAIT_MESSAGE || userObject == ERROR_MESSAGE) {
                result.setIcon(null);
            }
            else {
                if (userObject instanceof Folder) {
                    result.setText(((Folder) userObject).getName());
                }

                if (leaf) {
                    result.setIcon(result.getClosedIcon());
                }
            }

            return result;
        }
    }
    
    private static class FolderComparator implements Comparator {

        private Collator collator = Collator.getInstance();
        
        public int compare(Object object1, Object object2) {
            String name1 = ((Folder) object1).getName();
            String name2 = ((Folder) object2).getName();
            return collator.compare(name1, name2);
        }
    }
}
