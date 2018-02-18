package com.rebecasarai.mysoulmate.Views.Adapters;

/**
 * Created by rebecagonzalez on 10/2/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rebecasarai.mysoulmate.Views.Fragments.CameraFragment;
import com.rebecasarai.mysoulmate.Views.Fragments.DashboardFragment;
import com.rebecasarai.mysoulmate.Views.Fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int NUM_PAGES = 3;

    private static DashboardFragment sDashboardInstance;
    private static CameraFragment sCameraInstance;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new DashboardFragment();

        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}