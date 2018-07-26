package com.example.chadyeo.animetv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
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

import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.AnimeList;
import com.example.chadyeo.animetv.api.HttpClient;
import com.example.chadyeo.animetv.fragments.MainFragment;
import com.example.chadyeo.animetv.loaders.AnimeSeasonLoader;
import com.example.chadyeo.animetv.utils.ListContent;
import com.example.chadyeo.animetv.utils.SeasonUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static HttpClient client;
    String season;
    String year;
    ArrayList<String> years = new ArrayList<>();

    boolean loaded = false;
    boolean noInternet = false;
    int sort = 0;
    int asc = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        sort = sharedPreferences.getInt(getString(R.string.list_sort), 0);
        asc = sharedPreferences.getInt(getString(R.string.order_sort), -1);

        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

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

        getSupportActionBar().setTitle(season + " " + year);
        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#00BFA5'>" + SeasonUtil.getSubtitle(season) + "</font>"));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SEASON", season);
        outState.putString("YEAR", year);
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

        if (id == R.id.preArrow_season) {
            if (loaded && !(season.toLowerCase().equals("winter") && (Integer.valueOf(year) == 1951))) {
                String[] a = SeasonUtil.prevSeason(season, year);
                //loadDataForSeasonList(a[0], a[1]);
                season = a[0];
                year = a[1];
                ListContent.setCurrentSeason(season);
                ListContent.setCurrentYear(year);
            }
            return true;
        } else if (id == R.id.nextArrow_season) {
            if (loaded && !(season.toLowerCase().equals("fall") && (Integer.valueOf(year) == Calendar.getInstance().get(Calendar.YEAR)+1))) {
                String[] a = SeasonUtil.nextSeason(season, year);
                //loadDataForSeasonList(a[0], a[1]);
                season = a[0];
                year = a[1];
                ListContent.setCurrentSeason(season);
                ListContent.setCurrentYear(year);
            }
        } else if (id == R.id.search_anime) {
            return true;
        } else if (id == R.id.filter_anime) {
            SelectSortDialogListener();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static HttpClient getClient() {
        return client;
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
                        (orderSpinner.getSelectedItemPosition()  == 0 ? -1 : 1) != asc) {
                    sort = sortSpinner.getSelectedItemPosition();
                    asc = orderSpinner.getSelectedItemPosition() == 0 ? -1 : 1;

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
            return new AnimeSeasonLoader(context, season, year, sort, asc);
        }

        @Override
        public void onLoadFinished(Loader<AnimeList> loader, AnimeList data) {
            if (data == null) {
                noInternet = true;
            } else {
                if (noInternet) {
                    noInternet = false;
                }
                if ((season.toLowerCase() + " " + year.toLowerCase()).equals(getSupportActionBar().getTitle().toString().toLowerCase())) {
                    ListContent.setList(data);
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
        private boolean reinit;

        public SeasonLoad(Context context, String season, String year, boolean reinit) {
            this.context = context;
            this.season = season;
            this.year = year;
            this.reinit = reinit;
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
                }

            }
        }

        @Override
        public void onLoaderReset(Loader<AnimeList> loader) {

        }
    }
}
