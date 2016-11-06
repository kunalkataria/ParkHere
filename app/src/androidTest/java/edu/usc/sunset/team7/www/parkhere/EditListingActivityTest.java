package edu.usc.sunset.team7.www.parkhere;

/**
 * Created by johnsonhui on 11/5/16.
 */
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;

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

import edu.usc.sunset.team7.www.parkhere.Activities.EditListingActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class EditListingActivityTest {
    private Listing testListing;
    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<EditListingActivity> activityTestRule =
            new ActivityTestRule<>(EditListingActivity.class, true, false);

    @Before
    public void intent() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("JLu@gmail.com", "123456789!");
        Intent i = new Intent();
        testListing = new Listing();
        testListing.setProviderID(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        testListing.setListingID("KVkrHf_L55sVNtSn0AW");
        i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
        activityTestRule.launchActivity(i);
    }
    // Looks for an EditText with id = "R.id.etInput"
    // Types the text "Hello" into the EditText
    // Verifies the EditText has text "Hello"

    @Test
    public void validateActivityPrefill() {
        onView(withId(R.id.name_edittext)).check(matches(withText("test name")));
        onView(withId(R.id.description_edittext)).check(matches(withText("test description")));
        onView(withId(R.id.price_edittext)).check(matches(withText("3.5")));
        onView(withId(R.id.refundable_rButton)).check(matches(isChecked()));
        onView(withId(R.id.handicap_button_control)).check(matches((isChecked())));
        onView(withId(R.id.compact_button_control)).check(matches(not(isChecked())));
        onView(withId(R.id.covered_button_control)).check(matches(not(isChecked())));
    }

    //Testing editlistingactivity checkfields method
    @Test
    public void testFields() {
        //clear fields
        onView(withId(R.id.name_edittext)).perform(clearText());
        onView(withId(R.id.description_edittext)).perform(clearText());
        onView(withId(R.id.price_edittext)).perform(clearText());
        Assert.assertEquals(activityTestRule.getActivity().checkFields(), false);

        //only include name
        onView(withId(R.id.name_edittext)).perform(typeText(testListing.getName()));
        Assert.assertEquals(activityTestRule.getActivity().checkFields(), false);

        //name + description
        onView(withId(R.id.description_edittext)).perform(typeText(testListing.getDescription()));
        Assert.assertEquals(activityTestRule.getActivity().checkFields(), false);

        //name + description + price
        Double price = testListing.getPrice();
        String convert = price.toString();
        onView(withId(R.id.price_edittext)).perform(typeText(convert));
        Assert.assertEquals(activityTestRule.getActivity().checkFields(), true);

        //might need to check the cancel policy but I think it should already be preset and we cant exit out of it

    }

    //Testing posting edited listing into database
    @Test
    public void sendEditPosting() {
        ((AppCompatEditText) activityTestRule.getActivity().findViewById(R.id.name_edittext)).setText("NEW TEST NAME");
        onView(withId(R.id.upload_listing_button)).perform(click());
        //Probably can't use this but can uncomment if the above method fails during testing
        //(activityTestRule.getActivity().findViewById(R.id.upload_listing_button)).callOnClick();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                child(Consts.LISTINGS_DATABASE).child(testListing.getProviderID())
                .child(Consts.ACTIVE_LISTINGS).child(testListing.getListingID());
        final Listing toCompare = new Listing();
        toCompare.setProviderID(testListing.getProviderID());
        toCompare.setListingID(testListing.getListingID());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()) {
                        case Consts.LISTING_COMPACT:
                            toCompare.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_COVERED:
                            toCompare.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_HANDICAP:
                            toCompare.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_DESCRIPTION:
                            toCompare.setDescription(child.getValue().toString());
                            break;
                        case Consts.LISTING_IMAGE:
                            toCompare.setImageURL(child.getValue().toString());
                            break;
                        case Consts.LISTING_LATITUDE:
                            toCompare.setLatitude(Double.parseDouble(child.getValue().toString()));
                            break;
                        case Consts.LISTING_LONGITUDE:
                            toCompare.setLongitude(Double.parseDouble(child.getValue().toString()));
                            break;
                        case Consts.LISTING_NAME:
                            toCompare.setName(child.getValue().toString());
                            break;
                        case Consts.LISTING_REFUNDABLE:
                            toCompare.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_START_TIME:
                            toCompare.setStartTime(Long.parseLong(child.getValue().toString()));
                            break;
                        case Consts.LISTING_END_TIME:
                            toCompare.setStopTime(Long.parseLong(child.getValue().toString()));
                            break;
                        case Consts.LISTING_PRICE:
                            toCompare.setPrice(Long.parseLong(child.getValue().toString()));
                            break;
                    }
                }
                Assert.assertEquals(testListing.getListingID(), toCompare.getListingID());
                Assert.assertEquals(testListing.getDescription(), toCompare.getDescription());
                Assert.assertEquals(testListing.getImageURL(), toCompare.getImageURL());
                Assert.assertEquals(testListing.getLatitude(), toCompare.getLatitude());
                Assert.assertEquals(testListing.getLongitude(), toCompare.getLongitude());
                Assert.assertEquals(testListing.getName(), toCompare.getName());
                Assert.assertEquals(testListing.getProviderID(), toCompare.getProviderID());
                Assert.assertEquals(testListing.getStartTime(), toCompare.getStartTime());
                Assert.assertEquals(testListing.getStopTime(), toCompare.getStopTime());
                Assert.assertEquals(testListing.isCompact(), toCompare.isCompact());
                Assert.assertEquals(testListing.isCovered(), toCompare.isCovered());
                Assert.assertEquals(testListing.isHandicap(), toCompare.isHandicap());
                Assert.assertEquals(testListing.isRefundable(), toCompare.isRefundable());
                Assert.assertEquals(testListing.getPrice(), toCompare.getPrice());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
