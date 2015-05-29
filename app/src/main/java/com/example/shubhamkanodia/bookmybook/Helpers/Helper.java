package com.example.shubhamkanodia.bookmybook.Helpers;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.support.v7.widget.Toolbar;

/**
 * Created by shubhamkanodia on 17/05/15.
 * This file contains some of the most commonly used android functions
 * for checking network etc.
 * Listing
 * ---------------------
 * 1) boolean isNetworkOnline() : check internet connectivity
 * 2) boolean isLollipop() : check 5.0 and above
 */
public class Helper extends AnimationHelper{

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



    public static void setStatusBarColor(int color) {

        if(!isLollipop())
            return;

        Window window = ((Activity) context).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public static int pxToDp(int px) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return px / (int) metrics.density;
    }


}
