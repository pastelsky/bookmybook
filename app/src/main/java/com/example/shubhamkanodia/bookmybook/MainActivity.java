package com.example.shubhamkanodia.bookmybook;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
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

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    final String BOOKS_LABEL = "Test";
    final int animDuration = 400;
    ArrayList<BookItem> books = new ArrayList<BookItem>();
    BookListingAdapter bAdapter;
    AlphaInAnimationAdapter animationAdapter;

    FloatingActionButton bAddBook;
    Toolbar tbMain;
    ObservableListView lvBooks;
    ProgressBar pbLoading;

    NavigationView nvDrawer;
    DrawerLayout dlMain;


    int hideOffset;
    boolean areControlsHidden;
    boolean verticalThresholdExceeded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bAddBook = (FloatingActionButton) findViewById(R.id.bAddBook);
        tbMain = (Toolbar) findViewById(R.id.tbMain);
        lvBooks = (ObservableListView) findViewById(R.id.lvBooks);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        nvDrawer = (NavigationView) findViewById(R.id.nvDrawer);
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);


        Helper.setAndroidContext(this);
        lvBooks.setScrollViewCallbacks(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        AnimationHelper.zoomInView(bAddBook, 700);
        setSupportActionBar(tbMain);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        areControlsHidden = false;
        hideOffset = Helper.getDeviceHeight() / 10;
        verticalThresholdExceeded = false;

        lvBooks.setEmptyView(findViewById(R.id.rvEmptyLv));
        bAdapter = new BookListingAdapter(this, R.layout.book_item, books);
//        animationAdapter = new AlphaInAnimationAdapter(bAdapter);
//        animationAdapter.setAbsListView(lvBooks);
        lvBooks.setAdapter(bAdapter);

        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                }
                menuItem.setChecked(true);
                dlMain.closeDrawers();
                return true;
            }
        });


        queryBooks();

        bAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddBooksActivity_.class);
                startActivity(intent);
            }
        });

        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(MainActivity.this, DisplayBookListing.class);
                intent.putExtra("bookCover", bAdapter.getCoverByPosition(pos));

                View clickedview = bAdapter.getViewByPosition(pos);
                intent.putExtra("bookName", bAdapter.getItem(pos).book_name);
                intent.putExtra("bookAuthor", bAdapter.getItem(pos).book_author);

                if (Helper.isLollipop()) {

                    View navigationBar = findViewById(android.R.id.navigationBarBackground);

                    ActivityOptions options = ActivityOptions.
                            makeSceneTransitionAnimation(MainActivity.this,
                                    Pair.create(clickedview.findViewById(R.id.ivBookCover), "bookCover"),
                                    Pair.create(clickedview.findViewById(R.id.tvBookName), "tBookName"),
                                    Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

                    startActivity(intent, options.toBundle());

                } else
                    startActivity(intent);
            }
        });

    }

    /* Click Handlers */

//    @Click
//    void bAddBook() {
//        Intent intent = new Intent(this, AddBooksActivity_.class);
//        startActivity(intent);
//    }

//    @ItemClick
//    void lvBooks(int pos) {
//        Intent intent = new Intent(this, DisplayBookListing_.class);
//        intent.putExtra("bookCover", bAdapter.getCoverByPosition(pos));
//
//        View clickedview = bAdapter.getViewByPosition(pos);
//        intent.putExtra("bookName", bAdapter.getItem(pos).book_name);
//        intent.putExtra("bookAuthor", bAdapter.getItem(pos).book_author);
//
//        if (Helper.isLollipop()) {
//
//            View navigationBar = findViewById(android.R.id.navigationBarBackground);
//
//            ActivityOptions options = ActivityOptions.
//                    makeSceneTransitionAnimation(MainActivity.this,
//                            Pair.create(clickedview.findViewById(R.id.ivBookCover), "bookCover"),
//                            Pair.create(clickedview.findViewById(R.id.tvBookName), "tBookName"),
//                            Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
//
//            startActivity(intent, options.toBundle());
//
//        } else
//            startActivity(intent);
//    }


    public void queryBooks() {

        books = new ArrayList<BookItem>();
        pbLoading.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(BOOKS_LABEL);

        if (!Helper.isNetworkOnline())
            query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> bookList, ParseException e) {
                for (ParseObject book : bookList) {

                    BookItem toPush = new BookItem(book.getString("text"), book.getString("author"), book.getString("cover"));
                    books.add(toPush);
                }

                final List<ParseObject> newListToPin = bookList;

                ParseObject.unpinAllInBackground(BOOKS_LABEL, newListToPin, new DeleteCallback() {

                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }
                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground(BOOKS_LABEL, newListToPin);
                    }
                });
                Log.e("array", books.toString());
                bAdapter = new BookListingAdapter(MainActivity.this, R.layout.book_item, books);
                lvBooks.setAdapter(bAdapter);
                pbLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        queryBooks();

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
//
//        AnimationHelper.hideViewDown(bAddBook, true, animDuration);
//        AnimationHelper.hideViewUp(tbMainContainer,true,animDuration);
//        AnimationHelper.statusBarColorTransition(Color.BLACK, animDuration);

    }

    public void showControls() {
//
//        AnimationHelper.zoomOutView(bAddBook, animDuration);
//        AnimationHelper.showView(tbMainContainer, true, animDuration);
//        AnimationHelper.statusBarColorTransition(getResources().getColor(R.color.color2), animDuration);

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
                areControlsHidden = true;
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (areControlsHidden) {
                showControls(); //
                Log.e("Move", "ScrollState Down");
                areControlsHidden = false;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.search_books:
                hideControls();
        }

        return super.onOptionsItemSelected(item);
    }

}
