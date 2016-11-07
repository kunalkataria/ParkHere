package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;


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
        mListing.setCovered(false);
        mListing.setCompact(false);
        mListing.setHandicap(true);
        mListing.setLatitude(34.022858);
        mListing.setLongitude(-118.279987);
        mListing.setStartTime((System.currentTimeMillis() / 1000) + 10000);
        mListing.setStopTime((System.currentTimeMillis() / 1000) + 86400);

        activityRule.launchActivity(new Intent());
    }

    @Test
    public void validateUserWithCorrectFields() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().fillInFields(mListing);
                Assert.assertEquals(true, activityRule.getActivity().checkFields());
            }
        });

        activityRule.getActivity().finish();
    }

    @Test
    public void validateUserWithIncorrectFields() {

        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListing.setPrice(-1.5);
                mListing.setStopTime(mListing.getStartTime() - 10000);
                activityRule.getActivity().fillInFields(mListing);
                Assert.assertEquals(false, activityRule.getActivity().checkFields());
            }
        });

        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListing.setStopTime((System.currentTimeMillis() / 1000) + 86400);
                activityRule.getActivity().fillInFields(mListing);
                Assert.assertEquals(false, activityRule.getActivity().checkFields());
            }
        });

        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListing.setPrice(34.30);
                activityRule.getActivity().fillInFields(mListing);
                Assert.assertEquals(true, activityRule.getActivity().checkFields());
            }
        });

    }

    @Test
    public void validateStartAndStopTime() {
        /*
        We don't have a check for if the start and stop times are valid (as in the start comes before the start time)
        Also, if the stop time is before the current time, the user should not be allowed to post the listing
        We can say this is a bug, and that we added some more checks to deal with this (before we only checked if a start and stop was selected.)
        */

    }

}
