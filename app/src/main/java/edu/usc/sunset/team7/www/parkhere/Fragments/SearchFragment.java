package edu.usc.sunset.team7.www.parkhere.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Activities.HomeActivity;
import edu.usc.sunset.team7.www.parkhere.Activities.ResultsActivity;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by kunal on 10/12/16.
 */

public class SearchFragment extends Fragment {

    @BindView(R.id.start_date_inputlayout)
    TextInputLayout startDateInputlayout;
    @BindView(R.id.stop_date_inputlayout)
    TextInputLayout stopDateInputLayout;
    @BindView(R.id.start_date_edittext)
    AppCompatEditText startDateEditText;
    @BindView(R.id.stop_date_edittext)
    AppCompatEditText stopDateEditText;

    @BindView(R.id.latitude_edittext)
    AppCompatEditText latitudeEditText;
    @BindView(R.id.longitude_edittext)
    AppCompatEditText longitudeEditText;

    @BindView(R.id.search_date_checkbox)
    AppCompatCheckBox searchDateCheckbox;
    @BindView(R.id.search_currentlocation_checkbox)
    AppCompatCheckBox searchCurrentLocationCheckbox;

    private DatePickerDialog startDatePicker;
    private DatePickerDialog stopDatePicker;

    private TimePickerDialog startTimePicker;
    private TimePickerDialog stopTimePicker;

    private long startDate;
    private long stopDate;

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;

    private int stopYear;
    private int stopMonth;
    private int stopDay;
    private int stopHour;
    private int stopMinute;

    private Place locationSelected;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        startDate = -1;
        stopDate = -1;

        ActivityCompat.requestPermissions(this.getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            // set start date to current time if the time is not selected
            if (startDate == -1) {
                Calendar c = Calendar.getInstance();
                startDate = c.getTimeInMillis() / 1000;
            }
            ResultsActivity.startActivity(getActivity(), latLng.latitude, latLng.longitude, startDate, stopDate);
        } else {
            if (!latitudeEditText.getEditableText().toString().isEmpty() &&
                    !longitudeEditText.getEditableText().toString().isEmpty()) {
                double lat = Double.parseDouble(latitudeEditText.getEditableText().toString());
                double lon = Double.parseDouble(longitudeEditText.getEditableText().toString());
                System.out.println(lat + " " + lon);
                // set start date to current time if the time is not selected
                if (startDate == -1) {
                    Calendar c = Calendar.getInstance();
                    startDate = c.getTimeInMillis() / 1000;
                }
                ResultsActivity.startActivity(getActivity(), lat, lon, startDate, stopDate);
            } else {
                if (searchCurrentLocationCheckbox.isChecked()) {
                    if (HomeActivity.getGoogleApiClient() != null) {

                        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                                .getCurrentPlace(HomeActivity.getGoogleApiClient(), null);

                        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                            @Override
                            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                                Log.i("Place", "onResult");
                                if (likelyPlaces.getCount() <= 0) {
                                    Toast.makeText(SearchFragment.this.getActivity(), "Unable to detect Current Location", Toast.LENGTH_SHORT).show();
                                } else {
                                    LatLng latLng = likelyPlaces.get(0).getPlace().getLatLng();
                                    // set start date to current time if the time is not selected
                                    if (startDate == -1) {
                                        Calendar c = Calendar.getInstance();
                                        startDate = c.getTimeInMillis() / 1000;
                                    }
                                    ResultsActivity.startActivity(getActivity(), latLng.latitude, latLng.longitude, startDate, stopDate);
                                }
                                likelyPlaces.release();
                            }
                        });
                    }
                    else{
                        Toast.makeText(SearchFragment.this.getActivity(), "No GoogleApiClient", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter search criteria",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @OnClick(R.id.start_date_edittext)
    protected void startDateDialog() {
        if (searchDateCheckbox.isChecked()) {
            Calendar c = Calendar.getInstance();
            TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    startHour = hourOfDay;
                    startMinute = minute;
                    DateTime dateTime = new DateTime(startYear, startMonth, startDay, startHour, startMinute);
                    startDate = dateTime.getMillis() / 1000;
                    startDateEditText.setText(Tools.getDateString(dateTime));
                }
            };
            startTimePicker = new TimePickerDialog(getActivity(), startTimeListener, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
            DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    startYear = year;
                    startMonth = month + 1;
                    startDay = day;
                    startTimePicker.show();
                }
            };
            startDatePicker = new DatePickerDialog
                    (getActivity(), startDateListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            startDatePicker.show();

        }
    }

    @OnClick(R.id.stop_date_edittext)
    protected void stopDateDialog() {
        if (searchDateCheckbox.isChecked()) {
            Calendar c = Calendar.getInstance();
            TimePickerDialog.OnTimeSetListener stopTimeListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    stopHour = hourOfDay;
                    stopMinute = minute;
                    DateTime dateTime = new DateTime(stopYear, stopMonth, stopDay, stopHour, stopMinute);
                    stopDate = dateTime.getMillis() / 1000;
                    stopDateEditText.setText(Tools.getDateString(dateTime));
                }
            };
            stopTimePicker = new TimePickerDialog(getActivity(), stopTimeListener, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
            DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    stopYear = year;
                    stopMonth = month + 1;
                    stopDay = day;
                    stopTimePicker.show();
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
        startDate = -1;
        startDateEditText.setText("");
    }

    // set stop time to -1 for server
    private void clearStop() {
        stopDate = -1;
        stopDateEditText.setText("");
    }

}
