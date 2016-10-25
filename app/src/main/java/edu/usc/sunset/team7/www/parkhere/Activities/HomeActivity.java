package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import edu.usc.sunset.team7.www.parkhere.Fragments.SearchFragment;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

public class HomeActivity extends AppCompatActivity {

    public static final String FRAGMENT_TAG = "fragment_tag";
    public GoogleApiClient mGoogleApiClient;

    // call this static method if you want the homeactivity to start with the search fragment
    public static void startActivityForSearch(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    public static void startActivityForListing(Context context) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();

        String currentFragmentTag = getIntent().getStringExtra(FRAGMENT_TAG);

        Fragment currentFragment = null;

        // switch on the currentFragmentTag, which came from the intent
        switch (currentFragmentTag) {
            case Consts.SEARCH_FRAGMENT_TAG:
                // find if the fragment exists first
                if (getFragmentManager().findFragmentByTag(Consts.SEARCH_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.SEARCH_FRAGMENT_TAG);
                } else {
                    // create the fragment if it doesn't exist
                    currentFragment = new SearchFragment();
                }
        }

        // use the fragment manager to move to the fragment selected by the switch statement
        if (currentFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment, currentFragmentTag).commit();
        } else {
            //TODO: figure out what do in this case
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {
        boolean consumed = super.onOptionsItemSelected(selectedItem);
        if (!consumed) {
            switch (selectedItem.getItemId()) {
                case R.id.menu_action_sign_out :
                    FirebaseAuth.getInstance().signOut();
                    LoginActivity.startActivity(this);
                    finish();
                    return true;
            }
        }
        return false;
    }
}
