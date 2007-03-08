/*
 * Copyright (c) 2005 - 2007 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.outlook;

import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE_GEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Microsoft Outlook Datasources.
 */
public class OutlookDataSourceFactory implements DataSourceFactory {

    // ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////// CONSTANTS ////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    /** The path to the resource containing the ICAL_DESCRIPTION */
    private static final String OUTLOOK_DESCRIPTION = OutlookDataSourceFactory.class.getPackage().getName()
            .replace('.', '/')
            + "/outlookDataSource.rdf";

    /** The syntax the OUTLOOK_DESCRIPTION is expressed in */
    private static final Syntax OUTLOOK_SYNTAX = Syntax.Turtle;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////// METHODS FROM THE DATA SOURCE FACTORY INTERFACE ///////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    public URI getSupportedType() {
        return DATASOURCE_GEN.MicrosoftOutlookDataSource;
    }

    public DataSource newInstance() {
        return new OutlookDataSource();
    }

    public boolean getDescription(Model model) {
        InputStream stream = null;
        try {
            stream = OutlookDataSourceFactory.class.getClassLoader().getResourceAsStream(OUTLOOK_DESCRIPTION);
            model.readFrom(stream, OUTLOOK_SYNTAX);
            return true;
        }
        catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.warn("Couldn't return the description of ICal data source", e);
            return false;
        }
        finally {
            closeStream(stream);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////// UTILITY METHODS ////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void closeStream(InputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        }
        catch (IOException ioe) {
            // we can hardly do anything at the moment
        }
    }
}
