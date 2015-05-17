package com.example.shubhamkanodia.bookmybook.Adapters;

/**
 * Created by shubhamkanodia on 09/05/15.
 */
public class BookItem {

    public String book_name;
    public String book_author;
    public String book_cover_URL;
    public String book_ISBN;

    public String book_seller_count;
    public String book_min_offer_price;


    public BookItem(String n, String a, String u) {
        this.book_author = a;
        this.book_name = n;
        this.book_cover_URL = u;

    }

}
