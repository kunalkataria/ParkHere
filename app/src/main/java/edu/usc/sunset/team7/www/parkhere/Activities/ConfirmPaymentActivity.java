package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomPaymentAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by johnsonhui on 10/30/16.
 */

public class ConfirmPaymentActivity extends AppCompatActivity{

    @BindView(R.id.listing_listview) ListView listingListView;
    private DatabaseReference mDatabase;
    private ArrayList<Listing> inactiveListings;

    public static void startActivity(Context context, ArrayList<Listing> inactiveListings) {
        Intent intent = new Intent(context, ConfirmPaymentActivity.class);
        intent.putExtra(Consts.INACTIVE_LISTINGS_EXTRA, inactiveListings);
        context.startActivity(intent);
    }

    //ConfirmaPaymentActivity.startActivity(this, inactiveListings);

    @Override
    public void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        inactiveListings = (ArrayList<Listing>) getIntent().getSerializableExtra(Consts.INACTIVE_LISTINGS_EXTRA);
        final CustomPaymentAdapter adapter = new CustomPaymentAdapter(this, inactiveListings);
        listingListView.setAdapter(adapter);
        listingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ConfirmPaymentActivity.this);
                adb.setTitle("Confirm Payment?");
                adb.setMessage("Are you sure you want to confirm payment of " + inactiveListings.get(position).getName());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Confirm", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Listing toRemove = inactiveListings.get(positionToRemove);
                        String providerID = toRemove.getProviderID();
                        final double addToBalance = toRemove.getPrice();
                        final DatabaseReference ref = mDatabase.child(Consts.USERS_DATABASE).child(providerID);
                        ref.addListenerForSingleValueEvent(new ValueEventListener(){

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Double balance = (Double) dataSnapshot.getValue();
                                ref.child(Consts.USER_BALANCE).setValue(addToBalance + balance);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //remove listing from the db
                        mDatabase.child(Consts.LISTINGS_DATABASE).child(providerID).child(Consts.INACTIVE_LISTINGS).child(toRemove.getListingID()).removeValue();
                        inactiveListings.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
            }
        });
    }
}
