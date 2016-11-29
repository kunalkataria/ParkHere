package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomResultsAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Services.SearchService;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;
import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;

/**
 * Created by Acer on 10/25/2016.
 */

public class ResultsActivity extends AppCompatActivity {

    @BindView(R.id.avg_parking_price_title) TextView parkWhizIntroTextView;
    @BindView(R.id.list_content_space) LinearLayout listContentSpace;
    @BindView(R.id.avg_parking_price_value) TextView avgParkingView;
    @BindView(R.id.loading_panel) RelativeLayout loadingPanel;
    @BindView(R.id.results_toolbar) Toolbar resultsToolbar;

    private boolean covered = false;
    private boolean compact = false;
    private boolean handicap = false;
    private boolean priorBooking = false;

    private  SearchResult mSearchResult;

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SearchResult searchResult = (SearchResult) bundle.getSerializable(Consts.SEARCH_RESULT_EXTRA);
            populateResults(searchResult);
        }
    };

    public static void startActivity(Context context, double lat, double lon, long startTime, long stopTime) {
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra(Consts.LATITUDE_EXTRA, lat);
        intent.putExtra(Consts.LONGITUDE_EXTRA, lon);
        intent.putExtra(Consts.START_TIME_EXTRA, startTime);
        intent.putExtra(Consts.STOP_TIME_EXTRA, stopTime);
        context.startActivity(intent);
    }

    private void populateResults(SearchResult searchResult) {
        removeLoadingAnimation();
        mSearchResult = searchResult;
        ListViewCompat listView = new ListViewCompat(this);
        listView.setAdapter(new CustomResultsAdapter(this, searchResult.getAllListings()));
        listView.setId(Consts.RESULTS_LIST_VIEW_ID);
        listContentSpace.addView(listView);
        if (mSearchResult.getAverageParkPrice() == -1.0) {
            parkWhizIntroTextView.setVisibility(View.GONE);
            avgParkingView.setText(getResources().getString(R.string.avg_parking_price_not_found));
        } else {
            avgParkingView.setText(Double.toString(mSearchResult.getAverageParkPrice()));
        }
    }

    private void populateResultsList(List<ResultsPair> listings) {
        listContentSpace.removeAllViewsInLayout();
        ListViewCompat listView = new ListViewCompat(this);
        listView.setAdapter(new CustomResultsAdapter(this, listings));
        listContentSpace.addView(listView);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);
        setSupportActionBar(resultsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search Results");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Consts.SEARCH_INTENT_FILTER));
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra(Consts.LATITUDE_EXTRA, 0);
        double longitude = intent.getDoubleExtra(Consts.LONGITUDE_EXTRA, 0);
        long startTime = intent.getLongExtra(Consts.START_TIME_EXTRA, 0);
        long stopTime = intent.getLongExtra(Consts.STOP_TIME_EXTRA, 0);
//        SearchService searchService = new SearchService();


        Intent serviceIntent = new Intent(this, SearchService.class);
        serviceIntent.putExtra(Consts.LATITUDE_EXTRA, latitude);
        serviceIntent.putExtra(Consts.LONGITUDE_EXTRA, longitude);
        serviceIntent.putExtra(Consts.START_TIME_EXTRA, startTime);
        serviceIntent.putExtra(Consts.STOP_TIME_EXTRA, stopTime);
        startService(serviceIntent);
    }

    // public static void startActivityForResult(int requestCode, Context context, boolean covered, boolean handicap, boolean compact
    @OnClick (R.id.filter_button)
    protected void startFiltering() {
//        Intent intent = new Intent(this, FilterActivity.class);
//        intent.putExtra(Consts.COVERED_EXTRA, covered);
//        intent.putExtra(Consts.HANDICAP_EXTRA, handicap);
//        intent.putExtra(Consts.COMPACT_EXTRA, compact);
//        startActivityForResult(intent, 1);
        FilterActivity.startActivityForResult(1, this, covered, handicap, compact, priorBooking);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Consts.FILTERS_CHANGED) {
                handicap = data.getBooleanExtra(Consts.HANDICAP_EXTRA, false);
                covered = data.getBooleanExtra(Consts.COVERED_EXTRA, false);
                compact = data.getBooleanExtra(Consts.COMPACT_EXTRA, false);
                priorBooking = data.getBooleanExtra(Consts.PRIOR_BOOKING_EXTRA, false);
                filterResults();
            }
        }
    }

    private void filterResults() {
        if (mSearchResult != null) {
            List<ResultsPair> searchResultListings = mSearchResult.getAllListings();
            // if all are set to false, set back to original results
            if (!covered && !handicap && !compact && !priorBooking) {
                populateResultsList(searchResultListings);
            } else {
                List<ResultsPair> filteredResults = searchResultListings;
                if (handicap) {
                    filteredResults = filterResultsOnHandicap(filteredResults);
                }
                if (covered) {
                    filteredResults = filterResultsOnCovered(filteredResults);
                }
                if (compact) {
                    filteredResults = filteredResultsOnCompact(filteredResults);
                }
                if (priorBooking) {
                    filteredResults = filteredResultsOnPriorBooking(filteredResults);
                }
                populateResultsList(filteredResults);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public List<ResultsPair> filterResultsOnHandicap(List<ResultsPair> currentResults) {
        List<ResultsPair> filteredResults = new ArrayList<ResultsPair>();
        for (ResultsPair currentPair : currentResults) {
            if (currentPair.getListing().isHandicap()) {
                filteredResults.add(currentPair);
            }
        }
        return filteredResults;
    }

    public List<ResultsPair> filterResultsOnCovered(List<ResultsPair> currentResults) {
        List<ResultsPair> filteredResults = new ArrayList<ResultsPair>();
        for (ResultsPair currentPair : currentResults) {
            if (currentPair.getListing().isCovered()) {
                filteredResults.add(currentPair);
            }
        }
        return filteredResults;
    }

    public List<ResultsPair> filteredResultsOnCompact(List<ResultsPair> currentResults) {
        List<ResultsPair> filteredResults = new ArrayList<ResultsPair>();
        for (ResultsPair currentPair : currentResults) {
            if (currentPair.getListing().isCompact()) {
                filteredResults.add(currentPair);
            }
        }
        return filteredResults;
    }

    public List<ResultsPair> filteredResultsOnPriorBooking(List<ResultsPair> currentResults) {
        List<ResultsPair> filteredResults = new ArrayList<ResultsPair>();
        for (ResultsPair currentPair : currentResults) {
            if (currentPair.getDistance() <= 1.0) {
                filteredResults.add(currentPair);
            }
        }
        Collections.sort(filteredResults, new BookingCountComparator());
        return filteredResults;
    }

    private void removeLoadingAnimation() {
        loadingPanel.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    static class BookingCountComparator implements Comparator<ResultsPair> {
        @Override
        public int compare(ResultsPair rp1, ResultsPair rp2) {
            int count1 = rp1.getListing().getParkingSpot().getBookingCount();
            int count2 = rp2.getListing().getParkingSpot().getBookingCount();
            if ( count1 < count2 ){ return 1;}
            if ( count1 > count2 ){ return -1;}
            return 0;
        }
    }

}
