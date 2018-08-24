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
import com.example.chadyeo.animetv.adapters.SearchAnimeItemRecyclerViewAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.ListOptions;


public class SearchAnimeFragment extends Fragment {

    private OnSearchAnimeFragmentInteractionListener mListener;
    private SearchAnimeItemRecyclerViewAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    public SearchAnimeFragment() {
    }

    public static SearchAnimeFragment newInstance(int columnCount) {
        SearchAnimeFragment fragment = new SearchAnimeFragment();
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
        list.setItemViewCacheSize(40);
        list.setDrawingCacheEnabled(true);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(true);
            gridLayoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new SearchAnimeItemRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        int totalItemCount = adapter.getItemCount();
                        int loadedItems = ListContent.getList().getTV().size();
                        int visibleItemCount = gridLayoutManager.getChildCount();
                        int lastVisibleItemCount = gridLayoutManager.findFirstVisibleItemPosition();
                        if (totalItemCount < loadedItems && (lastVisibleItemCount + visibleItemCount) >= totalItemCount) {
                            endlessScrollUpdate();
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchAnimeFragmentInteractionListener) {
            mListener = (OnSearchAnimeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchAnimeFragmentInteractionListener");
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
        adapter.clearBitmapCache(this.getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchAnimeFragmentInteractionListener {
        void onSearchAnimeFragmentInteraction(Anime item);
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
                adapter = new SearchAnimeItemRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
            }
        }
    }

    public void reloadList() {
        if (adapter != null) {
            adapter.reloadDataSource(ListContent.getList());
            adapter.clearBitmapCache(this.getContext());
            adapter.notifyDataSetChanged();
        } else {
            adapter = new SearchAnimeItemRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
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
        } else {
            adapter = new SearchAnimeItemRecyclerViewAdapter(ListContent.getList().getAll(), mListener);
        }
    }
}
