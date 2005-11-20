/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FolderDataObject;
import org.semanticdesktop.aperture.accessor.UrlNotFoundException;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.accessor.base.AccessDataBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainerFactory;
import org.semanticdesktop.aperture.util.FileUtil;
import org.semanticdesktop.aperture.util.IOUtil;

public class TestFileAccessor extends ApertureTestBase {

    private static final String TMP_SUBDIR = "TestFileAccessor.tmpDir";

    private File tmpDir;

    private File tmpFile;

    private FileAccessor fileAccessor;

    public void setUp() throws IOException {
        // create a temporary folder containing a temporary file
        // unfortunately there is no File.createTempDir
        tmpDir = new File(System.getProperty("java.io.tmpdir"), TMP_SUBDIR).getCanonicalFile();
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

    public void testFileAccess() throws UrlNotFoundException, MalformedURLException, IOException {
        // create an RDFContainer
        String url = tmpFile.toURI().toString();
        SesameRDFContainer metadata = new SesameRDFContainer(url);
        
        // create the data object
        DataObject dataObject = fileAccessor.getDataObject(url, null, null, metadata);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof FileDataObject);

        // check its metadata
        checkStatement(Vocabulary.NAME, "file-", metadata);

        URI parentURI = new URIImpl(tmpDir.toURI().toString());
        checkStatement(Vocabulary.PART_OF, parentURI, metadata);
    }

    public void testFolderAccess() throws UrlNotFoundException, MalformedURLException, IOException {
        // create an RDFContainer
        String url = tmpFile.toURI().toString();
        SesameRDFContainer metadata = new SesameRDFContainer(url);

        // create the data object
        DataObject dataObject = fileAccessor.getDataObject(tmpDir.toURI().toString(), null, null, metadata);
        assertNotNull(dataObject);
        assertTrue(dataObject instanceof FolderDataObject);

        // check its metadata
        checkStatement(Vocabulary.NAME, "TestFileAccessor", metadata);
    }

    public void testNonModifiedFile() throws UrlNotFoundException, IOException {
        // create an RDFContainer
        String url = tmpFile.toURI().toString();
        SesameRDFContainer metadata = new SesameRDFContainer(url);

        // create a fake AccessData that holds the last modified date of tmpFile
        AccessData accessData = new AccessDataBase();
        accessData.put(url, AccessData.DATE_KEY, String.valueOf(tmpFile.lastModified()));

        // check that the FileAccessor returns null when we try to fetch a DataObject while passing it
        // this AccessData
        HashMap params = new HashMap();
        params.put(FileAccessor.FILE_KEY, tmpFile);
        DataObject object1 = fileAccessor.getDataObjectIfModified(url, null, accessData, params, metadata);
        assertNull(object1);
        
        // double-check that we *do* get a DataObject when we don't pass the AccessData
        DataObject object2 = fileAccessor.getDataObject(url, null, params, metadata);
        assertNotNull(object2);
    }
}
