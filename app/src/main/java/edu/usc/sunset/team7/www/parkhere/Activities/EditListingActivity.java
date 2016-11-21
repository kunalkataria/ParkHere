package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.concurrent.Semaphore;

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
    public void populateFields(Listing getListing) {
        parkingNameEditText.setText(getListing.getName());
        descriptionEditText.setText(getListing.getDescription());
        String origPrice = Double.toString(getListing.getPrice());
        priceEditText.setText(origPrice);
        listingId = getListing.getListingID();

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
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            new UpdateListingTask().execute(radioButtonID);
        }
    }

    public boolean checkFields() {
        nameString = parkingNameEditText.getText().toString();
        descriptionString = descriptionEditText.getText().toString();
        //possible invalid number format here
        String checkPrice = priceEditText.getText().toString();
        if(checkPrice.length() > 0) {
            price = Double.parseDouble(priceEditText.getText().toString());
        } else {
            price = -1;
        }
            //return false;
        boolean isValid = true;
        if(nameString.isEmpty()) {
            isValid = false;
            parkingNameTextInputLayout.setErrorEnabled(true);
            System.out.println("NAME FAIL");
            parkingNameTextInputLayout.setError("Please enter a name.");
        }
        if (descriptionString.isEmpty()) {
            isValid = false;
            descriptionTextInputLayout.setErrorEnabled(true);
            System.out.println("DESCRIPTION FAIL");
            descriptionTextInputLayout.setError("Please enter a description.");
        }
        if (price < 0) {
            isValid = false;
            priceTextInputLayout.setErrorEnabled(true);
            System.out.println("PRICE FAIL");
            priceTextInputLayout.setError("Please enter a price greater than $0");
        }
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            isValid = false;
            System.out.println("RADIO BUTTON FAIL");
            Toast.makeText(EditListingActivity.this, "Please select a cancellation policy.",
                    Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private class UpdateListingTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... radioButton) {

            System.out.println("ASYNC TASK IS CALLED");

            final Semaphore semaphore = new Semaphore(0);

            DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
            String uid = currentUser.getUid();

            Log.i("TESTING****", "LISTING ID: " + editListing.getListingID());

            DatabaseReference nameRef = mDatabase1.child(Consts.LISTINGS_DATABASE).child(uid).child(Consts.ACTIVE_LISTINGS).child(editListing.getListingID());
            nameRef.child(Consts.LISTING_DESCRIPTION).setValue(descriptionString);
            nameRef.child(Consts.LISTING_PRICE).setValue(price);

            nameRef.child(Consts.LISTING_NAME).setValue(nameString);
            nameRef.child(Consts.LISTING_REFUNDABLE).setValue(cancellationIds.get(radioButton[0]));
            semaphore.release();

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

            try {
                semaphore.acquire();
                System.out.println("FINISHED UPDATING IN DATABASE");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            HomeActivity.startActivityForListing(EditListingActivity.this);
            EditListingActivity.this.finish();
        }
    }
}
