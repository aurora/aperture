/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataAccessor;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.HttpClientUtil;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.util.UrlUtil;

/**
 * A DataAccessor implementation for the http and https schemes.
 */
public class HttpAccessor implements DataAccessor {

    private static final Logger LOGGER = Logger.getLogger(HttpAccessor.class.getName());

    /**
     * Key used to store that a url is known. This is necessary because not every url has a date that can
     * be stored and we still have to separate undated urls from known urls. This key has no
     * corresponding value.
     */
    private static final String ACCESSED_KEY = "accessed";

    private static final String DATE_KEY = "date";

    private static final int MAX_REDIRECTIONS = 20;

    public DataObject getDataObject(String url, DataSource source, Map params,
            RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        return get(url, source, null, params, containerFactory);
    }

    public DataObject getDataObjectIfModified(String url, DataSource source, AccessData accessData,
            Map params, RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        return get(url, source, accessData, params, containerFactory);
    }

    private DataObject get(String urlString, DataSource source, AccessData accessData, Map params,
            RDFContainerFactory containerFactory) throws UrlNotFoundException, IOException {
        // keep a backup of the originally passed url
        String originalUrlString = urlString;

        HttpURLConnection connection;
        int nrRedirections = 0;

        // We're going to loop, accessing urls until we arrive at a url that is not redirected. The
        // redirection is followed manually rather than automatically, which is HttpURLConnection's
        // default behaviour, so that we know the actual url we arrive at.
        while (true) {
            // check if we haven't been redirected too often
            if (nrRedirections > MAX_REDIRECTIONS) {
                throw new IOException("too many redirections, max = " + MAX_REDIRECTIONS + ", url = "
                        + originalUrlString);
            }

            // create an encoded URL instance
            URL url = new URL(UriUtil.encodeUri(urlString));

            // normalize the URL
            url = UrlUtil.normalizeURL(url);
            urlString = url.toExternalForm();

            // see if a date was registered for this url
            Date ifModifiedSince = getIfModifiedSince(urlString, accessData);

            // set up a connection (a HttpAccessor always has HttpURLConnections, else it's a bug)
            connection = createConnection(url, ifModifiedSince);

            // send the request to the server
            connection.connect();

            // check for http-specific response codes
            int responseCode = connection.getResponseCode();

            if (isRedirected(responseCode)) {
                // follow the redirected url
                urlString = getRedirectedUrl(url, connection);
                nrRedirections++;
            }
            else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                throw new UrlNotFoundException(urlString);
            }
            else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                // bail out ASAP
                return null;
            }
            else if (responseCode != HttpURLConnection.HTTP_OK) {
                // this is a communication error, quit with an exception
                throw new IOException("Http connection error, response code = " + responseCode + ", url = "
                        + url);
            }
            else {
                // we're done
                break;
            }
        }

        // create the actual data object
        DataObject result = createDataObject(urlString, source, connection, containerFactory);

        // register it in the access data
        if (accessData != null) {
            // make sure we always store something about this url, so that accessData.isKnownId will
            // return true
            accessData.put(urlString, ACCESSED_KEY, "");

            // also store this for the original url, so that crawlers can detect that they have accessed
            // a redirected url before (it won't be considered as being a new object, every time it is
            // accessed)
            if (nrRedirections > 0) {
                accessData.put(originalUrlString, ACCESSED_KEY, "");
            }

            // store the date with which we can check in the next access whether the object was modified
            long date = connection.getDate();
            if (date != 0L) {
                accessData.put(urlString, DATE_KEY, String.valueOf(date));
            }
        }

        return result;
    }

    private Date getIfModifiedSince(String urlString, AccessData accessData) {
        if (accessData == null) {
            return null;
        }
        else {
            String value = accessData.get(urlString, DATE_KEY);
            if (value == null) {
                return null;
            }
            
            try {
                long l = Long.parseLong(value);
                return new Date(l);
            }
            catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "invalid long: " + value, e);
                return null;
            }
        }
    }

    private HttpURLConnection createConnection(URL url, Date ifModifiedSince) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        HttpClientUtil.setAcceptGZIPEncoding(connection);
        connection.setInstanceFollowRedirects(false);
        if (ifModifiedSince != null) {
            connection.setIfModifiedSince(ifModifiedSince.getTime());
        }

        return connection;
    }

    private boolean isRedirected(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MULT_CHOICE
                || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER;
    }

    private String getRedirectedUrl(URL url, HttpURLConnection connection) throws IOException {
        String newLocation = connection.getHeaderField("Location");
        if (newLocation == null) {
            throw new IOException("missing redirection location");
        }
        else {
            return new URL(url, newLocation).toString();
        }
    }

    private DataObject createDataObject(String url, DataSource source, HttpURLConnection connection,
            RDFContainerFactory containerFactory) throws IOException {
        // create the resulting instance
        URI uri = new URIImpl(url);
        RDFContainer metadata = containerFactory.getRDFContainer(uri);
        
        InputStream stream = HttpClientUtil.getInputStream(connection);
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream, 8192);
        }
        
        DataObject object = new FileDataObjectBase(uri, source, metadata, stream);

        // populate the metadata
        String characterSet = null;
        String mimeType = null;

        String contentType = connection.getContentType();
        if (contentType != null) {
            ContentType parsedType = new ContentType(contentType);
            characterSet = parsedType.getCharset();
            mimeType = parsedType.getMimeType();
        }

        if (characterSet == null) {
            // character set defaults to ISO-8859-1 (Latin-1) for HTTP
            characterSet = "ISO-8859-1";
        }

        metadata.put(Vocabulary.CHARACTER_SET, characterSet);
        
        if (mimeType != null) {
            metadata.put(Vocabulary.MIME_TYPE, mimeType);
        }

        long contentLength = connection.getContentLength();
        if (contentLength >= 0l) {
            metadata.put(Vocabulary.BYTE_SIZE, contentLength);
        }
        
        long retrieved = connection.getDate();
        if (retrieved != 0L) {
            metadata.put(Vocabulary.RETRIEVAL_DATE, new Date(retrieved));
        }

        long lastModified = connection.getLastModified();
        if (lastModified != 0L) {
            metadata.put(Vocabulary.DATE, new Date(lastModified));
        }

        long expires = connection.getExpiration();
        if (expires != 0L) {
            metadata.put(Vocabulary.EXPIRATION_DATE, new Date(expires));
        }

        return object;
    }
}
