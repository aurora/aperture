/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.openxml;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXListener;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.semanticdesktop.aperture.vocabulary.DATA;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Extracts full-text and metadata from Office Open XML documents, the XML- and ZIP-based format introduced by
 * Microsoft Office 2007.
 */
public class OpenXmlExtractor implements Extractor {

    /*
     * Based on a study of the ECMA OpenXML drafts and sample OpenXML documents created with Office 2007 beta
     * 2, I gathered the following list of OpenXML content types that contain full-text fragments, together
     * with the tags that indicate full-text content.
     * 
     * It seems that these elements always contain pure text, i.e. no nested elements, which makes text
     * extraction easier.
     * 
     * Also I have never seen these tags being used elsewhere containing non-full-text content, so it is
     * probably safe to put them all on one pile and check all full-text-containing content types for all
     * known full-text tags. This also alleviates the problem that I haven't been able yet to determine the
     * full-text tags for some of these content types (see the questionmarks below). It is very likely that
     * these content types also use one of these tags.
     * 
     * application/vnd.ms-word.document.macroEnabled.main+xml: w:t
     * application/vnd.ms-word.template.macroEnabledTemplate.main+xml: w:t
     * application/vnd.openxmlformats-officedocument.presentationml.comments+xml: p:text
     * application/vnd.openxmlformats-officedocument.presentationml.slide+xml: a:t
     * application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml: a:t st:t? (the first was
     * encountered, the latter suggested by Ecma spec example)
     * application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml: a:t
     * application/vnd.openxmlformats-officedocument.presentationml.notesMaster+xml: a:t
     * application/vnd.openxmlformats-officedocument.presentationml.handoutMaster+xml: ?
     * application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml: st:t t
     * application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml: t
     * application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml: v
     * application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml: ?
     * application/vnd.openxmlformats-officedocument.wordprocessingml.comments+xml: ?
     * application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.main+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document.glossary+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml: w:t
     * application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml: w:t
     * 
     * Besides elements with full-text as PCDATA, there are also elements that contain full-text as an
     * attribute value. The all appear to use the "name" attribute to hold this full-text.
     * 
     * application/vnd.openxmlformats-officedocument.presentationml.commentAuthors+xml:
     * 
     * <p:cmAuthor id="0" name="Mary Smith"...
     * 
     * application/vnd.openxmlformats-officedocument.spreadsheetml.main+xml and
     * application/vnd.openxmlformats-officedocument.spreadsheetml.template.main+xml:
     * 
     * <sheet name="January" ...
     * 
     * application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml:
     * 
     * <tableColumn id="1" name="Salesman" ...
     * 
     * application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml:
     * 
     * <p:cSld name="">
     */

    private static final Logger LOGGER = Logger.getLogger(OpenXmlExtractor.class.getName());

    private static final String CONTENT_TYPES_FILE = "[Content_Types].xml";

    private static final int BUFFER_SIZE = 4 * 1024 * 1024;

    private static final String END_OF_LINE = System.getProperty("line.separator", "\n");

    /**
     * The set of OpenXML content types that contain full-text fragments embedded as PCDATA in certain
     * elements.
     */
    private static final HashSet<String> TEXT_ELEMENT_TYPES = new HashSet<String>();

    /**
     * A mapping from OpenXML content types to a tag that potentially has a name attribute containing a
     * full-text fragment.
     */
    private static final HashMap<String, String> TEXT_ATTRIBUTE_TYPES = new HashMap<String, String>();

    static {
        TEXT_ELEMENT_TYPES.add("application/vnd.ms-word.document.macroEnabled.main+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.ms-word.template.macroEnabledTemplate.main+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.presentationml.comments+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.presentationml.slide+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.presentationml.notesMaster+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.presentationml.handoutMaster+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.comments+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml");
        TEXT_ELEMENT_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.main+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.wordprocessingml.document.glossary+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
        TEXT_ELEMENT_TYPES
                .add("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml");

        TEXT_ATTRIBUTE_TYPES.put(
            "application/vnd.openxmlformats-officedocument.presentationml.commentAuthors+xml", "p:cmAuthor");
        TEXT_ATTRIBUTE_TYPES.put("application/vnd.openxmlformats-officedocument.spreadsheetml.main+xml",
            "sheet");
        TEXT_ATTRIBUTE_TYPES.put(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template.main+xml", "sheet");
        TEXT_ATTRIBUTE_TYPES.put("application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml",
            "tableColumn");
        TEXT_ATTRIBUTE_TYPES.put(
            "application/vnd.openxmlformats-officedocument.presentationml.notesSlide+xml", "p:cSld");
    }

    private ContentTypes contentTypes;

    private StringBuilder fullText = new StringBuilder(256 * 1024);

    public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
            throws ExtractorException {
        // wrap the stream in a mark-supported stream so that we can reset it after we've processes the
        // content types file
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream, BUFFER_SIZE);
        }
        stream.mark(BUFFER_SIZE);

        // create a ZipStream around the InputStream
        ZipInputStream zipStream = null;
        try {
            zipStream = new ZipInputStream(stream);
            ZipEntry entry = null;

            // loop over all entries in the package
            while ((entry = zipStream.getNextEntry()) != null) {
                String name = entry.getName();

                // see if this is the Content Types component
                if (CONTENT_TYPES_FILE.equals(name)) {
                    // parse the contents of this XML document
                    parseContentTypes(zipStream);
                    break;
                }

                zipStream.closeEntry();
            }
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // some sanity checking
        if (contentTypes == null) {
            throw new ExtractorException("missing " + CONTENT_TYPES_FILE + " file");
        }

        // reset the stream
        try {
            stream.reset();
        }
        catch (IOException e) {
            throw new ExtractorException("Unable to reset stream", e);
        }

        // loop over all entries in the zip file again
        try {
            zipStream = new ZipInputStream(stream);
            ZipEntry entry = null;

            // loop over all entries in the package
            while ((entry = zipStream.getNextEntry()) != null) {
                // determine the type of this entry
                String name = entry.getName();
                name = toAbsoluteName(name);
                String type = contentTypes.getType(name);

                if (type != null) {
                    if (TEXT_ELEMENT_TYPES.contains(type)) {
                        process(zipStream, new TextCollector());
                    }
                    else if (TEXT_ATTRIBUTE_TYPES.containsKey(type)) {
                        process(zipStream, new AttributeTextCollector(TEXT_ATTRIBUTE_TYPES.get(type)));
                    }
                    else if ("application/vnd.openxmlformats-package.core-properties+xml".equals(type)
                            || "application/vnd.openxmlformats-officedocument.extended-properties+xml"
                                    .equals(type)) {
                        extractMetadata(zipStream, result);
                    }
                }

                zipStream.closeEntry();
            }
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

        // add the extracted text to the metadata
        String text = fullText.toString();
        if (text.length() > 0) {
            result.add(DATA.fullText, text);
        }
    }

    private void parseContentTypes(InputStream stream) throws ExtractorException {
        Document doc = getDocument(stream, false);
        contentTypes = new ContentTypes();

        // traverse the document structure and add the encountered defaults and overrides to contentTypes
        Element rootElement = doc.getDocumentElement();
        NodeList children = rootElement.getChildNodes();
        int nrChildren = children.getLength();

        for (int i = 0; i < nrChildren; i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) child;
                String name = element.getTagName();
                String contentType = element.getAttribute("ContentType");

                if ("Default".equals(name)) {
                    String extension = element.getAttribute("Extension");
                    if (extension != null && contentType != null) {
                        contentTypes.addDefault(extension, contentType);
                    }
                }
                else if ("Override".equals(name)) {
                    String partName = element.getAttribute("PartName");
                    if (partName != null && contentType != null) {
                        contentTypes.addOverride(partName, contentType);
                    }
                }
            }
        }
    }

    private Document getDocument(InputStream stream, boolean namespaceAware) throws ExtractorException {
        // create a DocumentBuilder instance
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(namespaceAware);
        docBuilderFactory.setValidating(false);
        docBuilderFactory.setExpandEntityReferences(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            // this is an internal error rather than an extraction problem, hence the RuntimeException
            throw new RuntimeException("unable to instantiate DocumentBuilder", e);
        }

        // parse the XML in the specified stream
        try {
            // make sure the stream does not get closed or else the entire ZipStream will be closed
            return docBuilder.parse(new NonCloseableStream(stream));
        }
        catch (SAXException e) {
            throw new ExtractorException(e);
        }
        catch (IOException e) {
            throw new ExtractorException(e);
        }

    }

    private String toAbsoluteName(String name) {
        // this could be improved, e.g. to handle theoretical names like word/../word/document.xml
        if (name.startsWith("/")) {
            return name;
        }
        else {
            return "/" + name;
        }
    }

    private void process(InputStream stream, SimpleSAXListener listener) {
        // create a SimpleSaxParser
        SimpleSAXParser parser = null;
        try {
            parser = new SimpleSAXParser();
        }
        catch (Exception e) {
            // this is an internal error rather than an extraction problem, hence the RuntimeException
            throw new RuntimeException("unable to instantiate SAXParser", e);
        }

        parser.setListener(listener);
        parser.setTrimWhiteSpace(false);

        try {
            parser.parse(new NonCloseableStream(stream));
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while parsing XML", e);
        }
    }

    private void extractMetadata(InputStream stream, RDFContainer metadata) throws ExtractorException {
        Document document = getDocument(stream, true);
        Element root = document.getDocumentElement();

        // loop over all elements below the document element
        NodeList children = root.getChildNodes();
        int nrChildren = children.getLength();
        for (int i = 0; i < nrChildren; i++) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                String text = getText(childElement);

                if (text != null) {
                    // if it has a namespace URI, we can easily create an RDF property from it
                    String nameSpace = childElement.getNamespaceURI();
                    if (nameSpace != null) {
                        if (!nameSpace.endsWith("/")) {
                            nameSpace += "/";
                        }

                        try {
                            String uriString = nameSpace + childElement.getLocalName();
                            URI predicate = metadata.getValueFactory().createURI(uriString);
                            metadata.add(predicate, text);
                        }
                        catch (ModelException e) {
                            LOGGER.log(Level.WARNING, "ModelException while adding statement, ignoring", e);
                        }
                    }

                    // make sure we also add all relevant properties from the Aperture namespace
                    mapToApertureProperty(childElement, text, metadata);
                }
            }
        }
    }

    private void mapToApertureProperty(Element element, String value, RDFContainer metadata) {
        String localName = element.getLocalName();

        // note this tests for both core and app properties
        if ("title".equals(localName)) {
            metadata.add(DATA.title, value);
        }
        else if ("subject".equals(localName)) {
            metadata.add(DATA.subject, value);
        }
        else if ("created".equals(localName)) {
            metadata.add(DATA.created, value);
            metadata.add(DATA.date, value);
        }
        else if ("creator".equals(localName)) {
            metadata.add(DATA.creator, value);
        }
        else if ("description".equals(localName)) {
            metadata.add(DATA.description, value);
        }
        else if ("lastModifiedBy".equals(localName)) {
            metadata.add(DATA.creator, value);
        }
        else if ("modified".equals(localName)) {
            metadata.add(DATA.modified, value);
            metadata.add(DATA.date, value);
        }
        else if ("Application".equals(localName)) {
            metadata.add(DATA.generator, value);
        }
        else if ("Pages".equals(localName) || "Slides".equals(localName)) {
            try {
                metadata.add(DATA.pageCount, Integer.parseInt(value));
            }
            catch (NumberFormatException e) {
                // ignore
            }
        }
        else if ("keywords".equals(localName)) {
            StringTokenizer tokenizer = new StringTokenizer(value, " \t.,;|/\\", false);
            while (tokenizer.hasMoreTokens()) {
                String keyword = tokenizer.nextToken();
                metadata.add(DATA.keyword, keyword);
            }
        }
    }

    private String getText(Element element) {
        Node child = element.getFirstChild();
        if (child instanceof Text) {
            return ((Text) child).getWholeText();
        }
        else {
            return null;
        }
    }

    private static class ContentTypes {

        private HashMap<String, String> defaults;

        private HashMap<String, String> overrides;

        public ContentTypes() {
            defaults = new HashMap<String, String>();
            overrides = new HashMap<String, String>();
        }

        public void addDefault(String extension, String contentType) {
            defaults.put(extension, contentType);
        }

        public void addOverride(String partName, String contentType) {
            overrides.put(partName, contentType);
        }

        public String getDefault(String extension) {
            return (String) defaults.get(extension);
        }

        public String getOverride(String partName) {
            return (String) overrides.get(partName);
        }

        public String getType(String partName) {
            String override = getOverride(partName);
            if (override == null) {
                int index = partName.lastIndexOf('.');
                if (index >= 0 && index < partName.length() - 1) {
                    String extension = partName.substring(index + 1);
                    return getDefault(extension);
                }
                else {
                    return null;
                }
            }
            else {
                return override;
            }
        }

        public String toString() {
            return "ContentTypes[default=" + defaults + ",overrides=" + overrides + "]";
        }
    }

    private static class NonCloseableStream extends FilterInputStream {

        public NonCloseableStream(InputStream in) {
            super(in);
        }

        public void close() throws IOException {
        // prevent the wrapper stream from getting closed
        }
    }

    private class TextCollector extends SimpleSAXAdapter {

        private int initialLength;

        private boolean insideTabs = false;

        public void startDocument() throws SAXException {
            initialLength = fullText.length();
        }

        @SuppressWarnings("unchecked")
        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if ("w:t".equals(tagName)) {
                fullText.append(text);
            }
            else if ("t".equals(tagName) || "p:text".equals(tagName) || "a:t".equals(tagName)
                    || "st:t".equals(tagName) || "v".equals(tagName)) {
                fullText.append(text);
                fullText.append(' ');
            }
            else if ("w:tab".equals(tagName) && !insideTabs) {
                fullText.append('\t');
            }
            else if ("w:tabs".equals(tagName)) {
                insideTabs = true;
            }
        }

        public void endTag(String tagName) throws SAXException {
            if ("w:p".equals(tagName)) {
                fullText.append(END_OF_LINE);
            }
            else if ("w:tabs".equals(tagName)) {
                insideTabs = false;
            }
        }

        public void endDocument() throws SAXException {
            if (fullText.length() > initialLength) {
                fullText.append(END_OF_LINE);
            }
        }
    }

    private class AttributeTextCollector extends SimpleSAXAdapter {

        private String fullTextTag;

        public AttributeTextCollector(String fullTextTag) {
            this.fullTextTag = fullTextTag;
        }

        @SuppressWarnings("unchecked")
        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (fullTextTag.equals(tagName)) {
                Object value = atts.get("name");
                if (value instanceof String) {
                    fullText.append(value);
                    fullText.append(END_OF_LINE);
                }
            }
        }
    }
}
