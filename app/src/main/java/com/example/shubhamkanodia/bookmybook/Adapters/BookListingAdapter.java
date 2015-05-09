package com.example.shubhamkanodia.bookmybook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.R;
import com.nhaarman.listviewanimations.util.Insertable;

import java.util.ArrayList;
import android.widget.ArrayAdapter;
/**
 * Created by shubhamkanodia on 09/05/15.
 */


public class BookListingAdapter extends ArrayAdapter<BookItem>  {


    // declaring our ArrayList of items
    private ArrayList<BookItem> books;
    private int lastPosition = -1;


    public BookListingAdapter(Context context, int textViewResourceId, ArrayList<BookItem> objects) {
        super(context, textViewResourceId, objects);
        this.books = objects;
    }

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {






        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.book_item, null);
        }

        BookItem p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.bName);
            TextView tt2 = (TextView) v.findViewById(R.id.bAuthor);

            if (tt1 != null) {
                tt1.setText(p.getName());
            }

            if (tt2 != null) {
                tt2.setText(p.getAuthor());
            }


        }

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.up_from_bottom);
            animation.setStartOffset((position * 80));
            v.startAnimation(animation);
            lastPosition = position;


        return v;
    }

}