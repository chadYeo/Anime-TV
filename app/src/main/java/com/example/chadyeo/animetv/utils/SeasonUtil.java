package com.example.chadyeo.animetv.utils;


public class SeasonUtil {

    private static String[] s = {"Winter", "Spring", "Summer", "Fall"};

    private SeasonUtil() {
    }

    public static String checkMonth(int month) {
        String result;
        if (month == 11 || month < 2) {
            result = "Winter";
        } else if (month >= 2 && month <= 4) {
            result = "Spring";
        } else if (month >= 5 && month <= 7) {
            result = "Summer";
        } else {
            result = "Fall";
        }
        return result;
    }

    public static String getSubtitle(String season) {
        if (season.toLowerCase().equals("winter")) {
            return "December - February";
        } else if (season.toLowerCase().equals("spring")) {
            return "March - May";
        } else if (season.toLowerCase().equals("summer")) {
            return "June - August";
        } else {
            return "September - November";
        }
    }

    public static String getSeasonText(int index) {
        return s[(index-1)];
    }
}
