package com.example.chadyeo.animetv.utils;


import com.example.chadyeo.animetv.api.Anime;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.ArrayList;

public class JSONParse {

    public static final Gson gson = new Gson();

    private JSONParse() {
    }

    public static Anime parseJsonFromAnimePage(Reader JSON) {
        Anime anime = gson.fromJson(JSON, Anime.class);
        return anime;
    }

    public static ArrayList<Anime> parseJsonForList(Reader JSON) {
        try {
            ArrayList<Anime> list = new ArrayList<>();
            JsonElement jsonElement = new JsonParser().parse(JSON);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i).getAsJsonObject().get("adult").getAsBoolean()) {
                    continue;
                }
                Anime anime = gson.fromJson(jsonArray.get(i).getAsJsonObject(), Anime.class);
                list.add(anime);
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }
}
