package com.rebecasarai.mysoulmate.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rebecasarai.mysoulmate.Fragments.ProfileFragment;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.R;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.squareup.picasso.Picasso;

public class NewSoulMateActivity extends AppCompatActivity {
    private static final String TAG = NewSoulMateActivity.class.getSimpleName();

    private ImageView mImageNewSoulMate;
    private View mRootView;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_soul_mate);

        mImageNewSoulMate = mRootView.findViewById(R.id.imageNewSoulMate);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        setLastSoulmate();

        mViewModel.getLastSoulMate().observe(null, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                mImageNewSoulMate.setImageBitmap(bitmap);
            }
        });
    }


    public void setLastSoulmate() {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if (dataSnapshot.getChildrenCount() != 1) {
                Screenshot screenshot = dataSnapshot.getChildren().iterator().next().getValue(Screenshot.class);

                mViewModel.setLastSoulMate(stringToBitMap(screenshot.getImageURL()));
                //Picasso.with(getView().getContext()).load(screenshot.getImageURL()).placeholder(R.drawable.ic_launcher_foreground).fit().into(mLastSoulMateImage);
                //Log.d(TAG,screenshot.getImageURL()+"");
                //Log.d(TAG,dataSnapshot.getChildrenCount()+"");

                // }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getView().getContext(), R.string.content_no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
