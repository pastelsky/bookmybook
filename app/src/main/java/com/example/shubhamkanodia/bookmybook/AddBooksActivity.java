// Activity to add a new ad

package com.example.shubhamkanodia.bookmybook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shubhamkanodia.bookmybook.Adapters.BookItem;
import com.example.shubhamkanodia.bookmybook.Adapters.BooksAutocompleteAdapter;
import com.example.shubhamkanodia.bookmybook.Parsers.GoogleBooksParser;
import com.parse.ParseObject;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class AddBooksActivity extends AppCompatActivity {


    static AutoCompleteTextView etBookName;

    static AutoCompleteTextView etBookAuthor;

    ImageView ivBookCover;
    Button bPostAd;


    String presentURL = "";

    final String bookCoverURL = "http://covers.librarything.com/devkey/57c8874fc25c78dfeaa2f8eba4455276/large/isbn/";

    ArrayList<String> bookSuggestions = new ArrayList<String>();
    ArrayAdapter bookSuggestionsAdapter;

    static SlidingUpPanelLayout suPanelLayout;

    Button bExpandPanel;
    private static ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);

        mScannerView = (ZBarScannerView) findViewById(R.id.scanner_fragment);
        mScannerView.startCamera();

        suPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        etBookAuthor = (AutoCompleteTextView) findViewById(R.id.etBookAuthor);
        etBookName = (AutoCompleteTextView) findViewById(R.id.etBookName);
        bPostAd = (Button) findViewById(R.id.bPostAd);
        bExpandPanel = (Button) findViewById(R.id.bExpandPanel);
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


        bExpandPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                mScannerView.startCamera();
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


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (suPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
//            suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//        }
//
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


    public static class ScannerFragmentTest extends Fragment implements MessageDialogFragment.MessageDialogListener,
            ZBarScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
            CameraSelectorDialogFragment.CameraSelectorDialogListener {
        private static final String FLASH_STATE = "FLASH_STATE";
        private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
        private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
        private static final String CAMERA_ID = "CAMERA_ID";
        private ZBarScannerView mScannerView;
        private boolean mFlash;
        private boolean mAutoFocus;
        private ArrayList<Integer> mSelectedIndices;
        private int mCameraId = -1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
            mScannerView = new ZBarScannerView(getActivity());
            if (state != null) {
                mFlash = state.getBoolean(FLASH_STATE, false);
                mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
                mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
                mCameraId = state.getInt(CAMERA_ID, -1);
            } else {
                mFlash = false;
                mAutoFocus = true;
                mSelectedIndices = null;
                mCameraId = -1;
            }
            setupFormats();
            return mScannerView;
        }

        @Override
        public void onCreate(Bundle state) {
            super.onCreate(state);
            setHasOptionsMenu(true);
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            MenuItem menuItem;

            if (mFlash) {
                menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
            } else {
                menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
            }
            MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);


            if (mAutoFocus) {
                menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
            } else {
                menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
            }
            MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

            menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
            MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

            menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
            MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch (item.getItemId()) {
                case R.id.menu_flash:
                    mFlash = !mFlash;
                    if (mFlash) {
                        item.setTitle(R.string.flash_on);
                    } else {
                        item.setTitle(R.string.flash_off);
                    }
                    mScannerView.setFlash(mFlash);
                    return true;
                case R.id.menu_auto_focus:
                    mAutoFocus = !mAutoFocus;
                    if (mAutoFocus) {
                        item.setTitle(R.string.auto_focus_on);
                    } else {
                        item.setTitle(R.string.auto_focus_off);
                    }
                    mScannerView.setAutoFocus(mAutoFocus);
                    return true;
                case R.id.menu_formats:
                    DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                    fragment.show(getActivity().getSupportFragmentManager(), "format_selector");
                    return true;
                case R.id.menu_camera_selector:
                    mScannerView.stopCamera();
                    DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                    cFragment.show(getActivity().getSupportFragmentManager(), "camera_selector");
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            mScannerView.setResultHandler(this);
            mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(FLASH_STATE, mFlash);
            outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
            outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
            outState.putInt(CAMERA_ID, mCameraId);
        }

        @Override
        public void handleResult(Result rawResult) {
            try {

//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {}
//        showMessageDialog("Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName());
                Toast.makeText(getActivity(), "sadnese", Toast.LENGTH_SHORT).show();
                suPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                mScannerView.stopCamera();
                Log.e("sending to asyn", rawResult.getContents());
                new GetTitleAsyncTask().execute(rawResult.getContents());
//                etBookName.setText(GoogleBooksParser.getTitleFromISBN(rawResult.getContents()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void showMessageDialog(String message) {
            DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
            fragment.show(getActivity().getSupportFragmentManager(), "scan_results");
        }

        public void closeMessageDialog() {
            closeDialog("scan_results");
        }

        public void closeFormatsDialog() {
            closeDialog("format_selector");
        }

        public void closeDialog(String dialogName) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
            if (fragment != null) {
                fragment.dismiss();
            }
        }

        @Override
        public void onDialogPositiveClick(DialogFragment dialog) {
            // Resume the camera
            mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);
        }

        @Override
        public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
            mSelectedIndices = selectedIndices;
            setupFormats();
        }

        @Override
        public void onCameraSelected(int cameraId) {
            mCameraId = cameraId;
            mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);
        }

        public void setupFormats() {
            List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
            if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
                mSelectedIndices = new ArrayList<Integer>();
                for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
                    mSelectedIndices.add(i);
                }
            }

            for (int index : mSelectedIndices) {
                formats.add(BarcodeFormat.ALL_FORMATS.get(index));
            }
            if (mScannerView != null) {
                mScannerView.setFormats(formats);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            mScannerView.stopCamera();
            closeMessageDialog();
            closeFormatsDialog();
        }
    }


    // Asynctask
    private static class GetTitleAsyncTask extends AsyncTask<String, Void, String> {

        String bookTitle;
        String bookAuthor;

        @Override
        protected String doInBackground(String... params) {
            Log.e("in async", params[0]);
            bookTitle = GoogleBooksParser.getTitleFromISBN(params[0]);
            bookAuthor = GoogleBooksParser.getAuthorFromISBN(params[0]);
            return bookTitle;

        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            etBookName.setText(bookTitle);
            etBookAuthor.setText(bookAuthor);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
