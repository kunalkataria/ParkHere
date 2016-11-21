package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.content.Context;
import android.net.Uri;
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
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomParkingAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

public class ParkingSpotFragment extends Fragment {

    @BindView(R.id.parking_spot_listview) ListView parkingListView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        getParkingSpots();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parking_spot, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    private void getParkingSpots() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(Consts.PARKING_SPOTS_DATABASE)
                .child(currentUser.getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<ParkingSpot> userParkingSpots = new ArrayList<ParkingSpot>();
                for(DataSnapshot parkingSpotSnapshot : dataSnapshot.getChildren()) {
                    ParkingSpot spot = parseParkingSpot(parkingSpotSnapshot);
                    userParkingSpots.add(spot);
                }
                //use adapter to display spots
                ParkingSpot[] toAdapter = userParkingSpots.toArray(new ParkingSpot[userParkingSpots.size()]);
                parkingListView.setAdapter(new CustomParkingAdapter(getActivity(), toAdapter));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private ParkingSpot parseParkingSpot(DataSnapshot parkingSpotSnapshot) {
        ParkingSpot spot = new ParkingSpot();
        spot.setParkingSpotID(parkingSpotSnapshot.getKey().toString());
        for(DataSnapshot param : parkingSpotSnapshot.getChildren()) {
            String key = param.getKey();
            switch(key) {
                case Consts.COMPACT:
                    spot.setCompact(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.COVERED:
                    spot.setCovered(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.HANDICAP:
                    spot.setHandicap(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.IMAGE_URI:
                    spot.setImageURL(param.getValue().toString());
                    break;
                case Consts.LISTING_LATITUDE:
                    spot.setLatitude(Double.parseDouble(param.getValue().toString()));
                    break;
                case Consts.LISTING_LONGITUDE:
                    spot.setLongitude(Double.parseDouble(param.getValue().toString()));
                    break;
                case Consts.LISTING_NAME:
                    spot.setName(param.getValue().toString());
                    break;
            }
        }
        return spot;
    }

}
