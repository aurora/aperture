/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.subcrawler.SubCrawler;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerFactory;
import org.semanticdesktop.aperture.subcrawler.SubCrawlerTestBase;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;

/**
 * Tests for the MimeSubCrawler
 */
public class MimeSubCrawlerTest extends SubCrawlerTestBase {

    /**
     * This test tests the basic functionality, subcrawling a simple .eml file containing a plain-text message
     * without any attachments.
     * 
     * @throws Exception
     */
	public void testMailExtraction() throws Exception {
		// apply the extractor on an example file
		SubCrawlerFactory factory = new MimeSubCrawlerFactory();
		SubCrawler subCrawler = factory.get();
		TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
		RDFContainer container = subCrawl("mail-thunderbird-1.5.eml", subCrawler, handler);

		// check the extraction results
		checkStatement(NMO.plainTextMessageContent, "test body", container);
		checkStatement(NMO.messageSubject, "test subject", container);
		checkStatement(NIE.contentCreated, "2006", container);

		assertEquals("emailperson:Christiaan+Fluit", container.getURI(NMO.from).toString());
		assertEquals("emailperson:Christiaan+Fluit", container.getURI(NMO.to).toString());
        validate(container);
		container.dispose();
	}
	
	/**
     * This test is intended to check for the behavior described in the issue number [ 1888018 ] MimeExtractor
     * puts both plain and html in plainTextContent The file under test is a multipart/alternative email
     * message containing both a plain-text and a html version of the same message. The expected behavior
     * would be to put the plain text version in the nie:plainTextContent property of the returned container.
     * The html version is to be completely disregarded.
     * 
     * We check this by looking for some html tags in the extracted plain text. They are NOT to be found.
     * (note that
     * <P>
     * is an exception since by chance it does appear in the plain text part too)
     * 
     * @throws Exception
     */
    public void testMultipartExtraction() throws Exception {
        SubCrawlerFactory factory = new MimeSubCrawlerFactory();
        SubCrawler subCrawler = factory.get();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        RDFContainer container = subCrawl("mail-multipart-plain-html.eml", subCrawler, handler);

        String plainText = container.getString(NMO.plainTextMessageContent);
        assertFalse(plainText.contains("<font"));
        assertFalse(plainText.contains("<ul>"));
        assertFalse(plainText.contains("<li>"));
        
        validate(container);
        container.dispose();
    }
	
    /**
     * Tests the extraction of the mht web archive files
     * @throws Exception
     */
	public void testWebArchiveExtraction() throws Exception {
		testWebArchiveExtraction("mhtml-firefox.mht");
		testWebArchiveExtraction("mhtml-internet-explorer.mht");
	}
	
	private void testWebArchiveExtraction(String fileName) throws Exception {
	    SubCrawlerFactory factory = new MimeSubCrawlerFactory();
        SubCrawler subCrawler = factory.get();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        RDFContainer container = subCrawl(fileName, subCrawler, handler);

		String fullText = container.getString(NMO.plainTextMessageContent);
		// check that relevant content was extracted
		assertTrue(fullText.contains("Project name"));
		assertTrue(fullText.contains("FAQ"));
		assertTrue(fullText.contains("mailinglist"));

		// check that HTML markup was removed
		assertFalse(fullText.contains("<P>"));
		assertFalse(fullText.contains("<p>"));
        validate(container);
		container.dispose();
	}
}
