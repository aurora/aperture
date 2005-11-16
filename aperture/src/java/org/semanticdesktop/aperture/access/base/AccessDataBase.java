package org.semanticdesktop.aperture.access.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.access.AccessData;
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

    private static final String CHILD_TAG = "child";

    /**
     * A Map mapping IDs to another Map that contains the key-value pairs for that ID.
     */
    private HashMap idMap;

    /**
     * A Map mapping IDs to TreeSets of IDs, representing a parent-child relationship. The parent is used
     * as key and maps to a TreeSet of children.
     */
    private HashMap childrenMap;

    /**
     * Creates a new AccessData instance.
     */
    public AccessDataBase() {
        initMaps();
    }

    private void initMaps() {
        idMap = new HashMap(1024);
        childrenMap = new HashMap(1024);
    }

    public int getSize() {
        // warning: this assumes that every ID is at least stored in the idMap, i.e. IDs for which only
        // parent-child relationships are stored are not counted.
        return idMap.size();
    }

    public Set getStoredIDs() {
        HashSet result = new HashSet(idMap.keySet());
        result.addAll(childrenMap.keySet());
        return result;
    }

    public boolean isKnownId(String id) {
        return idMap.containsKey(id) || childrenMap.containsKey(id);
    }

    public void clear() {
        initMaps();
    }

    public void put(String id, String key, String value) {
        // assumption: lots of objects with relative few things to store: use an ArrayMap
        ArrayMap infoMap = getInfoMap(id);
        infoMap.put(key, value);
    }

    public void putChild(String id, String child) {
        TreeSet children = (TreeSet) childrenMap.get(id);

        if (children == null) {
            children = new TreeSet();
            childrenMap.put(id, children);
        }

        children.add(child);
    }

    public String get(String id, String key) {
        ArrayMap infoMap = getInfoMap(id);
        if (infoMap == null) {
            return null;
        }
        else {
            return (String) infoMap.get(key);
        }
    }

    public Set getChildren(String id) {
        return (Set) childrenMap.get(id);
    }

    public void remove(String id, String key) {
        ArrayMap infoMap = getInfoMap(id);
        if (infoMap != null) {
            infoMap.remove(key);
        }
    }

    public void removeChild(String id, String child) {
        TreeSet children = (TreeSet) childrenMap.get(id);
        if (children != null) {
            children.remove(child);
        }
    }

    public void remove(String id) {
        idMap.remove(id);
        childrenMap.remove(id);
    }

    private ArrayMap getInfoMap(String id) {
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
        Map info = getInfoMap(id);
        Iterator entries = info.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry entry = (Entry) entries.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            xmlWriter.textElement(key, value);
        }

        Set childrenSet = (Set) childrenMap.get(id);
        if (childrenSet != null) {
            Iterator children = childrenSet.iterator();
            while (children.hasNext()) {
                String child = (String) children.next();
                xmlWriter.textElement(CHILD_TAG, child.toString());
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
            else if (tagName.equals(CHILD_TAG)) {
                String childURI = text;

                if (childURI != null && dataObjectId != null) {
                    TreeSet children = (TreeSet) childrenMap.get(dataObjectId);

                    if (children == null) {
                        children = new TreeSet<String>();
                        childrenMap.put(dataObjectId, children);
                    }

                    children.add(childURI);
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
