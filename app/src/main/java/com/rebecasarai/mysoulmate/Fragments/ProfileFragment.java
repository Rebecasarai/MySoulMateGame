package com.rebecasarai.mysoulmate.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.rebecasarai.mysoulmate.Views.Adapters.PhotoAdapter;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import xyz.hanks.library.bang.SmallBangView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements RecyclerItemClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private ImageView mLastSoulMateImage;
    private View mRootView;
    private MainViewModel mViewModel;
    private SmallBangView mSmallBang;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mLastSoulMateImage = mRootView.findViewById(R.id.lastSoulMatePreview);
        ImageView imgSmallHeart = (ImageView) getView().findViewById(R.id.imgSmallHeart);
        mSmallBang.likeAnimation();
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
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
        anotheranim();
        animation();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void OnItemClick(int position, View view) {

    }

    public void setLastSoulmate() {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if (dataSnapshot.getChildrenCount() != 1) {
                    Screenshot screenshot = dataSnapshot.getChildren().iterator().next().getValue(Screenshot.class);
                    Picasso.with(getView().getContext()).load(screenshot.getImageURL()).placeholder(R.drawable.ic_launcher_foreground).fit().into(mLastSoulMateImage);
                    Log.d(TAG,screenshot.getImageURL()+"");
                    Log.d(TAG,dataSnapshot.getChildrenCount()+"");

               // }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getView().getContext(), R.string.content_no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void animation(){
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        // Start animating the image
        final ImageView splash = mLastSoulMateImage;
        splash.startAnimation(anim);

        // Later.. stop the animation
        splash.setAnimation(null);
    }

    public void anotheranim(){
        TranslateAnimation animate = new TranslateAnimation(0, -mLastSoulMateImage.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        mLastSoulMateImage.startAnimation(animate);


    }
}
