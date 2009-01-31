package org.semanticdesktop.nepomuk.nrl.validator.xsdhelpers.checkers;

import static org.junit.Assert.*;

import org.junit.Test;


public class DayTimeDurationCheckerTest {

    @Test public void test1() {
        DayTimeDurationChecker checker = new DayTimeDurationChecker();
        assertFalse(checker.checkString(""));
        assertFalse(checker.checkString("P5Y"));
        
        assertFalse(checker.checkString("P1Y2M3DT10H30M"));
        assertTrue(checker.checkString("PT15H"));
        assertFalse(checker.checkString("PT"));
        assertFalse(checker.checkString("-PT"));
        
        assertFalse(checker.checkString("P1347Y"));
        assertFalse(checker.checkString("P1347M"));
        assertFalse(checker.checkString("P1Y2MT2H"));
        assertFalse(checker.checkString("P0Y1347M"));
        assertFalse(checker.checkString("P0Y1347M0D"));
        assertFalse(checker.checkString("P-1347M"));
        assertFalse(checker.checkString("P1Y2MT"));
        assertFalse(checker.checkString("-P1347M"));
        assertTrue(checker.checkString("P1DT15H"));
        assertTrue(checker.checkString("PT15H30M23S"));
    }
}
