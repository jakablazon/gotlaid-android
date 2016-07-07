package com.gotlaid.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gotlaid.android.utils.InternetUtils;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static Typeface workSansExtraBoldTypeface;
    private static TextView appTitleTv;
    private static Button loginWithFbButton;

    private static CallbackManager mCallbackManager;
    private static final List<String> permissionNeeds =
            Arrays.asList("email", "public_profile", "user_friends");
    private static LoginManager mLoginMgr;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        workSansExtraBoldTypeface = Typeface.createFromAsset(getAssets(), "fonts/WorkSans-ExtraBold.ttf");
        appTitleTv = (TextView) findViewById(R.id.loginAppTitleTv);
        appTitleTv.setTypeface(workSansExtraBoldTypeface);
        loginWithFbButton = (Button) findViewById(R.id.fbLoginButton);
        loginWithFbButton.setTypeface(workSansExtraBoldTypeface);

        if (InternetUtils.isOnline(getApplicationContext())) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
            };
        }else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_internet_connection)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
        }catch (Exception e){}
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View v){
        mCallbackManager = CallbackManager.Factory.create();
        mLoginMgr = LoginManager.getInstance();
        loginWithFbButton.setVisibility(View.GONE);
        mLoginMgr.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                loginWithFbButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                loginWithFbButton.setVisibility(View.VISIBLE);
            }
        });

        mLoginMgr.logInWithReadPermissions(this, permissionNeeds);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, getString(R.string.aut_failed),
                                    Toast.LENGTH_SHORT).show();
                            loginWithFbButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
