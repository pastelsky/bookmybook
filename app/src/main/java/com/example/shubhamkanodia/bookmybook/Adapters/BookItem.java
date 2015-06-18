package com.example.shubhamkanodia.bookmybook.Adapters;

import java.util.Date;
import java.util.List;

/**
 * Created by shubhamkanodia on 09/05/15.
 */
public class BookItem {

    public String book_name;
    public String book_author;
    public String book_cover_URL;
    public String book_ISBN_13;
    public Date book_publish_year;
    public List<String> book_authors;
    public List<String> book_categories;

    public String book_seller_count;
    public String book_min_offer_price;


    public BookItem(String n, String a, String u) {
        this.book_author = a;
        this.book_name = n;
        this.book_cover_URL = u;

    }

    public BookItem(){

    }

    @Override
    public boolean equals(Object b){
            if (b == this) {
                return true;
            }
            if (!(b instanceof BookItem)) {
                return false;
            }
            BookItem c = (BookItem) b;

        return book_name.equals(c.book_name) && book_author.equals(c.book_author);
        }

    public void normalize(){

        this.book_name = toTitleCase(this.book_name);
        this.book_name = this.book_name.replaceAll("[(]?[pP]aper[bB]ack[)]?", "");
        this.book_name = this.book_name.replaceAll("[(]?[eE]nglish[)]?", "");
        this.book_author = toTitleCase(this.book_author);

    }

    private String toTitleCase(String input) {
        final String ACTIONABLE_DELIMITERS = " '-/(.,"; // these cause the character following
        // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : input.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }


    public String toString(){

        return this.book_name + " is the book!";
    }

}
