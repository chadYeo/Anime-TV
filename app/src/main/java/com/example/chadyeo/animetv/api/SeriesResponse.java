package com.example.chadyeo.animetv.api;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SeriesResponse {

    public ArrayList<Anime> results;

    public static class Anime implements Parcelable {

        /**
         * Series Model
         */
        @SerializedName("series_type")
        public String seriesType;

        @SerializedName("title_english")
        public String titleEnglish;

        @SerializedName("season")
        public int season;

        @SerializedName("description")
        public String description;

        @SerializedName("genres")
        public String genres;

        @SerializedName("adult")
        public Byte adult; // Respond will be in boolean : set true, byte ==1 and set false, byte ==2

        @SerializedName("average_score")
        public double averageScore;

        @SerializedName("popularity")
        public int popularity;

        @SerializedName("image_url_sml")
        public String image_url_sml;

        @SerializedName("image_url_med")
        public String image_url_med;

        @SerializedName("image_url_lge")
        public String image_url_lge;

        @SerializedName("image_url_banner")
        public String image_url_banner;

        public static final Parcelable.Creator<Anime> CREATOR = new Parcelable.Creator<Anime>() {
            public Anime createFromParcel(Parcel in) {
                return new Anime(in);
            }

            public Anime[] newArray(int size) {
                return new Anime[size];
            }
        };

        public Anime(Parcel in) {
            seriesType = in.readString();
            titleEnglish = in.readString();
            season = in.readInt();
            description = in.readString();
            genres = in.readString();
            adult = in.readByte();
            averageScore = in.readDouble();
            popularity = in.readInt();
            image_url_sml = in.readString();
            image_url_med = in.readString();
            image_url_lge = in.readString();
            image_url_banner = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(seriesType);
            out.writeString(titleEnglish);
            out.writeInt(season);
            out.writeString(description);
            out.writeString(genres);
            out.writeByte(adult);
            out.writeDouble(averageScore);
            out.writeInt(popularity);
            out.writeString(image_url_sml);
            out.writeString(image_url_med);
            out.writeString(image_url_lge);
            out.writeString(image_url_banner);
        }
    }
}
