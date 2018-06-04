package com.example.chadyeo.animetv.api;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MangaResponse {

    public static class Manga implements Parcelable {

        /**
         * Manga Model
         */
        @SerializedName("total_chapters")
        public int total_chapters;

        @SerializedName("total_volume")
        public int total_volume;

        public static final Creator<Manga> CREATOR = new Creator<Manga>() {
            @Override
            public Manga createFromParcel(Parcel in) {
                return new Manga(in);
            }

            @Override
            public Manga[] newArray(int size) {
                return new Manga[size];
            }
        };

        protected Manga(Parcel in) {
            total_chapters = in.readInt();
            total_volume = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(total_chapters);
            out.writeInt(total_volume);
        }
    }
}
