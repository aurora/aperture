/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.imap;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;

/**
 * ImapDataSourceFactory returns instances of the ImapDataSource class. 
 */
public class ImapDataSourceFactory implements DataSourceFactory {

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////// CONSTANTS ////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** The Logger */
	private static final Logger log = Logger.getLogger(ImapDataSourceFactory.class.getName());
	
	/** The path to the resource containing the IMAP_DESCRIPTION */
    private static final String IMAP_DESCRIPTION = ImapDataSourceFactory.class.getPackage().getName()
    		  .replace('.', '/') + "/imapDataSource.rdf";
    
    /** The syntax the IMAP_DESCRIPTION is expressed in */
    private static final Syntax IMAP_SYNTAX = Syntax.Turtle;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// METHODS FROM THE DATA SOURCE FACTORY INTERFACE ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
    
	public URI getSupportedType() {
        return DATASOURCE_GEN.IMAPDataSource;
    }

    public DataSource newInstance() {
        return new ImapDataSource();
    }

	public boolean getDescription(Model model) {
		InputStream stream = null;
		try {
			stream = ImapDataSourceFactory.class.getClassLoader().getResourceAsStream(IMAP_DESCRIPTION);
			model.readFrom(stream, IMAP_SYNTAX);
			return true;
		} catch (Exception e) {
			log.log(Level.WARNING,"Couldn't return the description of IMAP data source",e);
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
