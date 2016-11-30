package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Activities.ConfirmPaymentActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

import static android.support.v7.appcompat.R.styleable.View;

/**
 * Created by Acer on 10/29/2016.
 */

public class BalanceFragment extends Fragment {

    @BindView(R.id.transfer_button)
    Button transferBalanceButton;

    @BindView(R.id.current_balance)
    AppCompatTextView currentBalanceNumber;

    @BindView(R.id.confirm_payment_button)
    AppCompatButton confirmPaymentButton;

    private double userBalance;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String uid;
    private ArrayList<Listing> inactiveListing;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        System.out.println(uid);
        userBalance = 0.0;
        inactiveListing = new ArrayList<Listing>();
    }

    @Override
    public void onResume() {
        super.onResume();

        new FindBalanceTask().execute(uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.balance_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.transfer_button)
    protected void transferToBank(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Consts.USERS_DATABASE).child(currentUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double balance = Double.parseDouble(dataSnapshot.child(Consts.USER_BALANCE).getValue().toString());
                String formattedBalance = "$" + String.format("%.2f", balance);
                Toast.makeText(getActivity().getApplicationContext(),
                        "You transferred " + formattedBalance + " to the bank!",
                        Toast.LENGTH_SHORT).show();

                ref.child(Consts.USER_BALANCE).setValue(0.0);
                userBalance = 0.0;
                formattedBalance = "$" + String.format("%.2f", userBalance);
                currentBalanceNumber.setText(formattedBalance);
//                ref.child(Consts.USER_BALANCE).setValue(addToBalance + balance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.confirm_payment_button)
    protected void viewPayments(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = currentUser.getUid();
        //may need to check null
        DatabaseReference ref = mDatabase.child(Consts.LISTINGS_DATABASE).child(uid).child(Consts.INACTIVE_LISTINGS);
        ref.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Booking>  bookingsToConfirm = new ArrayList<>();
                for(DataSnapshot bookingSnap : dataSnapshot.getChildren()) {
                    String currBookingID = bookingSnap.getKey();
                    Booking booking = parseBooking(bookingSnap);
                    if(booking != null) {
                        booking.setBookingID(currBookingID);
                        bookingsToConfirm.add(booking);
                    }
                }
                if(bookingsToConfirm.size() != 0) {
                    ConfirmPaymentActivity.startActivity(getActivity(), bookingsToConfirm);
                } else {
                    AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                    adb.setTitle("No pending booking payments");
                    adb.setPositiveButton("OK", null);
                    adb.show();
                }
//                ArrayList<Listing> inactiveListingz = new ArrayList<Listing>();
//                for(DataSnapshot listing : dataSnapshot.getChildren()) {
//                    String currListingID = listing.getKey();
//                    Listing toAdd = parseListing(listing);
//                    if(toAdd != null){
//                        toAdd.setListingID(currListingID);
//                        inactiveListingz.add(toAdd);
//                    }
//                }
//                if (inactiveListingz.size() != 0) {
//                    ConfirmPaymentActivity.startActivity(getActivity(), inactiveListingz);
//                } else {
//                    // popup box
//                    AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
//                    adb.setTitle("No pending booking payments");
//                    adb.setPositiveButton("OK", null);
//                    adb.show();
//                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private Booking parseBooking(DataSnapshot bookingSnap) {
        Listing listing = new Listing();
        Booking booking = new Booking(listing);
        String endTime = bookingSnap.child(Consts.BOOKING_END_TIME).getValue().toString();
        long convertTime = Long.parseLong(endTime);
        long unixTime = System.currentTimeMillis()/1000L;
        if(unixTime >= convertTime) {
            for(DataSnapshot child : bookingSnap.getChildren()) {
                switch(child.getKey()) {
                    case Consts.LISTING_ID:
                        booking.getMListing().setListingID(child.getValue().toString());
                        break;
                    case Consts.LISTING_NAME:
                        booking.getMListing().setName(child.getValue().toString());
                        break;
                    case Consts.PARKING_SPOTS_ID:
                        booking.getMListing().getParkingSpot().setParkingSpotID(child.getValue().toString());
                        break;
                    case Consts.BOOKING_START_TIME:
                        String getStartTime = child.getValue().toString();
                        booking.setBookStartTime(Long.parseLong(getStartTime));
                        break;
                    case Consts.BOOKING_END_TIME:
                        String getEndTime = child.getValue().toString();
                        booking.setBookEndTime(Long.parseLong(getEndTime));
                        break;
                    case Consts.LISTING_IMAGE:
                        booking.getMListing().getParkingSpot().setImageURL(child.getValue().toString());
                        break;
                    case Consts.LISTING_BOOK_TIME:
                        booking.setTimeIncrement(Integer.parseInt(child.getValue().toString()));
                        break;
                    case Consts.LISTING_PRICE:
                        booking.getMListing().setPrice(Double.parseDouble(child.getValue().toString()));
                        break;
                }
            }
        } else {
            return null;
        }
        return booking;

    }
    private Listing parseListing (DataSnapshot snapshot) {
        Listing listing = new Listing();
        String endTime = snapshot.child(Consts.LISTING_END_TIME).getValue().toString();
        boolean isPaid = Boolean.parseBoolean(snapshot.child(Consts.LISTING_IS_PAID).getValue().toString());
        long convertTime = Long.parseLong(endTime);
        long unixTime = System.currentTimeMillis() / 1000L;
        if (unixTime >= convertTime && !isPaid) {
            //FirebaseDatabase.getInstance().getReference().child(Consts.LISTINGS_DATABASE).child(currentUser.getUid()).child(Consts.INACTIVE_LISTINGS).child(currListingID).child(Consts.LISTING_IS_PAID).setValue(true);
            for (DataSnapshot child : snapshot.getChildren()) {
                switch (child.getKey()) {
                    case "Compact":
                        listing.getParkingSpot().setCompact(Boolean.parseBoolean(child.getValue().toString()));
                        break;
                    case "Covered":
                        listing.getParkingSpot().setCovered(Boolean.parseBoolean(child.getValue().toString()));
                        break;
                    case "Listing Description":
                        listing.setDescription(child.getValue().toString());
                        break;
                    case "Handicap":
                        listing.getParkingSpot().setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                        break;
                    case "Image URL":
                        listing.getParkingSpot().setImageURL(child.getValue().toString());
                        break;
                    case "Latitude":
                        listing.getParkingSpot().setLatitude(Double.parseDouble(child.getValue().toString()));
                        break;
                    case "Longitude":
                        listing.getParkingSpot().setLongitude(Double.parseDouble(child.getValue().toString()));
                        break;
                    case "Listing Name":
                        listing.setName(child.getValue().toString());
                        break;
                    case "Is Refundable":
                        listing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                        break;
                    case "Start Time":
                        String startTime = child.getValue().toString();
                        listing.setStartTime(Long.valueOf(startTime));
                        break;
                    case "End Time":
                        String stopTime = child.getValue().toString();
                        listing.setStopTime(Long.valueOf(stopTime));
                        break;
                    case "Price":
                        double price = Double.parseDouble(child.getValue().toString());
                        listing.setPrice(price);
                        break;

                }
            }
        }
        else{
            return null;
        }
        return listing;
    }

    private class FindBalanceTask extends AsyncTask<String, Void, Double> {

        double balance;

        @Override
        protected Double doInBackground(String... strings) {

            final Semaphore semaphore = new Semaphore(0);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Consts.USERS_DATABASE+"/"+strings[0]);
            dbRef.orderByChild(Consts.USER_BALANCE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()) {
                        if(child.getKey().equals(Consts.USER_BALANCE)) {
                            System.out.println(child.getValue().toString());
                            balance = Double.parseDouble(child.getValue().toString());
                            semaphore.release();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return balance;
        }

        @Override
        protected void onPostExecute(Double result) {
            String formattedBalance = "$" + String.format("%.2f", result);
            currentBalanceNumber.setText(formattedBalance);
        }

    }


}
