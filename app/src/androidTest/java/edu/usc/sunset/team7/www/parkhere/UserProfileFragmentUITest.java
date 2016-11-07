package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Justin on 11/7/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfileFragmentUITest {

    @Rule
    public ActivityTestRule<HomeActivity> activityRule =
            new ActivityTestRule<>(HomeActivity.class, true, false);

    @Before
    public void initializeFragment() {
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
        try {
            loginSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

    }

    @Test
    public void populateFragment(){
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onData(anything()).inAdapterView(withId(R.id.left_drawer)).atPosition(4).perform(click());

        onView(withId(R.id.user_name_view_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.user_rating_bar_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.userProfileImage_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.review_content_space_fragment)).check(matches(isDisplayed()));

    }
}

