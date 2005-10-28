/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class GuiUtil {

    public static final Logger log = Logger.getLogger(GuiUtil.class.getName());

    /**
     * Show this exception to the user. reading exceptions builds character.
     * 
     * @param x the exception
     */
    public static void showException(Throwable e)
    {
        log.fine("user exception: " + e.toString());
        if (log.isLoggable(Level.FINE))
            e.printStackTrace();
        JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show this exception to the user. reading exceptions builds character.
     * 
     * @param e
     *            the exception
     * @param parentWindow
     *            the window that caused the exception
     */
    public static void showException(Throwable e, Component parentWindow)
    {
        log.fine("user exception: " + e.toString());
        if (log.isLoggable(Level.FINE))
            e.printStackTrace();
        JOptionPane.showMessageDialog(parentWindow, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
