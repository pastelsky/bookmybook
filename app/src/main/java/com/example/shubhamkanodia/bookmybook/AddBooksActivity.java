// Activity to add a new ad

package com.example.shubhamkanodia.bookmybook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.ScannedBooksAdapter;
import com.example.shubhamkanodia.bookmybook.Parsers.GoogleBooksParser;
import com.example.shubhamkanodia.bookmybook.Parsers.ImportIOParser;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;


import me.dm7.barcodescanner.zbar.ZBarScannerView;


@EActivity
public class AddBooksActivity extends AppCompatActivity {


    //    @ViewById
//    AutoCompleteTextView etBookName;
//
//    @ViewById
//    AutoCompleteTextView etBookAuthor;
//
//    @ViewById
//    ImageView ivBookCover;
//
    @ViewById
    Button bPostAd;

    @ViewById
    SlidingUpPanelLayout suPanelLayout;

    @ViewById
    Button bExpandPanel;

    @ViewById
    ZBarScannerView fScanner;

    @ViewById
    LinearLayout dragView;

    @ViewById
    DynamicListView dlvScannedResult;


    String presentURL = "";
    ArrayList<BookItem> booksScanned = new ArrayList<BookItem>();
    ScannedBooksAdapter sbAdapter;

    BookItem scannedBook_google;
    BookItem scannedBook_flipkart;
    BookItem scannedBook;

    boolean isFlipkartRequestComplete = false;
    boolean isGoogleRequestComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        fScanner.stopCamera();
        GoogleBooksParser.setAndroidContext(this);


//        etBookName.setAdapter(new BooksAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
//
//        etBookName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                BookItem clickedItem = (BookItem) etBookName.getAdapter().getItem(position);
//                etBookName.setText(clickedItem.book_name);
//                etBookAuthor.setText(clickedItem.book_author);
//                Picasso.with(AddBooksActivity.this).load(clickedItem.book_cover_URL.replaceAll("[0-9]{1,3}x[0-9]{1,3}", "400x400")).into(ivBookCover);
//                presentURL = clickedItem.book_cover_URL;
//            }
//        });

        bPostAd.setVisibility(View.GONE);

        //Posting the add to parse
        bPostAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseObject toPostBook = new ParseObject("book");
                toPostBook.put("ISBN_13", scannedBook.book_ISBN_13);
                toPostBook.put("book_name", scannedBook.book_name);
                toPostBook.put("publish_date", scannedBook.book_publish_year);
                toPostBook.put("is_isbn_indexed", true);
                toPostBook.put("book_cover_url", scannedBook.book_cover_URL);
                toPostBook.put("book_authors", scannedBook.book_author);


                final ParseObject adlisting = new ParseObject("adlisting");

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    adlisting.put("ad_poster", currentUser);
                } else {
                    Toast.makeText(getApplicationContext(), "Not signed in", Toast.LENGTH_SHORT).show();
                }

                adlisting.put("book", toPostBook);
                adlisting.saveEventually();
            }
        });


        bExpandPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });


        suPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelCollapsed(View view) {
                fScanner.stopCamera();
            }

            @Override
            public void onPanelExpanded(View view) {
                fScanner.startCamera();
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
                fScanner.stopCamera();
            }
        });

        sbAdapter = new ScannedBooksAdapter(this, R.layout.scanned_book_item, booksScanned);
        dlvScannedResult.setAdapter(sbAdapter);
    }


    @Override
    public void onBackPressed() {

        fScanner.stopCamera();

        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            fScanner.stopCamera();

            Log.e("Scanner", "Back button pressed - stopped");
        } else {
            fScanner.stopCamera();

            Log.e("Scanner", "Back button pressed - stopped - finish");

            this.finish();

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            fScanner.startCamera();
            Log.e("Scanner", "onResume - SP Expanded - start");
        }

    }

    public void onPause() {
        super.onPause();

        Log.e("Scanner", "onPause - stop");

        fScanner.stopCamera();
    }

    public void doAfterScanResult(final String isbn) {
        bExpandPanel.setVisibility(View.GONE);
        bPostAd.setVisibility(View.VISIBLE);
        RequestQueue google_queue = Volley.newRequestQueue(this);
        RequestQueue flipkart_queue = Volley.newRequestQueue(this);

        Log.e("Scanned Result", isbn);

//        //Google result
//        JsonObjectRequest jsonRequest_google = new JsonObjectRequest
//                (Request.Method.GET, GoogleBooksParser.apiISBNURL + isbn, (String) null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log.e("VOLLEY", GoogleBooksParser.apiISBNURL + isbn);
//
//                        scannedBook_google = GoogleBooksParser.getBookFromJSON(response);
//
////                        etBookName.setText(scannedBook.book_name);
//                        booksScanned.add(scannedBook);
//
//                        sbAdapter.notifyDataSetChanged();
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//
//        jsonRequest_google.setRetryPolicy(new DefaultRetryPolicy(6000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        JsonObjectRequest jsonRequest_flipkart = new JsonObjectRequest
                (Request.Method.GET, ImportIOParser.makeFlipkartURLFromISBN(isbn), (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        scannedBook = ImportIOParser.getFPBookFromJSON(response);
                        scannedBook.book_ISBN_13 = isbn;

//                        etBookName.setText(scannedBook.book_name);
                        booksScanned.add(scannedBook);

                        sbAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsonRequest_flipkart.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


//        google_queue.add(jsonRequest_google);
        flipkart_queue.add(jsonRequest_flipkart);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
