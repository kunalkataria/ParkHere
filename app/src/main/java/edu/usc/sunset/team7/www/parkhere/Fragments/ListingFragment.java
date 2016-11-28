package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomListingAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/22/16.
 */

public class ListingFragment extends Fragment {

    @BindView(R.id.post_listing_button) Button postListingButton;
    @BindView(R.id.listing_listview) ListView listingListView;

    @Override
    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final ArrayList<Listing> userListings = new ArrayList<Listing>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        .child(Consts.LISTINGS_DATABASE).child(userID).child(Consts.ACTIVE_LISTINGS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Listing> allListings = parseListing2(dataSnapshot);
                Listing[] arrayListings = new Listing[allListings.size()];
                arrayListings = allListings.toArray(arrayListings);
                listingListView.setAdapter(new CustomListingAdapter(getActivity(), arrayListings));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private ArrayList<Listing> parseListing2(DataSnapshot dataSnapshot) {
        ArrayList<Listing> allListings = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        DataSnapshot listings = dataSnapshot.child(Consts.LISTINGS_DATABASE).child(userID).child(Consts.ACTIVE_LISTINGS);

        for (DataSnapshot listingSnapshot : listings.getChildren()) {
            Listing listing = new Listing();
            listing.setProviderID(userID);
            listing.setListingID(listingSnapshot.getKey());
            listing.setDescription(listingSnapshot.child(Consts.LISTING_DESCRIPTION).getValue().toString());
            listing.setName(listingSnapshot.child(Consts.LISTING_NAME).getValue().toString());
            listing.setRefundable(Boolean.parseBoolean(listingSnapshot.child(Consts.LISTING_REFUNDABLE).getValue().toString()));
            listing.setStartTime(Long.valueOf(listingSnapshot.child(Consts.LISTING_START_TIME).getValue().toString()));
            listing.setStopTime(Long.valueOf(listingSnapshot.child(Consts.LISTING_END_TIME).getValue().toString()));
            listing.setPrice(Double.parseDouble(listingSnapshot.child(Consts.LISTING_PRICE).getValue().toString()));
            listing.setParkingID(listingSnapshot.child(Consts.PARKING_SPOTS_ID).getValue().toString());
            listing.setIncrement(Long.parseLong(listingSnapshot.child(Consts.LISTING_BOOK_TIME).getValue().toString()));
            listing.setTimesAvailable(listingSnapshot.child(Consts.LISTING_ACTIVE_TIMES).getValue().toString());
            Listing toAdd = parseParkingSpot(dataSnapshot, listing, userID);
            allListings.add(toAdd);
        }

        return allListings;
    }

    private Listing parseParkingSpot(DataSnapshot datasnapshot, Listing listing, String userID) {
        DataSnapshot parkingSnapShot = datasnapshot.child(Consts.PARKING_SPOT_DATABASE).child(userID).child(listing.getParkingSpot().getParkingSpotID());
        for(DataSnapshot child : parkingSnapShot.getChildren()) {
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
        return listing;
    }

    @OnClick(R.id.post_listing_button)
    protected void postListing() {
        PostListingActivity.startActivity(getActivity());
    }

}
