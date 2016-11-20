package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.ListingDetailsActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.ResultsActivity;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Acer on 11/6/2016.
 */

public class FilterTest {
    private Listing handicap;
    private Listing covered;
    private Listing compact;
    private Listing none;
    private ArrayList<ResultsPair> listings;

    @Rule
    public ActivityTestRule<ResultsActivity> activityRule =
            new ActivityTestRule<>(ResultsActivity.class, true, false);

    @Before
    public void createListings() {
        handicap = new Listing();
        covered = new Listing();
        compact = new Listing();
        none = new Listing();

        handicap.getParkingSpot().setHandicap(true);
        covered.getParkingSpot().setCovered(true);
        compact.getParkingSpot().setCompact(true);

        listings = new ArrayList<ResultsPair>();
        listings.add(new ResultsPair(handicap, 0));
        listings.add(new ResultsPair(covered, 0));
        listings.add(new ResultsPair(compact, 0));
        listings.add(new ResultsPair(none, 0));
    }

    @Test
    public void testFilterMethods_whitebox() {
        activityRule.launchActivity(new Intent());
        List<ResultsPair> compactResults = activityRule.getActivity().filteredResultsOnCompact(listings);
        Assert.assertEquals(compactResults.size(), 1);
        List<ResultsPair> coveredResults = activityRule.getActivity().filterResultsOnCovered(listings);
        Assert.assertEquals(coveredResults.size(), 1);
        List<ResultsPair> handicapResults = activityRule.getActivity().filterResultsOnHandicap(listings);
        Assert.assertEquals(handicapResults.size(), 1);
    }

    @Test
    public void test_blackbox(){
        activityRule.launchActivity(new Intent());
        onView(withId(R.id.filter_button)).perform(click());
        onView(withId(R.id.filter_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.covered_button_control)).check(matches(isDisplayed()));
        onView(withId(R.id.handicap_button_control)).check(matches(isDisplayed()));
        onView(withId(R.id.compact_button_control)).check(matches(isDisplayed()));
    }
}
