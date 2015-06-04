package com.example.shubhamkanodia.bookmybook;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;

@EFragment
public class IntroPage2Fragment extends Fragment {


    final static String staticMapsURL = "https://maps.googleapis.com/maps/api/staticmap?"; //center=40.714728,-73.998672&zoom=14&size=452x800
    static boolean isFirstTimeVisible = true;

    private OnFragmentInteractionListener mListener;

    @ViewById
    FrameLayout flp2;

    @ViewById
    ImageView ivMapView;

    @ViewById
    TextView tvHeading;

    @ViewById
    Button bGoogle;


    ImageView ivPin;

    public IntroPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_intro_page2, container, false);
        flp2 = (FrameLayout) v.findViewById(R.id.flp2);
        ivMapView = (ImageView) v.findViewById(R.id.ivMapView);
        tvHeading = (TextView) v.findViewById(R.id.tvHeading);
        bGoogle = (Button) v.findViewById(R.id.bGoogle);

        tvHeading.setVisibility(View.INVISIBLE);
        bGoogle.setVisibility(View.INVISIBLE);


        Helper.setAndroidContext(getActivity());

        double lat = Helper.getLastKnownLocation() != null ? Helper.getLastKnownLocation().getLatitude() : 12.88;
        double lon = Helper.getLastKnownLocation() != null ? Helper.getLastKnownLocation().getLongitude() : 77.9;

        String mapURL = staticMapsURL + "center=" + lat + "," + lon
                + "&zoom=16&size=" + (int) Math.round(Helper.getDeviceWidth()) + "x" + (int) Math.round(Helper.getDeviceHeight());

        Log.e("mapurl", "is " + mapURL);
        Picasso.with(getActivity()).load(mapURL).transform(new GrayscaleTransformation()).into(ivMapView);
        ivMapView.setColorFilter(new PorterDuffColorFilter(getActivity().getResources().getColor(R.color.color1), PorterDuff.Mode.MULTIPLY));
        return v;


    }

    public void createPin(int num) {

        final int pinSize = 80;

        Handler handler1 = new Handler();

        for (int i = 0; i < num; i++) {
            handler1.postDelayed(new Runnable() {

                @Override
                public void run() {
                    ivPin = new ImageView(getActivity());
                    FrameLayout.LayoutParams vp =
                            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                    pinSize);
                    vp.leftMargin = Helper.getRandomBetween(pinSize, (int) Math.floor(Helper.getDeviceWidth() - pinSize));
                    vp.topMargin = Helper.getRandomBetween((int) Math.floor(Helper.getDeviceHeight() / 4), (int) Math.floor(3 * Helper.getDeviceHeight() / 4) - pinSize);

                    ivPin.setImageResource(R.mipmap.ic_location_pin);
                    flp2.addView(ivPin, vp);

                    float x = (ivPin.getWidth() - ivPin.getPaddingLeft() - ivPin.getPaddingRight()) / 2
                            + ivPin.getPaddingLeft();
                    float y = 50;

                    AnimatorSet set = new AnimatorSet();
                    set.setDuration(600);
                    set.play(ObjectAnimator.ofFloat(ivPin, "pivotX", x, x, x, x, x));
                    set.play(ObjectAnimator.ofFloat(ivPin, "pivotY", y, y, y, y, y));
                    set.play(ObjectAnimator.ofFloat(ivPin, "rotationX", 55, -30, 0));
                    set.play(ObjectAnimator.ofFloat(ivPin, "alpha", 0, 1));

                    set.start();

                }
            }, 600 * i);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (isFirstTimeVisible) {
                createPin(5);
                tvHeading.setVisibility(View.VISIBLE);
                bGoogle.setVisibility(View.VISIBLE);

                AnimationHelper.showView(tvHeading, true , 500);
                AnimationHelper.showView(bGoogle, true , 500);





                isFirstTimeVisible = false;

            } else {
            }
        }
    }

        @Override
        public void onAttach (Activity activity){
            super.onAttach(activity);
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach () {
            super.onDetach();
            mListener = null;
        }

        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            public void onFragmentInteraction(Uri uri);
        }

    }
