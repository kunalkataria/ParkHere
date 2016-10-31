package edu.usc.sunset.team7.www.parkhere.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    @BindView(R.id.review_list_space) LinearLayout reviewListSpace;

    private String uid, name, rating, imageURL;
    private List<Review> userReviews;

    private static final String TAG = "UserProfileActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_public_profile);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && bundle.containsKey(Consts.USER_ID)){
            uid = (String)bundle.get(Consts.USER_ID);
            getValuesFromDatabase();
            setValues();
        } else{
            Log.d(TAG, "BUNDLE WAS EMPTY!");
        }

    }

    private void getValuesFromDatabase(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    switch (child.getKey()){
                        case "First Name":
                            name = child.getValue().toString();
                            break;
                        case "Rating":
                            rating = child.getValue().toString();
                            break;
                        case "Profile Picture URL":
                            imageURL = child.getValue().toString();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Reviews/" + uid);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    for (DataSnapshot child2 : child.getChildren()){
                        String aRating = "0";
                        String aReview = "";
                        switch (child2.getKey()){
                            case "Rating":
                                aRating = child2.getValue().toString();
                                break;
                            case "Review":
                                aReview = child2.getValue().toString();
                                break;
                        }
                        Review theReview = new Review(Integer.parseInt(aRating), aReview);
                        userReviews.add(theReview);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setValues(){

        userName.setText(name);

        DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
        userRating.setRating(Float.valueOf(oneDigit.format(rating)));

        Drawable drawable = userRating.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFCC00"), PorterDuff.Mode.SRC_ATOP);

        Picasso.with(this).load(imageURL).into(profilePic);

        reviewListSpace.removeAllViewsInLayout();
        ListViewCompat listView = new ListViewCompat(this);
        listView.setAdapter(new CustomReviewAdapter(this, userReviews));
        reviewListSpace.addView(listView);
    }
}
