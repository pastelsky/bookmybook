package com.example.shubhamkanodia.bookmybook.Parsers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shubhamkanodia on 28/05/15.
 */
public class FlipkartAutocompleteParser {

    final static String apiURL = "http://www.flipkart.com/s?query=";
    static String receivedJSON;
    static ArrayList<BookItem> bookList;

    public static ArrayList<BookItem> getBookAutocompleteJSON(String query, Context c){

        RequestQueue queue = Volley.newRequestQueue(c);

        Log.e("Searching...", apiURL + query.replaceAll("[ \n]+", "+"));

        bookList = new ArrayList<BookItem>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiURL + query.replaceAll("[ \n]+", "+"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Pattern pattern = Pattern.compile("\"(978[\\d]{10})\",\"([^\"]+)\",\"([^\"]+)\"", Pattern.MULTILINE);
                        Matcher matcher = pattern.matcher(response);
                        while (matcher.find()) {
                            BookItem toInsert = new BookItem();
                            toInsert.book_ISBN_13 = matcher.group(1);
                            toInsert.book_name = matcher.group(2);
//                toInsert.book_author  = GoogleBooksParser.getAuthorFromISBN(toInsert.book_ISBN_13);
                            toInsert.book_cover_URL = matcher.group(3).replaceAll("[0-9]{1,3}x[0-9]{1,3}", "100x100").replace("\\/", "/");
                            Log.e("TEST", toInsert.book_cover_URL);
                            bookList.add(toInsert);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);

        return bookList;
    }

    private static String normalizeAuthorName(String authorNames){

        //Solves |author appended at the end
        return authorNames.replaceAll("[|]?[aA]uthor[;]", "");

    }
}
