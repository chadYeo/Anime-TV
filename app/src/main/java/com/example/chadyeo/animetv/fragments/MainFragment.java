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

public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    //@BindView(R.id.main_frag_progressBar) ProgressBar progressBar;
    //@BindView(R.id.error_text_view) TextView errorTextView;
    @BindView(R.id.anime_recyclerView) RecyclerView recyclerView;

    private OnAllAnimeFragmentInteractionListener mListener;
    private LinearLayoutManager manager;
    private AllAnimeRecyclerViewAdapter adapter;

    public MainFragment() {
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
            manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
        }

        Log.d(LOG_TAG, "ListContent GetList Get All Info: " + String.valueOf(ListContent.getList().getAll()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAllAnimeFragmentInteractionListener) {
            mListener = (OnAllAnimeFragmentInteractionListener) context;
        }
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

    public interface OnAllAnimeFragmentInteractionListener {
        void onAllAnimeFragmentInteraction(Anime item);
    }

    public void reloadList() {
        if (adapter != null) {
            adapter.reloadDataSource(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            adapter.notifyDataSetChanged();
        } else {
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
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
            Log.d(LOG_TAG, "Size of Data: " + String.valueOf(adapter.getItemCount()));
        } else {
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
            Log.d(LOG_TAG, "Size of Data: " + String.valueOf(adapter.getItemCount()));
        }
    }
}
