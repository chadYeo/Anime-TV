package com.example.chadyeo.animetv.api;


import java.io.Serializable;
import java.util.ArrayList;

public class AnimeList implements Serializable{
    private String lastUpdated;
    private String season;
    private String query;
    private ArrayList<Anime> all = new ArrayList<>();
    private ArrayList<Anime> TV = new ArrayList<>();
    private ArrayList<Anime> Movie = new ArrayList<>();
    private ArrayList<Anime> OVAONASpecial = new ArrayList<>();

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<Anime> getAll() {
        return all;
    }

    public void setAll(ArrayList<Anime> all) {
        this.all = all;
    }

    public ArrayList<Anime> getTV() {
        return TV;
    }

    public void setTV(ArrayList<Anime> TV) {
        this.TV = TV;
    }

    public ArrayList<Anime> getMovie() {
        return Movie;
    }

    public void setMovie(ArrayList<Anime> movie) {
        Movie = movie;
    }

    public ArrayList<Anime> getOVAONASpecial() {
        return OVAONASpecial;
    }

    public void setOVAONASpecial(ArrayList<Anime> OVAONASpecial) {
        this.OVAONASpecial = OVAONASpecial;
    }
}
