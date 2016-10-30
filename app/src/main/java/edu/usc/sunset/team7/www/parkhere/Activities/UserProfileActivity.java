package edu.usc.sunset.team7.www.parkhere.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by Jonathan on 10/28/16.
 */

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_image) ImageView profilePic;
    @BindView(R.id.user_name_view) TextView userName;
    @BindView(R.id.user_rating_bar) RatingBar userRating;


    private String uid, name, rating, imageURL;

    private static final String TAG = "UserProfileActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_public_profile);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null && bundle.containsKey(Consts.USER_ID)){
            uid = (String)bundle.get(Consts.USER_ID);
            getValuesFromDatabase();
        } else{
            Log.d(TAG, "BUNDLE WAS EMPTY!");
        }

        userName.setText(name);

        DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
        userRating.setRating(Float.valueOf(oneDigit.format(rating)));

        Drawable drawable = userRating.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FFCC00"), PorterDuff.Mode.SRC_ATOP);

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
                            rating = child.getValue().toString();
                            break;
                    }
                }
                setValues();
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
                        for(DataSnapshot child : dataSnapshot.child(uid).getChildren()){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setValues(){
        if(name!=null && imageURL!=null && rating!=null){

        } else{
            Log.d(TAG, "MISSING VALUES FOR USER PROFILE = NULL");
        }
    }
}
