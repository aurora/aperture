/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.subcrawler;

import org.ontoware.rdf2go.model.node.impl.URIImpl;

import junit.framework.TestCase;

/**
 * Tests for {@link SubCrawlerUtil}
 */
public class TestSubCrawlerUtil extends TestCase {

    /**
     * Tests the {@link SubCrawlerUtil#getRootObjectUri(org.ontoware.rdf2go.model.node.URI)} method
     */
    public void testGetRootObjectUri() {
        rootTest("zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
        		 "/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx",
                 "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml");
        rootTest("file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml",
                 "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml");
        rootTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com",
                 "file:///D:/mails/mail.eml");
    }
    
    /**
     * Tests the {@link SubCrawlerUtil#getRootObjectUri(org.ontoware.rdf2go.model.node.URI)} method
     */
    public void testGetParentObjectUri() {
        parentTest("zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
        		   "/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx",
                   "mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
                   "/86b313dc282850fef1762fb400171750%2540amrapali.com#1");
        parentTest("file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml",null);
        parentTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com",
                   "file:///D:/mails/mail.eml");
    }

    /**
     * Tests the {@link SubCrawlerUtil#getSubCrawlerPrefix(org.ontoware.rdf2go.model.node.URI)} method
     */
    public void testGetSubCrawlerPrefix() {
        prefixTest("zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
        		   "/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx","zip");
        prefixTest("file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml",null);
        prefixTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com","mime");
    }
    
    /**
     * Tests the {@link SubCrawlerUtil#getSubCrawledObjectPath(org.ontoware.rdf2go.model.node.URI)} method
     */
    public void testGetSubCrawledObjectPath() {
        pathTest("zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
                   "/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx","/Board paper.docx");
        pathTest("file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml",null);
        pathTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com","/234873476-23467@domain.com");
    }
    
    /**
     * Tests the {@link SubCrawlerUtil#getSubCrawledObjectPath(org.ontoware.rdf2go.model.node.URI)} method
     */
    public void testIsSubcrawledObjectUri() {
        uriTest("zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!" +
                   "/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx",true);
        uriTest("file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml",false);
        uriTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com",true);
        uriTest("mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com!/#1",false); // too many paths
        uriTest("zip:mime:file:///D:/mails/mail.eml!/234873476-23467@domain.com",false); // too many prefixes
    }

    private void rootTest(String subCrawlerObjectUri, String expectedUri) {
        assertEquals(new URIImpl(expectedUri),SubCrawlerUtil.getRootObjectUri(new URIImpl(subCrawlerObjectUri)));
    }
    
    private void parentTest(String subCrawledObjectUri, String expectedUri) {
        if (expectedUri == null) {
            assertNull(SubCrawlerUtil.getParentObjectUri(new URIImpl(subCrawledObjectUri)));
        } else {
            assertEquals(new URIImpl(expectedUri),SubCrawlerUtil.getParentObjectUri(new URIImpl(subCrawledObjectUri)));
        }
    }
    
    private void prefixTest(String subCrawledObjectUri, String expectedPrefix) {
        if (expectedPrefix == null) {
            assertNull(SubCrawlerUtil.getSubCrawlerPrefix(new URIImpl(subCrawledObjectUri)));
        } else {
            assertEquals(expectedPrefix,SubCrawlerUtil.getSubCrawlerPrefix(new URIImpl(subCrawledObjectUri)));
        }
    }
    
    private void pathTest(String subCrawledObjectUri, String expectedPath) {
        if (expectedPath == null) {
            assertNull(SubCrawlerUtil.getSubCrawledObjectPath(new URIImpl(subCrawledObjectUri)));
        } else {
            assertEquals(expectedPath,SubCrawlerUtil.getSubCrawledObjectPath(new URIImpl(subCrawledObjectUri)));
        }     
    }
    
    private void uriTest(String string, boolean b) {
        assertEquals(b, SubCrawlerUtil.isSubcrawledObjectUri(new URIImpl(string)));
    }
}

