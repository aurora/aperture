package org.semanticdesktop.aperture.extractor.jpg;

import java.io.InputStream;

import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;

/**
 * Class for extraction of various header information that can be retrieved from JPEG files.
 * Currently we support NEXIF and IPTC header tags.
 * NEXIF tags are mapped to the "NEXIF vocabulary workspace - RDF Schema" (http://www.w3.org/2003/12/NEXIF/)
 * 
 * @author Manuel Moeller, www.manuelm.org
 *
 */
public class JpgHeaderExtractor {

	/**
	 * member variable holding the metadata 
	 */
	private Metadata metadata = null;
	
	/**
	 * member variable holding a link to the NEXIF class in the metadata data structure 
	 */
	private Directory exifDirectory = null;
	
	/**
	 * reference to logger instance
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * URI for current file
	 */
	@SuppressWarnings("unused")
	private URI id = null;
	
	/**
	 * RDFContainer holding the results
	 */
	private RDFContainer result = null;
	
	/**
	 * Constructor.
	 * @param id
	 * @param jpegFile
	 * @param result
	 * @author moeller
	 */
	public JpgHeaderExtractor(URI id, InputStream jpegFile, RDFContainer result)  {
		this.id = id;
		this.result = result;
		
		try {
			this.metadata = JpegMetadataReader.readMetadata(jpegFile);
			this.exifDirectory = this.metadata.getDirectory(ExifDirectory.class);
		} catch (JpegProcessingException e) {
			logger.error("error extracting metadata");
			e.printStackTrace();
		}
		
	}
	
	/**
     * Extract varios information from JPEG NEXIF header.
     * 
     * @param id URI identifing the current document
     * @param stream InputStream of the current document
     * @param charset 
     * @param mimeType
     * @param result RDFContainer holding the extracted information
     * @author Manuel Moeller, www.manuelm.org
     * @throws ExtractorException
     */
	public void extractExif() {
        if (exifDirectory == null)
            return;
        addMetadataIfSet(ExifDirectory.TAG_APERTURE, NEXIF.apertureValue);
        addMetadataIfSet(ExifDirectory.TAG_ARTIST, NEXIF.artist);
        //addMetadataIfSet(ExifDirectory.TAG_BATTERY_LEVEL, NEXIF.);
        addMetadataIfSet(ExifDirectory.TAG_BITS_PER_SAMPLE, NEXIF.bitsPerSample);
        addMetadataIfSet(ExifDirectory.TAG_BRIGHTNESS_VALUE, NEXIF.brightnessValue);
        // continue here.
        addMetadataIfSet(ExifDirectory.TAG_COPYRIGHT, NEXIF.copyright);
        addMetadataIfSet(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT, NEXIF.height);
        addMetadataIfSet(ExifDirectory.TAG_EXIF_IMAGE_WIDTH, NEXIF.width);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_BIAS, NEXIF.exposureBiasValue);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_INDEX, NEXIF.exposureIndex);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_MODE, NEXIF.exposureMode);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_PROGRAM, NEXIF.exposureProgram);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_PROGRAM, NEXIF.exposureProgram);
        addMetadataIfSet(ExifDirectory.TAG_EXPOSURE_TIME, NEXIF.exposureTime);
        addMetadataIfSet(ExifDirectory.TAG_FLASH, NEXIF.flash);
        addMetadataIfSet(ExifDirectory.TAG_FLASH_ENERGY, NEXIF.flashEnergy);
        addMetadataIfSet(ExifDirectory.TAG_FLASHPIX_VERSION, NEXIF.flashpixVersion);
        addMetadataIfSet(ExifDirectory.TAG_MAKE, NEXIF.make);
    }

    
    
    /**
     * Checks if the passed tag is set in the exif data,
     * if it is set, add it to the result as literal string using the passed property
     * @param tag the tag to check any of {@link http://www.drewnoakes.com/code/NEXIF/javadoc/constant-values.html}
     * @param property the property to use
     */
    private void addMetadataIfSet(int tag, URI property) {
        String entry = exifDirectory.getString(tag);
        if (entry != null)
            result.add(property, entry);
    }

    public void extractIPTC() {
    	// TODO: implement!
    }
    
    public void extractGPS() {
    	// TODO: implement!
    }
    
}
