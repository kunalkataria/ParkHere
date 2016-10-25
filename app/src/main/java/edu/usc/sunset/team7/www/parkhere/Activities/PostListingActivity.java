package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by kunal on 10/23/16.
 */

public class PostListingActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.name_textinputlayout)
    TextInputLayout parkingNameTextInputLayout;
    @BindView(R.id.description_textinputlayout)
    TextInputLayout desriptionTextInputLayout;
    @BindView(R.id.priceEditText)
    EditText priceEditText;

    //Cancellation controls
    @BindView(R.id.cancellation_textView)
    TextView cancellation;
    @BindView(R.id.myRadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.flexible_rButton)
    RadioButton flexibleRButton;
    @BindView(R.id.moderate_rButton)
    RadioButton moderateRButton;
    @BindView(R.id.strict_rButton)
    RadioButton strictRButton;
    @BindView(R.id.superstrict30_rButton)
    RadioButton superStrict30RButton;
    @BindView(R.id.superstrict60_rButton)
    RadioButton superStrict60RButton;
    @BindView(R.id.longTerm_rButton)
    RadioButton longTermRButton;

    //Parking image controls
    @BindView(R.id.upload_parking_button)
    Button uploadParkingImageButton;
    @BindView(R.id.uploadImage)
    ImageView parkingImageView;

    //Parking Type Buttons
    @BindView(R.id.handicap_button_control)
    SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control)
    SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control)
    SwitchCompat coveredSwitch;
    @BindView(R.id.truck_button_control)
    SwitchCompat truckSwitch;
    @BindView(R.id.suv_button_control)
    SwitchCompat suvSwitch;

    @BindView(R.id.upload_listing_button)
    Button uploadListingButton;

    private String cancellationDetailsString = "Flexible: Full refund 1 day prior to arrival, except fees\n" +
            "Moderate: Full refund 5 days prior to arrival, except fees\n" +
            "Strict: 50% refund up until 1 week prior to arrival, except fees\n" +
            "Super Strict 30: 50% refund up until 30 days prior to arrival, except fees\n" +
            "Super Strict 60: 50% refund up until 60 days prior to arrival, except fees\n" +
            "Long Term: First month not refundable, 30 day notice for cancellation";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private Uri sourceImageUri = null;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, PostListingActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listing);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        parkingImageView.setImageResource(R.mipmap.empty_parking);
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

    @OnClick(R.id.cancellation_textView)
    protected void viewCancellationPolicyDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(cancellationDetailsString)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do something
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.upload_listing_button)
    protected void submitListing() {
        if(checkFields()) {

        }
        else{
            Toast.makeText(PostListingActivity.this, "Please fill out all forms",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFields(){
        return true;
    }
}
