package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
    @BindView(R.id.parking_spot_name_edittext) AppCompatEditText parkingNameEditText;

    ///Image
    @BindView(R.id.parking_image) ImageView parkingImageView;

    @BindView(R.id.handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.covered_button_control) SwitchCompat coveredSwitch;
    @BindView(R.id.compact_button_control) SwitchCompat compactSwitch;

    @BindView(R.id.edit_parking_toolbar) Toolbar editParkingToolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ParkingSpot editParkingSpot;
    private Uri sourceImageUri = null;

    private String nameString;
    private boolean isHandicap;
    private boolean isCovered;
    private boolean isCompact;

    private String firebaseImageURL = "";

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

        setSupportActionBar(editParkingToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit parking spot");
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
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();
            final DatabaseReference editListingRef = mDatabase.child(Consts.PARKING_SPOT_DATABASE).child(uid).child(editParkingSpot.getParkingSpotID());
            editListingRef.child(Consts.PARKING_SPOTS_COMPACT).setValue(isCompact);
            editListingRef.child(Consts.PARKING_SPOTS_COVERED).setValue(isCovered);
            editListingRef.child(Consts.PARKING_SPOTS_HANDICAP).setValue(isHandicap);
            editListingRef.child(Consts.PARKING_SPOTS_NAME).setValue(nameString);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
            StorageReference parkingRef = storageRef.child(Consts.STORAGE_PARKING_SPACES);

            if (sourceImageUri != null) {
                UploadTask uploadTask = parkingRef.child(uid).putFile(sourceImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditParkingSpotActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        firebaseImageURL = taskSnapshot.getDownloadUrl().toString();
                    }
                });
                editListingRef.child(Consts.PARKING_SPOTS_IMAGE).setValue(firebaseImageURL);
            } else {
                editListingRef.child(Consts.PARKING_SPOTS_IMAGE).setValue(editParkingSpot.getImageURL());
            }
            // TODO: REDIRECT TO HOME ACTIVITY
            finish();
        }
    }

    private void populateFields() {
        parkingNameEditText.setText(editParkingSpot.getName());
        handicapSwitch.setChecked(editParkingSpot.isHandicap());
        coveredSwitch.setChecked(editParkingSpot.isCovered());
        compactSwitch.setChecked(editParkingSpot.isCompact());
        Picasso.with(this).load(editParkingSpot.getImageURL()).into(parkingImageView);
    }

    private void saveValues() {
        nameString = parkingNameEditText.getText().toString();
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

        return isValid;
    }
}
