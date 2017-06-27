package com.calm_health.sports.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

/**
 * Created by hero on 6/27/2017.
 */

public class AuthProvider extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    public static String GOOGLE_CLIENT_ID = "922162166114-jjeo8enadpdv71r12nnc780v4jkib4aj.apps.googleusercontent.com";
    public static String FACEBOOK_APP_ID = "1570076669692821";
    public static String FACEBOOK_PROTOCOL_SCHEME = "fb1570076669692821";
    public static String FACEBOOK_SECRET = "d881c59f6e0c411afc098e7c72068423";

    private FirebaseAuth mAuth;
    private Activity mActivity;
    private AuthResultEvent authResultEve;

    private final String default_web_client_id = "922162166114-aampp66svvd9832n7ldpgljl5ghk91qo.apps.googleusercontent.com";

    public AuthProvider(Activity activity) {
        mActivity = activity;
        authResultEve = (AuthResultEvent)activity;
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signupWithEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            authResultEve.onSignupWithEmailResult(user, "You have been registered");
                        } else {
                            authResultEve.onSignupWithEmailResult(null, task.getException().getMessage());
                        }
                    }
                });
    }

    public void signinWithEmail(String email, String password) {
        Log.d("mAuth", mAuth + "");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            authResultEve.onSigninWithEmailResult(user, "You have logged in");
                        } else {
                            authResultEve.onSigninWithEmailResult(null, task.getException().getMessage());
                        }
                    }
                });

    }
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            authResultEve.onGoogleAuthResult(user, "sucess");
                        } else {
                            authResultEve.onGoogleAuthResult(null, task.getException().getMessage());
                        }
                    }
                });
    }

    public void logoout() {
        mAuth.signOut();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}