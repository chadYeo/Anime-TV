package com.example.chadyeo.animetv.utils;


import android.util.Log;

import com.example.chadyeo.animetv.MainActivity;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.api.HttpClient;

import java.io.IOException;
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

    public static AnimeList buildSearchList(String query, int page, int sort, int asc) {
        if (checkQuery(query)) {
            ArrayList<Anime> all = getSearchList(query, page);
            if (all == null) {
                return null;
            }

            AnimeList result = new AnimeList();
            Collections.sort(all, new AnimeComparator(sort, asc));
            result.setAll(all);

            ArrayList<Anime> tv = new ArrayList<>();
            ArrayList<Anime> movie = new ArrayList<>();
            for (Anime anime : all) {
                if (anime.getType().toLowerCase().equals("tv") || anime.getType().toLowerCase().equals("tv short")) {
                    tv.add(anime);
                } else if (anime.getType().toLowerCase().equals("movie")) {
                    movie.add(anime);
                }
            }
            result.setTV(tv);
            result.setMovie(movie);

            return result;
        } else {
            ArrayList<Anime> all = getSearchList(query, page);
            if (all == null) {
                return null;
            }

            AnimeList results = new AnimeList();
            Collections.sort(all, new AnimeComparator(sort, asc));
            results.setAll(all);

            ArrayList<Anime> tv = new ArrayList<>();
            ArrayList<Anime> movie = new ArrayList<>();
            for (Anime anime : all) {
                if (anime.getType().toLowerCase().equals("tv") || anime.getType().toLowerCase().equals("tv short")) {
                    tv.add(anime);
                } else if (anime.getType().toLowerCase().equals("movie")) {
                    movie.add(anime);
                }
            }
            results.setTV(tv);
            results.setMovie(movie);

            return results;
        }
    }

    public static Anime buildAnimePage(String id) {
        Anime anime = getAnimePage(id);
        return anime;
    }

    private static ArrayList<Anime> getSearchList(String query, int page) {
        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("www.anilist.co")
                    .addPathSegment("api")
                    .addPathSegment("anime")
                    .addPathSegment("search")
                    .addPathSegment(query)
                    .addQueryParameter("access_token", HttpClient.getAccessToken())
                    .addQueryParameter("page", String.valueOf(page))
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cache-Control", "max-stale=3600000")
                    .build();
            Response response = MainActivity.getClient().getOkHttpClient().newCall(request).execute();
            ArrayList<Anime> a = JSONParse.parseJsonForList(new InputStreamReader(response.body().byteStream()));
            response.body().close();
            Log.d("Network: ", "Getting search list");
            String cr = "null";
            if (response.cacheResponse() != null) cr = response.cacheResponse().toString();
            Log.w("Cache response:", cr);
            Log.w("Network response:", response.networkResponse().toString());
            return a;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Anime getAnimePage(String id){
        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("www.anilist.co")
                    .addPathSegment("api")
                    .addPathSegment("anime")
                    .addPathSegment(id)
                    .addPathSegment("page")
                    .addQueryParameter("access_token", HttpClient.getAccessToken())
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cache-Control", "max-stale=3600000")
                    .tag("page")
                    .build();
            Response response = MainActivity.getClient().getOkHttpClient().newCall(request).execute();
            Anime a = JSONParse.parseJsonFromAnimePage(new InputStreamReader(response.body().byteStream()));
            response.body().close();
            return a;
        } catch (Exception e){
            Log.d("Request Anime page: ", e.toString());
        }
        return null;
    }

    public static AnimeList sortAnimeList(int sort, int ad, AnimeList animeList) {
        ArrayList<Anime> all = new ArrayList<>();
        ArrayList<Anime> movie = new ArrayList<>();
        ArrayList<Anime> tv = new ArrayList<>();

        all.addAll(animeList.getAll());
        movie.addAll(animeList.getMovie());
        tv.addAll(animeList.getTV());

        Collections.sort(all, new AnimeComparator(sort, ad));
        Collections.sort(movie, new AnimeComparator(sort, ad));
        Collections.sort(tv, new AnimeComparator(sort, ad));

        AnimeList xAnimeList = new AnimeList();
        xAnimeList.setAll(all);
        xAnimeList.setMovie(movie);
        xAnimeList.setTV(tv);

        return xAnimeList;
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
