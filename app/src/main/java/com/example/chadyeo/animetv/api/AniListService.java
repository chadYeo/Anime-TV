package com.example.chadyeo.animetv.api;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface AniListService {
    // Example: /discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
    @GET("discover/anime")
    void getAnimeList(@Query("sort_by") String sortBy, Callback<Anime> callback);

}
