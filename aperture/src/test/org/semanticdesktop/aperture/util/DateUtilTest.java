/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-Style license
 */
package org.semanticdesktop.aperture.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.semanticdesktop.aperture.ApertureTestBase;

/**
 * Tests the @link {@link DateUtil} class.
 */
public class DateUtilTest extends ApertureTestBase {
    
    /**
     * Tests if a java.util.Date is correctly converted to a string.
     */
    public void testDateTimeToString() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        String string = DateUtil.dateTime2String(date);
        assertTrue(string.contains("T"));
        String offset = getCurrentLocaleTimezoneOffset();
        assertTrue(string.endsWith(offset));
    }
    
    /**
     * Tests the string to UTC string conversion.
     * @throws ParseException
     */
    public void testStringToUTCString() throws ParseException {        
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T15:34:23+01:00"));
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T14:34:23Z"));
        assertEquals("2008-11-01T04:04:03Z", DateUtil.string2UTCString("2008-11-01T06:04:03+02:00"));
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T21:34:23+07:00"));
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T21:34:23+07:00"));
        assertEquals("2008-11-01T04:04:03Z", DateUtil.string2UTCString("2008-11-01T06:04:03+0200"));
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T21:34:23+0700"));
        assertEquals("2008-11-01T14:34:23Z", DateUtil.string2UTCString("2008-11-01T21:34:23+0700"));
        
        /*
         * res is timezone-dependent, therefore we don't test for equality
         */
        String res = DateUtil.string2UTCString("2008-11-01T21:34:23");
        assertTrue(res.startsWith("2008"));
        assertTrue(res.contains("T"));
        assertTrue(res.endsWith("Z"));
    }
    
    
    private String getCurrentLocaleTimezoneOffset() {
        Calendar cal = new GregorianCalendar();
        long offset = cal.getTimeZone().getOffset(System.currentTimeMillis());
        return timezoneOffsetToString(offset);
    }
    
    /**
     * Tests the timezone offset to string method. Like a test if the test is correct.
     */
    public void testTimezoneOffsetToString() {
        assertEquals("Z", timezoneOffsetToString(0L));
        assertEquals("+01:00", timezoneOffsetToString(3600000L));
        assertEquals("-01:00", timezoneOffsetToString(-3600000L));
        assertEquals("+01:30", timezoneOffsetToString(3600000L + 1800000L));
    }
    
    /**
     * Formats the timezone offset to the format described in the XSD dateTime datatype definition in
     * <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">here</a>
     * @param millis number of milliseconds (either positive or negative)
     * @return a string of the form: (('+' | '-') hh ':' mm) | 'Z'
     */
    private String timezoneOffsetToString(long millis) {
        if (millis == 0) {
            return "Z";
        }
        boolean neg = (millis < 0);
        millis = (neg ? -millis : millis);
        long hours = millis / (3600000L);
        millis %= (3600000L);
        long minutes = millis / 60000L; 
        return (neg ? "-" : "+") + zeroPad(hours) + ":" + zeroPad(minutes);
    }
    
    private String zeroPad(long i) {
        if (i >= 0 && i <=9) {
            return "0" + i;
        } else {
            return String.valueOf(i);
        }
    }
}


