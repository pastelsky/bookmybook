// Activity to add a new ad

package com.example.shubhamkanodia.bookmybook;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.ScannedBooksAdapter;
import com.example.shubhamkanodia.bookmybook.Helpers.AnimationHelper;
import com.example.shubhamkanodia.bookmybook.Helpers.Helper;
import com.example.shubhamkanodia.bookmybook.Parsers.AppEngineParser;
import com.example.shubhamkanodia.bookmybook.UI.widget.RippleButton;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.pixplicity.easyprefs.library.Prefs;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


@EActivity
public class AddBooksActivity extends AppCompatActivity {


    @ViewById
    SlidingUpPanelLayout suPanelLayout;

    @ViewById
    RippleButton bExpandPanel;

    @ViewById
    ZBarScannerView fScanner;

    @ViewById
    RelativeLayout dragView;

    @ViewById
    DynamicListView dlvScannedResult;

    @ViewById
    Toolbar tbMain;

    @ViewById
    View vLaser;

    @ViewById
    RelativeLayout rvEmptyLv;

    @ViewById
    ImageButton ibFlashControl;

    @ViewById
    ImageButton ibAutofocus;

    @ViewById
    FrameLayout flScan;

    @ViewById
    CardView cvLoading;


    ArrayList<BookItem> booksScanned = new ArrayList<BookItem>();
    ScannedBooksAdapter sbAdapter;

    boolean isAutofocus = true;
    boolean isFlash = false;
    boolean didUserScan = false;
    boolean areBooksAvailable = false;
    public static int clickedListPosition;
    private AnimationSet blinkMove;

    MenuItem postButton;
    MenuItem addMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        fScanner.stopCamera();
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

        formats.add(BarcodeFormat.EAN13);
        fScanner.setFormats(formats);

        setSupportActionBar(tbMain);


        dlvScannedResult.setEmptyView(rvEmptyLv);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();


        suPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {}

            @Override
            public void onPanelCollapsed(View view) {

                if (!didUserScan)
                    AnimationHelper.fadeIn((View) rvEmptyLv, 120);
                fScanner.stopCamera();
            }

            @Override
            public void onPanelExpanded(View view) {
                fScanner.startCamera();

                if (isFlash)
                    fScanner.setFlash(true);
            }

            @Override
            public void onPanelAnchored(View view) {}

            @Override
            public void onPanelHidden(View view) {fScanner.stopCamera();}
        });




    }


    @Click
    public void bExpandPanel(){

        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        AnimationHelper.fadeOut((View) rvEmptyLv, 200);
    }


    @Click
    public void ibAutofocus() {

        if (isAutofocus) {
            fScanner.setAutoFocus(false);
            ibAutofocus.setImageResource(R.mipmap.ic_action_autofocus_off);

        } else {
            fScanner.setAutoFocus(true);
            ibAutofocus.setImageResource(R.mipmap.ic_action_auto_focus_on);

        }

        isAutofocus = !isAutofocus;

    }


    @Click
    public void ibFlashControl() {

        if (isFlash) {
            fScanner.setFlash(false);
            ibFlashControl.setImageResource(R.mipmap.ic_action_image_flash_off);
            Toast.makeText(this, "Toggle Flash ON/OFF", Toast.LENGTH_LONG).setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);


        } else {
            fScanner.setFlash(true);
            ibFlashControl.setImageResource(R.mipmap.ic_action_image_flash_on);
        }

        isFlash = !isFlash;

    }


    @Override
    public void onBackPressed() {

        fScanner.stopCamera();

        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            fScanner.stopCamera();

            Log.e("Scanner", "Back button pressed - stopped");
        } else {
            fScanner.stopCamera();

            Log.e("Scanner", "Back button pressed - stopped - finish");

            this.finish();

        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            fScanner.startCamera();
            Log.e("Scanner", "onResume - SP Expanded - start");
        }


        //Code to restore data from previous scans
        Type type = new TypeToken<List<BookItem>>(){}.getType();
        Gson gson = new Gson();
        String toRestore = Prefs.getString("saved_scanned_books", "");
        ArrayList<BookItem> temp = new ArrayList<BookItem>();
        temp = gson.fromJson(toRestore, type);

        if(toRestore != "" && temp.size() > 0) {

                booksScanned = temp;
                areBooksAvailable = true;
                sbAdapter = new ScannedBooksAdapter(this, R.layout.scanned_book_item, booksScanned);
                dlvScannedResult.setAdapter(sbAdapter);
        }
        else {
            sbAdapter = new ScannedBooksAdapter(this, R.layout.scanned_book_item, booksScanned);
            dlvScannedResult.setAdapter(sbAdapter);
        }
        setUpAfterSuccesslScan();

    }

    public void onPause() {
        super.onPause();

        Log.e("Scanner", "onPause - stop");

        fScanner.stopCamera();
    }

    public void doAfterScanResult(final String isbn) {

        if( !Helper.validateIsbn13(isbn))
        {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The camera moved too fast. Try enabling flash if you're in low lighting conditions.")
                .setCancelable(true)
                .setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                })
                .setTitle("Oops, could'nt catch that!")
        .setIcon(R.mipmap.ic_barcode_);
        final AlertDialog alert = builder.create();
        alert.show();
            return;
        }

        didUserScan = true;
        cvLoading.setVisibility(View.VISIBLE);

        bExpandPanel.setVisibility(View.GONE);
        RequestQueue flipkart_queue = Volley.newRequestQueue(this);

        Log.e("Scanned Result", isbn);
        Log.e("Requesting:" , AppEngineParser.appEngineApiURL + isbn);

        JsonObjectRequest jsonRequest_flipkart = new JsonObjectRequest
                (Request.Method.GET, AppEngineParser.appEngineApiURL + isbn, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        BookItem scannedBook = AppEngineParser.getBookFromJSON(response);
                        cvLoading.setVisibility(View.GONE);
                        booksScanned.add(0, scannedBook);
                        sbAdapter.notifyDataSetChanged();
                        setUpAfterSuccesslScan();


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsonRequest_flipkart.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        flipkart_queue.add(jsonRequest_flipkart);

    }

    public void setBlinkingLaser(boolean state) {
        if (state) {

            blinkMove = new AnimationSet(true);

            Animation blink = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            Animation move = new TranslateAnimation(Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF, Animation.RELATIVE_TO_SELF, 80.0f);

            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            blink.setDuration(800);

            move.setRepeatMode(Animation.REVERSE);
            move.setRepeatCount(Animation.INFINITE);
            move.setDuration(1600);
            blinkMove.addAnimation(blink);
            blinkMove.addAnimation(move);

            vLaser.startAnimation(blinkMove);
        } else if (blinkMove != null) {
            vLaser.clearAnimation();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                View v = dlvScannedResult.getChildAt(clickedListPosition -
                        dlvScannedResult.getFirstVisiblePosition());

                if (v == null)
                    return;

                TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
                tvLocation.setText(place.getName() != null ? place.getName().toString() : place.getLatLng().toString());
            }

        }
    }

    @Override
    public void onStop(){
        super.onStop();

        Log.e("Stopping", "now");
        Gson toSave = new Gson();
        String toSaveString = toSave.toJson(booksScanned);
        Log.e("TAG","jsonBooks = " + toSaveString);

        Prefs.putString("saved_scanned_books", toSaveString);

    }

    public void setUpAfterSuccesslScan() {

        if(booksScanned.size() > 0) {

            getSupportActionBar().setTitle(sbAdapter.getItemCount() == 1 ? "Scanned Item (" + sbAdapter.getItemCount() + ")" : "Scanned Items (" + sbAdapter.getItemCount() + " )");

            if(postButton != null && addMoreButton!= null) {

                postButton.setVisible(true);
                addMoreButton.setVisible(true);
            }
        }

        else {

            getSupportActionBar().setTitle("Post an ad");

            if(postButton != null && addMoreButton!= null) {

                postButton.setVisible(false);
                addMoreButton.setVisible(false);
            }

            setBlinkingLaser(true);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_books, menu);

        postButton = menu.findItem(R.id.action_post);
        addMoreButton = menu.findItem(R.id.action_addmore);

        if(!areBooksAvailable) {
            postButton.setVisible(false);
            addMoreButton.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_addmore) {
            suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
