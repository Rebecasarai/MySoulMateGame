package com.rebecasarai.mysoulmate.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.Adapters.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements RecyclerItemClickListener {

    private ImageView mLastSoulMateImage;
    private StorageReference storageRef;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;
    private View mRootView;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLastSoulMateImage = mRootView.findViewById(R.id.lastSoulMatePreview);

        return mRootView;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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


    @Override
    public void OnItemClick(int position, View view) {

    }



}
