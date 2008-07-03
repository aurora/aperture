/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.detector;

import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.datasource.DataSourceFactory;

/**
 * Detects a possible datasource on the desktop of the currently logged in
 * user. Returns the detected datasource(s) using the DataSourceDescription
 * class.
 * Intended to be used by Desktop search engines to propose possible datasources
 * that users can index.
 * 
 * @since 1.1.1
 * @author sauermann, 2.7.2008 based on work by gunnar grimnes
 */
public interface DataSourceDetector {
    
    /**
     * Returns the URI of the supported DataSource type.
     * @see DataSourceFactory#getSupportedType()
     */
    public URI getSupportedType();
    
    /**
     * Detect one or more datasources. returns an empty list if nothing detected 
     * @return a list of detected datasources. May be empty, but never <code>null</code>
     * @throws Exception if there is a severe problem when detecting the datasource.
     * The method will only throw an Exception, if it can usually detect a datasource,
     * but is not able to detect it due to a faulty environment. 
     * For example, if the operating system is Apple, but the Address book was removed,
     * or if Microsoft Outlook is detected, but in a version that is unknown.
     * Error messages must be readable by the end-user and give hints how to 
     * add the datasource by hand or how to fix the problem. 
     */
     public List<DataSourceDescription> detect() throws Exception;
}

