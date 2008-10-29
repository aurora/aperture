/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.subcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.accessor.base.DataObjectBase;
import org.semanticdesktop.aperture.datasource.DataSource;

/**
 * A utility class containing some methods useful when working with subcrawlers and subcrawled resources.
 */
public class SubCrawlerUtil {

    private static final Pattern uriSchemePattern = Pattern.compile("\\w\\w+:");
    
    /**
     * <p>
     * Tries to access a DataObject that is hidden in a stream. This method can get the desired object
     * through multiple levels of nesting. E.g. for an uri:
     * </p>
     * <p>
     * "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/#1!/Board+paper.docx"
     * </p>
     * <p>
     * This method will assume that the given stream points at the root data object. i.e.:
     * </p>
     * <p>
     * "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml"
     * </p>
     * <p>
     * Then it will apply a MimeSubCrawler on that stream, to get the first attachment, and afterwards it
     * will apply the ZipSubCrawler on that attachment to get the desired file.
     * </p>
     * 
     * 
     * @param uri the uri of the subcrawled object
     * @param stream the stream pointing at the root data object of the uri
     * @param dataSource the data source that will be returned from the {@link DataObject#getDataSource()} method
     *  of the returned object
     * @param charset a charset (optional)
     * @param mimeType the mime type of the stream (optional)
     * @param containerFactory the factory of RDFContainers 
     * @param registry a SubCrawlerRegistry, from which all the necessary SubCrawlerFactories will be obtained
     * @return a DataObject for the given URI
     * @throws SubCrawlerException 
     * @throws PathNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static DataObject getDataObject(URI uri, InputStream stream, DataSource dataSource,
            Charset charset, String mimeType, RDFContainerFactory containerFactory,
            SubCrawlerRegistry registry) throws SubCrawlerException, PathNotFoundException, IOException {
        if (!isSubcrawledObjectUri(uri)) {
            throw new SubCrawlerException("not a proper subcrawled object uri: " + uri.toString());
        }
        
        List<String[]> stack = new LinkedList<String[]>();
        URI currentUri = new URIImpl(uri.toString());
        while (true) {
            if (!isSubcrawledObjectUri(currentUri)) {
                break;
            }
            String [] current = new String[3];
            current[0] = getSubCrawlerPrefix(currentUri);
            current[1] = getSubCrawledObjectPath(currentUri);
            currentUri = getParentObjectUri(currentUri);
            if (currentUri != null) {
                current[2] = currentUri.toString();
            }
            stack.add(0, current);
        }
        
        boolean bad = false;
        DataObject object = null;
        InputStream currentStream = stream;
        try {
            while (!stack.isEmpty()) {                
                String [] current = stack.remove(0);
                Set set = registry.getByPrefix(current[0]);
                if (set.isEmpty()) {
                    bad = true;
                    throw new SubCrawlerException("Couldn't find subcrawler for the prefix: " + current[0]);
                } else {
                    SubCrawlerFactory factory = (SubCrawlerFactory)set.iterator().next();
                    SubCrawler subCrawler = factory.get();
                    bad = true;
                    DataObject newObject = subCrawler.getDataObject(new URIImpl(current[2]), 
                        current[1], currentStream, dataSource, charset, mimeType, containerFactory);
                    if (!stack.isEmpty() && !(newObject instanceof FileDataObject)) {
                        throw new SubCrawlerException("an intermediate DataObject has no stream: " + currentUri);
                    } else {
                        currentStream = ((FileDataObject)newObject).getContent();
                    }
                    ((DataObjectBase)newObject).setWrappedDataObject(object);
                    object = newObject;
                    bad = false;
                }
            }
        } finally {
            if (bad && object != null) {
                object.dispose();
            }
        }
        return object;
    }   
    
    
    /**
     * <p>
     * Returns the URI of the root object, from the URI of a subcrawled object. E.g. for
     * </p>
     * <p>
     * "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx"
     * </p>
     * <p>
     * This method will return
     * </p>
     * <p>
     * "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml"
     * </p>
     * <p>
     * ... that is the portion of the uri between the last 'scheme' part (regex: '\w{2,}:') and the first
     * exclamation mark. The regex is constructed in a way to allow for windows drive names (single letter and
     * a semicolon), an uri scheme cannot have a single letter.
     * </p>
     * 
     * @param subCrawledObjectUri
     * @return the URI of the root object from which the sub crawled object has been obtained (possibily) by
     *         many nested subcrawlers
     */
    public static URI getRootObjectUri(URI subCrawledObjectUri) {
        String string = subCrawledObjectUri.toString();
        int startIndex = 0;
        int endIndex = string.length();
        Matcher matcher = uriSchemePattern.matcher(string);
        int matchStart = -1;
        while (matcher.find()) {
            matchStart = matcher.start();
        }
        if (matchStart > 0) {
            startIndex = matchStart;
        }
        endIndex = string.indexOf("!");
        if (endIndex < 0) {
            endIndex = string.length();
        }
        return new URIImpl(string.substring(startIndex, endIndex));
    }

    /**
     * <p>
     * Returns the URI of the parent data object, from the URI of a subcrawled object. E.g. for
     * </p>
     * <p>
     * "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx"
     * </p>
     * <p>
     * This method will return
     * </p>
     * <p>
     * "mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1"
     * </p>
     * <p>
     * If this object already denotes a root data object (i.e. not a subcrawled data object) this method will
     * return null. For example given a uri of a normal file:
     * </p>
     * <p>
     * "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml"
     * </p>
     * <p>
     * This method will return null.
     * </p>
     * 
     * @param subCrawledObjectUri
     * @return URI of the parent data object from which the sub crawled object has been obtained
     */
    public static URI getParentObjectUri(URI subCrawledObjectUri) {
        String string = subCrawledObjectUri.toString();
        int startIndex = 0;
        int endIndex = string.length();
        Matcher matcher = uriSchemePattern.matcher(string);
        if (matcher.find() && matcher.find()) {
            startIndex = matcher.start();
        }
        endIndex = string.lastIndexOf("!");
        if (endIndex > 0 && startIndex > 0) {
            return new URIImpl(string.substring(startIndex, endIndex));
        }
        else {
            return null;
        }
    }

    /**
     * <p>
     * Returns the subcrawler prefix from the URI of a subcrawled object. This means the immediate 'topmost'
     * data object. E.g. for
     * </p>
     * <p>
     * "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx"
     * </p>
     * <p>
     * This method will return "zip"
     * </p>
     * <p>
     * If this object already denotes a root data object (i.e. not a subcrawled data object) this method will
     * return null. For example given a uri of a normal file:
     * </p>
     * <p>
     * "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml"
     * </p>
     * <p>
     * This method will return null.
     * </p>
     * 
     * @param subCrawledObjectUri
     * @return the subcrawler prefix from the URI of a subcrawled Object
     */
    public static String getSubCrawlerPrefix(URI subCrawledObjectUri) {
        String string = subCrawledObjectUri.toString();
        int startIndex = 0;
        int endIndex = string.length();
        Matcher matcher = uriSchemePattern.matcher(string);
        if (matcher.find() && matcher.find()) {
            startIndex = matcher.start();
        }
        endIndex = string.lastIndexOf("!");
        if (endIndex > 0 && startIndex > 0) {
            return string.substring(0, startIndex - 1);
        }
        else {
            return null;
        }
    }

    /**
     * <p>
     * Returns the the path of the subcrawled object within the parent object. This means the immediate
     * 'topmost' data object. E.g. for
     * </p>
     * <p>
     * "zip:mime:file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml!/86b313dc282850fef1762fb400171750%2540amrapali.com#1!/Board+paper.docx"
     * </p>
     * <p>
     * This method will return "/Board+paper.docx"
     * </p>
     * <p>
     * If this object already denotes a root data object (i.e. not a subcrawled data object) this method will
     * return null. For example given a uri of a normal file:
     * </p>
     * <p>
     * "file:/C:/Users/Chris/Desktop/docx%20problem/Useful%20documents1.eml"
     * </p>
     * <p>
     * This method will return null.
     * </p>
     * 
     * @param subCrawledObjectUri
     * @return the path of the subcrawled object within the parent object
     */
    public static String getSubCrawledObjectPath(URI subCrawledObjectUri) {
        String string = subCrawledObjectUri.toString();
        int startIndex = 0;
        int endIndex = string.length();
        Matcher matcher = uriSchemePattern.matcher(string);
        if (matcher.find() && matcher.find()) {
            startIndex = matcher.start();
        }
        endIndex = string.lastIndexOf("!");
        if (endIndex > 0 && startIndex > 0) {
            return string.substring(endIndex + 1).replace('+', ' ');
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns true if the given uri is an URI of the subcrawled object, false otherwise. A proper URI for a
     * subcrawled object consists of a proper URI of the root object with uri prefixes and subcrawled object
     * paths at the end. The number of prefixes should be equal to the number of subcrawled object paths.
     * 
     * @param subCrawledObjectUri
     * @return true if the given URI is a valid URI of a subcrawled resource, false otherwise
     */
    public static boolean isSubcrawledObjectUri(URI subCrawledObjectUri) {
        String string = subCrawledObjectUri.toString();
        int prefixesCount = 0;
        int exclamationsCount = 0;
        Matcher matcher = uriSchemePattern.matcher(string);
        while (matcher.find()) {
            prefixesCount++;
        }
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '!') {
                exclamationsCount++;
            }
        }
        
        if (prefixesCount > 1 && prefixesCount - 1 == exclamationsCount) {
            return true;
        } else {
            return false;
        }
    }         
}
