/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import org.openrdf.model.impl.URIImpl;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.base.AccessDataImpl;
import org.semanticdesktop.aperture.accessor.impl.DefaultDataAccessorRegistry;
import org.semanticdesktop.aperture.datasource.config.ConfigurationUtil;
import org.semanticdesktop.aperture.datasource.ical.IcalDataSource;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class TestIcalCrawlerIncremental extends ApertureTestBase {

	public static final String ICAL_TESTDATA_PATH = DOCS_PATH + "icaltestdata/";
	public static final String TEMP_FILE_NAME = "temp-calendar.ics";

	private AccessData accessData;

	public void setUp() {
		accessData = new AccessDataImpl();
	}
	
	public void testIncrementalCrawlerHandler() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData);
		assertNewModUnmodDel(handler,5,0,0,0);
	}
	
	public void testOneChangedObject() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-1.ics",accessData);		
		// the event is reported as changed (new sequence number)
		// all other four components are unchanged
		assertNewModUnmodDel(handler2,0,1,4,0);
	}
	
	public void testOneLetterChangedInTimezone() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-2.ics",accessData);
		assertNewModUnmodDel(handler2,0,1,4,0);
	}
	
	public void testOneNewComponentAddition() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-3.ics",accessData);
		// we added a new component, other should be unchanged
		assertNewModUnmodDel(handler2,1,0,5,0);
	}
	
	public void testOneComponentDeletion() throws Exception {
		IcalTestIncrementalCrawlerHandler handler = readIcalFile("cal01.ics",accessData);
		assertNewModUnmodDel(handler,5,0,0,0);
		IcalTestIncrementalCrawlerHandler handler2 = readIcalFile("cal01-4.ics",accessData);
		// we have removed a component, other should be unchanged
		assertNewModUnmodDel(handler2,0,0,4,1);
	}
	
	/**
	 * Crawls the ICAL file and returns the crawler handler.
	 */
	private IcalTestIncrementalCrawlerHandler readIcalFile(String fileName, AccessData accessData)
			throws Exception {
		URL fileURL = ClassLoader.getSystemResource(ICAL_TESTDATA_PATH + fileName);
		assertNotNull(fileURL);
		File file = new File(fileURL.getFile());
		File tempFile = createTempFile(file);
		assertTrue(file.canRead());
		SesameRDFContainer configurationContainer = new SesameRDFContainer(new URIImpl("source:testsource"));
		ConfigurationUtil.setRootUrl(tempFile.getAbsolutePath(), configurationContainer);

		IcalDataSource icalDataSource = new IcalDataSource();
		icalDataSource.setConfiguration(configurationContainer);

		IcalTestIncrementalCrawlerHandler testCrawlerHandler = new IcalTestIncrementalCrawlerHandler();

		IcalCrawler icalCrawler = new IcalCrawler();
		icalCrawler.setDataSource(icalDataSource);
		icalCrawler.setDataAccessorRegistry(new DefaultDataAccessorRegistry());
		icalCrawler.setCrawlerHandler(testCrawlerHandler);
		icalCrawler.setAccessData(accessData);

		assertNewModUnmodDel(testCrawlerHandler, 0, 0, 0, 0);

		icalCrawler.crawl();

		assertTrue(tempFile.delete());

		return testCrawlerHandler;
	}
	
	public void assertNewModUnmodDel(IcalTestIncrementalCrawlerHandler handler, int newObjects,
			int changedObjects, int unchangedObjects, int deletedObjects) {
		assertEquals(handler.getNewObjects().size(), newObjects);
		assertEquals(handler.getChangedObjects().size(), changedObjects);
		assertEquals(handler.getUnchangedObjects().size(), unchangedObjects);
		assertEquals(handler.getDeletedObjects().size(), deletedObjects);
	}
	
	public File createTempFile(File inFile) throws Exception {
		URL tempFileDirectory = ClassLoader.getSystemResource(".");
		File outFile = new File(tempFileDirectory.getFile() + TEMP_FILE_NAME);
		FileInputStream fis = new FileInputStream(inFile);
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
