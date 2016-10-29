package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Activities.ResultsActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by kunal on 10/12/16.
 */

public class SearchFragment extends Fragment {

    @BindView(R.id.start_date_inputlayout) TextInputLayout startDateInputlayout;
    @BindView(R.id.stop_date_inputlayout) TextInputLayout stopDateInputLayout;
    @BindView(R.id.start_time_edittext) AppCompatEditText startTimeEditText;
    @BindView(R.id.stop_time_edittext) AppCompatEditText stopTimeEditText;

    @BindView(R.id.search_date_checkbox) AppCompatCheckBox searchDateCheckbox;

    private DatePickerDialog startDatePicker;
    private DatePickerDialog stopDatePicker;

    private long startDate;
    private long stopDate;

    private Place locationSelected;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        ButterKnife.bind(this, view);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.search_autocomplete_fragment);
        Log.i(TAG, "on create view");
        //set location to be the current location
        if (autocompleteFragment != null) {
            Log.i(TAG, "Autocomplete Fragment not null");
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

    @OnClick(R.id.search_button)
    protected void startSearch() {
        if (locationSelected != null) {
            LatLng latLng = locationSelected.getLatLng();
            ResultsActivity.startActivity(getActivity(), latLng.latitude, latLng.longitude, 0, 0);
        }
    }

    @OnClick(R.id.start_date_inputlayout)
    protected void startDateDialog() {
        if (searchDateCheckbox.isChecked()) {
            LocalDate localDate = LocalDate.now();
            DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    DateTime datetime = new DateTime(year, month, day, 0, 0);
                    startDate = datetime.getMillis() / 1000; // save this
                    startTimeEditText.setText(Tools.getDateString(year, month, day));
                    Log.i("TESTING******", "START DATE IS " + startDate);
                }
            };
            startDatePicker = new DatePickerDialog
                    (getActivity(), startDateListener, localDate.getYear(), localDate.getMonthOfYear() - 1, localDate.getDayOfYear());

        }
    }

    @OnClick(R.id.stop_date_inputlayout)
    protected void stopDateDialog() {
        if (searchDateCheckbox.isChecked()) {
            if (searchDateCheckbox.isChecked()) {
                LocalDate localDate = LocalDate.now();
                DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        DateTime datetime = new DateTime(year, month, day, 23, 59);
                        stopDate = datetime.getMillis() / 1000;
                        stopTimeEditText.setText(Tools.getDateString(year, month, day));
                        Log.i("TESTING******", "STOP DATE IS " + stopDate);
                    }
                };
                startDatePicker = new DatePickerDialog
                        (getActivity(), startDateListener, localDate.getYear(), localDate.getMonthOfYear() - 1, localDate.getDayOfYear());

            }
        }
    }

    @OnCheckedChanged(R.id.search_date_checkbox)
    protected void searchWithDateCheckbox() {
        if (!searchDateCheckbox.isChecked()) {
            // clear start and stop dates
            clearStart();
            clearStop();
        }
    }

    private void clearStart() {
        startDate = 0;
        startTimeEditText.clearComposingText();
    }

    private void clearStop() {
        stopDate = 0;
        stopTimeEditText.clearComposingText();
    }

    public void sendLocationToFirebase() {
        if(locationSelected == null) return;

        LatLng latLng = locationSelected.getLatLng();

        //send it over
//        try {
//            String url = "http://www.parkhere-ceccb.appspot.com/?"+
//                    "lat="+latLng.latitude+
//                    "&long="+latLng.longitude;
//            URL servletURL = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) servletURL.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Content-Type", "text/plain");
//            connection.setRequestProperty("charset", "utf-8");
//            connection.connect();
//
//            //reading back listings as json
//            InputStream is = connection.getInputStream();
//            JsonReader reader = new JsonReader(new InputStreamReader(is));
//            while(reader.hasNext()) {
//                reader.beginObject();
//
//            }
//        } catch (IOException ioe) {
//            System.out.println(ioe.getMessage());
//        }
    }


}
