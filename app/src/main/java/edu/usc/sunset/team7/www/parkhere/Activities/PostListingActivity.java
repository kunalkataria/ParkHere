package edu.usc.sunset.team7.www.parkhere.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.joda.time.DateTime;

import java.util.ArrayList;
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

    // Date selector pieces
    @BindView(R.id.start_date_inputlayout) TextInputLayout startDateLayout;
//    @BindView(R.id.stop_date_inputlayout) TextInputLayout stopDateLayout;
    @BindView(R.id.start_time_edittext) AppCompatEditText startDateEditText;
//    @BindView(R.id.stop_time_edittext) AppCompatEditText stopDateEditText;

    @BindView(R.id.post_listing_toolbar) Toolbar postListingToolbar;

    private ParkingSpot currentParkingSpot;

    private DatePickerDialog startDatePicker;
//    private DatePickerDialog stopDatePicker;

    private TimePickerDialog startTimePicker;
//    private TimePickerDialog stopTimePicker;

    private int startYear, startMonth, startDay, startHour, startMinute;

//    private int stopYear, stopMonth, stopDay, stopHour, stopMinute;

    private long startDate, stopDate;

    //Time Increments + Number of increments

    @BindView(R.id.time_increments_spinner) Spinner timeIncrementsSpinner;
    @BindView(R.id.num_increments_textinputlayout) TextInputLayout numIncrementsLayout;
    @BindView(R.id.num_increments_edittext) AppCompatEditText numIncrementsEditText;

    private int timeIncrements, numIncrements;

    boolean isRefundable;

    @BindView(R.id.upload_listing_button) Button uploadListingButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostListingActivity.class);
        context.startActivity(i);
        Log.i("Activity started", "Time: " + System.currentTimeMillis());
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
        final String userID = mAuth.getCurrentUser().getUid();

        nameString = "";
        descriptionString = "";
        price = 0.0;
        startDate = 0;
        stopDate = 0;

        final ArrayList<ParkingSpot> userSpots = new ArrayList<ParkingSpot>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Consts.PARKING_SPOT_DATABASE).child(userID);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    boolean isActive = Boolean.parseBoolean(child.child(Consts.PARKING_SPOTS_ACTIVE)
                            .getValue().toString());
                    if (isActive) {
                        ParkingSpot currentParkingSpot = parseParkingSpot(child);
                        currentParkingSpot.setProviderID(userID);
                        userSpots.add(currentParkingSpot);
                    }
                }

                if (userSpots.size() == 1) {
                    currentParkingSpot = userSpots.get(0);
                    parkingSpotEditText.setText(currentParkingSpot.getName());
                    Log.i("Parking spot selected", "Time: " + System.currentTimeMillis());

                } else {
                    currentParkingSpot = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        cancellationIds = new Hashtable<Integer, String>();
        cancellationIds.put(R.id.refundable_rButton, Consts.REFUNDABLE);
        cancellationIds.put(R.id.nonrefundable_rButton, Consts.NONREFUNDABLE);

        //Time Increments Initialization
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_increment_spinner_label, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeIncrementsSpinner.setAdapter(adapter);
        timeIncrements = 1;
        numIncrements = 0;
        timeIncrementsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                timeIncrements = Integer.parseInt(adapterView.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        Log.i("Parking spot selected", "Time: " + System.currentTimeMillis());
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
            newListingRef.child(Consts.LISTING_CURRENT_ACTIVE).setValue(true);

            //Calculate StopDate
            stopDate = startDate + (numIncrements * timeIncrements) * 60 * 60;

            newListingRef.child(Consts.LISTING_END_TIME).setValue(stopDate);
            newListingRef.child(Consts.PARKING_SPOTS_ID).setValue(currentParkingSpot.getParkingSpotID());

            //Time Increments
            StringBuilder sb = new StringBuilder();
            sb.append("0");
            for (int i = 1; i < numIncrements; i ++){
                sb.append(",");
                sb.append(Integer.toString(i));
            }
            newListingRef.child(Consts.LISTING_ACTIVE_TIMES).setValue(sb.toString());
            newListingRef.child(Consts.LISTING_BOOK_TIME).setValue(timeIncrements);
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

//    @OnClick(R.id.stop_time_edittext)
//    protected void selectStopTime() {
//        Calendar c = Calendar.getInstance();
//        TimePickerDialog.OnTimeSetListener stopTimeListener = new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
//                stopHour = hourOfDay;
//                stopMinute = minute;
//                DateTime dateTime = new DateTime(stopYear, stopMonth, stopDay, stopHour, stopMinute);
//                stopDate = dateTime.getMillis() / 1000;
//                stopDateEditText.setText(Tools.getDateString(dateTime));
//            }
//        };
//        stopTimePicker = new TimePickerDialog(this, stopTimeListener, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
//        DatePickerDialog.OnDateSetListener stopDateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                stopYear = year;
//                stopMonth = month + 1;
//                stopDay = day;
//                stopTimePicker.show();
//            }
//        };
//        stopDatePicker = new DatePickerDialog
//                (this, stopDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        stopDatePicker.show();
//    }


    public boolean checkFields() {
        nameString = parkingNameEditText.getText().toString();
        descriptionString = descriptionEditText.getText().toString();
        numIncrements = Integer.parseInt(numIncrementsEditText.getText().toString());
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
        if (!refundableRButton.isChecked() && !nonrefundableRButton.isChecked()) {
            isValid = false;
            Toast.makeText(PostListingActivity.this, "Please select a refund policy.",
                    Toast.LENGTH_SHORT).show();
        } else {
            isRefundable = refundableRButton.isChecked();
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
//        if (stopDate == 0) {
//            isValid = false;
//            stopDateLayout.setError("Please select a stop date");
//            stopDateLayout.setErrorEnabled(true);
//        } else {
//            stopDateLayout.setErrorEnabled(false);
//        }
//        if (startDate != 0 && stopDate != 0) {
//            if (startDate >= stopDate) {
//                isValid = false;
//                startDateLayout.setError("Please select a valid start date");
//                startDateLayout.setErrorEnabled(true);
//                stopDateLayout.setError("Please select a valid stop date");
//                stopDateLayout.setErrorEnabled(true);
//            }
//        }
        //Time Increments
        if (numIncrements == 0){
            isValid = false;
            numIncrementsLayout.setError("Please enter the number of time increments");
            numIncrementsLayout.setErrorEnabled(true);
        } else {
            numIncrementsLayout.setErrorEnabled(false);
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
        startDate = listing.getStartTime();
        stopDate = listing.getStopTime();
        //need to account for time increments
    }

    public ParkingSpot parseParkingSpot(DataSnapshot parkingSnapShot) {
        ParkingSpot pSpot = new ParkingSpot();
        //dont knw if we need this but insertedt the id as well
        pSpot.setParkingSpotID(parkingSnapShot.getKey());
        for(DataSnapshot child : parkingSnapShot.getChildren()) {
            switch (child.getKey()) {
                case Consts.PARKING_SPOTS_COMPACT:
                    pSpot.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_COVERED:
                    pSpot.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_HANDICAP:
                    pSpot.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LONGITUDE:
                    pSpot.setLongitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_LATITUDE:
                    pSpot.setLatitude(Double.parseDouble(child.getValue().toString()));
                    break;
                case Consts.PARKING_SPOTS_IMAGE:
                    pSpot.setImageURL(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_NAME:
                    pSpot.setName(child.getValue().toString());
                    break;
                case Consts.PARKING_SPOTS_BOOKING_COUNT:
                    pSpot.setBookingCount(Integer.parseInt(child.getValue().toString()));
                    break;
            }
        }
        return pSpot;
    }
}