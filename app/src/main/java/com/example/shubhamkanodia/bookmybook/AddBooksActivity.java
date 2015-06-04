// Activity to add a new ad

package com.example.shubhamkanodia.bookmybook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BooksAutocompleteAdapter;
import com.example.shubhamkanodia.bookmybook.Parsers.GoogleBooksParser;
import com.parse.ParseObject;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


public class AddBooksActivity extends AppCompatActivity {


    AutoCompleteTextView etBookName;

    AutoCompleteTextView etBookAuthor;

    ImageView ivBookCover;
    Button bPostAd;


    String presentURL = "";

    final String bookCoverURL = "http://covers.librarything.com/devkey/57c8874fc25c78dfeaa2f8eba4455276/large/isbn/";

    ArrayList<String> bookSuggestions = new ArrayList<String>();
    ArrayAdapter bookSuggestionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        etBookAuthor = (AutoCompleteTextView) findViewById(R.id.etBookAuthor);
        etBookName = (AutoCompleteTextView) findViewById(R.id.etBookName);
        bPostAd = (Button) findViewById(R.id.bPostAd);

        etBookName.setAdapter(new BooksAutocompleteAdapter(this, android.R.layout.simple_list_item_1));

        etBookName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookItem clickedItem = (BookItem) etBookName.getAdapter().getItem(position);
                etBookName.setText(clickedItem.book_name);
                etBookAuthor.setText(clickedItem.book_author);
                Picasso.with(AddBooksActivity.this).load(clickedItem.book_cover_URL.replaceAll("[0-9]{1,3}x[0-9]{1,3}", "400x400")).into(ivBookCover);
                presentURL = clickedItem.book_cover_URL;
            }
        });

        bPostAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject gameScore = new ParseObject("Test");
                gameScore.put("text", etBookName.getText().toString());
                gameScore.put("author", etBookAuthor.getText().toString());
                gameScore.put("cover", presentURL);
                gameScore.saveInBackground();

                Intent intent = new Intent(AddBooksActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

//    @ItemClick
//    public void etBookName(int pos) {
//
//        BookItem clickedItem = (BookItem) etBookName.getAdapter().getItem(pos);
//        etBookName.setText(clickedItem.book_name);
//        etBookAuthor.setText(clickedItem.book_author);
//        Picasso.with(this).load(clickedItem.book_cover_URL.replaceAll("[0-9]{1,3}x[0-9]{1,3}", "400x400")).into(ivBookCover);
//        presentURL = clickedItem.book_cover_URL;
//
//    }

//    @Click
//    public void bPostAd() {
//        ParseObject gameScore = new ParseObject("Test");
//        gameScore.put("text", etBookName.getText().toString());
//        gameScore.put("author", etBookAuthor.getText().toString());
//        gameScore.put("cover", presentURL);
//        gameScore.saveInBackground();
//
//        Intent intent = new Intent(this, MainActivity_.class);
//        startActivity(intent);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.menu_add_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
