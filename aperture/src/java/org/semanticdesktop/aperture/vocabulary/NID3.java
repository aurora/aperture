package org.semanticdesktop.aperture.vocabulary;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.util.ResourceUtil;
/**
 * Vocabulary File. Created by org.semanticdesktop.aperture.util.VocabularyWriter on Thu Aug 09 17:41:37 CEST 2007
 * input file: D:\workspace\aperture-nie/doc/ontology/nid3.rdfs
 * namespace: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#
 */
public class NID3 {

    public static final String NID3_RESOURCE_PATH = 
      NID3.class.getPackage().getName().replace('.', '/') + "/nid3.rdfs";

    /**
     * Puts the NID3 ontology into the given model.
     * @param model The model for the source ontology to be put into.
     * @throws Exception if something goes wrong.
     */
    public static void getNID3Ontology(Model model) {
        try {
            InputStream stream = ResourceUtil.getInputStream(NID3_RESOURCE_PATH, NID3.class);
            if (stream == null) {
                throw new FileNotFoundException("couldn't find resource " + NID3_RESOURCE_PATH);
             }
            model.readFrom(stream, Syntax.RdfXml);
        } catch(Exception e) {
             throw new RuntimeException(e);
        }
    }

    /** The namespace for NID3 */
    public static final URI NS_NID3 = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#");
    /**
     * Type: Class <br/>
     * Label: ID3Audio  <br/>
     * Comment: A File annotated with ID3 tags  <br/>
     */
    public static final URI ID3Audio = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio");
    /**
     * Type: Class <br/>
     * Label: InvolvedPerson  <br/>
     * Comment: Since there might be a lot of people contributing to an audio file in various ways, such as musicians and technicians, the 'Text information frames' are often insufficient to list everyone involved in a project. The 'Involved people list' is a frame containing the names of those involved, and how they were involved. The body simply contains a terminated string with the involvement directly followed by a terminated string with the involvee followed by a new involvement and so on. There may only be one "IPLS" frame in each tag.
Note that in this RDF representation each InvolvedPerson is represented with a separate instance of the InvolvedPerson class and with a separate involvedPerson triple.  <br/>
     */
    public static final URI InvolvedPerson = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson");
    /**
     * Type: Class <br/>
     * Label: SynchronizedText  <br/>
     * Comment: SYLT This is another way of incorporating the words, said or sung lyrics, in the audio file as text, this time, however, in sync with the audio. It might also be used to describing events e.g. occurring on a stage or on the screen in sync with the audio. The header includes a content descriptor, represented with as terminated textstring. If no descriptor is entered, 'Content descriptor' is $00 (00) only.  <br/>
     */
    public static final URI SynchronizedText = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText");
    /**
     * Type: Class <br/>
     * Label: SynchronizedTextElement  <br/>
     * Comment: An element of the synchronized text. It aggregates the actual text content, with the timestamp.  <br/>
     */
    public static final URI SynchronizedTextElement = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedTextElement");
    /**
     * Type: Class <br/>
     * Label: UserDefinedFrame  <br/>
     * Comment: This frame is intended for one-string text information concerning the audiofile in a similar way to the other "T"-frames. The frame body consists of a description of the string, represented as a terminated string, followed by the actual string. There may be more than one "TXXX" frame in each tag, but only one with the same description.  <br/>
     */
    public static final URI UserDefinedFrame = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#UserDefinedFrame");
    /**
     * Type: Class <br/>
     * Label: UserDefinedURLFrame  <br/>
     * Comment: This frame is intended for URL links concerning the audiofile in a similar way to the other "W"-frames. The frame body consists of a description of the string, represented as a terminated string, followed by the actual URL. The URL is always encoded with ISO-8859-1. There may be more than one "WXXX" frame in each tag, but only one with the same description.  <br/>
     */
    public static final URI UserDefinedURLFrame = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#UserDefinedURLFrame");
    /**
     * Type: Property <br/>
     * Label: albumTitle  <br/>
     * Comment: TALB
The 'Album/Movie/Show title' frame is intended for the title of the recording(/source of sound) which the audio in the file is taken from.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI albumTitle = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#albumTitle");
    /**
     * Type: Property <br/>
     * Label: attachedPicture  <br/>
     * Comment: A picture attached to an audio file. Inspired by the attached picture tag defined in http://www.id3.org/id3v2.3.0 sec. 4.15)  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI attachedPicture = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#attachedPicture");
    /**
     * Type: Property <br/>
     * Label: audiofileSize  <br/>
     * Comment: TSIZ
The 'Size' frame contains the size of the audiofile in bytes, excluding the ID3v2 tag, represented as a numeric string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI audiofileSize = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#audiofileSize");
    /**
     * Type: Property <br/>
     * Label: backgroundArtist  <br/>
     * Comment: TPE2
The 'Band/Orchestra/Accompaniment' frame is used for additional information about the performers in the recording.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI backgroundArtist = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#backgroundArtist");
    /**
     * Type: Property <br/>
     * Label: beatsPerMinute  <br/>
     * Comment: TBPM
The 'BPM' frame contains the number of beats per minute in the mainpart of the audio. The BPM is an integer and represented as a numerical string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI beatsPerMinute = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#beatsPerMinute");
    /**
     * Type: Property <br/>
     * Label: comments  <br/>
     * Comment: COMM - This frame is indended for any kind of full text information that does not fit in any other frame. It consists of a frame header followed by encoding, language and content descriptors and is ended with the actual comment as a text string. Newline characters are allowed in the comment text string. There may be more than one comment frame in each tag, but only one with the same language and content descriptor.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI comments = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#comments");
    /**
     * Type: Property <br/>
     * Label: commercialInformationURL  <br/>
     * Comment: WCOM
The 'Commercial information' frame is a URL pointing at a webpage with information such as where the album can be bought. There may be more than one "WCOM" frame in a tag, but not with the same content.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI commercialInformationURL = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#commercialInformationURL");
    /**
     * Type: Property <br/>
     * Label: composer  <br/>
     * Comment: TCOM
The 'Composer(s)' frame is intended for the name of the composer(s). They are seperated with the "/" character.
Note that in the RDF representation each composer is represented with a separate triple.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI composer = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#composer");
    /**
     * Type: Property <br/>
     * Label: conductor  <br/>
     * Comment: TPE3
The 'Conductor' frame is used for the name of the conductor.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI conductor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#conductor");
    /**
     * Type: Property <br/>
     * Label: contentGroupDescription  <br/>
     * Comment: TIT1
The 'Content group description' frame is used if the sound belongs to a larger category of sounds/music. For example, classical music is often sorted in different musical sections (e.g. "Piano Concerto", "Weather - Hurricane").  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentGroupDescription = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#contentGroupDescription");
    /**
     * Type: Property <br/>
     * Label: contentType  <br/>
     * Comment: TCON

The 'Content type', which previously was stored as a one byte numeric value only, is now a numeric string. You may use one or several of the types as ID3v1.1 did or, since the category list would be impossible to maintain with accurate and up to date categories, define your own. 

References to the ID3v1 genres can be made by, as first byte, enter "(" followed by a number from the genres list (appendix A) and ended with a ")" character. This is optionally followed by a refinement, e.g. "(21)" or "(4)Eurodisco". Several references can be made in the same frame, e.g. "(51)(39)". If the refinement should begin with a "(" character it should be replaced with "((", e.g. "((I can figure out any genre)" or "(55)((I think...)". The following new content types is defined in ID3v2 and is implemented in the same way as the numerig content types, e.g. "(RX)". 
RX    Remix 
CR    Cover  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI contentType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#contentType");
    /**
     * Type: Property <br/>
     * Label: copyrightInformationURL  <br/>
     * Comment: WCOP
The 'Copyright/Legal information' frame is a URL pointing at a webpage where the terms of use and ownership of the file is described.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI copyrightInformationURL = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#copyrightInformationURL");
    /**
     * Type: Property <br/>
     * Label: copyrightMessage  <br/>
     * Comment: TCOP
The 'Copyright message' frame, which must begin with a year and a space character (making five characters), is intended for the copyright holder of the original sound, not the audio file itself. The absence of this frame means only that the copyright information is unavailable or has been removed, and must not be interpreted to mean that the sound is public domain. Every time this field is displayed the field must be preceded with "Copyright".  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI copyrightMessage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#copyrightMessage");
    /**
     * Type: Property <br/>
     * Label: date  <br/>
     * Comment: TDAT
The 'Date' frame is a numeric string in the DDMM format containing the date for the recording. This field is always four characters long.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#dateTime  <br/>
     */
    public static final URI date = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#date");
    /**
     * Type: Property <br/>
     * Label: encodedBy  <br/>
     * Comment: TENC
The 'Encoded by' frame contains the name of the person or organisation that encoded the audio file. This field may contain a copyright message, if the audio file also is copyrighted by the encoder.
Note that the RDF representation doesn't allow the copyright message in this field. Please move it to the copyrightMessage field.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI encodedBy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#encodedBy");
    /**
     * Type: Property <br/>
     * Label: encodingSettings  <br/>
     * Comment: TSSE
The 'Software/Hardware and settings used for encoding' frame includes the used audio encoder and its settings when the file was encoded. Hardware refers to hardware encoders, not the computer on which a program was run.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI encodingSettings = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#encodingSettings");
    /**
     * Type: Property <br/>
     * Label: fileOwner  <br/>
     * Comment: TOWN
The 'File owner/licensee' frame contains the name of the owner or licensee of the file and it's contents.  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI fileOwner = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#fileOwner");
    /**
     * Type: Property <br/>
     * Label: fileType  <br/>
     * Comment: TFLT
The 'File type' frame indicates which type of audio this tag defines. The following type and refinements are defined: 
MPG MPEG Audio; 
/1 MPEG 1/2 layer I;
/2 MPEG 1/2 layer II;
/3 MPEG 1/2 layer III;
/2.5 MPEG 2.5;
/AAC Advanced audio compression;
VQF Transform-domain Weighted Interleave Vector Quantization;
PCM Pulse Code Modulated audio;  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI fileType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#fileType");
    /**
     * Type: Property <br/>
     * Label: generalEncapsulatedObject  <br/>
     * Comment: An arbitrary file embedded in an audio file. Inspired by http://www.id3.org/id3v2.3.0 sec. 
4.16)  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/01/19/nie#DataObject  <br/>
     */
    public static final URI generalEncapsulatedObject = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#generalEncapsulatedObject");
    /**
     * Type: Property <br/>
     * Label: hasSynchronizedText  <br/>
     * Comment: Links the ID3Audio with an instance of SynchronizedText  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText  <br/>
     */
    public static final URI hasSynchronizedText = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#hasSynchronizedText");
    /**
     * Type: Property <br/>
     * Label: hasSynchronizedTextElement  <br/>
     * Comment: Links the synchronized text object with the text elements.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedTextElement  <br/>
     */
    public static final URI hasSynchronizedTextElement = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#hasSynchronizedTextElement");
    /**
     * Type: Property <br/>
     * Label: initialKey  <br/>
     * Comment: TKEY
The 'Initial key' frame contains the musical key in which the sound starts. It is represented as a string with a maximum length of three characters. The ground keys are represented with "A","B","C","D","E", "F" and "G" and halfkeys represented with "b" and "#". Minor is represented as "m". Example "Cbm". Off key is represented with an "o" only.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI initialKey = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#initialKey");
    /**
     * Type: Property <br/>
     * Label: internationalStandardRecordingCode  <br/>
     * Comment: TSRC
The 'ISRC' frame should contain the International Standard Recording Code (ISRC) (12 characters).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI internationalStandardRecordingCode = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#internationalStandardRecordingCode");
    /**
     * Type: Property <br/>
     * Label: internetRadioStationName  <br/>
     * Comment: TRSN
The 'Internet radio station name' frame contains the name of the internet radio station from which the audio is streamed.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI internetRadioStationName = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#internetRadioStationName");
    /**
     * Type: Property <br/>
     * Label: internetRadioStationOwner  <br/>
     * Comment: TRSO
The 'Internet radio station owner' frame contains the name of the owner of the internet radio station from which the audio is streamed.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI internetRadioStationOwner = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#internetRadioStationOwner");
    /**
     * Type: Property <br/>
     * Label: interpretedBy  <br/>
     * Comment: TPE4
The 'Interpreted, remixed, or otherwise modified by' frame contains more information about the people behind a remix and similar interpretations of another existing piece.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI interpretedBy = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#interpretedBy");
    /**
     * Type: Property <br/>
     * Label: involvedPerson  <br/>
     * Comment: Links an ID3 file to an InvolvedPerson, an equivalent of the involvedPeopleList tag. Since there might be a lot of people contributing to an audio file in various ways, such as musicians and technicians, the 'Text information frames' are often insufficient to list everyone involved in a project. The 'Involved people list' is a frame containing the names of those involved, and how they were involved. The body simply contains a terminated string with the involvement directly followed by a terminated string with the involvee followed by a new involvement and so on. There may only be one "IPLS" frame in each tag.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson  <br/>
     */
    public static final URI involvedPerson = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvedPerson");
    /**
     * Type: Property <br/>
     * Label: involvedPersonContact  <br/>
     * Comment: An actual contact to the involved person.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI involvedPersonContact = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvedPersonContact");
    /**
     * Type: Property <br/>
     * Label: involvment  <br/>
     * Comment: How was this particular person involved in this particular track.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#InvolvedPerson  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI involvment = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#involvment");
    /**
     * Type: Property <br/>
     * Label: language  <br/>
     * Comment: TLAN
The 'Language(s)' frame should contain the languages of the text or lyrics spoken or sung in the audio. The language is represented with three characters according to ISO-639-2. If more than one language is used in the text their language codes should follow according to their usage.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI language = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#language");
    /**
     * Type: Property <br/>
     * Label: leadArtist  <br/>
     * Comment: TPE1
The 'Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group' is used for the main artist(s). They are seperated with the "/" character.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI leadArtist = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#leadArtist");
    /**
     * Type: Property <br/>
     * Label: length  <br/>
     * Comment: TLEN
The 'Length' frame contains the length of the audiofile in milliseconds, represented as a numeric string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Literal  <br/>
     */
    public static final URI length = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#length");
    /**
     * Type: Property <br/>
     * Label: licensee  <br/>
     * Comment: TOWN
The 'File owner/licensee' frame contains the name of the owner or licensee of the file and it's contents.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI licensee = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#licensee");
    /**
     * Type: Property <br/>
     * Label: mediaType  <br/>
     * Comment: TMED
The 'Media type' frame describes from which media the sound originated. This may be a text string or a reference to the predefined media types found in the list below. References are made within "(" and ")" and are optionally followed by a text refinement, e.g. "(MC) with four channels". If a text refinement should begin with a "(" character it should be replaced with "((" in the same way as in the "TCO" frame. Predefined refinements is appended after the media type, e.g. "(CD/A)" or "(VID/PAL/VHS)".
See http://www.id3.org/id3v2.3.0 for details.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI mediaType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#mediaType");
    /**
     * Type: Property <br/>
     * Label: musicCDIdentifier  <br/>
     * Comment: This frame is intended for music that comes from a CD, so that the CD can be identified in databases such as the CDDB. The frame consists of a binary dump of the Table Of Contents, TOC, from the CD, which is a header of 4 bytes and then 8 bytes/track on the CD plus 8 bytes for the 'lead out' making a maximum of 804 bytes. The offset to the beginning of every track on the CD should be described with a four bytes absolute CD-frame address per track, and not with absolute time. This frame requires a present and valid "TRCK" frame, even if the CD's only got one track. There may only be one "MCDI" frame in each tag.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI musicCDIdentifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#musicCDIdentifier");
    /**
     * Type: Property <br/>
     * Label: officialArtistWebpage  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI officialArtistWebpage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#officialArtistWebpage");
    /**
     * Type: Property <br/>
     * Label: officialAudioSourceWebpage  <br/>
     * Comment: WOAS
The 'Official audio source webpage' frame is a URL pointing at the official webpage for the source of the audio file, e.g. a movie.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI officialAudioSourceWebpage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#officialAudioSourceWebpage");
    /**
     * Type: Property <br/>
     * Label: officialFileWebpage  <br/>
     * Comment: WOAF
The 'Official audio file webpage' frame is a URL pointing at a file specific webpage.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI officialFileWebpage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#officialFileWebpage");
    /**
     * Type: Property <br/>
     * Label: officialInternetRadioStationHomepage  <br/>
     * Comment: WORS
The 'Official internet radio station homepage' contains a URL pointing at the homepage of the internet radio station.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI officialInternetRadioStationHomepage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#officialInternetRadioStationHomepage");
    /**
     * Type: Property <br/>
     * Label: originalAlbumTitle  <br/>
     * Comment: TOAL
The 'Original album/movie/show title' frame is intended for the title of the original recording (or source of sound), if for example the music in the file should be a cover of a previously released song.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI originalAlbumTitle = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#originalAlbumTitle");
    /**
     * Type: Property <br/>
     * Label: originalArtist  <br/>
     * Comment: TOPE
The 'Original artist(s)/performer(s)' frame is intended for the performer(s) of the original recording, if for example the music in the file should be a cover of a previously released song. The performers are seperated with the "/" character.
Note that in the RDF repressentation each orignal artist is represented with a separate triple.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI originalArtist = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#originalArtist");
    /**
     * Type: Property <br/>
     * Label: originalFilename  <br/>
     * Comment: TOFN
The 'Original filename' frame contains the preferred filename for the file, since some media doesn't allow the desired length of the filename. The filename is case sensitive and includes its suffix.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI originalFilename = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#originalFilename");
    /**
     * Type: Property <br/>
     * Label: originalReleaseYear  <br/>
     * Comment: TORY
The 'Original release year' frame is intended for the year when the original recording, if for example the music in the file should be a cover of a previously released song, was released. The field is formatted as in the "TYER" frame.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI originalReleaseYear = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#originalReleaseYear");
    /**
     * Type: Property <br/>
     * Label: originalTextWriter  <br/>
     * Comment: TOLY
The 'Original lyricist(s)/text writer(s)' frame is intended for the text writer(s) of the original recording, if for example the music in the file should be a cover of a previously released song. The text writers are seperated with the "/" character.
Note that in the RDF representation each original lyricist is represented with a separate triple.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI originalTextWriter = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#originalTextWriter");
    /**
     * Type: Property <br/>
     * Label: partOfSet  <br/>
     * Comment: TPOS
The 'Part of a set' frame is a numeric string that describes which part of a set the audio came from. This frame is used if the source described in the "TALB" frame is divided into several mediums, e.g. a double CD. The value may be extended with a "/" character and a numeric string containing the total number of parts in the set. E.g. "1/2".  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI partOfSet = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#partOfSet");
    /**
     * Type: Property <br/>
     * Label: paymentURL  <br/>
     * Comment: WPAY
The 'Payment' frame is a URL pointing at a webpage that will handle the process of paying for this file.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI paymentURL = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#paymentURL");
    /**
     * Type: Property <br/>
     * Label: playlistDelay  <br/>
     * Comment: TDLY
The 'Playlist delay' defines the numbers of milliseconds of silence between every song in a playlist. The player should use the "ETC" frame, if present, to skip initial silence and silence at the end of the audio to match the 'Playlist delay' time. The time is represented as a numeric string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI playlistDelay = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#playlistDelay");
    /**
     * Type: Property <br/>
     * Label: publisher  <br/>
     * Comment: TPUB
The 'Publisher' frame simply contains the name of the label or publisher.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI publisher = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#publisher");
    /**
     * Type: Property <br/>
     * Label: publishersWebpage  <br/>
     * Comment: WPUB
The 'Publishers official webpage' frame is a URL pointing at the official wepage for the publisher.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2000/01/rdf-schema#Resource  <br/>
     */
    public static final URI publishersWebpage = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#publishersWebpage");
    /**
     * Type: Property <br/>
     * Label: recordingDate  <br/>
     * Comment: TRDA
The 'Recording dates' frame is a intended to be used as complement to the "TYER", "TDAT" and "TIME" frames. E.g. "4th-7th June, 12th June" in combination with the "TYER" frame.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI recordingDate = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#recordingDate");
    /**
     * Type: Property <br/>
     * Label: recordingYear  <br/>
     * Comment: TYER
The 'Year' frame is a numeric string with a year of the recording. This frames is always four characters long (until the year 10000).  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI recordingYear = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#recordingYear");
    /**
     * Type: Property <br/>
     * Label: subtitle  <br/>
     * Comment: TIT3
The 'Subtitle/Description refinement' frame is used for information directly related to the contents title (e.g. "Op. 16" or "Performed live at Wembley").  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI subtitle = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#subtitle");
    /**
     * Type: Property <br/>
     * Label: synchronizedTextContentDescriptor  <br/>
     * Comment: Synchronized text content descriptor. Inspired by the content descriptor part of the SYLT frame defined in ID3 2.3.0 spec sec. 4.10  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI synchronizedTextContentDescriptor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#synchronizedTextContentDescriptor");
    /**
     * Type: Property <br/>
     * Label: textContentType  <br/>
     * Comment: Content type: 
$00     is other 
$01     is lyrics
$02     is text transcription
$03     is movement/part name (e.g. "Adagio")
$04     is events (e.g. "Don Quijote enters the stage")
$05     is chord (e.g. "Bb F Fsus")
$06     is trivia/'pop up' information  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI textContentType = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#textContentType");
    /**
     * Type: Property <br/>
     * Label: textElementContent  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedTextElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI textElementContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#textElementContent");
    /**
     * Type: Property <br/>
     * Label: textElementTimestamp  <br/>
     * Comment: The 'time stamp' is set to zero or the whole sync is omitted if located directly at the beginning of the sound. All time stamps should be sorted in chronological order. The sync can be considered as a validator of the subsequent string.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedTextElement  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#integer  <br/>
     */
    public static final URI textElementTimestamp = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#textElementTimestamp");
    /**
     * Type: Property <br/>
     * Label: textWriter  <br/>
     * Comment: TEXT
The 'Lyricist(s)/Text writer(s)' frame is intended for the writer(s) of the text or lyrics in the recording. They are seperated with the "/" character.
Note that in the RDF representation each text writer is represented with a separate triple.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/03/22/nco#Contact  <br/>
     */
    public static final URI textWriter = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#textWriter");
    /**
     * Type: Property <br/>
     * Label: time  <br/>
     * Comment: TIME
The 'Time' frame is a numeric string in the HHMM format containing the time for the recording. This field is always four characters long.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI time = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#time");
    /**
     * Type: Property <br/>
     * Label: timestampFormat  <br/>
     * Comment: Time stamp format is: 
$01 Absolute time, 32 bit sized, using MPEG frames as unit
$02 Absolute time, 32 bit sized, using milliseconds as unit  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#SynchronizedText  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI timestampFormat = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#timestampFormat");
    /**
     * Type: Property <br/>
     * Label: title  <br/>
     * Comment: TIT2
The 'Title/Songname/Content description' frame is the actual name of the piece (e.g. "Adagio", "Hurricane Donna").  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI title = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#title");
    /**
     * Type: Property <br/>
     * Label: trackNumber  <br/>
     * Comment: TRCK
The 'Track number/Position in set' frame is a numeric string containing the order number of the audio-file on its original recording. This may be extended with a "/" character and a numeric string containing the total numer of tracks/elements on the original recording. E.g. "4/9".  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI trackNumber = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#trackNumber");
    /**
     * Type: Property <br/>
     * Label: uniqueFileIdentifier  <br/>
     * Comment: This frame's purpose is to be able to identify the audio file in a database that may contain more information relevant to the content. Since standardisation of such a database is beyond this document, all frames begin with a null-terminated string with a URL containing an email address, or a link to a location where an email address can be found, that belongs to the organisation responsible for this specific database implementation. Questions regarding the database should be sent to the indicated email address. The URL should not be used for the actual database queries. The string "http://www.id3.org/dummy/ufid.html" should be used for tests. Software that isn't told otherwise may safely remove such frames. The 'Owner identifier' must be non-empty (more than just a termination). The 'Owner identifier' is then followed by the actual identifier, which may be up to 64 bytes. There may be more than one "UFID" frame in a tag, but only one with the same 'Owner identifier'.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI uniqueFileIdentifier = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#uniqueFileIdentifier");
    /**
     * Type: Property <br/>
     * Label: unsynchronizedTextContent  <br/>
     * Comment: Unsynchronized text content. Inspired by the content part of the USLT frame defined in the ID3 2.3.0 Spec sec. 4.9  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI unsynchronizedTextContent = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#unsynchronizedTextContent");
    /**
     * Type: Property <br/>
     * Label: unsynchronizedTextContentDescriptor  <br/>
     * Comment: The content descriptor of the unsynchronized text. Inspired by the Content Descriptor field of the USLT frame, defined in ID3 2.3.0 Spec sec. 4.9  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI unsynchronizedTextContentDescriptor = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#unsynchronizedTextContentDescriptor");
    /**
     * Type: Property <br/>
     * Label: userDefinedFrame  <br/>
     * Comment: Links the ID3 file to a user-defined frame.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#ID3Audio  <br/>
     * Range: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#UserDefinedFrame  <br/>
     */
    public static final URI userDefinedFrame = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#userDefinedFrame");
    /**
     * Type: Property <br/>
     * Label: userDefinedFrameDescription  <br/>
     * Comment: Description of a user-defined frame.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#UserDefinedFrame  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI userDefinedFrameDescription = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#userDefinedFrameDescription");
    /**
     * Type: Property <br/>
     * Label: userDefinedFrameValue  <br/>
     * Comment: Value of a user-defined frame.  <br/>
     * Domain: http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#UserDefinedFrame  <br/>
     * Range: http://www.w3.org/2001/XMLSchema#string  <br/>
     */
    public static final URI userDefinedFrameValue = new URIImpl("http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#userDefinedFrameValue");
}
