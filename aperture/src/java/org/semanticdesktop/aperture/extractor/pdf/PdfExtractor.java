/*
 * Copyright (c) 2005 - 2007 Aduna.
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

import org.ontoware.rdf2go.model.node.URI;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts full-text and metadata from Adobe Acrobat (PDF) files.
 */
public class PdfExtractor implements Extractor {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
            	// As of PDFBox 0.7.3, it is no longer possible to check if the passwords are emtpy.
                // if (document.isOwnerPassword("") || document.isUserPassword("")) {
                logger.info("Trying to decrypt " + id);
                document.decrypt("");
                logger.info("Decryption succeeded");
                // }
            }
            catch (CryptographyException e) {
                throw new ExtractorException(e);
            }
            catch (IOException e) {
                throw new ExtractorException(e);
            }
            catch (InvalidPasswordException e) {
                logger.info("Decryption failed", e);
            }
        }

        // extract the full-text
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text != null) {
                result.add(DATA.fullText, text);
            }
        }
        catch (IOException e) {
            // exception ends here, maybe we can still extract metadata
            logger.warn("IOException while extracting full-text of " + id, e);
        }

        // extract the metadata
        // note: we map both pdf:creator and pdf:producer to aperture:generator
        // note2: every call to PDFBox is wrapper in a separate try-catch, as an error
        // in one of these calls doesn't automatically mean that the others won't work well
        PDDocumentInformation metadata = document.getDocumentInformation();

        try {
            addStringMetadata(DATA.creator, metadata.getAuthor(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting author of " + id, e);
        }

        try {
            addStringMetadata(DATA.title, metadata.getTitle(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting title of " + id, e);
        }

        try {
            addStringMetadata(DATA.subject, metadata.getSubject(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting subject of " + id, e);
        }

        try {
            addStringMetadata(DATA.generator, metadata.getCreator(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting creator of " + id, e);
        }

        try {
            addStringMetadata(DATA.generator, metadata.getProducer(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting producer of " + id, e);
        }

        try {
            addCalendarMetadata(DATA.created, metadata.getCreationDate(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting creation date of " + id, e);
        }

        try {
            addCalendarMetadata(DATA.date, metadata.getModificationDate(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting modification date of " + id, e);
        }

        try {
            int nrPages = document.getNumberOfPages();
            if (nrPages >= 0) {
                result.add(DATA.pageCount, nrPages);
            }
        }
        catch (Exception e) {
            logger.warn("Exception while extracting number of pages of " + id, e);
        }

        try {
            String keywords = metadata.getKeywords();
            if (keywords != null) {
                StringTokenizer tokenizer = new StringTokenizer(keywords, " \t,;'\"|", false);
                while (tokenizer.hasMoreTokens()) {
                    String keyword = tokenizer.nextToken();
                    if (keyword != null) {
                        result.add(DATA.keyword, keyword);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.warn("Exception while extracting keywords of " + id, e);
        }
    }

    private void addStringMetadata(URI property, String value, RDFContainer result) {
        if (value != null) {
            result.add(property, value);
        }
    }

    private void addCalendarMetadata(URI property, Calendar value, RDFContainer result) {
        if (value != null) {
            result.add(property, value);
        }
    }
}
