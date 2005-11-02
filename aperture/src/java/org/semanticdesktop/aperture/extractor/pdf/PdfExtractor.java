/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.Vocabulary;

/**
 * Extracts full-text and metadata from Adobe Acrobat (PDF) files.
 */
public class PdfExtractor implements Extractor {
    private static final Logger LOGGER = Logger.getLogger(PdfExtractor.class.getName());

    public void extract(URI id, InputStream stream, Charset charset, String mimetype, RDFContainer result)
            throws ExtractorException {
        // setup a PDDocument
        PDDocument document = null;

        try {
            try {
                PDFParser parser = new PDFParser(stream);
                parser.parse();
                document = parser.getPDDocument();
            }
            catch (IOException e) {
                throw new ExtractorException(e);
            }

            // decrypt and extract info from this document
            processDocument(id, document, result);
        }
        finally {
            if (document != null) {
                // close the document
                try {
                    document.close();
                }
                catch (IOException e) {
                    throw new ExtractorException(e);
                }
            }
        }
    }

    private void processDocument(URI id, PDDocument document, RDFContainer result) throws ExtractorException {
        // try to decrypt it, if necessary
        if (document.isEncrypted()) {
            try {
                if (document.isOwnerPassword("") || document.isUserPassword("")) {
                    LOGGER.log(Level.INFO, "Trying to decrypt " + id);
                    document.decrypt("");
                    LOGGER.log(Level.INFO, "Decryption succeeded");
                }
            }
            catch (CryptographyException e) {
                throw new ExtractorException(e);
            }
            catch (IOException e) {
                throw new ExtractorException(e);
            }
            catch (InvalidPasswordException e) {
                LOGGER.log(Level.INFO, "Decryption failed", e);
            }
        }

        // extract the full-text
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text != null) {
                result.put(Vocabulary.FULL_TEXT_URI, text);
            }
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // extract the metadata
        // note: we map both pdf:creator and pdf:producer to aperture:generator
        PDDocumentInformation metadata = document.getDocumentInformation();

        addStringMetadata(Vocabulary.CREATOR_URI, metadata.getAuthor(), result);
        addStringMetadata(Vocabulary.TITLE_URI, metadata.getTitle(), result);
        addStringMetadata(Vocabulary.SUBJECT_URI, metadata.getSubject(), result);
        addStringMetadata(Vocabulary.GENERATOR_URI, metadata.getCreator(), result);
        addStringMetadata(Vocabulary.GENERATOR_URI, metadata.getProducer(), result);

        try {
            addCalendarMetadata(Vocabulary.CREATION_DATE_URI, metadata.getCreationDate(), result);
            addCalendarMetadata(Vocabulary.DATE_URI, metadata.getModificationDate(), result);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        int nrPages = document.getNumberOfPages();
        if (nrPages > 0) {
            result.put(Vocabulary.PAGE_COUNT_URI, nrPages);
        }

        String keywords = metadata.getKeywords();
        if (keywords != null) {
            StringTokenizer tokenizer = new StringTokenizer(keywords, " ,\t", false);
            while (tokenizer.hasMoreTokens()) {
                String keyword = tokenizer.nextToken();
                if (keyword != null) {
                    result.put(Vocabulary.KEYWORD_URI, keyword);
                }
            }
        }
    }

    private void addStringMetadata(org.openrdf.model.URI property, String value, RDFContainer result) {
        if (value != null) {
            result.put(property, value);
        }
    }

    private void addCalendarMetadata(org.openrdf.model.URI property, Calendar value, RDFContainer result) {
        if (value != null) {
            result.put(property, value);
        }
    }
}
