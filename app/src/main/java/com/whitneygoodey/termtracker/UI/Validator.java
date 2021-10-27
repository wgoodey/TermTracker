package com.whitneygoodey.termtracker.UI;

import android.text.TextUtils;

import java.time.ZonedDateTime;

public class Validator {

    public static boolean isValid(String title, String start, String end) {
        boolean validity = true;

        try {
            ZonedDateTime startDate = MainActivity.getZonedDateTime(start);
            ZonedDateTime endDate = MainActivity.getZonedDateTime(end);

            if (title.equals("") || start.equals("") || end.equals("")) {
                validity = false;
            } else if (endDate.isBefore(startDate)) {
                validity = false;
            }
        } catch (Exception e) {
            validity = false;
        }
        return validity;
    }

    public static boolean isValid(String title, int credits, String start, String end) {
        return (isValid(title, start, end)) && credits >= 0;
    }


    public static final boolean isValidEmail(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPassword(String pass) {

        if (!pass.matches("^(?=.*[_.()$&@]).*$")) {
            return false;
        } else if(!pass.matches("(.*[0-9].*)")) {
            return false;
        } else if (pass.length() < 8) {
            return false;
        } else return true;

    }

    public static boolean isMatchingPassword(String pass, String confirmPass) {

        return pass.equals(confirmPass) && !pass.isEmpty();

    }
}
