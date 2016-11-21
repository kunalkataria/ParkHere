package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

/**
 * Created by Wyatt-Kim on 11/20/16.
 */

public class EditParkingSpotActivity extends AppCompatActivity {

    @BindView(R.id.parking_spot_name_textinputlayout) TextInputLayout parkingNameTextInput;
    @BindView(R.id.parking_description_textinputlayout) TextInputLayout parkingDescriptionTextInput;

    @BindView(R.id.parking_spot_name_edittext) AppCompatEditText parkingNameEditText;
    @BindView(R.id.parking_description_edittext) AppCompatEditText parkingDescriptionEditText;

    ///Image
    @BindView(R.id.parking_image) ImageView parkingImageView;

    @BindView(R.id.handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.covered_button_control) SwitchCompat coveredSwitch;
    @BindView(R.id.compact_button_control) SwitchCompat compactSwitch;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ParkingSpot editParkingSpot;
    private Uri sourceImageUri = null;

    private String nameString;
    private String descriptionString;
    private boolean isHandicap;
    private boolean isCovered;
    private boolean isCompact;

    public static void startActivity(Context context, ParkingSpot parkingSpot) {
        Intent i = new Intent(context, EditParkingSpotActivity.class);
        i.putExtra(Consts.PARKING_SPOT_EDIT_EXTRA, parkingSpot);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parking_spot);
        ButterKnife.bind(this);

//        setSupportActionBar();
        if (getSupportActionBar() != null) {

        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        editParkingSpot = (ParkingSpot) getIntent().getSerializableExtra(Consts.PARKING_SPOT_EDIT_EXTRA);
        populateFields();
    }

    @OnClick(R.id.upload_parking_image_button)
    protected void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    //Method called when user selects a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Make sure the gallery Intent called this method
        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            sourceImageUri = data.getData();
            parkingImageView.setImageURI(sourceImageUri);
        }
    }

    @OnClick(R.id.upload_parking_spot_button)
    protected void updateParkingSpot() {
        saveValues();
        if (checkFields()) {

        }
    }

    private void populateFields() {
        parkingNameEditText.setText(editParkingSpot.getName());
        parkingDescriptionEditText.setText(editParkingSpot.getDescription());
        handicapSwitch.setChecked(editParkingSpot.isHandicap());
        coveredSwitch.setChecked(editParkingSpot.isCovered());
        compactSwitch.setChecked(editParkingSpot.isCompact());
        Picasso.with(this).load(editParkingSpot.getImageURL()).into(parkingImageView);
    }

    private void saveValues() {
        nameString = parkingNameEditText.getText().toString();
        descriptionString = parkingDescriptionEditText.getText().toString();
        isHandicap = handicapSwitch.isChecked();
        isCovered = coveredSwitch.isChecked();
        isCompact = compactSwitch.isChecked();
    }

    private boolean checkFields() {
        boolean isValid = true;
        if (nameString.isEmpty()) {
            isValid = false;
            parkingNameTextInput.setErrorEnabled(true);
            parkingNameTextInput.setError("Parking name must not be empty");
        } else {
            parkingNameTextInput.setErrorEnabled(false);
        }
        if (descriptionString.isEmpty()) {
            isValid = false;
            parkingDescriptionTextInput.setErrorEnabled(true);
            parkingDescriptionTextInput.setError("Parking description must not be empty");
        } else {
            parkingNameTextInput.setErrorEnabled(false);
        }

        return isValid;
    }
}
