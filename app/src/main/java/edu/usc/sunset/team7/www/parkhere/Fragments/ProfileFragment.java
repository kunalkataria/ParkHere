package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static final String TAG = "UserProfileFragment***";


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


        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference(Consts.REVIEWS_DATABASE);
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){

                    if(dataSnapshot.child(uid).hasChildren()){
                        int ratings = -1;
                        int counter = 0;
                        String description = null;
                        for(DataSnapshot child : dataSnapshot.child(uid).getChildren()){
                            for (DataSnapshot child2 : child.getChildren()){
                                switch (child2.getKey()) {
                                    case Consts.REVIEW_DESCRIPTION:
                                        description = child2.getValue().toString();
                                        break;
                                    case Consts.REVIEW_RATING:
                                        ratings = Integer.parseInt(child2.getValue().toString());
                                        rating += (double)ratings;
                                        counter++;
                                        break;
                                }
                            }
                            Log.d(TAG, "Description: " + description);
                            Log.d(TAG, "a Rating value: " + Integer.toString(ratings));
                            if(ratings !=-1 && description != null){
                                Log.d(TAG, "Created review");
                                Review r = new Review(ratings, description);
                                reviews.add(r);

                            }
                        }
                        rating = rating/(double) counter;
                        Log.d(TAG, Double.toString(rating));
                        Log.d(TAG, "Reviews Size: " + Integer.toString(reviews.size()));
                    }
                }
                setValues();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


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

            if (reviews != null || reviews.size() > 0) {
                reviewContentSpace.removeAllViewsInLayout();
                ListViewCompat listView = new ListViewCompat(getActivity());
                listView.setAdapter(new CustomReviewAdapter(getActivity(), reviews));
                reviewContentSpace.addView(listView);
            }
        } else{
            Log.d(TAG, "MISSING VALUES FOR USER PROFILE = NULL");
        }
    }
}
