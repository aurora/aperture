/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.zip;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
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
import org.semanticdesktop.aperture.accessor.base.FolderDataObjectBase;
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
                    processSingleEntry(zipStream,zipEntry,handler,dataSource,accessData,parentMetadata);
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

    private void processSingleEntry(ZipInputStream zipStream, ZipEntry zipEntry, 
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
                if (currentModifiedDate != -1 && lastModifiedDate == currentModifiedDate) {
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
        container.add(NFO.fileName,getFileName(zipEntry));
        
        String superfolder = getSuperfolder(zipEntry);
        if (superfolder != null) {
            URI superfolderUri = parentMetadata.getModel().createURI(
                parentMetadata.getDescribedUri().toString() + "/" + superfolder);
            container.add(NFO.belongsToContainer, superfolderUri);
        } else {
            container.add(NFO.belongsToContainer, parentMetadata.getDescribedUri());
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
        if (zipEntry.getTime() != -1) {
            container.add(NFO.fileLastModified, new Date(zipEntry.getTime()));
            if (accessData != null) {
                accessData.put(uri.toString(), LAST_MODIFIED_DATE, String.valueOf(zipEntry.getTime()));
            }
        }

        
        
        DataObject object = null;
        
        if (zipEntry.isDirectory()) {
            container.add(RDF.type, NFO.Folder);
            object = new FolderDataObjectBase(container.getDescribedUri(), dataSource, container);
        } else {
            object = new FileDataObjectBase(container.getDescribedUri(), dataSource, container,
                    new UnclosableStream(zipStream));
        }

        if (newEntry) {
            handler.objectNew(object);
        }
        else {
            handler.objectChanged(object);
        }        
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
    
    private String getFileName(ZipEntry entry) {
        String name = entry.getName();
        String fileName = null;
        if (name.equals("/")) {
            fileName = name;
        } else if (name.endsWith("/")) {
            // a special case for folder entries, whose names end with a slash 
            int lastSlash = name.lastIndexOf('/',name.length()-2);
            if (lastSlash == -1) {
                // this happens for folders directly beneath the root of the archive
                // the name is like 'zip-test/' - slash at the end, no slash at the beginning
                // we only need to cut off the last slash
                fileName = name.substring(0,name.length() - 1);
            } else {
                // this happens for folders nested deeper within the archive tree
                // the name is like 'zip-test/subfolder/', we need to cut off the
                // initial portion 'zip-test/' and the last slash
                fileName = name.substring(lastSlash + 1, name.length() - 1);
            }
        } else {
            // normal files, whose names don't end with a hash
            int lastSlash = name.lastIndexOf('/',name.length()-1);
            if (lastSlash == -1) {
                // this happens for files directly beneath the root of the archive
                // the name is like 'file.txt' - no slash at the end, no slash at the beginning
                // we need to return the name itself
                fileName = name;
            } else {
                // this happens for files nested deeper within the archive tree
                // the name is like 'zip-test/file1.txt', we need to cut off the
                // initial portion 'zip-test/' 
                fileName = name.substring(lastSlash + 1, name.length());
            }
        }
        return fileName;
    }
    
    private String getSuperfolder(ZipEntry entry) {
        String name = entry.getName();
        String superfolderName = null;
        if (name.equals("/")) {
            superfolderName = null;
        } else if (name.endsWith("/")) {
            // a special case for folder entries, whose names end with a slash 
            int lastSlash = name.lastIndexOf('/',name.length()-2);
            if (lastSlash == -1) {
                // this happens for folders directly beneath the root of the archive
                // the name is like 'zip-test/' - they don't have any superfolder
                superfolderName = null;
            } else {
                // this happens for folders nested deeper within the archive tree
                // the name is like 'zip-test/subfolder/', we need to cut off the
                // trailing portion 'subfolder/' 
                superfolderName = name.substring(0, lastSlash + 1);
            }
        } else {
            // normal files, whose names don't end with a hash
            int lastSlash = name.lastIndexOf('/',name.length()-1);
            if (lastSlash == -1) {
                // this happens for files directly beneath the root of the archive
                // the name is like 'file.txt' - they have no superfolder
                superfolderName = null;
            } else {
                // this happens for files nested deeper within the archive tree
                // the name is like 'zip-test/file1.txt', we need to cut off the
                // trailing portion 'file.txt' 
                superfolderName = name.substring(0, lastSlash + 1);
            }
        }
        return superfolderName;
    }

    public void stopSubCrawler() {
        stopRequested = true;
    }
    
    private class UnclosableStream extends FilterInputStream {

        protected UnclosableStream(InputStream in) {
            super(new BufferedInputStream(in));
        }
        
        public void close() {
            //swallow the call to close(), do not close the underlying stream
        }
    }
}
