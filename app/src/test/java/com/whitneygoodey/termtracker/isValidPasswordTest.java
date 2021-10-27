package com.whitneygoodey.termtracker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.whitneygoodey.termtracker.UI.Validator;

import org.junit.Test;

public class isValidPasswordTest {


    @Test
    public void isValidPasswordTest_matchingNumberSpecial() {
        assertTrue(Validator.isValidPassword("@dice-trapper-salon6"));
    }

    @Test
    public void isValidPasswordTest_Number() {
        assertFalse(Validator.isValidPassword("dice-trapper-salon6"));
    }

    @Test
    public void isValidPasswordTest_Special() {
        assertFalse(Validator.isValidPassword("@dice-trapper-salon"));
    }

    @Test
    public void isValidPasswordTest_matching() {
        assertTrue(Validator.isMatchingPassword("@dice-trapper-salon6", "@dice-trapper-salon6"));
    }

    @Test
    public void isMatchingPasswordTest_bothEmpty() {
        assertFalse(Validator.isMatchingPassword("", ""));
    }

    @Test
    public void isMatchingPasswordTest_different() {
        assertFalse(Validator.isMatchingPassword("@dice-trapper-salon6", "@dice-trapper-salon"));
    }


}
