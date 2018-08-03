package com.example.chadyeo.animetv.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.Anime;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_anime, container, false);
    }

    public interface OnMovieAnimeFragmentInteractionListener {
        void onMovieAnimeFragmentInteraction(Anime item);
    }
}
