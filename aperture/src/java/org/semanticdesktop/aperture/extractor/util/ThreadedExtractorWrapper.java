/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A ThreadedExtractorWrapper wraps an Extractor and executes it with a certain timeout, bailing out if the
 * wrapped Extractor appears to be hanging. The heuristic for determining whether the Extractor is hanging is
 * by looking at whether the InputStream is regularly accessed. This has proven to be a good heuristic. It for
 * example catches the case of a web server that hangs while uploading a requested file.
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
	 * Creates a new wrapper for the specified Extractor.
	 * 
	 * @param extractor The Extractor to wrap.
	 */
	public ThreadedExtractorWrapper(Extractor extractor) {
		this.extractor = extractor;
	}

	/**
	 * Starts the extraction process using the wrapped Extractor on a separate thread. This Thread is
	 * interrupted as soon as no progress is reported.
	 */
	public void extract(URI id, InputStream input, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		MonitoringInputStream monitoredStream = new MonitoringInputStream(input);

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
			ExtractorException e = thread.getException();
			if (e != null) {
				throw e;
			}
		}
	}

	private class ExtractionThread extends Thread {

		private URI id;

		private InputStream input;

		private Charset charset;

		private String mimeType;

		private RDFContainer result;

		private ExtractorException exception;
		
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
			catch (ExtractorException e) {
				exception = e;
			}
		}

		public void abortExtraction() {
			interrupt();
		}
		
		public ExtractorException getException() {
			return exception;
		}
	}

	private static class MonitoringInputStream extends FilterInputStream {

		private long lastAccessTime;

		private boolean allBytesRead;

		private int totalBytesRead;

		public MonitoringInputStream(InputStream in) {
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
	}
}
