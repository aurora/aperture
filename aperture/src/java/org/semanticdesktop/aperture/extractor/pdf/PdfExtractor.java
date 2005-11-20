/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.semanticdesktop.aperture.accessor.Vocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Extracts full-text and metadata from Adobe Acrobat (PDF) files.
 */
public class PdfExtractor implements Extractor {

    private static final Logger LOGGER = Logger.getLogger(PdfExtractor.class.getName());

    public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
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
                result.put(Vocabulary.FULL_TEXT, text);
            }
        }
        catch (IOException e) {
            // exception ends here, maybe we can still extract metadata
            LOGGER.log(Level.WARNING, "IOException while extracting full-text", e);
        }

        // extract the metadata
        // note: we map both pdf:creator and pdf:producer to aperture:generator
        // note2: every call to PDFBox is wrapper in a separate try-catch, as an error
        // in one of these calls doesn't automatically mean that the others won't work well
        PDDocumentInformation metadata = document.getDocumentInformation();

        try {
            putStringMetadata(Vocabulary.CREATOR, metadata.getAuthor(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting author", e);
        }

        try {
            putStringMetadata(Vocabulary.TITLE, metadata.getTitle(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting title", e);
        }

        try {
            putStringMetadata(Vocabulary.SUBJECT, metadata.getSubject(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting subject", e);
        }

        try {
            addStringMetadata(Vocabulary.GENERATOR, metadata.getCreator(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting creator", e);
        }

        try {
            addStringMetadata(Vocabulary.GENERATOR, metadata.getProducer(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting producer", e);
        }

        try {
            putCalendarMetadata(Vocabulary.CREATION_DATE, metadata.getCreationDate(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting creation date", e);
        }

        try {
            putCalendarMetadata(Vocabulary.DATE, metadata.getModificationDate(), result);
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting modification date", e);
        }

        try {
            int nrPages = document.getNumberOfPages();
            if (nrPages >= 0) {
                result.put(Vocabulary.PAGE_COUNT, nrPages);
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting number of pages", e);
        }

        try {
            String keywords = metadata.getKeywords();
            if (keywords != null) {
                StringTokenizer tokenizer = new StringTokenizer(keywords, " \t,;'\"|", false);
                while (tokenizer.hasMoreTokens()) {
                    String keyword = tokenizer.nextToken();
                    if (keyword != null) {
                        result.add(Vocabulary.KEYWORD, keyword);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while extracting keywords", e);
        }
    }

    private void putStringMetadata(org.openrdf.model.URI property, String value, RDFContainer result) {
        if (value != null) {
            result.put(property, value);
        }
    }

    private void addStringMetadata(org.openrdf.model.URI property, String value, RDFContainer result) {
        if (value != null) {
            result.add(property, value);
        }
    }

    private void putCalendarMetadata(org.openrdf.model.URI property, Calendar value, RDFContainer result) {
        if (value != null) {
            result.put(property, value);
        }
    }
}
