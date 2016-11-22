package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by kunal on 10/27/16.
 */

public class FilterActivity extends AppCompatActivity {

    @BindView(R.id.filter_toolbar) Toolbar filterToolbar;
    @BindView(R.id.handicap_button_control) SwitchCompat handicapSwitch;
    @BindView(R.id.compact_button_control) SwitchCompat compactSwitch;
    @BindView(R.id.covered_button_control) SwitchCompat coveredSwitch;
    @BindView(R.id.booking_count_button_control) SwitchCompat bookingCSwitch;

    private boolean handicapInitial;
    private boolean compactInitial;
    private boolean coveredInitial;
    private boolean bookingCInitial;

    public static void startActivityForResult(int requestCode, Activity activity, boolean covered, boolean handicap, boolean compact, boolean priorBooking) {
        Intent intent = new Intent(activity, FilterActivity.class);
        intent.putExtra(Consts.COVERED_EXTRA, covered);
        intent.putExtra(Consts.HANDICAP_EXTRA, handicap);
        intent.putExtra(Consts.COMPACT_EXTRA, compact);
        intent.putExtra(Consts.PRIOR_BOOKING_EXTRA, priorBooking);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        setSupportActionBar(filterToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.filter);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent dataIntent = getIntent();
        if (dataIntent != null) {
            handicapInitial = dataIntent.getBooleanExtra(Consts.HANDICAP_EXTRA, false);
            compactInitial = dataIntent.getBooleanExtra(Consts.COMPACT_EXTRA, false);
            coveredInitial = dataIntent.getBooleanExtra(Consts.COVERED_EXTRA, false);
            bookingCInitial = dataIntent.getBooleanExtra(Consts.PRIOR_BOOKING_EXTRA, false);

            handicapSwitch.setChecked(handicapInitial);
            compactSwitch.setChecked(compactInitial);
            coveredSwitch.setChecked(coveredInitial);
            bookingCSwitch.setChecked(bookingCInitial);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {
        switch (selectedItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(Consts.HANDICAP_EXTRA, handicapSwitch.isChecked());
        data.putExtra(Consts.COVERED_EXTRA, coveredSwitch.isChecked());
        data.putExtra(Consts.COMPACT_EXTRA, compactSwitch.isChecked());
        data.putExtra(Consts.PRIOR_BOOKING_EXTRA, bookingCSwitch.isChecked());

        int resultCode;
        if (filtersChanged()) {
            resultCode = Consts.FILTERS_CHANGED;
        } else {
            resultCode = Consts.FILTERS_UNCHANGED;
        }
        setResult(resultCode, data);
        finish();
    }

    private boolean filtersChanged() {
        return handicapInitial != handicapSwitch.isChecked() ||
                coveredInitial != coveredSwitch.isChecked() ||
                compactInitial != compactSwitch.isChecked() ||
                bookingCInitial != bookingCSwitch.isChecked();
    }


}
