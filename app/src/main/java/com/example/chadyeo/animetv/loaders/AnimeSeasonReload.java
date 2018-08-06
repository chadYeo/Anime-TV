package com.example.chadyeo.animetv.loaders;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.utils.AnimeListBuilder;

public class AnimeSeasonReload extends AsyncTaskLoader<AnimeList> {

    private AnimeList mData;
    private String season;
    private String year;
    private int sort;
    private int asc;

    public AnimeSeasonReload(@NonNull Context context, String season, String year, int sort, int asc) {
        super(context);
        this.season = season;
        this.year = year;
        this.sort = sort;
        this.asc = asc;
    }

    @Nullable
    @Override
    public AnimeList loadInBackground() {
        AnimeList result = AnimeListBuilder.reloadSeasonList(season + " " + year, sort, asc);
        mData = result;
        return result;
    }

    @Override
    public void deliverResult(@Nullable AnimeList data) {
        AnimeList oldData = mData;
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
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

        if (mData != null) {
            mData = null;
        }
    }
}
