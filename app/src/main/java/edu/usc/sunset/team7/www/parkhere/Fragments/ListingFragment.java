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

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("listings");
        dbRef.orderByChild("ownerID").addListenerForSingleValueEvent(new ValueEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @OnClick(R.id.post_listing_button)
    protected void postListing() {
        PostListingActivity.startActivity(getActivity());
    }

    private Listing parseListing (DataSnapshot snapshot) {
        Listing listing = new Listing();
        for (DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case "active":
                    if (!Boolean.parseBoolean(child.getValue().toString())) continue;
                    break;
                case "compact":
                    listing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "covered":
                    listing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "handicap":
                    listing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "latitude":
                    listing.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "longitude":
                    listing.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "name":
                    listing.setName(child.getValue().toString());
                    break;
                case "ownerID":
                    break;
                case "refundable":
                    listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "startTime":
                    listing.setStartTime(Long.getLong(child.getValue().toString()));
                    break;
                case "stopTime":
                    listing.setStopTime(Long.getLong(child.getValue().toString()));
                    break;
            }
        }
        return listing;
    }

}
