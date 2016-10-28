package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ResultsPair;

/**
 * Created by kunal on 10/26/16.
 */
public class ListingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.listing_name)
    TextView listingNameTextView;
    @BindView(R.id.listing_details)
    TextView listingDetailsTextView;
    @BindView(R.id.provider_name)
    TextView providerNameTextView;
    @BindView(R.id.parking_image)
    ImageView parkingImageView;
    @BindView(R.id.book_listing_button)
    Button bookListingButton;
    @BindView(R.id.post_listing_toolbar) Toolbar postListingToolbar;

    private ResultsPair listingResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        ButterKnife.bind(this);

        setSupportActionBar(postListingToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Temporary string, should replace with title of listing later
            getSupportActionBar().setTitle("Listing details");
        }

        listingResult = (ResultsPair) getIntent().getSerializableExtra(Consts.LISTING_RESULT_EXTRA);
        listingNameTextView.setText(listingResult.getListing().getName());
        providerNameTextView.setText(listingResult.getListing().getOwner().getFirstName());
        listingDetailsTextView.setText(listingDetailsString());
    }

    private String listingDetailsString(){
        //Format information for listing!!!
        String s = "";
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @OnClick(R.id.book_listing_button)
    protected void bookListing() {

        TransactionActivity.startActivity(ListingDetailsActivity.this);
    }
    @OnClick(R.id.provider_name)
    protected void displayProvider(){
        //Go to public user profile activity
        Intent intent = new Intent(ListingDetailsActivity.this, UserProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Consts.PAYMENT_TYPE, Consts.CREDIT_CARD);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
