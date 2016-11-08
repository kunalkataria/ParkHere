package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.PaypalActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.TransactionConfirmationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Acer on 11/7/2016.
 */

public class PaypalActivityTest {
    @Rule
    public ActivityTestRule<PaypalActivity> activityRule =
            new ActivityTestRule<>(PaypalActivity.class, true, false);
    @Rule
    public UiThreadTestRule activityUITestRule = new UiThreadTestRule();

    @Test
    public void validEmailPassword_whitebox() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.email_edittext)).perform(typeText("test@test.com"));
        onView(withId(R.id.password_edittext)).perform((typeText("test")));
        Assert.assertEquals(activityRule.getActivity().checkValues(), true);
    }

    @Test
    public void inValidEmailPassword_whitebox() {
        final Semaphore lock = new Semaphore(0);
        Intent intent = new Intent();
        activityRule.launchActivity(intent);
        onView(withId(R.id.email_edittext)).perform(typeText("test"));
        onView(withId(R.id.password_edittext)).perform((typeText("test")));
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(activityRule.getActivity().checkValues(), false);
                lock.release();
            }
        });
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
