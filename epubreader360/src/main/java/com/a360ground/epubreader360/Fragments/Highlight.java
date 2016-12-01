package com.a360ground.epubreader360.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a360ground.epubreader360.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Highlight extends Fragment {


    public Highlight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_highlight, container, false);
    }

}
