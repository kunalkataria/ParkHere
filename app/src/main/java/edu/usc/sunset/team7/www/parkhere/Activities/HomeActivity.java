package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import edu.usc.sunset.team7.www.parkhere.Fragments.BalanceFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.BookingFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.ListingFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.ProfileFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.SearchFragment;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.home_toolbar) Toolbar homeToolbar;

    public static final String FRAGMENT_TAG = "fragment_tag";
    public GoogleApiClient mGoogleApiClient;
    private static final String[] fragmentTitles = new String[]
                    {"Search",
                    "Listings",
                    "Bookings",
                    "Balance",
                    "My Profile"};
    private static final String[] fragmentTags = new String[]
                    {Consts.SEARCH_FRAGMENT_TAG,
                    Consts.LISTING_FRAGMENT_TAG,
                    Consts.BOOKING_FRAGMENT_TAG,
                    Consts.BALANCE_FRAGMENT_TAG,
                    Consts.MY_PROFILE_FRAGMENT_TAG};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    
    // call this static method if you want the homeactivity to start with the search fragment
    public static void startActivityForSearch(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    public static void startActivityForListing(Context context) {

    }

    public static void startActivityPostBooking(Context context) {
        Intent newIntent = new Intent(context, HomeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(FRAGMENT_TAG, Consts.BOOKING_FRAGMENT_TAG);
        context.startActivity(newIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(homeToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, fragmentTitles));

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                moveFragments(adapterView, view, i, l);
            }
        });

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
                setToolbarTitle(getResources().getString(R.string.search));
                break;
            case Consts.BOOKING_FRAGMENT_TAG:
                if (getFragmentManager().findFragmentByTag(Consts.BOOKING_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.BOOKING_FRAGMENT_TAG);
                } else {
                    currentFragment = new BookingFragment();
                }
                setToolbarTitle(getResources().getString(R.string.booking));
                break;
        }

        // use the fragment manager to move to the fragment selected by the switch statement
        if (currentFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment, currentFragmentTag).commit();
        } else {
            //TODO: figure out what do in this case
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
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

    protected void moveFragments(AdapterView<?> parent, View view, int position, long id) {
        String fragmentTag = fragmentTags[position];
        Fragment currentFragment = null;
        if (getFragmentManager().findFragmentByTag(fragmentTag) != null) {
            currentFragment = getFragmentManager().findFragmentByTag(fragmentTag);
        } else {
            switch (fragmentTag) {
                case Consts.SEARCH_FRAGMENT_TAG:
                    currentFragment = new SearchFragment();
                    setToolbarTitle(getResources().getString(R.string.search));
                    break;
                case Consts.LISTING_FRAGMENT_TAG:
                    currentFragment = new ListingFragment();
                    setToolbarTitle(getResources().getString(R.string.listing));
                    break;
                case Consts.BOOKING_FRAGMENT_TAG:
                    currentFragment = new BookingFragment();
                    setToolbarTitle(getResources().getString(R.string.booking));
                    break;
                case Consts.BALANCE_FRAGMENT_TAG:
                    currentFragment = new BalanceFragment();
                    setToolbarTitle(getResources().getString(R.string.balance));
                    break;
                case Consts.MY_PROFILE_FRAGMENT_TAG:
                    currentFragment = new ProfileFragment();
                    setToolbarTitle(getResources().getString(R.string.profile));
                    break;
            }
        }
        if (currentFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment, fragmentTag).commit();

            mDrawerList.setItemChecked(position, true);
            setTitle(fragmentTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            //TODO: figure out what do in this case
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }

}
