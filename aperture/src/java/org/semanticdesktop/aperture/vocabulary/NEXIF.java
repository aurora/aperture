package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Fri Nov 02 12:57:24 CET 2007
 * input file: D:\workspace\aperture/doc/ontology/nexif.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#
 */
public class NEXIF {

    /** Path to the ontology resource */
    public static final String NEXIF_RESOURCE_PATH = 
      NEXIF.class.getPackage().getName().replace('.', '/') + "/nexif.rdfs";

    /**
     * Puts the NEXIF ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNEXIFOntology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NEXIF_RESOURCE_PATH, NEXIF.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NEXIF_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NEXIF */
    public static final URI NS_NEXIF = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#");
    /**
     * Type: Class <br/>
     * Label: Photo  <br/>
     * Comment: An Image File Directory  <br/>
     */
    public static final URI Photo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo");
    /**
     * Type: Property <br/>
     * Label: apertureValue  <br/>
     * Comment: tagNumber: 37378
The lens aperture. The unit is the APEX value.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI apertureValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#apertureValue");
    /**
     * Type: Property <br/>
     * Label: artist  <br/>
     * Comment: tagNumber: 315
Person who created the image  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI artist = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#artist");
    /**
     * Type: Property <br/>
     * Label: bitsPerSample  <br/>
     * Comment: tagNumber: 258
The number of bits per image component. In this standard each component of the image is 8 bits, so the value for this tag is 8. See also SamplesPerPixel. In JPEG compressed data a JPEG marker is used instead of this tag.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI bitsPerSample = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#bitsPerSample");
    /**
     * Type: Property <br/>
     * Label: brightnessValue  <br/>
     * Comment: tagNumber: 37379
The value of brightness. The unit is the APEX value. Ordinarily it is given in the range of -99.99 to 99.99. Note that if the numerator of the recorded value is FFFFFFFF.H, Unknown shall be indicated.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI brightnessValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#brightnessValue");
    /**
     * Type: Property <br/>
     * Label: cfaPattern  <br/>
     * Comment: tagNumber: 41730
The color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor is used. It does not apply to all sensing methods.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI cfaPattern = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#cfaPattern");
    /**
     * Type: Property <br/>
     * Label: colorSpace  <br/>
     * Comment: tagNumber: 40961
The color space information tag (ColorSpace) is always recorded as the color space specifier. Normally sRGB (=1) is used to define the color space based on the PC monitor conditions and environment.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI colorSpace = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#colorSpace");
    /**
     * Type: Property <br/>
     * Label: componentsConfiguration  <br/>
     * Comment: Information specific to compressed data. The channels of each component are arranged in order from the 1st component to the 4th. For uncompressed data the data arrangement is given in the PhotometricInterpretation tag. However, since PhotometricInterpretation can only express the order of Y,Cb and Cr, this tag is provided for cases when compressed data uses components other than Y, Cb, and Cr and to enable support of other sequences.
tagNumber: 37121  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI componentsConfiguration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#componentsConfiguration");
    /**
     * Type: Property <br/>
     * Label: compressedBitsPerPixel  <br/>
     * Comment: tagNumber: 37122
Information specific to compressed data. The compression mode used for a compressed image is indicated in unit bits per pixel.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI compressedBitsPerPixel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#compressedBitsPerPixel");
    /**
     * Type: Property <br/>
     * Label: compression  <br/>
     * Comment: The compression scheme used for the image data. When a primary image is JPEG compressed, this designation is not necessary and is omitted. When thumbnails use JPEG compression, this tag value is set to 6.
tagNumber: 259  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI compression = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#compression");
    /**
     * Type: Property <br/>
     * Label: contrast  <br/>
     * Comment: tagNumber: 41992
The direction of contrast processing applied by the camera when the image was shot.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contrast = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#contrast");
    /**
     * Type: Property <br/>
     * Label: copyright  <br/>
     * Comment: tagNumber: 33432
Copyright information. In this standard the tag is used to indicate both the photographer and editor copyrights. It is the copyright notice of the person or organization claiming rights to the image.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI copyright = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#copyright");
    /**
     * Type: Property <br/>
     * Label: customRendered  <br/>
     * Comment: The use of special processing on image data, such as rendering geared to output. When special processing is performed, the reader is expected to disable or minimize any further processing.
tagNumber: 41985  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI customRendered = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#customRendered");
    /**
     * Type: Property <br/>
     * Label: datatype  <br/>
     * Comment: The Exif field data type, such as ascii, byte, short etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI datatype = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#datatype");
    /**
     * Type: Property <br/>
     * Label: date  <br/>
     * Comment: a date information. Usually saved as YYYY:MM:DD (HH:MM:SS) format in Exif data, but represented here as W3C-DTF format  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI date = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#date");
    /**
     * Type: Property <br/>
     * Label: dateAndOrTime  <br/>
     * Comment: An attribute relating to Date and/or Time  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI dateAndOrTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#dateAndOrTime");
    /**
     * Type: Property <br/>
     * Label: dateTime  <br/>
     * Comment: The date and time of image creation. In this standard it is the date and time the file was changed.
tagNumber: 306  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI dateTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#dateTime");
    /**
     * Type: Property <br/>
     * Label: dateTimeDigitized  <br/>
     * Comment: The date and time when the image was stored as digital data. If, for example, an image was captured by DSC and at the same time the file was recorded, then the DateTimeOriginal and DateTimeDigitized will have the same contents.
tagNumber: 36868  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI dateTimeDigitized = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#dateTimeDigitized");
    /**
     * Type: Property <br/>
     * Label: dateTimeOriginal  <br/>
     * Comment: tagNumber: 36867
The date and time when the original image data was generated. For a DSC the date and time the picture was taken are recorded.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI dateTimeOriginal = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#dateTimeOriginal");
    /**
     * Type: Property <br/>
     * Label: deviceSettingDescription  <br/>
     * Comment: tagNumber: 41995
Information on the picture-taking conditions of a particular camera model. The tag is used only to indicate the picture-taking conditions in the reader.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI deviceSettingDescription = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#deviceSettingDescription");
    /**
     * Type: Property <br/>
     * Label: digitalZoomRatio  <br/>
     * Comment: tagNumber: 41988
The digital zoom ratio when the image was shot. If the numerator of the recorded value is 0, this indicates that digital zoom was not used.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI digitalZoomRatio = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#digitalZoomRatio");
    /**
     * Type: Property <br/>
     * Label: exifAttribute  <br/>
     * Comment: A property that connects an IFD (or other resource) to one of its entries (Exif attribute). Super property which integrates all Exif tags. Domain definition dropped so that this vocabulary can be used to describe not only Exif IFD, but also general image.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exifAttribute = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exifAttribute");
    /**
     * Type: Property <br/>
     * Label: exifIFDPointer  <br/>
     * Comment: tagNumber: 34665
A pointer to the Exif IFD, which is a set of tags for recording Exif-specific attribute information.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI exifIFDPointer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exifIFDPointer");
    /**
     * Type: Property <br/>
     * Label: exifVersion  <br/>
     * Comment: tagNumber: 36864
Exif Version  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exifVersion = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exifVersion");
    /**
     * Type: Property <br/>
     * Label: exifdata  <br/>
     * Comment: An Exif IFD data entry  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exifdata = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exifdata");
    /**
     * Type: Property <br/>
     * Label: exposureBiasValue  <br/>
     * Comment: tagNumber: 37380
The exposure bias. The unit is the APEX value. Ordinarily it is given in the range of -99.99 to 99.99.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exposureBiasValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exposureBiasValue");
    /**
     * Type: Property <br/>
     * Label: exposureIndex  <br/>
     * Comment: The exposure index selected on the camera or input device at the time the image is captured.
tagNumber: 41493  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exposureIndex = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exposureIndex");
    /**
     * Type: Property <br/>
     * Label: exposureMode  <br/>
     * Comment: tagNumber: 41986
the exposure mode set when the image was shot. In auto-bracketing mode, the camera shoots a series of frames of the same scene at different exposure settings.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exposureMode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exposureMode");
    /**
     * Type: Property <br/>
     * Label: exposureProgram  <br/>
     * Comment: tagNumber: 34850
The class of the program used by the camera to set exposure when the picture is taken.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exposureProgram = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exposureProgram");
    /**
     * Type: Property <br/>
     * Label: exposureTime  <br/>
     * Comment: tagNumber: 33434
Exposure time, given in seconds (sec).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI exposureTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#exposureTime");
    /**
     * Type: Property <br/>
     * Label: fNumber  <br/>
     * Comment: tagNumber: 33437
F number  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#fNumber");
    /**
     * Type: Property <br/>
     * Label: fileSource  <br/>
     * Comment: The image source. If a DSC recorded the image, this tag value of this tag always be set to 3, indicating that the image was recorded on a DSC.
tagNumber: 41728  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fileSource = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#fileSource");
    /**
     * Type: Property <br/>
     * Label: flash  <br/>
     * Comment: tagNumber: 37385
The status of flash when the image was shot.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI flash = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#flash");
    /**
     * Type: Property <br/>
     * Label: flashEnergy  <br/>
     * Comment: tagNumber: 41483
The strobe energy at the time the image is captured, as measured in Beam Candle Power Seconds (BCPS).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI flashEnergy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#flashEnergy");
    /**
     * Type: Property <br/>
     * Label: flashpixVersion  <br/>
     * Comment: tagNumber: 40960
The Flashpix format version supported by a FPXR file. If the FPXR function supports Flashpix format Ver. 1.0, this is indicated similarly to ExifVersion by recording "0100" as 4-byte ASCII.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI flashpixVersion = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#flashpixVersion");
    /**
     * Type: Property <br/>
     * Label: focalLength  <br/>
     * Comment: The actual focal length of the lens, in mm. Conversion is not made to the focal length of a 35 mm film camera.
tagNumber: 37386  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI focalLength = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#focalLength");
    /**
     * Type: Property <br/>
     * Label: focalLengthIn35mmFilm  <br/>
     * Comment: The equivalent focal length assuming a 35mm film camera, in mm. A value of 0 means the focal length is unknown. Note that this tag differs from the FocalLength tag.
tagNumber: 41989  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI focalLengthIn35mmFilm = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#focalLengthIn35mmFilm");
    /**
     * Type: Property <br/>
     * Label: focalPlaneResolutionUnit  <br/>
     * Comment: The unit for measuring FocalPlaneXResolution and FocalPlaneYResolution. This value is the same as the ResolutionUnit.
tagNumber: 41488  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI focalPlaneResolutionUnit = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#focalPlaneResolutionUnit");
    /**
     * Type: Property <br/>
     * Label: focalPlaneXResolution  <br/>
     * Comment: The number of pixels in the image width (X) direction per FocalPlaneResolutionUnit on the camera focal plane.
tagNumber: 41486  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI focalPlaneXResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#focalPlaneXResolution");
    /**
     * Type: Property <br/>
     * Label: focalPlaneYResolution  <br/>
     * Comment: tagNumber: 41487
The number of pixels in the image height (Y) direction per FocalPlaneResolutionUnit on the camera focal plane.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI focalPlaneYResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#focalPlaneYResolution");
    /**
     * Type: Property <br/>
     * Label: gainControl  <br/>
     * Comment: tagNumber: 41991
The degree of overall image gain adjustment.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gainControl = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gainControl");
    /**
     * Type: Property <br/>
     * Label: geo  <br/>
     * Comment: Geometric data such as latitude, longitude and altitude. Usually saved as rational number.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI geo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#geo");
    /**
     * Type: Property <br/>
     * Label: gps  <br/>
     * Comment: The location where the picture has been made. This property aggregates values of two properties from the original EXIF specification: gpsLatitute (tag number 2) and gpsLongitude (tag number 4), and gpsAltitude (tag number 6).  <br/>
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point  <br/>
     */
    public static final URI gps = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gps");
    /**
     * Type: Property <br/>
     * Label: gpsAltitudeRef  <br/>
     * Comment: Indicates the altitude used as the reference altitude. If the reference is sea level and the altitude is above sea level, 0 is given. If the altitude is below sea level, a value of 1 is given and the altitude is indicated as an absolute value in the GPSAltitude tag. The reference unit is meters.
tagNumber: 5  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsAltitudeRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsAltitudeRef");
    /**
     * Type: Property <br/>
     * Label: gpsAreaInformation  <br/>
     * Comment: A character string recording the name of the GPS area. The first byte indicates the character code used, and this is followed by the name of the GPS area.
tagNumber: 28  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsAreaInformation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsAreaInformation");
    /**
     * Type: Property <br/>
     * Label: gpsDOP  <br/>
     * Comment: The GPS DOP (data degree of precision). An HDOP value is written during two-dimensional measurement, and PDOP during three-dimensional measurement.
tagNumber: 11  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDOP = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDOP");
    /**
     * Type: Property <br/>
     * Label: gpsDateStamp  <br/>
     * Comment: tagNumber: 29
date and time information relative to UTC (Coordinated Universal Time). The record format is "YYYY:MM:DD" while converted to W3C-DTF to use in RDF  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI gpsDateStamp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDateStamp");
    /**
     * Type: Property <br/>
     * Label: gpsDest  <br/>
     * Comment: Location of the destination. This property aggregates values of two other properties from the original exif specification. gpsDestLatitude (tag number 20) and gpsDestLongitude (tag number 22)  <br/>
     * Range: http://www.w3.org/2003/01/geo/wgs84_pos#Point  <br/>
     */
    public static final URI gpsDest = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDest");
    /**
     * Type: Property <br/>
     * Label: gpsDestBearing  <br/>
     * Comment: The bearing to the destination point. The range of values is from 0.00 to 359.99.
tagNumber: 24  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestBearing = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestBearing");
    /**
     * Type: Property <br/>
     * Label: gpsDestBearingRef  <br/>
     * Comment: Indicates the reference used for giving the bearing to the destination point. 'T' denotes true direction and 'M' is magnetic direction.
tagNumber: 23  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestBearingRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestBearingRef");
    /**
     * Type: Property <br/>
     * Label: gpsDestDistance  <br/>
     * Comment: The distance to the destination point.
tagNumber: 26  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestDistance = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestDistance");
    /**
     * Type: Property <br/>
     * Label: gpsDestDistanceRef  <br/>
     * Comment: Indicates the unit used to express the distance to the destination point. 'K', 'M' and 'N' represent kilometers, miles and knots.
tagNumber: 25  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestDistanceRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestDistanceRef");
    /**
     * Type: Property <br/>
     * Label: gpsDestLatitudeRef  <br/>
     * Comment: tagNumber: 19
Reference for latitude of destination  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestLatitudeRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestLatitudeRef");
    /**
     * Type: Property <br/>
     * Label: gpsDestLongitudeRef  <br/>
     * Comment: Reference for longitude of destination
tagNumber: 21  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDestLongitudeRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDestLongitudeRef");
    /**
     * Type: Property <br/>
     * Label: gpsDifferential  <br/>
     * Comment: tagNumber: 30
Indicates whether differential correction is applied to the GPS receiver.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsDifferential = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsDifferential");
    /**
     * Type: Property <br/>
     * Label: gpsImgDirection  <br/>
     * Comment: tagNumber: 17
The direction of the image when it was captured. The range of values is from 0.00 to 359.99.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsImgDirection = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsImgDirection");
    /**
     * Type: Property <br/>
     * Label: gpsImgDirectionRef  <br/>
     * Comment: tagNumber: 16
The reference for giving the direction of the image when it is captured. 'T' denotes true direction and 'M' is magnetic direction.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsImgDirectionRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsImgDirectionRef");
    /**
     * Type: Property <br/>
     * Label: gpsInfo  <br/>
     * Comment: An attribute relating to GPS information  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsInfo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsInfo");
    /**
     * Type: Property <br/>
     * Label: gpsInfoIFDPointer  <br/>
     * Comment: A pointer to the GPS IFD, which is a set of tags for recording GPS information.
tagNumber: 34853  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI gpsInfoIFDPointer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsInfoIFDPointer");
    /**
     * Type: Property <br/>
     * Label: gpsLatitudeRef  <br/>
     * Comment: tagNumber: 1
Indicates whether the latitude is north or south latitude. The ASCII value 'N' indicates north latitude, and 'S' is south latitude.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsLatitudeRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsLatitudeRef");
    /**
     * Type: Property <br/>
     * Label: gpsLongitudeRef  <br/>
     * Comment: tagNumber: 3
Indicates whether the longitude is east or west longitude. ASCII 'E' indicates east longitude, and 'W' is west longitude.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsLongitudeRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsLongitudeRef");
    /**
     * Type: Property <br/>
     * Label: gpsMapDatum  <br/>
     * Comment: The geodetic survey data used by the GPS receiver. If the survey data is restricted to Japan, the value of this tag is 'TOKYO' or 'WGS-84'. If a GPS Info tag is recorded, it is strongly recommended that this tag be recorded.
tagNumber: 18  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsMapDatum = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsMapDatum");
    /**
     * Type: Property <br/>
     * Label: gpsMeasureMode  <br/>
     * Comment: The GPS measurement mode. '2' means two-dimensional measurement and '3' means three-dimensional measurement is in progress.
tagNumber: 10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsMeasureMode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsMeasureMode");
    /**
     * Type: Property <br/>
     * Label: gpsProcessingMethod  <br/>
     * Comment: tagNumber: 27
A character string recording the name of the method used for location finding. The first byte indicates the character code used, and this is followed by the name of the method.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsProcessingMethod = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsProcessingMethod");
    /**
     * Type: Property <br/>
     * Label: gpsSatellites  <br/>
     * Comment: tagNumber: 8
The GPS satellites used for measurements. This tag can be used to describe the number of satellites, their ID number, angle of elevation, azimuth, SNR and other information in ASCII notation. The format is not specified. If the GPS receiver is incapable of taking measurements, value of the tag shall be set to NULL.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsSatellites = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsSatellites");
    /**
     * Type: Property <br/>
     * Label: gpsSpeed  <br/>
     * Comment: The speed of GPS receiver movement.
tagNumber: 13  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsSpeed = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsSpeed");
    /**
     * Type: Property <br/>
     * Label: gpsSpeedRef  <br/>
     * Comment: tagNumber: 12
The unit used to express the GPS receiver speed of movement. 'K' 'M' and 'N' represents kilometers per hour, miles per hour, and knots.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsSpeedRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsSpeedRef");
    /**
     * Type: Property <br/>
     * Label: gpsStatus  <br/>
     * Comment: tagNumber: 9
The status of the GPS receiver when the image is recorded. 'A' means measurement is in progress, and 'V' means the measurement is Interoperability.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsStatus = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsStatus");
    /**
     * Type: Property <br/>
     * Label: gpsTimeStamp  <br/>
     * Comment: tagNumber: 7
The time as UTC (Coordinated Universal Time). TimeStamp is expressed as three RATIONAL values giving the hour, minute, and second.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsTimeStamp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsTimeStamp");
    /**
     * Type: Property <br/>
     * Label: gpsTrack  <br/>
     * Comment: The direction of GPS receiver movement. The range of values is from 0.00 to 359.99.
tagNumber: 15  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsTrack = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsTrack");
    /**
     * Type: Property <br/>
     * Label: gpsTrackRef  <br/>
     * Comment: tagNumber: 14
The reference for giving the direction of GPS receiver movement. 'T' denotes true direction and 'M' is magnetic direction.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsTrackRef = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsTrackRef");
    /**
     * Type: Property <br/>
     * Label: gpsVersionID  <br/>
     * Comment: The version of GPSInfoIFD. The version is given as 2.2.0.0. This tag is mandatory when GPSInfo tag is present.
tagNumber: 0  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI gpsVersionID = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#gpsVersionID");
    /**
     * Type: Property <br/>
     * Label: height  <br/>
     * Comment: Height of an object  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI height = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#height");
    /**
     * Type: Property <br/>
     * Label: ifdPointer  <br/>
     * Comment: A tag that refers a child IFD  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI ifdPointer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#ifdPointer");
    /**
     * Type: Property <br/>
     * Label: imageConfig  <br/>
     * Comment: An attribute relating to Image Configuration  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageConfig = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageConfig");
    /**
     * Type: Property <br/>
     * Label: imageDataCharacter  <br/>
     * Comment: An attribute relating to image data characteristics  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageDataCharacter = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageDataCharacter");
    /**
     * Type: Property <br/>
     * Label: imageDataStruct  <br/>
     * Comment: An attribute relating to image data structure  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageDataStruct = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageDataStruct");
    /**
     * Type: Property <br/>
     * Label: imageDescription  <br/>
     * Comment: tagNumber: 270
A character string giving the title of the image. It may be a comment such as "1988 company picnic" or the like. Two-byte character codes cannot be used. When a 2-byte code is necessary, the Exif Private tag UserComment is to be used.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageDescription = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageDescription");
    /**
     * Type: Property <br/>
     * Label: imageLength  <br/>
     * Comment: tagNumber: 257
Image height. The number of rows of image data. In JPEG compressed data a JPEG marker is used.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageLength = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageLength");
    /**
     * Type: Property <br/>
     * Label: imageUniqueID  <br/>
     * Comment: An identifier assigned uniquely to each image. It is recorded as an ASCII string equivalent to hexadecimal notation and 128-bit fixed length.
tagNumber: 42016  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageUniqueID = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageUniqueID");
    /**
     * Type: Property <br/>
     * Label: imageWidth  <br/>
     * Comment: tagNumber: 256
Image width. The number of columns of image data, equal to the number of pixels per row. In JPEG compressed data a JPEG marker is used instead of this tag.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI imageWidth = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#imageWidth");
    /**
     * Type: Property <br/>
     * Label: interopInfo  <br/>
     * Comment: An attribute relating to Interoperability. Tags stored in
Interoperability IFD may be defined dependently to each Interoperability rule.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI interopInfo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#interopInfo");
    /**
     * Type: Property <br/>
     * Label: interoperabilityIFDPointer  <br/>
     * Comment: A pointer to the Interoperability IFD, which is composed of tags storing the information to ensure the Interoperability
tagNumber: 40965  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI interoperabilityIFDPointer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#interoperabilityIFDPointer");
    /**
     * Type: Property <br/>
     * Label: interoperabilityIndex  <br/>
     * Comment: Indicates the identification of the Interoperability rule. 'R98' = conforming to R98 file specification of Recommended Exif Interoperability Rules (ExifR98) or to DCF basic file stipulated by Design Rule for Camera File System. 'THM' = conforming to DCF thumbnail file stipulated by Design rule for Camera File System.
tagNumber: 1  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI interoperabilityIndex = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#interoperabilityIndex");
    /**
     * Type: Property <br/>
     * Label: interoperabilityVersion  <br/>
     * Comment: tagNumber: 2
Interoperability Version  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI interoperabilityVersion = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#interoperabilityVersion");
    /**
     * Type: Property <br/>
     * Label: isoSpeedRatings  <br/>
     * Comment: Indicates the ISO Speed and ISO Latitude of the camera or input device as specified in ISO 12232.
tagNumber: 34855  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI isoSpeedRatings = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#isoSpeedRatings");
    /**
     * Type: Property <br/>
     * Label: jpegInterchangeFormat  <br/>
     * Comment: tagNumber: 513
The offset to the start byte (SOI) of JPEG compressed thumbnail data. This is not used for primary image JPEG data.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI jpegInterchangeFormat = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#jpegInterchangeFormat");
    /**
     * Type: Property <br/>
     * Label: jpegInterchangeFormatLength  <br/>
     * Comment: The number of bytes of JPEG compressed thumbnail data. This is not used for primary image JPEG data.
tagNumber: 514  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI jpegInterchangeFormatLength = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#jpegInterchangeFormatLength");
    /**
     * Type: Property <br/>
     * Label: length  <br/>
     * Comment: Length of an object. Could be a subProperty of other general schema.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI length = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#length");
    /**
     * Type: Property <br/>
     * Label: lightSource  <br/>
     * Comment: tagNumber: 37384
Light source such as Daylight, Tungsten, Flash etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI lightSource = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#lightSource");
    /**
     * Type: Property <br/>
     * Label: make  <br/>
     * Comment: Manufacturer of image input equipment
tagNumber: 271  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI make = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#make");
    /**
     * Type: Property <br/>
     * Label: makerNote  <br/>
     * Comment: Manufacturer notes
tagNumber: 37500  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI makerNote = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#makerNote");
    /**
     * Type: Property <br/>
     * Label: maxApertureValue  <br/>
     * Comment: tagNumber: 37381
The smallest F number of the lens. The unit is the APEX value. Ordinarily it is given in the range of 00.00 to 99.99, but it is not limited to this range.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI maxApertureValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#maxApertureValue");
    /**
     * Type: Property <br/>
     * Label: meter  <br/>
     * Comment: A length with unit of meter  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI meter = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#meter");
    /**
     * Type: Property <br/>
     * Label: meteringMode  <br/>
     * Comment: Metering mode, such as CenterWeightedAverage, Spot, MultiSpot,Pattern, Partial etc.
tagNumber: 37383  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI meteringMode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#meteringMode");
    /**
     * Type: Property <br/>
     * Label: mm  <br/>
     * Comment: A length with unit of mm  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI mm = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#mm");
    /**
     * Type: Property <br/>
     * Label: model  <br/>
     * Comment: tagNumber: 272
Model of image input equipment  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI model = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#model");
    /**
     * Type: Property <br/>
     * Label: oecf  <br/>
     * Comment: tagNumber: 34856
Indicates the Opto-Electric Conversion Function (OECF) specified in ISO 14524. OECF is the relationship between the camera optical input and the image values.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI oecf = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#oecf");
    /**
     * Type: Property <br/>
     * Label: orientation  <br/>
     * Comment: tagNumber: 274
The image orientation viewed in terms of rows and columns.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI orientation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#orientation");
    /**
     * Type: Property <br/>
     * Label: photometricInterpretation  <br/>
     * Comment: Pixel composition. In JPEG compressed data a JPEG marker is used instead of this tag.
tagNumber: 262  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI photometricInterpretation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#photometricInterpretation");
    /**
     * Type: Property <br/>
     * Label: pictTaking  <br/>
     * Comment: An attribute relating to Picture-Taking Conditions  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pictTaking = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pictTaking");
    /**
     * Type: Property <br/>
     * Label: pimBrightness  <br/>
     * Comment: Brightness info for print image matching
tagNumber: 10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimBrightness = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimBrightness");
    /**
     * Type: Property <br/>
     * Label: pimColorBalance  <br/>
     * Comment: tagNumber: 11
ColorBalance info for print image matching  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimColorBalance = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimColorBalance");
    /**
     * Type: Property <br/>
     * Label: pimContrast  <br/>
     * Comment: tagNumber: 9
Contrast info for print image matching  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimContrast = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimContrast");
    /**
     * Type: Property <br/>
     * Label: pimInfo  <br/>
     * Comment: An attribute relating to print image matching  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimInfo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimInfo");
    /**
     * Type: Property <br/>
     * Label: pimSaturation  <br/>
     * Comment: tagNumber: 12
Saturation info for print image matching  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimSaturation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimSaturation");
    /**
     * Type: Property <br/>
     * Label: pimSharpness  <br/>
     * Comment: Sharpness info for print image matching
tagNumber: 13  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pimSharpness = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pimSharpness");
    /**
     * Type: Property <br/>
     * Label: pixelXDimension  <br/>
     * Comment: Information specific to compressed data. When a compressed file is recorded, the valid width of the meaningful image shall be recorded in this tag, whether or not there is padding data or a restart marker. This tag should not exist in an uncompressed file.
tagNumber: 40962  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pixelXDimension = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pixelXDimension");
    /**
     * Type: Property <br/>
     * Label: pixelYDimension  <br/>
     * Comment: Information specific to compressed data. When a compressed file is recorded, the valid height of the meaningful image shall be recorded in this tag, whether or not there is padding data or a restart marker. This tag should not exist in an uncompressed file. Since data padding is unnecessary in the vertical direction, the number of lines recorded in this valid image height tag will in fact be the same as that recorded in the SOF.
tagNumber: 40963  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI pixelYDimension = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#pixelYDimension");
    /**
     * Type: Property <br/>
     * Label: planarConfiguration  <br/>
     * Comment: Indicates whether pixel components are recorded in chunky or planar format. In JPEG compressed files a JPEG marker is used instead of this tag. If this field does not exist, the TIFF default of 1 (chunky) is assumed.
tagNumber: 284  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI planarConfiguration = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#planarConfiguration");
    /**
     * Type: Property <br/>
     * Label: primaryChromaticities  <br/>
     * Comment: The chromaticity of the three primary colors of the image. Normally this tag is not necessary, since color space is specified in the color space information tag (ColorSpace).
tagNumber: 319  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI primaryChromaticities = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#primaryChromaticities");
    /**
     * Type: Property <br/>
     * Label: printImageMatchingIFDPointer  <br/>
     * Comment: tagNumber: 50341
A pointer to the print image matching IFD  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     */
    public static final URI printImageMatchingIFDPointer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#printImageMatchingIFDPointer");
    /**
     * Type: Property <br/>
     * Label: recOffset  <br/>
     * Comment: An attribute relating to recording offset  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI recOffset = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#recOffset");
    /**
     * Type: Property <br/>
     * Label: referenceBlackWhite  <br/>
     * Comment: tagNumber: 532
The reference black point value and reference white point value. The color space is declared in a color space information tag, with the default being the value that gives the optimal image characteristics Interoperability these conditions.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI referenceBlackWhite = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#referenceBlackWhite");
    /**
     * Type: Property <br/>
     * Label: relatedFile  <br/>
     * Comment: Tag Relating to Related File Information  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedFile = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#relatedFile");
    /**
     * Type: Property <br/>
     * Label: relatedImageFileFormat  <br/>
     * Comment: Related image file format
tagNumber: 4096  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedImageFileFormat = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#relatedImageFileFormat");
    /**
     * Type: Property <br/>
     * Label: relatedImageLength  <br/>
     * Comment: Related image length
tagNumber: 4098  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedImageLength = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#relatedImageLength");
    /**
     * Type: Property <br/>
     * Label: relatedImageWidth  <br/>
     * Comment: tagNumber: 4097
Related image width  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedImageWidth = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#relatedImageWidth");
    /**
     * Type: Property <br/>
     * Label: relatedSoundFile  <br/>
     * Comment: Related audio file
tagNumber: 40964  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI relatedSoundFile = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#relatedSoundFile");
    /**
     * Type: Property <br/>
     * Label: resolution  <br/>
     * Comment: a rational number representing a resolution. Could be a subProperty of other general schema.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI resolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#resolution");
    /**
     * Type: Property <br/>
     * Label: resolutionUnit  <br/>
     * Comment: tagNumber: 296
The unit for measuring XResolution and YResolution. The same unit is used for both XResolution and YResolution. If the image resolution in unknown, 2 (inches) is designated.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI resolutionUnit = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#resolutionUnit");
    /**
     * Type: Property <br/>
     * Label: rowsPerStrip  <br/>
     * Comment: tagNumber: 278
The number of rows per strip. This is the number of rows in the image of one strip when an image is divided into strips. With JPEG compressed data this designation is not needed and is omitted.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI rowsPerStrip = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#rowsPerStrip");
    /**
     * Type: Property <br/>
     * Label: samplesPerPixel  <br/>
     * Comment: The number of components per pixel. Since this standard applies to RGB and YCbCr images, the value set for this tag is 3. In JPEG compressed data a JPEG marker is used instead of this tag.
tagNumber: 277  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI samplesPerPixel = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#samplesPerPixel");
    /**
     * Type: Property <br/>
     * Label: saturation  <br/>
     * Comment: The direction of saturation processing applied by the camera when the image was shot.
tagNumber: 41993  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI saturation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#saturation");
    /**
     * Type: Property <br/>
     * Label: sceneCaptureType  <br/>
     * Comment: tagNumber: 41990
The type of scene that was shot. It can also be used to record the mode in which the image was shot, such as Landscape, Portrait etc. Note that this differs from the scene type (SceneType) tag.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sceneCaptureType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#sceneCaptureType");
    /**
     * Type: Property <br/>
     * Label: sceneType  <br/>
     * Comment: tagNumber: 41729
The type of scene. If a DSC recorded the image, this tag value shall always be set to 1, indicating that the image was directly photographed.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sceneType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#sceneType");
    /**
     * Type: Property <br/>
     * Label: seconds  <br/>
     * Comment: a mesurement of time length with unit of second  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI seconds = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#seconds");
    /**
     * Type: Property <br/>
     * Label: sensingMethod  <br/>
     * Comment: tagNumber: 41495
The image sensor type on the camera or input device, such as One-chip color area sensor etc.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sensingMethod = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#sensingMethod");
    /**
     * Type: Property <br/>
     * Label: sharpness  <br/>
     * Comment: tagNumber: 41994
The direction of sharpness processing applied by the camera when the image was shot.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI sharpness = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#sharpness");
    /**
     * Type: Property <br/>
     * Label: shutterSpeedValue  <br/>
     * Comment: tagNumber: 37377
Shutter speed. The unit is the APEX (Additive System of Photographic Exposure) setting  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI shutterSpeedValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#shutterSpeedValue");
    /**
     * Type: Property <br/>
     * Label: software  <br/>
     * Comment: tagNumber: 305
The name and version of the software or firmware of the camera or image input device used to generate the image.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI software = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#software");
    /**
     * Type: Property <br/>
     * Label: spatialFrequencyResponse  <br/>
     * Comment: This tag records the camera or input device spatial frequency table and SFR values in the direction of image width, image height, and diagonal direction, as specified in ISO 12233.
tagNumber: 41484  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI spatialFrequencyResponse = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#spatialFrequencyResponse");
    /**
     * Type: Property <br/>
     * Label: spectralSensitivity  <br/>
     * Comment: Indicates the spectral sensitivity of each channel of the camera used. The tag value is an ASCII string compatible with the standard developed by the ASTM Technical committee.
tagNumber: 34852  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI spectralSensitivity = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#spectralSensitivity");
    /**
     * Type: Property <br/>
     * Label: stripByteCounts  <br/>
     * Comment: tagNumber: 279
The total number of bytes in each strip. With JPEG compressed data this designation is not needed and is omitted.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI stripByteCounts = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#stripByteCounts");
    /**
     * Type: Property <br/>
     * Label: stripOffsets  <br/>
     * Comment: For each strip, the byte offset of that strip. With JPEG compressed data this designation is not needed and is omitted.
tagNumber: 273  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI stripOffsets = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#stripOffsets");
    /**
     * Type: Property <br/>
     * Label: subSecTime  <br/>
     * Comment: tagNumber: 37520
DateTime subseconds  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subSecTime = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subSecTime");
    /**
     * Type: Property <br/>
     * Label: subSecTimeDigitized  <br/>
     * Comment: tagNumber: 37522
DateTimeDigitized subseconds  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subSecTimeDigitized = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subSecTimeDigitized");
    /**
     * Type: Property <br/>
     * Label: subSecTimeOriginal  <br/>
     * Comment: tagNumber: 37521
DateTimeOriginal subseconds  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subSecTimeOriginal = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subSecTimeOriginal");
    /**
     * Type: Property <br/>
     * Label: subjectArea  <br/>
     * Comment: tagNumber: 37396
The location and area of the main subject in the overall scene.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subjectArea = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subjectArea");
    /**
     * Type: Property <br/>
     * Label: subjectDistance  <br/>
     * Comment: tagNumber: 37382
The distance to the subject, given in meters. Note that if the numerator of the recorded value is FFFFFFFF.H, Infinity shall be indicated; and if the numerator is 0, Distance unknown shall be indicated.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subjectDistance = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subjectDistance");
    /**
     * Type: Property <br/>
     * Label: subjectDistanceRange  <br/>
     * Comment: The distance to the subject, such as Macro, Close View or Distant View.
tagNumber: 41996  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subjectDistanceRange = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subjectDistanceRange");
    /**
     * Type: Property <br/>
     * Label: subjectLocation  <br/>
     * Comment: The location of the main subject in the scene. The value of this tag represents the pixel at the center of the main subject relative to the left edge, prior to rotation processing as per the Rotation tag. The first value indicates the X column number and second indicates the Y row number.
tagNumber: 41492  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subjectLocation = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subjectLocation");
    /**
     * Type: Property <br/>
     * Label: subsecond  <br/>
     */
    public static final URI subsecond = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subsecond");
    /**
     * Type: Property <br/>
     * Label: subseconds  <br/>
     * Comment: A tag used to record fractions of seconds for a date property  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subseconds = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#subseconds");
    /**
     * Type: Property <br/>
     * Label: tagNumber  <br/>
     * Comment: The Exif tag number (for this schema definition)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tagNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#tagNumber");
    /**
     * Type: Property <br/>
     * Label: tagid  <br/>
     * Comment: The Exif tag number with context prefix, such as IFD type or maker name (for this schema definition)  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI tagid = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#tagid");
    /**
     * Type: Property <br/>
     * Label: transferFunction  <br/>
     * Comment: tagNumber: 301
A transfer function for the image, described in tabular style. Normally this tag is not necessary, since color space is specified in the color space information tag (ColorSpace).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI transferFunction = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#transferFunction");
    /**
     * Type: Property <br/>
     * Label: unknown  <br/>
     * Comment: An Exif tag whose meaning is not known  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI unknown = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#unknown");
    /**
     * Type: Property <br/>
     * Label: userComment  <br/>
     * Comment: tagNumber: 37510
A tag for Exif users to write keywords or comments on the image besides those in ImageDescription, and without the character code limitations of the ImageDescription tag. The character code used in the UserComment tag is identified based on an ID code in a fixed 8-byte area at the start of the tag data area.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI userComment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#userComment");
    /**
     * Type: Property <br/>
     * Label: userInfo  <br/>
     * Comment: An attribute relating to User Information  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI userInfo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#userInfo");
    /**
     * Type: Property <br/>
     * Label: versionInfo  <br/>
     * Comment: An attribute relating to Version  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI versionInfo = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#versionInfo");
    /**
     * Type: Property <br/>
     * Label: whiteBalance  <br/>
     * Comment: tagNumber: 41987
The white balance mode set when the image was shot.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI whiteBalance = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#whiteBalance");
    /**
     * Type: Property <br/>
     * Label: whitePoint  <br/>
     * Comment: The chromaticity of the white point of the image. Normally this tag is not necessary, since color space is specified in the color space information tag (ColorSpace).
tagNumber: 318  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI whitePoint = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#whitePoint");
    /**
     * Type: Property <br/>
     * Label: width  <br/>
     * Comment: Width of an object  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI width = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#width");
    /**
     * Type: Property <br/>
     * Label: xResolution  <br/>
     * Comment: The number of pixels per ResolutionUnit in the ImageWidth direction. When the image resolution is unknown, 72 [dpi] is designated.
tagNumber: 282  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI xResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#xResolution");
    /**
     * Type: Property <br/>
     * Label: yCbCrCoefficients  <br/>
     * Comment: tagNumber: 529
The matrix coefficients for transformation from RGB to YCbCr image data.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI yCbCrCoefficients = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#yCbCrCoefficients");
    /**
     * Type: Property <br/>
     * Label: yCbCrPositioning  <br/>
     * Comment: The position of chrominance components in relation to the luminance component. This field is designated only for JPEG compressed data or uncompressed YCbCr data.
tagNumber: 531  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI yCbCrPositioning = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#yCbCrPositioning");
    /**
     * Type: Property <br/>
     * Label: yCbCrSubSampling  <br/>
     * Comment: The sampling ratio of chrominance components in relation to the luminance component. In JPEG compressed data a JPEG marker is used instead of this tag.
tagNumber: 530  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI yCbCrSubSampling = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#yCbCrSubSampling");
    /**
     * Type: Property <br/>
     * Label: yResolution  <br/>
     * Comment: tagNumber: 283
The number of pixels per ResolutionUnit in the ImageLength direction. The same value as XResolution is designated.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#Photo  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI yResolution = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#yResolution");
}
