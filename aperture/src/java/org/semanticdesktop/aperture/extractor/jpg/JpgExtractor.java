package org.semanticdesktop.aperture.extractor.jpg;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.pdfbox.util.operator.NextLine;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;


/**
 * Implementation of JPG extractor for Aperture. Only a frontend to fuctionality from
 * classes JpgHeaderExtractor (EXIF, IPTC headers) and JpgContentExtractor (various features
 * on the pictures).
 * 
 * @author Manuel Moeller
 *
 */
public class JpgExtractor implements Extractor {

	/**
	 * Calls the appropriate methods from {@link JpgHeaderExtractor} and {@link JpgBodyExtractor}.
	 * 
	 * @author Manuel Moeller
	 * @param id Aperture URI of the current file
	 * @param stream InputStream of the current file
	 * @param charset irrelevant for images
	 * @param mimeType irrelevant for images
	 * @param result RDFContainer holding the results
	 * 
	 */
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
    throws ExtractorException {
    	JpgHeaderExtractor jpgHeaderExtractor = new JpgHeaderExtractor(id, stream, result);
    	jpgHeaderExtractor.extractExif();
    	jpgHeaderExtractor.extractIPTC();
    	jpgHeaderExtractor.extractGPS();
    	result.add(RDF.type,NEXIF.Photo);
    }
}