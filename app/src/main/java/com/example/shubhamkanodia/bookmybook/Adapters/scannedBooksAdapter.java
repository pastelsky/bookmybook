package com.example.shubhamkanodia.bookmybook.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.R;
import com.github.florent37.materialimageloading.MaterialImageLoading;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by shubhamkanodia on 09/05/15.
 */


public class ScannedBooksAdapter extends ArrayAdapter<BookItem> {

    private ArrayList<BookItem> books;
    Context context;
    ViewHolder holder;

    DynamicListView dlvScannedResults;

    public ScannedBooksAdapter(Context context, int textViewResourceId, ArrayList<BookItem> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        books = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)

        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.scanned_book_item, null);
            dlvScannedResults = (DynamicListView) parent;

            holder = new ViewHolder();

            holder.tvBookName = (TextView) convertView.findViewById(R.id.tvBookName);
            holder.tvBookAuthors = (TextView) convertView.findViewById(R.id.tvBookAuthors);
            holder.ivBookCover = (ImageView) convertView.findViewById(R.id.ivBookCover);
            holder.tvBookCategories = (TextView) convertView.findViewById(R.id.tvBookCategories);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        BookItem book = getItem(position);

        holder.tvBookName.setText(book.book_name);
        holder.tvBookAuthors.setText(book.book_authors + " ");

        Log.e("COVER URL", book.book_cover_URL);
        Picasso.with(this.context).load(book.book_cover_URL).into(holder.ivBookCover, new com.squareup.picasso.Callback() {

            @Override
            public void onSuccess() {
                MaterialImageLoading.animate(holder.ivBookCover).setDuration(2000).start();
            }

            @Override
            public void onError() {

            }
        });

        return convertView;

    }

    static class ViewHolder {
        TextView tvBookName;
        TextView tvBookAuthors;
        TextView tvBookCategories;
        ImageView ivBookCover;
    }

}