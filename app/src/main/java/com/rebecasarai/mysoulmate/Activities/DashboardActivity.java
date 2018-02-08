package com.rebecasarai.mysoulmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotType;
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

        setUpRecycler();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //setUpRecycler();
    }

    private void setUpRecycler() {
        mRecyclerView = findViewById(R.id.recyclerViewImages);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Query query = FirebaseDatabase.getInstance().getReference("users").child("j0NVPFX8dibmaRNtBbdFNaU9dt52");

        final FirebaseRecyclerOptions<Screenshot> options = new FirebaseRecyclerOptions.Builder<Screenshot>()
                .setQuery(query, Screenshot.class)
                .build();
        mPhotoAdapter = new PhotoAdapter(options, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPhotoAdapter);

        //TODO:Que muestre algo

            //Otra forma de hacerlo es escuchando cambios en la base de datos
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Screenshot image = postSnapshot.getValue(Screenshot.class);

                    mList.add(image);
                }*/


                Toast.makeText(DashboardActivity.this, "prueba", Toast.LENGTH_SHORT).show();

                mPhotoAdapter = new PhotoAdapter(options, new RecyclerItemClickListener() {

                    @Override
                    public void OnItemClick(int position, View view) {

                    }
                });

                mRecyclerView.setAdapter(mPhotoAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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