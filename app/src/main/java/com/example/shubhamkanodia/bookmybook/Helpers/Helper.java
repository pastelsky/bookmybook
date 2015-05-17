package com.example.shubhamkanodia.bookmybook.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by shubhamkanodia on 17/05/15.
 * This file contains some of the most commonly used android functions
 * for checking network etc.
 * Listing
 * ---------------------
 * 1) boolean isNetworkOnline() : check internet connectivity
 * 2) boolean isLollipop() : check 5.0 and above
 */
public class Helper {

    static Context context;

    public static void setAndroidContext(Context c) {
        context = c;
    }

    public static boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }


    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
