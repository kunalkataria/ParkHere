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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        final ArrayList<Listing> userListings = new ArrayList<Listing>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child
                (Consts.LISTINGS_DATABASE).child(userID).child(Consts.ACTIVE_LISTINGS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Listing currentListing = parseListing(child);
                    String listingID = child.getKey();
                    currentListing.setListingID(child.getKey());
                    currentListing.setProviderID(userID);
                    System.out.println(currentListing.getListingID());
                    userListings.add(currentListing);
                }

                Listing[] arrayListings = new Listing[userListings.size()];
                arrayListings = userListings.toArray(arrayListings);
                listingListView.setAdapter(new CustomListingAdapter(getActivity(), arrayListings));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @OnClick(R.id.post_listing_button)
    protected void postListing() {
        PostListingActivity.startActivity(getActivity());
    }

    private Listing parseListing (DataSnapshot listingSnapshot) {
        final Listing listing = new Listing();
        for (DataSnapshot child : listingSnapshot.getChildren()) {
            switch (child.getKey()) {
                case "Listing Description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "Listing Name":
                    listing.setName(child.getValue().toString());
                    break;
                case "Is Refundable":
                    listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Start Time":
                    String startTime = child.getValue().toString();
                    listing.setStartTime(Long.valueOf(startTime));
                    break;
                case "End Time":
                    String stopTime = child.getValue().toString();
                    listing.setStopTime(Long.valueOf(stopTime));
                    break;
                case "Price":
                    Double price = Double.parseDouble(child.getValue().toString());
                    listing.setPrice(price);
                    break;
                case "ParkingID":
                    String parkingID = child.getValue().toString();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final String UID = mAuth.getCurrentUser().getUid();
                    DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference().child
                            (Consts.PARKING_SPOT_DATABASE).child(UID).child(parkingID);

                    dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child2 : dataSnapshot.getChildren()) {
                                switch (child2.getKey()) {
                                    case "Compact":
                                        listing.getParkingSpot().setCompact(Boolean.parseBoolean
                                                (child2.getValue().toString()));
                                        break;
                                    case "Covered":
                                        listing.getParkingSpot().setCovered(Boolean.parseBoolean
                                                (child2.getValue().toString()));
                                        break;
                                    case "Handicap":
                                        listing.getParkingSpot().setHandicap(Boolean.parseBoolean
                                                (child2.getValue().toString()));
                                        break;
                                    case "Image URL":
                                        listing.getParkingSpot().setImageURL(child2.getValue()
                                                .toString());
                                        break;
                                    case "Latitude":
                                        listing.getParkingSpot().setLatitude(Double.parseDouble
                                                (child2.getValue().toString()));
                                        break;
                                    case "Longitude":
                                        listing.getParkingSpot().setLongitude(Double.parseDouble
                                                (child2.getValue().toString()));
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

            }
        }
        return listing;
    }

}
