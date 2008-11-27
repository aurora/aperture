/*
 * Copyright (c) 2005 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.extractor.jpg;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.GEO;
import org.semanticdesktop.aperture.vocabulary.NEXIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;

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

    private Directory iptcDirectory;

    private Directory gpsDirectory;
	
    private NumberFormat degreesFormat;
    
	/**
	 * Constructor.
	 * @param id
	 * @param jpegFile
	 * @param result
	 */
	public JpgHeaderExtractor(URI id, InputStream jpegFile, RDFContainer result)  {
		this.id = id;
		this.result = result;
		DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.US);
		this.degreesFormat = new DecimalFormat("0.#########",sym);
		try {
			this.metadata = JpegMetadataReader.readMetadata(jpegFile);
			this.exifDirectory = this.metadata.getDirectory(ExifDirectory.class);
			this.iptcDirectory = this.metadata.getDirectory(IptcDirectory.class);
			this.gpsDirectory = this.metadata.getDirectory(GpsDirectory.class);
		} catch (JpegProcessingException e) {
			logger.warn("error extracting metadata",e);
		}
	}
	
	/**
     * Extract varios information from JPEG NEXIF header.
     * 
     * @throws ExtractorException
     */
	public void extractExif() {
        if (exifDirectory == null)
            return;
        addStringMetadataIfSet(ExifDirectory.TAG_APERTURE, NEXIF.apertureValue, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_ARTIST, NEXIF.artist, exifDirectory);
        //addMetadataIfSet(ExifDirectory.TAG_BATTERY_LEVEL, NEXIF.);
        addStringMetadataIfSet(ExifDirectory.TAG_BITS_PER_SAMPLE, NEXIF.bitsPerSample, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_BRIGHTNESS_VALUE, NEXIF.brightnessValue, exifDirectory);
        // continue here.
        addStringMetadataIfSet(ExifDirectory.TAG_COPYRIGHT, NEXIF.copyright, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT, NEXIF.height, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXIF_IMAGE_WIDTH, NEXIF.width, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_BIAS, NEXIF.exposureBiasValue, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_INDEX, NEXIF.exposureIndex, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_MODE, NEXIF.exposureMode, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_PROGRAM, NEXIF.exposureProgram, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_PROGRAM, NEXIF.exposureProgram, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_EXPOSURE_TIME, NEXIF.exposureTime, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_FLASH, NEXIF.flash, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_FLASH_ENERGY, NEXIF.flashEnergy, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_FLASHPIX_VERSION, NEXIF.flashpixVersion, exifDirectory);
        addStringMetadataIfSet(ExifDirectory.TAG_MAKE, NEXIF.make, exifDirectory);
    }

    
    
    /**
     * Checks if the passed tag is set in the exif data,
     * if it is set, add it to the result as literal string using the passed property
     * @param tag the tag to check any of {@link http://www.drewnoakes.com/code/EXIF/javadoc/constant-values.html}
     * @param property the property to use
     */
    private void addStringMetadataIfSet(int tag, URI property, Directory directory) {
        String entry = directory.getString(tag);
        if (entry != null)
            result.add(property, entry);
    }

    private double getRationalArrayValue(int tag, Directory directory) {
        if (!directory.containsTag(tag)) {
            return Double.NaN;
        }
        try {
            Rational [] rationalArray = directory.getRationalArray(tag);
            
            double resultDouble = 
                rationalArray[0].doubleValue() + 
                rationalArray[1].doubleValue()/60.0 + 
                rationalArray[2].doubleValue()/3600;
            return resultDouble;
        }
        catch (MetadataException e) {
            return Double.NaN;
        }
    }
    
    public void extractIPTC() {
    	// TODO: implement!
    }
    
    public void extractGPS() {      
        if (gpsDirectory == null) {
            return;
        }
        String longitude = gpsDirectory.getString(GpsDirectory.TAG_GPS_LONGITUDE);
        String latitude = gpsDirectory.getString(GpsDirectory.TAG_GPS_LATITUDE);
        String altitude = gpsDirectory.getString(GpsDirectory.TAG_GPS_ALTITUDE);
        if (longitude == null && latitude == null && altitude == null) {
            return;
        }
        Model model = result.getModel();
        Resource point = UriUtil.generateRandomResource(model);
        result.add(NEXIF.gps, point);
        model.addStatement(point,RDF.type,GEO.Point);
        if (altitude != null) {
            double alt = getRationalArrayValue(GpsDirectory.TAG_GPS_ALTITUDE, gpsDirectory);
            model.addStatement(point,GEO.alt,degreesFormat.format(alt));
        }
        if (longitude != null) {
            double lon = getRationalArrayValue(GpsDirectory.TAG_GPS_LONGITUDE, gpsDirectory);
            String ref = gpsDirectory.getString(GpsDirectory.TAG_GPS_LONGITUDE_REF);
            if (ref != null && ref.equalsIgnoreCase("W")) {
                lon = -lon;
            }
            model.addStatement(point,GEO.long_,degreesFormat.format(lon));
        }
        if (latitude != null) {
            double lat = getRationalArrayValue(GpsDirectory.TAG_GPS_LATITUDE, gpsDirectory);
            String ref = gpsDirectory.getString(GpsDirectory.TAG_GPS_LATITUDE_REF);
            if (ref != null && ref.equalsIgnoreCase("S")) {
                lat = -lat;
            }
            model.addStatement(point, GEO.lat,degreesFormat.format(lat));
        }
    }
}
