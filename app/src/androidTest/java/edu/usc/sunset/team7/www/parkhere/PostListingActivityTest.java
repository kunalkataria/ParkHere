package edu.usc.sunset.team7.www.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.usc.sunset.team7.www.parkhere.Activities.PostListingActivity;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;


/**
 * Created by Acer on 11/5/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostListingActivityTest {
    private String currentUID;

    private String validName, validDescription;
    private double validPrice;
    private long validStartDate;
    private long validStopDate;
    private boolean isCompact,isHandicap, isCovered, isRefundable;
    private double validLatitude, validLongitude;

    @Rule
    public ActivityTestRule<PostListingActivity> activityRule =
            new ActivityTestRule<>(PostListingActivity.class, true, false);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("tester@test.me", "hello12345!");

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i("TESTING LOG", "NOT LOGGED In");
                } else {
                    currentUID = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference balanceRef = FirebaseDatabase.getInstance().getReference()
                            .child(Consts.USERS_DATABASE).child(currentUID);

                }
            }
        });

        validName = "Lorenzo Spot";
        validDescription = "Spacious, covered spot!";
        validPrice = 34.30;
        validStartDate = 1502114981L;
        validStopDate = 1504793381L;
        isCompact = false;
        isHandicap = false;
        isCovered = true;
        isRefundable = true;
        validLatitude = 34.027458;
        validLongitude = -118.272957;
    }

    @Test
    public void validateUserWithCorrectFields() {
        activityRule.launchActivity(new Intent());
        if (checkValues(validName, validDescription, validPrice, validStartDate, validStopDate, validLatitude, validLongitude)) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//            String uid = currentUser.getUid();
//            String listingID = mDatabase.child(Consts.LISTINGS_DATABASE).push().getKey();
//            final DatabaseReference newListingRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(uid).child(Consts.ACTIVE_LISTINGS).child(listingID);
//            newListingRef.child(Consts.LISTING_NAME).setValue(nameString);
//            newListingRef.child(Consts.LISTING_DESCRIPTION).setValue(descriptionString);
//            newListingRef.child(Consts.LISTING_REFUNDABLE).setValue(isRefundable);
//            newListingRef.child(Consts.LISTING_PRICE).setValue(price);
//            newListingRef.child(Consts.LISTING_COMPACT).setValue(isCompact);
//            newListingRef.child(Consts.LISTING_COVERED).setValue(isCovered);
//            newListingRef.child(Consts.LISTING_HANDICAP).setValue(isHandicap);
//            newListingRef.child(Consts.LISTING_LATITUDE).setValue(latitude);
//            newListingRef.child(Consts.LISTING_LONGITUDE).setValue(longitude);
//            newListingRef.child(Consts.LISTING_START_TIME).setValue(startDate);
//            newListingRef.child(Consts.LISTING_END_TIME).setValue(stopDate);
//
//            StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
//            StorageReference parkingRef = storageRef.child(Consts.STORAGE_PARKING_SPACES);
//            //Best way to store the data?
//            UploadTask uploadTask = parkingRef.child(listingID).putFile(sourceImageUri);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Log.d(TAG, exception.toString());
//                    Toast.makeText(PostListingActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                    firebaseImageURL = taskSnapshot.getDownloadUrl().toString();
//                    newListingRef.child(Consts.LISTING_IMAGE).setValue(firebaseImageURL);
//                }
//            });
//            finish();
        }

    }

    @Test
    public void validateUserWithIncorrectFields() {
        activityRule.launchActivity(new Intent());

    }

    @Test
    public void validateStartAndStopTime(){
        /*
        We don't have a check for if the start and stop times are valid (as in the start comes before the start time)
        Also, if the stop time is before the current time, the user should not be allowed to post the listing
        We can say this is a bug, and that we added some more checks to deal with this (before we only checked if a start and stop was selected.)
        */

    }

    private boolean checkValues(String nameString, String descriptionString, double price, long startDate, long stopDate, double latitude, double longitude){
        boolean isValid = true;

        if (nameString.equals("")) {
            isValid = false;
        }
        if (descriptionString.equals("")){
            isValid = false;
        }
        if (price <= 0){
            isValid = false;
        }
        if (startDate == 0) {
            isValid = false;
        }
        if (stopDate == 0) {
            isValid = false;
        }
        if (startDate != 0 && stopDate != 0) {
            long currentTime = System.currentTimeMillis() / 1000L;
            if (startDate >= stopDate || stopDate <= currentTime || startDate <= currentTime) {
                isValid = false;
            }
        }
        if (latitude == -1 && longitude == -1) {
            isValid = false;
        }
        return isValid;
    }
}
