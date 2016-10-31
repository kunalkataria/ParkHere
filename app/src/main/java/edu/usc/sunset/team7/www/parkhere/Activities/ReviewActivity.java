package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Acer on 10/30/2016.
 */

public class ReviewActivity extends AppCompatActivity {

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
    }

    private void sendReview() {
        int reviewRating = 0; //get stars
        String reviewDescription = ""; //get description

        //accessing firebase
        String providerID = booking.getMListing().getProviderID();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE+"/"+providerID).push();
        dbRef.child(Consts.REVIEW_DESCRIPTION).setValue(reviewDescription);
        dbRef.child(Consts.REVIEW_RATING).setValue(reviewRating);


    }

}
