package com.rebecasarai.mysoulmate.Views;

/**
 * Created by rebecagonzalez on 10/2/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rebecasarai.mysoulmate.Fragments.CameraFragment;
import com.rebecasarai.mysoulmate.Fragments.DashboardFragment;

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
                return sDashboardInstance == null ? sDashboardInstance = new DashboardFragment() : sDashboardInstance;
            case 1:
                return sCameraInstance == null ? sCameraInstance = new CameraFragment() : sCameraInstance;
            case 2:
                return sDashboardInstance == null ? sDashboardInstance = new DashboardFragment() : sDashboardInstance;
            default:
                return sDashboardInstance == null ? sDashboardInstance = new DashboardFragment() : sDashboardInstance;

        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}