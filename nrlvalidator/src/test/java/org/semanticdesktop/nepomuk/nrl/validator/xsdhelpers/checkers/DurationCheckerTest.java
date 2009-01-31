package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import static org.junit.Assert.*;

import org.junit.Test;


public class DurationCheckerTest {

    @Test public void test1() {
        DurationChecker checker = new DurationChecker();
        assertFalse(checker.checkString(""));
        assertTrue(checker.checkString("P5Y"));
        
        assertTrue(checker.checkString("P1Y2M3DT10H30M"));
        assertTrue(checker.checkString("PT15H"));
        assertFalse(checker.checkString("PT"));
        assertFalse(checker.checkString("-PT"));
        
        assertTrue(checker.checkString("P1347Y"));
        assertTrue(checker.checkString("P1347M"));
        assertTrue(checker.checkString("P1Y2MT2H"));
        assertTrue(checker.checkString("P0Y1347M"));
        assertTrue(checker.checkString("P0Y1347M0D"));
        assertFalse(checker.checkString("P-1347M"));
        assertFalse(checker.checkString("P1Y2MT"));
        assertTrue(checker.checkString("-P1347M"));
    }
}
