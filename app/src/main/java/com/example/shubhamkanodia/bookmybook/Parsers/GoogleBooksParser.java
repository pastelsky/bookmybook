package com.example.shubhamkanodia.bookmybook.Parsers;

import android.util.Log;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.androidannotations.annotations.Background;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by shubhamkanodia on 12/05/15.
 */
public class GoogleBooksParser {

    final static String apiURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,categories,imageLinks(smallThumbnail)))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=10&langRestrict=en&projection=lite&prettyPrint=false&q=intitle:";
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
            JSONArray jArray = jsonObject.getJSONArray("items");

            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                    Log.e("BookBook", volumeInfo.getString("title"));

                    BookItem toInsert = new BookItem();
                    toInsert.book_name = volumeInfo.getString("title");
                    toInsert.book_author  = volumeInfo.getJSONArray("authors").join(", ").replaceAll("\"", "");

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    toInsert.book_cover_URL = imageLinks.getString("smallThumbnail");

                    JSONObject industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers").getJSONObject(1);
                    toInsert.book_ISBN_13 = industryIdentifiers.getString("identifier");

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



}
