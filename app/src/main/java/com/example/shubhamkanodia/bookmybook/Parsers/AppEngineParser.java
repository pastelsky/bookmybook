package com.example.shubhamkanodia.bookmybook.Parsers;

import android.util.Log;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shubhamkanodia on 23/06/15.
 */
public class AppEngineParser {

    public static String appEngineApiURL = "http://bookmybook-963.appspot.com/q2?isbn=";

    public static BookItem getBookFromJSON(JSONObject json){

BookItem toReturn = new BookItem();

        try{
        toReturn.book_name = json.getString("name");
        toReturn.book_author = json.getString("author");
        toReturn.book_cover_URL = json.getString("cover_url");
        toReturn.book_ISBN_13 = json.getString("isbn");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            try {
                Date pub_date = formatter.parse(json.getString("pub_year"));
                toReturn.book_publish_year = pub_date;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        toReturn.book_new_edition_URL = json.getString("new_edition_url");
        toReturn.book_language = json.getString("language");

        toReturn.book_cat_level_1 = json.getString("cat_level_1");
        toReturn.book_cat_level_2 = json.getString("cat_level_2");
        toReturn.book_cat_level_3 = json.getString("cat_level_3");
        toReturn.book_cat_level_4 = json.getString("cat_level_4");

        toReturn.book_flipkart_price =Integer.parseInt(json.getString("flipkart_price"));
            toReturn.book_mrp = Integer.parseInt(json.getString("mrp"));

            return toReturn;
        }

        catch (JSONException e) {
            Log.e("JSONPrint", "JSOnExc...");
            return null;

        }

    }
}
