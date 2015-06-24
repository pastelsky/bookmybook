package com.example.shubhamkanodia.bookmybook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

/**
 * Created by Chirag Shenoy on 25-Jun-15.
 */
public class MyRequestsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myrequets, container, false);
        TextView text = (TextView) view.findViewById(R.id.bobo1);
        text.setText("My Requests");

        return view;
    }
}
