package com.calm_health.sports;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.calm_health.sports.firebase.AuthProvider;
import com.calm_health.sports.firebase.AuthResultEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements AuthResultEvent, GoogleApiClient.OnConnectionFailedListener{
    CallbackManager callbackManager;
    TextView txtName;
    TextView txtGender;
    TextView txtHeight;
    TextView txtWeight;
    TextView txtBirthday;
    TextView txtEmail;
    TextView txtPassword;

    Button btnSigninEmail;
    Button btnSignupEmail;
    LoginButton btnFacebook;
    Button btnGoogle;
    Button btnLogout;

    FirebaseUser mUser;

    AuthProvider mAuthProvider;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("FirebaseApp", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthProvider = new AuthProvider(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AuthProvider.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage((FragmentActivity) this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        txtName = (TextView) findViewById(R.id.name);
        txtGender = (TextView) findViewById(R.id.gender);
        txtHeight = (TextView) findViewById(R.id.height);
        txtWeight = (TextView) findViewById(R.id.weight);
        txtBirthday = (TextView) findViewById(R.id.birthday);
        txtEmail = (TextView) findViewById(R.id.email);
        txtPassword = (TextView) findViewById(R.id.password);

        btnSigninEmail = (Button) findViewById(R.id.signin);
        btnSignupEmail = (Button) findViewById(R.id.signup);
        btnFacebook = (LoginButton) findViewById(R.id.facebook);
        btnGoogle = (Button) findViewById(R.id.google);
        btnLogout = (Button) findViewById(R.id.logout);

        btnFacebook.setReadPermissions("email");

        btnSigninEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                Log.d("email, password", email + " : " + password);
                mAuthProvider.signinWithEmail(email, password);
            }
        });
        btnSignupEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                Log.d("email, password", email + " : " + password);
                mAuthProvider.signupWithEmail(email, password);
            }
        });
        callbackManager = CallbackManager.Factory.create();
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook login", loginResult.getAccessToken() + "");
            }

            @Override
            public void onCancel() {
                Log.d("facebook login", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("facebook login", error.getMessage());
            } });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("logout", "btnSigninEmail");
                mAuthProvider.logoout();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mUser = mAuthProvider.getCurrentUser();
        if(mUser == null) Log.d("auth status", "no user");
        else Log.d("auth status", mUser.getDisplayName());
    }

    @Override
    public void onSigninWithEmailResult(FirebaseUser user, String message) {
        Log.d("email signin result", message);
    }

    @Override
    public void onSignupWithEmailResult(FirebaseUser user, String message) {
        Log.d("email signup result", message);
    }

    @Override
    public void onFacebookAuthResult(FirebaseUser user, String message) {
        Log.d("facebook result", message);
    }

    @Override
    public void onGoogleAuthResult(FirebaseUser user, String message) {
        Log.d("google result", message);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("google result code", result.getStatus().getStatusCode() + "");
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Log.d("Google name", account.getDisplayName());
                mAuthProvider.firebaseAuthWithGoogle(account);
            } else {
                Log.d("Google Auth", "Failed");
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
