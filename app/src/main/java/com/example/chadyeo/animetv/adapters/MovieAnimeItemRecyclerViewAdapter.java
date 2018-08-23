package com.example.chadyeo.animetv.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.fragments.MovieAnimeFragment;

import java.util.ArrayList;

public class MovieAnimeItemRecyclerViewAdapter
        extends RecyclerView.Adapter<MovieAnimeItemRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Anime> mValues = new ArrayList<>();
    private MovieAnimeFragment.OnMovieAnimeFragmentInteractionListener mListener;

    public MovieAnimeItemRecyclerViewAdapter(ArrayList<Anime> items, MovieAnimeFragment.OnMovieAnimeFragmentInteractionListener listener) {
        mValues.addAll(items);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anime_item, parent, false);
        return new MovieAnimeItemRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.anime_title_textView.setText(holder.mItem.getTitle_romaji());
        holder.anime_episodes_textView.setText(String.valueOf(holder.mItem.getTotal_episodes()));
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
                .into(holder.anime_item_imageView);
        holder.anime_item_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Intent to Detail
                mListener.onMovieAnimeFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private synchronized void update(ArrayList<Anime> temp) {
        mValues.clear();
        mValues.addAll(temp);
    }

    public void reloadDataSource(AnimeList newUserList) {
        update(newUserList.getMovie());
    }

    public void changeDataSource(AnimeList newUserList) {
        ArrayList<Anime> temp = new ArrayList<>();
        for (int i = 0; i < Math.min(newUserList.getMovie().size(), 20); i++) {
            temp.add(newUserList.getMovie().get(i));
        }
        update(temp);
    }

    public void endlessScrollReload(AnimeList newAnimeList) {
        ArrayList<Anime> temp = new ArrayList<>();
        temp.addAll(mValues);
        int updateSize = temp.size() + 20;
        for (int i = Math.min(newAnimeList.getMovie().size(), temp.size()); i < Math.min(newAnimeList.getMovie().size(), updateSize); i++) {
            temp.add(newAnimeList.getMovie().get(i));
        }
        update(temp);
    }

    public void clearBitmapCache(Context c) {
        Glide.get(c).clearMemory();
        Log.w("Memory Cleared: ", "Glid Memory is Cleared");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public Anime mItem;

        public CardView anime_item_cardView;
        public ImageView anime_item_imageView;
        public TextView anime_title_textView;
        public TextView anime_episodes_textView;
        public TextView anime_type_textView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            anime_item_cardView = (CardView) view.findViewById(R.id.anime_item_cardView);
            anime_item_imageView = (ImageView) view.findViewById(R.id.anime_item_imageView);
            anime_title_textView = (TextView) view.findViewById(R.id.anime_title_textView);
            anime_episodes_textView = (TextView) view.findViewById(R.id.anime_episodes_textView);
            anime_type_textView = (TextView) view.findViewById(R.id.anime_type_textView);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) anime_item_cardView.getLayoutParams();
            anime_item_imageView.requestLayout();
            anime_item_imageView.getLayoutParams().width = (int) (view.getContext().getResources()
                    .getDisplayMetrics().widthPixels * (1.0/2.0) - (layoutParams.leftMargin + layoutParams.rightMargin));
            anime_item_imageView.getLayoutParams().height = (int) (anime_item_imageView.getLayoutParams().width * 1.44);
        }
    }
}