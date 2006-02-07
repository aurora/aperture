/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.util;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Features Apache POI-specific utility methods for text and metadata extraction purposes.
 */
public class PoiUtil {

	/**
	 * Returns the SummaryInformation holding the document metadata from a POIFSFileSystem. Any POI-related or
	 * I/O Exceptions that may occur during this operation are ignored and 'null' is returned in those cases.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @return A populated SummaryInformation, or 'null' when the relevant document parts could not be
	 *         located.
	 */
	public static SummaryInformation getSummaryInformation(POIFSFileSystem poiFileSystem) {
		SummaryInformation summary = null;

		try {
			DocumentInputStream docInputStream = poiFileSystem
					.createDocumentInputStream(SummaryInformation.DEFAULT_STREAM_NAME);
			summary = (SummaryInformation) PropertySetFactory.create(docInputStream);
			docInputStream.close();
		}
		catch (Exception e) {
			// ignore
		}

		return summary;
	}

	/**
	 * Extracts all metadata from the POIFSFileSystem's SummaryInformation and transforms it to RDF statements
	 * that are stored in the specified RDFContainer.
	 * 
	 * @param poiFileSystem The POI file system to obtain the metadata from.
	 * @param container The RDFContainer to store the created RDF statements in.
	 */
	public static void extractMetadata(POIFSFileSystem poiFileSystem, RDFContainer container) {
		SummaryInformation summary = getSummaryInformation(poiFileSystem);
		if (summary != null) {
			copyString(summary.getTitle(), AccessVocabulary.TITLE, container);
			copyString(summary.getSubject(), AccessVocabulary.SUBJECT, container);
			copyString(summary.getComments(), AccessVocabulary.DESCRIPTION, container);
			copyString(summary.getApplicationName(), AccessVocabulary.GENERATOR, container);
			copyString(summary.getAuthor(), AccessVocabulary.CREATOR, container);
			copyString(summary.getLastAuthor(), AccessVocabulary.CREATOR, container);

			copyDate(summary.getCreateDateTime(), AccessVocabulary.CREATION_DATE, container);
			copyDate(summary.getLastSaveDateTime(), AccessVocabulary.DATE, container);

			int nrPages = summary.getPageCount();
			if (nrPages > 1) {
				// '0' means 'unknown' according to POI's API (<sigh>)
				// '1' is often erroneously returned and can thus not be trusted
				// higher values tend to be right (not seen a counter example yet) and are
				// therefore included
				System.out.println(nrPages);
				container.put(AccessVocabulary.PAGE_COUNT, nrPages);
			}

			String keywords = summary.getKeywords();
			if (keywords != null) {
				StringTokenizer tokenizer = new StringTokenizer(keywords, " \t,;", false);
				while (tokenizer.hasMoreTokens()) {
					String keyword = tokenizer.nextToken();
					container.add(AccessVocabulary.KEYWORD, keyword);
				}
			}
		}
	}
	
	private static void copyString(String value, URI property, RDFContainer container) {
		if (value != null) {
			value = value.trim();
			if (!value.equals("")) {
				// NOTE: "add", not "put", as some properties will be used multiple times!!
				container.add(property, value);
			}
		}
	}

	private static void copyDate(Date date, URI property, RDFContainer container) {
		if (date != null) {
			container.add(property, date);
		}
	}
}
