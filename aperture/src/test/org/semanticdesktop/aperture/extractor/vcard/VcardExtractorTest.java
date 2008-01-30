/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.vcard;

import org.semanticdesktop.aperture.extractor.ExtractorTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * A test case for the vcard extractor
 */
public class VcardExtractorTest extends ExtractorTestBase {

    private RDFContainer metadata;
    
    public void testRfc2426ExampleExtraction() throws Exception {
        VcardExtractor extractor = new VcardExtractor();
        metadata = extract(DOCS_PATH + "vcard-rfc2426.vcf", extractor);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testOutlookExampleExtraction() throws Exception {
        VcardExtractor extractor = new VcardExtractor();
        metadata = extract(DOCS_PATH + "vcard-antoni-outlook2003.vcf", extractor);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testKontactExampleExtraction() throws Exception {
        VcardExtractor extractor = new VcardExtractor();
        metadata = extract(DOCS_PATH + "vcard-antoni-kontact.vcf", extractor);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testDirkExtraction() throws Exception {
        VcardExtractor extractor = new VcardExtractor();
        metadata = extract(DOCS_PATH + "vcard-dirk.vcf", extractor);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
    
    public void testSapVcardsExtraction() throws Exception {
        VcardExtractor extractor = new VcardExtractor();
        metadata = extract(DOCS_PATH + "vcard-vCards-SAP.vcf", extractor);
        validate(metadata);
        metadata.dispose();
        metadata = null;
    }
}

