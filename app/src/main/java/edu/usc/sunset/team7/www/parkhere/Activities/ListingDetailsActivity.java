package edu.usc.sunset.team7.www.parkhere.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import butterknife.BindView;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by kunal on 10/26/16.
 */
public class ListingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.post_listing_toolbar) Toolbar postListingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        setSupportActionBar(postListingToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Temporary string, should replace with title of listing later
            getSupportActionBar().setTitle("Listing details");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @OnClick(R.id.book_listing_button)
    protected void bookListing() {

    }

}
