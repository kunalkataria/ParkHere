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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.usc.sunset.team7.www.parkhere.Activities.LoginActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    String validEmail = "jonathzw@usc.edu";
    String validPassword = "jonathanwang!";

    String invalidEmail = "invalid@email.doesnotexist";

    FirebaseAuth mAuth;


    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setUp() {

        mAuth = FirebaseAuth.getInstance();

    }

    @Test
    public void loginWithValidInformation() {
        mAuth.signInWithEmailAndPassword(validEmail, validPassword)
                .addOnCompleteListener(activityRule.launchActivity(new Intent()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Assert.assertEquals(true, task.isSuccessful());
                    }
                });
    }

    @Test
    public void loginWithInvalidEmail() {
        mAuth.signInWithEmailAndPassword(invalidEmail, validPassword)
                .addOnCompleteListener(activityRule.launchActivity(new Intent()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Assert.assertEquals(true, !task.isSuccessful());
                    }
                });
    }

    @Test
    public void forgotPasswordWithValidEmail() {
        activityRule.launchActivity(new Intent());
        mAuth.sendPasswordResetEmail(validEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Assert.assertEquals(true, task.isSuccessful());
                    }
                });
    }

    @Test
    public void forgotPasswordWithInvalidEmail() {
        activityRule.launchActivity(new Intent());
        mAuth.sendPasswordResetEmail(invalidEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Assert.assertEquals(false, task.isSuccessful());
                    }
                });
    }
}