package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

import edu.usc.sunset.team7.www.parkhere.Activities.UserProfileActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Justin on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfileActivityTest {

    private String mFirstName, mUId;
    private float mRating;

    @Rule
    public ActivityTestRule<UserProfileActivity> activityRule =
            new ActivityTestRule<>(UserProfileActivity.class, true, false);

    @Before
    public void initializeActivity(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("kunal@me.com", "hello12345");

        final Semaphore loginSemaphore = new Semaphore(0);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i("TESTING LOG", "NOT LOGGED In");
                } else {
                    loginSemaphore.release();
                    mUId = firebaseAuth.getCurrentUser().getUid();
                }
            }
        });
        try {
            loginSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }



        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE);
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mUId)){
                    if(dataSnapshot.child(mUId).hasChildren()){
                        int ratings = -1;
                        int counter = 0;
                        for(DataSnapshot child : dataSnapshot.child(mUId).getChildren()){
                            switch (child.getKey()) {
                                case Consts.REVIEW_RATING:
                                    counter++;
                                    ratings = (int)child.getValue();
                                    break;
                            }
                        }
                        if(counter != 0){
                            DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
                            mRating = Float.valueOf(oneDigit
                                    .format(((double)ratings/(double)counter)));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Test
    public void populateActivity(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(Consts.USER_ID, mUId);
        intent.putExtras(bundle);
        activityRule.launchActivity(intent);

        TextView textView = (TextView) activityRule.getActivity()
                .findViewById(R.id.user_name_view);
        RatingBar ratingBar = (RatingBar) activityRule.getActivity()
                .findViewById(R.id.user_rating_bar);


        final String textString = textView.getText().toString();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE)
                .child(mUId).child(Consts.USER_FIRSTNAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFirstName = dataSnapshot.getValue().toString();
                Assert.assertEquals(mFirstName, textString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //Test for correct rating
        Assert.assertEquals(true, ratingBar.getRating() == mRating);

    }

}
