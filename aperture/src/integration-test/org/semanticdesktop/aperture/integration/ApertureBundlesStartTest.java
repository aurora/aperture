package org.semanticdesktop.aperture.integration;
import java.io.File;

import org.osgi.framework.ServiceReference;
import org.semanticdesktop.aperture.crawler.CrawlerRegistry;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.mime.MimeExtractorFactory;
import org.semanticdesktop.aperture.extractor.word.WordExtractorFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.test.provisioning.ArtifactLocator;

/**
 * THIS TEST REQUIRES YOU TO TYPE 'ant prepareosgirelease' BEFORE RUNNING IN ECLIPSE.
 * 
 * The bundles must be built, because they are loaded from files under concrete hard-coded paths and not from
 * the eclipse classpath.
 * 
 * It checks if the aperture bundles shipped with the osgirelease actually resolve properly and start.
 */
public class ApertureBundlesStartTest extends AbstractConfigurableBundleCreatorTests {

	public void testThreeApertureBundlesStarted() throws Exception {
		String filter = "(objectclass=" + CrawlerRegistry.class.getName() + ")";
		ServiceReference [] refs = bundleContext.getServiceReferences(null, filter);
		
		///////////////////////////////////////////////////////////////////
		//// THE CORE BUNDLE SHOULD START, THEREFORE ONE CRAWLER       ////
		//// REGISTRY SHOULD BE AVAILABLE                              ////
		///////////////////////////////////////////////////////////////////
		assertEquals(1, refs.length); 

		
        ///////////////////////////////////////////////////////////////////
		//// THE IMPL AND THE SAFE BUNDLES SHOULD START                ////
		//// WE TEST FOR THE AVAILABILITY OF ONE FACTORY FROM THE SAFE ////
		//// BUNDLE - THE WORD EXTRACTOR, AND ONE FACTORY FROM THE IMPL////
		//// BUNDLE - THE MIME EXTRACTOR                               ////
		///////////////////////////////////////////////////////////////////
		
		boolean implOK = false;
		boolean safeOK = false;
		
		filter = "(objectclass=" + ExtractorFactory.class.getName() + ")";
		refs = bundleContext.getServiceReferences(null, filter);
		for (int i = 0; i < refs.length; i++) {
			Object obj = bundleContext.getService(refs[i]);
			if (obj instanceof WordExtractorFactory) {
				safeOK = true;
			} else if (obj instanceof MimeExtractorFactory) {
				implOK = true;
			}
			bundleContext.ungetService(refs[i]);
		}
		assertTrue(implOK && safeOK);
		
	}
	
	
	@Override
	protected ArtifactLocator getLocator() {
		return new ApertureArtifactLocator();
	}	

	@Override
	protected String getRootPath() {
		return "file:./build/classes-integration";
	}
	
	@Override
	protected String[] getTestBundlesNames() {
		return new String[] {
				"com.drew.metadata,                 com.drew.metadata,                 2.4.0",
				"javax.activation,                  javax.activation,                  1.1.1",
				"javax.mail,                        javax.mail,                        1.4.1",
				"javax.xml,                         javax.xml,                         1.3.4.v200806030440",
				"jcl104-over-slf4j,                 jcl104-over-slf4j,                 1.5.0",
				"openrdf-sesame-onejar-osgi,        openrdf-sesame-onejar-osgi,        2.2.3",
				"com.sun.jai.codec,                 com.sun.jai.codec,                 1.1.3",
				"com.sun.jai.core,                  com.sun.jai.core,                  1.1.3",
				"org.apache.poi,                    org.apache.poi,                    3.2.0",
				"org.bouncycastle.bcmail,           org.bouncycastle.bcmail,           1.32.0",
				"org.bouncycastle.bcprovider,       org.bouncycastle.bcprovider,       1.32.0",
				"org.fontbox,                       org.fontbox,                       0.2.0",
				"org.htmlparser,                    org.htmlparser,                    1.6.0",
				"org.jempbox.xmp,                   org.jempbox.xmp,                   0.2.0",
				"org.pdfbox,                        org.pdfbox,                        0.7.4",
				"org.semanticdesktop.aperture,      org.semanticdesktop.aperture,      1.2.0",
				"org.semanticdesktop.aperture.impl, org.semanticdesktop.aperture.impl, 1.2.0",
				"org.semanticdesktop.aperture.safe, org.semanticdesktop.aperture.safe, 1.2.0",
				"rdf2go.api,                        rdf2go.api,                        4.7.0",
				"rdf2go.impl.sesame22,              rdf2go.impl.sesame22,              4.7.0",
				"slf4j-api,                         slf4j-api,                         1.5.0",
				"slf4j-jdk14,                       slf4j-jdk14,                       1.5.0"};
	}
	

	private static class ApertureArtifactLocator implements ArtifactLocator {

		public Resource locateArtifact(String groupId, String artifactId, String version) {
			File file1 = new File("./integration-test-lib/" + artifactId + "-" + version + ".jar");
			File file3 = new File("./integration-test-lib/" + artifactId + "_" + version + ".jar");
			File file2 = new File("./build/osgirelease/lib/" + artifactId + "_" + version + ".jar");
			File file4 = new File("./build/osgirelease/lib/" + artifactId + "-" + version + ".jar");
			if (file1.exists()) {
				return new FileSystemResource(file1);
			} else if (file2.exists()){
				return new FileSystemResource(file2);
			} else if (file3.exists()){
				return new FileSystemResource(file3);
			} else if (file4.exists()){
				return new FileSystemResource(file4);
			} else {
				System.out.println("group: " + groupId + " id: " + artifactId + " version: " + version );
				return null;
			}
		}

		public Resource locateArtifact(String arg0, String arg1, String arg2,
				String arg3) {
			System.out.println("group: " + arg0 + " id: " + arg1 + " version: " + arg2 + " type: " + arg3);
			return null;
		}
	}	
}
