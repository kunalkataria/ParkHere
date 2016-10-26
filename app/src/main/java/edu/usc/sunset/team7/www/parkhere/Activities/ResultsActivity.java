package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by Acer on 10/25/2016.
 */

public class ResultsActivity extends AppCompatActivity {

    @BindView(R.id.results_view)
    ListView resultsListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);
    }

    public static void startActivityForResults(Context context) {
        Intent intent = new Intent(context, ResultsActivity.class);
        //intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    public void populateWithListings (String URL) {
        //retrofit
        //get array of objects back
        //create listings
    }

}
