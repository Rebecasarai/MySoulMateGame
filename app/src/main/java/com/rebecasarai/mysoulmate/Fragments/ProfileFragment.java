package com.rebecasarai.mysoulmate.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
    private SmallBangView mNotFinalsmallBangImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final SmallBangView like_heart = mRootView.findViewById(R.id.like_heart);
        mLastSoulMateImage = mRootView.findViewById(R.id.lastSoulMatePreview);
        like_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like_heart.isSelected()) {
                    like_heart.setSelected(false);
                } else {
                    like_heart.setSelected(true);
                    like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                }
            }
        });


        ImageView imgSmallHeart = (ImageView) mRootView.findViewById(R.id.imgSmallHeart);
        //mSmallBang.likeAnimation();
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void OnItemClick(int position, View view) {

    }

    public void setLastSoulmate() {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                if (dataSnapshot.getChildrenCount() == 1) {
                    Screenshot screenshot = dataSnapshot.getChildren().iterator().next().getValue(Screenshot.class);
                    Picasso.with(getView().getContext()).load(screenshot.getImageURL()).fit().into(mLastSoulMateImage);
                    Log.d(TAG,screenshot.getImageURL()+"");
                    Log.d(TAG,dataSnapshot.getChildrenCount()+"");

                    YoYo.with(Techniques.Landing)
                            .duration(800)
                            .playOn(mRootView.findViewById(R.id.lastSoulMatePreview));
                    final SmallBangView like_heart = mRootView.findViewById(R.id.like_heart);
                   like_heart.likeAnimation();
               }
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
