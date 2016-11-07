package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Hashtable;

import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;


/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostListingActivityTest {
    private String nameString, descriptionString;
    private double price;

    private static Hashtable<Integer, String> cancellationIds;

    private int startYear, startMonth, startDay, startHour, startMinute;

    private int stopYear, stopMonth, stopDay, stopHour, getStopMinute;

    private long startDate;
    private long stopDate;

    boolean isCompact,isHandicap, isCovered, isRefundable;

    @Rule
    public ActivityTestRule<PostListingActivity> activityRule =
            new ActivityTestRule<>(PostListingActivity.class, true, false);

    @Before
    public void setUp() {
        nameString = "Wyatt";
        descriptionString = "This is a cool spot";
        price = 34.30;

    }

    @Test
    public void validateUserWithCorrectFields() {
        activityRule.launchActivity(new Intent());


    }

    @Test
    public void validateUserWithIncorrectFields() {
        activityRule.launchActivity(new Intent());

    }

    @Test
    public void validateStartAndStopTime(){
        /*
        We don't have a check for if the start and stop times are valid (as in the start comes before the start time)
        Also, if the stop time is before the current time, the user should not be allowed to post the listing
        We can say this is a bug, and that we added some more checks to deal with this (before we only checked if a start and stop was selected.)
        */

    }

}
