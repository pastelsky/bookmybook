// Activity to add a new ad

package com.example.shubhamkanodia.bookmybook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BooksAutocompleteAdapter;
import com.example.shubhamkanodia.bookmybook.Parsers.GoogleBooksParser;
import com.parse.ParseObject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.zbar.ZBarScannerView;


@EActivity
public class AddBooksActivity extends AppCompatActivity {


    @ViewById
    AutoCompleteTextView etBookName;
    
    @ViewById
    AutoCompleteTextView etBookAuthor;

    @ViewById
    ImageView ivBookCover;

    @ViewById
    Button bPostAd;

    @ViewById 
    SlidingUpPanelLayout suPanelLayout;

    @ViewById
    Button bExpandPanel;

    @ViewById
    ZBarScannerView fScanner;

    String presentURL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        fScanner.stopCamera();
        GoogleBooksParser.setAndroidContext(this);


        etBookName.setAdapter(new BooksAutocompleteAdapter(this, android.R.layout.simple_list_item_1));

        etBookName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookItem clickedItem = (BookItem) etBookName.getAdapter().getItem(position);
                etBookName.setText(clickedItem.book_name);
                etBookAuthor.setText(clickedItem.book_author);
                Picasso.with(AddBooksActivity.this).load(clickedItem.book_cover_URL.replaceAll("[0-9]{1,3}x[0-9]{1,3}", "400x400")).into(ivBookCover);
                presentURL = clickedItem.book_cover_URL;
            }
        });


        bExpandPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                ScannerFragment fragment = (ScannerFragment) getSupportFragmentManager().findFragmentById(R.id.fScanner);
                fScanner.startCamera();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            fScanner.stopCamera();
        } else {
            fScanner.stopCamera();
        }
    }

    public void doAfterScanResult(String isbn){


        Log.e("Scanned Result", isbn);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, GoogleBooksParser.apiISBNURL + isbn, (String)null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Response:", "is " + response);


                        BookItem scannedBook = GoogleBooksParser.getBookFromJSON(response);

                        Log.e("The book is " , ":"+ GoogleBooksParser.getBookFromJSON(response).book_name);
                        etBookName.setText(scannedBook.book_name);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(jsonRequest);

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
