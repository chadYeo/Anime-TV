package com.example.chadyeo.animetv.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.adapters.AllAnimeRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.ListOptions;

public class AllAnimeFragment extends Fragment {

    private static final String LOG_TAG = AllAnimeFragment.class.getSimpleName();

    private OnAllAnimeFragmentInteractionListener mListener;
    private GridLayoutManager gridLayoutManager;
    private AllAnimeRecyclerViewAdapter adapter;

    public AllAnimeFragment() {}

    public static AllAnimeFragment newInstance(int columnCount) {
        AllAnimeFragment fragment = new AllAnimeFragment();
        Bundle args = new Bundle();
        args.putInt(ListOptions.ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        final RecyclerView list = (RecyclerView)view;

        list.setHasFixedSize(true);
        list.setItemViewCacheSize(20);
        list.setDrawingCacheEnabled(true);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            gridLayoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        int totalItemCount = adapter.getItemCount();
                        int loadedItems = ListContent.getList().getAll().size();
                        int visibleItemCount = gridLayoutManager.getChildCount();
                        int lastVisibleItemCount = gridLayoutManager.findFirstVisibleItemPosition();
                        if (totalItemCount < loadedItems && (lastVisibleItemCount + visibleItemCount) >= totalItemCount) {
                            endlessScrollUpdate();
                        }
                    }
                }
            });
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
            //updateList();
        }
    }

    public interface OnAllAnimeFragmentInteractionListener {
        void onAllAnimeFragmentInteraction(Anime item);
    }

    private void endlessScrollUpdate() {
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
                adapter = new AllAnimeRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
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
