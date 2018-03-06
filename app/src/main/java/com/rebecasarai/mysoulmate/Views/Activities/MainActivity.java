package com.rebecasarai.mysoulmate.Views.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.rebecasarai.mysoulmate.Views.Adapters.ViewPagerAdapter;

/**
 * Actividad central y en la que se entra luego de iniciar sesión. Se crea un ViewPager para mostrar los fragmentso y un menu inferior.
 * Se comienza en el fragmento de Camara.
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private MenuItem mPrevMenuItem;
    private BottomNavigationView mNavigationView;
    private MainViewModel mViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.setActivarCameraFargment(true);

        mNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mPager = (ViewPager) findViewById(R.id.framelayout);

        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);
        mPager.setCurrentItem(1);

        mViewModel.getLastSoulMate().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                if(bitmap!=null){
                    Log.d(TAG,"bitmap no nulo, fragment: "+mPager.getCurrentItem());
                    mPager.setCurrentItem(2);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

            // Si el usuario está mirando actualmente el primer fragmento, permite que exista el desplazamiento de retroceso.
            // Este llama a finish () en esta actividad y muestra el back stack.

            if (mPager.getCurrentItem() != 0) {
                // De lo contrario, selecciona el fragmento anterior.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * AL seleccionar un item, actualiza el menu de navegación
     * @param position
     */
    @Override
    public void onPageSelected(int position) {

        if (mPrevMenuItem != null) {
            mPrevMenuItem.setChecked(false);
        } else {
            mNavigationView.getMenu().getItem(0).setChecked(false);
        }

        mNavigationView.getMenu().getItem(position).setChecked(true);
        mPrevMenuItem = mNavigationView.getMenu().getItem(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * Metodo en el que dependiendo de que fragmento seleccione, actualiza el pager
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mPager.setCurrentItem(0);
                    mViewModel.setActivarCameraFargment(false);
                    return true;

                case R.id.navigation_dashboard:
                    mPager.setCurrentItem(1);
                    mViewModel.setActivarCameraFargment(true);
                    return true;

                case R.id.navigation_notifications:
                    mPager.setCurrentItem(2);
                    mViewModel.setActivarCameraFargment(false);
                    return true;

            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){


        }
        return super.onOptionsItemSelected(item);
    }

}
