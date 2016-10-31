package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Acer on 10/30/2016.
 */

public class BookingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.booking_name) TextView bookingName;
    @BindView(R.id.provider_name) TextView providerName;
    @BindView(R.id.parking_image) ImageView parkingImage;
    @BindView(R.id.booking_details) TextView bookingDetails;
    @BindView(R.id.review_booking_button) AppCompatButton reviewBookingButton;

    private static final String TAG = "BookingDetailsActivity";
    private Booking booking;
    private Listing listing;
    private String providerFirstName;
    private String seekerFirstName;
    private String providerPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = (Booking) getIntent().getSerializableExtra(Consts.BOOKING_EXTRA);
        if(booking != null) {
            listing = booking.getMListing();
            initializeReviewButton();
            getFirebaseData();
            displayBooking();
        }
    }

    public static void startActivity(Context context, Booking booking) {
        Intent intent = new Intent(context, BookingDetailsActivity.class);
        intent.putExtra(Consts.BOOKING_EXTRA, booking);
        context.startActivity(intent);
    }

    private void initializeReviewButton() {
        if(booking.getReview() == null) {
            reviewBookingButton.setEnabled(true);
        } else {
            reviewBookingButton.setEnabled(false);
        }
    }

    private void getFirebaseData() {
        providerFirstName = null;
        providerPhoneNumber = null;

        //getting first name of provider
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE)
                .child(listing.getProviderID()).child(Consts.USER_FIRSTNAME);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                providerFirstName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadProviderName:onCancelled", databaseError.toException());
                providerFirstName = "Error getting name";
            }
        });

        //getting phone number of provider
        dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE)
                .child(listing.getProviderID()).child(Consts.USER_PHONENUMBER);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                providerPhoneNumber = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadProviderName:onCancelled", databaseError.toException());
                providerPhoneNumber = "Error getting phone number";
            }
        });

        while(providerFirstName == null || providerPhoneNumber == null) {
            try { Thread.sleep(5); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void displayBooking() {
        bookingName.setText(listing.getName());
        Picasso.with(this).load(listing.getImageURL()).into(parkingImage);
        bookingDetails.setText(bookingDetailsString());
    }

    private String bookingDetailsString() {
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("Name of Listing: " + listing.getName());
        descriptionBuilder.append("\nListing Description: "  + listing.getDescription());
        descriptionBuilder.append("\nStart Time: " + Tools.convertUnixTimeToDateString(listing.getStartTime()));
        descriptionBuilder.append("\nEnd Time: " + Tools.convertUnixTimeToDateString(listing.getStopTime()));
        descriptionBuilder.append("\nListing provider: " + providerFirstName);
        descriptionBuilder.append("\nProvider phone number: " + providerPhoneNumber);

        descriptionBuilder.append("\n\nParking Information");
        descriptionBuilder.append("\nPrice: " + listing.getPrice());
        descriptionBuilder.append("\nRefundable? " +listing.isRefundable());
        descriptionBuilder.append("\nHandicap? " + listing.isHandicap());
        descriptionBuilder.append("\nCovered? " + listing.isCovered());
        descriptionBuilder.append("\nCompact? " + listing.isCompact());

        return descriptionBuilder.toString();
    }

    @OnClick(R.id.provider_name)
    protected void displayProvider(){
        //Go to public user profile activity
        Intent intent = new Intent(BookingDetailsActivity.this, UserProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Consts.USER_ID, listing.getProviderID());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
