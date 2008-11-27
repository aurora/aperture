/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.opener.email;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.opener.DataOpener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.com.NotImplementedException;

/**
 * Supported URI's:
 *
 *  email://MSGID
 *  msgid://MSGID
 *  imap://
 */
public class EmailOpener implements DataOpener {

    static private Logger logger = LoggerFactory.getLogger(EmailOpener.class);;

    private static String MSGID = "msgid://" ;
    
    //Hardcoded in the Thunderbird Plugin
    int port = 10802;
    String host = "localhost";

    
    public void open(URI uri) throws IOException {
            openThunderbirdEmail(uri);
    }

    /**
     * Thunderbird plugin supports "imap://" and "msgid://".
     * Uri, wchich starts with "email://" will be converted to "msgid://"
     * @param uri
     * @throws IOException
     */
    private void openThunderbirdEmail(URI uri) throws IOException {
        
        String url = uri.toString();
        if (!url.startsWith("imap://") && !url.startsWith("msgid://") && !url.startsWith("email://"))
            throw new IOException("Email URI is not valid.");

        String correctURI=url;
        if(url.startsWith("email://")){ 
            correctURI = url.replaceFirst("email://", "msgid://");
        }
        
        if(url.startsWith("imap://")){ 
           throw new NotImplementedException("Imap URI not implemented in Aperture EmailOpener");
        }
        openCommand(correctURI);
    }

    public void openCommand(String uri) throws IOException{ 
        try
        {
            Socket sock = null;
            // open socket
            sock = new Socket(host, port);
            
            OutputStream out = sock.getOutputStream();
            String message = "open " + uri + " \n";
            out.write(message.getBytes());
            sock.close();
        } catch (Exception e)
        {
            logger.warn("Could not open the email uri: " + uri + " " + e,e);
            throw new IOException("Error opening uri " + uri + ": " + e.toString());
        }

    }

}

