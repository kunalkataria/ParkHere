package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomPaymentAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/30/16.
 */

public class ConfirmPaymentActivity extends AppCompatActivity{

    @BindView(R.id.confirm_payment_listview) ListView listingListView;
    @BindView(R.id.no_booking_payments_textview) TextView noBookingPaymentsTextView;
    @BindView(R.id.confirm_payment_toolbar) Toolbar confirmPaymentToolbar;
    private ArrayList<Booking> inactiveListings;

    public static void startActivity(Context context, ArrayList<Booking> inactiveListings) {
        Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(Consts.INACTIVE_LISTINGS_EXTRA, inactiveListings);
        context.startActivity(intent);
    }

    //ConfirmaPaymentActivity.startActivity(this, inactiveListings);

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_confirm_payments);
        ButterKnife.bind(this);
        setSupportActionBar(confirmPaymentToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Confirm Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        inactiveListings = (ArrayList<Booking>) getIntent().getSerializableExtra(Consts.INACTIVE_LISTINGS_EXTRA);
        final CustomPaymentAdapter adapter = new CustomPaymentAdapter(this, inactiveListings);
        listingListView.setAdapter(adapter);
        listingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ConfirmPaymentActivity.this);
                adb.setTitle("Confirm Payment?");
                adb.setMessage("Are you sure you want to confirm payment of " + inactiveListings.get(position).getMListing().getName() + " Note: ParkHere will take a 10% cut of the payment.");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Confirm", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Booking toRemove = inactiveListings.get(positionToRemove);
                        String providerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final double addToBalance = toRemove.getMListing().getPrice() * .9;
                        final DatabaseReference ref = mDatabase.child(Consts.USERS_DATABASE).child(providerID);
                        ref.addListenerForSingleValueEvent(new ValueEventListener(){

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Double balance = Double.parseDouble(dataSnapshot.child(Consts.USER_BALANCE).getValue().toString());
                                ref.child(Consts.USER_BALANCE).setValue(addToBalance + balance);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //mark it as paid
                        mDatabase
                                .child(Consts.LISTINGS_DATABASE)
                                .child(providerID)
                                .child(Consts.INACTIVE_LISTINGS)
                                .child(toRemove.getBookingID())
                                .removeValue();

                        final DatabaseReference pRef = mDatabase
                                .child(Consts.PARKING_SPOT_DATABASE)
                                .child(providerID)
                                .child(toRemove.getMListing().getParkingID());

                        pRef.addListenerForSingleValueEvent(new ValueEventListener(){

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Log.i("TESTING*******", "TRYING TO INCREMENT BOOKING COUNT");
                                int bookCount = Integer.parseInt(dataSnapshot.child(Consts.PARKING_SPOTS_BOOKING_COUNT).getValue().toString());
                                //Log.i("TESTING*******", "BOOKING COUNT CURRENTLY:" + bookCount);
                                pRef.child(Consts.PARKING_SPOTS_BOOKING_COUNT).setValue(bookCount + 1);
                                //Log.i("TESTING*******", "BOOKING COUNT CURRENTLY:" + bookCount);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//                        mDatabase
//                                .child(Consts.LISTINGS_DATABASE)
//                                .child(providerID)
//                                .child(Consts.INACTIVE_LISTINGS)
//                                .child(toRemove.getListingID())
//                                .child(Consts.LISTING_IS_PAID)
//                                .setValue(true);
                        inactiveListings.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        if (adapter.getCount() == 0) {
                            noBookingPaymentsTextView.setVisibility(View.VISIBLE);
                        }
                    }});
                adb.show();
            }
        });
    }
}
