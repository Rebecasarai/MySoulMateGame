package com.rebecasarai.mysoulmate.Fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Views.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements RecyclerItemClickListener {

    private static final String TAG=ProfileFragment.class.getSimpleName();

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
        setLastSoulmate();

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

    public void setLastSoulmate(){
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getValue(Screenshot.class).getImageURL());
                //Picasso.with(getView().getContext()).load(dataSnapshot.getValue()+"").into(mLastSoulMateImage);
                Picasso.with(getView().getContext()).load(dataSnapshot.getValue(Screenshot.class).getImageURL()).placeholder(R.drawable.ic_launcher_background).fit().into(mLastSoulMateImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
