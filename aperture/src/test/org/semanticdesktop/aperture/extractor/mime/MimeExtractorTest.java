/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mime;

import java.io.IOException;

import org.ontoware.rdf2go.exception.ModelException;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NMO;

public class MimeExtractorTest extends ExtractorTestBase {

	public void testMailExtraction() throws ExtractorException, IOException, ModelException {
		// apply the extractor on an example file
		ExtractorFactory factory = new MimeExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(DOCS_PATH + "mail-thunderbird-1.5.eml", extractor);

		// check the extraction results
		checkStatement(NMO.plainTextMessageContent, "test body", container);
		checkStatement(NMO.messageSubject, "test subject", container);
		checkStatement(NMO.sentDate, "2006", container);

		assertEquals("email:christiaan.fluit@aduna.biz", container.getURI(NMO.from).toString());
		assertEquals("email:Christiaan.Fluit@aduna.biz", container.getURI(NMO.to).toString());
        validate(container);
		container.dispose();
	}
	
	/**
	 * This test is intended to check for the behavior described in the issue number
	 * [ 1888018 ] MimeExtractor puts both plain and html in plainTextContent
	 * The file under test is a multipart/alternative email message containing
	 * both a plain-text and a html version of the same message. The expected
	 * behavior would be to put the plain text version in the nie:plainTextContent
	 * property of the returned container. The html version is to be completely
	 * disregarded. 
	 * 
	 * We check this by looking for some html tags in the extracted plain text.
	 * They are NOT to be found. (note that <P> is an exception since by chance
	 * it does appear in the plain text part too)
	 * 
	 * @throws ExtractorException
	 * @throws IOException
	 * @throws ModelException
	 */
    public void testMultipartExtraction() throws ExtractorException, IOException, ModelException {
        ExtractorFactory factory = new MimeExtractorFactory();
        Extractor extractor = factory.get();
        RDFContainer container = extract(DOCS_PATH + "mail-multipart-plain-html.eml", extractor);

        String plainText = container.getString(NMO.plainTextMessageContent);
        assertFalse(plainText.contains("<font"));
        assertFalse(plainText.contains("<ul>"));
        assertFalse(plainText.contains("<li>"));
        
        validate(container);
        container.dispose();
    }
	
	public void testWebArchiveExtraction() throws ExtractorException, IOException {
		testWebArchiveExtraction("mhtml-firefox.mht");
		testWebArchiveExtraction("mhtml-internet-explorer.mht");
	}
	
	public void testWebArchiveExtraction(String fileName) throws ExtractorException, IOException {
		ExtractorFactory factory = new MimeExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(DOCS_PATH + fileName, extractor);
		String fullText = container.getString(NMO.plainTextMessageContent);
		System.out.println(container.getString(NMO.plainTextMessageContent));
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
