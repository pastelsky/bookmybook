package com.example.shubhamkanodia.bookmybook.Adapters;

/**
 * Created by shubhamkanodia on 09/05/15.
 */
public class BookItem {

    private String book_name;
    private String book_author;
    private boolean isAnimated;
    private String coverURL;


    public BookItem(){
        isAnimated = true;
    }

    public BookItem(String n, String a, String u) {
        this.book_author = a;
        this.book_name = n;
        this.coverURL = u;
    }

    public boolean getAnimation(){
        return isAnimated;
    }

    public void setAnimation(boolean a) {
        isAnimated = a;
    }

    public String getCoverURL() {
        return this.coverURL;
    }

    public void setCoverURL(String c) {
        this.coverURL = c;
    }

    public String getName() {
        return book_name;
    }

  public void setName(String n){
      book_name = n;
  }

    public String getAuthor(){
        return book_author;
    }

    public void setAuthor(String a) {
        book_name = a;
    }

}
