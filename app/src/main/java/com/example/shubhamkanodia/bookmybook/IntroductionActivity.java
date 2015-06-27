package com.example.shubhamkanodia.bookmybook;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.shubhamkanodia.bookmybook.Helpers.Helper;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class IntroductionActivity extends FragmentActivity implements
        IntroFragment.OnFragmentInteractionListener{

    private static final int NUM_PAGES = 2;

    @ViewById
    ViewPager vpIntro;

    private PagerAdapter mPagerAdapter;
    Fragment signInFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Helper.setAndroidContext(this);
        mPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        vpIntro.setAdapter(mPagerAdapter);

    }


    @Override
    public void onBackPressed() {
        if (vpIntro.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            vpIntro.setCurrentItem(vpIntro.getCurrentItem() - 1);
        }
}

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {

        Log.e("Resolved in main", "G+");

        signInFragment.onActivityResult(requestCode, responseCode, intent);
        super.onActivityResult(requestCode, responseCode, intent);
//        if (requestCode == RC_SIGN_IN) {
//            if (responseCode != getActivity().RESULT_OK) {
//                mSignInClicked = false;
//            }
//
//            mIntentInProgress = false;
//
//            if (!mGoogleApiClient.isConnecting()) {
//                mGoogleApiClient.connect();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_introduction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private class IntroPagerAdapter extends FragmentStatePagerAdapter {
        android.support.v4.app.FragmentManager mFragmentManager;

        public IntroPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;

        }

        public Fragment getActiveFragment(ViewPager container, int position) {
            String name = makeFragmentName(container.getId(), position);
            return  mFragmentManager.findFragmentByTag(name);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new IntroFragment();

                case 1: signInFragment = new IntroPage2Fragment();
                    return signInFragment;

                default: return new IntroPage2Fragment();

            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
