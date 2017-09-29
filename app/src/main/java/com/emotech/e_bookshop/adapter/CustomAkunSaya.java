package com.emotech.e_bookshop.adapter;

/**
 * Created by EmotEch on 27/09/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emotech.e_bookshop.fragment.DetailPribadiFragment;
import com.emotech.e_bookshop.fragment.UbahPassFragment;

public class CustomAkunSaya extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;

    public CustomAkunSaya(FragmentManager fm) {
        super(fm);
    }

    @Override

    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UbahPassFragment();
            case 1:
                return new DetailPribadiFragment();
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
                return "Ubah Password";
            case 1:
                return "Detail Pribadi";
        }
        return null;
    }
}