package org.semanticdesktop.aperture.accessor.base;

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

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ArrayMap;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.semanticdesktop.aperture.util.XmlWriter;
import org.xml.sax.SAXException;

/**
 * A basic AccessData implementation that stores all its information in main memory and can read it from
 * and write it to a stream.
 */
public class AccessDataBase implements AccessData {

    private static final Logger LOGGER = Logger.getLogger(AccessDataBase.class.getName());

    private static final String REFERRED_ID_TAG = "referredID";

    /**
     * A Map mapping IDs to another Map that contains the key-value pairs for that ID.
     */
    private HashMap idMap;

    /**
     * A mapping from IDs to Sets of IDs that the former ID refers to. This can be used to model
     * parent-child relationships or links between IDs. The use of this mapping is typically to register
     * IDs that need special treatment once the referring ID has been changed or removed.
     */
    private HashMap referredIDMap;

    /**
     * Creates a new AccessData instance.
     */
    public AccessDataBase() {
        initMaps();
    }

    private void initMaps() {
        idMap = new HashMap(1024);
        referredIDMap = new HashMap(1024);
    }

    public int getSize() {
        // warning: this assumes that every ID is at least stored in the idMap, i.e. IDs for which only
        // parent-child relationships are stored are not counted.
        return idMap.size();
    }

    public Set getStoredIDs() {
        HashSet result = new HashSet(idMap.keySet());
        result.addAll(referredIDMap.keySet());
        return result;
    }

    public boolean isKnownId(String id) {
        return idMap.containsKey(id) || referredIDMap.containsKey(id);
    }

    public void clear() {
        initMaps();
    }

    public void put(String id, String key, String value) {
        // assumption: lots of objects with relative few things to store: use an ArrayMap
        ArrayMap infoMap = createInfoMap(id);
        infoMap.put(key, value);
    }

    public void putReferredID(String id, String referredID) {
        HashSet ids = (HashSet) referredIDMap.get(id);

        if (ids == null) {
            ids = new HashSet();
            referredIDMap.put(id, ids);
        }

        ids.add(referredID);
    }

    public String get(String id, String key) {
        ArrayMap infoMap = (ArrayMap) idMap.get(id);
        if (infoMap == null) {
            return null;
        }
        else {
            return (String) infoMap.get(key);
        }
    }

    public Set getReferredIDs(String id) {
        return (Set) referredIDMap.get(id);
    }

    public void remove(String id, String key) {
        ArrayMap infoMap = (ArrayMap) idMap.get(id);
        if (infoMap != null) {
            infoMap.remove(key);
        }
    }

    public void removeReferredID(String id, String referredID) {
        HashSet ids = (HashSet) referredIDMap.get(id);
        if (ids != null) {
            ids.remove(referredID);
            
            if (ids.isEmpty()) {
                referredIDMap.remove(id);
            }
        }
    }

    public void remove(String id) {
        idMap.remove(id);
        referredIDMap.remove(id);
    }

    private ArrayMap createInfoMap(String id) {
        ArrayMap infoMap = (ArrayMap) idMap.get(id);
        if (infoMap == null) {
            infoMap = new ArrayMap();
            idMap.put(id, infoMap);
        }
        return infoMap;
    }

    /**
     * Reads an XML document containing ID information from the supplied InputStream. This method does
     * not close the InputStream when it has finished reading, this is the responsibility of the method's
     * caller.
     * 
     * @param in The stream to read the XML document from.
     * @exception IOException If an I/O error occurred while reading from the supplied stream.
     */
    public void read(InputStream in) throws IOException {
        read(in, new AccessDataParser());
    }

    private void read(InputStream in, SimpleSAXAdapter listener) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
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

    /**
     * Writes an XML document containing data object info to the supplied OutputStream. This method does
     * not close the OutputStream when it has finished writing, this is the responsibility of the
     * method's caller.
     * 
     * @param out The stream to write the XML document to.
     * @exception IOException If an I/O error occurred while writing to the supplied stream.
     */
    public void write(OutputStream out) throws IOException {
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

        public void startDocument() {
            // Clear any current results
            clear();
        }

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
