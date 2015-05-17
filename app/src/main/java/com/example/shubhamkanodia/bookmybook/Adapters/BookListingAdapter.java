package com.example.shubhamkanodia.bookmybook.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by shubhamkanodia on 09/05/15.
 */


public class BookListingAdapter extends ArrayAdapter<BookItem> {

    Context context;
    boolean[] animationStates;
    ListView targetListView;
    // declaring our ArrayList of items
    private ArrayList<BookItem> books;

    public BookListingAdapter(Context context, int textViewResourceId, ArrayList<BookItem> objects) {
        super(context, textViewResourceId, objects);
        animationStates = new boolean[objects.size()];
        this.books = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null)

        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.book_item, null);
            targetListView = (ListView) parent;

            holder = new ViewHolder();
            holder.bName = (TextView) convertView.findViewById(R.id.bName);
            holder.bAuthour = (TextView) convertView.findViewById(R.id.bAuthor);
            holder.bCover = (ImageView) convertView.findViewById(R.id.ivBookCover);
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

        holder.bName.setText(book.book_name);
        holder.bAuthour.setText(book.book_author);
        Picasso.with(this.context).load(book.book_cover_URL).into(holder.bCover);

        return convertView;

    }

    public byte[] getCoverByPosition(int pos) {

        //Get view clicked
        View clickedView = getViewByPosition(pos);


        ImageView ivBookCover = (ImageView) clickedView.findViewById(R.id.ivBookCover);
        BitmapDrawable drawable = (BitmapDrawable) ivBookCover.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }

    public View getViewByPosition(int pos) {
        final int firstListItemPosition = targetListView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + targetListView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return targetListView.getAdapter().getView(pos, null, targetListView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return targetListView.getChildAt(childIndex);
        }

    }


	/*
     * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */

    static class ViewHolder {
        TextView bName;
        TextView bAuthour;
        ImageView bCover;
        int position;
    }

}