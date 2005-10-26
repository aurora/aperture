/*
 * Created on 26.10.2005
 * $Id$
 * 
 */
package org.semanticdesktop.aperture.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;

import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * 
 * Extractors are used to extract metadata and fulltext from InputStreams,
 * the inputstream is in a format passed by Mime-Type.
 * These extractors can produce RDFMaps.
 */
public interface Extractor {


    /**
     * create extracted information into the passed RDFMap called "result"
     * To see what fields should be needed and which must be added, look at the 
     * commments above
     * @param id the uri identifying the passed object. You may need it when you add sophisticated rdf information. It is also the topResource in the passed result
     * @param stream an opened inputstream which you can exclusively read. You must call the
stream.close() operation when you are finished extracting.
     * @param charset the charset in which the inputstream is encoded
     * @param mimetype the mimetype of the passed file/stream. If your extractor can handle multiple mime-types, this can be handy.
     * @param result - the place where the extracted data is to be written to 
     * @throws IOException when problems arise reading the stream.
     * @throws ExctractorException when the metadata of the stream cannot be extracted,
     * when the stream does not conform to the MimeType's norms.
     */
    public void extract(URI id, InputStream stream, Charset charset, String mimetype, RDFContainer result)  throws IOException, ExtractorException;


 /*
  inferior ALTERNATIVE:

    public RDFMap extract(URI id, InputStream stream, Charset charset, String mimetype)  throws IOException, DocumentExtractorException;

  inferior because with first, they only need to know the interface and with inferior they 
  have to know how to instantiate a RDFMap. Also performace of first is better, if the
  RDF store is sneaked and passed through the method */

 
}

/*
 * $Log$
 * Revision 1.2  2005/10/26 14:08:59  leo_sauermann
 * added the sesame-model and began with RDFContainer
 *
 * Revision 1.1  2005/10/26 08:27:08  leo_sauermann
 * first shot, the result of our 3 month discussion on https://gnowsis.opendfki.de/cgi-bin/trac.cgi/wiki/SemanticDataIntegrationFramework
 *
 */