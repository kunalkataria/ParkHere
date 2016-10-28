package edu.usc.sunset.team7.www.parkhere.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Jonathan on 10/26/16.
 */
public class TransactionConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "TransactionConfirmation";

    private Listing listing;

    private String paymentType;

    //Paypal variables
    private String paypalEmail;

    //Credit Card
    private String name, creditCardNumber, securityCode, address, city, state, zipcode, creditCardType;

    private String billingText, listingText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @BindView(R.id.listing_details_textview)
    TextView listingDetailsTextView;

    @BindView(R.id.billing_information_textview)
    TextView billingInformationTextView;

    @BindView(R.id.place_booking_button)
    Button placeBookingButton;


//    public static void startActivity(Context context) {
//        Intent i = new Intent(context, TransactionConfirmationActivity.class);
//        context.startActivity(i);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_confirmation);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && bundle.containsKey(Consts.PAYMENT_TYPE)){
            paymentType = (String)bundle.get(Consts.PAYMENT_TYPE);
            if(paymentType.equals(Consts.CREDIT_CARD)){
                name = (String)bundle.get(Consts.CREDIT_CARD_NAME);
                creditCardNumber = (String)bundle.get(Consts.CREDIT_CARD_NUMBER);
                securityCode = (String)bundle.get(Consts.SECURITY_CODE);
                address = (String)bundle.get(Consts.ADDRESS);
                city = (String)bundle.get(Consts.CITY);
                state = (String)bundle.get(Consts.STATE);
                zipcode = (String)bundle.get(Consts.ZIPCODE);
                creditCardType = (String)bundle.get(Consts.CREDIT_CARD_TYPE);
                displayCreditCardInfo();
            } else{
                paypalEmail = (String) bundle.get(Consts.PAYPAL_EMAIL);
                displayPaypalInfo();
            }
        } else{
            Log.d(TAG, "BUNDLE WAS EMPTY!");
        }
    }

    private void displayCreditCardInfo(){
        //FINISH STRING FORMATTING HERE
        billingText = "Payment Type: Credit Card" + "\nCredit Card Number: " + hideCreditCardNumber() +
                "\n";
        billingText += billingAddressText();

    }

    private String hideCreditCardNumber(){
        String last4 = creditCardNumber.substring(creditCardNumber.length()-4, creditCardNumber.length());
        return "************" + last4;
    }

    private void displayPaypalInfo(){
        billingText = "Payment Type: PayPal" + "\n PayPal email: " + paypalEmail;
        billingInformationTextView.setText(billingText);
    }

    private String billingAddressText(){
        //format the billing address text
        String billingAddress = address + "\n" + city + " " + state + " " + zipcode;
        return billingAddress;
    }

    @OnClick(R.id.place_booking_button)
    protected void placeBooking() {
        //Write to database here
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = currentUser.getUid();

        DatabaseReference nameRef = mDatabase.child(Consts.BOOKINGS_DATABASE).child(uid).child(listing.getName());
        nameRef.child("seeker").setValue(name);
        nameRef.child("bookStartTime").setValue(listing.getStartTime());
        nameRef.child("bookEndTime").setValue(listing.getStopTime());

        finish();
    }
}
