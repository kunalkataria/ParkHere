package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by kunal on 10/23/16.
 */

public class PostListingActivity extends AppCompatActivity {

    private static final String TAG = "PostListingActivity";

    @BindView(R.id.name_textinputlayout)
    TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.description_textinputlayout)
    TextInputLayout descriptionTextInputLayout;
    @BindView(R.id.price_textinputlayout)
    TextInputLayout priceTextInputLayout;

    @BindView(R.id.name_edittext)
    AppCompatEditText parkingNameEditText;
    @BindView(R.id.description_edittext)
    AppCompatEditText descriptionEditText;
    @BindView(R.id.price_edittext)
    AppCompatEditText priceEditText;

    private String nameString, descriptionString;
    private double price;

    //Cancellation controls
    @BindView(R.id.cancellation_textView)
    TextView cancellation;
    @BindView(R.id.myRadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.refundable_rButton)
    RadioButton refundableRButton;
    @BindView(R.id.nonrefundable_rButton)
    RadioButton nonrefundableRButton;

    private static Hashtable<Integer, String> cancellationIds;

    //Parking image controls
    @BindView(R.id.upload_parking_button)
    Button uploadParkingImageButton;

    @BindView(R.id.parkingImage)
    ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control)
    SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control)
    SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control)
    SwitchCompat coveredSwitch;

    boolean isCompact,isHandicap, isCovered, isRefundable;

    @BindView(R.id.upload_listing_button)
    Button uploadListingButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private Uri sourceImageUri = null;
    private String firebaseImageURL = "";

    //NEED TO ADD DATE AND TIME PICKERS
    private long startTime, endTime;

    //NEED TO GET LONGITUDE AND LATITUDE
    private double longitude, latitude;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostListingActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);
        ButterKnife.bind(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_autocomplete_fragment);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        parkingImageView.setImageResource(R.mipmap.empty_parking);

        nameString = "";
        descriptionString = "";
        price=0.0;

        cancellationIds = new Hashtable<Integer, String>();
        cancellationIds.put(R.id.refundable_rButton, Consts.REFUNDABLE);
        cancellationIds.put(R.id.nonrefundable_rButton, Consts.NONREFUNDABLE);

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

    @OnClick(R.id.upload_listing_button)
    protected void submitListing() {
        if(checkFields()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();
            String listingID = mDatabase.child(Consts.LISTINGS_DATABASE).push().getKey();;
            DatabaseReference newListingRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(listingID);

            newListingRef.child(Consts.LISTING_NAME).setValue(nameString);
            newListingRef.child(Consts.LISTING_DESCRIPTION).setValue(descriptionString);
            newListingRef.child(Consts.LISTING_REFUNDABLE).setValue(isRefundable);
            newListingRef.child(Consts.LISTING_PRICE).setValue(price);
            newListingRef.child(Consts.LISTING_COMPACT).setValue(isCompact);
            newListingRef.child(Consts.LISTING_COVERED).setValue(isCovered);
            newListingRef.child(Consts.LISTING_HANDICAP).setValue(isHandicap);
            newListingRef.child(Consts.LISTING_LATITUDE).setValue(latitude);
            newListingRef.child(Consts.LISTING_LONGITUDE).setValue(longitude);
            newListingRef.child(Consts.LISTING_START_TIME).setValue(startTime);
            newListingRef.child(Consts.LISTING_END_TIME).setValue(endTime);
            newListingRef.child(Consts.LISTING_PROVIDER).setValue(uid);
            newListingRef.child(Consts.LISTING_SEEKER).setValue(null);
            newListingRef.child(Consts.LISTING_RATING).setValue(null);
            newListingRef.child(Consts.LISTING_REVIEW).setValue(null);

            StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
            StorageReference parkingRef = storageRef.child(Consts.STORAGE_PARKING_SPACES);
            //Best way to store the data?
            UploadTask uploadTask = parkingRef.child(uid).putFile(sourceImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, exception.toString());
                    Toast.makeText(PostListingActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    firebaseImageURL = taskSnapshot.getDownloadUrl().toString();
                }
            });
            newListingRef.child(Consts.LISTING_IMAGE).setValue(firebaseImageURL);
        }
    }

    private boolean checkFields(){
        nameString = parkingNameEditText.getText().toString();
        descriptionString =descriptionEditText.getText().toString();
        //possible invalid number format here
        price = Double.parseDouble(priceEditText.getText().toString());

        if(!nameString.equals("")){
            if(!descriptionString.equals("")){
                if(price>=0){
                    if(sourceImageUri!=null) {
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            saveSwitchValues();
                            return true;
                        } else {
                            Toast.makeText(PostListingActivity.this, "Please select a cancellation policy.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(PostListingActivity.this, "Please upload a picture of your parking spot.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else{
                    priceTextInputLayout.setErrorEnabled(true);
                    priceTextInputLayout.setError("Please enter a price greater than $0");
                }
            } else{
                descriptionTextInputLayout.setErrorEnabled(true);
                descriptionTextInputLayout.setError("Please enter a description.");
            }
        } else{
            parkingNameTextInputLayout.setErrorEnabled(true);
            parkingNameTextInputLayout.setError("Please enter a name.");
        }
        return false;
    }

    private void saveSwitchValues(){
        isCompact = compactSwitch.isChecked();
        isCovered = coveredSwitch.isChecked();
        isHandicap =  handicapSwitch.isChecked();
        isRefundable = radioGroup.getCheckedRadioButtonId() == R.id.refundable_rButton;
    }
}
