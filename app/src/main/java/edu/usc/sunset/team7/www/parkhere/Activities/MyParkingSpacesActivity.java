package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomListingAdapter;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomParkingSpaceSelectionAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

/**
 * Created by johnsonhui on 11/19/16.
 */

public class MyParkingSpacesActivity extends AppCompatActivity {

    @BindView(R.id.parking_spaces_listview) ListView ParkingSpaceListView;


    public static void startActivityForResult(int requestCode, Activity activity) {
        Intent intent = new Intent(activity, MyParkingSpacesActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make xml file
        setContentView(R.layout.activity_my_parking_spaces);
        ButterKnife.bind(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getCurrentUser().getUid();
        final ArrayList<ParkingSpot> userListings = new ArrayList<ParkingSpot>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Consts.PARKING_SPOT_DATABASE).child(userID);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    boolean isActive = Boolean.parseBoolean(child.child(Consts.PARKING_SPOTS_ACTIVE)
                            .getValue().toString());
                    if (isActive) {
                        ParkingSpot currentParkingSpot = parseParkingSpot(child);
                        currentParkingSpot.setProviderID(userID);
                        userListings.add(currentParkingSpot);
                    }
                }
                ParkingSpot[] parkingSpotArray = new ParkingSpot[userListings.size()];
                parkingSpotArray = userListings.toArray(parkingSpotArray);

                ParkingSpaceListView.setAdapter(new CustomParkingSpaceSelectionAdapter
                        (MyParkingSpacesActivity.this, parkingSpotArray));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //return view;
    }

    public void sendParkingSpace(ParkingSpot selectedSpace) {
        Intent data = new Intent();
        data.putExtra(Consts.PARKING_SPOT_EXTRA, selectedSpace);
        int resultCode = Consts.PARKING_SPOT_SUCCESSFUL_RESULT;

        setResult(resultCode, data);
        finish();
    }

    public ParkingSpot parseParkingSpot(DataSnapshot parkingSnapShot) {
        ParkingSpot pSpot = new ParkingSpot();
        //dont knw if we need this but insertedt the id as well
        pSpot.setParkingSpotID(parkingSnapShot.getKey());
        for(DataSnapshot child : parkingSnapShot.getChildren()) {
            switch (child.getKey()) {
                case Consts.PARKING_SPOTS_COMPACT:
                    pSpot.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_COVERED:
                    pSpot.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_HANDICAP:
                    pSpot.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LONGITUDE:
                    pSpot.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LATITUDE:
                    pSpot.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_IMAGE:
                    pSpot.setImageURL(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_NAME:
                    pSpot.setName(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_BOOKING_COUNT:
                    pSpot.setBookingCount(Integer.parseInt(child.getValue().toString()));
                    break;
            }
        }
        return pSpot;
    }

}
