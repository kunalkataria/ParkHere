package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;


/**
 * Created by Acer on 11/5/2016.
 */

public class PostListingActivityTest {
    private String currentUID;

    private String validName, validDescription;
    private double validPrice;
    private long validStartDate;
    private long validStopDate;
    private boolean isCompact,isHandicap, isCovered, isRefundable;
    private double validLatitude, validLongitude;

    final Semaphore mSemaphore = new Semaphore(0);
    private Listing mListing;

    @Rule
    public ActivityTestRule<PostListingActivity> activityRule =
            new ActivityTestRule<>(PostListingActivity.class, true, false);

    @Rule
    public UiThreadTestRule activityUITestRule = new UiThreadTestRule();

    @Before
    public void setUp() {
        mListing = new Listing();
        mListing.setName("Test listing");
        mListing.setDescription("Test description");
        mListing.setPrice(34.30);
        mListing.setRefundable(true);
        mListing.getParkingSpot().setCovered(false);
        mListing.getParkingSpot().setCompact(false);
        mListing.getParkingSpot().setHandicap(true);
        mListing.getParkingSpot().setLatitude(34.022858);
        mListing.getParkingSpot().setLongitude(-118.279987);
        mListing.setStartTime((System.currentTimeMillis() / 1000) + 10000);
        mListing.setStopTime((System.currentTimeMillis() / 1000) + 86400);

        //activityRule.launchActivity(new Intent());
        mSemaphore.release();
    }

//    @Test
//    public void validateUserWithCorrectFields() {
//
//        final Semaphore mSemaphore = new Semaphore(0);
//
//        activityRule.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                activityRule.getActivity().fillInFields(mListing);
//                mSemaphore.release();
//            }
//        });
//
//        Assert.assertEquals(true, activityRule.getActivity().checkFields());
//
//        activityRule.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mSemaphore.acquire();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    @Test
    public void validateCheckFieldsUI() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            activityRule.launchActivity(i);

            //clear fields
            onView(withId(R.id.name_edittext)).perform(clearText());
            onView(withId(R.id.description_edittext)).perform(clearText());
            onView(withId(R.id.price_edittext)).perform(clearText());
            onView(withId(R.id.upload_listing_button)).perform(scrollTo()).perform(click());
            onView(allOf(withId(R.id.name_textinputlayout), withText("Please enter a name.")));
            onView(allOf(withId(R.id.description_edittext), withText("Please enter a description.")));
            onView(allOf(withId(R.id.price_edittext), withText("Please enter a price greater than $0.")));
            mSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateUserWithIncorrectFields() {
        final Semaphore lock = new Semaphore(0);
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            activityRule.launchActivity(i);
            activityRule.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityRule.getActivity().fillInFields(mListing);
                }
            });

            onView(withId(R.id.price_edittext)).perform(clearText());
            onView(withId(R.id.price_edittext)).perform(typeText("0"));

            activityRule.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(false, activityRule.getActivity().checkFields());
                    lock.release();
                }
            });

            onView(withId(R.id.price_edittext)).perform(clearText());
            onView(withId(R.id.price_edittext)).perform(typeText("34.30"));

            activityRule.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Assert.assertEquals(true, activityRule.getActivity().checkFields());
                    lock.release();
                }
            });
            mSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
