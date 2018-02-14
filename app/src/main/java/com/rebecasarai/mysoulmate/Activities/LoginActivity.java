package com.rebecasarai.mysoulmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rebecasarai.mysoulmate.Models.User;
import com.rebecasarai.mysoulmate.R;

import java.util.Arrays;

/**
 * This is the entry class of the application.
 * It allows to log in with your Google account or email.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    private final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mCurrentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseLogin();
    }

    /**
     * This method logs the user in with Firebase.
     * It checks if a user is logged in or not.
     */
    private void firebaseLogin() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get the current firebase user.
                mCurrentFirebaseUser = firebaseAuth.getCurrentUser();

                // Check if the user is logged in
                if (mCurrentFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "Iniciado Sesion!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                            )
                                    )
                                    //.setTheme(R.style.AppTheme)
                                    //.setLogo(R.drawable.btn_heart)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    /**
     * Creates a user in the firebase database. We store their name and email.
     *
     * @param firebaseUser A {@link  com.google.firebase.auth.FirebaseUser} object representing a user
     */
    public void createUser(@NonNull final FirebaseUser firebaseUser) {

        // Set all the firebase variables
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

        // Add a listener which adds the user to the database if he does not exist.
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    User user = new User();
                    user.setUid(firebaseUser.getUid());
                    user.setName(firebaseUser.getDisplayName());
                    user.setEmail(firebaseUser.getEmail());
                    userRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method catches the creation of the user.
     *
     * @param requestCode is 1 for the creation of the user.
     * @param resultCode  is to check if everything went the way it should go.
     * @param data        the intent which was passed.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: check if a try catch is possible here
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mCurrentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                createUser(mCurrentFirebaseUser);
                Toast.makeText(this, "Has iniciado sesión !", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Has cancelado tu sesión.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}