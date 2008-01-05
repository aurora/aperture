/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mp3;

import java.util.HashMap;

import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyCOMM;
import org.jaudiotagger.tag.id3.framebody.FrameBodyGEOB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTALB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTBPM;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTCOM;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTCON;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTCOP;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTENC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTEXT;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTFLT;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT1;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT3;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTKEY;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTLAN;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTLEN;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTMED;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOAL;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOFN;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOLY;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOPE;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTORY;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOWN;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE1;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE3;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE4;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPOS;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPUB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTRCK;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTRSN;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTRSO;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTSRC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTSSE;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTYER;
import org.jaudiotagger.tag.id3.framebody.FrameBodyUFID;
import org.jaudiotagger.tag.id3.framebody.FrameBodyUSLT;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWCOM;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWCOP;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWOAF;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWOAR;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWOAS;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWORS;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWPAY;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWPUB;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NID3;
import org.semanticdesktop.aperture.vocabulary.NIE;



/**
 * An enumeration of ID3v2 frames defined in the standards.
 */
public enum FrameIdentifier {
    
    // the frames below have come from 2.3.0 and were kept in 2.4.0 
    // they are common to both versions of the standard
    // http://www.id3.org/id3v2.4.0-frames
    
    AENC("Audio encryption",false), // not supported by NID3
    APIC("Attached picture",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            FrameBodyAPIC apic = (FrameBodyAPIC)body;
            Model model = result.getModel();
            Resource resource = UriUtil.generateRandomResource(model);
            model.addStatement(resource, RDF.type, NFO.Attachment);
            model.addStatement(resource, RDF.type, NFO.Image);
            if (apic.getMimeType() != null && apic.getMimeType().length() > 0) {
                model.addStatement(resource,NIE.mimeType,apic.getMimeType());
            }
            model.addStatement(result.getDescribedUri(), NID3.attachedPicture, resource);            
        }},
    COMM("Comments",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            String description = ((FrameBodyCOMM)body).getDescription();
            boolean addDescription = (description != null && description.length() > 0); 
            String text = ((FrameBodyCOMM)body).getText();
            boolean addText = (text != null && text.length() > 0);
            String resultString = (addDescription ? description : "") +
               (addDescription && addText ? "\n" : "") + (addText ? text : "");
            result.add(NID3.comments,resultString);
            id3v1props.remove(NID3.comments);
        }},
    COMR("Commercial frame",false), // not supported by NID3
    ENCR("Encryption method registration",false), // not supported by NID3
    ETCO("Event timing codes",false), // not supported by NID3
    GEOB("General encapsulated object",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            FrameBodyGEOB apic = (FrameBodyGEOB)body;
            Model model = result.getModel();
            Resource resource = UriUtil.generateRandomResource(model);
            model.addStatement(resource, RDF.type, NFO.Attachment);
            model.addStatement(resource, RDF.type, NIE.InformationElement);
            model.addStatement(result.getDescribedUri(), NID3.generalEncapsulatedObject, resource);
            // TODO, the FrameBodyGEOB class doesn't support mime types even thought the specs indicate it should, this might change
            // in future versions of jaudiotagger and deserves to be investigated
        }},
    GRID("Group identification registration",false), // not supported by NID3
    LINK("Linked information",false), // not supported by NID3
    MCDI("Music CD identifier",false) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            // the jaudiotagger sucks, the FrameBodyMCDI doesn't have any methods that would allow us to put anything interesting
            // with the NID3.musicCDIdentifier wtriple
        }}, 
    MLLT("MPEG location lookup table",false), // not supported by NID3
    OWNE("Ownership frame",false), // not supported by NID3
    PRIV("Private frame",false), // not supported by NID3
    PCNT("Play counter",false), // not supported by NID3
    POPM("Popularimeter",false), // not supported by NID3
    POSS("Position synchronisation frame",false), // not supported by NID3
    RBUF("Recommended buffer size",false), // not supported by NID3
    RVRB("Reverb",false), // not supported by NID3
    SYLT("Synchronised lyric/text",false),
    SYTC("Synchronised tempo codes",false),
    TALB("Album/Movie/Show title", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.albumTitle,((FrameBodyTALB)body).getFirstTextValue());
            id3v1props.remove(NID3.albumTitle);
        }},
    TBPM("BPM (beats per minute)",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.beatsPerMinute,result.getModel().createDatatypeLiteral(((FrameBodyTBPM)body).getFirstTextValue(),XSD._integer));
        }},
    TCOM("Composer",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.composer,((FrameBodyTCOM)body).getFirstTextValue(),result);
        }},
    TCON("Content type",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            String value = ((FrameBodyTCON)body).getFirstTextValue();
            // the ID3V2 specs allow for the use of ID3V1 genre identifiers bu they need to be enclosed in brackets
            // we disregard multiple genres (take only the first one) and the extensions (completely)\
            boolean ok = false;
            if (value.startsWith("(")) {
                int end = value.indexOf(')');
                if (end != -1) {
                    String integerString = value.substring(1,end);
                    try {
                        int intValue = Integer.parseInt(integerString);
                        Genre genre = Genre.getGenreByIntId(intValue);
                        if (genre != null) {
                            result.add(NID3.contentType, genre.getName());
                            ok = true;
                        }
                    } catch (NumberFormatException nfe) {
                        // do nothing, that's not a problem, the boolean ok var will stay false and the string will
                        // be inserted into the result RDFContainer as it is
                    }
                }
            }
            // if there were any problems with translating the value like (13) or (13)Pop or (13)(45) into "Pop"
            // leave it as it is
            if (!ok) {
                result.add(NID3.contentType, value);
            }
            id3v1props.remove(NID3.contentType);
        }},
    TCOP("Copyright message",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.copyrightMessage,((FrameBodyTCOP)body).getFirstTextValue());
        }},
    TDLY("Playlist delay",true), // not supported by NID3
    TENC("Encoded by",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.encodedBy,((FrameBodyTENC)body).getFirstTextValue(),result);
        }},
    TEXT("Lyricist/Text writer",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.textWriter,((FrameBodyTEXT)body).getFirstTextValue(),result);
        }},
    TFLT("File type",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.fileType,((FrameBodyTFLT)body).getFirstTextValue());
        }},
    TIT1("Content group description",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.contentGroupDescription,((FrameBodyTIT1)body).getFirstTextValue());
        }},
    TIT2("Title/songname/content description", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.title,((FrameBodyTIT2)body).getFirstTextValue());
            id3v1props.remove(NID3.title);
        }},
    TIT3("Subtitle/Description refinement",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.subtitle,((FrameBodyTIT3)body).getFirstTextValue());
        }},
    TKEY("Initial key",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.initialKey,((FrameBodyTKEY)body).getFirstTextValue());
        }},
    TLAN("Language(s)",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.language,((FrameBodyTLAN)body).getFirstTextValue());
        }},
    TLEN("Length",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.length,((FrameBodyTLEN)body).getFirstTextValue());
        }},
    TMED("Media type",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.mediaType,((FrameBodyTMED)body).getFirstTextValue());
        }},
    TOAL("Original album/movie/show title",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.originalAlbumTitle,((FrameBodyTOAL)body).getFirstTextValue());
        }},
    TOFN("Original filename",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.originalFilename,((FrameBodyTOFN)body).getFirstTextValue());
        }},
    TOLY("Original lyricist(s)/text writer(s)",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.originalTextWriter,((FrameBodyTOLY)body).getFirstTextValue(),result);
        }},
    TOPE("Original artist(s)/performer(s)",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.originalArtist,((FrameBodyTOPE)body).getFirstTextValue(),result);
        }},
    TOWN("File owner/licensee",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.fileOwner,((FrameBodyTOWN)body).getFirstTextValue(),result);
        }},
    TPE1("Lead performer(s)/Soloist(s)", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.leadArtist,((FrameBodyTPE1)body).getFirstTextValue(),result);
            id3v1props.remove(NID3.leadArtist);
        }},
    TPE2("Band/orchestra/accompaniment",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.backgroundArtist,((FrameBodyTPE2)body).getFirstTextValue(),result);
        }},
    TPE3("Conductor/performer refinement",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.conductor,((FrameBodyTPE3)body).getFirstTextValue(),result);
        }},
    TPE4("Interpreted, remixed, or otherwise modified by",true)  {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.interpretedBy,((FrameBodyTPE4)body).getFirstTextValue(),result);
        }},
    TPOS("Part of a set",true){
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.partOfSet,((FrameBodyTPOS)body).getFirstTextValue());
        }},
    TPUB("Publisher",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.publisher,((FrameBodyTPUB)body).getFirstTextValue(),result);
        }},
    TRCK("Track number/Position in set",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.trackNumber,((FrameBodyTRCK)body).getFirstTextValue());
            id3v1props.remove(NID3.trackNumber);
        }},
    TRSN("Internet radio station name",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.internetRadioStationName,((FrameBodyTRSN)body).getFirstTextValue());
        }},
    TRSO("Internet radio station owner",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            addSimpleContact(NID3.internetRadioStationOwner,((FrameBodyTRSO)body).getFirstTextValue(),result);
        }},
    TSRC("ISRC (international standard recording code)",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.internationalStandardRecordingCode,((FrameBodyTSRC)body).getFirstTextValue());
        }},
    TSSE("Software/Hardware and settings used for encoding",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.encodingSettings,((FrameBodyTSSE)body).getFirstTextValue());
        }},
    TXXX("User defined text information frame",false),
    UFID("Unique file identifier",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.uniqueFileIdentifier,((FrameBodyUFID)body).getOwner() + "/" + ((FrameBodyUFID)body).getIdentifier());
        }},
    USER("Terms of use",false) , // unsupported in NID3
    USLT("Unsynchronised lyric/text transcription",false) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.unsynchronizedTextContent,((FrameBodyUSLT)body).getLyric());
        }},
    WCOM("Commercial information",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWCOM)body).getUrlLink());
            result.add(NID3.commercialInformationURL,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WCOP("Copyright/Legal information",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWCOP)body).getUrlLink());
            result.add(NID3.copyrightInformationURL,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WOAF("Official audio file webpage",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWOAF)body).getUrlLink());
            result.add(NID3.officialFileWebpage,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WOAR("Official artist/performer webpage",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWOAR)body).getUrlLink());
            result.add(NID3.officialArtistWebpage,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WOAS("Official audio source webpage",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWOAS)body).getUrlLink());
            result.add(NID3.officialAudioSourceWebpage,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WORS("Official Internet radio station homepage",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWORS)body).getUrlLink());
            result.add(NID3.officialInternetRadioStationHomepage,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WPAY("Payment",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWPAY)body).getUrlLink());
            result.add(NID3.paymentURL,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WPUB("Publishers official webpage",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            Resource resource = result.getModel().createURI(((FrameBodyWPUB)body).getUrlLink());
            result.add(NID3.publishersWebpage,resource);
            result.getModel().addStatement(resource,RDF.type, RDFS.Resource);
        }},
    WXXX("User defined URL link frame",false),
    
    // these frames are new, they appeared in the 2.4.0 version of the standard
    // http://www.id3.org/id3v2.4.0-changes
    ASPI("Audio seek point index",false),
    EQU2("Equalisation (2)",false),
    RVA2("Relative volume adjustment (2)",false),
    SEEK("Seek frame",false),
    SIGN("Signature frame",false),
    TDEN("Encoding time",false),
    TDOR("Original release time",false),
    TDRC("Recording time",false),
    TDRL("Release time",false),
    TDTG("Tagging time",false),
    TIPL("Involved people list",false),
    TMCL("Musician credits list",false),
    TMOO("Mood",false),
    TPRO("Produced notice",false),
    TSOA("Album sort order",false),
    TSOP("Performer sort order",false),
    TSOT("Title sort order",false),
    TSST("Set subtitle",false),
    
    // these frames were present in ID3 v 2.3.0, but have been deprecated in
    // 2.4.0 see: http://www.id3.org/id3v2.4.0-changes
    
    /** This frame is replaced by the EQU2 frame, 'Equalisation (2)' [F:4.12]. */
    EQUA("Equalization",false),
    /** This frame is replaced by the two frames TMCL, 'Musician credits list' [F:4.2.2], and TIPL, 'Involved
     * people list' [F:4.2.2]. */
    IPLS("Involved people list",false),
    /** This frame is replaced by the RVA2 frame, 'Relative volume adjustment (2)' [F:4.11]. */
    RVAD("Relative volume adjustment",false),
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TDAT("Date",false),
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TIME("Time",false),
    /** This frame is replaced by the TDOR frame, 'Original release time' [F:4.2.5]. */
    TORY("Original release year",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2,HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.originalReleaseYear,result.getModel().createDatatypeLiteral(((FrameBodyTORY)body).getFirstTextValue(), XSD._integer));
        }},
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TRDA("Recording dates",false),
    /** The information contained in this frame is in the general case either trivial to calculate for the
     * player or impossible for the tagger to calculate. There is however no good use for such information.
     * The frame is therefore completely deprecated. */
    TSIZ("Size",false),
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TYER("Year",true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2,HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.recordingYear,result.getModel().createDatatypeLiteral(((FrameBodyTYER)body).getFirstTextValue(), XSD._integer));
            id3v1props.remove(NID3.recordingYear);
        }},
    
    // and these are the frames defined in ID3 2.2.0 standard, they used 3-letter codes
    // they are now obsolete, but files with those frames do still occur
    
    BUF("Recommended buffer size",false),
    CNT("Play counter",false),
    COM("Comments",false),
    CRA("Audio encryption",false),
    CRM("Encrypted meta frame",false),
    ETC("Event timing codes",false),
    EQU("Equalization",false),
    GEO("General encapsulated object",false),
    IPL("Involved people list",false),
    LNK("Linked information",false),
    MCI("Music CD Identifier",false),
    MLL("MPEG location lookup table",false),
    PIC("Attached picture",false),
    POP("Popularimeter",false),
    REV("Reverb",false),
    RVA("Relative volume adjustment",false),
    SLT("Synchronized lyric/text",false),
    STC("Synced tempo codes",false),
    TAL("Album/Movie/Show title",false),
    TBP("BPM (Beats Per Minute)",false),
    TCM("Composer",false),
    TCO("Content type",false),
    TCR("Copyright message",false),
    TDA("Date",false),
    TDY("Playlist delay",false),
    TEN("Encoded by",false),        
    TFT("File type",false),
    TIM("Time",false),
    TKE("Initial key",false),
    TLA("Language(s)",false),
    TLE("Length",false),
    TMT("Media type",false),
    TOA("Original artist(s)/performer(s)",false),
    TOF("Original filename",false),
    TOL("Original Lyricist(s)/text writer(s)",false),
    TOR("Original release year",false),
    TOT("Original album/Movie/Show title",false),
    TP1("Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group", false),
    TP2("Band/Orchestra/Accompaniment",false),
    TP3("Conductor/Performer refinement",false),
    TP4("Interpreted, remixed, or otherwise modified by",false),
    TPA("Part of a set",false),
    TPB("Publisher",false),
    TRC("ISRC (International Standard Recording Code)",false),
    TRD("Recording dates",false),
    TRK("Track number/Position in set",false),
    TSI("Size",false),
    TSS("Software/hardware and settings used for encoding",false),
    TT1("Content group description",false),
    TT2("Title/Songname/Content description", false),
    TT3("Subtitle/Description refinement",false),
    TXT("Lyricist/text writer",false),
    TXX("User defined text information frame",false),
    TYE("Year",false),
    UFI("Unique file identifier",false),
    ULT("Unsychronized lyric/text transcription",false),
    WAF("Official audio file webpage",false),
    WAR("Official artist/performer webpage",false),
    WAS("Official audio source webpage",false),
    WCM("Commercial information",false),
    WCP("Copyright/Legal information",false),
    WPB("Publishers official webpage",false),
    WXX("User defined URL link frame", false);
    
    private String name;
    private boolean isSupported;
    
    FrameIdentifier(String name, boolean isSupported) {
        this.name = name;
        this.isSupported = isSupported;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isSupported() {
        return isSupported;
    }
    
    public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2,
            HashMap<URI, String> id3v1props, RDFContainer result) {
        // the default behavior for unsupported frames is to do nothing
    }
    
    protected void addSimpleContact(URI property, String fullname, RDFContainer container) {
        Model model = container.getModel();
        Resource resource = UriUtil.generateRandomResource(model);
        model.addStatement(resource, RDF.type, NCO.Contact);
        model.addStatement(resource, NCO.fullname, fullname);
        model.addStatement(container.getDescribedUri(), property, resource);
    }
}

