package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.content.Context;
import android.net.Uri;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Button;

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
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.PostParkingSpotActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

public class ParkingSpotFragment extends Fragment {

    @BindView(R.id.parking_spot_listview) ListView parkingListView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @BindView(R.id.post_parking_spot_button) Button postParkingSpotButton;

    public ParkingSpotFragment() {
        // Required empty public constructor
    }

    public static ParkingSpotFragment newInstance(String param1, String param2) {
        ParkingSpotFragment fragment = new ParkingSpotFragment();
        Bundle args = new Bundle();
        return fragment;
    }

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
                    boolean isActive = Boolean.parseBoolean(parkingSpotSnapshot
                            .child(Consts.PARKING_SPOTS_ACTIVE).getValue().toString());
                    if (isActive){
                        ParkingSpot spot = parseParkingSpot(parkingSpotSnapshot);
                        userParkingSpots.add(spot);
                    }
                }
                //use adapter to display spots
                ParkingSpot[] toAdapter = userParkingSpots.toArray(new ParkingSpot[userParkingSpots.size()]);
                parkingListView.setAdapter(new CustomParkingAdapter(getActivity(), toAdapter));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @OnClick(R.id.post_parking_spot_button)
    protected void postParkingSpot() {PostParkingSpotActivity.startActivity(getActivity());}


    private ParkingSpot parseParkingSpot(DataSnapshot parkingSpotSnapshot) {
        ParkingSpot spot = new ParkingSpot();
        spot.setParkingSpotID(parkingSpotSnapshot.getKey().toString());
        for(DataSnapshot param : parkingSpotSnapshot.getChildren()) {
            String key = param.getKey();
            switch(key) {
                case Consts.PARKING_SPOTS_COMPACT:
                    spot.setCompact(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_COVERED:
                    spot.setCovered(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_HANDICAP:
                    spot.setHandicap(Boolean.parseBoolean(param.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_IMAGE:
                    spot.setImageURL(param.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_LATITUDE:
                    spot.setLatitude(Double.parseDouble(param.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LONGITUDE:
                    spot.setLongitude(Double.parseDouble(param.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_NAME:
                    spot.setName(param.getValue().toString());
                    break;
            }
        }
        return spot;
    }

}
