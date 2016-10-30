package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomResultsAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Services.SearchService;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;
import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;

/**
 * Created by Acer on 10/25/2016.
 */

public class ResultsActivity extends AppCompatActivity {

    @BindView(R.id.list_content_space) LinearLayout listContentSpace;
    @BindView(R.id.avg_parking_price_value) TextView avgParkingView;
    @BindView(R.id.loading_panel) RelativeLayout loadingPanel;

    private boolean covered = false;
    private boolean compact = false;
    private boolean handicap = false;

    private SearchResult mSearchResult;

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
        listContentSpace.addView(listView);
        avgParkingView.setText(Double.toString(mSearchResult.getAverageParkPrice()));
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
        FilterActivity.startActivityForResult(1, this, covered, handicap, compact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Consts.FILTERS_CHANGED) {
                handicap = data.getBooleanExtra(Consts.HANDICAP_EXTRA, false);
                covered = data.getBooleanExtra(Consts.COVERED_EXTRA, false);
                compact = data.getBooleanExtra(Consts.COMPACT_EXTRA, false);
                filterResults();
            }
        }
    }

    private void filterResults() {
        if (mSearchResult != null) {
            List<ResultsPair> searchResultListings = mSearchResult.getAllListings();
            // if all are set to false, set back to original results
            if (!covered && !handicap && !compact) {
                populateResultsList(searchResultListings);
            } else {
                List<ResultsPair> filteredResults = new ArrayList<ResultsPair>();
                for (ResultsPair currentPair : searchResultListings) {
                    Listing currentListing = currentPair.getListing();
                    if (currentListing.isCompact() == compact &&
                            currentListing.isCovered() == covered &&
                            currentListing.isHandicap() == handicap) {

                        filteredResults.add(currentPair);
                    }
                }
                populateResultsList(filteredResults);
            }

        }
    }

    private void removeLoadingAnimation() {
        loadingPanel.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
