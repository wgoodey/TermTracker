package com.whitneygoodey.termtracker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.whitneygoodey.termtracker.UI.Validator;

import org.junit.Test;


public class isValidTest {
    @Test
    public void isValidTest_allGood() {
        assertTrue(Validator.isValid("Title", "01/01/2021", "01/02/2022"));
    }

    @Test
    public void isValidTest_sameDate() {
        assertTrue(Validator.isValid("title", "12/12/2012", "12/12/2012"));
    }

    @Test
    public void isValidTest_allEmpty() {
        assertFalse(Validator.isValid("", "", ""));
    }

    @Test
    public void isValidTest_noTitle() {
        assertFalse(Validator.isValid("", "12/23/1999", "12/24/1999"));
    }

    @Test
    public void isValidTest_noStart() {
        assertFalse(Validator.isValid("title", "", "03/19/2000"));
    }

    @Test
    public void isValidTest_noEnd() {
        assertFalse(Validator.isValid("title", "05/13/2010", ""));
    }

    @Test
    public void isValidTest_endAfterStart() {
        assertFalse(Validator.isValid("title", "12/12/2012", "12/11/2012"));
    }
}
