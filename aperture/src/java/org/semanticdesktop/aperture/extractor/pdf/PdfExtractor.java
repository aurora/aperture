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
import java.util.List;
import java.util.StringTokenizer;

import org.jempbox.xmp.XMPMetadata;
import org.jempbox.xmp.XMPSchemaDublinCore;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdmodel.common.PDMetadata;
import org.pdfbox.util.PDFTextStripper;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
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
        extractFullText(id, document, result);

        // extract the metadata
        extractNormalMetadata(id, document, result);
        
        // extract the additional bits from the XMP metadata (if they are there)
        extractXMPMetadata(id, document, result);
    }

    private void extractFullText(URI id, PDDocument document, RDFContainer result) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text != null) {
                result.add(NIE.plainTextContent, text);
            }
        }
        catch (IOException e) {
            // exception ends here, maybe we can still extract metadata
            logger.warn("IOException while extracting full-text of " + id, e);
        }
    }
    
    private void extractNormalMetadata(URI id, PDDocument document, RDFContainer result) {
        // note: we map both pdf:creator and pdf:producer to aperture:generator
        // note2: every call to PDFBox is wrapper in a separate try-catch, as an error
        // in one of these calls doesn't automatically mean that the others won't work well
        PDDocumentInformation metadata = document.getDocumentInformation();
        

        try {
            addContactStatement(NCO.creator, metadata.getAuthor(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting author of " + id, e);
        }

        try {
            addStringMetadata(NIE.title, metadata.getTitle(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting title of " + id, e);
        }

        try {
            addStringMetadata(NIE.subject, metadata.getSubject(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting subject of " + id, e);
        }

        try {
            addStringMetadata(NIE.generator, metadata.getCreator(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting creator of " + id, e);
        }

        try {
            addStringMetadata(NIE.generator, metadata.getProducer(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting producer of " + id, e);
        }

        try {
            addCalendarMetadata(NIE.contentCreated, metadata.getCreationDate(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting creation date of " + id, e);
        }

        try {
            addCalendarMetadata(NIE.contentLastModified, metadata.getModificationDate(), result);
        }
        catch (Exception e) {
            logger.warn("Exception while extracting modification date of " + id, e);
        }

        try {
            int nrPages = document.getNumberOfPages();
            if (nrPages >= 0) {
                result.add(RDF.type, NFO.PaginatedTextDocument);
                result.add(NFO.pageCount, nrPages);
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
                        result.add(NIE.keyword, keyword);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.warn("Exception while extracting keywords of " + id, e);
        }
    }

    @SuppressWarnings({ "unchecked", "cast" })
    private void extractXMPMetadata(URI id, PDDocument document, RDFContainer result) {
        try {
            PDDocumentInformation pddi = document.getDocumentInformation();
            PDMetadata md = document.getDocumentCatalog().getMetadata();
            if (md == null) { return; }
            XMPMetadata xmpmd = XMPMetadata.load(md.createInputStream());
            XMPSchemaDublinCore dcschema = xmpmd.getDublinCoreSchema();
            
            String creator = null;
            try {
                creator = pddi.getAuthor();
            } catch (Exception e) {
                // do nothing, this should have been done already
            }       
            try {
                addContactListMetadata(NCO.creator, dcschema.getCreators(), creator, result);
            }
            catch (Exception e) {
                logger.warn("Exception while extracting modification date of " + id, e);
            }
            
            try {
                addContactListMetadata(NCO.contributor, dcschema.getContributors(), null, result);
            }
            catch (Exception e) {
                logger.warn("Exception while extracting modification date of " + id, e);
            }
                        
        } catch (Exception e) {
            logger.warn("Exception while extracting XMP metadata of " + id,e);
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

    private void addContactStatement(URI uri, String fullname, RDFContainer container) {
        if (fullname != null) {
            Model model = container.getModel();
            Resource contactResource = UriUtil.generateRandomResource(model);
            model.addStatement(contactResource, RDF.type, NCO.Contact);
            model.addStatement(contactResource, NCO.fullname, fullname);
            container.add(uri, contactResource);
        }
    }
    
    private void addContactListMetadata(URI property, List<String> values, String omitValue, RDFContainer result) {
        if (values != null) {
            for (String value : values) {
                if (omitValue == null || !value.equals(omitValue)) {
                    addContactStatement(property, value, result);
                }
            }
        }
    }
}
