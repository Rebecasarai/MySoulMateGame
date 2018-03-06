package com.rebecasarai.mysoulmate.Views.Activities;

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
 * Esta es la clase es la main activity de la aplicación.
 * ermite iniciar sesión con cuenta de Google o correo electrónico.
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
     * Este método registra al usuario con Firebase. Comprueba si un usuario está conectado o no.
     */
    private void firebaseLogin() {

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Obtiene el usuario actual de firebase.
                mCurrentFirebaseUser = firebaseAuth.getCurrentUser();

                // Verifica si el usuario está conectado
                if (mCurrentFirebaseUser != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // Si ek usuario cierra sesión
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
                                    .setTheme(R.style.AppTheme)
                                    .setLogo(R.drawable.btn_heart)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    /**
     * Crea un usuario en la base de datos de firebase. Almacenamos su nombre y correo electrónico.
     *
     * @param firebaseUser A {@link  com.google.firebase.auth.FirebaseUser} objeto que representa usuario
     */
    public void createUser(@NonNull final FirebaseUser firebaseUser) {

        // Establecer todas las variables de firebase
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());

        // Agrega un oyente que agrega el usuario a la base de datos si no existe.
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
     * Este método obtiene la creación del usuario.
     *
     * @param requestCode es 1 para la creación del usuario.
     * @param resultCode  es comprobar si salió como debería.
     * @param data        el intent a iniciar.
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
                Toast.makeText(this, "Has cerrado tu sesión.", Toast.LENGTH_SHORT).show();
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