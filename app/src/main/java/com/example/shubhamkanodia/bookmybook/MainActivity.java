package com.example.shubhamkanodia.bookmybook;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final String BOOKS_LABEL = "Test";
    ArrayList<BookItem> books = new ArrayList<BookItem>();
    BookListingAdapter bAdapter;
    AlphaInAnimationAdapter animationAdapter;
    RelativeLayout mainrel;
    ActionButton addBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mainrel = (RelativeLayout) findViewById(R.id.mainrel);
        addBook = (ActionButton) findViewById(R.id.add_book);

        final ListView listView = (ListView) findViewById(R.id.dynamiclistview);

        ScaleAnimation zoomButton = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoomButton.setFillAfter(true); // Needed to keep the result of the animation
        zoomButton.setDuration(700);
        addBook.startAnimation(zoomButton);


        //Init already done  in Application.java
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                BOOKS_LABEL);

        if (!isNetworkOnline())
            query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> bookList, ParseException e) {
                for (ParseObject book : bookList) {

                    BookItem toPush = new BookItem(book.getString("text"), book.getString("author"));
                    books.add(toPush);
                }

                bAdapter = new BookListingAdapter(MainActivity.this, R.layout.book_item, books);
                final List<ParseObject> bList = bookList;
                animationAdapter = new AlphaInAnimationAdapter(bAdapter);
                animationAdapter.setAbsListView(listView);
                listView.setAdapter(animationAdapter);

                // Release any objects previously pinned for this query.
                ParseObject.unpinAllInBackground(BOOKS_LABEL, bookList, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }


                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground(BOOKS_LABEL, bList);
                    }
                });

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DisplayBookListing.class);
                startActivity(intent);
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

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
