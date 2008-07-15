/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

public class ThreadedExtractorWrapperTest extends ApertureTestBase {

	public void testExtractorException() {
		testThrowException(new ExtractorException());
	}

	public void testRuntimeException() {
		testThrowException(new ArrayIndexOutOfBoundsException());
	}

	private void testThrowException(Exception e) {
		ExceptionThrowingExtractor extractor = new ExceptionThrowingExtractor(e);
		ThreadedExtractorWrapper wrapper = new ThreadedExtractorWrapper(extractor);
		try {
			wrapper.extract(null, null, null, null, null);

			// when any exceptions are lost in the extracting thread (i.e. are not caught and end up at the
			// top of the thread's own stack), the extract method will seem to terminate normally and proceed,
			// even though extraction has failed and an exception should have been thrown
			fail();
		}
		catch (Exception exc) {
			// make sure we receive the same Exception instance as was thrown by the original Extractor
			assertSame(e, exc);
		}
	}

	private static class ExceptionThrowingExtractor implements Extractor {

		private Exception e;

		public ExceptionThrowingExtractor(Exception e) {
			this.e = e;
		}

		public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
				throws ExtractorException {
			if (e instanceof ExtractorException) {
				throw (ExtractorException) e;
			}
			else {
				throw (RuntimeException) e;
			}
		}
	}
}
