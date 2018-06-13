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

import com.bumptech.glide.Glide;
import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.AniListService;
import com.example.chadyeo.animetv.api.AnimeResponse;
import com.example.chadyeo.animetv.api.RetrofitAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.RestAdapter;

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

    // Loads Anime List
    private void loadData() {
        RestAdapter adapter = RetrofitAdapter.getRestAdapter();
        AniListService service = adapter.create(AniListService.class);
    }


    // Anime RecyclerView adapter class
    private static class AnimeAdapter extends RecyclerView.Adapter<AnimeViewHolder> {

        final private Context mContext;
        private ArrayList<AnimeResponse.Anime> mItems;
        final private ListActionListener mActionListener;

        public AnimeAdapter(Context context, ListActionListener listener) {
            mContext = context;
            mActionListener = listener;
            mItems = new ArrayList<>();
        }

        @NonNull
        @Override
        public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.anime_item, parent, false);
            return new AnimeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AnimeViewHolder holder, final int position) {
            Glide.with(mContext)
                    .load(mItems.get(position).getPosterUrl())
                    .centerCrop()
                    .placeholder(R.drawable.movie_placeholder)
                    .crossFade()
                    .into(holder.mAnimeImageView);
            holder.mAnimeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionListener.onAnimeSelected(mItems.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void setItemCount(ArrayList<AnimeResponse.Anime> items) {
            mItems = items;
            notifyDataSetChanged();
        }

        public ArrayList<AnimeResponse.Anime> getItems() {
            return mItems;
        }

    }

    // Anime view holder class
    public static class AnimeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.anime_item_imageView) ImageView mAnimeImageView;

        public AnimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //Anime list action listener
    public interface ListActionListener {
        void onAnimeSelected(AnimeResponse.Anime anime);
    }
}
