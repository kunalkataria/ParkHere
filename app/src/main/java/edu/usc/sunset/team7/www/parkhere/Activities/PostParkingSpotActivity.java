package edu.usc.sunset.team7.www.parkhere.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

public class PostParkingSpotActivity extends AppCompatActivity {

    private static final String TAG = "PostParkingSpotActivity";

    @BindView(R.id.parking_spot_name_textinputlayout) TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.parking_spot_name_edittext)AppCompatEditText parkingNameEditText;
    private String nameString;

    @BindView(R.id.post_currentlocation_checkbox)
    AppCompatCheckBox postCurrentLocationCheckbox;

    //Parking image controls
    @BindView(R.id.upload_parking_spot_picture_button) Button uploadParkingImageButton;
    @BindView(R.id.parking_spot_Image) ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.parking_spot_handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.parking_spot_compact_button_control) SwitchCompat compactSwitch;
    @BindView(R.id.parking_spot_covered_button_control) SwitchCompat coveredSwitch;

    boolean isCompact,isHandicap, isCovered;

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

    PlaceAutocompleteFragment autocompleteFragment;

    public GoogleApiClient mGoogleApiClient;

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostParkingSpotActivity.class);
        context.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        setContentView(R.layout.activity_post_parking_spot);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();

        setSupportActionBar(postParkingSpotToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post a Parking Spot");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        autocompleteFragment = (PlaceAutocompleteFragment)
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
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    @OnClick(R.id.post_currentlocation_checkbox)
    protected void setCurrentLocation() {
        if(postCurrentLocationCheckbox.isChecked()){
            if (this.getGoogleApiClient() != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(this.getGoogleApiClient(), null);

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        Log.i("Place", "onResult");
                        if (likelyPlaces.getCount() <= 0) {
                            Toast.makeText(PostParkingSpotActivity.this, "Unable to detect Current Location", Toast.LENGTH_SHORT).show();
                        } else {
                            locationSelected = likelyPlaces.get(0).getPlace();
                            latitude = locationSelected.getLatLng().latitude;
                            longitude = locationSelected.getLatLng().longitude;
                        }
                        likelyPlaces.release();
                    }
                });
            }
            else{
                Toast.makeText(PostParkingSpotActivity.this, "No GoogleApiClient", Toast.LENGTH_SHORT).show();
            }
        } else {
            latitude = -1;
            longitude = -1;
        }
    }

    @OnClick(R.id.upload_parking_spot_button)
    protected void submitListing() {
        if(checkFields()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();
            String parkingID = mDatabase.child(Consts.PARKING_SPOTS_DATABASE).child(uid).push().getKey();
            final DatabaseReference newListingRef = mDatabase.child(Consts.PARKING_SPOTS_DATABASE)
                    .child(uid).child(parkingID);
            newListingRef.child(Consts.PARKING_SPOTS_NAME).setValue(nameString);
            newListingRef.child(Consts.PARKING_SPOTS_COMPACT).setValue(isCompact);
            newListingRef.child(Consts.PARKING_SPOTS_COVERED).setValue(isCovered);
            newListingRef.child(Consts.PARKING_SPOTS_HANDICAP).setValue(isHandicap);
            newListingRef.child(Consts.PARKING_SPOTS_LATITUDE).setValue(latitude);
            newListingRef.child(Consts.PARKING_SPOTS_LONGITUDE).setValue(longitude);
            newListingRef.child(Consts.PARKING_SPOTS_BOOKING_COUNT).setValue(0);
            newListingRef.child(Consts.PARKING_SPOTS_ACTIVE).setValue(true);

            StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
            StorageReference parkingRef = storageRef.child(Consts.STORAGE_PARKING_SPACES);
            //Best way to store the data?
            if (sourceImageUri == null) {
                newListingRef.child(Consts.PARKING_SPOTS_IMAGE).setValue(Consts.DEFAULT_PARKING_IMAGE);
            } else {
                //compress image
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(sourceImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                        "Title", null);
                sourceImageUri = Uri.parse(path);

                try {
                    stream.close();
                    stream = null;
                } catch (IOException e) {

                    e.printStackTrace();
                }


                UploadTask uploadTask = parkingRef.child(parkingID).putFile(sourceImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, exception.toString());
                        Toast.makeText(PostParkingSpotActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        firebaseImageURL = taskSnapshot.getDownloadUrl().toString();
                        newListingRef.child(Consts.PARKING_SPOTS_IMAGE).setValue(firebaseImageURL);
                    }
                });
            }
            finish();
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
