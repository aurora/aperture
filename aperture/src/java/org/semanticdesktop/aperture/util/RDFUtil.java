package org.semanticdesktop.aperture.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * provides helper methods to handle RDF
 * 
 * 
 * @author Sauermann
 * $Id$
 */
public class RDFUtil {
    
    private static DateFormat dateTimeFormat = null;
    /**
     * format the given date in a good dateTime format:
     * ISO 8601, using the T seperator and the - and : seperators accordingly.
     * example: 2003-01-22T17:00:00
     * @param date a date
     * @return a formatted string.
     */
    public static String dateTime2String(Date date) {
        return getDateTimeFormat().format(date);        
    }
    /**
     * the datetimeformat that conforms to ISO 8601, which is used in XSD-datetime.
     */
    public static DateFormat getDateTimeFormat() {
        if (dateTimeFormat == null) {
            dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        }
        return dateTimeFormat;
    }
}


/*
 * $Log$
 * Revision 1.1  2005/10/26 14:08:59  leo_sauermann
 * added the sesame-model and began with RDFContainer
 *
 */