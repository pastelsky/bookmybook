package com.example.shubhamkanodia.bookmybook.Fragments.Homescreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.MyPostsAdapter;
import com.example.shubhamkanodia.bookmybook.Adapters.MyPostsAdapter;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chirag Shenoy on 25-Jun-15.
 */
public class MyPostsFragment extends Fragment implements ObservableScrollViewCallbacks {

    ListView lvPostBooks;
    ArrayList<BookItem> books = new ArrayList<BookItem>();
    MyPostsAdapter bAdapter;
    ProgressBar pbLoading;

    int hideOffset;
    boolean areControlsHidden;
    boolean verticalThresholdExceeded;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myposts, container, false);

        lvPostBooks = (ListView) view.findViewById(R.id.lv_my_posts);
        lvPostBooks.setEmptyView(view.findViewById(R.id.rvEmptyLv));
//        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);

        areControlsHidden = false;
//        hideOffset = Helper.getDeviceHeight() / 10;
        verticalThresholdExceeded = false;

        bAdapter = new MyPostsAdapter(getActivity(), R.layout.book_item, books);

        lvPostBooks.setAdapter(bAdapter);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpenedInBackground(getActivity().getIntent());

        queryBooks();

//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("adlisting");
//        query.whereEqualTo("ad_poster", ParseUser.getCurrentUser());
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//
//            public void done(List<ParseObject> AdList, ParseException e) {
//
//                ParseObject postedBook = new ParseObject("book");
//                for (int i = 0; i < AdList.size(); i++)
//
//                {
//                    postedBook = AdList.get(i).getParseObject("book");
//
//                    postedBook.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                        public void done(ParseObject object, ParseException e) {
//                            // all fields of the object will now be available here.
//
//                            BookItem postedBook = new BookItem(object.getString("book_name"), "Shubhamm Bobo", "Cover");
//                            books.add(postedBook);
////                            Log.e("Book name is ", object.getString("book_name"));
//
//                        }
//                    });
//
//
//                }
//
//            }
//
//
//
//        });

        return view;
    }

    public void queryBooks() {

        books = new ArrayList<BookItem>();
//        pbLoading.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("adlisting");
        query.whereEqualTo("ad_poster", ParseUser.getCurrentUser());

        if (!Helper.isNetworkOnline())
            query.fromLocalDatastore();

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> AdList, ParseException e) {

                ParseObject postedBook = new ParseObject("book");
                for (int i = 0; i < AdList.size(); i++)

                {
                    postedBook = AdList.get(i).getParseObject("book");

                    postedBook.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            // all fields of the object will now be available here.

                            String authors = object.getString("book_authors");
                            BookItem my_book = new BookItem(object.getString("book_name"), authors, object.getString("book_cover_url"));
                            books.add(my_book);
                            Log.e("Book name is ", object.getString("book_name"));
                        }
                    });


                }

                final List<ParseObject> newListToPin = AdList;

                ParseObject.unpinAllInBackground("adlisting", newListToPin, new DeleteCallback() {

                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }
                        // Add the latest results for this query to the cache.
                        ParseObject.pinAllInBackground("adlisting", newListToPin);
                    }
                });

                if (getActivity() != null)
                    bAdapter = new MyPostsAdapter(getActivity(), R.layout.book_item, books);

                lvPostBooks.setAdapter(bAdapter);
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


}
