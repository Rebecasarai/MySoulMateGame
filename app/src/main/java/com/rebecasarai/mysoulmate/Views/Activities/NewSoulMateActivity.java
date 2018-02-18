package com.rebecasarai.mysoulmate.Views.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.rebecasarai.mysoulmate.Utils.ScreenshotType;
import com.rebecasarai.mysoulmate.Utils.ScreenshotUtils;
import com.rebecasarai.mysoulmate.ViewModels.MainViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class NewSoulMateActivity extends AppCompatActivity {
    private static final String TAG = NewSoulMateActivity.class.getSimpleName();

    private ImageView mImageNewSoulMate;
    private View mRootView;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_soul_mate);
        mImageNewSoulMate = (ImageView) findViewById(R.id.imageNewSoulMate);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getLastSoulMate().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                mImageNewSoulMate.setImageBitmap(bitmap);
            }
        });
        setLastSoulmate();
        ImageButton shareButton =  (ImageButton) findViewById(R.id.shareBtn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScreenshot(mViewModel.getLastSoulMate().getValue(),ScreenshotType.FULL);
            }
        });
        mViewModel.getLastSoulMate().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {

            }
        });




    }


    public void setLastSoulmate() {
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots").orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getChildrenCount()+"");
               if (dataSnapshot.getChildrenCount() == 1) {
                    Screenshot screenshot = dataSnapshot.getChildren().iterator().next().getValue(Screenshot.class);
                    Picasso.with(getApplicationContext()).load(screenshot.getImageURL()).fit().into(mImageNewSoulMate);
                    Log.d(TAG,screenshot.getImageURL()+"");
                    Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                    //final SmallBangView smallBangImage = mRootView.findViewById(R.id.smallBangImageNew);
                    //smallBangImage.likeAnimation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getView().getContext(), R.string.content_no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void shareScreenshot(Bitmap bitmap, ScreenshotType screenshotType) {
        File saveFile = ScreenshotUtils.getMainDirectoryName(getApplicationContext());
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
