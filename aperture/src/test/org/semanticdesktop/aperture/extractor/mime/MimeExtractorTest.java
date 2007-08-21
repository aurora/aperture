/*
 * Copyright (c) 2006 - 2007 Aduna.
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
	
	public void testWebArchiveExtraction() throws ExtractorException, IOException {
		testWebArchiveExtraction("mhtml-firefox.mht");
		testWebArchiveExtraction("mhtml-internet-explorer.mht");
	}
	
	public void testWebArchiveExtraction(String fileName) throws ExtractorException, IOException {
		ExtractorFactory factory = new MimeExtractorFactory();
		Extractor extractor = factory.get();
		RDFContainer container = extract(DOCS_PATH + fileName, extractor);
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
