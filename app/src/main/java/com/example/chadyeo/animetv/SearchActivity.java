package com.example.chadyeo.animetv;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.fragments.SearchAnimeFragment;
import com.example.chadyeo.animetv.loaders.AnimeSearchLoader;
import com.example.chadyeo.animetv.utils.ColumnUtil;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.ListOptions;

public class SearchActivity extends AppCompatActivity implements SearchAnimeFragment.OnSearchAnimeFragmentInteractionListener {

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    String query;
    int sort = 0;
    int asc = 1;
    int page =1;
    boolean noInternet = false;
    boolean atEnd = false;
    boolean loaded = true;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sort = sharedPreferences.getInt(getString(R.string.list_sort), 0);
        asc = sharedPreferences.getInt(getString(R.string.order_sort), 1);

        if (ListContent.getList() != null) {
            ListContent.setList(new AnimeList());
        }

        setupFragment();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int o = getResources().getConfiguration().orientation;
        if (o != ListOptions.SCREEN_ORIENTATION) {
            ListOptions.COLUMN_COUNT = ColumnUtil.calculateNoOfColumns(this);
            setupFragment();
            ListOptions.SCREEN_ORIENTATION = 0;
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.list_sort), sort);
        editor.putInt(getString(R.string.order_sort), asc);
        editor.apply();;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_anime).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // Anime Fragment Interface
    @Override
    public void onSearchAnimeFragmentInteraction(Anime item) {
        Intent searchIntent = new Intent(this, AnimeDetailActivity.class);
        searchIntent.putExtra("ID", String.valueOf(item.getId()));
        startActivity(searchIntent);
    }

    // Searching method
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!query.equals(this.query) && !query.isEmpty()) {
                if (page != 1) {
                    page = 1;
                }
                atEnd = false;
                this.query = query;
                loaded = false;
                queryLoadDataForList(query, page);
            }
        }
    }

    // Initial loading in data and adding progress animation
    private void queryLoadDataForList(String query, int page) {
        if (getSupportLoaderManager().getLoader(5) == null) {
            getSupportLoaderManager().initLoader(5, null, new QueryLoad(this, query, page));
        } else {
            getSupportLoaderManager().restartLoader(5, null, new QueryLoad(this, query, page));
        }
    }

    public void setupFragment() {
        if (getSupportFragmentManager().findFragmentByTag("search") != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag("search"))
                    .commitAllowingStateLoss();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_list, SearchAnimeFragment.newInstance(ListOptions.COLUMN_COUNT), "search")
                .commitAllowingStateLoss();
    }

    // Initialization
    private class QueryLoad implements LoaderManager.LoaderCallbacks<AnimeList> {

        private Context context;
        private String query;
        private int page;

        public QueryLoad(Context context, String query, int page) {
            this.context = context;
            this.query = query;
            this.page = page;
        }

        @NonNull
        @Override
        public Loader<AnimeList> onCreateLoader(int id, @Nullable Bundle args) {
            return new AnimeSearchLoader(context, query, page, sort, asc);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<AnimeList> loader, AnimeList data) {

            LinearLayout view = (LinearLayout) findViewById(R.id.search_list);
            View progress = findViewById(R.id.progress_bar);
            TextView connect = (TextView) findViewById(R.id.connect_text);
            TextView retry = (TextView) findViewById(R.id.retry_text);
            TextView noResults = (TextView) findViewById(R.id.no_results_text);

            if (data == null && !isNetworkAvailable(context)) {
                noInternet = true;
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
                view.setVisibility(View.GONE);
                connect.setVisibility(View.VISIBLE);
                retry.setVisibility(View.VISIBLE);
                Log.d(LOG_TAG, "There is no data onLoadFinished");
            } else if (data == null && page == 1) {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
                view.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else if (data == null) {
                atEnd = true;
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
            } else {
                Log.d(LOG_TAG, "InitLoad Initiated in MainActivity");
                if (noInternet) {
                    noInternet = false;
                    connect.setVisibility(View.GONE);
                    retry.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }
                if (view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                }

                ListContent.setList(data);

                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }

                SearchAnimeFragment fragment = (SearchAnimeFragment) getSupportFragmentManager().findFragmentByTag("search");
                if (page == 1) {
                    fragment.updateList();
                } else {
                    fragment.reloadList();
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<AnimeList> loader) {

        }

        public boolean isNetworkAvailable(Context context) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return (networkInfo != null && networkInfo.isConnected());
            } catch (Exception e) {
                return false;
            }
        }
    }
}