package com.example.chadyeo.animetv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chadyeo.animetv.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    @BindView(R.id.main_frag_progressBar) ProgressBar progressBar;
    @BindView(R.id.error_text_view) TextView errorTextView;
    @BindView(R.id.anime_recyclerView) RecyclerView recyclerView;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);


        return view;
    }


    // Anime RecyclerView adapter class
    private static class AnimeAdapter extends RecyclerView.Adapter<AnimeViewHolder> {

        final private Context mContet;

        private AnimeAdapter(Context mContet) {
            this.mContet = mContet;
        }

        @NonNull
        @Override
        public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    // Anime view holder class
    private static class AnimeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.anime_item_imageView) ImageView mAnimeImageView;

        public AnimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
