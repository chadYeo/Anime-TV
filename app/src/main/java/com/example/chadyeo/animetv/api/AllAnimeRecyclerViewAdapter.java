package com.example.chadyeo.animetv.api;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chadyeo.animetv.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllAnimeRecyclerViewAdapter extends RecyclerView.Adapter<AllAnimeRecyclerViewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public Anime mItem;
        @BindView(R.id.anime_item_cardView)CardView anime_item_cardView;
        @BindView(R.id.anime_item_imageView) ImageView anime_item_imageView;
        @BindView(R.id.anime_title_textView) TextView anime_title_textView;
        @BindView(R.id.anime_episodes_textView) TextView anime_episodes_textView;
        @BindView(R.id.anime_type_textView) TextView anime_type_textView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
