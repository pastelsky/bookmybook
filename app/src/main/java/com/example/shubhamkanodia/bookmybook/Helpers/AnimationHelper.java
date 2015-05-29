package com.example.shubhamkanodia.bookmybook.Helpers;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.nineoldandroids.view.ViewHelper;

import org.androidannotations.annotations.ViewById;

/**
 * Created by shubhamkanodia on 21/05/15.
 */
public class AnimationHelper {

    static Context context;

    public static void setAndroidContext(Context c) {
        context = c;
    }


    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getDeviceHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getDeviceWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static void hideViewUp(View v, boolean preventDoubleTranslation, int duration) {
        if (v == null)
            Log.e("View", "No view found: Null reference");

        else if (preventDoubleTranslation && v.getTranslationY() == 0 || !preventDoubleTranslation) {

            ObjectAnimator mover = ObjectAnimator.ofFloat(v, "translationY", 0, -(v.getTop() + v.getHeight()));
            ObjectAnimator alphaer = ObjectAnimator.ofFloat(v, "alpha", 1, 0);

            mover.setDuration(duration);
            alphaer.setDuration(duration);

            AnimatorSet aSet = new AnimatorSet();
            aSet.play(mover).with(alphaer);
            aSet.start();

        }

    }

    public static void hideViewDown(View v, boolean preventDoubleTranslation, int duration) {
        if (v == null)
            Log.e("View", "No view found: Null reference");

        else if (preventDoubleTranslation && v.getTranslationY() == 0 &&v.getAlpha()==1 || !preventDoubleTranslation) {

            ObjectAnimator mover = ObjectAnimator.ofFloat(v, "translationY", 0, (getDeviceHeight() - v.getTop()));
            ObjectAnimator alphaer = ObjectAnimator.ofFloat(v, "alpha", 0, 1);

            mover.setDuration(duration);
            alphaer.setDuration(duration);

            AnimatorSet aSet = new AnimatorSet();
            aSet.play(mover).with(alphaer);
            aSet.start();

        }

    }

    public static void showView(View v, boolean preventDoubleTranslation, int duration){

        if (v == null)
            Log.e("View", "No view found: Null reference");

        else if(preventDoubleTranslation && v.getAlpha()==0 || !preventDoubleTranslation){

            ObjectAnimator mover = ObjectAnimator.ofFloat(v, "translationY", v.getTranslationY(), 0);
            ObjectAnimator alphaer = ObjectAnimator.ofFloat(v, "alpha", 0, 1);

            mover.setDuration(duration);
            alphaer.setDuration(duration);

            AnimatorSet aSet = new AnimatorSet();
            aSet.play(mover).with(alphaer);
            aSet.start();

        }

    }


    public static void statusBarColorTransition(int newclr, int duration) {

        if(!isLollipop())
            return;

        final Window window = ((Activity) context).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int oldclr = window.getStatusBarColor();

        ValueAnimator anim = ValueAnimator.ofArgb(oldclr, newclr);
        anim.setDuration(duration);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                window.setStatusBarColor((int)animation.getAnimatedValue());
            }
        });

        anim.start();

    }


    public static void zoomInView(View v, int duration) {

        ScaleAnimation zoomButton = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoomButton.setFillAfter(true); // Needed to keep the result of the animation
        zoomButton.setDuration(duration);
        v.startAnimation(zoomButton);
    }

    public static void zoomOutView(View v, int duration) {

        ScaleAnimation zoomButton = new ScaleAnimation(
                1f, 0f, // Start and end values for the X axis scaling
                1f, 0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoomButton.setFillAfter(true); // Needed to keep the result of the animation
        zoomButton.setDuration(duration);
        v.startAnimation(zoomButton);
    }
}
