package com.example.shubhamkanodia.bookmybook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.R;
import com.nhaarman.listviewanimations.util.Insertable;

import java.util.ArrayList;

import android.widget.ArrayAdapter;

/**
 * Created by shubhamkanodia on 09/05/15.
 */


public class BookListingAdapter extends ArrayAdapter<BookItem> {


    // declaring our ArrayList of items
    private ArrayList<BookItem> books;
    boolean[] animationStates;

    static class ViewHolder {
        TextView bName;
        TextView bAuthour;
        ImageView bCover;
        int position;
    }


    public BookListingAdapter(Context context, int textViewResourceId, ArrayList<BookItem> objects) {
        super(context, textViewResourceId, objects);
        animationStates = new boolean[objects.size()];
        this.books = objects;
    }

	/*
     * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null)

        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.book_item, null);
            holder = new ViewHolder();
            holder.bName = (TextView) convertView.findViewById(R.id.bName);
            holder.bAuthour = (TextView) convertView.findViewById(R.id.bAuthor);
            convertView.setTag(holder);
            if (!animationStates[position]) {
                animationStates[position] = true;
                Animation animationListView = AnimationUtils.loadAnimation(getContext(), R.anim.up_from_bottom);
                animationListView.setStartOffset((position * 80));
                convertView.startAnimation(animationListView);

            }


        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        BookItem book = getItem(position);

        holder.bName.setText(book.getName());
        holder.bAuthour.setText(book.getAuthor());



        return convertView;

    }

}