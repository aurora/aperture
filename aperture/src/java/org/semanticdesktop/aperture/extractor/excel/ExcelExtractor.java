/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.excel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SeriesTextRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.UnicodeString;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.PoiUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.StringExtractor;

public class ExcelExtractor implements Extractor {

	private static final Logger LOGGER = Logger.getLogger(ExcelExtractor.class.getName());

	private static final String END_OF_LINE = System.getProperty("line.separator", "\n");

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		// mark the stream with a sufficiently large buffer so that, when POI chokes on a document, there is a
		// good chance we can reset to the beginning of the buffer and apply a StringExtractor
		int bufferSize = PoiUtil.getBufferSize("aperture.excelExtractor.bufferSize", 4 * 1024 * 1024);
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream, bufferSize);
		}
		stream.mark(bufferSize);

		// apply the POI-based extraction code
		try {
			applyPoi(stream, result);
		}
		catch (Exception e) {
			// we catch Exception rather than IOException because in our experience POI can sometimes throw
			// NullPointerExceptions, ArrayIndexOOBExceptions, etc., in which case we can still fall-back on a
			// StringExtractor
			LOGGER.log(Level.INFO,
				"Exception while processing Excel document, switching to heuristic string extraction: " + id,
				e);

			try {
				stream.reset();
				applyStringExtractor(stream, result);
			}
			catch (IOException exc) {
				throw new ExtractorException(exc);
			}
		}
	}

	private void applyPoi(InputStream stream, RDFContainer result) throws IOException {
		// get the stream containing the Workbook
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(stream);
		DocumentInputStream docStream = poiFileSystem.createDocumentInputStream("Workbook");

		// setup a HSSFRequest object
		HSSFRequest request = new HSSFRequest();
		ExcelEventListener listener = new ExcelEventListener();

		// it is vital that the listener is registed as a listener for all Record types that it processes (see
		// below)
		request.addListener(listener, BoundSheetRecord.sid);
		request.addListener(listener, NumberRecord.sid);
		request.addListener(listener, SSTRecord.sid);
		request.addListener(listener, LabelSSTRecord.sid);
		request.addListener(listener, StringRecord.sid);
		request.addListener(listener, HeaderRecord.sid);
		request.addListener(listener, FooterRecord.sid);
		request.addListener(listener, NameRecord.sid);
		request.addListener(listener, SeriesTextRecord.sid);

		// process the document input stream
		HSSFEventFactory factory = new HSSFEventFactory();
		factory.processEvents(request, docStream);
		docStream.close();

		// store the extracted full-text
		String text = listener.getText();
		if (!text.equals("")) {
			result.put(AccessVocabulary.FULL_TEXT, text);
		}

		// extract all metadata
		PoiUtil.extractMetadata(poiFileSystem, result);
	}

	private void applyStringExtractor(InputStream stream, RDFContainer result) throws IOException {
		StringExtractor extractor = new StringExtractor();
		String text = extractor.extract(stream).trim();
		if (!text.equals("")) {
			result.put(AccessVocabulary.FULL_TEXT, text);
		}
	}

	private static class ExcelEventListener implements HSSFListener {

		private SSTRecord sstrec;

		private StringBuffer buffer = new StringBuffer(64 * 1024);

		public void processRecord(Record record) {
			// implementation note: make sure this HSSFListener is registered for all Record types used below
			switch (record.getSid()) {
			case BoundSheetRecord.sid:
				// append the sheet name
				BoundSheetRecord bsr = (BoundSheetRecord) record;
				String sheetName = bsr.getSheetname();
				if (sheetName != null) {
					buffer.append(END_OF_LINE);
					buffer.append(sheetName);
					buffer.append(END_OF_LINE);
					buffer.append(END_OF_LINE);
				}
				break;
			case NumberRecord.sid:
				// append the number inside this cell
				NumberRecord numrec = (NumberRecord) record;
				buffer.append(numrec.getValue());
				buffer.append(' ');
				break;
			case SSTRecord.sid:
				// keep the record for the next case statement
				sstrec = (SSTRecord) record;

				// the following code does extract all strings but does so in a rather random order and only
				// does it once for each unique string. The next case statements extracts the strings in the
				// order and amount in which they appear in the sheet.

				// int nrStrings = sstrec.getNumUniqueStrings();
				// for (int k = 0; k < nrStrings; k++) {
				// append(sstrec.getString(k).toString(), buffer);
				// }

				break;
			case LabelSSTRecord.sid:
				LabelSSTRecord lrec = (LabelSSTRecord) record;
				if (sstrec != null) {
					UnicodeString uString = sstrec.getString(lrec.getSSTIndex());
					if (uString != null) {
						append(uString.toString(), buffer);
					}
				}
				break;
			case StringRecord.sid:
				StringRecord sRecord = (StringRecord) record;
				append(sRecord.getString(), buffer);
				break;
			case HeaderRecord.sid:
				HeaderRecord hRecord = (HeaderRecord) record;
				append(hRecord.getHeader(), buffer);
				break;
			case FooterRecord.sid:
				FooterRecord fRecord = (FooterRecord) record;
				append(fRecord.getFooter(), buffer);
				break;
			case NameRecord.sid:
				NameRecord nRecord = (NameRecord) record;
				append(nRecord.getNameText(), buffer);
				append(nRecord.getDescriptionText(), buffer);
				break;
			case SeriesTextRecord.sid:
				SeriesTextRecord stRecord = (SeriesTextRecord) record;
				append(stRecord.getText(), buffer);
				break;
			}
		}

		private void append(String string, StringBuffer buffer) {
			if (string != null) {
				buffer.append(string);
				buffer.append(' ');
			}
		}

		public String getText() {
			return buffer.toString().trim();
		}
	}
}
