package com.example.chadyeo.animetv;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import com.example.chadyeo.animetv.loaders.AnimeSeasonReload;
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

    String season;
    String year;
    SeasonPagerStateAdapter adapter;

    ArrayList<String> years = new ArrayList<>();
    int sort = 0;
    int asc = -1;
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
        asc = sharedPreferences.getInt(getString(R.string.order_sort), -1);
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
            this.season = savedInstanceState.getString("SEASON");
            this.year = savedInstanceState.getString("YEAR");
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
        getSupportActionBar().setSubtitle(Html.fromHtml(SeasonUtil.getSubtitle(season)));

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
        outState.putString("SEASON", season);
        outState.putString("YEAR", year);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.search_anime) {
            return true;
        } else if (id == R.id.filter_anime) {
            SelectSortDialogListener();
            return true;
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

    public void initLoadDataForSeasonList(String season, String year) {
        getSupportActionBar().setTitle(season + " " + year);
        getSupportActionBar().setSubtitle(SeasonUtil.getSubtitle(season));
        if (getSupportLoaderManager().getLoader(1) == null) {
            getSupportLoaderManager().initLoader(1, null, new SeasonLoad(this, season, year));
        } else {
            getSupportLoaderManager().restartLoader(1, null, new SeasonLoad(this, season, year));
        }
    }

    public void reloadDataForSeasonList(String season, String year) {
        if (getSupportLoaderManager().getLoader(3) == null) {
            getSupportLoaderManager().initLoader(3, null, new ReloadList(this, season, year)).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(3, null, new ReloadList(this, season, year)).forceLoad();
        }
    }

    public void reloadDataForSeasonListSorted() {
        if (getSupportLoaderManager().getLoader(4) == null) {
            getSupportLoaderManager().initLoader(4, null, new SortList(this)).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(4, null, new SortList(this)).forceLoad();
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
            ViewPager view = (ViewPager) findViewById(R.id.viewPager);
            if (data == null) {
                noInternet = true;
                Log.d(LOG_TAG, "There is no data onLoadFinished");
            } else {
                if (noInternet) {
                    noInternet = false;
                }
                if ((season.toLowerCase() + " " + year.toLowerCase()).equals(getSupportActionBar().getTitle().toString().toLowerCase())) {
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
     * Re-Loading list
     */
    private class ReloadList implements LoaderManager.LoaderCallbacks<AnimeList> {

        private Context context;
        private String season;
        private String year;

        public ReloadList(Context context, String season, String year) {
            this.context = context;
            this.season = season;
            this.year = year;
        }

        @NonNull
        @Override
        public Loader<AnimeList> onCreateLoader(int id, @Nullable Bundle args) {
            return new AnimeSeasonReload(context, season, year, sort, asc);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<AnimeList> loader, AnimeList data) {
            if (data == null) {
                noInternet = true;
                Toast.makeText(context, "There's no interent connection", Toast.LENGTH_SHORT).show();
            }
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

        @Override
        public void onLoaderReset(@NonNull Loader<AnimeList> loader) {

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
                    Toast.makeText(context, "There's no interent connection", Toast.LENGTH_SHORT).show();
                }
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

    /**
     * Sort Dialog Menu
     */
    private void SelectSortDialogListener() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle(getString(R.string.sort_dialog_title));
        View dialogView = inflater.inflate(R.layout.spinner_sort_select_dialog, null);
        builder.setView(dialogView);

        final Spinner seasonSpinner = (Spinner) dialogView.findViewById(R.id.sort_season_spinner);
        final ArrayAdapter<CharSequence> seasonAdapter =
                ArrayAdapter.createFromResource(this, R.array.season_array, R.layout.spinner_dropdown_item);
        seasonAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        seasonSpinner.setAdapter(seasonAdapter);

        int idx = 0;
        if (season.toLowerCase().equals("spring")) idx = 1;
        else if (season.toLowerCase().equals("summer")) idx = 2;
        else if (season.toLowerCase().equals("fall")) idx = 3;
        seasonSpinner.setSelection(idx);

        final Spinner yearSpinner = (Spinner) dialogView.findViewById(R.id.sort_year_spinner);
        ArrayAdapter<CharSequence> yearAdapter =
                new ArrayAdapter<CharSequence>(this, R.layout.spinner_dropdown_item, years.toArray(new String[years.size()]));
        yearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setSelection(years.indexOf(year));

        final Spinner sortSpinner = (Spinner) dialogView.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortAdapter =
                ArrayAdapter.createFromResource(this, R.array.sort_array, R.layout.spinner_dropdown_item);
        sortAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        final Spinner orderSpinner = (Spinner) dialogView.findViewById(R.id.order_spinner);
        ArrayAdapter<CharSequence> orderAdapter =
                ArrayAdapter.createFromResource(this, R.array.order_array, R.layout.spinner_dropdown_item);
        orderAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderAdapter);

        sortSpinner.setSelection(sort);
        orderSpinner.setSelection(asc == -1 ? 0 : 1);

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (sortSpinner.getSelectedItemPosition() != sort ||
                        (orderSpinner.getSelectedItemPosition()  == 0 ? -1 : 1) != asc ||
                        !seasonSpinner.getSelectedItem().toString().toLowerCase().equals(season.toLowerCase()) ||
                        !yearSpinner.getSelectedItem().toString().toLowerCase().equals(year.toLowerCase())) {
                    year = yearSpinner.getSelectedItem().toString();
                    season = yearSpinner.getSelectedItem().toString();
                    sort = sortSpinner.getSelectedItemPosition();
                    asc = orderSpinner.getSelectedItemPosition() == 0 ? -1 : 1;

                    initLoadDataForSeasonList(seasonSpinner.getSelectedItem().toString(), yearSpinner.getSelectedItem().toString());

                    ListContent.setCurrentYear(year);
                    ListContent.setCurrentSeason(season);
                    reloadDataForSeasonListSorted();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Log.e("TESTING", "SHOWING");
        Dialog dialog = builder.create();
        dialog.show();
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
