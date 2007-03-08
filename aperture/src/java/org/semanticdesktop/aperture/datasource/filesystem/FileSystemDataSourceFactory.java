/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.filesystem;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImapDataSourceFactory returns instances of the ImapDataSource class. 
 */
public class FileSystemDataSourceFactory implements DataSourceFactory {

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////// CONSTANTS ////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The path to the resource containing the ICAL_DESCRIPTION */
    private static final String FILESYSTEM_DESCRIPTION = FileSystemDataSourceFactory.class.getPackage()
    		.getName().replace('.', '/') + "/filesystemDataSource.rdf";
    
    /** The syntax the IMAP_DESCRIPTION is expressed in */
    private static final Syntax FILESYSTEM_SYNTAX = Syntax.Turtle;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// METHODS FROM THE DATA SOURCE FACTORY INTERFACE ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
    
	public URI getSupportedType() {
        return DATASOURCE_GEN.FileSystemDataSource;
    }

    public DataSource newInstance() {
        return new FileSystemDataSource();
    }

	public boolean getDescription(Model model) {
		InputStream stream = null;
		try {
			stream = ResourceUtil.getInputStream(FILESYSTEM_DESCRIPTION, FileSystemDataSourceFactory.class);
			model.readFrom(stream,FILESYSTEM_SYNTAX);
			return true;
		} catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
			logger.error("Couldn't return the description of ICal data source",e);
			return false;
		} finally {
			closeStream(stream);
		}
	}
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////// UTILITY METHODS ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void closeStream(InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException ioe) {
			// we can hardly do anything at the moment
		}
	}
}
