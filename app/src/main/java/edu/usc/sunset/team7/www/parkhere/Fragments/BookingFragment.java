package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by kunal on 10/22/16.
 */

public class BookingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private ArrayList<Booking> allBookings;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        //Get booking from database
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        allBookings = new ArrayList<Booking>();
        getAllBookings();
        Booking[] sendToAdapter = allBookings.toArray(new Booking[allBookings.size()]);
        //need to call the adapter now

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected void getAllBookings(){
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child(Consts.BOOKINGS_DATABASE).child(uid);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Booking toAdd = parseBooking(child);
                    allBookings.add(toAdd);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError);
            }
        });
    }

    private Booking parseBooking (DataSnapshot snapshot) {
        Booking toAddBooking = new Booking(null);
        for(DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case Consts.BOOKING_SEEKER_ID:
                    toAddBooking.setSeekerID(child.getValue().toString());
                    break;
                case Consts.BOOKING_SPACE_RATING:
                    toAddBooking.setSpaceRating(Integer.parseInt(child.getValue().toString()));
                    break;
                case Consts.BOOKING_SPACE_REVIEW:
                    toAddBooking.setReview(child.getValue().toString());
                    break;
                case Consts.LISTING_ID:
                    toAddBooking.setMListing(parseListing(child));
                    break;
            }
        }
        return toAddBooking;
    }

    private Listing parseListing (DataSnapshot snapshot) {
        Listing currBooking = new Listing();
        for (DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case Consts.LISTING_NAME:
                    currBooking.setName(child.getValue().toString());
                    break;
                case Consts.LISTING_DESCRIPTION:
                    currBooking.setDescription(child.getValue().toString());
                    break;
                case Consts.LISTING_REFUNDABLE:
                    currBooking.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                case Consts.LISTING_COMPACT:
                    currBooking.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.LISTING_COVERED:
                    currBooking.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.LISTING_HANDICAP:
                    currBooking.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.LISTING_PRICE:
                    currBooking.setPrice(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_LATITUDE:
                    currBooking.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_LONGITUDE:
                    currBooking.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_SEEKER:
                    currBooking.setProviderID(child.getValue().toString());
                    break;
                case Consts.LISTING_START_TIME:
                    currBooking.setStartTime(Long.valueOf(child.getValue().toString()));
                    break;
                case Consts.LISTING_END_TIME:
                    currBooking.setStopTime(Long.valueOf(child.getValue().toString()));
                    break;
            }
        }
        return currBooking;
    }
}
