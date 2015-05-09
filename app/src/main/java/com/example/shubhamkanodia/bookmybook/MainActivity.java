package com.example.shubhamkanodia.bookmybook;

import android.animation.ValueAnimator;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ArrayList<BookItem> books = new ArrayList<BookItem>();
    BookListingAdapter bAdapter;
    AlphaInAnimationAdapter animationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

//        toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT >= 16) {
//                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                } else {
//                    toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
//            }
//        });




        final ListView listView = (ListView) findViewById(R.id.dynamiclistview);

        listView.setOnScrollListener(new ListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {   }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == listView.getId()) {
                    final int currentFirstVisibleItem = listView.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        listView.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();


                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        listView.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });


        //Init already done  in Application.java
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());




        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "Test");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> bookList, ParseException e) {
                for (ParseObject book : bookList) {

                    BookItem toPush = new BookItem(book.getString("text"), book.getString("author"));
                    books.add(toPush);
                }


                bAdapter = new BookListingAdapter(MainActivity.this, R.layout.book_item, books);

                animationAdapter = new AlphaInAnimationAdapter(bAdapter);
                animationAdapter.setAbsListView(listView);
                listView.setAdapter(animationAdapter);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
