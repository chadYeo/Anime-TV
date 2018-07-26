package com.example.chadyeo.animetv.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.chadyeo.animetv.MainActivity;
import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.AllAnimeRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.loaders.AnimeSeasonLoader;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.SeasonUtil;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<AnimeList> {

    @BindView(R.id.main_frag_progressBar) ProgressBar progressBar;
    @BindView(R.id.error_text_view) TextView errorTextView;
    @BindView(R.id.anime_recyclerView) RecyclerView recyclerView;

    private LinearLayoutManager manager;
    private AllAnimeRecyclerViewAdapter adapter;

    int sort = 0;
    int asc = -1;

    String season;
    String year;
    ArrayList<String> years = new ArrayList<>();

    private boolean loaded = false;
    boolean running = false;
    boolean noInternet = false;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort = sharedPreferences.getInt(getString(R.string.list_sort), 0);
        asc = sharedPreferences.getInt(getString(R.string.order_sort), -1);

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        if (savedInstanceState == null) {
            String season = SeasonUtil.checkMonth(month);
            String year = String.valueOf(y);
            ListContent.setCurrentSeason(season);
            this.season = season;
            ListContent.setCurrentYear(year);
            this.year = year;
        } else {
            this.season = savedInstanceState.getString("SEASON");
            this.year = savedInstanceState.getString("YEAR");
            ListContent.setCurrentSeason(season);
            ListContent.setCurrentYear(year);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll());
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
        if (getView() != null) {
            updateList();
        }
    }

    @Override
    public Loader<AnimeList> onCreateLoader(int i, Bundle bundle) {
        return new AnimeSeasonLoader(getContext(), season, year, sort, asc);
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

    public void reloadList() {
        if (adapter != null) {
            adapter.changeDataSource(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            adapter.notifyDataSetChanged();
            if (getView() != null) {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        } else {
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll());
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
