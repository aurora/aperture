/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.addressbook.thunderbird;

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
public class ThunderbirdAddressbookDataSourceFactory implements DataSourceFactory {

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////// CONSTANTS ////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /** The path to the resource containing the ICAL_DESCRIPTION */
    private static final String ADDRESSBOOK_DESCRIPTION = ThunderbirdAddressbookDataSourceFactory.class
            .getPackage().getName().replace('.', '/')
            + "/ThunderbirdAddressbookDataSource.rdf";

    /** The syntax the ADDRESSBOOK_DESCRIPTION is expressed in */
    private static final Syntax ADDRESSBOOK_SYNTAX = Syntax.Turtle;

    //////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////// METHODS FROM THE DATA SOURCE FACTORY INTERFACE ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
    
	public URI getSupportedType() {
        return DATASOURCE_GEN.AddressbookDataSource;
    }

    public DataSource newInstance() {
        return new ThunderbirdAddressbookDataSource();
    }

	public boolean getDescription(Model model) {
		InputStream stream = null;
		try {
			stream = ResourceUtil.getInputStream(ADDRESSBOOK_DESCRIPTION, 
                ThunderbirdAddressbookDataSourceFactory.class);
			model.readFrom(stream,ADDRESSBOOK_SYNTAX);
			return true;
		} catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
			logger.warn("Could not return the description of ICal data source",e);
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
