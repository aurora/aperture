/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.filebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.plaintext.PlainTextExtractorFactory;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerSesame;

/**
 * holding data for the example browser
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class FileBrowserData {
    
    private File file;
    private RDFContainerSesame data;
    String mimetype;

    /**
     * load the metadata from the passed file
     * TODO do the mimetype and extractor registry magic here
     * @param file the file to open
     * @throws FileNotFoundException 
     * @throws ExtractorException when the extraction fails for some reason
     */
    public void loadFile(File file) throws ExtractorException, FileNotFoundException {
        this.file = file;
        // guess mime
        mimetype = "text/plain";
        // access file
        FileInputStream fin = new FileInputStream(file);
        // get Extractor
        Extractor extractor = new PlainTextExtractorFactory().get();
        URI uri = file.toURI();
        data = new RDFContainerSesame(uri);
        extractor.extract(uri, fin, null, mimetype, data);
    }
    
    public RDFContainerSesame getRDF() {
        return data;
    }
    
    public File getFile() {
        return file;
    }
    
    public String getMimetype() {
        return mimetype;
    }

}

