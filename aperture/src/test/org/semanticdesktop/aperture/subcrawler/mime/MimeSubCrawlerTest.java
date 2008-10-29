/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler.mime;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
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
		subCrawl("mail-thunderbird-1.5.eml", subCrawler, handler);
		Model model = handler.getModel();
		// let's see if the uri is OK
		URI messageUri = new URIImpl("uri:dummyuri/mail-thunderbird-1.5.eml");
		assertNewModUnmod(handler, 0, 0, 0, 0); // all metadata in the parent container, no new objects
		
		// check the extraction results
		assertTrue(findSingleObjectNode(model, messageUri, NMO.plainTextMessageContent).asLiteral().getValue().contains("test body"));
		assertTrue(findSingleObjectNode(model, messageUri, NMO.messageSubject).asLiteral().getValue().equals("test subject"));
		assertTrue(findSingleObjectNode(model, messageUri, NIE.contentCreated).asLiteral().getValue().contains("2006"));

		assertTrue(findSingleObjectResource(model, messageUri, NMO.from).toString().equals("emailperson:Christiaan+Fluit"));
		assertTrue(findSingleObjectResource(model, messageUri, NMO.to).toString().equals("emailperson:Christiaan+Fluit"));
        validate(model);
		model.close();
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
        subCrawl("mail-multipart-plain-html.eml", subCrawler, handler);
        Model model = handler.getModel();
        URI messageUri = new URIImpl("uri:dummyuri/mail-multipart-plain-html.eml");
        String plainText = findSingleObjectNode(model, messageUri, NMO.plainTextMessageContent).asLiteral().getValue();
        assertFalse(plainText.contains("<font"));
        assertFalse(plainText.contains("<ul>"));
        assertFalse(plainText.contains("<li>"));
        
        validate(model);
        model.close();
    }
    
    /**
     * This tests whether the subcrawler can process .eml file with attachments correctly. I.e. all metadata of the
     * parent email is to be placed in the parent metadata container, whereas the metadata of the attachments
     * is supposed to be returned in separate data objects with uris like:
     * 
     *  <pre>
     *  mime:file://path/to/file.eml!/#1
     *  mime:file://path/to/file.eml!/#2
     *  </pre>
     *  
     *  I.e. prefix is 'mime', the parent uri is 'file://path/to/file.eml' and the internal path is /#1
     * 
     * @throws Exception  
     */
    public void testAttachmentExtraction() throws Exception {
        SubCrawlerFactory factory = new MimeSubCrawlerFactory();
        SubCrawler subCrawler = factory.get();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        subCrawl("mail-multipart-test.eml", subCrawler, handler);
        assertNewModUnmod(handler, 2, 0, 0, 0); // two attachments - two new objects
        assertTrue(handler.getNewObjects().contains("mime:uri:dummyuri/mail-multipart-test.eml!/1"));
        assertTrue(handler.getNewObjects().contains("mime:uri:dummyuri/mail-multipart-test.eml!/2"));
    }
	
    /**
     * Tests the extraction of the mht web archive files
     * @throws Exception
     */
	public void testWebArchiveExtraction() throws Exception {
		testWebArchiveExtraction("mhtml-firefox.mht","uri:dummyuri/mhtml-firefox.mht");
		testWebArchiveExtraction("mhtml-internet-explorer.mht","uri:dummyuri/mhtml-internet-explorer.mht");
	}
	
	private void testWebArchiveExtraction(String fileName, String uri) throws Exception {
	    SubCrawlerFactory factory = new MimeSubCrawlerFactory();
        SubCrawler subCrawler = factory.get();
        TestBasicSubCrawlerHandler handler = new TestBasicSubCrawlerHandler();
        
        subCrawl(fileName, subCrawler, handler);
        URI messageUri = new URIImpl(uri);
		String fullText = findSingleObjectNode(handler.getModel(), messageUri, NMO.plainTextMessageContent).asLiteral().getValue();
		// check that relevant content was extracted
		assertTrue(fullText.contains("Project name"));
		assertTrue(fullText.contains("FAQ"));
		assertTrue(fullText.contains("mailinglist"));

		// check that HTML markup was removed
		assertFalse(fullText.contains("<P>"));
		assertFalse(fullText.contains("<p>"));
        validate(handler.getModel());
		handler.close();
	}
}
