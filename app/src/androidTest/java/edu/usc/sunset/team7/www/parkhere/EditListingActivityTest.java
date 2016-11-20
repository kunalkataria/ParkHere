package edu.usc.sunset.team7.www.parkhere;

/**
 * Created by johnsonhui on 11/5/16.
 */
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import edu.usc.sunset.team7.www.parkhere.Activities.EditListingActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class EditListingActivityTest {
    private Listing testListing, editTestListing;
    final Semaphore mSemaphore = new Semaphore(0);
    private DatabaseReference mDatabase;
    private Instrumentation mInstrumentation;

    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<EditListingActivity> activityTestRule =
            new ActivityTestRule<>(EditListingActivity.class, true, false);

    @Rule
    public UiThreadTestRule activityUITestRule = new UiThreadTestRule();

    @Before
    public void setup() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("tester@test.me", "hello12345!");

        final Semaphore loginSemaphore = new Semaphore(0);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i("TESTING LOG", "NOT LOGGED In");
                } else {
                    loginSemaphore.release();
                    //balanceRef.child(Consts.USER_BALANCE).setValue(balanceAmount);

                }
            }
        });
        try {
            loginSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
        testListing = new Listing();
        testListing.setProviderID(currentUID);
        testListing.setPrice(3.50);
        testListing.getParkingSpot().setCompact(false);
        testListing.getParkingSpot().setCovered(false);
        testListing.getParkingSpot().setHandicap(true);
        testListing.setName("test name");
        testListing.setDescription("test description");
        testListing.getParkingSpot().setImageURL("https://firebasestorage.googleapis.com/v0/b/parkhere-ceccb.appspot.com/o/parking_spaces%2F-KVkrHf_L55sVNtSn0AW?alt=media&token=448c50fc-1e7a-49bf-bf49-0fc522f6f3cb");
        testListing.getParkingSpot().setLatitude(34.0223519);
        testListing.getParkingSpot().setLongitude(-118.285117);
        testListing.setStartTime(1477983600);
        testListing.setStopTime(1478678400);
        testListing.setRefundable(true);
        //testListing.setListingID(listingID);
        testListing.setProviderID(currentUID);

        editTestListing = new Listing();
        editTestListing.setName("test edit name");
        editTestListing.setPrice(5.55);
        editTestListing.getParkingSpot().setCompact(true);
        editTestListing.getParkingSpot().setCovered(true);
        editTestListing.getParkingSpot().setHandicap(false);
        editTestListing.setDescription("test edit description");
        editTestListing.setRefundable(false);
        editTestListing.getParkingSpot().setImageURL("https://firebasestorage.googleapis.com/v0/b/parkhere-ceccb.appspot.com/o/parking_spaces%2F-KVkrHf_L55sVNtSn0AW?alt=media&token=448c50fc-1e7a-49bf-bf49-0fc522f6f3cb");
        editTestListing.getParkingSpot().setLatitude(34.0223519);
        editTestListing.getParkingSpot().setLongitude(-118.285117);
        editTestListing.setStartTime(1477983600);
        editTestListing.setStopTime(1478678400);
        //editTestListing.setListingID("1");
        editTestListing.setProviderID(currentUID);


        String listingID = mDatabase1.child(Consts.LISTINGS_DATABASE).child(currentUID).child(Consts.ACTIVE_LISTINGS).push().getKey();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Uri sourceImageUri = null;


        final DatabaseReference newListingRef = mDatabase1.child(Consts.LISTINGS_DATABASE).child(currentUID).child(Consts.ACTIVE_LISTINGS).child(listingID);
        newListingRef.child(Consts.LISTING_NAME).setValue(testListing.getName());
        newListingRef.child(Consts.LISTING_DESCRIPTION).setValue(testListing.getDescription());
        newListingRef.child(Consts.LISTING_REFUNDABLE).setValue(testListing.isRefundable());
        newListingRef.child(Consts.LISTING_PRICE).setValue(testListing.getPrice());
        newListingRef.child(Consts.LISTING_COMPACT).setValue(testListing.isCompact());
        newListingRef.child(Consts.LISTING_COVERED).setValue(testListing.isCovered());
        newListingRef.child(Consts.LISTING_HANDICAP).setValue(testListing.isHandicap());
        newListingRef.child(Consts.LISTING_LATITUDE).setValue(testListing.getLatitude());
        newListingRef.child(Consts.LISTING_LONGITUDE).setValue(testListing.getLongitude());
        newListingRef.child(Consts.LISTING_START_TIME).setValue(testListing.getStartTime());
        newListingRef.child(Consts.LISTING_END_TIME).setValue(testListing.getStopTime());
        newListingRef.child(Consts.LISTING_IMAGE).setValue(testListing.getImageURL());

        mSemaphore.release();
        editTestListing.setListingID(listingID);
        testListing.setListingID(listingID);


        mInstrumentation = getInstrumentation();

    }


    //black box
    @Test
    public void validateActivityPrefill() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
            activityTestRule.launchActivity(i);
            onView(withId(R.id.name_edittext)).check(matches(withText("test name")));
            onView(withId(R.id.description_edittext)).check(matches(withText("test description")));
            onView(withId(R.id.price_edittext)).check(matches(withText("3.5")));
            onView(withId(R.id.refundable_rButton)).check(matches(isChecked()));
            onView(withId(R.id.handicap_button_control)).check(matches((isChecked())));
            onView(withId(R.id.compact_button_control)).check(matches(not(isChecked())));
            onView(withId(R.id.covered_button_control)).check(matches(not(isChecked())));
            mSemaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //white box
    @Test
    public void testFields() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
            activityTestRule.launchActivity(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        final Semaphore lock = new Semaphore(1);
        onView(withId(R.id.name_edittext)).perform(clearText());
        onView(withId(R.id.description_edittext)).perform(clearText());
        onView(withId(R.id.price_edittext)).perform(clearText());
        //might need to check the cancel policy but I think it should already be preset and we cant exit out of it
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Assert.assertEquals(false, activityTestRule.getActivity().checkFields());
                lock.release();
            }
        });

        //insert name
        onView(withId(R.id.name_edittext)).perform(typeText(testListing.getName()));
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(false, activityTestRule.getActivity().checkFields());
                lock.release();
            }
        });

        //insert description
        onView(withId(R.id.description_edittext)).perform(typeText(testListing.getDescription()));
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(false, activityTestRule.getActivity().checkFields());
                lock.release();
            }
        });

        //insert price
        Double price = testListing.getPrice();
        String convert = price.toString();
        onView(withId(R.id.price_edittext)).perform(typeText(convert));
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertEquals(true, activityTestRule.getActivity().checkFields());
                lock.release();
            }
        });
        mSemaphore.release();
    }

    //black box
    @Test
    public void checkFailSubmit() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
            activityTestRule.launchActivity(i);
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

    //black box
    @Test
    public void sendEditPosting() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
            activityTestRule.launchActivity(i);

            Double price = editTestListing.getPrice();
            String convert = price.toString();
            onView(withId(R.id.name_edittext)).perform(clearText());
            onView(withId(R.id.description_edittext)).perform(clearText());
            onView(withId(R.id.price_edittext)).perform(clearText());
            onView(withId(R.id.name_edittext)).perform(typeText(editTestListing.getName()));
            onView(withId(R.id.price_edittext)).perform(typeText(convert));
            onView(withId(R.id.description_edittext)).perform(typeText(editTestListing.getDescription()));
            onView(withId(R.id.description_edittext)).perform(closeSoftKeyboard());
            onView(withId(R.id.nonrefundable_rButton)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.handicap_button_control)).perform(scrollTo()).perform(click()).check(matches(not(isChecked())));
            onView(withId(R.id.compact_button_control)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.covered_button_control)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.upload_listing_button)).perform(scrollTo()).perform(click());

            onView(withId(R.id.listing_listview)).check(matches(isDisplayed()));
            mSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void editPostingInDatabase() {
        try {
            mSemaphore.acquire();
            Intent i = new Intent();
            i.putExtra(Consts.LISTING_EDIT_EXTRA, testListing);
            activityTestRule.launchActivity(i);

            Double price = editTestListing.getPrice();
            String convert = price.toString();
            onView(withId(R.id.name_edittext)).perform(clearText());
            onView(withId(R.id.description_edittext)).perform(clearText());
            onView(withId(R.id.price_edittext)).perform(clearText());

            onView(withId(R.id.name_edittext)).perform(typeText(editTestListing.getName()));
            onView(withId(R.id.price_edittext)).perform(typeText(convert));
            onView(withId(R.id.description_edittext)).perform(typeText(editTestListing.getDescription()));
            onView(withId(R.id.description_edittext)).perform(closeSoftKeyboard());
            onView(withId(R.id.nonrefundable_rButton)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.handicap_button_control)).perform(scrollTo()).perform(click()).check(matches(not(isChecked())));
            onView(withId(R.id.compact_button_control)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.covered_button_control)).perform(scrollTo()).perform(click()).check(matches(isChecked()));
            onView(withId(R.id.upload_listing_button)).perform(scrollTo()).perform(click());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                    child(Consts.LISTINGS_DATABASE).child(editTestListing.getProviderID())
                    .child(Consts.ACTIVE_LISTINGS).child(editTestListing.getListingID());
            final Listing toCompare = new Listing();
            toCompare.setProviderID(editTestListing.getProviderID());
            toCompare.setListingID(editTestListing.getListingID());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        switch (child.getKey()) {
                            case Consts.LISTING_COMPACT:
                                toCompare.getParkingSpot().setCompact(Boolean.parseBoolean(child.getValue().toString()));
                                break;
                            case Consts.LISTING_COVERED:
                                toCompare.getParkingSpot().setCovered(Boolean.parseBoolean(child.getValue().toString()));
                                break;
                            case Consts.LISTING_HANDICAP:
                                toCompare.getParkingSpot().setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                                break;
                            case Consts.LISTING_DESCRIPTION:
                                System.out.println("JUST READ DESCRIPTION");
                                toCompare.setDescription(child.getValue().toString());
                                break;
                            case Consts.LISTING_IMAGE:
                                toCompare.getParkingSpot().setImageURL(child.getValue().toString());
                                break;
                            case Consts.LISTING_LATITUDE:
                                toCompare.getParkingSpot().setLatitude(Double.parseDouble(child.getValue().toString()));
                                break;
                            case Consts.LISTING_LONGITUDE:
                                toCompare.getParkingSpot().setLongitude(Double.parseDouble(child.getValue().toString()));
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
                                toCompare.setPrice(Double.parseDouble(child.getValue().toString()));
                                break;
                        }
                    }
                    Assert.assertEquals(editTestListing.getListingID(), toCompare.getListingID());
                    Assert.assertEquals(editTestListing.getDescription(), toCompare.getDescription());
                    Assert.assertEquals(editTestListing.getImageURL(), toCompare.getImageURL());
                    Assert.assertEquals(editTestListing.getName(), toCompare.getName());
                    Assert.assertEquals(editTestListing.getProviderID(), toCompare.getProviderID());
                    Assert.assertEquals(editTestListing.isCompact(), toCompare.isCompact());
                    Assert.assertEquals(editTestListing.isCovered(), toCompare.isCovered());
                    Assert.assertEquals(editTestListing.isHandicap(), toCompare.isHandicap());
                    Assert.assertEquals(editTestListing.isRefundable(), toCompare.isRefundable());
                    Assert.assertEquals(editTestListing.getPrice(), toCompare.getPrice());
                    mSemaphore.release();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



//    @Test
//    public void listingInDbChanged() {
//        try {
//            mSemaphore.acquire();
//            mSemaphore.release();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    //
//
//
//
//
//
//        onView(withId(R.id.upload_listing_button)).perform(click());
//        //Probably can't use this but can uncomment if the above method fails during testing
//        //(activityTestRule.getActivity().findViewById(R.id.upload_listing_button)).callOnClick();
//



    @After
    public void finishTest() {
        try {
            mSemaphore.acquire();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(Consts.LISTINGS_DATABASE).child(testListing.getProviderID()).child(Consts.ACTIVE_LISTINGS).child(testListing.getListingID()).removeValue();
            activityTestRule.getActivity().finish();
            System.out.println("FINISH TEST CALLED");
            mSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
