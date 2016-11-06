package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Executor;

import edu.usc.sunset.team7.www.parkhere.Activities.RegisterActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.content.ContentValues.TAG;

/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivityTest {

    String validEmail = "wyatt@me.com";
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

    @Before
    public void setUp() {

    }

    @Test
    public void validateUserWithCorrectFields() {
        Intent intent = new Intent();
        intent.putExtra("test", true);
        activityRule.launchActivity(intent);
        if (activityRule.getActivity().checkValues(validFirstName, validLastName, validPhoneNumber, validEmail, validPassword)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(validEmail, validPassword).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
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

    @Test
    public void validateUserWithIncorrectFields() {

    }

    @Test
    public void readFromDatabase() {

    }



}
