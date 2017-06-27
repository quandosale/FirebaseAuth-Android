package com.calm_health.sports.firebase;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by hero on 6/27/2017.
 */

public interface AuthResultEvent {
    public void onSigninWithEmailResult(FirebaseUser user, String message);
    public void onSignupWithEmailResult(FirebaseUser user, String message);
    public void onFacebookAuthResult(FirebaseUser user, String message);
    public void onGoogleAuthResult(FirebaseUser user, String message);
}
