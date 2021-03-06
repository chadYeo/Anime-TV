package com.example.chadyeo.animetv.loaders;



import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.utils.AnimeListBuilder;

public class AnimeSeasonLoader extends AsyncTaskLoader<AnimeList> {

    private static final String LOG_TAG = AnimeSeasonLoader.class.getSimpleName();

    private AnimeList mAnimeList;
    private String season;
    private String year;
    private int sort;
    private int asc;

    public AnimeSeasonLoader(Context context, String season, String year, int sort, int asc) {
        super(context);
        this.season = season;
        this.year = year;
        this.sort = sort;
        this.asc = asc;
    }

    @Override
    public AnimeList loadInBackground() {
        AnimeList result = AnimeListBuilder.buildSeasonList(season + " " + year, sort, asc);
        mAnimeList = result;

        Log.d(LOG_TAG, "loadInBackground with result: " + result);

        return mAnimeList;
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
