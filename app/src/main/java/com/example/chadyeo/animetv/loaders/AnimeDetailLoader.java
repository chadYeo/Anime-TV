package com.example.chadyeo.animetv.loaders;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.utils.AnimeListBuilder;

public class AnimeDetailLoader extends AsyncTaskLoader<Anime> {

    private Anime mData;
    private String id;

    public AnimeDetailLoader(@NonNull Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    public Anime loadInBackground() {
        Anime result = AnimeListBuilder.buildAnimePage(id);
        mData = result;
        return result;
    }

    @Override
    public void deliverResult(@Nullable Anime data) {
        if (isReset()) {
            return;
        }

        Anime oldData = mData;
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

    @Override
    public void onCanceled(@Nullable Anime data) {
        super.onCanceled(data);
    }
}
