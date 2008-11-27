/*
 * Copyright (c) 2006 - 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.crawler.ical;

/** This class contains the constants used for ical datatypes */
public class IcalDataType {
    /** The CAL-ADDRESS constant */
    public static final String CAL_ADDRESS = "CAL-ADDRESS";
    /** The DATE constant */
    public static final String DATE = "DATE";
    /** The DATE-TIME constant */
    public static final String DATE_TIME = "DATE-TIME";
    /** The DURATION constant */
    public static final String DURATION = "DURATION";
    /** The INTEGER constant */
    public static final String INTEGER = "INTEGER";
    /** The TEXT constant */
    public static final String TEXT = "TEXT";
    /** The PERIOD constant */
    public static final String PERIOD = "PERIOD";
    /** The URI constant */
    public static final String URI = "URI";
    
    /**
     * A special constant. It is used for values that should be expressed
     * with an instance of NcalDateTime class even if there is no TZID
     * parameter.
     */
    public static final String NCAL_DATE_TIME = "NCAL-DATE-TIME";
}

