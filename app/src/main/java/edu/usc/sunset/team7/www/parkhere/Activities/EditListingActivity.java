package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import com.squareup.picasso.Picasso;

/**
 * Created by johnsonhui on 10/27/16.
 */

//NEED TO CHANGE HOW YOU POST THE LISTING DUE TO DB RESTRUCTURING
public class EditListingActivity extends AppCompatActivity {

    //private static final String EditListingTag = "EditListingActivity";

    //the container for each textfield
    @BindView(R.id.name_textinputlayout) TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.description_textinputlayout) TextInputLayout descriptionTextInputLayout;
    @BindView(R.id.price_textinputlayout) TextInputLayout priceTextInputLayout;

    //The actual textfields
    @BindView(R.id.name_edittext) AppCompatEditText parkingNameEditText;
    @BindView(R.id.description_edittext) AppCompatEditText descriptionEditText;
    @BindView(R.id.price_edittext) AppCompatEditText priceEditText;

    private String nameString, descriptionString;
    private double price;

    ///Image
    @BindView(R.id.parking_image) ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control) SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control) SwitchCompat coveredSwitch;

    boolean isCompact, isHandicap, isCovered;

    //Cancellation policies
    @BindView(R.id.myRadioGroup) RadioGroup radioGroup;
    @BindView(R.id.refundable_rButton) RadioButton refundableRButton;
    @BindView(R.id.nonrefundable_rButton) RadioButton nonrefundableRButton;

    private static Hashtable<Integer, String> cancellationIds;

    @BindView(R.id.upload_listing_button) Button uploadListingButton;

    private String editNameString, editDescriptionString;
    private double editPrice;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private Uri sourceImageUri = null;
    private String firebaseImageURL = "";
    private String listingId;
    private Listing editListing;

    public static void startActivity(Context context, Listing listing) {
        Intent i = new Intent(context, EditListingActivity.class);
        i.putExtra(Consts.LISTING_EDIT_EXTRA, listing);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        editListing = (Listing) getIntent().getSerializableExtra(Consts.LISTING_EDIT_EXTRA);
        cancellationIds = new Hashtable<Integer, String>();
        cancellationIds.put(R.id.refundable_rButton, Consts.REFUNDABLE);
        cancellationIds.put(R.id.nonrefundable_rButton, Consts.NONREFUNDABLE);
        populateFields(editListing);
    }

    //For populating the fields
    protected void populateFields(Listing getListing) {
        parkingNameEditText.setText(getListing.getName());
        descriptionEditText.setText(getListing.getDescription());
        String origPrice = Double.toString(getListing.getPrice());
        priceEditText.setText(origPrice);
        handicapSwitch.setChecked(getListing.isHandicap());
        compactSwitch.setChecked(getListing.isCompact());
        coveredSwitch.setChecked(getListing.isCovered());
        listingId = getListing.getListingID();
        sourceImageUri = null;
        URL url = null;
        Picasso.with(this).load(getListing.getImageURL()).into(parkingImageView);

        boolean isRefundable = getListing.isRefundable();
        if(isRefundable){
            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
        }
        else{
            ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
        }
        //need to make an image class for listings to set the image as that
        //grab the url and grab the image from the database through URL
        //String imageURL = getListing.getImageURL();
    }

    @OnClick(R.id.upload_listing_button)
    protected void submitListing() {
        if(checkFields()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();

            Log.i("TESTING****", "LISTING ID: " + editListing.getListingID());

            DatabaseReference nameRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(uid).child(Consts.ACTIVE_LISTINGS).child(editListing.getListingID());
            nameRef.child(Consts.LISTING_DESCRIPTION).setValue(descriptionString);
            nameRef.child(Consts.LISTING_PRICE).setValue(price);

            nameRef.child(Consts.LISTING_HANDICAP).setValue(isHandicap);
            nameRef.child(Consts.LISTING_COMPACT).setValue(isCompact);
            nameRef.child(Consts.LISTING_COVERED).setValue(isCovered);
            nameRef.child(Consts.LISTING_NAME).setValue(nameString);
            nameRef.child(Consts.LISTING_REFUNDABLE).setValue(cancellationIds.get(radioGroup.getCheckedRadioButtonId()));


            if (sourceImageUri != null) {
                //Need image url
                StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
                StorageReference parkingRef = storageRef.child(Consts.STORAGE_PARKING_SPACES);
                //Best way to store the data?
                UploadTask uploadTask = parkingRef.child(uid).putFile(sourceImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditListingActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        firebaseImageURL = taskSnapshot.getDownloadUrl().toString();
                    }
                });
                nameRef.child(Consts.LISTING_IMAGE).setValue(firebaseImageURL);
            } else {
                nameRef.child(Consts.LISTING_IMAGE).setValue(editListing.getImageURL());
            }

            HomeActivity.startActivityForListing(this);
            finish();
        }
    }

    @OnClick(R.id.upload_parking_button)
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


    private boolean checkFields() {
        nameString = parkingNameEditText.getText().toString();
        descriptionString =descriptionEditText.getText().toString();
        //possible invalid number format here
        price = Double.parseDouble(priceEditText.getText().toString());

        if(!nameString.equals("")){
            if(!descriptionString.equals("")) {
                if(price>=0){
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        saveSwitchValues();
                        return true;
                    } else {
                        Toast.makeText(EditListingActivity.this, "Please select a cancellation policy.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    priceTextInputLayout.setErrorEnabled(true);
                    priceTextInputLayout.setError("Please enter a price greater than $0");
                }
            } else {
                descriptionTextInputLayout.setErrorEnabled(true);
                descriptionTextInputLayout.setError("Please enter a description.");
            }
        } else {
            parkingNameTextInputLayout.setErrorEnabled(true);
            parkingNameTextInputLayout.setError("Please enter a name.");
        }
        return false;
    }

    private void saveSwitchValues(){
        isCompact = compactSwitch.isChecked();
        isCovered = coveredSwitch.isChecked();
        isHandicap =  handicapSwitch.isChecked();
    }
}
