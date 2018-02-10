package com.rebecasarai.mysoulmate.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private MenuItem mPrevMenuItem;
    private BottomNavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mPager = (ViewPager) findViewById(R.id.framelayout);

        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
        mPager.setCurrentItem(1);

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPrevMenuItem != null) {
            mPrevMenuItem.setChecked(false);
        } else {
            mNavigationView.getMenu().getItem(0).setChecked(false);
        }
        Log.d(TAG, "onPageSelected: " + position);
        mNavigationView.getMenu().getItem(position).setChecked(true);
        mPrevMenuItem = mNavigationView.getMenu().getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mPager.setCurrentItem(0);
                    return true;

                case R.id.navigation_dashboard:
                    mPager.setCurrentItem(1);
                    return true;

                case R.id.navigation_notifications:
                    mPager.setCurrentItem(2);
                    return true;

            }
            return false;
        }
    };
}
