package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomBookingAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

import static edu.usc.sunset.team7.www.parkhere.R.string.listing;

/**
 * Created by kunal on 10/22/16.
 */

public class BookingFragment extends Fragment {

    @BindView(R.id.booking_listview) ListView bookingListview;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        allBookings = new ArrayList<Booking>();
        getAllBookings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected void getAllBookings() {
        final String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grabBookings(dataSnapshot, uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void grabBookings(DataSnapshot datasnapshot, String uid) {
        DataSnapshot bookings = datasnapshot.child(Consts.BOOKINGS_DATABASE).child(uid);
        DataSnapshot listings = datasnapshot.child(Consts.LISTINGS_DATABASE);
        for(DataSnapshot booking : bookings.getChildren()) {
            String listingID = booking.child(Consts.LISTING_ID).getValue().toString();
            String providerID = booking.child(Consts.PROVIDER_ID).getValue().toString();
            Booking toAdd = parseBooking(datasnapshot, booking,listingID, providerID);
            allBookings.add(toAdd);
        }
        Booking[] sendToAdapter = allBookings.toArray(new Booking[allBookings.size()]);
        bookingListview.setAdapter(new CustomBookingAdapter(getActivity(), sendToAdapter));
    }

    private Booking parseBooking (DataSnapshot datasnapshot, DataSnapshot booking,
                                  String listingID, String providerID) {
        Booking toAddBooking = new Booking(null);
        toAddBooking.setBookingID(booking.getKey());
        for(DataSnapshot child : booking.getChildren()) {
            switch (child.getKey()) {
                case Consts.LISTING_END_TIME:
                    toAddBooking.setBookEndTime(Long.parseLong(child.getValue().toString()));
                    break;
                case Consts.LISTING_START_TIME:
                    toAddBooking.setBookStartTime(Long.parseLong(child.getValue().toString()));
                    break;
                case Consts.LISTING_BOOK_TIME:
                    toAddBooking.setTimeIncrement(Integer.parseInt(child.getValue().toString()));
                    break;
            }
        }
        Listing listing = parseListing(datasnapshot, listingID, booking.getKey(), providerID);
        parseParkingSpot(datasnapshot, listing);
        toAddBooking.setMListing(listing);

        return toAddBooking;
    }

    private Listing parseListing (DataSnapshot dataSnapshot, String listingID, String bookingID, String providerID) {
        DataSnapshot listing = dataSnapshot.child(Consts.LISTINGS_DATABASE).child(providerID).child(Consts.INACTIVE_LISTINGS).child(bookingID);
        Listing curListing = new Listing();
        curListing.setListingID(listingID);
        curListing.setProviderID(providerID);

        for (DataSnapshot child : listing.getChildren()) {
            switch (child.getKey()) {
                case Consts.LISTING_NAME:
                    curListing.setName(child.getValue().toString());
                    break;
                case Consts.LISTING_DESCRIPTION:
                    curListing.setDescription(child.getValue().toString());
                    break;
                case Consts.LISTING_REFUNDABLE:
                    curListing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.LISTING_PRICE:
                    curListing.setPrice(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_LATITUDE:
                    curListing.getParkingSpot().setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_LONGITUDE:
                    curListing.getParkingSpot().setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.LISTING_START_TIME:
                    curListing.setStartTime(Long.valueOf(child.getValue().toString()));
                    break;
                case Consts.LISTING_END_TIME:
                    curListing.setStopTime(Long.valueOf(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_ID:
                    curListing.setParkingID(child.getValue().toString());
                    break;
            }
        }

        return curListing;
    }

    private void parseParkingSpot(DataSnapshot dataSnapshot, Listing listing) {
        DataSnapshot parkingSnapshot = dataSnapshot
                .child(Consts.PARKING_SPOT_DATABASE)
                .child(listing.getProviderID())
                .child(listing.getParkingID());

        for (DataSnapshot child : parkingSnapshot.getChildren()) {
            switch (child.getKey()) {
                case Consts.PARKING_SPOTS_COMPACT:
                    listing.getParkingSpot().setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_COVERED:
                    listing.getParkingSpot().setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_HANDICAP:
                    listing.getParkingSpot().setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_IMAGE:
                    listing.getParkingSpot().setImageURL(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_NAME:
                    listing.getParkingSpot().setName(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_LATITUDE:
                    listing.getParkingSpot().setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LONGITUDE:
                    listing.getParkingSpot().setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_BOOKING_COUNT:
                    listing.getParkingSpot().setBookingCount(Integer.parseInt(child.getValue().toString()));
                    break;
            }
        }
    }
}
