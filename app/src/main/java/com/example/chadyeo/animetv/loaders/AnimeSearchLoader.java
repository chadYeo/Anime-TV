package com.example.chadyeo.animetv.loaders;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.utils.AnimeListBuilder;

public class AnimeSearchLoader extends AsyncTaskLoader<AnimeList> {

    private AnimeList mAnimeList;
    private String query;
    private int page;
    private int sort;
    private int asc;

    public AnimeSearchLoader(@NonNull Context context, String query, int page, int sort, int asc) {
        super(context);
        this.query = query;
        this.page = page;
        this.sort = sort;
        this.asc = asc;
    }

    @Nullable
    @Override
    public AnimeList loadInBackground() {
        AnimeList result = AnimeListBuilder.buildSearchList(query, page, sort, asc);
        mAnimeList = result;
        return result;
    }

    @Override
    public void deliverResult(AnimeList data) {
        mAnimeList = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mAnimeList != null) {
            deliverResult(mAnimeList);
        }

        if (takeContentChanged() || mAnimeList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mAnimeList != null) {
            mAnimeList = null;
        }
    }
}
