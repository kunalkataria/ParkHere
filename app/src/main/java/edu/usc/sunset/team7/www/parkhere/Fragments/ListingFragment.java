package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
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
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/22/16.
 */

public class ListingFragment extends Fragment {

    @BindView(R.id.post_listing_button)
    Button postListingButton;

    @Override
    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listing_fragment, container, false);
        ButterKnife.bind(this, view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        final ArrayList<Listing> userListings = new ArrayList<Listing>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Listings/"+userID);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.hasChildren()) {
                        for(DataSnapshot childSnap : child.getChildren()) {
                            if (child.getKey().equals("ownerID")) {
                                String ownerID = child.getValue().toString();
                                if (ownerID.equals(userID)) {
                                    userListings.add(parseListing(child));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @OnClick(R.id.post_listing_button)
    protected void postListing() {
        PostListingActivity.startActivity(getActivity());
    }

    private Listing parseListing (DataSnapshot listingSnapshot) {
        Listing listing = new Listing();
        for (DataSnapshot child : listingSnapshot.getChildren()) {
            switch (child.getKey()) {
                case "Compact":
                    listing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Covered":
                    listing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Listing Description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "Handicap":
                    listing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "Image URL":
                    listing.setImageURL(child.getValue().toString());
                    break;
                case "Latitude":
                    listing.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "Longitude":
                    listing.setLongitude(Double.parseDouble(child.getValue().toString()));
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
            }
        }
        return listing;
    }

}
