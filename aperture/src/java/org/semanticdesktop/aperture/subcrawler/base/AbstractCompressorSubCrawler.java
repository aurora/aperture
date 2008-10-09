/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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
 * A SubCrawler Implementation working with compressors.
 */
public abstract class AbstractCompressorSubCrawler extends AbstractSubCrawler {
    
    /**
     * Returns a stream that uncompresses the data
     * @param stream InputStream with the compressed data
     * @return stream with the uncompressed data
     * @throws IOException
     */
    protected abstract InputStream getUncompressedStream(InputStream stream) throws IOException;
    
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

        InputStream uncompressedStream = null;
        try {
            
            URI contentUri = getContentUri(parentMetadata.getDescribedUri());
            uncompressedStream = getUncompressedStream(stream);
            
            parentMetadata.add(RDF.type, NFO.Archive);
            
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
                        new BufferedInputStream(uncompressedStream));

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
            closeClosable(uncompressedStream);
        }
    }

    private void closeClosable(InputStream gzipStream) {
        if (gzipStream != null) {
            try {
                gzipStream.close();
            } catch (Exception e) {
                // there is hardly anything we can do about it now
            }
        }
    }
    
    /**
     * Returns the uri of the content file, this method is supposed to strip the compressor-specific suffix
     * (like .gz or .bz2). It is meant to be overridden by the concrete compressor subcrawler subclasses.
     * 
     * @param archiveUri the uri of the archive
     * @return the uri of the compressed file content
     */
    protected URI getContentUri(URI archiveUri) {
        // this method is supposed to be overridden, so either there is an error, or a subclass
        // has called super.getContentUri, therefore we invent an arbitrary extension
        String string = archiveUri.toString();
        int hashIndex = string.indexOf("/");
        if (hashIndex != -1) {
            return createChildUri(archiveUri, string.substring(hashIndex) + ".content");
        } else {
            return createChildUri(archiveUri, string + ".content");
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
