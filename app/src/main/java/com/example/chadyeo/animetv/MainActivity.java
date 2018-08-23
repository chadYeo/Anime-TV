package com.example.chadyeo.animetv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chadyeo.animetv.adapters.SeasonPagerStateAdapter;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.api.HttpClient;
import com.example.chadyeo.animetv.fragments.AllAnimeFragment;
import com.example.chadyeo.animetv.fragments.MovieAnimeFragment;
import com.example.chadyeo.animetv.fragments.TVAnimeFragment;
import com.example.chadyeo.animetv.loaders.AnimeSeasonLoader;
import com.example.chadyeo.animetv.loaders.AnimeSortLoader;
import com.example.chadyeo.animetv.utils.ColumnUtil;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.ListOptions;
import com.example.chadyeo.animetv.utils.SeasonUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements AllAnimeFragment.OnAllAnimeFragmentInteractionListener,
        MovieAnimeFragment.OnMovieAnimeFragmentInteractionListener,
        TVAnimeFragment.OnTVAnimeFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static HttpClient client;

    ViewPager viewPager;
    TabLayout tabLayout;
    SeasonPagerStateAdapter adapter;

    String season;
    String year;
    ArrayList<String> years = new ArrayList<>();
    int sort = 0;
    int asc = 1;
    int currentSelectedTab = 0;
    boolean noInternet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sort = sharedPreferences.getInt(getString(R.string.list_sort), 0);
        asc = sharedPreferences.getInt(getString(R.string.order_sort), 1);
        season = sharedPreferences.getString(getString(R.string.season_sort), season);
        year = sharedPreferences.getString(getString(R.string.year_sort), year);

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        if (ListContent.getList() != null) {
            ListContent.setList(new AnimeList());
        }

        if (savedInstanceState == null) {
            String season = SeasonUtil.checkMonth(month);
            String year = String.valueOf(y);
            ListContent.setCurrentSeason(season);
            this.season = season;
            ListContent.setCurrentYear(year);
            this.year = year;
        } else {
            this.season = savedInstanceState.getString(getString(R.string.season_sort));
            this.year = savedInstanceState.getString(getString(R.string.year_sort));
            ListContent.setCurrentSeason(season);
            ListContent.setCurrentYear(year);
        }
        for (int i = 1951; i <= y+1; i++) {
            years.add(String.valueOf(i));
        }
        client = new HttpClient(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setUpViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(season + " " + year);
        getSupportActionBar().setSubtitle(SeasonUtil.getSubtitle(season));

        boolean reinit = (savedInstanceState != null);
        Log.d(LOG_TAG, "Reinit: " + reinit);
        initLoadDataForList(season, year, reinit);
    }

    private void setUpViewPager(ViewPager viewPager) {
        adapter = new SeasonPagerStateAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPagerSwiperListener());
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.season_sort), season);
        outState.putString(getString(R.string.year_sort), year);
        outState.putInt(getString(R.string.list_sort), sort);
        outState.putInt(getString(R.string.order_sort), asc);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation != ListOptions.SCREEN_ORIENTATION) {
            ListOptions.COLUMN_COUNT = ColumnUtil.calculateNoOfColumns(this);
            setUpViewPager(viewPager);
            ListOptions.SCREEN_ORIENTATION = orientation;
            if (tabLayout.getSelectedTabPosition() != currentSelectedTab) {
                tabLayout.getTabAt(currentSelectedTab).select();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeSettings();
    }

    private void writeSettings() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.list_sort), sort);
        editor.putInt(getString(R.string.order_sort), asc);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchAnime = menu.findItem(R.id.search_anime);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_anime) {
            return true;
        } else if (id == R.id.filter_anime) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            builder.setTitle(getString(R.string.sort_dialog_title));
            View dialogView = inflater.inflate(R.layout.spinner_sort_select_dialog, null);
            builder.setView(dialogView);

            Spinner seasonSpinner = (Spinner) dialogView.findViewById(R.id.sort_season_spinner);
            ArrayAdapter<CharSequence> seasonAdapter =
                    ArrayAdapter.createFromResource(this, R.array.season_array, R.layout.spinner_dropdown_item);
            seasonAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            seasonSpinner.setAdapter(seasonAdapter);

            int idx = 0;
            if (season.toLowerCase().equals("spring")) {
                idx = 1;
            } else if (season.toLowerCase().equals("summer")) {
                idx = 2;
            } else if (season.toLowerCase().equals("fall")) {
                idx = 3;
            }
            seasonSpinner.setSelection(idx);
            Log.d(LOG_TAG, "SelectSortDialog's SeasonSpinner idx: " + idx + "SEASON IS : " + season);

            Spinner yearSpinner = (Spinner) dialogView.findViewById(R.id.sort_year_spinner);
            ArrayAdapter<CharSequence> yearAdapter =
                    new ArrayAdapter<CharSequence>(this, R.layout.spinner_dropdown_item, years.toArray(new String[years.size()]));
            yearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            yearSpinner.setAdapter(yearAdapter);

            yearSpinner.setSelection(years.indexOf(year));

            Spinner sortSpinner = (Spinner) dialogView.findViewById(R.id.sort_spinner);
            ArrayAdapter<CharSequence> sortAdapter =
                    ArrayAdapter.createFromResource(this, R.array.sort_array, R.layout.spinner_dropdown_item);
            sortAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            sortSpinner.setAdapter(sortAdapter);

            Spinner orderSpinner = (Spinner) dialogView.findViewById(R.id.order_spinner);
            ArrayAdapter<CharSequence> orderAdapter =
                    ArrayAdapter.createFromResource(this, R.array.order_array, R.layout.spinner_dropdown_item);
            orderAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            orderSpinner.setAdapter(orderAdapter);

            sortSpinner.setSelection(sort);
            orderSpinner.setSelection(asc == -1 ? 1 : 0);

            builder.setPositiveButton("Apply", new SelectSortDialogListener(dialogView))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

            final Dialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static HttpClient getClient() {
        return client;
    }

    public void initLoadDataForList(String season, String year, boolean reinit) {
        if (getSupportLoaderManager().getLoader(0) == null) {
            getSupportLoaderManager().initLoader(0, null, new InitLoad(this, season, year, reinit));
            Log.d(LOG_TAG, "InitLoadDataForList Activated");
        } else {
            getSupportLoaderManager().restartLoader(0, null, new InitLoad(this, season, year, reinit));
            Log.d(LOG_TAG, "InitLoadDataForList Re-Initiated");
        }
    }

    //Todo: to merge with
    public void initLoadDataForSeasonList(String season, String year) {
        getSupportActionBar().setTitle(season + " " + year);
        getSupportActionBar().setSubtitle(SeasonUtil.getSubtitle(season));
        if (getSupportLoaderManager().getLoader(1) == null) {
            getSupportLoaderManager().initLoader(1, null, new SeasonLoad(this, season, year));
            Log.d(LOG_TAG, "initLoadDataForSeasonList Activated");
        } else {
            getSupportLoaderManager().restartLoader(1, null, new SeasonLoad(this, season, year));
            Log.d(LOG_TAG, "initLoadDataForSeasonList Re-Initiated");
        }
    }

    //Todo: to merge with
    public void reloadDataForSeasonListSorted() {
        if (getSupportLoaderManager().getLoader(4) == null) {
            getSupportLoaderManager().initLoader(4, null, new SortList(this)).forceLoad();
            Log.d(LOG_TAG, "reloadDataForSeasonListSorted Activated");
        } else {
            getSupportLoaderManager().restartLoader(4, null, new SortList(this)).forceLoad();
            Log.d(LOG_TAG, "reloadDataForSeasonListSorted Re-Initiated");
        }
    }

    @Override
    public void onAllAnimeFragmentInteraction(Anime item) {
    }

    @Override
    public void onMovieAnimeFragmentInteraction(Anime item) {
    }

    @Override
    public void onTVAnimeFragmentInteraction(Anime item) {
    }

    /**
     * Loading for all
     */
    private class InitLoad implements LoaderManager.LoaderCallbacks<AnimeList> {

        private Context context;
        private String season;
        private String year;
        private boolean reInit;

        public InitLoad(Context context, String season, String year, boolean reInit) {
            this.context = context;
            this.season = season;
            this.year = year;
            this.reInit = reInit;
        }

        @Override
        public Loader<AnimeList> onCreateLoader(int id, Bundle args) {
            Log.d(LOG_TAG, "On CreateLoader with " + "Season: " + season + " Year: " + year);
            return new AnimeSeasonLoader(context, season, year, sort, asc);
        }

        @Override
        public void onLoadFinished(Loader<AnimeList> loader, AnimeList data) {
            if (data == null) {
                noInternet = true;
                Log.d(LOG_TAG, "There is no data onLoadFinished");
            } else {
                Log.d(LOG_TAG, "InitLoad Initiated in MainActivity");
                if (noInternet) {
                    noInternet = false;
                }
                if ((season.toLowerCase() + " " + year.toLowerCase())
                        .equals(getSupportActionBar().getTitle().toString().toLowerCase())) {
                    ListContent.setList(data);
                    Log.d(LOG_TAG, "AnimeList Data is: " + data);
                    if (adapter.getRegisteredFragment(0) != null) {
                        AllAnimeFragment all = (AllAnimeFragment) adapter.getRegisteredFragment(0);
                        all.updateList();
                    }
                    if (adapter.getRegisteredFragment(1) != null) {
                        MovieAnimeFragment movie = (MovieAnimeFragment) adapter.getRegisteredFragment(1);
                        movie.updateList();
                    }
                    if (adapter.getRegisteredFragment(2) != null) {
                        TVAnimeFragment tv = (TVAnimeFragment) adapter.getRegisteredFragment(2);
                        tv.updateList();
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<AnimeList> loader) {
        }
    }

    /**
     * Loading for season
     */
    private class SeasonLoad implements LoaderManager.LoaderCallbacks<AnimeList> {
        private Context context;
        private String season;
        private String year;

        public SeasonLoad(Context context, String season, String year) {
            this.context = context;
            this.season = season;
            this.year = year;
        }

        @Override
        public Loader<AnimeList> onCreateLoader(int i, Bundle bundle) {
            return new AnimeSeasonLoader(context, season, year, sort, asc);
        }

        @Override
        public void onLoadFinished(Loader<AnimeList> loader, AnimeList data) {
            if (data == null) {
                noInternet = true;
            } else {
                Log.d(LOG_TAG, "SeasonLoad Initiated in MainActivity");
                if (noInternet) {
                    Toast.makeText(context, "There's no Internet Connection", Toast.LENGTH_SHORT).show();
                    noInternet = false;
                }
                if ((season.toLowerCase() + " "  + year.toLowerCase()).equals(getSupportActionBar().getTitle().toString().toLowerCase())) {
                    ListContent.setList(data);
                    if (adapter.getRegisteredFragment(0) != null) {
                        AllAnimeFragment all = (AllAnimeFragment) adapter.getRegisteredFragment(0);
                        all.updateList();
                    }
                    if (adapter.getRegisteredFragment(1) != null) {
                        MovieAnimeFragment movie = (MovieAnimeFragment) adapter.getRegisteredFragment(1);
                        movie.updateList();
                    }
                    if (adapter.getRegisteredFragment(2) != null) {
                        TVAnimeFragment tv = (TVAnimeFragment) adapter.getRegisteredFragment(2);
                        tv.updateList();
                    }
                }

            }
        }

        @Override
        public void onLoaderReset(Loader<AnimeList> loader) {

        }
    }

    /**
     * Sort list and reloads
     */
    private class SortList implements LoaderManager.LoaderCallbacks<AnimeList> {

        private Context context;

        public SortList(Context context) {
            this.context = context;
        }

        @Override
        public Loader<AnimeList> onCreateLoader(int id, Bundle args) {
            return new AnimeSortLoader(context, sort, asc);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<AnimeList> loader, AnimeList data) {
            if (data != null) {
                if (noInternet) {
                    Toast.makeText(context, "There's no internet connection", Toast.LENGTH_SHORT).show();
                }
                Log.d(LOG_TAG, "SortList Initiated in MainActivity");
                if ((season.toLowerCase() + " " + year.toLowerCase()).equals(getSupportActionBar().getTitle().toString().toLowerCase())) {
                    ListContent.setList(data);
                    if (adapter.getRegisteredFragment(0) != null) {
                        AllAnimeFragment all = (AllAnimeFragment) adapter.getRegisteredFragment(0);
                        all.reloadList();
                    }
                    if (adapter.getRegisteredFragment(1) != null) {
                        MovieAnimeFragment movie = (MovieAnimeFragment) adapter.getRegisteredFragment(1);
                        movie.reloadList();
                    }
                    if (adapter.getRegisteredFragment(2) != null) {
                        TVAnimeFragment tv = (TVAnimeFragment) adapter.getRegisteredFragment(2);
                        tv.reloadList();
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<AnimeList> loader) {

        }
    }

    private class SelectSortDialogListener implements DialogInterface.OnClickListener {

        View dialogView;

        public  SelectSortDialogListener(View v) {
            dialogView = v;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Spinner seasonSpinner = (Spinner) dialogView.findViewById(R.id.sort_season_spinner);
            Spinner yearSpinner = (Spinner) dialogView.findViewById(R.id.sort_year_spinner);
            Spinner sortSpinner = (Spinner) dialogView.findViewById(R.id.sort_spinner);
            Spinner orderSpinner = (Spinner) dialogView.findViewById(R.id.order_spinner);

            if (sortSpinner.getSelectedItemPosition() != sort ||
                    (orderSpinner.getSelectedItemPosition()  == 0 ? 1 : -1) != asc) {
                sort = sortSpinner.getSelectedItemPosition();
                asc = orderSpinner.getSelectedItemPosition() == 0 ? 1 : -1;
                reloadDataForSeasonListSorted();
                Log.d(LOG_TAG, "SelectSortDialogListener activated for sort & asc changes");
            } else if (!seasonSpinner.getSelectedItem().toString().toLowerCase().equals(season.toLowerCase()) ||
                    !yearSpinner.getSelectedItem().toString().toLowerCase().equals(year.toLowerCase())) {
                initLoadDataForSeasonList(seasonSpinner.getSelectedItem().toString(), yearSpinner.getSelectedItem().toString());
                ListContent.setCurrentYear(year);
                ListContent.setCurrentSeason(season);
                year = yearSpinner.getSelectedItem().toString();
                season = seasonSpinner.getSelectedItem().toString();
                Log.d(LOG_TAG, "SelectSortDialogListener activated for season & year changes");
            }
        }
    }

    private class ViewPagerSwiperListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentSelectedTab = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
