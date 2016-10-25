package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by kunal on 10/23/16.
 */

public class PostListingActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.name_textinputlayout)
    TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.description_textinputlayout)
    TextInputLayout desriptionTextInputLayout;
    @BindView(R.id.priceEditText)
    EditText priceEditText;

    //Cancellation controls
    @BindView(R.id.cancellation_textView)
    TextView cancellation;
    @BindView(R.id.myRadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.flexible_rButton)
    RadioButton flexibleRButton;
    @BindView(R.id.moderate_rButton)
    RadioButton moderateRButton;
    @BindView(R.id.strict_rButton)
    RadioButton strictRButton;
    @BindView(R.id.superstrict30_rButton)
    RadioButton superStrict30RButton;
    @BindView(R.id.superstrict60_rButton)
    RadioButton superStrict60RButton;
    @BindView(R.id.longTerm_rButton)
    RadioButton longTermRButton;

    //Parking image controls
    @BindView(R.id.upload_parking_button)
    Button uploadParkingImageButton;
    @BindView(R.id.uploadImage)
    ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control)
    SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control)
    SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control)
    SwitchCompat coveredSwitch;
    @BindView(R.id.truck_button_control)
    SwitchCompat truckSwitch;
    @BindView(R.id.suv_button_control)
    SwitchCompat suvSwitch;

    @BindView(R.id.upload_listing_button)
    Button uploadListingButton;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostListingActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);
        parkingImageView.setImageResource(R.mipmap.empty_parking);
    }



    
}
