package com.example.shubhamkanodia.bookmybook.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.Parsers.DataWeavePriceParser;
import com.example.shubhamkanodia.bookmybook.Parsers.FlipkartAutocompleteParser;
import com.example.shubhamkanodia.bookmybook.Parsers.GoogleBooksParser;
import com.example.shubhamkanodia.bookmybook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shubhamkanodia on 25/05/15.
 */
public class BooksAutocompleteAdapter extends ArrayAdapter<BookItem> implements Filterable {

    private ArrayList<BookItem> bookResults;

    Context mContext;
    int mResource;
    ViewHolder holder;

    public BooksAutocompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return bookResults.size();
    }

    @Override
    public BookItem getItem(int i){
        return  bookResults.get(i);
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    bookResults = FlipkartAutocompleteParser.getBookAutocompleteJSON(constraint.toString());

                    filterResults.values = bookResults;
                    filterResults.count = bookResults.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)

        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.autocomplete_book_item, null);

            holder = new ViewHolder();
            holder.bName = (TextView) convertView.findViewById(R.id.tvBookName);
            holder.bAuthour = (TextView) convertView.findViewById(R.id.tvBookAuthor);
            holder.bCover = (ImageView) convertView.findViewById(R.id.ivBookCover);
            convertView.setTag(holder);



        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        BookItem book = getItem(position);

        holder.bName.setText(book.book_name);
        holder.bAuthour.setText(book.book_author);
        Picasso.with(this.mContext).load(book.book_cover_URL).into(holder.bCover);


        return convertView;

    }
    static class ViewHolder {
        TextView bName;
        TextView bAuthour;
        ImageView bCover;
        int position;
    }
}
