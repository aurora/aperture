/*
 * Copyright (c) 2006 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.ical;

import junit.framework.TestCase;


public class DurationConversionTest extends TestCase {
    public void testDurationConvertion() {
        IcalCrawler crawler = new IcalCrawler();
        assertEquals(crawler.convertIcalDurationToXSDDayTimeDuration("P15DT5H0M20S"),"P15DT5H20S");
        assertEquals(crawler.convertIcalDurationToXSDDayTimeDuration("P7W"),"P49D");
        assertEquals(crawler.convertIcalDurationToXSDDayTimeDuration("-P7W"),"-P49D");
        assertEquals(crawler.convertIcalDurationToXSDDayTimeDuration("P0DT0H0M0S"),"P0S");
        assertEquals(crawler.convertIcalDurationToXSDDayTimeDuration("P15DT241H0M20S"),"P25DT1H20S");
    }
}

