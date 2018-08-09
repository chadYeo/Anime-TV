package com.example.chadyeo.animetv.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.utils.AnimeListBuilder;
import com.example.chadyeo.animetv.utils.ListContent;

public class AnimeSortLoader extends AsyncTaskLoader<AnimeList> {

    private AnimeList mData;
    private int sort;
    private int asc;

    public AnimeSortLoader(Context context, int sort, int asc) {
        super(context);
        this.sort = sort;
        this.asc = asc;
    }

    @Override
    public AnimeList loadInBackground() {
        AnimeList result = AnimeListBuilder.sortAnimeList(sort, asc, ListContent.getList());
        mData = result;
        return result;
    }

    @Override
    public void deliverResult(AnimeList data) {
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
    public void onCanceled(AnimeList data) {
        super.onCanceled(data);
    }
}
