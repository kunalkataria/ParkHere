package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomReviewAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Justin on 10/31/2016.
 */

public class ProfileFragment extends Fragment{

    @BindView(R.id.userProfileImage_fragment)
    ImageView profilePic;
    @BindView(R.id.user_name_view_fragment)
    TextView userName;
    @BindView(R.id.user_rating_bar_fragment)
    RatingBar userRating;
    @BindView(R.id.review_content_space_fragment)
    LinearLayout reviewContentSpace;

    private String uid, name, imageURL;
    private double rating = -1;
    private ArrayList<Review> reviews = new ArrayList<Review>();

    private static final String TAG = "ProfileFragment*****";


    @Override
    public void onCreate(Bundle savedBundleInstance){

        super.onCreate(savedBundleInstance);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, uid);
        getValuesFromDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    private void getValuesFromDatabase(){
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(Consts.USERS_DATABASE).child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()){
                        case Consts.USER_FIRSTNAME:
                            name = child.getValue().toString();
                            Log.d(TAG, "name: " + name);
                            break;
                        case Consts.USER_PROFILE_PIC:
                            imageURL = child.getValue().toString();
                            break;
                        case Consts.USER_RATING:
                            rating = Double.parseDouble(child.getValue().toString());
                            Log.d(TAG, "Rating: " + Double.toString(rating));
                            break;
                    }
                }
                if (imageURL == null){
                    StorageReference storage = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(Consts.STORAGE_URL)
                            .child(Consts.STORAGE_PROFILE_PICTURES).child(uid);
                    final long ONE_MEGABYTE = 5 * 1024 * 1024;
                    storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(
                            new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    profilePic.setImageBitmap(bmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        //Get Reviews
        //ReviewsDB -> UID -> ParkingSpotID -> Booking ID -> Review Data
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE).child(uid);
        if(reviewsRef!=null){ //User has reviews
            reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChildren()){
                        //Getting all Parking Spot IDs
                        for(DataSnapshot parkingSpotID : dataSnapshot.getChildren()){
                            //Getting the Booking ID
                            for(DataSnapshot bookingID : parkingSpotID.getChildren()){
                                int ratings = -1;
                                String description = null;
                                //Get the review data
                                for(DataSnapshot review : bookingID.getChildren()){
                                    switch (review.getKey()) {
                                        case Consts.REVIEW_DESCRIPTION:
                                            description = review.getValue().toString();
                                            Log.d(TAG, description);
                                            break;
                                        case Consts.REVIEW_RATING:
                                            ratings = Integer.parseInt(review.getValue().toString());
                                            break;
                                    }
                                }
                                if(ratings !=-1 && description!=null){
                                    Review r = new Review(ratings, description);
                                    reviews.add(r);
                                } else {
                                    Log.d(TAG, "RATING WAS NOT ADDED");
                                }
                            }
                        }
                    }
                    setValues();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void setValues(){
        if(name!=null && rating!=-1){
            userName.setText(name);
            //profilePic.setImageURI(Uri.parse(imageURL));

            if (imageURL != null) {
                Picasso.with(getActivity()).load(imageURL).into(profilePic);
            }

            DecimalFormat oneDigit = new DecimalFormat("#,##0.0");

            Drawable drawable = userRating.getProgressDrawable();
            drawable.setColorFilter(Color.parseColor("#FFCC00"), PorterDuff.Mode.SRC_ATOP);
            userRating.setRating(Float.valueOf(oneDigit.format(rating)));

            if (reviews.size() > 0) {
                reviewContentSpace.removeAllViewsInLayout();
                ListView listView = new ListView(getActivity());
                listView.setAdapter(new CustomReviewAdapter(getActivity(), reviews));
                reviewContentSpace.addView(listView);

                int reviewSum = 0;
                for(Review r : reviews) {
                    reviewSum += r.getReviewRating();
                }
                userRating.setRating(Float.valueOf(oneDigit.format(reviewSum/reviews.size())));
            }
        } else{
            Log.d(TAG, "MISSING VALUES FOR USER PROFILE = NULL");
        }
    }
}
