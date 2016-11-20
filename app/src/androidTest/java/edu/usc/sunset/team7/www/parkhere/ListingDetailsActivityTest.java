package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

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

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListingDetailsActivityTest {
    private Listing sample;

    @Rule
    public ActivityTestRule<ListingDetailsActivity> activityRule =
            new ActivityTestRule<>(ListingDetailsActivity.class, true, false);

    @Before
    public void addListingToBeRemoved() {
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

        //first add an example listing
        sample = new Listing();
        sample.setDescription("Description!");
        sample.setPrice(20.99);
        sample.getParkingSpot().setHandicap(false);
        sample.getParkingSpot().setCompact(false);
        sample.getParkingSpot().setCovered(true);
        sample.setName("Sample listing");
        sample.setListingID("Sample listing");
        sample.setProviderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        sample.setRefundable(false);

    }

    @Test
    public void deleteListingTest_whitebox() {
        //write to active listings database
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference()
                .child(Consts.LISTINGS_DATABASE).child(sample.getProviderID()).child(Consts.ACTIVE_LISTINGS)
                .child(sample.getListingID());
        nameRef.child(Consts.LISTING_DESCRIPTION).setValue(sample.getDescription());
        nameRef.child(Consts.LISTING_PRICE).setValue(sample.getPrice());

        nameRef.child(Consts.LISTING_HANDICAP).setValue(sample.isHandicap());
        nameRef.child(Consts.LISTING_COMPACT).setValue(sample.isCompact());
        nameRef.child(Consts.LISTING_COVERED).setValue(sample.isCovered());
        nameRef.child(Consts.LISTING_NAME).setValue(sample.getName());
        nameRef.child(Consts.LISTING_REFUNDABLE).setValue(sample.isRefundable());

        Intent intent = new Intent();
        intent.putExtra(Consts.LISTING_EXTRA, sample);
        activityRule.launchActivity(intent);
        activityRule.getActivity().deleteListing();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                child(Consts.LISTINGS_DATABASE).child(sample.getProviderID())
                .child(Consts.ACTIVE_LISTINGS).child(sample.getListingID()).child(Consts.LISTING_NAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                Assert.assertEquals(value, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Test
    public void deleteListingAfterStartTime_blackbox() {
        //write to inactive listings database
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference()
                .child(Consts.LISTINGS_DATABASE).child(sample.getProviderID()).child(Consts.INACTIVE_LISTINGS)
                .child(sample.getListingID());
        nameRef.child(Consts.LISTING_DESCRIPTION).setValue(sample.getDescription());
        nameRef.child(Consts.LISTING_PRICE).setValue(sample.getPrice());

        nameRef.child(Consts.LISTING_HANDICAP).setValue(sample.isHandicap());
        nameRef.child(Consts.LISTING_COMPACT).setValue(sample.isCompact());
        nameRef.child(Consts.LISTING_COVERED).setValue(sample.isCovered());
        nameRef.child(Consts.LISTING_NAME).setValue(sample.getName());
        nameRef.child(Consts.LISTING_REFUNDABLE).setValue(sample.isRefundable());

        Intent intent = new Intent();
        intent.putExtra(Consts.LISTING_EXTRA, sample);
        activityRule.launchActivity(intent);
        int deleteListingButtonVisibility = activityRule.getActivity().deleteListingButton.getVisibility();

        //make sure that the delete button is not visible to the user
        Assert.assertEquals(deleteListingButtonVisibility, View.GONE);
    }

}
