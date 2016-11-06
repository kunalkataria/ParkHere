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

    //Listing Information
    private Listing listing;
    private double distance;
    private String providerFirstName, startTime, endTime;


    private String billingText;
    //Payment Information
    private String paymentType;

    //Paypal variables
    private String paypalEmail;

    //Credit Card variables
    private String name, creditCardNumber, securityCode, month, year, address, city, state, zipcode, creditCardType;

    @BindView(R.id.listing_details_textview)
    TextView listingDetailsTextView;
    @BindView(R.id.billing_information_textview)
    TextView billingInformationTextView;
    @BindView(R.id.place_booking_button)
    Button placeBookingButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_confirmation);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(Consts.LISTING_DISTANCE) && bundle.containsKey(Consts.LISTING_TO_BE_BOOKED)
                    &&  bundle.containsKey(Consts.LISTING_DETAILS_STRING)) {
                distance = bundle.getDouble(Consts.LISTING_DISTANCE);
                listing = (Listing)bundle.getSerializable(Consts.LISTING_TO_BE_BOOKED);
                listingDetailsTextView.setText(bundle.getString(Consts.LISTING_DETAILS_STRING));
            } else{
                Log.d(TAG, "LISTING AND DISTANCE NOT FOUND");
                placeBookingButton.setEnabled(false);
            }
            if(bundle.containsKey(Consts.PAYMENT_TYPE)) {
                paymentType = (String) bundle.get(Consts.PAYMENT_TYPE);
                if (paymentType.equals(Consts.CREDIT_CARD)) {
                    name = bundle.getString(Consts.CREDIT_CARD_NAME);
                    creditCardNumber = bundle.getString(Consts.CREDIT_CARD_NUMBER);
                    creditCardType =  bundle.getString(Consts.CREDIT_CARD_TYPE);
                    securityCode = bundle.getString(Consts.SECURITY_CODE);
                    month = bundle.getString(Consts.EXPIRATION_MONTH);
                    year = bundle.getString(Consts.EXPIRATION_YEAR);
                    address =  bundle.getString(Consts.ADDRESS);
                    city =  bundle.getString(Consts.CITY);
                    state =  bundle.getString(Consts.STATE);
                    zipcode = bundle.getString(Consts.ZIPCODE);
                    displayCreditCardInfo();
                } else{
                    paypalEmail = (String) bundle.get(Consts.PAYPAL_EMAIL);
                    displayPaypalInfo();
                }
            } else{
                Log.d(TAG, "NO PAYMENT TYPE???");
                placeBookingButton.setEnabled(false);
            }
        } else{
            Log.d(TAG, "BUNDLE WAS EMPTY!");
            placeBookingButton.setEnabled(false);
        }
    }

    private void displayCreditCardInfo(){
        billingText = "Payment Type: Credit Card" + "\n" + creditCardType + " - " + hideCreditCardNumber() +
                "\nSecurity Code:" + securityCode + "\nExpiration Date: " + month +"/" + year +
                "\n\nBilling Address: \n" + billingAddressText();
        billingInformationTextView.setText(billingText);
    }

    private String billingAddressText(){
        return address + "\n" + city + ", " + state + " " + zipcode;
    }

    private String hideCreditCardNumber(){
        String last4 = creditCardNumber.substring(creditCardNumber.length()-4, creditCardNumber.length());
        return "************" + last4;
    }

    private void displayPaypalInfo(){
        billingText = "Payment Type: PayPal" + "\n PayPal email: " + paypalEmail;
        billingInformationTextView.setText(billingText);
    }

    @OnClick(R.id.place_booking_button)
    public void placeBooking() {
        //Write to database here
        String uid = currentUser.getUid();
        String bookingID = mDatabase.child(Consts.BOOKINGS_DATABASE).push().getKey();

        //Add to Booking database
        DatabaseReference bookingRef = mDatabase.child(Consts.BOOKINGS_DATABASE).child(uid).child(bookingID);
        bookingRef.child(Consts.LISTING_ID).setValue(listing.getListingID());
        bookingRef.child(Consts.LISTING_START_TIME).setValue(listing.getStartTime());
        bookingRef.child(Consts.LISTING_END_TIME).setValue(listing.getStopTime());
        bookingRef.child(Consts.PROVIDER_ID).setValue(listing.getProviderID());

        //Move Listing to inactive
        DatabaseReference inactiveListingRef = mDatabase.child(Consts.LISTINGS_DATABASE).child(listing.getProviderID()).child(Consts.INACTIVE_LISTINGS).child(listing.getListingID());
        inactiveListingRef.child(Consts.LISTING_NAME).setValue(listing.getName());
        inactiveListingRef.child(Consts.LISTING_DESCRIPTION).setValue(listing.getDescription());
        inactiveListingRef.child(Consts.LISTING_REFUNDABLE).setValue(listing.isRefundable());
        inactiveListingRef.child(Consts.LISTING_PRICE).setValue(listing.getPrice());
        inactiveListingRef.child(Consts.LISTING_COMPACT).setValue(listing.isCompact());
        inactiveListingRef.child(Consts.LISTING_COVERED).setValue(listing.isCovered());
        inactiveListingRef.child(Consts.LISTING_HANDICAP).setValue(listing.isHandicap());
        inactiveListingRef.child(Consts.LISTING_LATITUDE).setValue(listing.getLatitude());
        inactiveListingRef.child(Consts.LISTING_LONGITUDE).setValue(listing.getLongitude());
        inactiveListingRef.child(Consts.LISTING_START_TIME).setValue(listing.getStartTime());
        inactiveListingRef.child(Consts.LISTING_END_TIME).setValue(listing.getStopTime());
        inactiveListingRef.child(Consts.LISTING_IMAGE).setValue(listing.getImageURL());
        inactiveListingRef.child(Consts.LISTING_IS_PAID).setValue(false);
        //Remove listing from active
        mDatabase.child(Consts.LISTINGS_DATABASE).child(listing.getProviderID()).child(Consts.ACTIVE_LISTINGS).child(listing.getListingID()).removeValue();

//        final DatabaseReference providerRef = mDatabase.child(Consts.USERS_DATABASE)
//                .child(listing.getProviderID()).child(Consts.USER_BALANCE);

//        //Get and add balance from provider
//        ValueEventListener providerNameListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Double balance = Double.parseDouble(dataSnapshot.getValue().toString());
//                providerRef.setValue(balance + listing.getPrice());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadProviderName:onCancelled", databaseError.toException());
//            }
//        };
//        providerRef.addListenerForSingleValueEvent(providerNameListener);
        HomeActivity.startActivityPostBooking(this);
        finish();
    }
}
