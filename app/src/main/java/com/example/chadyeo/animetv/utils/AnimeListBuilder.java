package com.example.chadyeo.animetv.utils;


import android.util.Log;

import com.example.chadyeo.animetv.MainActivity;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.api.HttpClient;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ANIMELIST FACTORY CLASS THAT CREATES ANIMELISTS FROM AniList.co API
 */
public class AnimeListBuilder {

    private static final String LOG_TAG = AnimeListBuilder.class.getSimpleName();

    private AnimeListBuilder() {
    }

    private static ArrayList<Anime> getSeasonList(String season, int sort, int asc) {
        String s = season.substring(0, season.indexOf(" ")).toLowerCase();
        String y = season.substring(season.indexOf(" ") + 1);

        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("www.anilist.co")
                    .addPathSegment("api")
                    .addPathSegment("browse")
                    .addPathSegment("anime")
                    .addQueryParameter("access_token", HttpClient.getAccessToken())
                    .addQueryParameter("season", s)
                    .addQueryParameter("year", y)
                    .addQueryParameter("airing-data", "true")
                    .addQueryParameter("full_page", "true")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cache-Control", "max-stale=3600000")
                    .tag("season")
                    .build();

            Response response = MainActivity.getClient().getOkHttpClient().newCall(request).execute();
            ArrayList<Anime> animeArrayList = JSONParse.parseJsonForList(new InputStreamReader(response.body().byteStream()));
            response.body().close();
            Log.d(LOG_TAG, "Network: Getting Season List: " + response);

            String catchResponse = "null";
            if (response.cacheResponse() != null) {
                catchResponse = response.cacheResponse().toString();
                Log.d(LOG_TAG, "Cache Response: " + catchResponse);
                Log.d(LOG_TAG, "Network Response: " + response.networkResponse().toString());
            }
            Collections.sort(animeArrayList, new AnimeComparator(sort, asc));
            Log.d(LOG_TAG, animeArrayList.toString());
            return animeArrayList;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Request Season List: " + e.toString());
        }
        return null;
    }

    /*
     * Create AnimeList object to store in ListContent. If so, then just return null
     */
    public static AnimeList buildSeasonList(String season, int sort, int asc) {
        if (checkSeason(season)) {
            return ListContent.getList();
        }

        ArrayList<Anime> all = getSeasonList(season, sort, asc);
        if (all == null) {
            return null;
        }
        AnimeList result = new AnimeList();
        result.setAll(all);

        ArrayList<Anime> tv = new ArrayList<>();
        ArrayList<Anime> movie = new ArrayList<>();
        ArrayList<Anime> ova = new ArrayList<>();
        for (Anime anime : all) {
            if (anime.getType().toLowerCase().equals("tv") || anime.getType().toLowerCase().equals("tv short")) {
                tv.add(anime);
            } else if (anime.getType().toLowerCase().equals("movie")) {
                movie.add(anime);
            } else if (anime.getType().toLowerCase().equals("special") || anime.getType().toLowerCase().equals("ova") || anime.getType().toLowerCase().equals("ona")) {
                ova.add(anime);
            }
        }
        result.setTV(tv);
        result.setMovie(movie);
        result.setOVAONASpecial(ova);
        result.setSeason(season);

        return result;
    }

    public static AnimeList reloadSeasonList(String season, int sort, int asc) {
        ArrayList<Anime> all = getSeasonList(season, sort, asc);
        if (all == null) {
            return null;
        }
        AnimeList result = new AnimeList();
        result.setAll(all);

        ArrayList<Anime> movie = new ArrayList<>();
        ArrayList<Anime> tv = new ArrayList<>();
        for (Anime anime : all) {
            if (anime.getType().toLowerCase().equals("movie")) {
                movie.add(anime);
            } else if (anime.getType().toLowerCase().equals("tv")) {
                tv.add(anime);
            }
        }
        result.setMovie(movie);
        result.setTV(tv);
        result.setSeason(season);

        return result;
    }

    /*
     * Check with ListContent if the season/query is the same.
     */
    private static boolean checkSeason(String season) {
        if (ListContent.getList().getSeason() == null) {
            return false;
        }
        return season.equalsIgnoreCase(ListContent.getList().getSeason());
    }

    private static boolean checkQuery(String query) {
        if (ListContent.getList().getQuery() == null) {
            return false;
        }
        return query.equalsIgnoreCase(ListContent.getList().getQuery());
    }
}
