package com.rebecasarai.mysoulmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements RecyclerItemClickListener {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private ArrayList<Screenshot> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpRecycler();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //setUpRecycler();
    }

    private void setUpRecycler() {

        mRecyclerView = findViewById(R.id.recyclerViewImages);
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots");

        final FirebaseRecyclerOptions<Screenshot> options = new FirebaseRecyclerOptions.Builder<Screenshot>()
                .setQuery(query, Screenshot.class)
                .build();

        mPhotoAdapter = new PhotoAdapter(options, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPhotoAdapter);
    }


    @Override
    public void OnItemClick(int position, View view) {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:


                    return true;
                case R.id.navigation_dashboard:

                    Intent intent = new Intent(getApplicationContext(), FindSoulMateActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

}