/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener;

import java.io.IOException;

import org.ontoware.rdf2go.model.node.URI;

/**
 * A DataOpener opens a URI in its native application, e.g. an editor, web browser or mail reader.
 * DataOpeners are typically scheme-dependent, e.g. a "file:" DataOpener may immediately open the
 * indicated File in the application registered with the platform whereas a "http:" DataOpener may open a
 * web browser.
 */
public interface DataOpener {

    /**
     * Opens the resource indicated by the specified URI in the application that the user typically would
     * use to access such a resource.
     * 
     * @param uri The uri of the resource that should be opened
     * @throws IOException In case of an I/O error.
     */
    public void open(URI uri) throws IOException;
}
