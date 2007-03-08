/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A trivial default implementation of FileDataObject. 
 */
public class FileDataObjectBase extends DataObjectBase implements FileDataObject {

    private InputStream content;
    
    public FileDataObjectBase() { }
    
    public FileDataObjectBase(URI id, DataSource dataSource, RDFContainer metadata, InputStream content) {
        super(id, dataSource, metadata);
        setContent(content);
    }
    
    public void finalize() throws Throwable {
        try {
            // Just try to close the InputStream once more: can remedy nasty programming errors.
            // Not documented in the Javadoc as programmers shouldn't rely on this.
            if (content != null) {
                content.close();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    public void setContent(InputStream content) {
        if (content != null && !content.markSupported()) {
            throw new IllegalArgumentException("content should support mark and reset");
        }
        this.content = content;
    }
    
    public InputStream getContent() {
        return content;
    }
    
    public void dispose() {
        try {
        	if (content != null) {
        		content.close();
        	}
        }
        catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("IOException while closing stream", e);
        }
        
        super.dispose();
    }
}
