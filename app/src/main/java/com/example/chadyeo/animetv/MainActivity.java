package com.example.chadyeo.animetv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
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

import com.example.chadyeo.animetv.api.HttpClient;

public class MainActivity extends AppCompatActivity {

    private static HttpClient client;

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

        if (id == R.id.search_anime) {
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
}
