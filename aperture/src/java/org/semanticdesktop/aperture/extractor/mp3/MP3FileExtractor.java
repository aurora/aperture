/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mp3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.AbstractFileExtractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NID3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A file extractor implementation for MP3 files.
 */
public class MP3FileExtractor extends AbstractFileExtractor {

    private Logger logger = LoggerFactory.getLogger(MP3FileExtractor.class);
    
    /**
     * Extracts ID3 metadata from an MP3 file
     * 
     * @param id the URI of the file
     * @param file the java.io.File instance representing the path to the MP3 file. It is assumed that the
     *            file exists and is readable.
     * @param charset ignored in this implementation
     * @param mimeType ignored in this implementation
     * @param result the RDFContainer where the generated triples should be stored
     */
    protected void performExtraction(URI id, File file, Charset charset, String mimeType, RDFContainer result)
            throws ExtractorException {
        MP3File mp3File = null;
        try {
            // we want to open the file in write mode
            mp3File = new MP3File(file);
        }
        catch (FileNotFoundException e) {
            throw new ExtractorException(e);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }
        catch (TagException e) {
            throw new ExtractorException("File format not supported by the MP3Exctractor: "
                    + file.getParent(), e);
        }
        catch (ReadOnlyFileException e) {
            throw new ExtractorException(e);
        }
        catch (InvalidAudioFrameException e) {
            throw new ExtractorException(e);
        }

        HashMap<URI, String> id3v1hashMap = new HashMap<URI, String>();

        if (mp3File.hasID3v1Tag()) {
            processID3V1Tags(id, mp3File, charset, mimeType, id3v1hashMap);
        }

        if (mp3File.hasID3v2Tag()) {
            processID3V2Tags(id, mp3File, charset, mimeType, id3v1hashMap, result);
        }

        /*
         * now that we processed all the ID3V2 tags, we can add the remaining id3v1 fields we do it now
         * because after processing id3v2 frames the id3v1 fields may have been obsoleted this solution is
         * better because the extractor doesn't need to delete anything from the rdfcontainer
         */
        addId3V1Fields(id3v1hashMap, result);
        /*
        if (mp3File.hasFilenameTag()) {
            processFilenameTag(id, mp3File, charset, mimeType, result);
        }

        if (mp3File.hasLyrics3Tag()) {
            processLyrics3Tag(id, mp3File, charset, mimeType, result);
        }
        */
        result.add(RDF.type, NID3.ID3Audio);

    }

    private void processID3V1Tags(URI id, MP3File mp3File, Charset charset, String mimeType,
            HashMap<URI, String> resultHashMap) {

        ID3v1Tag id3v1 = mp3File.getID3v1Tag();

        // note that getSongTitle is the same as getTitle().trim() - viva la open source
        addStringProperty(NID3.title, id3v1.getFirstTitle(), resultHashMap);

        // note that getLeadArtist is the same as getArtist().trim()
        addStringProperty(NID3.leadArtist, id3v1.getFirstArtist(), resultHashMap);

        // note that getAlbumTitle is the same as getAlbum().trim()
        addStringProperty(NID3.albumTitle, id3v1.getFirstAlbum(), resultHashMap);

        // note that getYearReleased is the same as getYear().trim()
        addStringProperty(NID3.recordingYear, id3v1.getFirstYear(), resultHashMap);

        // note that getSongComment is the same as getComment().trim()
        addStringProperty(NID3.comments, id3v1.getFirstComment(), resultHashMap);

        addStringProperty(NID3.contentType, id3v1.getFirstGenre(), resultHashMap);

        if (id3v1 instanceof ID3v11Tag) {
            ID3v11Tag id3v1_1 = (ID3v11Tag) id3v1;
            addStringProperty(NID3.trackNumber, id3v1_1.getFirstTrack(), resultHashMap);
        }
    }

    private void processID3V2Tags(URI id, MP3File mp3File, Charset charset, String mimeType,
            HashMap<URI, String> id3v1FieldHashMap, RDFContainer result) {
        AbstractID3v2Tag id3v2 = mp3File.getID3v2Tag();
        
        Iterator iterator = id3v2.getFields();
        while (iterator.hasNext()) {
            AbstractID3v2Frame frame = (AbstractID3v2Frame)iterator.next();
            String identifier = frame.getIdentifier();
            try {
                FrameIdentifier frameIdentifier = FrameIdentifier.valueOf(identifier.trim());
                AbstractTagFrameBody body = frame.getBody();
                frameIdentifier.process(body, id3v2, id3v1FieldHashMap, result);
            } catch (Exception e) {
                logger.warn("Problem while getting the frame '" + identifier + "' from file " + id,e);
            }
        }
    }

    private void addStringProperty(URI property, String string, HashMap<URI, String> resultHashMap) {
        if (string != null && string.length() > 0) {
            resultHashMap.put(property, string);
        }
    }

    private void addId3V1Fields(HashMap<URI, String> id3v1hashMap, RDFContainer result) {
        Iterator<URI> iterator = id3v1hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            String value = id3v1hashMap.get(uri);
            if (uri.equals(NID3.recordingYear)) {
                try {
                    int intValue = Integer.parseInt(value);
                    result.add(uri, intValue);
                }
                catch (NumberFormatException nfe) {
                    // ignore this
                }
            }
            else if (uri.equals(NID3.leadArtist)) {
                Model model = result.getModel();
                Resource anonymousContact = UriUtil.generateRandomResource(model);
                model.addStatement(result.getDescribedUri(), NID3.leadArtist, anonymousContact);
                model.addStatement(anonymousContact, RDF.type, NCO.Contact);
                model.addStatement(anonymousContact, NCO.fullname, value);
            }
            else {
                result.add(uri, value);
            }
        }
    }
}
