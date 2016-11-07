package edu.usc.sunset.team7.www.parkhere;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Acer on 11/7/2016.
 */

public class SearchFragmentTest {

    @Rule
    public ActivityTestRule<HomeActivity> activityRule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class, true, false);

    @Test
    public void testForUIElements_blackbox() {
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        activityRule.launchActivity(intent);

        onView(withId(R.id.search_autocomplete_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.latitude_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.longitude_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.search_date_checkbox)).check(matches(isDisplayed()));
        onView(withId(R.id.start_date_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.stop_date_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.place_autocomplete_search_button)).check(matches(isDisplayed()));
    }

}
