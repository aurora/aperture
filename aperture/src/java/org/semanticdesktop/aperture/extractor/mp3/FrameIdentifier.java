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
import org.jaudiotagger.tag.id3.framebody.FrameBodyCOMM;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTALB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE1;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NID3;

/**
 * An enumeration of ID3v2 frames defined in the standards.
 */
public enum FrameIdentifier {
    
    // the frames below have come from 2.3.0 and were kept in 2.4.0 
    // they are common to both versions of the standard
    // http://www.id3.org/id3v2.4.0-frames
    
    AENC("Audio encryption",false),
    APIC("Attached picture",false),
    COMM("Comments",false) {
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
    COMR("Commercial frame",false),
    ENCR("Encryption method registration",false),
    ETCO("Event timing codes",false),
    GEOB("General encapsulated object",false),
    GRID("Group identification registration",false),
    LINK("Linked information",false),
    MCDI("Music CD identifier",false),
    MLLT("MPEG location lookup table",false),
    OWNE("Ownership frame",false),
    PRIV("Private frame",false),
    PCNT("Play counter",false),
    POPM("Popularimeter",false),
    POSS("Position synchronisation frame",false),
    RBUF("Recommended buffer size",false),
    RVRB("Reverb",false),
    SYLT("Synchronised lyric/text",false),
    SYTC("Synchronised tempo codes",false),
    TALB("Album/Movie/Show title", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.albumTitle,((FrameBodyTALB)body).getFirstTextValue());
            id3v1props.remove(NID3.albumTitle);
        }},
    TBPM("BPM (beats per minute)",false),
    TCOM("Composer",false),
    TCON("Content type",false),
    TCOP("Copyright message",false),
    TDLY("Playlist delay",false),
    TENC("Encoded by",false),
    TEXT("Lyricist/Text writer",false),
    TFLT("File type",false),
    TIT1("Content group description",false),
    TIT2("Title/songname/content description", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.title,((FrameBodyTIT2)body).getFirstTextValue());
            id3v1props.remove(NID3.title);
        }},
    TIT3("Subtitle/Description refinement",false),
    TKEY("Initial key",false),
    TLAN("Language(s)",false),
    TLEN("Length",false),
    TMED("Media type",false),
    TOAL("Original album/movie/show title",false),
    TOFN("Original filename",false),
    TOLY("Original lyricist(s)/text writer(s)",false),
    TOPE("Original artist(s)/performer(s)",false),
    TOWN("File owner/licensee",false),
    TPE1("Lead performer(s)/Soloist(s)", true) {
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            result.add(NID3.leadArtist,((FrameBodyTPE1)body).getFirstTextValue());
            id3v1props.remove(NID3.leadArtist);
        }},
    TPE2("Band/orchestra/accompaniment",false),
    TPE3("Conductor/performer refinement",false),
    TPE4("Interpreted, remixed, or otherwise modified by",false),
    TPOS("Part of a set",false),
    TPUB("Publisher",false),
    TRCK("Track number/Position in set",false),
    TRSN("Internet radio station name",false),
    TRSO("Internet radio station owner",false),
    TSRC("ISRC (international standard recording code)",false),
    TSSE("Software/Hardware and settings used for encoding",false),
    TXXX("User defined text information frame",false),
    UFID("Unique file identifier",false),
    USER("Terms of use",false),
    USLT("Unsynchronised lyric/text transcription",false),
    WCOM("Commercial information",false),
    WCOP("Copyright/Legal information",false),
    WOAF("Official audio file webpage",false),
    WOAR("Official artist/performer webpage",false),
    WOAS("Official audio source webpage",false),
    WORS("Official Internet radio station homepage",false),
    WPAY("Payment",false),
    WPUB("Publishers official webpage",false),
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
    TORY("Original release year",false),
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TRDA("Recording dates",false),
    /** The information contained in this frame is in the general case either trivial to calculate for the
     * player or impossible for the tagger to calculate. There is however no good use for such information.
     * The frame is therefore completely deprecated. */
    TSIZ("Size",false),
    /** This frame is replaced by the TDRC frame, 'Recording time' [F:4.2.5]. */
    TYER("Year",false),
    
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
    TP1("Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group", true){ 
        public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
            TPE1.process(body, id3v2, id3v1props, result);
        }},
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
    TT2("Title/Songname/Content description", true) { 
            public void process(AbstractTagFrameBody body, AbstractID3v2Tag id3v2, HashMap<URI, String> id3v1props, RDFContainer result) {
                TIT2.process(body, id3v2, id3v1props, result);
            }},
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
}

