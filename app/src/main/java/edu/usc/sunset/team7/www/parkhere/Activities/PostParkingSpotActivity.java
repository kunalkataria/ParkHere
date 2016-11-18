package edu.usc.sunset.team7.www.parkhere.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

public class PostParkingSpotActivity extends AppCompatActivity {

    private static final String TAG = "PostListingActivity";

    @BindView(R.id.parking_spot_name_textinputlayout) TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.parking_spot_name_edittext)AppCompatEditText parkingNameEditText;
    private String nameString;

    //Parking image controls
    @BindView(R.id.upload_parking_spot_picture_button) Button uploadParkingImageButton;
    @BindView(R.id.parking_spot_Image) ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.parking_spot_handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.parking_spot_compact_button_control) SwitchCompat compactSwitch;
    @BindView(R.id.parking_spot_covered_button_control) SwitchCompat coveredSwitch;

    boolean isCompact,isHandicap, isCovered, isRefundable;

    @BindView(R.id.post_parking_spot_toolbar) Toolbar postParkingSpotToolbar;

    @BindView(R.id.upload_parking_spot_button) Button uploadParkingSpotButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private Uri sourceImageUri = null;
    private String firebaseImageURL = "";

    //NEED TO GET LONGITUDE AND LATITUDE
    private double longitude, latitude;

    private Place locationSelected;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostParkingSpotActivity.class);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_parking_spot);
        ButterKnife.bind(this);


        setSupportActionBar(postParkingSpotToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post a Parking Spot");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.post_parking_spot_autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    locationSelected = place;
                    latitude = locationSelected.getLatLng().latitude;
                    longitude = locationSelected.getLatLng().longitude;
                    Log.i(TAG, "Place: " + place.getName());
                }

                @Override
                public void onError(Status status) {// TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        parkingImageView.setImageResource(R.mipmap.empty_parking);

        nameString = "";
        latitude = -1;
        longitude = -1;


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }


    @OnClick(R.id.upload_parking_spot_picture_button)
    protected void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    //Method called when user selects a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Make sure the gallery Intent called this method
        if(requestCode==1 && resultCode==RESULT_OK && data!=null ){
            sourceImageUri = data.getData();
            parkingImageView.setImageURI(sourceImageUri);
        }
    }

    public boolean checkFields(){
        nameString = parkingNameEditText.getText().toString();
        boolean isValid = true;

        if (nameString.isEmpty()) {
            isValid = false;
            parkingNameTextInputLayout.setErrorEnabled(true);
            parkingNameTextInputLayout.setError("Please enter a name.");
        } else {
            parkingNameTextInputLayout.setErrorEnabled(false);
        }
        // allow the user now to upload a parking space with the blank/default image
//        if (sourceImageUri == null) {
//            isValid = false;
//            Toast.makeText(PostListingActivity.this, "Please upload a picture of your parking spot.",
//                    Toast.LENGTH_SHORT).show();
//        }
        if (latitude == -1 && longitude == -1) {
            isValid = false;
            Toast.makeText(PostParkingSpotActivity.this, "Please select a location through the search bar.",
                    Toast.LENGTH_SHORT).show();
        }
        saveSwitchValues();
        return isValid;
    }

    private void saveSwitchValues(){
        isCompact = compactSwitch.isChecked();
        isCovered = coveredSwitch.isChecked();
        isHandicap =  handicapSwitch.isChecked();
    }

}
