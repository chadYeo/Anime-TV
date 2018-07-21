package com.example.chadyeo.animetv.api;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
        } else if (holder.mItem.getTotal_episodes() == 1){
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
        Glide.with(holder.anime_item_imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.movie_placeholder)
                .into(holder.anime_item_imageView);
        holder.anime_item_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Intent to Detail
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private synchronized void update(ArrayList<Anime> temp) {
        mValues.clear();
        mValues.addAll(temp);
    }

    public void changeDataSource(AnimeList newUserList) {
        ArrayList<Anime> temp = new ArrayList<>();
        for (int i = 0; i < Math.min(newUserList.getAll().size(), 20); i++) {
            temp.add(newUserList.getAll().get(i));
            update(temp);
        }
    }

    public void clearBitmapCache(Context c) {
        Glide.get(c).clearMemory();
        Log.w("Memory Cleared: ", "Glid Memory is Cleared");
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
