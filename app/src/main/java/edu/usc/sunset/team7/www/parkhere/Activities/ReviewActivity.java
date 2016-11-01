package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.widget.RatingBar;

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
    @BindView(R.id.review_description_text) AppCompatEditText reviewTextView;
    @BindView(R.id.submit_review_button) AppCompatButton submitReviewButton;

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
        setContentView(R.layout.activity_make_review);
        ButterKnife.bind(this);
        setValues();
    }

    private void setValues() {
        ratingBar.setIsIndicator(false);
        ratingBar.setMax(5);
        ratingBar.setStepSize(1.0f);
    }

    @OnClick(R.id.submit_review_button)
    public void sendReview() {
        int reviewRating = ratingBar.getNumStars(); //get stars
        String reviewDescription = reviewTextView.getEditableText().toString(); //get description

        if(reviewDescription.equals("")) return;

        //accessing firebase
        String providerID = booking.getMListing().getProviderID();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE+"/"+providerID).push();
        dbRef.child(Consts.REVIEW_DESCRIPTION).setValue(reviewDescription);
        dbRef.child(Consts.REVIEW_RATING).setValue(reviewRating);
        finish();
    }

}