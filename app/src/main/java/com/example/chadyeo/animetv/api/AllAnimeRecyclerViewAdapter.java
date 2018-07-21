package com.example.chadyeo.animetv.api;


import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chadyeo.animetv.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllAnimeRecyclerViewAdapter extends RecyclerView.Adapter<AllAnimeRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Anime> mValues = new ArrayList<>();

    public AllAnimeRecyclerViewAdapter(ArrayList<Anime> items) {
        mValues.addAll(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.anime_title_textView.setText(holder.mItem.getTitle_romaji());
        holder.anime_episodes_textView.setText(holder.mItem.getTotal_episodes());
        holder.anime_type_textView.setText(holder.mItem.getType());


        if (holder.mItem.getTotal_episodes() > 1) {
            holder.anime_episodes_textView.setText(holder.mItem.getTotal_episodes() + " eps");
        } else if (holder.mItem.getTotal_episodes() = 1){
            holder.anime_episodes_textView.setText(holder.mItem.getTotal_episodes() + " ep");
        } else {
            holder.anime_episodes_textView.setText("? eps");
        }

        String url = holder.mItem.getImage_url_lge();
        if (url == null) {
            url = holder.mItem.getImage_url_med();
        }
        if (url == null) {
            url = holder.mItem.getImage_url_sml();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

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
