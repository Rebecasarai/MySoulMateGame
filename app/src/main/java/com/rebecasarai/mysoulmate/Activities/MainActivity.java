package com.rebecasarai.mysoulmate.Activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.TextScale;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.MenuItem;

import com.rebecasarai.mysoulmate.Fragments.CameraFragment;
import com.rebecasarai.mysoulmate.Fragments.BlankFragment;
import com.rebecasarai.mysoulmate.Fragments.DashboardFragment;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    /**
     * The number of pages or fragments
     */
    private static final int NUM_PAGES = 3;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    MenuItem prevMenuItem;
    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        DashboardFragment fragment = new DashboardFragment();
        fragmentTransaction.add(R.id.framelayout, fragment);
        fragmentTransaction.commit();  */

        mPager = (ViewPager) findViewById(R.id.framelayout);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //DashboardNavigationController.setNavigationTab(position);

                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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




    /*--------------  Page Listener  -------------*/

    /**
     *
     *
     */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

       //DashboardNavigationController.setNavigationTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Para emular animaciones del bottom menu
     */
    public class DashboardNavigationController {

        private BottomNavigationView bottomNavigationView;
        private final TransitionSet transitionSet;
        private int activeTabPosition = 0;
        private static final long ACTIVE_ANIMATION_DURATION_MS = 115L;

        public DashboardNavigationController(BottomNavigationView bottomNavigationView) {
            this.bottomNavigationView = bottomNavigationView;

            //XXX copy of BottomNavigationAnimationHelperIcs transition set
            transitionSet = new AutoTransition();
            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
            transitionSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
            transitionSet.setInterpolator(new FastOutSlowInInterpolator());
            //TextScale textScale = new TextScale();
            //transitionSet.addTransition(textScale);
        }

        public void setNavigationTab(int position) {

            if (activeTabPosition == position) {
                return;
            } else {
                activeTabPosition = position;
            }

            //Timber.v("setNavigationTab: %s", position);

            TransitionManager.beginDelayedTransition(bottomNavigationView, transitionSet);

            bottomNavigationView.getMenu().getItem(position).setChecked(true);
        }
    }


     /*--------------  End Page Listener  -------------*/




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Navegación
     */
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
