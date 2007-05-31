/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.opener;

import java.util.Set;


public interface DataOpenerFactory {
	  /**
     * Returns all schemes supported by the DataOpener returned by this DataOpenerFactory.
     * 
     * @return A Set of Strings.
     */
    public Set getSupportedSchemes();

    /**
     * Returns a DataOpener instance for accessing the represented schemes.
     * 
     * @return A DataOpener instance.
     */
    public DataOpener get();
}

