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

import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.LoginActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    String validEmail = "jonathzw@usc.edu";
    String validPassword = "jonathanwang!";

    String invalidEmail = "invalid@email.doesnotexist";

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    final Semaphore loginSemaphore = new Semaphore(0);

    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setUp() {
        mAuth = FirebaseAuth.getInstance();
        activityRule.launchActivity(new Intent());
    }

    @Test
    public void loginWithInvalidEmail() {
        Task <AuthResult> task = mAuth.signInWithEmailAndPassword(invalidEmail, validPassword);
        while(!task.isComplete()){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.assertTrue(!task.isSuccessful());
    }

    @Test
    public void loginWithValidEmail() {
        Task <AuthResult> task2 = mAuth.signInWithEmailAndPassword(validEmail, validPassword);

        while(!task2.isComplete()){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Assert.assertTrue(task2.isSuccessful());
    }

    @Test
    public void forgotPasswordWithValidEmail() {
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
        mAuth.sendPasswordResetEmail(invalidEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Assert.assertEquals(false, task.isSuccessful());
                    }
                });
    }
}