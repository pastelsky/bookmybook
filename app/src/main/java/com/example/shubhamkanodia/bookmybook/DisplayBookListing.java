package com.example.shubhamkanodia.bookmybook;

import android.app.SharedElementCallback;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

public class DisplayBookListing extends AppCompatActivity implements ObservableScrollViewCallbacks {

    static boolean isComplete = false;

    ImageView ivBookCover;
    Toolbar tbExtended;
    TextView tvBookName;
    TextView tvBookAuthor;
    ObservableListView lvAds;
    RelativeLayout rvBookPanel;
    SwitchCompat switch_compat;

    private int initialCoverWidth;
    private int initialToolBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_book_listing);

        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tbExtended = (Toolbar) findViewById(R.id.tbExtended);
        tvBookName = (TextView) findViewById(R.id.tvBookName);
        tvBookAuthor = (TextView) findViewById(R.id.tvBookAuthor);
        lvAds = (ObservableListView) findViewById(R.id.lvAds);
        rvBookPanel = (RelativeLayout) findViewById(R.id.rvBookPanel);
        switch_compat = (SwitchCompat) findViewById(R.id.switch_compat);

        isComplete = false;

        Helper.setAndroidContext(this);

        setSupportActionBar(tbExtended);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        lvAds.setScrollViewCallbacks(this);

        if (Helper.isLollipop()) {
            setEnterSharedElementCallback(new SharedElementCallback() {

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames,
                                               List<View> sharedElements, List<View> sharedElementSnapshots) {
                    isComplete = true;

                }
            });
        } else {
            isComplete = true;
        }

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("bookCover");
        tvBookName.setText(extras.getString("bookName"));
        tvBookAuthor.setText(extras.getString("bookAuthor"));

        initialCoverWidth = ivBookCover.getLayoutParams().width;

        //weightsum ratio 1:3
        initialToolBarHeight = Helper.getDeviceHeight() / 4;

        // Defined Array values to show in ListView
        String[] values = new String[]{"Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        lvAds.setAdapter(adapter);

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Drawable d = new BitmapDrawable(getResources(), bmp);
        d.setFilterBitmap(true);
        ivBookCover.setImageDrawable(d);
        setStatusAndToolbarColor();


    }

    public void onStart() {
        super.onStart();
        isComplete = false;
    }

    public void onPause() {
        super.onPause();
        isComplete = true;
    }

    public void onStop() {
        super.onStop();
        isComplete = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_display_book_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setStatusAndToolbarColor() {
        Palette palette = Palette.generate(((BitmapDrawable) ivBookCover.getDrawable()).getBitmap());
        int vibrantLight = palette.getLightVibrantColor(0x000000);
        int vibrantDark = palette.getDarkVibrantColor(0x000000);
        int mutedLight = palette.getLightMutedColor(0x000000);

        int toolbarTextcolor = vibrantLight;
        float hsv[];
        hsv = new float[2];

        tbExtended.setBackgroundDrawable(new ColorDrawable(vibrantDark));
        tvBookName.setTextColor(toolbarTextcolor);


        int statusColor = vibrantDark;
        Color.colorToHSV(statusColor, hsv);
        hsv[2] *= 0.8f; // value component
        statusColor = Color.HSVToColor(hsv);

        AnimationHelper.statusBarColorTransition(statusColor, 400);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean b1) {

        if (isComplete) {

            final int newCoverWidth = (int) (initialCoverWidth - (float) scrollY / 3);
            int newToolbarHeight = (int) (initialToolBarHeight - (float) scrollY / 2);
            final int defaultToolbarSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());

            ivBookCover.getLayoutParams().width = Math.max(newCoverWidth, initialCoverWidth / 2);
//        ivBookCover.setAlpha(Math.max( 1 - (float)scrollY/200, 0));
            tbExtended.getLayoutParams().height = Math.max(newToolbarHeight, initialToolBarHeight / 2);
            rvBookPanel.setTranslationY(Math.max(-scrollY / 4, -initialToolBarHeight / 4));

            ivBookCover.requestLayout();
            tbExtended.requestLayout();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
