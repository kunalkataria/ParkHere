package edu.usc.sunset.team7.www.parkhere;

/**
 * Created by johnsonhui on 11/5/16.
 */
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Activities.EditListingActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class EditListingActivityTest {
    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<EditListingActivity> activityTestRule =
            new ActivityTestRule<>(EditListingActivity.class, true, false);

    @Before
    public void intent() {
        Intent i = new Intent();
        Listing testListing = new Listing();
        testListing.setProviderID("12345");
        testListing.setPrice(3.50);
        testListing.setCompact(false);
        testListing.setCovered(false);
        testListing.setHandicap(true);
        testListing.setName("test name");
        testListing.setDescription("test description");
        testListing.setImageURL("https://firebasestorage.googleapis.com/v0/b/parkhere-ceccb.appspot.com/o/parking_spaces%2F-KVkrHf_L55sVNtSn0AW?alt=media&token=448c50fc-1e7a-49bf-bf49-0fc522f6f3cb");
        testListing.setLatitude(34.0223519);
        testListing.setLongitude(-118.285117);
        testListing.setStartTime(1477983600);
        testListing.setStopTime(1478678400);
        testListing.setRefundable(true);
        i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
        activityTestRule.launchActivity(i);
    }
    // Looks for an EditText with id = "R.id.etInput"
    // Types the text "Hello" into the EditText
    // Verifies the EditText has text "Hello"
    @Test
    public void validateEditText() {
        //onView(withId(R.id.name_textinputlayout)).perform(typeText("test name")).check(matches(withText("Hello")));
        onView(withId(R.id.name_edittext)).check(matches(withText("test name")));
        onView(withId(R.id.description_edittext)).check(matches(withText("test description")));
        onView(withId(R.id.price_edittext)).check(matches(withText("3.5")));
        onView(withId(R.id.refundable_rButton)).check(matches(isChecked()));
        onView(withId(R.id.handicap_button_control)).check(matches((isChecked())));
        onView(withId(R.id.compact_button_control)).check(matches(not(isChecked())));
        onView(withId(R.id.covered_button_control)).check(matches(not(isChecked())));
    }
}
