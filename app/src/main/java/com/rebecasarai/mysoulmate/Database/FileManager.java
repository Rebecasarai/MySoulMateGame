package com.rebecasarai.mysoulmate.Database;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FileManager {

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "users";


    private static FirebaseStorage mFirebaseStorage;

    static {
        if (mFirebaseStorage == null) mFirebaseStorage = FirebaseStorage.getInstance();
    }

    public static FirebaseStorage getStorage() {
        return mFirebaseStorage;
    }

    public static void uploadScreenshot(@NonNull final FirebaseAuth auth, @NonNull Bitmap bitmap, final OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        final String uid = auth.getCurrentUser().getUid();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String path = "users/" + uid + "/" + System.currentTimeMillis() + ".jpeg";
        byte[] data = baos.toByteArray();
        OnSuccessListener<UploadTask.TaskSnapshot> onUploadListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageUrl = taskSnapshot.getDownloadUrl().toString();
                //updateUserScreenshots(auth, imageUrl);
            }
        };
        uploadFile(path, data, onUploadListener, onFailureListener);
    }

    private static void uploadFile(@NonNull String path, @NonNull byte[] data, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        UploadTask uploadTask = mFirebaseStorage.getReference(path).putBytes(data);
        if (onSuccessListener != null) uploadTask.addOnSuccessListener(onSuccessListener);
        if (onFailureListener != null) uploadTask.addOnFailureListener(onFailureListener);
    }

    private static void updateUserScreenshots(FirebaseAuth auth, String imageUrl) {
        DatabaseReference screenshotRef = FirebaseDatabase.getInstance().getReference("users").child(auth.getUid()).child("screenshots").push();
        screenshotRef.setValue(imageUrl);
    }

    public static DatabaseReference getFilesByUser(@NonNull final FirebaseAuth auth){
//        final String uid = auth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        return databaseReference;
      }

}