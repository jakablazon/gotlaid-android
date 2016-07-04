package com.poviolabs.gotlaid;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private static Typeface workSansExtraBoldTypeface;
    private static TextView appTitleTv;
    private static Button loginWithFbButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        workSansExtraBoldTypeface = Typeface.createFromAsset(getAssets(), "fonts/WorkSans-ExtraBold.ttf");
        appTitleTv = (TextView) findViewById(R.id.loginAppTitleTv);
        appTitleTv.setTypeface(workSansExtraBoldTypeface);
        loginWithFbButton = (Button) findViewById(R.id.fbLoginButton);
        loginWithFbButton.setTypeface(workSansExtraBoldTypeface);
    }
}
