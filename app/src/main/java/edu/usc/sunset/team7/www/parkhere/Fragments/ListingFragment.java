package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by johnsonhui on 10/22/16.
 */

public class ListingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.listing_fragment, container, false);
        return view;
    }

}
