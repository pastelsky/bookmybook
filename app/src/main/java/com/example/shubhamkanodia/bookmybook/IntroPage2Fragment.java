package com.example.shubhamkanodia.bookmybook;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.ContrastFilterTransformation;

@EFragment
public class IntroPage2Fragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    final static String staticMapsURL = "https://maps.googleapis.com/maps/api/staticmap?"; //center=40.714728,-73.998672&zoom=14&size=452x800
    static boolean isFirstTimeVisible = true;

    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    @ViewById
    FrameLayout flp2;

    @ViewById
    ImageView ivMapView;

    @ViewById
    TextView tvHeading;

    @ViewById
    Button bGoogle;

    @ViewById
    Button bSkipSignup;

    ImageView ivPin;

    public IntroPage2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_intro_page2, container, false);
        flp2 = (FrameLayout) v.findViewById(R.id.flp2);
        ivMapView = (ImageView) v.findViewById(R.id.ivMapView);
        tvHeading = (TextView) v.findViewById(R.id.tvHeading);
        bGoogle = (Button) v.findViewById(R.id.bGoogle);
        bSkipSignup = (Button) v.findViewById(R.id.bSkipSignup);

        bGoogle.setOnClickListener(this);
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

    @Click
    public void bSkipSignup() {
        Intent toStartMain = new Intent(getActivity(), MainActivity.class);
        startActivity(toStartMain);
    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {

        Log.e("resolveSignInError", "G+");

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                Log.e("startResolutionForRe", "G+");

                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
                Log.e("Exception", "G+");

            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("Conn failed", "G+");

        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),
                    0).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {

        Log.e("Resolved in frag", "G+");
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();

        getProfileInformation();


        Intent toStartMain = new Intent(getActivity(), MainActivity.class);
        startActivity(toStartMain);

    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("TAG", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                ParseUser user = new ParseUser();

                int index = email.indexOf("@");

                String id_and_password = email.substring(0, index);
                user.setUsername(id_and_password);
                user.setEmail(email);
                user.setPassword(id_and_password);

                user.put("phoneVerified", false);


                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Toast.makeText(getActivity(),
                                    "Successfully Signed up, please log in.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Login failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * Button on click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bGoogle:
                signInWithGplus();
                break;
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        Log.e("Connecting", "g+");
        if (mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), "Already connected", Toast.LENGTH_SHORT);
            Log.e("Already Connecting", "g+");

        } else if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            mGoogleApiClient.connect();
                        }

                    });
        }
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
                    set.play(ObjectAnimator.ofFloat(ivPin, "rotationX", 85, -30, 0));
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

                AnimationHelper.fadeInDown(tvHeading, 500).addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        createPin(6);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });

                isFirstTimeVisible = false;

            } else {
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}