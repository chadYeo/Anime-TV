package com.example.chadyeo.animetv.utils;


import com.example.chadyeo.animetv.api.AnimeList;

public class ListContent {
    private static AnimeList list = new AnimeList();
    private static String currentSeason = "";
    private static String currentYear = "";

    private ListContent() {
    }

    public static AnimeList getList() {
        return list;
    }

    public static void setList(AnimeList list) {
        ListContent.list = list;
    }

    public static String getCurrentSeason() {
        return currentSeason;
    }

    public static void setCurrentSeason(String currentSeason) {
        ListContent.currentSeason = currentSeason;
    }

    public static String getCurrentYear() {
        return currentYear;
    }

    public static void setCurrentYear(String currentYear) {
        ListContent.currentYear = currentYear;
    }
}
