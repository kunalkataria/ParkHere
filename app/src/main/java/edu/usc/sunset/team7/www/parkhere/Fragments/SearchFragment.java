package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import butterknife.BindView;
import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.LoginActivity;
import edu.usc.sunset.team7.www.parkhere.R;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by kunal on 10/12/16.
 */

public class SearchFragment extends Fragment {

    private Place locationSelected;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_autocomplete_bar);
        //set location to be the current location
        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    locationSelected = place;
                    Log.i(TAG, "Place: " + place.getName());
                    //send over latitude and longitude w/ place.latlng
                }

                @Override
                public void onError(Status status) {// TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

        }
        return view;
    }

    public void sendLocationToFirebase() {
        if(locationSelected == null) return;

        Location toSend = new Location("");
        LatLng latLng = locationSelected.getLatLng();
        toSend.setLatitude(latLng.latitude);
        toSend.setLongitude(latLng.longitude);

        //send it over

    }

    public boolean isWithinRadius(Location a, Location b) {
        return (a.distanceTo(b) <= 4828.02); //number of meters in 3 miles
    }

}
