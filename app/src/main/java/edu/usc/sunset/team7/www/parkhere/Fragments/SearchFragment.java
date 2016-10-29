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

import java.util.Calendar;
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
            ResultsActivity.startActivity(getActivity(), latLng.latitude, latLng.longitude, startDate, stopDate);
        }
    }

    @OnClick(R.id.start_time_edittext)
    protected void startDateDialog() {
        if (searchDateCheckbox.isChecked()) {
            Calendar c = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    DateTime datetime = new DateTime(year, month + 1, day, 0, 0);
                    startDate = datetime.getMillis() / 1000; // save this
                    startTimeEditText.setText(Tools.getDateString(year, month + 1, day));
                }
            };
            startDatePicker = new DatePickerDialog
                    (getActivity(), startDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            startDatePicker.show();

        }
    }

    @OnClick(R.id.stop_time_edittext)
    protected void stopDateDialog() {
        if (searchDateCheckbox.isChecked()) {
//                LocalDate localDate = LocalDate.now();
            Calendar c = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    DateTime datetime = new DateTime(year, month + 1, day, 23, 59);
                    stopDate = datetime.getMillis() / 1000;
                    stopTimeEditText.setText(Tools.getDateString(year, month + 1, day));
                }
            };
            stopDatePicker = new DatePickerDialog
                    (getActivity(), startDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            stopDatePicker.show();
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

    // set the time to be the current time when clearing
    private void clearStart() {
        Calendar c = Calendar.getInstance();
        startDate = c.getTimeInMillis() / 1000;
        startTimeEditText.setText("");
    }

    // set stop time to -1 for server
    private void clearStop() {
        stopDate = -1;
        stopTimeEditText.setText("");
    }

}
