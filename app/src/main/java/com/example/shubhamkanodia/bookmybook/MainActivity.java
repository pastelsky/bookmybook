package com.example.shubhamkanodia.bookmybook;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.shubhamkanodia.bookmybook.Fragments.Homescreen.MainActivityFragment;
import com.example.shubhamkanodia.bookmybook.Fragments.Homescreen.MyPostsFragment;
import com.example.shubhamkanodia.bookmybook.Fragments.Homescreen.MyRequestsFragment;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.pixplicity.easyprefs.library.Prefs;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity
public class MainActivity extends AppCompatActivity {

    private PagerAdapter mPagerAdapter;

    @ViewById
    LinearLayout lvBooks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        new Prefs.Builder()
                .setContext(getApplication())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getApplication().getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        String id_and_pw = Prefs.getString("id_and_pw", "");


        Helper.setAndroidContext(this);

        ParseUser.logInInBackground(id_and_pw, id_and_pw);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vpIntro);

//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }

//        mPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(mPagerAdapter);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }


    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }




    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyPostsFragment();
                case 1:
                    return new MainActivityFragment();
                case 2:
                    return new MyRequestsFragment();

                default:
                    return new MyPostsFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My Posts";
                case 1:
                    return "Books Feed";
                case 2:
                    return "My Requets";
                default:
                    return "My Posts";
            }
        }
    }

}