/*
 * Copyright (c) 2005 - 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.base.FileAccessData;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class TestFileAccessor extends ApertureTestBase {

    private static final String TMP_SUBDIR = "TestFileAccessor.tmpDir";

    private File tmpDir;

    private File tmpFile;

    private FileAccessor fileAccessor;

    public void setUp() throws IOException {
        // create a temporary folder containing a temporary file
        // unfortunately there is no File.createTempDir
        tmpDir = new File(System.getProperty("java.io.tmpdir"), TMP_SUBDIR).getCanonicalFile();
        FileUtil.deltree(tmpDir);
        tmpDir.mkdir();

        tmpFile = File.createTempFile("file-", ".txt", tmpDir);
        IOUtil.writeString("test file", tmpFile);

        // setup a FileAccessor
        FileAccessorFactory factory = new FileAccessorFactory();
        fileAccessor = (FileAccessor) factory.get();
    }

    public void tearDown() {
        // delete the temporary folder
        FileUtil.deltree(tmpDir);
    }

    public void testFileAccess() throws UrlNotFoundException, MalformedURLException, IOException, ModelException {
        // create the data object
        SimpleRDFContainerFactory factory = new SimpleRDFContainerFactory();
        DataObject dataObject = fileAccessor.getDataObject(tmpFile.toURI().toString(), null, null, factory);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof FileDataObject);

        // check its metadata
        checkStatement(DATA.name, "file-", (RDF2GoRDFContainer) dataObject.getMetadata());

        URI parentURI = dataObject.getMetadata().getValueFactory().createURI(tmpDir.toURI().toString());
        checkStatement(DATA.partOf, parentURI, (RDF2GoRDFContainer) dataObject.getMetadata());
        
        // we don't need to dispose the DataSource because we passed null as the value of DataSource to the
        // extract method
        dataObject.dispose();
    }

    public void testFolderAccess() throws UrlNotFoundException, MalformedURLException, IOException, ModelException {
        // create the data object
        SimpleRDFContainerFactory factory = new SimpleRDFContainerFactory();
        DataObject dataObject = fileAccessor.getDataObject(tmpDir.toURI().toString(), null, null, factory);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof FolderDataObject);

        // check its metadata
        checkStatement(DATA.name, "TestFileAccessor", (RDF2GoRDFContainer) dataObject.getMetadata());
        
        // we don't need to dispose the DataSource because we passed null as the value of DataSource to the
        // extract method
        dataObject.dispose();
    }

    public void testNonModifiedFile() throws UrlNotFoundException, IOException {
        // create a fake AccessData that holds the last modified date of tmpFile
        AccessData accessData = new FileAccessData();
        accessData.initialize();
        String url = tmpFile.toURI().toString();
        accessData.put(url, AccessData.DATE_KEY, String.valueOf(tmpFile.lastModified()));

        // check that the FileAccessor returns null when we try to fetch a DataObject while passing it
        // this AccessData
        HashMap params = new HashMap();
        params.put(FileAccessor.FILE_KEY, tmpFile);
        SimpleRDFContainerFactory factory = new SimpleRDFContainerFactory();
        DataObject object1 = fileAccessor.getDataObjectIfModified(url, null, accessData, params, factory);
        assertNull(object1);
        
        // double-check that we *do* get a DataObject when we don't pass the AccessData
        DataObject object2 = fileAccessor.getDataObject(url, null, params, factory);
        assertNotNull(object2);
        
        // we don't need to dispose the DataSource because we passed null as the value of DataSource to the
		// extract method
		object2.dispose();
    }
    
    private class SimpleRDFContainerFactory implements RDFContainerFactory {
        public RDFContainer getRDFContainer(URI uri) {
            return createRDFContainer(uri);
        }
    }
}
