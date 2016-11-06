package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.TransactionConfirmationActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Acer on 11/6/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TransactionConfirmationActivityTest {
    private Listing sample;
    private String listingDetailsString;
    private String paypalEmail;
    private double listingDistance;

    @Rule
    public ActivityTestRule<TransactionConfirmationActivity> activityRule =
            new ActivityTestRule<>(TransactionConfirmationActivity.class, true, false);

    @Before
    public void initialize() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("kunal@me.com", "hello12345");

        //creating example listing
        sample = new Listing();
        sample.setDescription("Description!");
        sample.setPrice(20.99);
        sample.setHandicap(false);
        sample.setCompact(false);
        sample.setCovered(true);
        sample.setName("Sample listing");
        sample.setListingID("Sample listing");
        sample.setProviderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sample.setRefundable(false);

        //creating example listingDetailsString and listing distance
        listingDetailsString = "Example listing details string";
        listingDistance = 5.58;
        paypalEmail = "test@test.com";
    }

    @Test
    public void testBooking() {
        Intent intent = new Intent();
        intent.putExtra(Consts.LISTING_TO_BE_BOOKED, sample);
        intent.putExtra(Consts.LISTING_DETAILS_STRING, listingDetailsString);
        intent.putExtra(Consts.LISTING_DISTANCE, listingDistance);
        intent.putExtra(Consts.PAYPAL_EMAIL, paypalEmail);

        activityRule.launchActivity(intent);
        activityRule.getActivity().placeBooking();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(Consts.BOOKINGS_DATABASE).child(uid);
        db.removeValue();
        activityRule.getActivity().placeBooking();
        Assert.assertEquals(false, db.getKey() == null);
    }
}
