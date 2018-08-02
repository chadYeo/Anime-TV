package com.example.chadyeo.animetv.utils;


import android.content.Context;
import android.util.DisplayMetrics;

public class ColumnUtil {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 136); // 120dp + 16dp for both margins
        return noOfColumns;
    }
}
