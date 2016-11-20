package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Activities.MyParkingSpacesActivity;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomParkingSpaceSelectionAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by kunal on 11/20/16.
 */

public class MyReviewFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_review_fragment, container, false);
        ButterKnife.bind(this, view);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        final ArrayList<Review> reviewsListing = new ArrayList<Review>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Consts.REVIEWS_DATABASE).child(userID);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //youre at each parking spot id so need to iterate through all booking ids, possible threading problems here later
                    parseParkingSpot(child, reviewsListing);
                }
                Review[] reviewParkingSpotArray = new Review[reviewsListing.size()];
                reviewParkingSpotArray = reviewsListing.toArray(reviewParkingSpotArray);

                //set adapter here
                /*ParkingSpaceListView.setAdapter(new CustomParkingSpaceSelectionAdapter
                        (MyParkingSpacesActivity.this, parkingSpotArray));*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void parseParkingSpot(DataSnapshot parkingIDShot, ArrayList<Review> reviewsListing) {
        //possible threading problems
        for(DataSnapshot child : parkingIDShot.getChildren()) {
            for(DataSnapshot reviewInfo : child.getChildren()) {
                Review toAdd = new Review();
                switch (reviewInfo.getKey()) {
                    case Consts.REVIEW_DESCRIPTION:
                        toAdd.setReview(reviewInfo.getValue().toString());
                        break;
                    case Consts.REVIEW_RATING:
                        toAdd.setReviewRating(Integer.parseInt(reviewInfo.getValue().toString()));
                        break;
                }
                reviewsListing.add(toAdd);
            }
        }
    }
}
