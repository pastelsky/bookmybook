package com.example.shubhamkanodia.bookmybook.Adapters;

/**
 * Created by shubhamkanodia on 09/05/15.
 */
public class BookItem {

    private String book_name;
    private String book_author;
private boolean isAnimated;


    public BookItem(){
        isAnimated = true;

    }

    public void setAnimation(boolean a){
        isAnimated = a;
    }

    public boolean getAnimation(){
        return isAnimated;
    }

    public BookItem(String n, String a){
        this.book_author = a;
        this.book_name = n;
    }

  public void setName(String n){
      book_name = n;
  }

    public void setAuthor(String a){
        book_name = a;
    }

    public String getName(){
        return book_name;
    }

    public String getAuthor(){
        return book_author;
    }

}
