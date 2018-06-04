package com.example.chadyeo.animetv.api;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AnimeResponse {

    public ArrayList<Anime> results;

    private static class Anime implements Parcelable {

        /**
         * Anime Model
         */
        @SerializedName("total_episodes")
        public int totalEpisodes;

        @SerializedName("youtube_id")
        public String youtube_id;

        protected Anime(Parcel in) {
            totalEpisodes = in.readInt();
            youtube_id = in.readString();
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
        }
    }
}
