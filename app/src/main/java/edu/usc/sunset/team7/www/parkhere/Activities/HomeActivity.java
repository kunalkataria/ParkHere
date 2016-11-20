package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.Fragments.BalanceFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.BookingFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.ListingFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.ParkingSpotFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.ProfileFragment;
import edu.usc.sunset.team7.www.parkhere.Fragments.SearchFragment;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.home_toolbar) Toolbar homeToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    public static final String FRAGMENT_TAG = "fragment_tag";
    public static GoogleApiClient mGoogleApiClient;
    private static final String[] fragmentTitles = new String[]
                    {"Search",
                    "Parking Spots",
                    "Listings",
                    "Bookings",
                    "Balance",
                    "My Profile"};
    private static final String[] fragmentTags = new String[]
                    {Consts.SEARCH_FRAGMENT_TAG,
                    Consts.PARKING_SPOTS_FRAGMENT_TAG,
                    Consts.LISTING_FRAGMENT_TAG,
                    Consts.BOOKING_FRAGMENT_TAG,
                    Consts.BALANCE_FRAGMENT_TAG,
                    Consts.MY_PROFILE_FRAGMENT_TAG};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    // call this static method if you want the homeactivity to start with the search fragment
    public static void startActivityForSearch(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(FRAGMENT_TAG, Consts.SEARCH_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    public static void startActivityForListing(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(FRAGMENT_TAG, Consts.LISTING_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    public static void startActivityPostBooking(Context context) {
        Intent newIntent = new Intent(context, HomeActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(FRAGMENT_TAG, Consts.BOOKING_FRAGMENT_TAG);
        context.startActivity(newIntent);
    }

    public static void startActivityForBalance(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(FRAGMENT_TAG, Consts.BALANCE_FRAGMENT_TAG);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(homeToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, homeToolbar, R.string.open_drawer, R.string.close_drawer)
        {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                //drawerOpened = false;
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
                //drawerOpened = true;
            }
        };
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

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

        // go to search screen if starting home actviity without passing a fragment tag
        if (currentFragmentTag == null) {
            currentFragmentTag = Consts.SEARCH_FRAGMENT_TAG;
        }

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
            case Consts.PARKING_SPOTS_FRAGMENT_TAG:
                if (getFragmentManager().findFragmentByTag(Consts.PARKING_SPOTS_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.PARKING_SPOTS_FRAGMENT_TAG);
                } else {
                    currentFragment = new ParkingSpotFragment();
                }
                setToolbarTitle(getResources().getString(R.string.parking_spot));
            case Consts.BOOKING_FRAGMENT_TAG:
                if (getFragmentManager().findFragmentByTag(Consts.BOOKING_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.BOOKING_FRAGMENT_TAG);
                } else {
                    currentFragment = new BookingFragment();
                }
                setToolbarTitle(getResources().getString(R.string.booking));
                break;
            case Consts.LISTING_FRAGMENT_TAG:
                if (getFragmentManager().findFragmentByTag(Consts.LISTING_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.LISTING_FRAGMENT_TAG);
                } else {
                    currentFragment = new ListingFragment();
                }
                setToolbarTitle(getResources().getString(R.string.listing));
                break;
            case Consts.BALANCE_FRAGMENT_TAG:
                if (getFragmentManager().findFragmentByTag(Consts.BALANCE_FRAGMENT_TAG) != null) {
                    currentFragment = getFragmentManager().findFragmentByTag(Consts.BALANCE_FRAGMENT_TAG);
                } else {
                    currentFragment = new BalanceFragment();
                }
                setToolbarTitle(getResources().getString(R.string.balance));
        }

        // use the fragment manager to move to the fragment selected by the switch statement
        if (currentFragment != null) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment, currentFragmentTag).commit();
        } else {
            //TODO: figure out what do in this case
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
                case R.id.menu_action_report_issue:
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Issue with ParkHere");
                    String[] address = new String[] {"parkhereteam@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, address);
                    if (intent.resolveActivity(this.getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Email application not found", Toast.LENGTH_SHORT).show();
                    }
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
                case Consts.PARKING_SPOTS_FRAGMENT_TAG:
                    currentFragment = new ParkingSpotFragment();
                    setToolbarTitle(getResources().getString(R.string.parking_spot));
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Unable to connect to GoogleApiClient", Toast.LENGTH_SHORT).show();
    }
}
