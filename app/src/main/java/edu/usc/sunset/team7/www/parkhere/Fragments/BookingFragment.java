package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;

/**
 * Created by kunal on 10/22/16.
 */

public class BookingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private Booking[] allBookings;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        //Get booking from database
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        allBookings = getAllBookings();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected Booking[] getAllBookings(){

        return null;
    }
}
