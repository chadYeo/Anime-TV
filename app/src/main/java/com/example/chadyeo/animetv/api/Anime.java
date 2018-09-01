package com.example.chadyeo.animetv.api;


import java.io.Serializable;
import java.util.ArrayList;

public class Anime implements Serializable {

    private int id;

    private String title_english;
    private String title_romaji;
    private String title_japanese;
    private String description;
    private String source; // Source (Manga, Light novel etc.)
    private ArrayList<String> genres;
    private ArrayList<String> synonyms;
    private int start_date_fuzzy;
    private int end_date_fuzzy;

    private ArrayList<Studio> studio;

    private double average_score;
    private int popularity;

    private String type; //Movie, TV Short, TV etc.
    private int season;
    private int total_episodes;
    private int total_chapters;
    private int total_volume;
    private int duration;

    private String youtube_id;
    private String image_url_sml;
    private String image_url_med;
    private String image_url_lge;
    private String image_url_banner;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle_english() {
        return title_english;
    }

    public void setTitle_english(String title_english) {
        this.title_english = title_english;
    }

    public String getTitle_romaji() {
        return title_romaji;
    }

    public void setTitle_romaji(String title_romaji) {
        this.title_romaji = title_romaji;
    }

    public String getTitle_japanese() {
        return title_japanese;
    }

    public void setTitle_japanese(String title_japanese) {
        this.title_japanese = title_japanese;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public double getAverage_score() {
        return average_score;
    }

    public void setAverage_score(double average_score) {
        this.average_score = average_score;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getTotal_episodes() {
        return total_episodes;
    }

    public void setTotal_episodes(int total_episodes) {
        this.total_episodes = total_episodes;
    }

    public int getTotal_chapters() {
        return total_chapters;
    }

    public void setTotal_chapters(int total_chapters) {
        this.total_chapters = total_chapters;
    }

    public int getTotal_volume() {
        return total_volume;
    }

    public void setTotal_volume(int total_volume) {
        this.total_volume = total_volume;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getImage_url_sml() {
        return image_url_sml;
    }

    public void setImage_url_sml(String image_url_sml) {
        this.image_url_sml = image_url_sml;
    }

    public String getImage_url_med() {
        return image_url_med;
    }

    public void setImage_url_med(String image_url_med) {
        this.image_url_med = image_url_med;
    }

    public String getImage_url_lge() {
        return image_url_lge;
    }

    public void setImage_url_lge(String image_url_lge) {
        this.image_url_lge = image_url_lge;
    }

    public String getImage_url_banner() {
        return image_url_banner;
    }

    public void setImage_url_banner(String image_url_banner) {
        this.image_url_banner = image_url_banner;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<Studio> getStudio() {
        return studio;
    }

    public void setStudio(ArrayList<Studio> studio) {
        this.studio = studio;
    }

    public int getStart_date_fuzzy() {
        return start_date_fuzzy;
    }

    public void setStart_date_fuzzy(int start_date_fuzzy) {
        this.start_date_fuzzy = start_date_fuzzy;
    }

    public int getEnd_date_fuzzy() {
        return end_date_fuzzy;
    }

    public void setEnd_date_fuzzy(int end_date_fuzzy) {
        this.end_date_fuzzy = end_date_fuzzy;
    }
}
