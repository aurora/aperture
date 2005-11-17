package org.semanticdesktop.aperture.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides helper methods to handle RDF statements.
 */
public class RDFUtil {

    private static DateFormat dateTimeFormat = null;

    /**
     * format the given date in a good dateTime format: ISO 8601, using the T seperator and the - and :
     * seperators accordingly. example: 2003-01-22T17:00:00
     * 
     * @param date a date
     * @return a formatted string.
     */
    public static String dateTime2String(Date date) {
        return getDateTimeFormat().format(date);
    }

    public static Date string2DateTime(String string) throws ParseException {
        return getDateTimeFormat().parse(string);
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
