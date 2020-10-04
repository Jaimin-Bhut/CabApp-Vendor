package com.jb.dev.cabapp_vendor.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.jb.dev.cabapp_vendor.R;

public class Helper {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showSnackBar(View view, int message, final Context context) {
        String path = "";
        switch (message) {
            case R.string.please_turn_on_internet:
                path = Settings.ACTION_NETWORK_OPERATOR_SETTINGS;
                break;
            case R.string.please_turn_on_location:
                path = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                break;

        }
        final String finalPath = path;
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("TURN ON", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(finalPath);
                        context.startActivity(intent);
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }
}

