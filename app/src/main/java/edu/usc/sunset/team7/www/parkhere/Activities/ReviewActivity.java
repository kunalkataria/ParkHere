package edu.usc.sunset.team7.www.parkhere.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Acer on 10/30/2016.
 */

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void sendReview() {
        int reviewRating = 0; //get stars
        String reviewDescription = ""; //get description

        //accessing firebase
        String providerID = "";
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE+"/"+providerID).push();
        dbRef.child(Consts.REVIEW_DESCRIPTION).setValue(reviewDescription);
        dbRef.child(Consts.REVIEW_RATING).setValue(reviewRating);
    }

}
