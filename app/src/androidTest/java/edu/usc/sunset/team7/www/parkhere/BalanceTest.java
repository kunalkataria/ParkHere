package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by kunal on 11/6/16.
 */

public class BalanceTest {

    public static final String FRAGMENT_TAG = "fragment_tag";
    public static final double balanceAmount = 50.00;
    public static final String balanceString = "$50.00";
    public static final String emptyBalanceString = "$0.00";

    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule =
            new ActivityTestRule<>(HomeActivity.class, true, false);


    private Semaphore mSemaphore = new Semaphore(0);

    // set balance of user to be $50
    @Before
    public void setup() {

        FirebaseAuth.getInstance().signInWithEmailAndPassword("tester@test.me", "hello12345!");
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i("TESTING LOG", "NOT LOGGED In");
                } else {
                    String currentUID = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference balanceRef = FirebaseDatabase.getInstance().getReference()
                            .child(Consts.USERS_DATABASE).child(currentUID);

                    balanceRef.child(Consts.USER_BALANCE).setValue(balanceAmount);
                    mSemaphore.release();

                }
            }
        });

    }

    @Test
    public void validateDisplay() {
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra(FRAGMENT_TAG, Consts.BALANCE_FRAGMENT_TAG);
        activityTestRule.launchActivity(intent);

        onView(withId(R.id.current_balance)).check(matches(withText(balanceString)));
        mSemaphore.release();
    }

    @Test
    public void validateTransfer() {
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra(FRAGMENT_TAG, Consts.BALANCE_FRAGMENT_TAG);
        activityTestRule.launchActivity(intent);

        onView(withId(R.id.transfer_button)).perform(click());
        onView(withId(R.id.current_balance)).check(matches(withText(emptyBalanceString)));
        mSemaphore.release();
    }


}
