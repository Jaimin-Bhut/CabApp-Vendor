package com.jb.dev.cabapp_vendor.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.jb.dev.cabapp_vendor.R;
import com.jb.dev.cabapp_vendor.helper.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loader();
    }

    void loader() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDriverLogin(getApplicationContext())) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    public static boolean isDriverLogin(Context context) {
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.IS_DRIVER_EMAIL, false);
    }
}