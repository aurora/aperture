/*
 * Copyright (c) 2005 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.mime.identifier.magic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Determines the MIME type of a binary resource based on magic number-based heuristics.
 */
public class MagicMimeTypeIdentifier implements MimeTypeIdentifier {

    private static final Logger LOGGER = Logger.getLogger(MagicMimeTypeIdentifier.class.getName());
    
    private static final String MIME_TYPES_RESOURCE = "org/semanticdesktop/aperture/mime/identifier/magic/mimetypes.xml";
    
    private ArrayList mimeTypeDescriptions;

    private int minArrayLength;

    public MagicMimeTypeIdentifier() {
        readDescriptions();
        setRequiringTypes();
        determineMinArrayLength();
    }

    private void readDescriptions() {
        // get the mimetypes.xml resource as an input stream
        InputStream stream = ResourceUtil.getInputStream(MIME_TYPES_RESOURCE);
        
        // setup a document builder
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setValidating(false);
        docBuilderFactory.setExpandEntityReferences(true);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            // this is an internal error rather than an extraction problem, hence the RuntimeException
            throw new RuntimeException("unable to instantiate DocumentBuilder", e);
        }
        
        // parse the document
        Document document;
        try {
            document = docBuilder.parse(stream);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        // loop over all description elements
        Element documentRoot = document.getDocumentElement();
        NodeList descriptionElements = documentRoot.getElementsByTagName("description");
        
        int nrDescriptions = descriptionElements.getLength();
        mimeTypeDescriptions = new ArrayList(nrDescriptions);
        
        for (int i = 0; i < nrDescriptions; i++) {
            Element descriptionElement = (Element) descriptionElements.item(i);
            MimeTypeDescription description = createMimeTypeDescription(descriptionElement);
            if (description != null) {
                mimeTypeDescriptions.add(description);
            }
        }
    }

    private MimeTypeDescription createMimeTypeDescription(Element descriptionElement) {
        // initialize variables
        String mimeType = null;
        ArrayList extensions = new ArrayList();
        ArrayList conditions = new ArrayList();
        
        // extract info from the specified element
        NodeList childNodes = descriptionElement.getChildNodes();
        int nrNodes = childNodes.getLength();
        for (int i = 0; i < nrNodes; i++) {
            // fetch the next element node
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            String tagName = childNode.getNodeName();
            
            // handle mimeType element
            if ("mimeType".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                if (valueNode != null) {
                    mimeType = valueNode.getNodeValue().trim();
                }
            }
            
            // handle extensions element
            else if ("extensions".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                if (valueNode != null) {
                    String extensionsString = valueNode.getNodeValue().trim();
                    StringTokenizer tokenizer = new StringTokenizer(extensionsString, ", ", false);
                    while (tokenizer.hasMoreTokens()) {
                        extensions.add(tokenizer.nextToken().toLowerCase());
                    }
                }
            }
            
            // handle condition elements
            else if ("condition".equals(tagName)) {
                Condition condition = createCondition((Element) childNode);
                if (condition != null) {
                    conditions.add(condition);
                }
            }
        }
        
        // create the resulting MimeTypeDescription
        if (mimeType == null) {
            return null;
        }
        else {
            return new MimeTypeDescription(mimeType, extensions, conditions);
        }
    }

    private Condition createCondition(Element conditionElement) {
        // initialize variables
        byte[] magicBytes = null;
        int offset = 0;
        int minimumLength = 0;
        String parentType = null;

        // extract info from the specified element
        NodeList childNodes = conditionElement.getChildNodes();
        int nrNodes = childNodes.getLength();
        for (int i = 0; i < nrNodes; i++) {
            // fetch the next element node
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            String tagName = childNode.getNodeName();
            
            // handle byteSequence element
            if ("byteSequence".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                Node encodingNode = childNode.getAttributes().getNamedItem("encoding");
                
                if (valueNode != null && encodingNode != null) {
                    String byteSequenceString = valueNode.getNodeValue(); // note: NO trim() !
                    String encoding = encodingNode.getNodeValue().trim();
                    
                    if ("string".equals(encoding)) {
                        // get the string as bytes, using UTF-8 decoding
                        try {
                            magicBytes = byteSequenceString.getBytes("UTF-8");
                        }
                        catch (UnsupportedEncodingException e) {
                            // clearly an internal error, Java should always support UTF-8
                            throw new RuntimeException(e);
                        }
                    }
                    else if ("hex".equals(encoding)) {
                        // remove all leading and trailing white space, as well as all spaces inbetween
                        byteSequenceString = byteSequenceString.trim();
                        byteSequenceString = byteSequenceString.replaceAll(" ", "");
                        
                        // convert the hex encoding to a byte array
                        byteSequenceString = byteSequenceString.toLowerCase();
                        
                        int nrChars = byteSequenceString.length();
                        byte[] bytes = new byte[nrChars / 2];
                        int cumulative = 0;
                        
                        for (int j = 0; j < nrChars; j++) {
                            // fetch the next character
                            char c = byteSequenceString.charAt(j);
                            
                            // convert this hexadecimal char to its int representation
                            int decimalValue = 0;
                            if (c >= '0' && c <= '9') {
                                decimalValue = c - '0';
                            }
                            else if (c >= 'a' && c <= 'f') {
                                decimalValue = c - 'a' + 10;
                            }
                            else {
                                LOGGER.warning("illegal hexadecimal char: " + c);
                                break;
                            }
                            
                            // take the value of two consecutive hexadecimal chars together to form a byte
                            if (j % 2 == 0) {
                                cumulative = 16 * decimalValue;
                            }
                            else {
                                cumulative += decimalValue;
                                bytes[j / 2] = (byte) cumulative;
                            }
                        }
                        
                        // now that we know that the complete hex string is valid, set the magic bytes
                        magicBytes = bytes;
                    }
                    else {
                        LOGGER.warning("unknown byte sequence encoding: " + encoding);
                    }
                }
            }
            
            // handle offset element
            else if ("offset".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                if (valueNode != null) {
                    String offsetString = valueNode.getNodeValue().trim();
                    try {
                        offset = Integer.parseInt(offsetString);
                    }
                    catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "unable to parse offset: " + offsetString, e);
                    }
                }                
            }
            
            // handle minimumLength element
            else if ("minimumLength".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                if (valueNode != null) {
                    String lengthString = valueNode.getNodeValue().trim();
                    try {
                        minimumLength = Integer.parseInt(lengthString);
                    }
                    catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "unable to parse minimumLength: " + lengthString, e);
                    }
                }
            }
            
            // handle parentType element
            else if ("parentType".equals(tagName)) {
                Node valueNode = childNode.getFirstChild();
                if (valueNode != null) {
                    parentType = valueNode.getNodeValue().trim();
                }
            }
        }
        
        // create the resulting Condition
        return new Condition(magicBytes, offset, minimumLength, parentType);
    }

    private void setRequiringTypes() {
        // create a mapping from MIME type to MimeTypeDescription
        HashMap descriptionMap = new HashMap();
        
        int nrDescriptions = mimeTypeDescriptions.size();
        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) mimeTypeDescriptions.get(i);
            descriptionMap.put(description.getMimeType(), description);
        }
        
        // loop over all MimeTypeDescriptions
        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) mimeTypeDescriptions.get(i);
            
            // loop over all its Conditions
            ArrayList conditions = description.getConditions();
            int nrConditions = conditions.size();
            for (int j = 0; j < nrConditions; j++) {
                Condition condition = (Condition) conditions.get(j);
                
                // if it has a parent type, register the description on the parent type's description
                String parentType = condition.getParentType();
                if (parentType != null) {
                    MimeTypeDescription parentDescription = (MimeTypeDescription) descriptionMap.get(parentType);

                    if (parentDescription == null) {
                        LOGGER.warning("unable to retrieve parent type: " + parentType);
                    }
                    else {
                        parentDescription.addRequiringType(description);
                    }
                }
            }
        }
    }
    
    private void determineMinArrayLength() {
        minArrayLength = 0;
        
        // loop over all MimeTypeDescriptions
        int nrDescriptions = mimeTypeDescriptions.size();
        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) mimeTypeDescriptions.get(i);
            
            // loop over all its Conditions
            ArrayList conditions = description.getConditions();
            int nrConditions = conditions.size();
            for (int j = 0; j < nrConditions; j++) {
                Condition condition = (Condition) conditions.get(j);
                minArrayLength = Math.max(minArrayLength, condition.getMinimumLength());
            }
        }
    }
    
    protected ArrayList getMimeTypeDescriptions() {
        return mimeTypeDescriptions;
    }

    public String identify(byte[] firstBytes, String fileName, URI uri) {
        // determine a file name extension that we can use as a fallback if type detection based on
        // content cannot be performed or is incomplete (most notably the MS Office file types)
        String extension = fileName;
        
        if (extension == null && uri != null) {
            extension = uri.getLocalName();
        }
        
        if (extension != null) {
            int lastDotIndex = extension.lastIndexOf('.');

            if (lastDotIndex > 0 && lastDotIndex < extension.length() - 1) {
                extension = extension.substring(lastDotIndex + 1);
            }
            
            extension = extension.toLowerCase();
        }

        // now traverse the MimeTypeDescription tree to find a matching MIME type
        return identify(firstBytes, extension, mimeTypeDescriptions);
    }

    private String identify(byte[] firstBytes, String extension, ArrayList descriptions) {
        // loop over the specified list of descriptions
        int nrDescriptions = descriptions.size();
        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) descriptions.get(i);
            
            // see if this description has a matching condition
            if (description.hasMatchingCondition(firstBytes)) {
                // we found at least one matching mime type.
                // see if it is overrules by any of the requiring mime type descriptions
                ArrayList requiringTypes = description.getRequiringTypes();
                String overrulingResult = identify(firstBytes, extension, requiringTypes);
                return overrulingResult == null ? description.getMimeType() : overrulingResult;
            }
        }

        // no match based on magic number could be found, now try on file extension
        for (int i = 0; i < nrDescriptions; i++) {
            MimeTypeDescription description = (MimeTypeDescription) descriptions.get(i);
            if (description.containsExtension(extension)) {
                return description.getMimeType();
            }
        }
        
        // we couldn't find any matching mime types
        return null;
    }

    public int getMinArrayLength() {
        return minArrayLength;
    }
}
