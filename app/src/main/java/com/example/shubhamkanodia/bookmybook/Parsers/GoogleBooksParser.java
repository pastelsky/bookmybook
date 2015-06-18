package com.example.shubhamkanodia.bookmybook.Parsers;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.androidannotations.annotations.Background;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shubhamkanodia on 12/05/15.
 */
public class GoogleBooksParser {

    final static String apiURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,categories,imageLinks(smallThumbnail)))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=10&langRestrict=en&projection=lite&prettyPrint=false&q=intitle:";
    final static String apiAuthorURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(authors))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=1&projection=lite&prettyPrint=false&q=isbn:";
    final public static String apiISBNURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,publishedDate,categories))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=1&langRestrict=en&prettyPrint=false&q=isbn:";

    static String receivedJSON;
    static Context context;


    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    final static String URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public static ArrayList<BookItem> getBookAutocompleteJSON(String query) {

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

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                    Log.e("BookBook", volumeInfo.getString("title"));

                    BookItem toInsert = new BookItem();
                    toInsert.book_name = volumeInfo.getString("title");
                    toInsert.book_author = volumeInfo.getJSONArray("authors").join(", ").replaceAll("\"", "");

                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    toInsert.book_cover_URL = imageLinks.getString("smallThumbnail");

                    JSONObject industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers").getJSONObject(1);
                    toInsert.book_ISBN_13 = industryIdentifiers.getString("identifier");

                    if (!bookList.contains(toInsert) && toInsert.book_name.length() < 70)
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


    public static void setAndroidContext(Context c) {
        context = c;
    }

    public static BookItem getBookFromJSON(JSONObject json) {

        BookItem toReturn = new BookItem();
        toReturn.book_name = "bullshit";


        JSONArray items = null;
        try {

            items = json.getJSONArray("items");

            JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
            Log.e("volumeInfo  is " , ": " + volumeInfo);


            //Book Name
            toReturn.book_name = volumeInfo.getString("title");

            Log.e("Okay", "Fetched bookname now.....");


            //Book Authors
            JSONArray authors = new JSONArray();
            authors = volumeInfo.getJSONArray("authors");
            List<String> author_list = new ArrayList<String>();
            for (int i = 0; i < authors.length(); i++) {
                author_list.add(authors.getString(i));
            }

            toReturn.book_authors = author_list;

            Log.e("Okay", "Fetched authores now.....");


            //Book publishedDate
            String pubYear = volumeInfo.getString("publishedDate").substring(0, 4);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            try {
                Date date = formatter.parse(pubYear);
                toReturn.book_publish_year = date;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("Okay", "Fetched pub now.....");

            //Book Categories
            JSONArray categories = new JSONArray();
            categories = volumeInfo.getJSONArray("categories");
            List<String> cat_list = new ArrayList<String>();
            for (int i = 0; i < categories.length(); i++) {
                cat_list.add(categories.getString(i));
            }

            toReturn.book_categories = cat_list;
            Log.e("Okay", "Returning now.....");
            return toReturn;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }



    }
}