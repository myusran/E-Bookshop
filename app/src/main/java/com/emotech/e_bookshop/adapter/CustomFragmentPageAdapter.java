package com.emotech.e_bookshop.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emotech.e_bookshop.fragment.BestFragment;
import com.emotech.e_bookshop.fragment.BukuFragment;

/**
 * Created by muham_000 on 12/09/2017.
 */

public class CustomFragmentPageAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 2;

    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new BukuFragment();
            case 1:
                return new BestFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Buku Baru";
            case 1:
                return "Best Seller";
        }
        return null;
    }
}
