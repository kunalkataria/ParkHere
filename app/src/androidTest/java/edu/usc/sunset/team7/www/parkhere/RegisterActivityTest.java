package edu.usc.sunset.team7.www.parkhere;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;

/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<ListingDetailsActivity> activityRule =
            new ActivityTestRule<>(ListingDetailsActivity.class, true, false);

    @Before
    public void setUp() {


    }

    @Test
    public void validateUserWithCorrectFields() {

    }

    @Test
    public void validateUserWithIncorrectFields() {

    }

    @Test
    public void readFromDatabase() {

    }



}
