package com.example.shubhamkanodia.bookmybook.Parsers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by shubhamkanodia on 12/05/15.
 */
public class GoogleBooksParser {

    final static String apiURL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String getCoverURLFromISBN(String ISBN) throws IOException, JSONException {

        InputStream is = new URL(apiURL + ISBN).openStream();

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json.getString("totalItems");
        } finally {
            is.close();
        }


//
//        InputStream is = new URL("apiURL" + ISBN).openStream();
//        try {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = readAll(rd);
//            JSONObject json = new JSONObject(jsonText);
//            return json;
//        } finally {
//            is.close();
//        }
    }

}
