package com.example.chadyeo.animetv.utils;

import com.example.chadyeo.animetv.api.Anime;

import java.util.Comparator;


class AnimeComparator implements Comparator<Anime> {

    /**
     * Sorting Type
     * 0 = Popularity
     * 1 = Title
     * 2 = Avg Score
     *
     * Sorting Asc/Des
     * 1 = Ascending
     * -1 = Descending
     */

    private int type;
    private int sort;

    public AnimeComparator(int type, int sort) {
        this.type = type;
        this.sort = sort;
    }

    @Override
    public int compare(Anime o1, Anime o2) {
        if (type == 0) {
            return (o1.getPopularity() - o2.getPopularity()) * sort;
        } else if (type == 1) {
            return o1.getTitle_romaji().compareToIgnoreCase(o2.getTitle_romaji()) * sort;
        } else {
            return  (int)((o1.getAverage_score() - o2.getAverage_score())) * sort;
        }
    }
}
