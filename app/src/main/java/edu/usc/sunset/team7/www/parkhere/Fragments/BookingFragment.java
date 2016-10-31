package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Booking;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by kunal on 10/22/16.
 */

public class BookingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private boolean done;

    //for parsing the listing
    private boolean foundListing;
    private DataSnapshot listing;
    private Listing curListing;

    private ArrayList<Booking> allBookings;
    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        //Get booking from database
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        allBookings = new ArrayList<Booking>();
        done = false;
        getAllBookings();
        //while(!done) try { Thread.sleep(5); } catch (InterruptedException e) {e.printStackTrace();}
        Booking[] sendToAdapter = allBookings.toArray(new Booking[allBookings.size()]);
        //need to call the adapter now

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected void getAllBookings(){
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child(Consts.BOOKINGS_DATABASE).child(uid);
        System.out.println("Got here");
        if(ref == null) System.out.println("FUCK");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot booking : dataSnapshot.getChildren()) {
                    System.out.println(booking.getKey());
                    Booking toAdd = parseBooking(booking);
                    System.out.println("Parsed booking");
                    String listingID = booking.child(Consts.BOOKING_ID).getValue().toString();
                    String providerID = booking.child(Consts.PROVIDER_ID).getValue().toString();
                    Listing mListing = parseListing(listingID, providerID);
                    System.out.println("Parsed listing");
                    toAdd.setMListing(mListing);
                    allBookings.add(toAdd);
                }
                done = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private Booking parseBooking (DataSnapshot snapshot) {
        System.out.println("BOOKING");
        Booking toAddBooking = new Booking(null);
        for(DataSnapshot child : snapshot.getChildren()) {
            switch (child.getKey()) {
                case Consts.LISTING_END_TIME:
                    toAddBooking.setBookEndTime(Long.parseLong(child.getValue().toString()));
                    break;
                case Consts.LISTING_START_TIME:
                    toAddBooking.setBookStartTime(Long.parseLong(child.getValue().toString()));
                    break;

            }
        }
        return toAddBooking;
    }

    private Listing parseListing (String listingID, String providerID) {
        foundListing = false;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Consts.LISTINGS_DATABASE)
                .child(providerID).child(Consts.INACTIVE_LISTINGS).child(listingID);
        curListing = new Listing();
        curListing.setListingID(listingID);
        curListing.setProviderID(providerID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listing = dataSnapshot;
                System.out.println(dataSnapshot.getKey());
                for (DataSnapshot child : listing.getChildren()) {
                    switch (child.getKey()) {
                        case Consts.LISTING_NAME:
                            curListing.setName(child.getValue().toString());
                            break;
                        case Consts.LISTING_DESCRIPTION:
                            curListing.setDescription(child.getValue().toString());
                            break;
                        case Consts.LISTING_REFUNDABLE:
                            curListing.setRefundable(Boolean.parseBoolean(child.getValue().toString()));
                        case Consts.LISTING_COMPACT:
                            curListing.setCompact(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_COVERED:
                            curListing.setCovered(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_HANDICAP:
                            curListing.setHandicap(Boolean.parseBoolean(child.getValue().toString()));
                            break;
                        case Consts.LISTING_PRICE:
                            curListing.setPrice(Double.parseDouble(child.getValue().toString()));
                            break;
                        case Consts.LISTING_LATITUDE:
                            curListing.setLatitude(Double.parseDouble(child.getValue().toString()));
                            break;
                        case Consts.LISTING_LONGITUDE:
                            curListing.setLongitude(Double.parseDouble(child.getValue().toString()));
                            break;
                        case Consts.LISTING_START_TIME:
                            curListing.setStartTime(Long.valueOf(child.getValue().toString()));
                            break;
                        case Consts.LISTING_END_TIME:
                            curListing.setStopTime(Long.valueOf(child.getValue().toString()));
                            break;
                    }
                }
                foundListing = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                foundListing = true;
                System.out.println("NO!");
            }
        });

        while(!foundListing) try { Thread.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }


        return curListing;
    }
}
