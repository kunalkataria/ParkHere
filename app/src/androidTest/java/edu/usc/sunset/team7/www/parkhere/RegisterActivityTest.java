package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import edu.usc.sunset.team7.www.parkhere.Activities.RegisterActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivityTest {
    Random rand = new Random();
    int randomNumber = rand.nextInt(100000) + 1;
    String validEmail = "wyatt" + randomNumber + "@me.com";
    String validPassword = "wyattkim5@";
    String validFirstName = "Wyatt";
    String validLastName = "Kim";
    String validPhoneNumber = "4154124807";
    String validProfPicURL = Consts.USER_DEFAULT_PROFILE_PIC_URL;

    String invalidEmail = "wyatt";
    String invalidPassword = "2520";
    String invalidFirstName = "";
    String invalidLastName = "";
    String invalidPhoneNumber = "415-412-4807";
    String invalidProfPicURL = "invalidUrl";


    @Rule
    public ActivityTestRule<RegisterActivity> activityRule =
            new ActivityTestRule<>(RegisterActivity.class, true, false);

    //white-box
    @Test
    public void validateUserWithCorrectFields() {
        Intent intent = new Intent();
        intent.putExtra("test", true);
        activityRule.launchActivity(intent);
        if (activityRule.getActivity().checkValues(validFirstName, validLastName, validPhoneNumber, validEmail, validPassword)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(validEmail, validPassword).addOnCompleteListener(activityRule.getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //Add user to users database
                        mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_FIRSTNAME).setValue(validFirstName);
                        mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_LASTNAME).setValue(validLastName);
                        mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_EMAIL).setValue(validEmail);
                        mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_PHONENUMBER).setValue(validPhoneNumber);
                        mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_PROFILE_PIC).setValue(validProfPicURL);
                    }

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                            child(Consts.USERS_DATABASE).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot child : dataSnapshot.getChildren())
                            {
                                switch (child.getKey())
                                {
                                    case Consts.USER_FIRSTNAME:
                                        Assert.assertEquals(child.getValue(), validFirstName);
                                        break;
                                    case Consts.USER_LASTNAME:
                                        Assert.assertEquals(child.getValue(), validLastName);
                                        break;
                                    case Consts.USER_PHONENUMBER:
                                        Assert.assertEquals(child.getValue(), validPhoneNumber);
                                        break;
                                    case Consts.USER_EMAIL:
                                        Assert.assertEquals(child.getValue(), validEmail);
                                        break;
                                    case Consts.USER_PROFILE_PIC:
                                        Assert.assertEquals(child.getValue(), Consts.USER_DEFAULT_PROFILE_PIC_URL);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });
                }
            });
        }
    }

    //white-box
    @Test
    public void validateUserWithIncorrectFields() {
        Intent intent = new Intent();
        intent.putExtra("test", true);
        activityRule.launchActivity(intent);
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(false, activityRule.getActivity()
                        .checkValues(invalidFirstName, invalidLastName, invalidPhoneNumber, invalidEmail, invalidPassword));
            }
        });
    }

    //black-box
    @Test
    public void registerWithInvalidInfo() {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.firstname_edittext)).perform(typeText(invalidFirstName));
        onView(withId(R.id.lastname_edittext)).perform(scrollTo()).perform(typeText(invalidLastName));
        onView(withId(R.id.phonenumber_edittext)).perform(scrollTo()).perform(typeText(invalidPhoneNumber));
        onView(withId(R.id.email_edittext)).perform(scrollTo()).perform(typeText(invalidEmail));
        onView(withId(R.id.password_edittext)).perform(scrollTo()).perform(typeText(invalidPassword));
        onView(withId(R.id.provider_switch)).perform(scrollTo()).perform(click());
        onView(withId(R.id.register_button)).perform(scrollTo()).perform(click());

        // check if the login button is available to login with now
        onView(withId(R.id.register_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    //black-box
    @Test
    public void registerWithValidInfo() throws InterruptedException {
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        onView(withId(R.id.firstname_edittext)).perform(typeText(validFirstName));
        onView(withId(R.id.lastname_edittext)).perform(scrollTo()).perform(typeText(validLastName));
        onView(withId(R.id.phonenumber_edittext)).perform(scrollTo()).perform(typeText(validPhoneNumber));
        onView(withId(R.id.email_edittext)).perform(scrollTo()).perform(typeText(validEmail));
        onView(withId(R.id.password_edittext)).perform(scrollTo()).perform(typeText(validPassword));
        onView(withId(R.id.provider_switch)).perform(scrollTo()).perform(click());
        onView(withId(R.id.register_button)).perform(scrollTo()).perform(click());

        onView(withId(R.id.search_button)).perform(scrollTo()).check(matches(isDisplayed()));

    }
}
