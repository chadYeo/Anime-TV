package com.example.chadyeo.animetv.api;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnimeResponse {

    public ArrayList<Anime> results;

    public static class Anime implements Parcelable {

        /**
         * Anime Model
         */
        @SerializedName("total_episodes")
        public int totalEpisodes;

        @SerializedName("youtube_id")
        public String youtube_id;

        @SerializedName("image_url_sml")
        public String image_url_sml;

        @SerializedName("image_url_med")
        public String image_url_med;

        @SerializedName("image_url_lge")
        public String image_url_lge;

        @SerializedName("image_url_banner")
        public String image_url_banner;

        protected Anime(Parcel in) {
            totalEpisodes = in.readInt();
            youtube_id = in.readString();
            image_url_sml = in.readString();
            image_url_med = in.readString();
            image_url_lge = in.readString();
            image_url_banner = in.readString();
        }

        public static final Creator<Anime> CREATOR = new Creator<Anime>() {
            @Override
            public Anime createFromParcel(Parcel in) {
                return new Anime(in);
            }

            @Override
            public Anime[] newArray(int size) {
                return new Anime[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(totalEpisodes);
            out.writeString(youtube_id);
            out.writeString(image_url_sml);
            out.writeString(image_url_med);
            out.writeString(image_url_lge);
            out.writeString(image_url_banner);
        }

        /**
         * Helper method to build poster image url.
         */
        public String getPosterUrl() {
            return IMAGE_BASE_URL + "w185" + this.image_url_banner;
        }

        private final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    }
}
