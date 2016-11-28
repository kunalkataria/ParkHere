package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;

/**
 * Created by Acer on 10/30/2016.
 */

public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.leave_review_rating_bar) RatingBar ratingBar;
    @BindView(R.id.review_description_text) AppCompatEditText reviewEditText;
    @BindView(R.id.review_text) TextInputLayout reviewTextInput;
    @BindView(R.id.submit_review_button) AppCompatButton submitReviewButton;
    @BindView(R.id.review_toolbar) Toolbar reviewToolbar;
    private static final String TAG = "ReviewActivity";
    private Booking booking;

    public static void startActivity(Context context, Booking booking) {
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra(Consts.BOOKING_EXTRA, booking);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = (Booking) getIntent().getSerializableExtra(Consts.BOOKING_EXTRA);

        if(booking == null){
            Log.d (TAG, "*****************BOOKING IS NULL!");
        } else {
            Log.d (TAG, "*****************BOOKING IS NOT NULL!");
        }
        setContentView(R.layout.activity_make_review);
        ButterKnife.bind(this);

        setSupportActionBar(reviewToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Review");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setValues();
    }

    private void setValues() {
        ratingBar.setIsIndicator(false);
        ratingBar.setMax(5);
        ratingBar.setStepSize(1.0f);
    }

    private boolean checkFields(){
        boolean isValid = true;
        if(ratingBar.getNumStars()==0){
            Toast.makeText(ReviewActivity.this, "Please select a rating between 1 and 5",
                    Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if(reviewEditText.getEditableText().toString().equals("")){
            reviewTextInput.setErrorEnabled(true);
            reviewTextInput.setError("Please enter a comment");
            isValid = false;
        }
        return isValid;
    }

    @OnClick(R.id.submit_review_button)
    public void sendReview() {
        if(checkFields()) {
            //Get Values
            int reviewRating = ratingBar.getNumStars(); //get stars
            String reviewDescription = reviewEditText.getEditableText().toString(); //get description

            //Add review to Firebase
            //Reviews -> Provider ID -> ParkingSpotID -> BookingID
            DatabaseReference parkingSpotReviewRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE)
                    .child(booking.getMListing().getProviderID()).child(booking.getMListing().getParkingSpot().getParkingSpotID())
                    .child(booking.getBookingID());

            parkingSpotReviewRef.child(Consts.REVIEW_DESCRIPTION).setValue(reviewDescription);
            parkingSpotReviewRef.child(Consts.REVIEW_RATING).setValue(reviewRating);

            finish();

        }
    }
}