package com.example.shubhamkanodia.bookmybook.Helpers;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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

import java.util.List;
import java.util.Random;

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


    public static boolean validateIsbn13( String isbn )
    {
        if ( isbn == null )
        {
            return false;
        }

        //remove any hyphens
        isbn = isbn.replaceAll( "-", "" );

        //must be a 13 digit ISBN
        if ( isbn.length() != 13 )
        {
            return false;
        }

        try
        {
            int tot = 0;
            for ( int i = 0; i < 12; i++ )
            {
                int digit = Integer.parseInt( isbn.substring( i, i + 1 ) );
                tot += (i % 2 == 0) ? digit * 1 : digit * 3;
            }

            //checksum must be 0-9. If calculated as 10 then = 0
            int checksum = 10 - (tot % 10);
            if ( checksum == 10 )
            {
                checksum = 0;
            }

            return checksum == Integer.parseInt( isbn.substring( 12 ) );
        }
        catch ( NumberFormatException nfe )
        {
            //to catch invalid ISBNs that have non-numeric characters in them
            return false;
        }
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

        public static Location getLastKnownLocation() {

            LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locMan.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locMan.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if (bestLocation == null) {
                return null;
            }
            return bestLocation;
        }

    public static double[] getCoarseLocation() {
        // Get the location manager

        double[] gps = new double[2];

        LocationManager locationManager = (LocationManager)
                context.getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            gps[0] = location.getLatitude();
            gps[1] = location.getLongitude();
        } catch (NullPointerException e) {
            gps[0] = 40.714728;
            gps[1] = -73.998672;
        }

        return gps;
    }


    public static int getRandomBetween(int low, int high)
    {
        Random r = new Random();
        return r.nextInt(high-low) + low;
    }


}
