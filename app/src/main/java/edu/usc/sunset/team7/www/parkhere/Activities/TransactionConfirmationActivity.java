package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Services.EmailService;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Jonathan on 10/26/16.
 */
public class TransactionConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "TransactionConfirmation";

    //Listing Information
    private Listing listing;
    private String providerFirstName, providerLastName, providerPhoneNumber, providerEmail;
    private int bookTime;

    private String billingText, listingText;
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

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Consts.EMAIL_INTENT_FILTER));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(Consts.LISTING_DISTANCE) && bundle.containsKey(Consts.LISTING_TO_BE_BOOKED)
                    &&  bundle.containsKey(Consts.LISTING_DETAILS_STRING)) {
                listing = (Listing)bundle.getSerializable(Consts.LISTING_TO_BE_BOOKED);
                listingText = bundle.getString(Consts.LISTING_DETAILS_STRING);
                bookTime = bundle.getInt(Consts.LISTING_BOOK_TIME);
                listingDetailsTextView.setText(listingText);
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
        billingText = "Payment Type: Credit Card" + "\nName: " + name +"\n" + creditCardType + " - " + hideCreditCardNumber() +
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
        bookingRef.child(Consts.PARKING_SPOTS_ID).setValue(listing.getParkingSpot().getParkingSpotID());
        long bookStart = listing.getStartTime() + (bookTime * listing.getIncrement() * 60 * 60);
        long bookStop = bookStart + (listing.getIncrement() * 60 * 60);
        bookingRef.child(Consts.LISTING_START_TIME).setValue(listing.getStartTime());
        bookingRef.child(Consts.LISTING_END_TIME).setValue(listing.getStopTime());
        bookingRef.child(Consts.LISTING_BOOK_TIME).setValue(bookTime);

        //Doodle: New Move Listing to inactive
        DatabaseReference inactiveBookingRef = mDatabase.child(Consts.LISTINGS_DATABASE)
                .child(listing.getProviderID()).child(Consts.INACTIVE_LISTINGS).child(bookingID);
        inactiveBookingRef.child(Consts.LISTING_ID).setValue(listing.getListingID());
        inactiveBookingRef.child(Consts.BOOKING_START_TIME).setValue(listing.getStartTime());
        inactiveBookingRef.child(Consts.BOOKING_END_TIME).setValue(listing.getStopTime());
        inactiveBookingRef.child(Consts.LISTING_BOOK_TIME).setValue(bookTime);
        inactiveBookingRef.child(Consts.LISTING_IMAGE).setValue(listing.getImageURL());
        inactiveBookingRef.child(Consts.LISTING_NAME).setValue(listing.getName());
        inactiveBookingRef.child(Consts.LISTING_PRICE).setValue(listing.getPrice());
        inactiveBookingRef.child(Consts.PARKING_SPOTS_ID).setValue(listing.getParkingID());
        inactiveBookingRef.child(Consts.LISTING_REFUNDABLE).setValue(listing.isRefundable());

        //Edit Listing Active Times
        final DatabaseReference activeTimesRef = mDatabase.child(Consts.LISTINGS_DATABASE)
                .child(listing.getProviderID()).child(Consts.ACTIVE_LISTINGS)
                .child(listing.getListingID());
        activeTimesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String bookingTimes = dataSnapshot
                        .child(Consts.LISTING_ACTIVE_TIMES)
                        .getValue().toString();
                String[] timeAvailability = bookingTimes.split(",");
                ArrayList<Integer> timesAvailable = new ArrayList<>();
                for (int i = 0; i < timeAvailability.length; i++) {
                    int currTime = Integer.parseInt(timeAvailability[i]);
                    if (currTime != bookTime){
                        timesAvailable.add(currTime);
                    }
                }
                if (timesAvailable.size() > 0) {
                    Collections.sort(timesAvailable);
                    StringBuilder sb = new StringBuilder();
                    sb.append(timesAvailable.get(0));
                    for (int i = 1; i < timesAvailable.size(); i++) {
                        sb.append(",");
                        sb.append(timesAvailable.get(i));
                    }
                    String timeAvailabilityString = sb.toString();
                    activeTimesRef.child(Consts.LISTING_ACTIVE_TIMES)
                            .setValue(timeAvailabilityString);
                } else {
                    activeTimesRef.child(Consts.LISTING_ACTIVE_TIMES)
                            .setValue(",");
                    activeTimesRef.child(Consts.LISTING_CURRENT_ACTIVE)
                            .setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        HomeActivity.startActivityPostBooking(TransactionConfirmationActivity.this);
        finish();
    }

    private void getProviderInformation(){
        Log.d(TAG, "GET PROVIDER INFORMATION REACHED!");

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE).child(listing.getProviderID());

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                providerFirstName = dataSnapshot.child(Consts.USER_FIRSTNAME).getValue().toString();
                providerLastName = dataSnapshot.child(Consts.USER_LASTNAME).getValue().toString();
                providerPhoneNumber = dataSnapshot.child(Consts.USER_PHONENUMBER).getValue().toString();
                providerEmail = dataSnapshot.child(Consts.USER_EMAIL).getValue().toString();
                makeEmailBody();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void makeEmailBody(){
        Log.d(TAG, "************MAKE EMAIL BODY REACHED!");
        String s = "Thanks for choosing ParkHere! Below are the details of your transaction.\n\n" +
        "Provider Contact Information:\n" +
                "\tName: " + providerFirstName + " " + providerLastName +
                "\n\tPhone number: " + providerPhoneNumber +
                "\n\tEmail: " + providerEmail +
        "\n\nParking Spot Location: (" + listing.getLongitude() + ", " + listing.getLatitude() + ")" +
        "\n\nListing Details:\n" + listingText +
        "\n\nBilling Details:\n" + billingText +
        "\n\nHappy parking!\nParkHere Team" +
        "\n\nQuestions? Too bad!";
        generateEmailText(mAuth.getCurrentUser().getEmail(), s);
    }

    private void generateEmailText(String email, String textBody) {
        Log.d(TAG, "********GENERATE EMAIL TEXT REACHED!");

        Intent serviceIntent = new Intent(this, EmailService.class);
        serviceIntent.putExtra(Consts.EMAIL_EXTRA, email);
        serviceIntent.putExtra(Consts.TEXT_BODY_EXTRA, textBody);
        startService(serviceIntent);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            HomeActivity.startActivityPostBooking(TransactionConfirmationActivity.this);
//            finish();
        }
    };
}
