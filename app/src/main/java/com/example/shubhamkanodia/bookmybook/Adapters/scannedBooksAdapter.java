package com.example.shubhamkanodia.bookmybook.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.daimajia.easing.quad.QuadEaseInOut;
import com.example.shubhamkanodia.bookmybook.AddBooksActivity;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.R;
import com.github.florent37.materialimageloading.MaterialImageLoading;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.animateaddition.AnimateAdditionAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
import com.nhaarman.listviewanimations.util.Insertable;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shubhamkanodia on 09/05/15.
 */


public class ScannedBooksAdapter extends ArrayAdapter<BookItem>{

    private ArrayList<BookItem> books;
    Context context;
    ViewHolder holder;
    static int toAnimate = 0;
    HashMap<BookItem, Integer> mIdMap = new HashMap<BookItem, Integer>();

    DynamicListView dlvScannedResults;

    public ScannedBooksAdapter(Context context, int textViewResourceId, ArrayList<BookItem> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        books = objects;
    }

    public void remove(int position){
        books.remove(books.get(position));
    }

    public int getItemCount(){
        return books.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)

        {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.scanned_book_item, null);
//            dlvScannedResults = (DynamicListView) parent;

            holder = new ViewHolder();

            holder.tvBookName = (EditText) convertView.findViewById(R.id.tvBookName);
            holder.tvBookAuthor = (EditText) convertView.findViewById(R.id.tvBookAuthors);
            holder.ivBookCover = (ImageView) convertView.findViewById(R.id.ivBookCover);
            holder.rangeBar = (SeekBar) convertView.findViewById(R.id.rangebar);
            holder.tvCurrentPrice = (TextView) convertView.findViewById(R.id.tvCurrentPrice);
            holder.lvFlipHint = (LinearLayout) convertView.findViewById(R.id.lvFlipHint);
            holder.tvfpPrice = (TextView) convertView.findViewById(R.id.tvfpPrice);
            holder.lvMRPHint = (LinearLayout) convertView.findViewById(R.id.lvMRPHint);
            holder.tvMRPPrice = (TextView) convertView.findViewById(R.id.tvMRPPrice);
            holder.rlPlacePicker = (RelativeLayout) convertView.findViewById(R.id.rlPlacePicker);
            holder.tvLabel1 = (TextView) convertView.findViewById(R.id.tvLabel1);
            holder.ibRemove = (ImageButton) convertView.findViewById(R.id.ibRemove);
            holder.ibRemove.setTag(position);

            convertView.setTag(holder);
            holder.rangeBar.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final BookItem book = getItem(position);

        holder.tvBookName.setText(book.book_name);
        holder.tvBookAuthor.setText(book.book_author);
        holder.tvMRPPrice.setText("\u20B9" + book.book_mrp);
        holder.tvfpPrice.setText("\u20B9" + book.book_flipkart_price);
        holder.tvLabel1.setText("#" + getItemCount());

        holder.rlPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    ((AddBooksActivity) context).clickedListPosition = position;
                    ((Activity) context).startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.ibRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                books.remove(view.getTag());
                notifyDataSetChanged();
            }
        });


        final int MRP = book.book_mrp;
        final int fpPrice = book.book_flipkart_price;

        holder.rangeBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.rangeBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    holder.rangeBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                holder.rangeBar.setMax(MRP);

                int rangeBarWidth = holder.rangeBar.getWidth() - holder.rangeBar.getPaddingRight() - Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()));
                int adjustedWidthFP = rangeBarWidth -  holder.lvFlipHint.getWidth() / 2;
                int adjustedWidthMRP = rangeBarWidth -  holder.lvMRPHint.getWidth() / 2 + Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));



                Log.e("FP", fpPrice + "");
                Log.e("MRP", MRP + "");
                Log.e("rangeBarWidth", rangeBarWidth + "");

                float toMoveMRP =adjustedWidthMRP;
                float toMoveFP = (float)fpPrice / MRP * adjustedWidthFP ;

                Log.e("toMoveFP", toMoveFP + "");


                holder.lvFlipHint.setTranslationX(toMoveFP);
                holder.lvMRPHint.setTranslationX(toMoveMRP);

                holder.rangeBar.setProgress(Math.round(MRP * 0.55f));

            }
        });


        holder.rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar rangeBar, int i, boolean b) {

                ViewHolder mH = (ViewHolder) rangeBar.getTag();
                mH.tvCurrentPrice.setText(i + "");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                ViewHolder mH = (ViewHolder) seekBar.getTag();
                mH.tvfpPrice.animate().setDuration(200).translationY(-8).alpha(1).start();
                mH.tvMRPPrice.animate().setDuration(200).translationY(-8).alpha(1).start();



            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {

                ViewHolder mH = (ViewHolder) seekBar.getTag();
                mH.tvfpPrice.animate().setDuration(200).translationY(0).alpha(0).start();
                mH.tvMRPPrice.animate().setDuration(200).translationY(0).alpha(0).start();



                int curProgress = seekBar.getProgress();

                if( curProgress > book.book_mrp * 0.95  && curProgress < book.book_mrp *1.05 ){

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ViewHolder mH = (ViewHolder) seekBar.getTag();
                            mH.rangeBar.setProgress(book.book_mrp);

                        }
                    });
                }

                else if( curProgress > book.book_flipkart_price * 0.95  && curProgress < book.book_flipkart_price *1.05 ){

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ViewHolder mH = (ViewHolder) seekBar.getTag();
                            mH.rangeBar.setProgress(book.book_flipkart_price);

                        }
                    });
                }
            }
        });

        Picasso.with(this.context).load(book.book_cover_URL).into(holder.ivBookCover);
        return convertView;

    }

    static class ViewHolder {
        EditText tvBookName;
        EditText tvBookAuthor;
        TextView tvBookCategories;
        ImageView ivBookCover;

        SeekBar rangeBar;
        TextView tvCurrentPrice;
        LinearLayout lvFlipHint;
        TextView tvfpPrice;

        LinearLayout lvMRPHint;
        TextView tvMRPPrice;

        RelativeLayout rlPlacePicker;
        TextView tvLabel1;

        ImageButton ibRemove;
    }

}