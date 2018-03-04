package com.rebecasarai.mysoulmate.Views.Fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.squareup.picasso.Picasso;



/**
 * Clase View Model en la que se maneja el ultimo bitmap del alma gemela tomada,i activar el CameraFragment dependiendo si se encuentra en dicho fragmento o no
 */
public class NewSoulMateFragment extends Fragment {
    private ImageView mImageNewSoulMate;
    private View mRootView;
    private MainViewModel mViewModel;
    private static final String TAG = NewSoulMateFragment.class.getSimpleName();


    public NewSoulMateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView= inflater.inflate(R.layout.fragment_new_soul_mate, container, false);
        mImageNewSoulMate = (ImageView) mRootView.findViewById(R.id.imageNewSoulMate);
        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        ImageButton shareButton =  (ImageButton) mRootView.findViewById(R.id.shareBtn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScreenshot();
            }
        });
        mViewModel.getLastSoulMate().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                if(bitmap!=null){
                    Log.d(TAG, "Bitmap de NewSoulMate: "+bitmap.toString());
                     mImageNewSoulMate.setImageBitmap(bitmap);
                }else{
                    setLastSoulmate();
                }
            }
        });
        return mRootView;
    }


    public void setLastSoulmate() {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                if (dataSnapshot.getChildrenCount() == 1) {
                    Screenshot screenshot = dataSnapshot.getChildren().iterator().next().getValue(Screenshot.class);
                    Picasso.with(getContext()).load(screenshot.getImageURL()).fit().into(mImageNewSoulMate);
                    Log.d(TAG,screenshot.getImageURL()+"");
                    Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                    /*final SmallBangView smallBangImage = mRootView.findViewById(R.id.smallBangImageNew);
                    smallBangImage.likeAnimation();*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getView().getContext(), R.string.content_no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shareScreenshot() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, mViewModel.getUrlDeUltimoBitmap());//uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }



}
