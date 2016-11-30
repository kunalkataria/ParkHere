package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;

/**
 * Created by kunal on 10/26/16.
 */
public class ListingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.listing_name) TextView listingNameTextView;
    @BindView(R.id.listing_details_description) TextView listingDetailsDescriptionTextView;
    @BindView(R.id.listing_details_start_time) TextView listingDetailsStartTimeTextView;
    @BindView(R.id.listing_details_end_time) TextView listingDetailsStopTimeTextView;
    @BindView(R.id.listing_details_distance_away) TextView listingDetailsDistanceTextView;
    @BindView(R.id.listing_details_price) TextView listingDetailsPriceTextView;
    @BindView(R.id.listing_details_refundable) TextView listingDetailsRefundableTextView;
    @BindView(R.id.listing_details_handicap) TextView listingDetailsHandicapTextView;
    @BindView(R.id.listing_details_covered) TextView listingDetailsCoveredTextView;
    @BindView(R.id.listing_details_compact) TextView listingDetailsCompactTextView;
    @BindView(R.id.provider_name) TextView providerNameTextView;
    @BindView(R.id.parking_image) ImageView parkingImageView;
    @BindView(R.id.book_listing_button) Button bookListingButton;
    @BindView(R.id.listing_details_toolbar) Toolbar postListingToolbar;
    @BindView(R.id.edit_listing_button) AppCompatButton editListingButton;
    @BindView(R.id.delete_listing_button) public AppCompatButton deleteListingButton;

    private ResultsPair listingResultPair;
    private Listing listingResult;
    private String providerFirstName;
    private String providerID;
    private static final String TAG = "ListingDetailsActivity";
    private boolean myOwnListing;
    private long bookStart, bookStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        ButterKnife.bind(this);

        getData();

        //TOOL BAR SET UP
        setSupportActionBar(postListingToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Temporary string, should replace with title of listing later
            getSupportActionBar().setTitle("Listing Details");
        }
        displayView();
    }

    private void getData() {
        if (!getIntent().hasExtra(Consts.LISTING_RESULT_EXTRA)) {
            listingResult = (Listing) getIntent().getSerializableExtra(Consts.LISTING_EXTRA);
        } else {
            listingResultPair = (ResultsPair) getIntent().getSerializableExtra(Consts.LISTING_RESULT_EXTRA);
            listingResult = listingResultPair.getListing();
        }

        providerID = listingResult.getProviderID();
        myOwnListing = providerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (!myOwnListing) {

            DatabaseReference providerNameRef = FirebaseDatabase.getInstance().getReference().child(Consts.USERS_DATABASE)
                    .child(providerID);
            providerNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //TODO: followup - that's because we were using an inconsistent user database
                    providerFirstName = dataSnapshot.child(Consts.USER_FIRSTNAME).getValue().toString();
                    providerNameTextView.setText("Owner: " + providerFirstName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadProviderName:onCancelled", databaseError.toException());
                }
            });
        }
    }


    private void displayView(){
        //DISPLAY BUTTONS ACCORDING TO USER
        if (myOwnListing) {
//            providerNameTextView.setVisibility(View.GONE);
            bookListingButton.setVisibility(View.GONE);
            deleteListingButton.setVisibility(View.GONE); //assume inactive initially

            //check if the listing is active
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.LISTINGS_DATABASE).child(providerID)
                    .child(Consts.ACTIVE_LISTINGS).child(listingResult.getListingID());

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) { //if it is active, allow delete
                        deleteListingButton.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        } else {
            providerNameTextView.setText(providerFirstName);
            editListingButton.setVisibility(View.GONE);
            deleteListingButton.setVisibility(View.GONE);
        }

        listingNameTextView.setText(listingResult.getName());
        Picasso.with(this).load(listingResult.getImageURL()).into(parkingImageView);
        listingDetailsDescriptionTextView.setText("Listing Description: " + listingResult.getDescription());
        listingDetailsStartTimeTextView.setText("Start Time: " + Tools.convertUnixTimeToDateString(listingResult.getStartTime()));
        listingDetailsStopTimeTextView.setText("End Time: " + Tools.convertUnixTimeToDateString(listingResult.getStopTime()));

        if (!myOwnListing) {
            listingDetailsDistanceTextView.setText("Distance Away: " + listingResultPair.getDistance() + " miles");
        }
        else {
            listingDetailsDistanceTextView.setVisibility(View.GONE);
        }
        listingDetailsPriceTextView.setText("Price: $" + listingResult.getPrice());
        listingDetailsRefundableTextView.setText("Refundable? " +listingResult.isRefundable());
        listingDetailsHandicapTextView.setText("Handicap? " + listingResult.isHandicap());
        listingDetailsCoveredTextView.setText("Covered? " + listingResult.isCovered());
        listingDetailsCompactTextView.setText("Compact? " + listingResult.isCompact());
    }


    //name, listing description, start time, end time, distance away, price, refundable handicap covered compact
    private String listingDetailsString() {
        Listing listing = listingResult;
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Name of Listing: " + listing.getName());
        descriptionBuilder.append("\nListing Description: "  + listing.getDescription());
//        descriptionBuilder.append("\nStart Time: " + Tools.convertUnixTimeToDateString(listing.getStartTime()));
//        descriptionBuilder.append("\nEnd Time: " + Tools.convertUnixTimeToDateString(listing.getStopTime()));
        descriptionBuilder.append("\nStart Time: " + Tools.convertUnixTimeToDateString(bookStart));
        descriptionBuilder.append("\nEnd Time: " + Tools.convertUnixTimeToDateString(bookStop));
        if (!myOwnListing) {
            descriptionBuilder.append("\nDistance Away: " + listingResultPair.getDistance() + " miles");
        }
        descriptionBuilder.append("\nPrice: $" + listing.getPrice());
        descriptionBuilder.append("\nRefundable? " +listing.isRefundable());
        descriptionBuilder.append("\nHandicap? " + listing.isHandicap());
        descriptionBuilder.append("\nCovered? " + listing.isCovered());
        descriptionBuilder.append("\nCompact? " + listing.isCompact());

        return descriptionBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @OnClick(R.id.book_listing_button)
    protected void bookListing() {
        final ArrayList<Integer> timeIncrements = listingResultPair.getListing().getTimesAvailable();
        final long startTime = listingResultPair.getListing().getStartTime();
        final long timeIncr = listingResultPair.getListing().getIncrement();
        Log.i("TESTING******", "TIME INCREMENT: " + timeIncr);
        List<String> timeStrings = new ArrayList<>();
        for (int i = 0; i < timeIncrements.size(); i++) {
            long startTimeLong = startTime + (timeIncrements.get(i) * timeIncr * 60 * 60);
            String timeStart = Tools.convertUnixTimeToDateString(startTimeLong);
            String timeStop = Tools.convertUnixTimeToDateString(startTimeLong + (timeIncr * 60 * 60));
            Log.i("TESTING*****", "TIMESTART: " + timeStart);
            Log.i("TESTING*****", "TIMESTOP: " + timeStop);
            timeStrings.add(timeStart + " - " + timeStop);
        }

        CharSequence times[] = timeStrings.toArray(new CharSequence[timeStrings.size()]);

//        CharSequence times[] = new CharSequence[]{"1", "2", "3"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a time");
        builder.setItems(times, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int sel) {
                int selected = timeIncrements.get(sel);
                bookStart = startTime + (selected * timeIncr * 60 * 60);
                bookStop = bookStart + (timeIncr * 60 * 60);

                Listing bookListing = listingResultPair.getListing();
                bookListing.setStartTime(bookStart);
                bookListing.setStopTime(bookStop);

                Intent intent = new Intent(ListingDetailsActivity.this, TransactionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Consts.LISTING_TO_BE_BOOKED, listingResultPair.getListing());
                bundle.putDouble(Consts.LISTING_DISTANCE, listingResultPair.getDistance());
                bundle.putString(Consts.LISTING_DETAILS_STRING, listingDetailsString());
                bundle.putInt(Consts.LISTING_BOOK_TIME, selected);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        builder.show();

    }

    @OnClick(R.id.edit_listing_button)
    protected void editListing() {
        EditListingActivity.startActivity(this, listingResult);
    }

    @OnClick(R.id.delete_listing_button)
    protected void deleteListingRequested() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Listing?")
                .setMessage("Are you sure you want to delete this listing?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteListing();
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

    @OnClick(R.id.provider_name)
    protected void displayProvider() {
        //Go to public user profile activity
        Intent intent = new Intent(ListingDetailsActivity.this, UserProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Consts.USER_ID, listingResult.getProviderID());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void deleteListing() {
        FirebaseDatabase.getInstance().getReference(Consts.LISTINGS_DATABASE).child(providerID)
                .child(Consts.ACTIVE_LISTINGS).child(listingResult.getListingID()).setValue(null);
        System.out.println(providerID+"/"+Consts.ACTIVE_LISTINGS+"/"+listingResult.getListingID());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ListingDetailsActivity.this,
                        "Listing deleted.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
}