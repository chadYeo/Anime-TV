package com.example.chadyeo.animetv.api;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AnimeResponse implements Serializable {

    /**
     * Anime Model
     */
    private int id;
    private int totalEpisodes;
    private String youtube_id;
    private String image_url_sml;
    private String image_url_med;
    private String image_url_lge;
    private String image_url_banner;

    /**
     * Manga Model
     */
    public int total_chapters;
    public int total_volume;

    /**
     * Series Model
     */
    public String seriesType;
    public String titleEnglish;
    public int season;
    public String description;
    public String genres;
    public Byte adult; // Respond will be in boolean : set true, byte ==1 and set false, byte ==2
    public double averageScore;
    public int popularity;
}
