/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mime;

import java.io.IOException;

import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class MimeExtractorTest extends ExtractorTestBase {

	public void testExtraction() throws ExtractorException, IOException {
		// apply the extractor on an example file
		ExtractorFactory factory = new MimeExtractorFactory();
		Extractor extractor = factory.get();
		SesameRDFContainer container = extract(DOCS_PATH + "mail-thunderbird-1.5.eml", extractor);

		// check the extraction results
		checkStatement(AccessVocabulary.FULL_TEXT, "test body", container);
		checkStatement(AccessVocabulary.TITLE, "test subject", container);
		checkStatement(AccessVocabulary.DATE, "2006", container);

		assertEquals("email:christiaan.fluit@aduna.biz", container.getURI(AccessVocabulary.FROM).toString());
		assertEquals("email:Christiaan.Fluit@aduna.biz", container.getURI(AccessVocabulary.TO).toString());
	}
}
