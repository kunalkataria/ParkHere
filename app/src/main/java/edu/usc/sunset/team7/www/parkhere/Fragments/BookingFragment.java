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

    private ArrayList<Listing> allBookings;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        //Get booking from database
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        allBookings = new ArrayList<Listing>();
        getAllBookings();
        Listing[] sendToAdapter = allBookings.toArray(new Listing[allBookings.size()]);
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
                    Listing toAdd = parseListing(child);
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

    private Listing parseListing (DataSnapshot snapshot) {
        Listing listing = new Listing();
        for (DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case "name":
                    listing.setName(child.getValue().toString());
                    break;
                case "description":
                    listing.setDescription(child.getValue().toString());
                    break;
                case "refundable":
                    listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                case "compact":
                    listing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "covered":
                    listing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "handicap":
                    listing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case "price":
                    listing.setPrice(Double.parseDouble(child.getValue().toString()));
                    break;
                case "latitude":
                    listing.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "longitude":
                    listing.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case "providerID":
                    listing.setProviderID(child.getValue().toString());
                    break;
                case "startTime":
                    listing.setStartTime(Long.valueOf(child.getValue().toString()));
                    break;
                case "stopTime":
                    listing.setStopTime(Long.valueOf(child.getValue().toString()));
                    break;
                case "seekerID":
                    listing.setSeekerID(child.getValue().toString());
                    break;
                case "rating":
                    listing.setSpaceRating(Integer.parseInt(child.getValue().toString()));
                    break;
                case "review":
                    listing.setReview(child.getValue().toString());
                    break;
            }
        }
        return listing;
    }
}
