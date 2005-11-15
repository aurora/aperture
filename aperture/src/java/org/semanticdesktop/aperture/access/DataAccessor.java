/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.access;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.semanticdesktop.aperture.datasource.DataObject;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A DataAccessor provides access to physical resources by creating DataObjects representing the
 * resource, based on a url and optionally data about a previous access and other parameters.
 * 
 * <p>
 * The optionally passed Date can be used to instruct the DataAccessor to only create and return a
 * DataObject when its "date" (typically a last modified date, depending on the scheme) differs from the
 * specified date. This facilitates the creation of fast incremental Crawlers.
 * 
 * <p>
 * The ID of the returned DataObject may differ from the specified URL, based on normalization, following
 * redirected URLs, etc. It is required though to provide a URI through which this DataAccessor can later
 * on also access the same resource, i.e. the URI should also be a URL.
 */
public interface DataAccessor {

    /**
     * Get a DataObject for the specified url. The resulting DataObject's ID may differ from the
     * specified url due to normalization schemes, following of redirected URLs, etc.
     * 
     * <p>
     * A Date can optionally be specified, indicating that the DataObject is only wanted when its regular
     * date (typically a last modified date) differs from the specified date. When no Date is specified,
     * a DataObject is always returned.
     * 
     * <p>
     * Specific DataAccessor implementations may accept additional parameters through the params Map,
     * e.g. to speed up this method with ready-made datastructures it can reuse. See the documentation of
     * these implementations for information on the type of parameters they accept. However,
     * implementations should not rely on the contents of this Map to work properly.
     * 
     * @param url The url of the requested resource.
     * @param dataSource The DataSource that will be registered as the source of the DataObject.
     * @param previousDate The previous date of the DataObject (optional).
     * @param params Additional parameters facilitating access to the physical resource (optional).
     * @return A DataObject for the specified URI, or null when the binary resource has not been modified
     *         since the last access.
     * @throws UrlNotFoundException When the specified url did not point to an existing resource.
     * @throws IOException When any kind of I/O error occurs.
     */
    public DataObject get(String url, DataSource source, Date previousDate, Map params)
            throws UrlNotFoundException, IOException;
}
