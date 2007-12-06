/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.fileextractor.mp3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v1_1;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.fileextractor.AbstractFileExtractor;
import org.semanticdesktop.aperture.fileextractor.FileExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NID3;

/**
 * A file extractor implementation for MP3 files.
 */
public class MP3FileExtractor extends AbstractFileExtractor {

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
            throws FileExtractorException {
        MP3File mp3File = null;
        try {
            // we want to open the file in write mode
            mp3File = new MP3File(file, false);
        }
        catch (IOException e) {
            throw new FileExtractorException(e);
        }
        catch (TagException e) {
            throw new FileExtractorException("File format not supported by the MP3Exctractor: "
                    + file.getParent(), e);
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

        if (mp3File.hasFilenameTag()) {
            processFilenameTag(id, mp3File, charset, mimeType, result);
        }

        if (mp3File.hasLyrics3Tag()) {
            processLyrics3Tag(id, mp3File, charset, mimeType, result);
        }

        result.add(RDF.type, NID3.ID3Audio);

    }

    private void processID3V1Tags(URI id, MP3File mp3File, Charset charset, String mimeType,
            HashMap<URI, String> resultHashMap) {

        /*
         * The crappy thing about this class is that it contains getters for fields that are not present in
         * the ID3v1 specification. These getters throw an UnsupportedOperationException. Why did they end up
         * there in the first place - only the author knows. The id3v1 spec contained exactly SIX fields. And
         * only those six can actually be extracted from this object. Why does it have 17 getters for fields
         * if six of them throw UnsupportedOperationException, five other getters only do a trim() and only
         * those six actually return the internal fields. This deserves a honorable mention at thedailywtf.com
         */
        ID3v1 id3v1 = mp3File.getID3v1Tag();

        // note that getSongTitle is the same as getTitle().trim() - viva la open source
        addStringProperty(NID3.title, id3v1.getSongTitle(), resultHashMap);

        // note that getLeadArtist is the same as getArtist().trim()
        addStringProperty(NID3.leadArtist, id3v1.getLeadArtist(), resultHashMap);

        // note that getAlbumTitle is the same as getAlbum().trim()
        addStringProperty(NID3.albumTitle, id3v1.getAlbumTitle(), resultHashMap);

        // note that getYearReleased is the same as getYear().trim()
        addStringProperty(NID3.recordingYear, id3v1.getYearReleased(), resultHashMap);

        // note that getSongComment is the same as getComment().trim()
        addStringProperty(NID3.comments, id3v1.getSongComment(), resultHashMap);        
        
        byte genre = id3v1.getGenre();
        if (genre != -1) {
            addStringProperty(NID3.contentType, Genre.getGenreById(id3v1.getGenre()).getName(), resultHashMap);
        }
    
        if (id3v1 instanceof ID3v1_1) {
            ID3v1_1 id3v1_1 = (ID3v1_1) id3v1;
            byte trackNumber = id3v1_1.getTrack();
            // this is crappy, it is impossible to tell if 0 is the actual track number
            // or if it's an indication that no track number has been set
            if (trackNumber > 0) {
                addStringProperty(NID3.trackNumber, id3v1_1.getTrackNumberOnAlbum(), resultHashMap);
            }
        }
    }

    private void processID3V2Tags(URI id, MP3File mp3File, Charset charset, String mimeType,
            HashMap<URI, String> id3v1FieldHashMap, RDFContainer result) {

        AbstractID3v2 id3v2 = mp3File.getID3v2Tag();

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

    private void processFilenameTag(URI id, MP3File mp3File, Charset charset, String mimeType,
            RDFContainer result) {
    // TODO add support for the filename tag
    }

    private void processLyrics3Tag(URI id, MP3File mp3File, Charset charset, String mimeType,
            RDFContainer result) {
    // TODO add support for the lyrics3 tags
    }

    private void addStringProperty(URI property, String string, HashMap<URI, String> resultHashMap) {
        if (string != null && string.length() > 0) {
            resultHashMap.put(property, string);
        }
    }
}
