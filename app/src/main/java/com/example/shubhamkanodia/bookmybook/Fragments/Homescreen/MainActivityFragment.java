package com.example.shubhamkanodia.bookmybook.Fragments.Homescreen;

/**
 * Created by Chirag Shenoy on 24-Jun-15.
 */

import android.app.ActivityOptions;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BookListingAdapter;
import com.example.shubhamkanodia.bookmybook.AddBooksActivity;
import com.example.shubhamkanodia.bookmybook.AddBooksActivity_;
import com.example.shubhamkanodia.bookmybook.DisplayBookListing;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.Helpers.Metaphone;
import com.example.shubhamkanodia.bookmybook.IntroductionActivity;
import com.example.shubhamkanodia.bookmybook.IntroductionActivity_;
import com.example.shubhamkanodia.bookmybook.R;
import com.example.shubhamkanodia.bookmybook.SettingsActivity;
import com.example.shubhamkanodia.bookmybook.UI.SearchBooksActivity;
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
import com.pixplicity.easyprefs.library.Prefs;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_main_activity)
public class MainActivityFragment extends Fragment implements ObservableScrollViewCallbacks {

    final String BOOKS_LABEL = "Test";
    final int animDuration = 400;
    ArrayList<BookItem> books = new ArrayList<BookItem>();
    BookListingAdapter bAdapter;
    AlphaInAnimationAdapter animationAdapter;

    @ViewById
    FloatingActionButton bAddBook;

    @ViewById
    ObservableListView lvBooks;

    @ViewById
    ProgressBar pbLoading;

    @ViewById
    NavigationView nvDrawer;


    @ViewById
    DrawerLayout dlMain;

    @ViewById
    RelativeLayout rvEmptyLv;


    int hideOffset;
    boolean areControlsHidden;
    boolean verticalThresholdExceeded;

    @ViewById
    EditText etSearch;


    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        AnimationHelper.zoomInView(bAddBook, 700);

        areControlsHidden = false;
        hideOffset = Helper.getDeviceHeight() / 10;
        verticalThresholdExceeded = false;

        lvBooks.setEmptyView(rvEmptyLv);
        bAdapter = new BookListingAdapter(getActivity(), R.layout.book_item, books);
        lvBooks.setAdapter(bAdapter);

        nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.settings:
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
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
                Intent intent = new Intent(getActivity(), AddBooksActivity_.class);
                startActivity(intent);
            }
        });

        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(getActivity(), DisplayBookListing.class);
                intent.putExtra("bookCover", bAdapter.getCoverByPosition(pos));

                View clickedview = bAdapter.getViewByPosition(pos);
                intent.putExtra("bookName", bAdapter.getItem(pos).book_name);
                intent.putExtra("bookAuthor", bAdapter.getItem(pos).book_author);

                if (Helper.isLollipop()) {

                    View navigationBar = view.findViewById(android.R.id.navigationBarBackground);

                    ActivityOptions options = ActivityOptions.
                            makeSceneTransitionAnimation(getActivity(),
                                    Pair.create(clickedview.findViewById(R.id.ivBookCover), "bookCover"),
                                    Pair.create(clickedview.findViewById(R.id.tvBookName), "tBookName"),
                                    Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

                    getActivity().startActivity(intent, options.toBundle());

                } else
                    startActivity(intent);
            }
        });

//        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    Toast.makeText(getActivity(), "got the focus", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivity(), "lost the focus", Toast.LENGTH_LONG).show();
//                }
//            }
//        });



//        bSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Metaphone m = new Metaphone();
//                m.setMaxCodeLen(20);
//                String s = etSearch.getText().toString();
//
//
////                Toast.makeText(getActivity(), m.metaphone(s), Toast.LENGTH_LONG).show();
//
////                Toast.makeText(getActivity(), "size" + m.getMaxCodeLen(), Toast.LENGTH_LONG).show();
//
//
//                if (m.metaphone("Data Structures and program design in C").contains(m.metaphone(s)))
//                    Toast.makeText(getActivity(), "EQUAL", Toast.LENGTH_LONG).show();
//            }
//        });




    }

    @FocusChange
    public void etSearch(View v, boolean hasFocus){

        Intent intent = new Intent(getActivity(), SearchBooksActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);

    }

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

                if (getActivity() != null)
                    bAdapter = new BookListingAdapter(getActivity(), R.layout.book_item, books);

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.search_books:
                hideControls();
        }

        return super.onOptionsItemSelected(item);
    }

}
