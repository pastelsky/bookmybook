package com.example.shubhamkanodia.bookmybook.Parsers;

import android.util.Log;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by shubhamkanodia on 26/05/15.
 */
public class DataWeavePriceParser {

    final static String apiURL = "http://api.dataweave.in/v1/book_search/searchByTitle/?api_key=845969199fbea23f42118b44fd8278c0a9fc3e44&title=";
    static String receivedJSON;

    public static ArrayList<BookItem> getBookAutocompleteJSON(String query){

        DefaultHttpClient defaultClient = new DefaultHttpClient();
        HttpGet httpGetRequest = new HttpGet(apiURL + query.replaceAll("[ \n]+", "+"));
        Log.e("Searching...", apiURL + query.replaceAll("[ \n]+", "+"));

        ArrayList<BookItem> bookList = new ArrayList<>();

        HttpResponse httpResponse = null;
        try {
            httpResponse = defaultClient.execute(httpGetRequest);
        } catch (IOException e) {
            Log.e("JSONPrint", "IOerror...");
            e.printStackTrace();
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            receivedJSON = reader.readLine();

            JSONObject jsonObject = new JSONObject(receivedJSON);
            JSONArray jArray = jsonObject.getJSONArray("data");

            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    BookItem toInsert = new BookItem();
                    toInsert.book_name = oneObject.getString("title");
                    toInsert.book_author  = normalizeAuthorName(oneObject.getString("author"));
                    toInsert.book_cover_URL = oneObject.getString("thumbnail");
                    toInsert.book_ISBN_13 = oneObject.getString("isbn");

                    toInsert.normalize();

                    if(!bookList.contains(toInsert) && toInsert.book_name.length() < 70)
                        bookList.add(toInsert);



                } catch (JSONException e) {
                    Log.e("JSONPrint", "JSOnExc...");

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error1...");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error2...");

        }
        return bookList;
    }

    private static String normalizeAuthorName(String authorNames){

        //Solves |author appended at the end
        return authorNames.replaceAll("[|]?[aA]uthor[;]", "");

    }
}
