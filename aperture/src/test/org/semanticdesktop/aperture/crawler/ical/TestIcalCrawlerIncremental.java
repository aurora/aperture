/*
 * Copyright (c) 2006 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.rdf2go.RepositoryModel;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.rdf2go.RDF2GoRDFContainer;

public class TestIcalCrawlerIncremental extends ApertureTestBase {

	public static final String ICAL_TESTDATA_PATH = DOCS_PATH + "icaltestdata/";
	public static final String TEMP_FILE_NAME = "temp-calendar.ics";

	private AccessData accessData;

	public void setUp() {
		accessData = new AccessDataImpl();
	}
	
	public void testIncrementalCrawlerHandler() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData,null);
		assertNewModUnmodDel(handler,5,0,0,0);
		handler.close();
	}
	
	public void testOneChangedObject() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData,null);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-1.ics",accessData,handler.getFile());		
		// the event is reported as changed (new sequence number)
		// all other four components are unchanged
		assertNewModUnmodDel(handler2,0,1,4,0);
		handler.close();
		handler2.close();
	}
	
	public void testOneLetterChangedInTimezone() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData,null);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-2.ics",accessData,handler.getFile());
		assertNewModUnmodDel(handler2,0,1,4,0);
		handler.close();
		handler2.close();
	}
	
	public void testOneNewComponentAddition() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData,null);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-3.ics",accessData,handler.getFile());
		// we added a new component, other should be unchanged
		assertNewModUnmodDel(handler2,1,0,5,0);
		handler.close();
		handler2.close();
	}
	
	public void testOneComponentDeletion() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData,null);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-4.ics",accessData,handler.getFile());
		// we have removed a component, other should be unchanged
		assertNewModUnmodDel(handler2,0,0,4,1);
		handler.close();
		handler2.close();
	}
	
	/**
	 * Crawls the ICAL file and returns the crawler handler.
	 */
	private IcalTestIncrementalCrawlerHandler readIcalFile(String fileName, AccessData accessData, File file)
			throws Exception {
		InputStream fileStream = ClassLoader.getSystemResourceAsStream(ICAL_TESTDATA_PATH + fileName);
		assertNotNull(fileStream);
		File tempFile = null;
		if (file == null) {
			tempFile = createTempFile(fileStream,null);
		} else {
			tempFile = createTempFile(fileStream,file);
		}
		Model configurationModel = new RepositoryModel(false);
		RDF2GoRDFContainer configurationContainer = new RDF2GoRDFContainer(configurationModel,URIImpl
				.createURIWithoutChecking("source:testsource"));
		ConfigurationUtil.setRootUrl(tempFile.getAbsolutePath(), configurationContainer);

		IcalDataSource icalDataSource = new IcalDataSource();
		icalDataSource.setConfiguration(configurationContainer);

		IcalTestIncrementalCrawlerHandler testCrawlerHandler = new IcalTestIncrementalCrawlerHandler(tempFile);

		IcalCrawler icalCrawler = new IcalCrawler();
		icalCrawler.setDataSource(icalDataSource);
		icalCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
		icalCrawler.setCrawlerHandler(testCrawlerHandler);
		icalCrawler.setAccessData(accessData);

		assertNewModUnmodDel(testCrawlerHandler, 0, 0, 0, 0);

		icalCrawler.crawl();

		assertTrue(tempFile.delete());

		configurationModel.close();
		return testCrawlerHandler;
	}
	
	public void assertNewModUnmodDel(IcalTestIncrementalCrawlerHandler handler, int newObjects,
			int changedObjects, int unchangedObjects, int deletedObjects) {
		assertEquals(handler.getNewObjects().size(), newObjects);
		assertEquals(handler.getChangedObjects().size(), changedObjects);
		assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
		assertEquals(handler.getDeletedObjects().size(), deletedObjects);
	}
	
	public File createTempFile(InputStream fis, File file) throws Exception {
		File outFile = null;
		if (file == null) {
			outFile = File.createTempFile("temp", ".ics");
		} else {
			outFile = file;
		}
		outFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(outFile);
		byte[] buf = new byte[1024];
		int i = 0;
		while ((i = fis.read(buf)) != -1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
		return outFile;
	}
}
