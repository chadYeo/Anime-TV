package com.example.chadyeo.animetv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.adapters.MovieAnimeItemRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.utils.ListContent;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieAnimeFragment extends Fragment {

    private OnMovieAnimeFragmentInteractionListener mListener;
    private MovieAnimeItemRecyclerViewAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    public MovieAnimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        final RecyclerView list = (RecyclerView) view;
        list.setHasFixedSize(true);
        list.setItemViewCacheSize(20);
        list.setDrawingCacheEnabled(true);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            gridLayoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new MovieAnimeItemRecyclerViewAdapter(ListContent.getList().getMovie(), mListener);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public interface OnMovieAnimeFragmentInteractionListener {
        void onMovieAnimeFragmentInteraction(Anime item);
    }
}
