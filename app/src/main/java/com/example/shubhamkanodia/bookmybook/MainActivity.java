package com.example.shubhamkanodia.bookmybook;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.firebase.client.Firebase;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity
public class MainActivity extends AppCompatActivity {

    final String BOOKS_LABEL = "Test";
    ArrayList<BookItem> books = new ArrayList<BookItem>();
    BookListingAdapter bAdapter;
    AlphaInAnimationAdapter animationAdapter;

    @ViewById
    ActionButton bAddBook;
    @ViewById
    Toolbar tbMain;
    @ViewById
    RelativeLayout rvMain;
    @ViewById
    ListView lvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Helper.setAndroidContext(this);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://intense-torch-2456.firebaseio.com/android/books");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        setSupportActionBar(tbMain);

        zoomInAddButton();
        queryBooks();

    }

    @ItemClick
    void lvBooks(int pos) {
        Intent intent = new Intent(MainActivity.this, DisplayBookListing.class);
        intent.putExtra("bookCover", bAdapter.getCoverByPosition(pos));

        if (Helper.isLollipop()) {

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            Pair.create(bAdapter.getViewByPosition(pos).findViewById(R.id.ivBookCover), "bookCover")
                            , Pair.create((View) tbMain, "toolbar"));
            
            startActivity(intent, options.toBundle());

        } else
            startActivity(intent);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void zoomInAddButton() {

        ScaleAnimation zoomButton = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        zoomButton.setFillAfter(true); // Needed to keep the result of the animation
        zoomButton.setDuration(700);
        bAddBook.startAnimation(zoomButton);
    }

    public void queryBooks() {


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                BOOKS_LABEL);

        if (!Helper.isNetworkOnline())
            query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> bookList, ParseException e) {
                for (ParseObject book : bookList) {

                    BookItem toPush = new BookItem(book.getString("text"), book.getString("author"), book.getString("cover"));
                    books.add(toPush);
                }

                bAdapter = new BookListingAdapter(MainActivity.this, R.layout.book_item, books);
                final List<ParseObject> bList = bookList;
                animationAdapter = new AlphaInAnimationAdapter(bAdapter);
                animationAdapter.setAbsListView(lvBooks);
                lvBooks.setAdapter(animationAdapter);

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

    }

}
