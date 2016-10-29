package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/27/16.
 */

//NEED TO CHANGE HOW YOU POST THE LISTING DUE TO DB RESTRUCTURING
public class EditListingActivity extends AppCompatActivity {

    //private static final String EditListingTag = "EditListingActivity";

    //the container for each textfield
    @BindView(R.id.name_textinputlayout)
    TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.description_textinputlayout)
    TextInputLayout descriptionTextInputLayout;
    @BindView(R.id.price_textinputlayout)
    TextInputLayout priceTextInputLayout;

    //The actual textfields
    @BindView(R.id.name_edittext)
    AppCompatEditText parkingNameEditText;
    @BindView(R.id.description_edittext)
    AppCompatEditText descriptionEditText;
    @BindView(R.id.price_edittext)
    AppCompatEditText priceEditText;

    private String nameString, descriptionString;
    private double price;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control)
    SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control)
    SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control)
    SwitchCompat coveredSwitch;

    boolean isCompact, isHandicap, isCovered;

    //Cancellation policies
    @BindView(R.id.myRadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.refundable_rButton)
    RadioButton refundableRButton;
    @BindView(R.id.nonrefundable_rButton)
    RadioButton nonrefundableRButton;

    private static Hashtable<Integer, String> cancellationIds;


    //probably need this to reupload edits
    @BindView(R.id.upload_listing_button)
    Button uploadListingButton;

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
    public static void startActivity(Context context) {
        Intent i = new Intent(context, EditListingActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        Listing getEditableListing = (Listing) getIntent().getSerializableExtra(Consts.LISTING_EDIT_EXTRA);
        cancellationIds = new Hashtable<Integer, String>();
        cancellationIds.put(R.id.refundable_rButton, Consts.REFUNDABLE);
        cancellationIds.put(R.id.nonrefundable_rButton, Consts.NONREFUNDABLE);
        populateFields(getEditableListing);
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

            DatabaseReference nameRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(listingId);
            nameRef.child("description").setValue(descriptionString);
            nameRef.child("price").setValue(price);

            nameRef.child("handicap").setValue(isHandicap);
            nameRef.child("compact").setValue(isCompact);
            nameRef.child("covered").setValue(isCovered);
            nameRef.child("name").setValue(nameString);
            nameRef.child("refundable").setValue(cancellationIds.get(radioGroup.getCheckedRadioButtonId()));

            //Need image url

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
                    if(sourceImageUri!=null) {
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            saveSwitchValues();
                            return true;
                        } else {
                            Toast.makeText(EditListingActivity.this, "Please select a cancellation policy.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(EditListingActivity.this, "Please upload a picture of your parking spot.",
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
