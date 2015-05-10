package com.example.shubhamkanodia.bookmybook;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class DisplayBookListing extends ActionBarActivity {

    private ImageView ivBookCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_book_listing);

        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);

        //Set Toolbar color
        final Toolbar toolbar = (Toolbar) findViewById(R.id.extended_toolbar);
        setSupportActionBar(toolbar);

        Palette palette = Palette.generate(((BitmapDrawable) ivBookCover.getDrawable()).getBitmap());
        int vibrant = palette.getVibrantColor(0x000000);
        int vibrantLight = palette.getLightVibrantColor(0x000000);
        int vibrantDark = palette.getDarkVibrantColor(0x000000);
        int muted = palette.getMutedColor(0x000000);
        int mutedLight = palette.getLightMutedColor(0x000000);
        int mutedDark = palette.getDarkMutedColor(0x000000);

        float[] hsv = new float[3];
        float[] hsv2 = new float[3];


        Color.colorToHSV(vibrantLight, hsv);
        Color.colorToHSV(mutedLight, hsv2);

        int toolbarTextcolor = hsv[2] > hsv2[2] ? vibrantLight : mutedLight;

        toolbar.setBackgroundDrawable(new ColorDrawable(vibrantDark));
        toolbar.setTitleTextColor(toolbarTextcolor);


        int statusColor = vibrantDark;
        Color.colorToHSV(statusColor, hsv);
        hsv[2] *= 0.8f; // value component
        statusColor = Color.HSVToColor(hsv);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusColor);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_book_listing, menu);
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
