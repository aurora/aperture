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
import org.semanticdesktop.aperture.subcrawler.tar.TarSubCrawlerFactory;
import org.semanticdesktop.aperture.vocabulary.NFO;

/**
 * A SubCrawler Implementation working with compressors.
 */
public abstract class AbstractCompressorSubCrawler implements SubCrawler {
    
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
            
            /*
             * this is a really unelegant hack, it is intended to cover the very popular case of
             * .tar.gz and .tar.bz2 archives. Without it, the compressor would return a stream
             * with an uncompressed tar file. Unfortunately the tar format doesn't have any
             * magic header and the mime type identifier would not be able to recognize it. 
             * In a positive case, the mime type identifier would fall back to the extension-based
             * recognition, which would regonize the .tar extension (if the user didn't forget to
             * pass the URI to the mime type identifier). 
             * In the worst case the name of the 'root' folder of the archive would start at the
             * very first byte of the tar file and the mime type identifier would treat it as
             * the magic prefix. This means that a tar file containing the 'pkutils' folder would
             * be identifier as a zip file (or any other mime type identifier by a magic string).
             * This would obviously lead to a catastrophe. That's why I introduced this hack in hope
             * that it will filter out the most common cases, and tread them correctly.
             */
            if (contentUri.toString().endsWith(".tar")) {
                TarSubCrawlerFactory fac = new TarSubCrawlerFactory();
                SubCrawler sc = fac.get();
                sc.subCrawl(id, uncompressedStream, handler, dataSource, accessData, charset, mimeType, parentMetadata);
                return;
            }
            
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
    
    // this should actually be the responsibility of the concrete compressors
    // it may be moved downwards in future...
    private URI getContentUri(URI archiveUri) {
        String uriString = archiveUri.toString();
        if (uriString.endsWith(".gz")) {
            // cut of '.gz' from the end
            return new URIImpl(uriString.substring(0,uriString.length() - 3));
        } else if (uriString.endsWith(".bz2")) {
            // cut of '.gz' from the end
            return new URIImpl(uriString.substring(0,uriString.length() - 4));
        } else if (uriString.endsWith(".tgz") || uriString.endsWith(".tbz")) {
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
