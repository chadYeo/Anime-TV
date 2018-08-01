package com.example.chadyeo.animetv.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.chadyeo.animetv.fragments.AllAnimeFragment;
import com.example.chadyeo.animetv.utils.ListOptions;

public class SeasonPagerStateAdapter extends SmartFragmentStatePagerAdapter {

    private static int NUM_ITEMS = 3;
    private static boolean[] scrolling = new boolean[3];

    public SeasonPagerStateAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AllAnimeFragment.newInstance(ListOptions.COLUMN_COUNT);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
