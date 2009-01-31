package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import static org.junit.Assert.*;

import org.junit.Test;


public class DateTimeCheckerTest {

    @Test public void test1() {
        DateTimeChecker checker = new DateTimeChecker();
        assertFalse(checker.checkString(""));
        assertTrue(checker.checkString("1696-09-01T00:00:00Z"));
        assertTrue(checker.checkString("2002-10-10T12:00:00+05:00"));
        assertFalse(checker.checkString("2002-45-10T12:00:00+05:00"));
        assertFalse(checker.checkString("2002-45-10T12:00:00+65:00"));
    }
}
