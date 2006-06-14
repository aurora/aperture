/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource.config;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sesame.repository.Repository;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.sesame.SesameRDFContainer;
import org.semanticdesktop.aperture.vocabulary.DATASOURCE;

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
        long size1 = 8192L;
        ConfigurationUtil.setMaximumByteSize(size1, configuration);
        Long size2 = ConfigurationUtil.getMaximumByteSize(configuration);
        assertEquals(size1, size2.longValue());
    }

    public void testIncludeHiddenResources() {
        boolean include1 = true;
        ConfigurationUtil.setIncludeHiddenResources(include1, configuration);
        Boolean include2 = ConfigurationUtil.getIncludeHiddenResourceS(configuration);
        assertEquals(include1, include2.booleanValue());
    }

    public void testConnectionSecurity() {
        String security1 = DATASOURCE.SSL.toString();
        ConfigurationUtil.setConnectionSecurity(security1, configuration);
        String security2 = ConfigurationUtil.getConnectionSecurity(configuration);
        assertEquals(security1, security2);
    }

    public void testDomainBoundaries() throws RDFHandlerException {
        String javaString = ".java";
        String cvsString = ".*/CVS/.*";

        // first check that a boundaries is correctly stored and retrieved
        DomainBoundaries boundaries1 = new DomainBoundaries();
        boundaries1.addIncludePattern(new SubstringPattern(javaString, new SubstringCondition.EndsWith()));
        boundaries1.addExcludePattern(new RegExpPattern(cvsString));
        ConfigurationUtil.setDomainBoundaries(boundaries1, configuration);

        DomainBoundaries boundaries2 = ConfigurationUtil.getDomainBoundaries(configuration);
        assertEquals(1, boundaries2.getIncludePatterns().size());
        assertEquals(1, boundaries2.getExcludePatterns().size());

        SubstringPattern pattern1 = (SubstringPattern) boundaries2.getIncludePatterns().get(0);
        assertEquals(javaString, pattern1.getSubstring());
        assertTrue(pattern1.getCondition().getClass().equals(SubstringCondition.EndsWith.class));

        RegExpPattern pattern2 = (RegExpPattern) boundaries2.getExcludePatterns().get(0);
        assertEquals(cvsString, pattern2.getPatternString());

        /*
         * The folling test fails, probably due to a bug in Sesame 2.0 alpha 1 (see
         * http://www.openrdf.org/issues/browse/SES-221).
         */

         // check that the boundaries can also be completely removed
         ConfigurationUtil.setDomainBoundaries(null, configuration);
         DomainBoundaries boundaries3 = ConfigurationUtil.getDomainBoundaries(configuration);
         assertEquals(0, boundaries3.getIncludePatterns().size());
         assertEquals(0, boundaries3.getExcludePatterns().size());
         
         assertEquals(0,((Repository)configuration.getModel()).size());
    }
}
