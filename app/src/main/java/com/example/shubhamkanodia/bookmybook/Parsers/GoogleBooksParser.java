package com.example.shubhamkanodia.bookmybook.Parsers;

import android.util.Log;

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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shubhamkanodia on 12/05/15.
 */
public class GoogleBooksParser {

    final static String apiURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,categories,imageLinks(smallThumbnail)))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=10&langRestrict=en&projection=lite&prettyPrint=false&q=intitle:";
    final static String apiAuthorURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(authors))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=1&projection=lite&prettyPrint=false&q=isbn:";
    final static String apiCategoriesURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(categories))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=1&projection=lite&prettyPrint=false&q=isbn:";

    final static String apiTitleURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,categories,imageLinks(smallThumbnail)))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=10&langRestrict=en&projection=lite&prettyPrint=false&q=isbn:";

    final static String apiPublishedURL = "https://www.googleapis.com/books/v1/volumes?fields=items(volumeInfo(title,authors,categories,publishedDate,imageLinks(smallThumbnail)))&key=AIzaSyDeA-dg07cO9ygUVkbCFSNqtL5WEIwwOBs&printType=books&maxResults=10&langRestrict=en&projection=lite&prettyPrint=false&q=isbn:";
    static String receivedJSON;

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

    static public String getJSONFromUrl(String isbn) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(URL + isbn);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                }
            }
        }
        return null;
    }


    static public String getAuthorFromISBN(String isbn) {
        DefaultHttpClient defaultClient = new DefaultHttpClient();
        HttpGet httpGetRequest = new HttpGet(apiAuthorURL + isbn);
        Log.e("Searching...", apiURL + isbn);

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

            try {
                JSONObject oneObject = jArray.getJSONObject(0);
                JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                return volumeInfo.getJSONArray("authors").join(", ").replaceAll("\"", "");


            } catch (JSONException e) {
                Log.e("JSONPrint", "JSOnExc...");

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error1...");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error2...");

        }

        return " ";
    }

    static public String getTitleFromISBN(String isbn) {
        DefaultHttpClient defaultClient = new DefaultHttpClient();
        HttpGet httpGetRequest = new HttpGet(apiTitleURL + isbn);
        Log.e("Searching...", apiTitleURL + isbn);

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

            try {
                JSONObject oneObject = jArray.getJSONObject(0);
                JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                Log.e("title", volumeInfo.getString("title"));
                return volumeInfo.getString("title");

            } catch (JSONException e) {
                Log.e("JSONPrint", "JSOnExc...");

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error1...");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error2...");

        }

        return " ";
    }

    //getting publish date
    static public String getPublishDateFromISBN(String isbn) {
        DefaultHttpClient defaultClient = new DefaultHttpClient();
        HttpGet httpGetRequest = new HttpGet(apiPublishedURL + isbn);
        Log.e("Searching...", apiPublishedURL + isbn);

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

            try {
                JSONObject oneObject = jArray.getJSONObject(0);
                JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                Log.e("publishedDate", volumeInfo.getString("publishedDate"));
                return volumeInfo.getString("publishedDate");


            } catch (JSONException e) {
                Log.e("JSONPrint", "JSOnExc...");

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error1...");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error2...");

        }

        return " ";
    }


    static public String getCategoriesFromISBN(String isbn) {
        DefaultHttpClient defaultClient = new DefaultHttpClient();
        HttpGet httpGetRequest = new HttpGet(apiCategoriesURL + isbn);
        Log.e("Searching...", apiCategoriesURL + isbn);

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

            try {
                JSONObject oneObject = jArray.getJSONObject(0);
                JSONObject volumeInfo = oneObject.getJSONObject("volumeInfo");
                return volumeInfo.getJSONArray("categories").join(", ").replaceAll("\"", "");


            } catch (JSONException e) {
                Log.e("JSONPrint", "JSOnExc...");

            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error1...");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONPrint", "Error2...");

        }

        return " ";
    }


}