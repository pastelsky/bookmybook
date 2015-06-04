package com.example.shubhamkanodia.bookmybook;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Log;


public class SignUp extends Activity
        implements FragmentManager.OnBackStackChangedListener {
    /**
     * A handler object, used for deferring UI operations.
     */
    static public Handler mHandler = new Handler();

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    static boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);


        Button temp = (Button) findViewById(R.id.temp);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });

        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new CardFrontFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);

        Button b = (Button) findViewById(R.id.accept);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });
    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.container, new CardBackFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();

        getFragmentManager().executePendingTransactions();

        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {
        public CardFrontFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.front_page_fragment, container, false);
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {

        private ArrayList<Item> list = null;
        View view;
        RadioGroup rg;
        String selection;

        public CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int[] ids = {R.id.radio_button_1, R.id.radio_button_2, R.id.radio_button_3, R.id.radio_button_4, R.id.radio_button_5, R.id.radio_button_6, R.id.radio_button_7, R.id.radio_button_8, R.id.radio_button_9};

            list = getData();
            view = inflater.inflate(R.layout.back_page_fragment,
                    container, false);

            Button next = (Button) view.findViewById(R.id.next);

            rg = (RadioGroup) view.findViewById(R.id.radiogroup);

            for (int i = 0; i < list.size(); i++) {
                RadioButton newRadioButton = new RadioButton(getActivity());
                newRadioButton.setText(list.get(i).getValue());
                newRadioButton.setId(ids[i]);
                LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                rg.addView(newRadioButton, 0, layoutParams);
            }


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (rg.getCheckedRadioButtonId() != -1) {
                        int id = rg.getCheckedRadioButtonId();
                        View radioButton = rg.findViewById(id);
                        int radioId = rg.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) rg.getChildAt(radioId);
                        selection = (String) btn.getText();
                    }
                    Toast.makeText(getActivity(), selection, Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }


        private ArrayList<Item> getData() {
            ArrayList<Item> accountsList = new ArrayList<Item>();

            //Getting all registered Google Accounts;
            try {
                Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");
                for (Account account : accounts) {
                    Item item = new Item(account.type, account.name);
                    accountsList.add(item);
                }
            } catch (Exception e) {
                Log.i("Exception", "Exception:" + e);
            }

            //For all registered accounts;
        /*try {
            Account[] accounts = AccountManager.get(this).getAccounts();
			for (Account account : accounts) {
				Item item = new Item( account.type, account.name);
				accountsList.add(item);
			}
		} catch (Exception e) {
			Log.i("Exception", "Exception:" + e);
		}*/
            return accountsList;
        }

    }


}
