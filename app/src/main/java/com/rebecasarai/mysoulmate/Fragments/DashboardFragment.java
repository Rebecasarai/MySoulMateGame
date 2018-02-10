package com.rebecasarai.mysoulmate.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rebecasarai.mysoulmate.Activities.FindSoulMateActivity;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements RecyclerItemClickListener {

    // Creating RecyclerView.
    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private View view;
    private Context context;
    private LinearLayoutManager mLayoutManager;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        LayoutInflater lf = getActivity().getLayoutInflater();

        view =  lf.inflate(R.layout.fragment_dashboard, container, false);

        setUpRecycler(view);

        if(mPhotoAdapter!= null){
            mPhotoAdapter.startListening();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onStop() {
        super.onStop();

        if(mPhotoAdapter!=null){
            mPhotoAdapter.stopListening();
            mPhotoAdapter = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //setUpRecycler();
        if(mPhotoAdapter!= null){
            mPhotoAdapter.startListening();
        }
    }

    private void setUpRecycler(View view) {

        mRecyclerView = view.findViewById(R.id.recycler);
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots");

        final FirebaseRecyclerOptions<Screenshot> options = new FirebaseRecyclerOptions.Builder<Screenshot>()
                .setQuery(query, Screenshot.class)
                .build();

        mPhotoAdapter = new PhotoAdapter(options, this);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        //Log.v("debugMode", "stopped?");
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mPhotoAdapter);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    Intent intent = new Intent(getContext(), FindSoulMateActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };


    @Override
    public void OnItemClick(int position, View view) {

    }
}
