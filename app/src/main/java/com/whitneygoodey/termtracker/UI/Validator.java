package com.whitneygoodey.termtracker.UI;

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
}
