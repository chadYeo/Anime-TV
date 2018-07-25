package com.example.chadyeo.animetv.utils;


import java.util.ArrayList;
import java.util.Arrays;

public class SeasonUtil {

    private static String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private static String[] s = {"Winter", "Spring", "Summer", "Fall"};
    private static ArrayList<String> seasons = new ArrayList<>(Arrays.asList(s));

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

    public static String[] prevSeason(String season, String year) {
        int y = Integer.parseInt(year);
        String[] result = new String[2];
        int seasonIndex = seasons.indexOf(season);
        if (seasonIndex == 0) {
            seasonIndex = seasons.size() - 1;
            y--;
        } else {
            seasonIndex--;
        }
        result[0] = seasons.get(seasonIndex);
        result[1] = String.valueOf(y);
        return result;
    }

    public static String[] nextSeason(String season, String year) {
        int y = Integer.parseInt(year);
        String[] result = new String[2];
        int seasonIndex = seasons.indexOf(season);
        if (seasonIndex == 0) {
            seasonIndex = seasons.size() - 1;
            y++;
        } else {
            seasonIndex++;
        }
        result[0] = seasons.get(seasonIndex);
        result[1] = String.valueOf(y);
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

    public static String getMonth(int id) {
        if (id-1 < 12) {
            return months[(id-1)];
        } else {
            return "error";
        }
    }

    public static String getSeasonText(int index) {
        return s[(index-1)];
    }
}
