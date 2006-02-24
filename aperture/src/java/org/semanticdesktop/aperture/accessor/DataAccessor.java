/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor;

import java.io.IOException;
import java.util.Map;

import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A DataAccessor provides access to physical resources by creating DataObjects representing the
 * resource, based on a url and optionally previous acces data and other parameters.
 */
public interface DataAccessor {

    /**
     * Get a DataObject for the specified url.
     * 
     * <p>
     * The resulting DataObject's ID may differ from the specified url due to normalization schemes,
     * following of redirected URLs, etc. It is required though to provide a URI through which this
     * DataAccessor can later on also access the same resource, i.e. the URI should also be a URL.
     * 
     * <p>
     * Specific DataAccessor implementations may accept additional parameters through the params Map,
     * e.g. to speed up this method with ready-made datastructures it can reuse. See the documentation of
     * these implementations for information on the type of parameters they accept. However,
     * implementations should not rely on the contents of this Map to work properly.
     * 
     * @param url The url of the requested resource.
     * @param source The DataSource to be registered as the source of the DataObject (optional).
     * @param params Additional parameters facilitating access to the physical resource (optional).
     * @param containerFactory An RDFContainerFactory that delivers the RDFContainer to which the
     *            metadata of the DataObject should be added. The provided RDFContainer can later be
     *            retrieved as the DataObject's metadata container.
     * @return A DataObject for the specified URI.
     * @throws UrlNotFoundException When the specified url did not point to an existing resource.
     * @throws IOException When any kind of I/O error occurs.
     */
    public DataObject getDataObject(String url, DataSource source, Map params,
            RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException;

    /**
     * Get a DataObject for the specified url.
     * 
     * <p>
     * The resulting DataObject's ID may differ from the specified url due to normalization schemes,
     * following of redirected URLs, etc. It is required though to provide a URI through which this
     * DataAccessor can later on also access the same resource, i.e. the URI should also be a URL.
     * 
     * <p>
     * The optionally passed AccessData can be used to let the DataAccessor store information about the
     * created DataSource. The next time it is invoked with the same URL, it can then use this
     * information to determine whether the resource has changed or not. The DataAccessor should return
     * null when the resource has not changed. This facilitates fast incremental crawling of DataSources.
     * When no AccessData is specified, no change detection takes place and an AccessData is always
     * returned.
     * 
     * <p>
     * Specific DataAccessor implementations may accept additional parameters through the params Map,
     * e.g. to speed up this method with ready-made datastructures it can reuse. See the documentation of
     * these implementations for information on the type of parameters they accept. However,
     * implementations should not rely on the contents of this Map to work properly.
     * 
     * @param url The url of the requested resource.
     * @param source The DataSource to be registered as the source of the DataObject (optional).
     * @param accessData Any access data obtained during the previous access to this DataObject
     *            (optional).
     * @param params Additional parameters facilitating access to the physical resource (optional).
     * @param containerFactory An RDFContainerFactory that delivers the RDFContainer to which the
     *            metadata of the DataObject should be added. The provided RDFContainer can later be
     *            retrieved as the DataObject's metadata container.
     * @return A DataObject for the specified URI, or null when the binary resource has not been modified
     *         since the last access.
     * @throws UrlNotFoundException When the specified url did not point to an existing resource.
     * @throws IOException When any kind of I/O error occurs.
     */
    public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
            Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException;
}
