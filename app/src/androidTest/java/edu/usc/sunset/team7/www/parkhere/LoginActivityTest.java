package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.LoginActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class LoginActivityTest {

    private String validEmail = "jonathzw@usc.edu";
    private String validPassword = "jonathanwang!";

    private String invalidEmail = "invalid@email.doesnotexist";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Rule
    public UiThreadTestRule activityUITestRule = new UiThreadTestRule();

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(Consts.SIGN_OUT_EXTRA, true);
        activityRule.launchActivity(intent);
    }

    @Test
    public void loginWithInvalidEmail_whitebox() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((AppCompatEditText) activityRule.getActivity().findViewById(R.id.email_edittext)).setText(invalidEmail);
            }
        });
        activityRule.getActivity().attemptLogin();

        Assert.assertNull(activityRule.getActivity().getCurrentUser());
        System.out.println("FINISHED INVALID EMAIL WHITE BOX");
    }

    @Test
    public void loginWithInvalidEmail_blackbox() {
        onView(withId(R.id.email_edittext)).perform(typeText(invalidEmail));
        onView(withId(R.id.password_edittext)).perform(typeText(validPassword));
        onView(withId(R.id.password_edittext)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // make sure that the login did not redirect
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
        System.out.println("FINISHED INVALID EMAIL BLACK BOX");
        activityRule.getActivity().finish();
    }
//
    @Test
    public void loginWithValidEmail_blackbox() {
        onView(withId(R.id.email_edittext)).perform(typeText(validEmail));
        onView(withId(R.id.password_edittext)).perform(typeText(validPassword));
        onView(withId(R.id.password_edittext)).perform(closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.home_toolbar)).check(matches(isDisplayed()));
    }
}