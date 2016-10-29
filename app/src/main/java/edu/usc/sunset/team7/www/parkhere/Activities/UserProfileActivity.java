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

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by Jonathan on 10/28/16.
 */

public class UserProfileActivity extends AppCompatActivity {

    @BindView(R.id.userProfileImage) ImageView profilePic;
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
            setValues();
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

    }

    private void setValues(){

    }
}
