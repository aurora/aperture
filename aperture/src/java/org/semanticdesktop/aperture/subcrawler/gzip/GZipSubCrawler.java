/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.gzip;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.FileDataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A SubCrawler Implementation working with GZIP archives.
 */
public class GZipSubCrawler implements SubCrawler {
    
    /**
     * @see SubCrawler#subCrawl(URI, InputStream, SubCrawlerHandler, DataSource, AccessData, Charset, String, RDFContainer)
     */
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata) throws SubCrawlerException {
        
        if (stream == null) {
            throw new SubCrawlerException("The stream cannot be null");
        } else if (handler == null) {
            throw new SubCrawlerException("The SubCrawlerHandler cannot be null");
        }
        else if (parentMetadata == null) {
            throw new SubCrawlerException("The parentMetadata cannot be null");
        }

        GZIPInputStream gzipStream = null;
        try {
            gzipStream = new GZIPInputStream(stream);
            
            System.out.println(parentMetadata.getDescribedUri());
            
            parentMetadata.add(RDF.type, NFO.Archive);
            
            URI contentUri = getContentUri(parentMetadata.getDescribedUri());
            
            boolean newEntry = true;
            if (accessData != null && accessData.isKnownId(contentUri.toString())) {
                newEntry = false;
            }
            // we assume that this subcrawler would not be invoked if the archived file
            // itself would be unmodified

            RDFContainerFactory fac = handler.getRDFContainerFactory(contentUri.toString());
            RDFContainer container = fac.getRDFContainer(contentUri);
            container.add(RDF.type, NFO.ArchiveItem);
            container.add(NFO.fileName,getFileName(contentUri));
            container.add(NFO.belongsToContainer, parentMetadata.getDescribedUri());            
            
            DataObject object = new FileDataObjectBase(container.getDescribedUri(), dataSource, container,
                        new BufferedInputStream(gzipStream));

            if (newEntry) {
                handler.objectNew(object);
            }
            else {
                handler.objectChanged(object);
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
        } finally {
            // the programmer should have closed the stream by disposing of the data object
            // but it won't hurt if we do it by ourselves
            closeClosable(gzipStream);
        }
    }

    private void closeClosable(GZIPInputStream gzipStream) {
        if (gzipStream != null) {
            try {
                gzipStream.close();
            } catch (Exception e) {
                // there is hardly anything we can do about it now
            }
        }
    }
    
    private URI getContentUri(URI archiveUri) {
        String uriString = archiveUri.toString();
        if (uriString.endsWith(".gz")) {
            // cut of '.gz' from the end
            return new URIImpl(uriString.substring(0,uriString.length() - 3));
        } else if (uriString.endsWith(".tgz")) {
            // cut off 'tgz' and replace it with 'tar'
            return new URIImpl(uriString.substring(0,uriString.length() - 3) + "tar");
        } else {
            // this means that the extension is wrong, we invent an arbitrary one
            return new URIImpl(uriString + ".content");
        }
    }

    private String getFileName(URI contentUri) {
        String name = contentUri.toString();
        String fileName = null;
        // normal files, whose names don't end with a hash
        int lastSlash = name.lastIndexOf('/',name.length()-1);
        if (lastSlash == -1) {
            // this happens for files directly beneath the root 
            // the name is like 'file.txt' - no slash at the end, no slash at the beginning
            // we need to return the name itself
            fileName = name;
        } else {
            // this happens for normal uris containing some slashes 
            fileName = name.substring(lastSlash + 1, name.length());
        }
        return fileName;
    }

    public void stopSubCrawler() {
        // nothing interesting, don't do anything
    }
}
