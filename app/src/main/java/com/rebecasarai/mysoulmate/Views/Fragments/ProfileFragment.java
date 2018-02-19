package com.rebecasarai.mysoulmate.Views.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rebecasarai.mysoulmate.Views.Activities.LoginActivity;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.Utils.ScreenshotType;
import com.rebecasarai.mysoulmate.Utils.ScreenshotUtils;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.rebecasarai.mysoulmate.Views.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import xyz.hanks.library.bang.SmallBangView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements RecyclerItemClickListener, View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private ImageView mLastSoulMateImage;
    private View mRootView;
    private MainViewModel mViewModel;
    private SmallBangView mNotFinalsmallBangImage;
    private SmallBangView mlike_heart;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final SmallBangView like_heart = mRootView.findViewById(R.id.like_heart);
        mlike_heart = mRootView.findViewById(R.id.like_heart);
        TextView cerrarSesionText = mRootView.findViewById(R.id.cerrarSesion);
        cerrarSesionText.setOnClickListener(this);
        mLastSoulMateImage = mRootView.findViewById(R.id.lastSoulMatePreview);
        like_heart.setOnClickListener(this);

        ImageView imgSmallHeart = (ImageView) mRootView.findViewById(R.id.imgSmallHeart);
        ImageView shareBtn = (ImageButton) mRootView.findViewById(R.id.share);
        shareBtn.setOnClickListener(this);
        //mSmallBang.likeAnimation();
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

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
                    Picasso.with(getContext()).load(screenshot.getImageURL()).fit().into(mLastSoulMateImage);
                    Log.d(TAG,screenshot.getImageURL()+"");
                    Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                    YoYo.with(Techniques.Landing)
                            .duration(800)
                            .playOn(mRootView.findViewById(R.id.lastSoulMatePreview));
                    //final SmallBangView like_heart = mRootView.findViewById(R.id.like_heart);
                    if(mlike_heart.getScaleX()>0){
                    mlike_heart.likeAnimation();
                    }

                   mViewModel.setLastSoulMate(StringToBitMap(screenshot.getImageURL()+""));
             }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getView().getContext(), R.string.content_no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        Bitmap bitmap = null;
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        }catch(Exception e){
            e.getMessage();
        }
        return bitmap;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.like_heart:
                if (mlike_heart.isSelected()) {
                    mlike_heart.setSelected(false);
                } else {
                    mlike_heart.setSelected(true);
                    mlike_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                }
                break;

            case R.id.share:
                shareScreenshot(mViewModel.getLastSoulMate().getValue(),ScreenshotType.CUSTOM.FULL);
                break;

            case R.id.cerrarSesion:
                cerrarSesion();
                break;
        }
    }


    private void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(getView().getContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        getActivity().finish();
    }

    private void shareScreenshot(Bitmap bitmap, ScreenshotType screenshotType) {
        File saveFile = ScreenshotUtils.getMainDirectoryName(getView().getContext());
        File screenShotFile = ScreenshotUtils.store(bitmap, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
        Uri uri = Uri.fromFile(screenShotFile);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }



}
