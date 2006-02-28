package org.semanticdesktop.aperture.accessor.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ArrayMap;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.semanticdesktop.aperture.util.XmlWriter;
import org.xml.sax.SAXException;

/**
 * A basic AccessData implementation that stores all its information in main memory and can read it from
 * and write it to a file.
 */
public class FileAccessData extends AccessDataImpl {

    private static final Logger LOGGER = Logger.getLogger(FileAccessData.class.getName());

    /**
     * The File from which previous access data is loaded and to which new data will be written. This may
     * be null in case an in-memory-only AccessData is required.
     */
    private File dataFile;

    /**
     * Creates a new FileAccessData instance. The access data cannot be made persistent until a data file
     * is specified.
     */
    public FileAccessData() {
    	super();
    }

    /**
     * Creates a new FileAccessData that uses the specified File for persistent storage.
     * 
     * @param dataFile The data file to be used by this FileAccessData.
     */
    public FileAccessData(File dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Sets the data File to be used by this FileAccessData.
     * 
     * @param dataFile The data File to use or 'null' when the FileAccessData is not required (anymore)
     *            to persistently store its access data.
     */
    public void setDataFile(File dataFile) {
        this.dataFile = dataFile;
    }

    public File getDataFile() {
        return dataFile;
    }

    public void initialize() throws IOException {
        idMap = null;
        referredIDMap = null;
        super.initialize();
        if (dataFile != null && dataFile.exists()) {
            FileInputStream fileStream = new FileInputStream(dataFile);
            BufferedInputStream buffer = new BufferedInputStream(fileStream);
            GZIPInputStream zipStream = new GZIPInputStream(buffer);
            read(zipStream);
            zipStream.close();
        }
    }

    public void store() throws IOException {
        if (dataFile != null) {
            FileOutputStream fileStream = new FileOutputStream(dataFile);
            BufferedOutputStream buffer = new BufferedOutputStream(fileStream);
            GZIPOutputStream zipStream = new GZIPOutputStream(buffer);
            write(zipStream);
            zipStream.close();
        }
        // somehow in practice, setting it null doesn't allow storing twice, which somebody does
        //idMap = null;
        //referredIDMap = null;
    }
    
    public void clear() throws IOException {
        super.clear();
        if (dataFile != null && dataFile.exists()) {
            dataFile.delete();
        }
    }

    private void read(InputStream in) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
            SimpleSAXAdapter listener = new AccessDataParser();
            parser.setListener(listener);
            parser.parse(in);
        }
        catch (ParserConfigurationException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (SAXException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }

    private void write(OutputStream out) throws IOException {
        XmlWriter xmlWriter = new XmlWriter(out);
        xmlWriter.setPrettyPrint(true);

        xmlWriter.startDocument();
        xmlWriter.setAttribute("version", 1);
        xmlWriter.startTag("scanresult");

        Iterator idIter = idMap.keySet().iterator();
        while (idIter.hasNext()) {
            String id = (String) idIter.next();

            if (id != null) {
                xmlWriter.setAttribute("id", id.toString());
                xmlWriter.startTag("dataobject");

                writeInfo(id, xmlWriter);

                xmlWriter.endTag("dataobject");
            }
            else {
                LOGGER.log(Level.WARNING, "Failed to write null id");
            }
        }

        xmlWriter.endTag("scanresult");
        xmlWriter.endDocument();
    }

    /**
     * Writes the information for the data object with the specified id using the supplied XmlWriter.
     */
    private void writeInfo(String id, XmlWriter xmlWriter) throws IOException {
        ArrayMap infoMap = (ArrayMap) idMap.get(id);
        if (infoMap != null) {
            Iterator entries = infoMap.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry entry = (Entry) entries.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                xmlWriter.textElement(key, value);
            }
        }

        Set referredIDs = (Set) referredIDMap.get(id);
        if (referredIDs != null) {
            Iterator ids = referredIDs.iterator();
            while (ids.hasNext()) {
                String referredID = (String) ids.next();
                xmlWriter.textElement(REFERRED_ID_TAG, referredID);
            }
        }
    }

    private class AccessDataParser extends SimpleSAXAdapter {

        private String dataObjectId;

        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (tagName.equals("scanresult")) {
                // Check version number
                String version = (String) atts.get("version");
                if (version == null) {
                    throw new SAXException("Scan results file version missing");
                }

                try {
                    int versionInt = Integer.parseInt(version);
                    if (versionInt != 1) {
                        throw new SAXException("Unsupported scan results file version: " + version);
                    }
                }
                catch (NumberFormatException e) {
                    throw new SAXException("Illegal scan results file version: " + version);
                }
            }
            else if (tagName.equals("dataobject")) {
                dataObjectId = (String) atts.get("id");
            }
            else if (tagName.equals(REFERRED_ID_TAG)) {
                String referredID = text;

                if (referredID != null && dataObjectId != null) {
                    HashSet referredIDs = (HashSet) referredIDMap.get(dataObjectId);

                    if (referredIDs == null) {
                        referredIDs = new HashSet();
                        referredIDMap.put(dataObjectId, referredIDs);
                    }

                    referredIDs.add(referredID);
                }
            }
            else if (dataObjectId != null) {
                put(dataObjectId, tagName, text);
            }
        }

        public void endTag(String tagName) {
            if (tagName.equals("dataobject")) {
                dataObjectId = null;
            }
        }
    }
}
