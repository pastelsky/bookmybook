package com.example.shubhamkanodia.bookmybook;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.firebase.client.Firebase;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity
public class MainActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

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
    ObservableListView lvBooks;
    @ViewById
    CardView tbMainContainer;

    int hideOffset;
    boolean areControlsHidden;
    boolean verticalThresholdExceeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Helper.setAndroidContext(this);
        Firebase.setAndroidContext(this);
        lvBooks.setScrollViewCallbacks(this);
        Firebase myFirebaseRef = new Firebase("https://intense-torch-2456.firebaseio.com/android/books");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        setSupportActionBar(tbMain);
        areControlsHidden = false;
        hideOffset = Helper.getDeviceHeight() / 8;
        verticalThresholdExceeded = false;

        zoomInAddButton();
        queryBooks();

    }

    @Click
    void bAddBook() {
        Intent intent = new Intent(this, AddBooksActivity_.class);
        startActivity(intent);

    }

    @ItemClick
    void lvBooks(int pos) {
        Intent intent = new Intent(this, DisplayBookListing_.class);
        intent.putExtra("bookCover", bAdapter.getCoverByPosition(pos));

        View clickedview = bAdapter.getViewByPosition(pos);
        intent.putExtra("bookName", bAdapter.getItem(pos).book_name);
        intent.putExtra("bookAuthor", bAdapter.getItem(pos).book_author);



        if (Helper.isLollipop()) {

            View navigationBar = findViewById(android.R.id.navigationBarBackground);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            Pair.create(clickedview.findViewById(R.id.ivBookCover), "bookCover"),
                            Pair.create(clickedview.findViewById(R.id.tvBookName), "tBookName")
                            , Pair.create((View) tbMain, "toolbar"),
                            Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

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

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

        if (i > hideOffset) {
            verticalThresholdExceeded = true;
        } else {
            verticalThresholdExceeded = false;
            if (areControlsHidden)
                showControls();
        }

    }

    public void hideControls() {

        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillAfter(true);
        animSet.setDuration(300);
        AlphaAnimation alp = new AlphaAnimation(1.0f, 0);
        TranslateAnimation translate = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, TranslateAnimation.RELATIVE_TO_SELF, TranslateAnimation.RELATIVE_TO_SELF, Helper.getDeviceHeight() / 3);
        animSet.addAnimation(translate);
        animSet.addAnimation(alp);
        bAddBook.startAnimation(animSet);


        AnimationSet animToolbar = new AnimationSet(true);
        animToolbar.setFillAfter(true);
        animToolbar.setDuration(300);
        AlphaAnimation alpToolbar = new AlphaAnimation(1.0f, 0);
        TranslateAnimation translateToolbar = new
                TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF,
                TranslateAnimation.RELATIVE_TO_SELF,
                TranslateAnimation.RELATIVE_TO_SELF,
                -getResources().getDimension(R.dimen.actionBarSize)
        );
        animToolbar.addAnimation(alpToolbar);
        animToolbar.addAnimation(translateToolbar);
        tbMainContainer.startAnimation(animToolbar);

        areControlsHidden = true;

        //Set status bar to black;
        if (Helper.isLollipop()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }


    }

    public void showControls() {

        AnimationSet animSet = new AnimationSet(true);
        animSet.setFillAfter(true);
        animSet.setDuration(300);
        AlphaAnimation alp = new AlphaAnimation(0, 1);
        TranslateAnimation translate = new TranslateAnimation(0, 0, Helper.getDeviceHeight() / 3, 0);
        animSet.addAnimation(translate);
        animSet.addAnimation(alp);
        bAddBook.startAnimation(animSet);


        AnimationSet animToolbar = new AnimationSet(true);
        animToolbar.setFillAfter(true);
        animToolbar.setDuration(300);
        AlphaAnimation alpToolbar = new AlphaAnimation(0, 1);
        TranslateAnimation translateToolbar = new
                TranslateAnimation(0, 0, -getResources().getDimension(R.dimen.actionBarSize), 0
        );
        animToolbar.addAnimation(alpToolbar);
        animToolbar.addAnimation(translateToolbar);
        tbMainContainer.startAnimation(animToolbar);

        //Compensate the listview - move it up babe!

        areControlsHidden = false;

        //Set status bar to original;
        if (Helper.isLollipop()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.color2));
        }

    }


    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP && verticalThresholdExceeded) {
            if (!areControlsHidden) {
                hideControls();
                Log.e("Move", "ScrollState Up");
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (areControlsHidden) { // TODO Not implemented
                showControls(); // TODO Not implemented
                Log.e("Move", "ScrollState Down");

            }
        }

    }
}
