/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
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
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A SubCrawler Implementation working with ZIP archives.
 */
public class ZipSubCrawler implements SubCrawler {
    
    /**
     * A key used within the AccessData to connect an entry with the date of the last modification.
     */
    private static final String LAST_MODIFIED_DATE = "lastModified";
    
    private boolean stopRequested = false;
    
    /**
     * @see SubCrawler#subCrawl(URI, InputStream, SubCrawlerHandler, DataSource, AccessData, Charset, String, RDFContainer)
     */
    public void subCrawl(URI id, InputStream stream, SubCrawlerHandler handler, DataSource dataSource,
            AccessData accessData, Charset charset, String mimeType, RDFContainer parentMetadata) throws SubCrawlerException {

        stopRequested = false;
        
        if (stream == null) {
            throw new SubCrawlerException("The stream cannot be null");
        } else if (handler == null) {
            throw new SubCrawlerException("The SubCrawlerHandler cannot be null");
        }
        else if (parentMetadata == null) {
            throw new SubCrawlerException("The parentMetadata cannot be null");
        }

        ZipInputStream zipStream = null;
        try {
            zipStream = new ZipInputStream(stream);
            ZipEntry zipEntry = null;
            
            parentMetadata.add(RDF.type, NFO.Archive);
            
            while ((zipEntry = zipStream.getNextEntry()) != null && ! stopRequested) {
                try {
                    processSingleEntry(zipStream,zipEntry,stream,handler,dataSource,accessData,parentMetadata);
                } finally {
                    zipStream.closeEntry();
                }
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
        } finally {
            closeClosable(zipStream);
        }
    }

    private void processSingleEntry(ZipInputStream zipStream, ZipEntry zipEntry, InputStream stream,
            SubCrawlerHandler handler, DataSource dataSource, AccessData accessData,
            RDFContainer parentMetadata) {
        URI uri = parentMetadata.getModel().createURI(
            parentMetadata.getDescribedUri().toString() + "/" + zipEntry.getName());
        boolean newEntry = true;
        if (accessData != null && accessData.isKnownId(uri.toString())) {
            newEntry = false;
        }
        String lastModifiedDateString = null;
        if (!newEntry
                && (lastModifiedDateString = accessData.get(uri.toString(), LAST_MODIFIED_DATE)) != null) {
            try {
                long lastModifiedDate = Long.parseLong(lastModifiedDateString);
                long currentModifiedDate = zipEntry.getTime();
                if (lastModifiedDate == currentModifiedDate) {
                    handler.objectNotModified(uri.toString());
                    return;
                }
            }
            catch (NumberFormatException e) {
                // this is not a problem, just ignore it and report the file as modified
            }
        }

        RDFContainerFactory fac = handler.getRDFContainerFactory(uri.toString());
        RDFContainer container = fac.getRDFContainer(uri);
        container.add(RDF.type, NFO.ArchiveItem);
        container.add(NIE.isPartOf, parentMetadata.getDescribedUri());
        String name = zipEntry.getName();
        if (name.equals("/")) {
            container.add(NFO.fileName, name);
        } else if (name.endsWith("/")) {
            // a special case for folder entries, whose names end with a slash 
            container.add(NFO.fileName, name.substring(name.lastIndexOf('/',name.length()-1)));
        } else {
            container.add(NFO.fileName, name.substring(name.lastIndexOf('/',name.length())));
        }
        
        if (zipEntry.getComment() != null) {
            container.add(NIE.comment, zipEntry.getComment());
        }
        if (zipEntry.getCompressedSize() != -1) {
            container.add(NFO.fileSize, zipEntry.getCompressedSize());
        }
        if (zipEntry.getCrc() != -1) {
            Model model = container.getModel();
            Resource hashResource = UriUtil.generateRandomResource(model);
            model.addStatement(hashResource, RDF.type, NFO.FileHash);
            model.addStatement(hashResource, NFO.hashAlgorithm, "CRC-32");
            model.addStatement(hashResource, NFO.hashValue, String.valueOf(zipEntry.getCrc()));
            model.addStatement(container.getDescribedUri(), NFO.hasHash, hashResource);
        }

        DataObject object = new FileDataObjectBase(container.getDescribedUri(), dataSource, container,
                new UnclosableStream(stream));

        if (newEntry) {
            handler.objectNew(object);
        }
        else {
            handler.objectChanged(object);
        }

        // the uncompressed size property has been left out due to a glitch in NFO, which
        // only allows the uncompressedSize property on an Archive, not an ArchiveItem
        // zipEntry.getSize();
    }

    private void closeClosable(ZipInputStream zipStream) {
        if (zipStream != null) {
            try {
                zipStream.close();
            } catch (Exception e) {
                // there is hardly anything we can do about it now
            }
        }
        
    }

    public void stopSubCrawler() {
        stopRequested = true;
    }
    
    private class UnclosableStream extends FilterInputStream {

        protected UnclosableStream(InputStream in) {
            super(in);
        }
        
        public void close() {
            //swallow the call to close(), do not close the underlying stream
        }
    }
}
