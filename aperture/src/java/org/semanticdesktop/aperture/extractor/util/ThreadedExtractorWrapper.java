/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A ThreadedExtractorWrapper wraps an Extractor and executes it on a separate thread, bailing out if the
 * wrapped Extractor appears to be hanging. The heuristic for determining whether the Extractor is hanging is
 * by looking at whether the InputStream is regularly accessed. Any Exceptions thrown by the wrapped Extractor
 * are eventually thrown by the ThreadedExtractorWrapper.
 * 
 * <p>
 * Furthermore, a ThreadedExtractorWrapper can be requested to stop processing, causing it to throw an
 * IOException on the InputStream the next time it is accessed by the wrapped Extractor. This allows for
 * interrupting an extraction process upon user request, for example because it has been processing a single
 * file for a very long time (especially large PDF documents are notorious). This implementation strategy is
 * preferred over interrupting the Thread as that should only be used as a last resort to stop a thread.
 */
public class ThreadedExtractorWrapper implements Extractor {

	/**
	 * The maximum time per MB of data that the wrapped Extractor is allowed to work on the read data before
	 * it is considered to be hanging, in milliseconds.
	 */
	private static final long MAX_PROCESSING_TIME_PER_MB = 10L * 1000L; // 10 seconds

	/**
	 * The minimum maximum processing time that the wrapped Extractor is allowed to work on the read data
	 * before it is considered to be hanging. This minimum gives a lower bound for very small files.
	 */
	private static final long MINIMUM_MAX_PROCESSING_TIME = 30L * 1000L; // 30 seconds

	/**
	 * The maximum time between two reads that the wrapped Extractor is allowed to work on the read data
	 * before it is considered to be hanging.
	 */
	private static final long MAX_IDLE_READ_TIME = 30L * 1000L; // 30 seconds

	/**
	 * The wrapped Extractor.
	 */
	private Extractor extractor;

	/**
	 * Flag that indicates that a request to stop extracting has been issued.
	 */
	private boolean stopRequested;

	/**
	 * Creates a new wrapper for the specified Extractor.
	 * 
	 * @param extractor The Extractor to wrap.
	 */
	public ThreadedExtractorWrapper(Extractor extractor) {
		this.extractor = extractor;
		stopRequested = false;
	}

	/**
	 * Interrupts processing of the wrapped extractor as soon as possible.
	 */
	public void stop() {
		stopRequested = true;
	}

	/**
	 * Starts the extraction process using the wrapped Extractor on a separate thread. This Thread is
	 * interrupted as soon as no progress is reported.
	 */
	public void extract(URI id, InputStream input, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		ExtractionStream monitoredStream = new ExtractionStream(input);

		ExtractionThread thread = new ExtractionThread(id, monitoredStream, charset, mimeType, result);
		thread.start();

		while (true) {
			// wait for the thread to finish its work
			long waitTime;
			if (monitoredStream.allBytesRead()) {
				waitTime = MAX_PROCESSING_TIME_PER_MB * monitoredStream.getTotalBytesRead() / (1024 * 1024);
				waitTime = Math.max(waitTime, MINIMUM_MAX_PROCESSING_TIME);
			}
			else {
				waitTime = MAX_IDLE_READ_TIME;
			}

			waitTime -= System.currentTimeMillis() - monitoredStream.getLastAccessTime();

			if (waitTime <= 0L || !thread.isAlive()) {
				break;
			}

			try {
				thread.join(waitTime);
			}
			catch (InterruptedException e) {
				throw new ExtractorException(e);
			}
		}

		if (thread.isAlive()) {
			thread.abortExtraction();
			throw new ExtractorException("Extractor aborted");
		}
		else {
			// throw any exceptions that may have been thrown during the operation of the Extractor
			Exception e = thread.getException();
			if (e != null) {
				if (e instanceof ExtractorException) {
					throw (ExtractorException) e;
				}
				else {
					throw (RuntimeException) e;
				}
			}
		}
	}

	private class ExtractionThread extends Thread {

		private URI id;

		private InputStream input;

		private Charset charset;

		private String mimeType;

		private RDFContainer result;

		private Exception exception;

		public ExtractionThread(URI id, InputStream input, Charset charset, String mimeType,
				RDFContainer result) {
			this.id = id;
			this.input = input;
			this.charset = charset;
			this.mimeType = mimeType;
			this.result = result;
		}

		public void run() {
			try {
				extractor.extract(id, input, charset, mimeType, result);
			}
			catch (Exception e) {
				exception = e;
			}
		}

		public void abortExtraction() {
			interrupt();
		}

		public Exception getException() {
			return exception;
		}
	}

	private class ExtractionStream extends FilterInputStream {

		private long lastAccessTime;

		private boolean allBytesRead;

		private int totalBytesRead;

		public ExtractionStream(InputStream in) {
			super(in);

			lastAccessTime = System.currentTimeMillis();
			allBytesRead = false;
			totalBytesRead = 0;
		}

		public long getLastAccessTime() {
			return lastAccessTime;
		}

		public boolean allBytesRead() {
			return allBytesRead;
		}

		public int getTotalBytesRead() {
			return totalBytesRead;
		}

		public int read() throws IOException {
			checkStopRequested();

			int result = super.read();

			if (result >= 0) {
				lastAccessTime = System.currentTimeMillis();
				totalBytesRead++;
			}
			else {
				allBytesRead = true;
			}

			return result;
		}

		public int read(byte[] b) throws IOException {
			checkStopRequested();

			int result = super.read(b);

			if (result >= 0) {
				lastAccessTime = System.currentTimeMillis();
				totalBytesRead += result;
			}
			else {
				allBytesRead = true;
			}

			return result;
		}

		public int read(byte[] b, int off, int len) throws IOException {
			checkStopRequested();

			int result = super.read(b, off, len);

			if (result >= 0) {
				lastAccessTime = System.currentTimeMillis();
				totalBytesRead += result;
			}
			else {
				allBytesRead = true;
			}

			return result;
		}

		public void close() throws IOException {
			super.close();
			allBytesRead = true;
		}

		private void checkStopRequested() throws ExtractionInterruptedException {
			if (stopRequested) {
				throw new ExtractionInterruptedException();
			}
		}
	}

	public static class ExtractionInterruptedException extends IOException {

		public ExtractionInterruptedException() {
			super("Extraction interrupted upon request");
		}
	}
}
