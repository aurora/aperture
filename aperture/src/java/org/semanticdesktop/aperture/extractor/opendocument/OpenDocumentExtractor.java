/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.opendocument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Extracts full-text and metadata from OpenDocument files and is backwards compatible with older OpenOffice
 * (1.x) and StarOffice (6.x and 7.x) documents.
 */
public class OpenDocumentExtractor implements Extractor {

	// used to append to extracted text, to make it more readable
	private static final String END_OF_LINE = System.getProperty("line.separator", "\n");
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	// used to fool the parser, when it tries to load the system dtd.
	// seems to work better than tricks such as providing a dummy EntityResolver, which is probably parser
	// implementation-dependent
	// (see e.g. http://www.jroller.com/comments/santhosh/Weblog/putoff_dtd_parsing_html)
	private static final String SYSTEM_ID =
		ResourceUtil.getURL("org/semanticdesktop/aperture/extractor/opendocument/office.dtd",OpenDocumentExtractor.class).toString();

	/**
	 * @see {@link Extractor#extract(URI, InputStream, Charset, String, RDFContainer)}
	 */
	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		byte[] contentBytes = null;
		byte[] metadataBytes = null;

		// fetch the byte arrays from the zip file that contain the document content and metadata
		try {
			ZipInputStream zipStream = new ZipInputStream(stream);
			ZipEntry entry = null;
			while ((entry = zipStream.getNextEntry()) != null) {
				String entryName = entry.getName();
				if ("content.xml".equals(entryName)) {
					contentBytes = IOUtil.readBytes(zipStream);
				}
				else if ("meta.xml".equals(entryName)) {
					metadataBytes = IOUtil.readBytes(zipStream);
				}

				zipStream.closeEntry();
			}
			zipStream.close();
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
        
        // determine the type of the document
        // TODO there is no document type determination
        {
            result.add(RDF.type,NFO.Document);
        }

		// extract the document text
		if (contentBytes != null) {
			extractFullText(contentBytes, result);
		}

		// extract the metadata
		if (metadataBytes != null) {
			extractMetadata(metadataBytes, result);
		}
	}

	private void extractFullText(byte[] bytes, RDFContainer result) throws ExtractorException {
		// create a SimpleSaxParser
		SimpleSAXParser parser = null;
		try {
			parser = new SimpleSAXParser();
		}
		catch (Exception e) {
			// this is an internal error rather than an extraction problem, hence the RuntimeException
			throw new RuntimeException("unable to instantiate SAXParser", e);
		}

		// create a listener that will interpret the document events
		ContentExtractor contentExtractor = new ContentExtractor();
		parser.setListener(contentExtractor);

		// parse the byte array
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		try {
			parser.parse(stream, SYSTEM_ID);
		}
		catch (SAXException e) {
			throw new ExtractorException(e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}

		// put the extracted full-text in the RDF container
		String contents = contentExtractor.getContents();
		if (contents != null && !contents.equals("")) {
			result.add(NIE.plainTextContent, contents);
		}
	}

	/**
	 * Reads the metadata of the document from the specified byte array
	 */
	private void extractMetadata(byte[] bytes, RDFContainer result) throws ExtractorException {
		// create a DocumentBuilder instance
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
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

		// parse the XML using the DocumentBuilder
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		Document doc;
		try {
			doc = docBuilder.parse(stream, SYSTEM_ID);
		}
		catch (SAXException e) {
			throw new ExtractorException(e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}

		// iterate over the nodes containing the metadata
		Element rootelement = doc.getDocumentElement();
		Node metaNode = rootelement.getFirstChild();
		NodeList metaChildren = metaNode.getChildNodes();
		int nrChildren = metaChildren.getLength();

		for (int i = 0; i < nrChildren; i++) {
			Node metaChild = metaChildren.item(i);
			addOasisMetadataPropertyToRdfContainer(metaChild, result);
			mapToApertureProperty(metaChild,result);
		}
	}
	
	private void addOasisMetadataPropertyToRdfContainer(Node node, RDFContainer result) {
	    String nameSpace = node.getNamespaceURI();
        if (nameSpace != null) {
            if (!nameSpace.endsWith("/")) {
                nameSpace += "/";
            }

            try {
                String uriString = nameSpace + node.getLocalName();
                String text = getText(node);
                URI predicate = result.getValueFactory().createURI(uriString);
                
                if (text != null) {
                    result.add(predicate, text);
                } 
            }
            catch (ModelException e) {
                logger.error("ModelException while adding statement, ignoring", e);
            }
        }

	}
	
   private String getText(Node node) {
        if (node instanceof Attr) {
            return ((Attr)node).getValue();
        }
        
        Node child = node.getFirstChild();
        if (child instanceof Text) {
            return ((Text) child).getWholeText();
        }
        else {
            return null;
        }
    }
	
	private void mapToApertureProperty(Node metaChild, RDFContainer result) {
	    String name = metaChild.getNodeName();

        // determine which metadata property we're dealing with
        if ("dc:creator".equals(name)) {
            addContactStatement(NCO.creator, metaChild.getFirstChild().getNodeValue(), result);
        }
        else if ("meta:initial-creator".equals(name)) {
            addContactStatement(NCO.creator, metaChild.getFirstChild().getNodeValue(), result);
        }
        else if ("dc:title".equals(name)) {
            addStatement(NIE.title, metaChild.getFirstChild(), result);
        }
        else if ("dc:description".equals(name)) {
            addStatement(NIE.description, metaChild.getFirstChild(), result);
        }
        else if ("dc:subject".equals(name)) {
            addStatement(NIE.subject, metaChild.getFirstChild(), result);
        }
        else if ("dc:date".equals(name)) {
            addDateStatement(NIE.informationElementDate, metaChild.getFirstChild(), result);
        }
        else if ("meta:creation-date".equals(name)) {
            addDateStatement(NIE.contentCreated, metaChild.getFirstChild(), result);
        }
        // TODO get back to it after introducing nie:printdate
        else if ("meta:print-date".equals(name)) {
            addDateStatement(NIE.informationElementDate, metaChild.getFirstChild(), result);
            //addDateStatement(DATA.printDate, metaChild.getFirstChild(), result);
        }
        else if ("dc:language".equals(name)) {
            addStatement(NIE.language, metaChild.getFirstChild(), result);
        }
        else if ("meta:generator".equals(name)) {
            addStatement(NIE.generator, metaChild.getFirstChild(), result);
        }
        else if ("meta:user-defined".equals(name)) {
            // user-defined properties are NOT supported
            // TODO clarify this issue with the Nepomuk Consortium
        }
        else if ("meta:keywords".equals(name)) {
            // handles OpenOffice 1.x keywords
            NodeList keywordNodes = metaChild.getChildNodes();
            int nrKeywordNodes = keywordNodes.getLength();
            for (int j = 0; j < nrKeywordNodes; j++) {
                Node keywordNode = keywordNodes.item(j);
                if ("meta:keyword".equals(keywordNode.getNodeName())) {
                    addStatement(NIE.keyword, keywordNode.getFirstChild(), result);
                }
            }
        }
        else if ("meta:keyword".equals(name)) {
            // handles OpenOffice 2.x, i.e. OpenDocument
            addStatement(NIE.keyword, metaChild.getFirstChild(), result);
        }
        else if ("meta:document-statistic".equals(name)) {
            NamedNodeMap attributes = metaChild.getAttributes();
            if (attributes != null) {
                for (int i = 0; i<attributes.getLength(); i++) {
                    Node node = attributes.item(i);
                    addOasisMetadataPropertyToRdfContainer(node, result);
                    if (node instanceof Attr) {
                        mapStatisticsAttributeToApertureProperty((Attr)node, result);
                    }
                }
            }
        }
	}

	/**
	 * Many kinds of document statics fields are stored in the document metadata.
	 * @param statisticsAttribute
	 * @param result
	 */
    private void mapStatisticsAttributeToApertureProperty(Attr statisticsAttribute, 
           RDFContainer result) {
        String name = statisticsAttribute.getNodeName();

        // determine which metadata property we're dealing with
        if ("meta:page-count".equals(name)) {
            String pageNodeValue = statisticsAttribute.getValue();
            if (pageNodeValue != null) {
                try {
                    int pageCount = Integer.parseInt(pageNodeValue);
                    result.add(RDF.type,NFO.PaginatedTextDocument);
                    result.add(NFO.pageCount, pageCount);
                }
                catch (NumberFormatException e) {
                    // ignore
                }
             }
         }
     }
	
	private void addStatement(URI uri, Node node, RDFContainer container) {
		if (node != null) {
			addStatement(uri, node.getNodeValue(), container);
		}
	}

	private void addStatement(URI uri, String value, RDFContainer container) {
		if (value != null) {
			container.add(uri, value);
		}
	}

	private void addDateStatement(URI uri, Node node, RDFContainer container) {
		if (node != null) {
			String value = node.getNodeValue();
			if (value != null) {
				try {
					Date date = DateUtil.string2DateTime(value);
					container.add(uri, date);
				}
				catch (ParseException e) {
					// ignore
				}
			}
		}
	}
    
    private void addContactStatement(URI uri, String fullname, RDFContainer container) {
        Model model = container.getModel();
        Resource contactResource = UriUtil.generateRandomResource(model);
        model.addStatement(contactResource,RDF.type,NCO.Contact);
        model.addStatement(contactResource,NCO.fullname,fullname);
        container.add(uri,contactResource);
    }

	/**
	 * Inner class for extracting full-text from the content.xml part of an OpenDocument/OpenOffice/StarOffice
	 * document.
	 */
	private static class ContentExtractor extends SimpleSAXAdapter {

		private static final String OFFICE_BODY = "office:body";

		private static final String MATH_MATH = "math:math";

		private static final String TEXT_P = "text:p";

		private static final String TEXT_H = "text:h";

		private StringBuilder contents = new StringBuilder(4096);

		private boolean insideBody = false;

		public String getContents() {
			return contents.toString();
		}

		public void startTag(String tagName, Map atts, String text) {
			if (OFFICE_BODY.equals(tagName) || MATH_MATH.equals(tagName)) {
				insideBody = true;
			}
			else if (insideBody && text.length() > 0) {
				if (TEXT_H.equals(tagName) && contents.length() > 0) {
					contents.append(END_OF_LINE);
					contents.append(END_OF_LINE);
				}

				contents.append(text);

				if (TEXT_P.equals(tagName)) {
					contents.append(END_OF_LINE);
				}
				else if (TEXT_H.equals(tagName)) {
					contents.append(END_OF_LINE);
					contents.append(END_OF_LINE);
				}
				else {
					contents.append(' ');
				}
			}
		}

		public void endTag(String tagName) {
			if (OFFICE_BODY.equals(tagName) || MATH_MATH.equals(tagName)) {
				insideBody = false;
			}
		}
	}
}
