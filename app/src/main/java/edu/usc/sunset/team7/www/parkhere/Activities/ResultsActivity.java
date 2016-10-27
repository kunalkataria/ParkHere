package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Adapters.CustomResultsAdapter;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Services.SearchService;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.SearchResult;

/**
 * Created by Acer on 10/25/2016.
 */

public class ResultsActivity extends AppCompatActivity {

    @BindView(R.id.list_content_space) LinearLayout listContentSpace;

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SearchResult searchResult = (SearchResult) bundle.getSerializable(Consts.SEARCH_RESULT_EXTRA);
            populateResults(searchResult);
        }
    };

    public static void startActivityForResults(Context context) {
        Intent intent = new Intent(context, ResultsActivity.class);
        //intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    private void populateResults(SearchResult searchResult) {
        ListViewCompat listView = new ListViewCompat(this);
        listView.setAdapter(new CustomResultsAdapter(this, searchResult.getAllListings()));
        listContentSpace.addView(listView);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Consts.SEARCH_INTENT_FILTER));
        Intent intent = getIntent();
        long latitude = intent.getLongExtra(Consts.LATITUDE_EXTRA, 0);
        long longitude = intent.getLongExtra(Consts.LONGITUDE_EXTRA, 0);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
