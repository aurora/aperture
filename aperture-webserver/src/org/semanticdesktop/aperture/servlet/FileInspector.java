/*
 * Copyright (c) 2005 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ontoware.rdf2go.model.Syntax;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerFactoryImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * Servlet implementation class for Servlet: FileInspector
 * author: Benjamin Horak
 */
public class FileInspector extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    /*
         * (non-Java-doc)
         * 
         * @see javax.servlet.http.HttpServlet#HttpServlet()
         */
    public FileInspector() {
	super();
    }

    public String getMimeType(String url) throws Exception {
	HttpClient client = new HttpClient();
	GetMethod get = new GetMethod(url);
	get.setFollowRedirects(true);
	int httpResult = client.executeMethod(get);
	if (httpResult == 200) {
	    return getMimeType(get.getResponseBodyAsStream(), url);
	}
	throw new HttpException("Invalid result: HTTP code is " + httpResult);
    }

    public List<String> extractHyperlinks(String url) throws Exception {
	HttpClient client = new HttpClient();
	GetMethod get = new GetMethod(url);
	get.setFollowRedirects(true);
	int httpResult = client.executeMethod(get);
	if (httpResult == 200) {
	    return extractHyperlinks(get.getResponseBodyAsStream());
	}
	throw new HttpException("Invalid result: HTTP code is " + httpResult);
    }

    public String inspectFile(String url, String mimeType, List<String> links) throws Exception {
	HttpClient client = new HttpClient();
	GetMethod get = new GetMethod(url);
	get.setFollowRedirects(true);
	int httpResult = client.executeMethod(get);
	if (httpResult == 200) {
	    return inspectFile(get.getResponseBodyAsStream(), url, mimeType, links);
	}
	throw new HttpException("Invalid result: HTTP code is " + httpResult);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String mimeType = null;
	String urls = request.getParameter("url");
	List<String> links = new ArrayList<String>();
	String rdf;
	try {
	    if (urls != null) {
		urls = urls.replace(" ", "%20");
		mimeType = getMimeType(urls);

		if (mimeType.equals("text/html")) {
		    links = extractHyperlinks(urls);
		}

		rdf = inspectFile(urls, mimeType, links);
		response.getOutputStream().print(rdf);
		return;
	    }
	} catch (Exception e) {
	    throw new ServletException(e);
	}
    }

    /*
         * (non-Java-doc)
         * 
         * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest
         *      request, HttpServletResponse response)
         */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	    IOException {
	String rdf;
	List<String> links = new ArrayList<String>();

	try {
	    String mimeType = null;
	    String urls = request.getParameter("url");
	    if (urls != null) {
		urls = urls.replace(" ", "%20");
		mimeType = getMimeType(urls);

		if (mimeType != null && mimeType.equals("text/html")) {
		    links = extractHyperlinks(urls);
		}

		rdf = inspectFile(urls, mimeType, links);
		response.getOutputStream().print(rdf);
		return;
		// } else {
		// throw new ServletException(get.getResponseBodyAsString());
		// }
	    }

	    // Create a factory for disk-based file items
	    FileItemFactory factory = new DiskFileItemFactory();

	    // Create a new file upload handler
	    ServletFileUpload upload = new ServletFileUpload(factory);

	    // Parse the request
	    List<FileItem> /* FileItem */items = upload.parseRequest(request);

	    for (FileItem fileItem : items) {
		File uploadedFile = File.createTempFile(fileItem.getName(), ".tmp");
		fileItem.write(uploadedFile);
		mimeType = getMimeType(new FileInputStream(uploadedFile), uploadedFile.toURI().toString());
		if (mimeType != null && mimeType.equals("text/html")) {
		    links = extractHyperlinks(new FileInputStream(uploadedFile));
		}
		rdf = inspectFile(new FileInputStream(uploadedFile), uploadedFile.toURI().toString(), mimeType, links);
		uploadedFile.delete();
		response.getOutputStream().print(rdf);
		return;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ServletException(e);
	}
    }

    private String getMimeType(InputStream stream, String path) throws IOException {
	MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();
	int minimumArrayLength = identifier.getMinArrayLength();
	int bufferSize = Math.max(minimumArrayLength, 8192);
	BufferedInputStream buffer = new BufferedInputStream(stream, bufferSize);
	buffer.mark(minimumArrayLength + 10); // add some for safety
	byte[] bytes = IOUtil.readBytes(buffer, minimumArrayLength);

	// let the MimeTypeIdentifier determine the MIME type of this file
	String mimeType = identifier.identify(bytes, path, null);

	if(mimeType == null) {
	    throw new IOException("Could not identify mimetype of: " + path +". Therefore document normalization is not possible.");
	}
	
	return mimeType;
    }

    private List<String> extractHyperlinks(InputStream stream) throws Exception {
	BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

	ArrayList<String> list = new ArrayList<String>();

	boolean tag = false;
	boolean link = false;
	boolean label = false;
	boolean a = true;

	StringBuffer buffer = new StringBuffer();

	for (String line = ""; line != null; line = reader.readLine()) {
	    char[] cline = line.toCharArray();
	    for (char c : cline) {
		if (!label && !link && c == '<') {
		    tag = true;
		} else if (!label && !link && tag && c == 'a') {
		    a = true;
		} else if (!label && !link && tag && a && c == ' ') {
		    link = true;
		} else if (!label && link && c == '>') {
		    label = true;
		} else if (label && c != '<') {
		    if (c == ' ' || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
			buffer.append(c);
		    }
		} else if (label && c == '<') {
		    if (buffer.length() > 0) {
			String s = buffer.toString().trim().toLowerCase();
			if (s.length() > 0 && s.length() <= 30) {
			    if (!s.startsWith("http") && !s.startsWith("www") && !s.startsWith("ftp")
				    && !s.startsWith("mailto")) {
				list.add(s);
			    }
			}
		    }
		    buffer.setLength(0);
		    a = false;
		    tag = false;
		    link = false;
		    label = false;
		}

		if (c == '>') {
		    tag = false;
		    a = false;
		}
	    }
	}

	return list;

    }

    private String inspectFile(InputStream stream, String path, String mimeType, List<String> hyperlinks)
	    throws Exception {

	StringWriter stringWriter = new StringWriter();
	// create a MimeTypeIdentifier

	// create an ExtractorRegistry containing all Extractors
	ExtractorRegistry extractorRegistry = new DefaultExtractorRegistry();

	BufferedInputStream buffer = new BufferedInputStream(stream);

	// skip the extraction phase when the MIME type could not be determined
	if (mimeType == null) {
	    System.err.println("WARNING: MIME type could not be established.");
	} else {

	    // create the RDFContainer that will hold the RDF model
	    RDFContainerFactoryImpl containerFactory = new RDFContainerFactoryImpl();
	    RDFContainer container = containerFactory.newInstance(path);

	    // determine and apply an Extractor that can handle this MIME
	    Set factories = extractorRegistry.get(mimeType);
	    if (factories != null && !factories.isEmpty()) {
		// just fetch the first available Extractor
		ExtractorFactory factory = (ExtractorFactory) factories.iterator().next();
		Extractor extractor = factory.get();

		// apply the extractor on the specified file
		extractor.extract(container.getDescribedUri(), buffer, null, mimeType, container);
	    }
	    // add the MIME type as an additional statement to the RDF model
	    container.add(DATA.mimeType, mimeType);
	    for (String link : hyperlinks) {
		container.add(DATA.keyword, link);
	    }
	    // report the output to System.out
	    container.getModel().writeTo(stringWriter, Syntax.RdfXml);
	    container.dispose();
	}
	buffer.close();

	String out = stringWriter.toString();

	if (out == null || out.length() == 0) {
	    throw new Exception("Invalid content for: " + path);
	}

	out = out.replace("&#xD;", "");
	String patternStr = "\\s+";
	String replaceStr = " ";
	Pattern pattern = Pattern.compile(patternStr);
	Matcher matcher = pattern.matcher(out);
	out = matcher.replaceAll(replaceStr);

	patternStr = "[^\\w\\p{Punct}\\s‰¸ˆƒ‹÷ﬂ]";
	replaceStr = "";
	pattern = Pattern.compile(patternStr);
	matcher = pattern.matcher(out);
	out = matcher.replaceAll(replaceStr);
	return out;
    }
}