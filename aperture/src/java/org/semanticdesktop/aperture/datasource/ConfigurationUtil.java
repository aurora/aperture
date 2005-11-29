/*
 * Copyright (c) 2005 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.datasource;

import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * ConfigurationUtil provides utility methods for setting and retrieving standard DataSource
 * configuration properties from an RDFContainer.
 */
public class ConfigurationUtil {

    private ConfigurationUtil() {
        // prevent instantiation
    }

    public static void setRootUrl(String url, RDFContainer configuration) {
        configuration.put(Vocabulary.ROOT_URL, url);
    }

    public static String getRootUrl(RDFContainer configuration) {
        return configuration.getString(Vocabulary.ROOT_URL);
    }

    public static void setMaximumDepth(int maximumDepth, RDFContainer configuration) {
        configuration.put(Vocabulary.MAXIMUM_DEPTH, maximumDepth);
    }

    public static Integer getMaximumDepth(RDFContainer configuration) {
        return configuration.getInteger(Vocabulary.MAXIMUM_DEPTH);
    }

    public static void setMaximumByteSize(int maximumSize, RDFContainer configuration) {
        configuration.put(Vocabulary.MAXIMUM_BYTE_SIZE, maximumSize);
    }

    public static Integer getMaximumByteSize(RDFContainer configuration) {
        return configuration.getInteger(Vocabulary.MAXIMUM_BYTE_SIZE);
    }

    public static Boolean getIncludeHiddenResourceS(RDFContainer configuration) {
        return configuration.getBoolean(Vocabulary.INCLUDE_HIDDEN_RESOURCES);
    }

    public static void setIncludeHiddenResources(boolean value, RDFContainer configuration) {
        configuration.put(Vocabulary.INCLUDE_HIDDEN_RESOURCES, value);
    }

}
