package edu.usc.sunset.team7.www.parkhere;

import android.app.Fragment;
import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Created by Justin on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationFragmentUITest {

    @Rule
    public ActivityTestRule<HomeActivity> activityRule =
            new ActivityTestRule<>(HomeActivity.class, true, false);

    @Before
    public void initializeActivity() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("kunal@me.com", "hello12345");
    }

    @Test
    public void runNavigationDrawerTest(){
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        for (int i = 0; i < 5; i++) {

            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

            onData(anything()).inAdapterView(withId(R.id.left_drawer))
                    .atPosition(i).perform(click());

            switch(i){
                case 0:
                    onView(withId(R.id.fragment_search_layout)).check(matches(isDisplayed()));
                    onView(withId(R.id.fragment_listing_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_booking_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_balance_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_public_profile_layout)).check(doesNotExist());
                    break;
                case 1:
                    onView(withId(R.id.fragment_listing_layout)).check(matches(isDisplayed()));
                    onView(withId(R.id.fragment_search_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_booking_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_balance_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_public_profile_layout)).check(doesNotExist());
                    break;
                case 2:
                    onView(withId(R.id.fragment_booking_layout)).check(matches(isDisplayed()));
                    onView(withId(R.id.fragment_search_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_listing_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_balance_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_public_profile_layout)).check(doesNotExist());
                    break;
                case 3:
                    onView(withId(R.id.fragment_balance_layout)).check(matches(isDisplayed()));
                    onView(withId(R.id.fragment_search_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_listing_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_booking_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_public_profile_layout)).check(doesNotExist());
                    break;
                case 4:
                    onView(withId(R.id.fragment_public_profile_layout))
                            .check(matches(isDisplayed()));
                    onView(withId(R.id.fragment_search_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_listing_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_booking_layout)).check(doesNotExist());
                    onView(withId(R.id.fragment_balance_layout)).check(doesNotExist());
                    break;
            }

        }
    }
}
