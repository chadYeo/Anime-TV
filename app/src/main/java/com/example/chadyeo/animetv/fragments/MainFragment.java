package com.example.chadyeo.animetv.fragments;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.AllAnimeRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.loaders.AnimeSeasonLoader;
import com.example.chadyeo.animetv.utils.ListContent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    @BindView(R.id.main_frag_progressBar) ProgressBar progressBar;
    @BindView(R.id.error_text_view) TextView errorTextView;
    @BindView(R.id.anime_recyclerView) RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;
    private AllAnimeRecyclerViewAdapter adapter;

    int sort = 0;
    int asc = -1;
    boolean running = false;
    boolean noInternet = false;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll());
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            updateList();
        }
    }

    public void initLoadDataForList(String season, String year, boolean reinit) {
        if (getLoaderManager().getLoader(0) == null) {
            getLoaderManager().initLoader(0, null, new InitLoader(getContext(), season, year));
        }
    }

    private class InitLoader implements LoaderManager.LoaderCallbacks<AnimeList> {

        private Context context;
        private String season;
        private String year;

        public InitLoader(Context context, String season, String year) {
            this.context = context;
            this.season = season;
            this.year = year;
        }

        @Override
        public Loader<AnimeList> onCreateLoader(int id, Bundle args) {
            return new AnimeSeasonLoader(context, season, year, sort, asc);
        }

        @Override
        public void onLoadFinished(Loader<AnimeList> loader, AnimeList data) {
            running = false;
            if (data == null) {
                noInternet = true;
            } else {
                if ((season.toLowerCase() + " " + year.toLowerCase())
                        .equals(((AppCompatActivity)getActivity()).getSupportActionBar().getTitle().toString().toLowerCase())) {
                    ListContent.setList(data);
                    Log.w("Size of Data: ", String.valueOf(data.getAll().size()));

                    updateList();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<AnimeList> loader) {

        }
    }

    public void updateList() {
        if (adapter != null) {
            adapter.changeDataSource(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            adapter.notifyDataSetChanged();
            if (getView() != null) {
                RecyclerView list = (RecyclerView) getView().findViewById(R.id.anime_recyclerView);
                list.getLayoutManager().scrollToPosition(0);
            }
            Log.w("Size of Data: ", String.valueOf(adapter.getItemCount()));
        } else {
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll());
        }
    }
}
