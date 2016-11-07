package edu.usc.sunset.team7.www.parkhere;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Fragments.SearchFragment;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Acer on 11/6/2016.
 */

public class SearchFragmentTest {
    public static final String FRAGMENT_TAG = "fragment_tag";

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
        intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        activityRule.launchActivity(intent).getFragmentManager()
                .findFragmentById(R.id.search_autocomplete_fragment);

    }

}
