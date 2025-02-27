package com.rebecasarai.mysoulmate.Views.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.Adapters.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;

/**
 * Fragmento en el que se muestran todas las almas gemelas capturadas hasta el momento
 */
public class DashboardFragment extends Fragment implements RecyclerItemClickListener {

    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private View mRootView;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //setUpRecycler();
        return mRootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTheme(R.style.AppTheme);
        setUpRecycler();
        if (mPhotoAdapter != null) {
            mPhotoAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPhotoAdapter != null) {
            mPhotoAdapter.stopListening();
            mPhotoAdapter = null;
        }
    }

    /**
     * Crea el recycler View para mostrar las imagenes con vistas CardView
     */
    private void setUpRecycler() {
        mRecyclerView = getView().findViewById(R.id.recycler);
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots");

        final FirebaseRecyclerOptions<Screenshot> options = new FirebaseRecyclerOptions.Builder<Screenshot>()
                .setQuery(query, Screenshot.class)
                .build();

        mPhotoAdapter = new PhotoAdapter(options, this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(mPhotoAdapter);
    }


    @Override
    public void OnItemClick(int position, View view) {

    }



}
