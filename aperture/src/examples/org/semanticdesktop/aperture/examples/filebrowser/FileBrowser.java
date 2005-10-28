/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.io.File;

/**
 * example file browser that can show metadata of a file
 * <pre>usage:
 * org.semanticdesktop.aperture.examples.filebrowser.FileBrowser [filename]
 * 
 * [filename] ... optional filename parameter, will show this file
 * </pre>
 * @author Sauermann
 * $Id$
 */
public class FileBrowser {
    

    /**
     * run the file browser.
     */
    public static void main(String[] args)
    {
        FileBrowserFrame browser = new FileBrowserFrame();
        if (args.length > 0)
        {
            File f = new File(args[0]);
            try
            {
                browser.loadFile(f);
            } catch (Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            } 
        }
        browser.setVisible(true);

    }
    


}

