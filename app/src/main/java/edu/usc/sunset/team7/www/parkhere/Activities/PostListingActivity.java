package edu.usc.sunset.team7.www.parkhere.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

/**
 * Created by kunal on 10/23/16.
 */

public class PostListingActivity extends AppCompatActivity {

    private static final String TAG = "PostListingActivity";

    @BindView(R.id.name_textinputlayout) TextInputLayout listingNameTextInput;
    @BindView(R.id.description_textinputlayout) TextInputLayout descriptionTextInputLayout;
    @BindView(R.id.price_textinputlayout) TextInputLayout priceTextInputLayout;
    @BindView(R.id.parkingspot_textinputlayout) TextInputLayout parkingSpotTextInput;

    @BindView(R.id.name_edittext) AppCompatEditText parkingNameEditText;
    @BindView(R.id.description_edittext) AppCompatEditText descriptionEditText;
    @BindView(R.id.price_edittext) AppCompatEditText priceEditText;
    @BindView(R.id.parkingspot_edittext) AppCompatEditText parkingSpotEditText;

    private String nameString, descriptionString;
    private double price;

    @BindView(R.id.myRadioGroup) RadioGroup radioGroup;
    @BindView(R.id.refundable_rButton) RadioButton refundableRButton;
    @BindView(R.id.nonrefundable_rButton) RadioButton nonrefundableRButton;

    private static Hashtable<Integer, String> cancellationIds;

    //Parking image controls
    @BindView(R.id.upload_parking_button) Button uploadParkingImageButton;

    @BindView(R.id.parkingImage) ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control) SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control) SwitchCompat coveredSwitch;

    // Date selector pieces
    @BindView(R.id.start_date_inputlayout) TextInputLayout startDateLayout;
    @BindView(R.id.stop_date_inputlayout) TextInputLayout stopDateLayout;
    @BindView(R.id.start_time_edittext) AppCompatEditText startDateEditText;
    @BindView(R.id.stop_time_edittext) AppCompatEditText stopDateEditText;

    @BindView(R.id.post_listing_toolbar) Toolbar postListingToolbar;

    private ParkingSpot currentParkingSpot;

    private DatePickerDialog startDatePicker;
    private DatePickerDialog stopDatePicker;

    private TimePickerDialog startTimePicker;
    private TimePickerDialog stopTimePicker;

    private int startYear, startMonth, startDay, startHour, startMinute;

    private int stopYear, stopMonth, stopDay, stopHour, stopMinute;

    private long startDate, stopDate;

    boolean isRefundable;

    @BindView(R.id.upload_listing_button) Button uploadListingButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostListingActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);
        ButterKnife.bind(this);

        setSupportActionBar(postListingToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post a Listing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        parkingImageView.setImageResource(R.mipmap.empty_parking);

        nameString = "";
        descriptionString = "";
        price = 0.0;
        startDate = 0;
        stopDate = 0;
        currentParkingSpot = null;

        cancellationIds = new Hashtable<Integer, String>();
        cancellationIds.put(R.id.refundable_rButton, Consts.REFUNDABLE);
        cancellationIds.put(R.id.nonrefundable_rButton, Consts.NONREFUNDABLE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

    }

    // Method called when user selects a parking space
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Consts.PARKING_SPOT_REQUEST
                && resultCode == Consts.PARKING_SPOT_SUCCESSFUL_RESULT && data != null ) {
            currentParkingSpot = (ParkingSpot) data.getSerializableExtra(Consts.PARKING_SPOT_EXTRA);
            parkingSpotEditText.setText(currentParkingSpot.getName());
        }
    }

    @OnClick (R.id.parkingspot_edittext)
    protected void selectParkingSpot() {
        MyParkingSpacesActivity.startActivityForResult(Consts.PARKING_SPOT_REQUEST, PostListingActivity.this);
    }

    @OnClick(R.id.upload_listing_button)
    protected void submitListing() {
        if(checkFields()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();
            String listingID = mDatabase.child(Consts.LISTINGS_DATABASE).push().getKey();
            final DatabaseReference newListingRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(uid).child(Consts.ACTIVE_LISTINGS).child(listingID);
            newListingRef.child(Consts.LISTING_NAME).setValue(nameString);
            newListingRef.child(Consts.LISTING_DESCRIPTION).setValue(descriptionString);
            newListingRef.child(Consts.LISTING_REFUNDABLE).setValue(isRefundable);
            newListingRef.child(Consts.LISTING_PRICE).setValue(price);
            newListingRef.child(Consts.LISTING_START_TIME).setValue(startDate);
            newListingRef.child(Consts.LISTING_END_TIME).setValue(stopDate);
            newListingRef.child(Consts.PARKING_SPOTS_ID).setValue("");
            finish();
        }
    }

    @OnClick(R.id.start_time_edittext)
    protected void selectStartTime() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                startHour = hourOfDay;
                startMinute = minute;
                DateTime dateTime = new DateTime(startYear, startMonth, startDay, startHour, startMinute);
                startDate = dateTime.getMillis() / 1000;
                startDateEditText.setText(Tools.getDateString(dateTime));
            }
        };
        startTimePicker = new TimePickerDialog(this, startTimeListener, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
        DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                startYear = year;
                startMonth = month + 1;
                startDay = day;
                startTimePicker.show();
            }
        };
        startDatePicker = new DatePickerDialog
                (this, startDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        startDatePicker.show();
    }

    @OnClick(R.id.stop_time_edittext)
    protected void selectStopTime() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener stopTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                stopHour = hourOfDay;
                stopMinute = minute;
                DateTime dateTime = new DateTime(stopYear, stopMonth, stopDay, stopHour, stopMinute);
                stopDate = dateTime.getMillis() / 1000;
                stopDateEditText.setText(Tools.getDateString(dateTime));
            }
        };
        stopTimePicker = new TimePickerDialog(this, stopTimeListener, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
        DatePickerDialog.OnDateSetListener stopDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                stopYear = year;
                stopMonth = month + 1;
                stopDay = day;
                stopTimePicker.show();
            }
        };
        stopDatePicker = new DatePickerDialog
                (this, stopDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        stopDatePicker.show();
    }


    public boolean checkFields() {
        nameString = parkingNameEditText.getText().toString();
        descriptionString = descriptionEditText.getText().toString();
        boolean isValid = true;
        try {
            price = Double.parseDouble(priceEditText.getText().toString());
            if (price > 0) {
                priceTextInputLayout.setErrorEnabled(false);
            } else {
                isValid = false;
                priceTextInputLayout.setErrorEnabled(true);
                priceTextInputLayout.setError("Not a valid price");
            }
        } catch (NumberFormatException nfe) {
            isValid = false;
            priceTextInputLayout.setErrorEnabled(true);
            priceTextInputLayout.setError("Not a valid price");
        }

        if (nameString.isEmpty()) {
            isValid = false;
            listingNameTextInput.setErrorEnabled(true);
            listingNameTextInput.setError("Please enter a name.");
        } else {
            listingNameTextInput.setErrorEnabled(false);
        }
        if (descriptionString.isEmpty()) {
            isValid = false;
            descriptionTextInputLayout.setErrorEnabled(true);
            descriptionTextInputLayout.setError("Please enter a description.");
        } else {
            descriptionTextInputLayout.setErrorEnabled(false);
        }
        if (currentParkingSpot == null) {
            isValid = false;
            parkingSpotTextInput.setErrorEnabled(true);
            parkingSpotTextInput.setError("Select a parking space");
        } else {
            parkingSpotTextInput.setErrorEnabled(false);
        }
        if (price <= 0){
            isValid = false;
            priceTextInputLayout.setErrorEnabled(true);
            priceTextInputLayout.setError("Please enter a price greater than $0");
        } else {
            priceTextInputLayout.setErrorEnabled(false);
        }
        if (startDate == 0) {
            isValid = false;
            startDateLayout.setError("Please select a start date");
            startDateLayout.setErrorEnabled(true);
        } else {
            startDateLayout.setErrorEnabled(false);
        }
        if (stopDate == 0) {
            isValid = false;
            stopDateLayout.setError("Please select a stop date");
            stopDateLayout.setErrorEnabled(true);
        } else {
            stopDateLayout.setErrorEnabled(false);
        }
        if (startDate != 0 && stopDate != 0) {
            if (startDate >= stopDate) {
                isValid = false;
                startDateLayout.setError("Please select a valid start date");
                startDateLayout.setErrorEnabled(true);
                stopDateLayout.setError("Please select a valid stop date");
                stopDateLayout.setErrorEnabled(true);
            }
        }
        return isValid;
    }

    public void fillInFields(Listing listing) {
        parkingNameEditText.setText(listing.getName());
        descriptionEditText.setText(listing.getDescription());
        priceEditText.setText(Double.toString(listing.getPrice()));
        refundableRButton.toggle();
        if (listing.isRefundable()) {
            radioGroup.check(R.id.refundable_rButton);
        } else {
            radioGroup.check(R.id.nonrefundable_rButton);
        }
        compactSwitch.setChecked(listing.isCompact()); 
        coveredSwitch.setChecked(listing.isCovered());
        handicapSwitch.setChecked(listing.isHandicap());
        startDate = listing.getStartTime();
        stopDate = listing.getStopTime();
    }
}