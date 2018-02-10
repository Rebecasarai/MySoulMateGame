package com.rebecasarai.mysoulmate.Repository;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rebecasarai.mysoulmate.Database.FileManager;
import com.rebecasarai.mysoulmate.Models.Screenshot;
import com.rebecasarai.mysoulmate.Screenshot.ScreenshotUtils;

import java.io.File;

/**
 * Created by macbookpro on 28/1/18.
 */

public class UserRepository {



    public void getSoulMatesImages(){
        Query query = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("screenshots");

        final FirebaseRecyclerOptions<Screenshot> options = new FirebaseRecyclerOptions.Builder<Screenshot>()
                .setQuery(query, Screenshot.class)
                .build();
    }

    public UserRepository() {

    }


    public static void uploadScreenShot(final Activity activity, Bitmap bitmap){

        FileManager.uploadScreenshot(FirebaseAuth.getInstance(), bitmap, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO: maybe say something else
                Toast.makeText(activity, "Image uploaded.", Toast.LENGTH_SHORT).show();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO: treat
                Toast.makeText(activity, "Uploading failed.", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
