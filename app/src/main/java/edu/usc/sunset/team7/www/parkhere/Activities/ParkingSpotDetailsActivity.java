package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

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

    private ParkingSpot parkingSpot;
    private String providerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "ParkingSpotDetailsActivity";

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


    private void displayView(){
        parkingSpotNameTextView.setText(parkingSpot.getName());
        Picasso.with(this).load(parkingSpot.getImageURL()).into(parkingImageView);
        parkingSpotDetailsLocationTextView.setText("Latitude: " + parkingSpot.getLatitude() + " " + "Longitude: " + parkingSpot.getLongitude());
        parkingSpotDetailsHandicapTextView.setText("Handicap? " + parkingSpot.isHandicap());
        parkingSpotDetailsCoveredTextView.setText("Covered? " + parkingSpot.isCovered());
        parkingSpotDetailsCompactTextView.setText("Compact? " + parkingSpot.isCompact());
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
        FirebaseDatabase.getInstance().getReference(Consts.PARKING_SPOT_DATABASE).child(providerID)
                .child(parkingSpot.getParkingSpotID()).setValue(null);
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
}
