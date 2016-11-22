package edu.usc.sunset.team7.www.parkhere.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomReviewAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Review;

/**
 * Created by Jonathan on 10/28/16.
 */

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.userProfileImage) ImageView profilePic;
    @BindView(R.id.user_name_view) TextView userName;
    @BindView(R.id.user_rating_bar) RatingBar userRating;
    @BindView(R.id.review_content_space) LinearLayout reviewContentSpace;
    @BindView(R.id.public_profile_toolbar) Toolbar publicProfileToolbar;

    private String uid, name, imageURL;
    private double rating = -1;
    private List<Review> reviews;

    private static final String TAG = "UserProfileActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_public_profile);
        ButterKnife.bind(this);

        setSupportActionBar(publicProfileToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Public Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null && bundle.containsKey(Consts.USER_ID)) {
            uid = (String)bundle.get(Consts.USER_ID);
            getValuesFromDatabase();
        } else {
            Log.d(TAG, "BUNDLE WAS EMPTY!");
        }
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
                            break;
                        case Consts.USER_PROFILE_PIC:
                            imageURL = child.getValue().toString();
                            break;
                        case Consts.USER_RATING:
                            rating = Double.parseDouble(child.getValue().toString());
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
                            Log.d(TAG, "Image did not download");
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
                        String description = null;
                        for(DataSnapshot child : dataSnapshot.child(uid).getChildren()){
                            switch (child.getKey()) {
                                case Consts.REVIEW_DESCRIPTION:
                                    description = child.getValue().toString();
                                    Log.d(TAG, description);
                                    break;
                                case Consts.REVIEW_RATING:
                                    ratings = (int)child.getValue();
                                    break;
                            }
                        }
                        if(ratings !=-1 && description!=null){
                            Review r = new Review(ratings, description);
                            reviews.add(r);
                        }
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

            if (imageURL != null) {
                Picasso.with(this).load(imageURL).into(profilePic);
            }

            DecimalFormat oneDigit = new DecimalFormat("#,##0.0");

            Drawable drawable = userRating.getProgressDrawable();
            drawable.setColorFilter(Color.parseColor("#FFCC00"), PorterDuff.Mode.SRC_ATOP);
            userRating.setRating(Float.valueOf(oneDigit.format(rating)));

            if (reviews != null) {
                reviewContentSpace.removeAllViewsInLayout();
                ListViewCompat listView = new ListViewCompat(this);
                listView.setAdapter(new CustomReviewAdapter(this, reviews));
                reviewContentSpace.addView(listView);
            }
        } else{
            Log.d(TAG, "MISSING VALUES FOR USER PROFILE = NULL");
        }
    }
}
