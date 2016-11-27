package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomReviewAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

public class ParkingSpotDetailsActivity extends AppCompatActivity {

    @BindView(R.id.parking_spot_name) TextView parkingSpotNameTextView;
    @BindView(R.id.parking_spot_details_location) TextView parkingSpotDetailsLocationTextView;
    @BindView(R.id.parking_spot_details_handicap) TextView parkingSpotDetailsHandicapTextView;
    @BindView(R.id.parking_spot_details_covered) TextView parkingSpotDetailsCoveredTextView;
    @BindView(R.id.parking_spot_details_compact) TextView parkingSpotDetailsCompactTextView;
    @BindView(R.id.parking_image) ImageView parkingImageView;
    @BindView(R.id.parking_spot_details_toolbar) Toolbar parkingSpotDetailsToolbar;
    @BindView(R.id.edit_parking_spot_button) AppCompatButton editParkingSpotButton;
    @BindView(R.id.delete_parking_spot_button) public AppCompatButton deleteParkingSpotButton;
    @BindView(R.id.review_content_space) LinearLayout reviewContentSpace;

    private ParkingSpot parkingSpot;
    private String providerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "ParkingSpotDetails";
    private ArrayList<Review> reviews = new ArrayList<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot_details);
        ButterKnife.bind(this);

        getData();

        //TOOL BAR SET UP
        setSupportActionBar(parkingSpotDetailsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Temporary string, should replace with title of listing later
            getSupportActionBar().setTitle("Parking Spot Details");
        }

        displayView();
    }

    private void getData() {
        parkingSpot = (ParkingSpot) getIntent().getSerializableExtra(Consts.PARKING_SPOT_EXTRA);
    }

    private String getAddressString() throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(parkingSpot.getLatitude(), parkingSpot.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        StringBuilder sb = new StringBuilder();
        sb.append(addresses.get(0).getAddressLine(0)).append(", "); //address
        sb.append(addresses.get(0).getLocality()).append(", "); //city
        sb.append(addresses.get(0).getAdminArea()); //state

        return sb.toString();
    }


    private void displayView(){
        parkingSpotNameTextView.setText(parkingSpot.getName());
        Picasso.with(this).load(parkingSpot.getImageURL()).into(parkingImageView);
        try {
            parkingSpotDetailsLocationTextView.setText(getAddressString());
        } catch (IOException e) {
            parkingSpotDetailsLocationTextView.setText("Latitude: " + parkingSpot.getLatitude()
            + " Longitude: " + parkingSpot.getLongitude());
        }
        parkingSpotDetailsHandicapTextView.setText("Handicap? " + parkingSpot.isHandicap());
        parkingSpotDetailsCoveredTextView.setText("Covered? " + parkingSpot.isCovered());
        parkingSpotDetailsCompactTextView.setText("Compact? " + parkingSpot.isCompact());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        final String userID = mAuth.getCurrentUser().getUid();

        //Get Reviews
        //ReviewsDB -> UID -> ParkingSpotID -> Booking ID -> Review Data
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE).child(userID);
        if(reviewsRef!=null){ //User has reviews
            if(reviewsRef.child(parkingSpot.getParkingSpotID()) != null ) { //The Parking Spot has reviews
                reviewsRef.child(parkingSpot.getParkingSpotID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot bookingID : dataSnapshot.getChildren()){
                            int ratings = -1;
                            String description = null;
                            //Get the review data
                            for(DataSnapshot review : bookingID.getChildren()){
                                switch (review.getKey()) {
                                    case Consts.REVIEW_DESCRIPTION:
                                        description = review.getValue().toString();
                                        Log.d(TAG, description);
                                        break;
                                    case Consts.REVIEW_RATING:
                                        ratings = Integer.parseInt(review.getValue().toString());
                                        break;
                                }
                            }
                            if(ratings !=-1 && description!=null){
                                Review r = new Review(ratings, description);
                                reviews.add(r);
                            } else {
                                Log.d(TAG, "RATING WAS NOT ADDED");
                            }
                        }

                        displayReviews();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }
        }
    }

    private void displayReviews(){
        if (reviews != null || reviews.size() > 0) {
            reviewContentSpace.removeAllViewsInLayout();
            ListView listView = new ListView(this);
            listView.setAdapter(new CustomReviewAdapter(this, reviews));
            reviewContentSpace.addView(listView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @OnClick(R.id.edit_parking_spot_button)
    protected void editParkingSpot() {
        EditParkingSpotActivity.startActivity(this, parkingSpot);
    }

    @OnClick(R.id.delete_parking_spot_button)
    protected void deleteParkingSpotRequested() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Parking Spot?")
                .setMessage("Are you sure you want to delete this parking spot?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteParkingSpot();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    public void deleteParkingSpot() {
        /*FirebaseDatabase.getInstance().getReference(Consts.PARKING_SPOT_DATABASE).child(providerID)
                .child(parkingSpot.getParkingSpotID()).setValue(null);*/
        FirebaseDatabase.getInstance().getReference(Consts.PARKING_SPOT_DATABASE).child(providerID)
                .child(parkingSpot.getParkingSpotID()).child(Consts.PARKING_SPOTS_ACTIVE).setValue(false);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ParkingSpotDetailsActivity.this,
                        "Parking spot deleted.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    private Review parseBookingID(DataSnapshot bookingSnapShot) {
        //possible threading problems
        Review toAdd = new Review();
        for(DataSnapshot child : bookingSnapShot.getChildren()) {
            switch(child.getKey()) {
                case Consts.REVIEW_DESCRIPTION:
                    toAdd.setReview(child.getValue().toString());
                    break;
                case Consts.REVIEW_RATING:
                    toAdd.setReviewRating(Integer.parseInt(child.getValue().toString()));
                    break;
            }
        }
        return toAdd;
    }
}
