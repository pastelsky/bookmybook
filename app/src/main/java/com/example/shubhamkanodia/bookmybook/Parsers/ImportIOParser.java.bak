package com.example.shubhamkanodia.bookmybook.Parsers;

import android.util.Log;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shubhamkanodia on 19/06/15.
 */
public class ImportIOParser {


    public static String makeFlipkartURLFromISBN(String isbn) {


        String madeURL = "https://api.import.io/store/data/9d957326-4d5c-4ed4-829d-945d04b1392e/_query?input/isbn=" + isbn + "&_user=47b7f8ed-a6d8-42f7-911b-2fd994b14ee1&_apikey=47b7f8ed-a6d8-42f7-911b-2fd994b14ee1%3AYHRKdkH%2BPEBK4Hztw3OSMpdZ6mwchcoLSDVI%2Bz3nADMUxtFEQQAzqjvbceeYqHS4%2BB7%2FhiUcugH0KLt42BL%2B1w%3D%3D";

        Log.e("URL-Made", madeURL);
        return madeURL;
    }

    public static BookItem getFPBookFromJSON(JSONObject json) {

        BookItem toReturn = new BookItem();
        try {
            JSONObject result = json.getJSONArray("results").getJSONObject(0);

            try {
                toReturn.book_cat_level_1 = result.getString("cat_level_1").equals("null") ? "" : result.getString("cat_level_1");
                toReturn.book_cat_level_2 = result.getString("cat_level_2").equals("null") ? "" : result.getString("cat_level_2");
                toReturn.book_cat_level_3 = result.getString("cat_level_3").equals("null") ? "" : result.getString("cat_level_3");
                toReturn.book_cat_level_4 = result.getString("cat_level_4").equals("null") ? "" : result.getString("cat_level_4");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid or NPE in book categories.. NOT PARSED!");

            }

            try {

                String pubYear = result.getString("pub_year");

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
                try {
                    Date date = formatter.parse(pubYear);
                    toReturn.book_publish_year = date;

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid Year");

            }

            try {
                toReturn.book_name = result.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid Book Name");

            }

            try {
                toReturn.book_flipkart_price = Integer.parseInt(result.getString("price"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid Price");

            }

            try {
                toReturn.book_cover_URL = result.getString("cover_url");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid Cover");

            }

            try {

//                String authorList[] = result.getString("author").split(",");
//                List<String> authors = new ArrayList<String>();
//
//                for(int i =0; i < authorList.length; i++)
//                {
//                    authors.add(i, authorList[i]);
//                }

                toReturn.book_author = result.getString("author");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ImportIO", "Invalid Author");

            }

            return toReturn;


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ImportIO", "Invalid json recieved - possbile timeout or api limit reached?");

            return null;

        }


    }

}
