package com.example.chadyeo.animetv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.adapters.MovieAnimeItemRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.ListOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieAnimeFragment extends Fragment {

    private static final String LOG_TAG = MovieAnimeFragment.class.getSimpleName();

    private GridLayoutManager gridLayoutManager;
    private MovieAnimeItemRecyclerViewAdapter adapter;
    private OnMovieAnimeFragmentInteractionListener mListener;

    public MovieAnimeFragment() {
    }

    public static MovieAnimeFragment newInstance(int columnCount) {
        MovieAnimeFragment fragment = new MovieAnimeFragment();
        Bundle args = new Bundle();
        args.putInt(ListOptions.ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
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
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        int totalItemCount = adapter.getItemCount();
                        int loadedItems = ListContent.getList().getMovie().size();
                        int visibleItemCount = gridLayoutManager.getChildCount();
                        int lastVisibleItemCount = gridLayoutManager.findFirstVisibleItemPosition();
                        if (totalItemCount < loadedItems && (lastVisibleItemCount + visibleItemCount) >= totalItemCount) {
                            endlessScrollUpdate();
                        }
                    }
                }
            });
        }
        Log.d(LOG_TAG, "ListContent GetList Get Movie Info: " + String.valueOf(ListContent.getList().getMovie()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieAnimeFragmentInteractionListener) {
            mListener = (OnMovieAnimeFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        if (getView() != null) {
            RecyclerView list = (RecyclerView) getView().findViewById(R.id.anime_recyclerView);
            list.getLayoutManager().scrollToPosition(0);
            updateList();
            Log.d(LOG_TAG, "onResume: Size of Data: " + String.valueOf(adapter.getItemCount()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.clearBitmapCache(this.getContext());
    }

    public interface OnMovieAnimeFragmentInteractionListener {
        void onMovieAnimeFragmentInteraction(Anime item);
    }

    public void endlessScrollUpdate() {
        if (adapter != null) {
            adapter.endlessScrollReload(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            if (getView() != null) {
                RecyclerView list = (RecyclerView) getView().findViewById(R.id.anime_recyclerView);
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                adapter = new MovieAnimeItemRecyclerViewAdapter(ListContent.getList().getMovie(), mListener);
            }
        }
    }

    public void reloadList() {
        if (adapter != null) {
            adapter.reloadDataSource(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "reloadList - Size of Data: " + String.valueOf(adapter.getItemCount()));
        } else {
            adapter = new MovieAnimeItemRecyclerViewAdapter(ListContent.getList().getMovie(), mListener);
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
            adapter = new MovieAnimeItemRecyclerViewAdapter(ListContent.getList().getMovie(), mListener);
            Log.d(LOG_TAG, "Size of Data: " + String.valueOf(adapter.getItemCount()));
        }
    }
}
