package com.gotlaid.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by zigapk on 7.7.2016.
 */
public class InternetUtils {
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
