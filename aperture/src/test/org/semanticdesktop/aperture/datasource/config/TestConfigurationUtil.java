/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.datasource.Vocabulary;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;

public class TestConfigurationUtil extends ApertureTestBase {

    private RDFContainer configuration;

    public void setUp() {
        configuration = new SesameRDFContainer("urn:test:dummysource");
    }

    public void tearDown() {
        configuration = null;
    }

    public void testRootUrl() {
        String url1 = "http://dummy.com";
        ConfigurationUtil.setRootUrl(url1, configuration);
        String url2 = ConfigurationUtil.getRootUrl(configuration);
        assertEquals(url1, url2);
    }

    public void testPassword() {
        String password1 = "p@ssw0rd";
        ConfigurationUtil.setPassword(password1, configuration);
        String password2 = ConfigurationUtil.getPassword(configuration);
        assertEquals(password1, password2);
    }

    public void testMaximumDepth() {
        int depth1 = 13;
        ConfigurationUtil.setMaximumDepth(depth1, configuration);
        Integer depth2 = ConfigurationUtil.getMaximumDepth(configuration);
        assertEquals(depth1, depth2.intValue());
    }

    public void testMaximumByteSize() {
        int size1 = 8192;
        ConfigurationUtil.setMaximumByteSize(size1, configuration);
        Integer size2 = ConfigurationUtil.getMaximumByteSize(configuration);
        assertEquals(size1, size2.intValue());
    }

    public void testIncludeHiddenResources() {
        boolean include1 = true;
        ConfigurationUtil.setIncludeHiddenResources(include1, configuration);
        Boolean include2 = ConfigurationUtil.getIncludeHiddenResourceS(configuration);
        assertEquals(include1, include2.booleanValue());
    }

    public void testConnectionSecurity() {
        String security1 = Vocabulary.SSL.toString();
        ConfigurationUtil.setConnectionSecurity(security1, configuration);
        String security2 = ConfigurationUtil.getConnectionSecurity(configuration);
        assertEquals(security1, security2);
    }

    /*
     * This method has temporarily been outcommented as it does not evaluate correctly due to a bug in
     * Sesame (see http://openrdf.org/issues/browse/SES-220).
     */
    
//     public void testDomainBoundaries() {
//        String javaString = ".java";
//        String cvsString = ".*/CVS/.*";
//
//        // first check that a boundaries is correctly stored and retrieved
//        DomainBoundaries boundaries1 = new DomainBoundaries();
//        boundaries1.addIncludePattern(new SubstringPattern(javaString, new SubstringCondition.EndsWith()));
//        boundaries1.addExcludePattern(new RegExpPattern(cvsString));
//        ConfigurationUtil.setDomainBoundaries(boundaries1, configuration);
//
//        DomainBoundaries boundaries2 = ConfigurationUtil.getDomainBoundaries(configuration);
//        assertEquals(1, boundaries2.getIncludePatterns().size());
//        assertEquals(1, boundaries2.getExcludePatterns().size());
//
//        SubstringPattern pattern1 = (SubstringPattern) boundaries2.getIncludePatterns().get(0);
//        assertEquals(javaString, pattern1.getSubstring());
//        assertTrue(pattern1.getCondition().getClass().getName().endsWith("SubstringCondition.EndsWith"));
//
//        RegExpPattern pattern2 = (RegExpPattern) boundaries2.getExcludePatterns().get(0);
//        assertEquals(cvsString, pattern2.getPatternString());
//
//        // check that the boundaries can also be completely removed
//        ConfigurationUtil.setDomainBoundaries(null, configuration);
//        DomainBoundaries boundaries3 = ConfigurationUtil.getDomainBoundaries(configuration);
//        assertEquals(0, boundaries3.getIncludePatterns().size());
//        assertEquals(0, boundaries3.getExcludePatterns().size());
//    }
}
