/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.base;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

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
import org.semanticdesktop.aperture.subcrawler.PathNotFoundException;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerException;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerHandler;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * A SubCrawler Implementation working with archive files, i.e. files containing a number
 * of other files. This tries to be an abstraction over all known archive systems (zip, tar etc.)
 */
public abstract class AbstractArchiverSubCrawler extends AbstractSubCrawler {
    
    /**
     * A key used within the AccessData to connect an entry with the date of the last modification.
     */
    private static final String LAST_MODIFIED_DATE = "lastModified";
    
    /**
     * A flag indicating that a stop has been requested by the client. If the SubCrawler is within the
     * {@link #subCrawl(URI, InputStream, SubCrawlerHandler, DataSource, AccessData, Charset, String, RDFContainer)}
     * method, it should terminate as soon as possible.
     */
    private boolean stopRequested = false;
    
    /** An input stream encapsulating an archive stream with compressed data */
    protected abstract static class ArchiveInputStream extends FilterInputStream {
        /**
         * The main constructor
         * @param in the input stream to be wrapped
         */
        public ArchiveInputStream(InputStream in) {
            super (in);
        }
        /** 
         * Returns the next archive entry
         * @return the next archive entry or null if the end of the stream has been reached
         * @throws IOException if something goes wrong
         */
        public abstract ArchiveEntry getNextEntry() throws IOException ;
        /** 
         * closes the current archive entry
         * @throws IOException if something goes wrong 
         */
        public abstract void closeEntry() throws IOException;
    }
    
    /** Encapsulates an archive entry */
    protected abstract static class ArchiveEntry {
        /** @return the archive entry comment */
        public String getComment() { return null; }
        /** @return the compressed size of the entry */
        public long getCompressedSize() { return -1; }
        /** @return the crc 32 checksum of the entry */
        public long getCrc() { return -1; }
        /** @return the last modification time */
        public long getLastModificationTime() { return -1; }
        /** @return the path of the archive entry within the archive file */
        public String getPath() { return null; }
        /** @return true if the archive entry refers to a directory */
        public boolean isDirectory() { return false; }
    }
    
    /** 
     * @param compressedStream the stream with the compressed archive data 
     * @return and ArchiveInputStream encapsulating the given compressed stream 
     */
    protected abstract ArchiveInputStream getArchiveInputStream(InputStream compressedStream);
    
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

        ArchiveInputStream archiveStream = null;
        try {
            archiveStream = getArchiveInputStream(stream);
            ArchiveEntry archiveEntry = null;
            
            parentMetadata.add(RDF.type, NFO.Archive);
            while ((archiveEntry = archiveStream.getNextEntry()) != null && ! stopRequested) {
                try {
                    processSingleEntry(archiveStream,archiveEntry,handler,dataSource,accessData,parentMetadata);
                } finally {
                    archiveStream.closeEntry();
                }
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
        } finally {
            closeClosable(archiveStream);
        }
    }
    
    @Override
    public DataObject getDataObject(URI parentUri, String path, InputStream stream, DataSource dataSource, Charset charset,
            String mimeType, RDFContainerFactory factory) throws SubCrawlerException, PathNotFoundException {
        if (stream == null) {
            throw new SubCrawlerException("The stream cannot be null");
        } 
        
        ArchiveInputStream archiveStream = null;
        try {
            archiveStream = getArchiveInputStream(stream);
            ArchiveEntry archiveEntry = null;
            DataObject result = null;
            while ((archiveEntry = archiveStream.getNextEntry()) != null) {
                if (("/" + archiveEntry.getPath()).equals(path)) {
                    result = convertEntryToDataObject(parentUri, path, archiveStream, archiveEntry,
                        dataSource, factory, false);
                    break;
                }
                else {
                    archiveStream.closeEntry();
                }
            }
            if (result == null) {
                closeClosable(archiveStream);
                throw new PathNotFoundException(getClass().getName(),parentUri,path);
            } else {
                return result;
            }
        }
        catch (Exception e) {
            throw new SubCrawlerException(e);
        } 
    } 

    private void processSingleEntry(ArchiveInputStream archiveStream, ArchiveEntry archiveEntry, 
            SubCrawlerHandler handler, DataSource dataSource, AccessData accessData,
            RDFContainer parentMetadata) {
        
        URI uri = createChildUri(parentMetadata.getDescribedUri(), archiveEntry.getPath());
        
        boolean newEntry = true;
        if (accessData != null && accessData.isKnownId(uri.toString())) {
            newEntry = false;
        }
        
        String lastModifiedDateString = (accessData != null) ? 
                accessData.get(uri.toString(), LAST_MODIFIED_DATE) : 
                null;
        
        if ( !newEntry && lastModifiedDateString != null) {
            try {
                long lastModifiedDate = Long.parseLong(lastModifiedDateString);
                long currentModifiedDate = archiveEntry.getLastModificationTime();
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
        
        DataObject object = convertEntryToDataObject(parentMetadata.getDescribedUri(), archiveEntry.getPath(),
            archiveStream, archiveEntry, dataSource, fac, true);
        
        Date lastModificationDate = object.getMetadata().getDate(NFO.fileLastModified);
        if (lastModificationDate != null && accessData != null) {
            accessData.put(uri.toString(), LAST_MODIFIED_DATE, 
                String.valueOf(lastModificationDate.getTime()));
        }

        if (newEntry) {
            handler.objectNew(object);
        }
        else {
            handler.objectChanged(object);
        }        
    }
    
    private DataObject convertEntryToDataObject(URI parentUri, String path, ArchiveInputStream archiveStream,
            ArchiveEntry archiveEntry, DataSource dataSource, RDFContainerFactory fac, boolean unclosable) {
        URI uri = createChildUri(parentUri, (path.startsWith("/") ? path.substring(1) : path));
        RDFContainer container = fac.getRDFContainer(uri);
        container.add(RDF.type, NFO.ArchiveItem);
        container.add(NFO.fileName,getFileName(archiveEntry));
        
        String superfolder = getSuperfolder(archiveEntry);
        if (superfolder != null) {
            URI superfolderUri = createChildUri(parentUri, superfolder);
            container.add(NFO.belongsToContainer, superfolderUri);
        } else {
            container.add(NFO.belongsToContainer, parentUri);
        }
        
        if (archiveEntry.getComment() != null) {
            container.add(NIE.comment, archiveEntry.getComment());
        }
        if (archiveEntry.getCompressedSize() != -1) {
            container.add(NFO.fileSize, archiveEntry.getCompressedSize());
        }
        if (archiveEntry.getCrc() != -1) {
            Model model = container.getModel();
            Resource hashResource = UriUtil.generateRandomResource(model);
            model.addStatement(hashResource, RDF.type, NFO.FileHash);
            model.addStatement(hashResource, NFO.hashAlgorithm, "CRC-32");
            model.addStatement(hashResource, NFO.hashValue, String.valueOf(archiveEntry.getCrc()));
            model.addStatement(container.getDescribedUri(), NFO.hasHash, hashResource);
        }
        if (archiveEntry.getLastModificationTime() != -1) {
            container.add(NFO.fileLastModified, new Date(archiveEntry.getLastModificationTime()));
        }

        DataObject object = null;
        
        if (archiveEntry.isDirectory()) {
            container.add(RDF.type, NFO.Folder);
            object = new FolderDataObjectBase(container.getDescribedUri(), dataSource, container);
        } else if (unclosable) {
            object = new FileDataObjectBase(container.getDescribedUri(), dataSource, container,
                new UnclosableStream(archiveStream));
        } else {
            object = new FileDataObjectBase(container.getDescribedUri(), dataSource, container,
                archiveStream.markSupported() ? archiveStream : new BufferedInputStream(archiveStream));
        }
        
        return object;
    }

    private void closeClosable(ArchiveInputStream zipStream) {
        if (zipStream != null) {
            try {
                zipStream.close();
            } catch (Exception e) {
                // there is hardly anything we can do about it now
            }
        }
    }
    
    private String getFileName(ArchiveEntry entry) {
        String path = entry.getPath();
        String fileName = null;
        if (path.equals("/")) {
            fileName = path;
        } else if (path.endsWith("/")) {
            // a special case for folder entries, whose names end with a slash 
            int lastSlash = path.lastIndexOf('/',path.length()-2);
            if (lastSlash == -1) {
                // this happens for folders directly beneath the root of the archive
                // the name is like 'zip-test/' - slash at the end, no slash at the beginning
                // we only need to cut off the last slash
                fileName = path.substring(0,path.length() - 1);
            } else {
                // this happens for folders nested deeper within the archive tree
                // the name is like 'zip-test/subfolder/', we need to cut off the
                // initial portion 'zip-test/' and the last slash
                fileName = path.substring(lastSlash + 1, path.length() - 1);
            }
        } else {
            // normal files, whose names don't end with a hash
            int lastSlash = path.lastIndexOf('/',path.length()-1);
            if (lastSlash == -1) {
                // this happens for files directly beneath the root of the archive
                // the name is like 'file.txt' - no slash at the end, no slash at the beginning
                // we need to return the name itself
                fileName = path;
            } else {
                // this happens for files nested deeper within the archive tree
                // the name is like 'zip-test/file1.txt', we need to cut off the
                // initial portion 'zip-test/' 
                fileName = path.substring(lastSlash + 1, path.length());
            }
        }
        return fileName;
    }
    
    private String getSuperfolder(ArchiveEntry entry) {
        String path = entry.getPath();
        String superfolderName = null;
        if (path.equals("/")) {
            superfolderName = null;
        } else if (path.endsWith("/")) {
            // a special case for folder entries, whose names end with a slash 
            int lastSlash = path.lastIndexOf('/',path.length()-2);
            if (lastSlash == -1) {
                // this happens for folders directly beneath the root of the archive
                // the name is like 'zip-test/' - they don't have any superfolder
                superfolderName = null;
            } else {
                // this happens for folders nested deeper within the archive tree
                // the name is like 'zip-test/subfolder/', we need to cut off the
                // trailing portion 'subfolder/' 
                superfolderName = path.substring(0, lastSlash + 1);
            }
        } else {
            // normal files, whose names don't end with a hash
            int lastSlash = path.lastIndexOf('/',path.length()-1);
            if (lastSlash == -1) {
                // this happens for files directly beneath the root of the archive
                // the name is like 'file.txt' - they have no superfolder
                superfolderName = null;
            } else {
                // this happens for files nested deeper within the archive tree
                // the name is like 'zip-test/file1.txt', we need to cut off the
                // trailing portion 'file.txt' 
                superfolderName = path.substring(0, lastSlash + 1);
            }
        }
        return superfolderName;
    }

    public void stopSubCrawler() {
        stopRequested = true;
    }
    
    private class UnclosableStream extends FilterInputStream {
        /** 
         * the main constructor of an unclosable stream
         * @param in the input stream to wrap
         */
        protected UnclosableStream(InputStream in) {
            super(in.markSupported() ? in : new BufferedInputStream(in));
        }
        /** the close() implementation does not close the underlying stream */
        public void close() {
            //swallow the call to close(), do not close the underlying stream
        }
    }
}
