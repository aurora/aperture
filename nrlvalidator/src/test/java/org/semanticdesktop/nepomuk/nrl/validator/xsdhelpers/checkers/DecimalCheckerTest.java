package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import static org.junit.Assert.*;

import org.junit.Test;


public class DecimalCheckerTest {

    @Test public void test1() {
        DecimalChecker checker = new DecimalChecker();
        assertFalse(checker.checkString(""));
        assertFalse(checker.checkString("Antoni Rules"));
        assertTrue(checker.checkString("0"));
        assertTrue(checker.checkString("-0"));
        assertTrue(checker.checkString("0.0"));
        assertTrue(checker.checkString("234234.234234"));
        assertTrue(checker.checkString("-23423.123123"));
        assertFalse(checker.checkString("36534563."));
        assertFalse(checker.checkString("0..0"));
        assertFalse(checker.checkString("--2342342.234234"));
        assertFalse(checker.checkString("13212312a.334"));
    }
}
