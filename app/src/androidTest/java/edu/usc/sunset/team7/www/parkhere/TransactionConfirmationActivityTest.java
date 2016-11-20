package edu.usc.sunset.team7.www.parkhere;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.TransactionConfirmationActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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

    @Rule
    public UiThreadTestRule activityUITestRule = new UiThreadTestRule();

    @Before
    public void initialize() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("kunal@me.com", "hello12345");
        final Semaphore loginSemaphore = new Semaphore(0);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i("TESTING LOG", "NOT LOGGED In");
                } else {
                    loginSemaphore.release();
                }
            }
        });
        try { loginSemaphore.acquire(); }
        catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        //creating example listing
        sample = new Listing();
        sample.setDescription("Description!");
        sample.setPrice(20.99);
        sample.getParkingSpot().setHandicap(false);
        sample.getParkingSpot().setCompact(false);
        sample.getParkingSpot().setCovered(true);
        sample.setName("Sample listing");
        sample.setListingID("Sample listing");
        sample.setProviderID("Sample provider");
        sample.setRefundable(false);

        //creating example listingDetailsString and listing distance
        listingDetailsString = "Example listing details string";
        listingDistance = 5.58;
        paypalEmail = "test@test.com";
    }

    @Test
    public void testBooking_whitebox() {
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
        Assert.assertEquals(false, db.getKey() == null); //properly written to bookings database

        db = FirebaseDatabase.getInstance().getReference().child(Consts.LISTINGS_DATABASE)
                .child(uid).child(Consts.INACTIVE_LISTINGS).child(sample.getListingID());
        Assert.assertEquals(false, db.getKey() == null); //properly written to inactive listings
        activityRule.getActivity().finish();
    }

    @Test
    public void testPlaceBookingLeadsToHomeActivity_blackbox() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Consts.LISTING_TO_BE_BOOKED, sample);
        bundle.putSerializable(Consts.LISTING_DETAILS_STRING, listingDetailsString);
        bundle.putSerializable(Consts.LISTING_DISTANCE, listingDistance);
        bundle.putSerializable(Consts.PAYPAL_EMAIL, paypalEmail);
        intent.putExtras(bundle);

        activityRule.launchActivity(intent);
        onView(withId(R.id.place_booking_button)).perform(scrollTo()).perform(click());
        //check that its moved to the home activity
        onView(withId(R.id.home_toolbar)).check(matches(isDisplayed()));
    }

    @After
    public void cleanup(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child(Consts.BOOKINGS_DATABASE).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.removeValue();
        db = FirebaseDatabase.getInstance().getReference().child(Consts.LISTINGS_DATABASE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Consts.INACTIVE_LISTINGS).child(sample.getListingID());
        db.removeValue();
    }
}
