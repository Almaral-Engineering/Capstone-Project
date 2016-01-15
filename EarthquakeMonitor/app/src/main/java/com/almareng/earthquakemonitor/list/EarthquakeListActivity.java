package com.almareng.earthquakemonitor.list;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.Utils;
import com.almareng.earthquakemonitor.api.ApiClient;
import com.almareng.earthquakemonitor.details.DetailActivity;
import com.almareng.earthquakemonitor.details.DetailFragment;
import com.almareng.earthquakemonitor.sync.SyncAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class EarthquakeListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, EarthquakeListFragment.EarthquakeSelectedListener {
    public static final String PREFERENCE_LONGITUDE = "preference_longitude";
    public static final String PREFERENCE_LATITUDE = "preference_latitude";
    public static final String LOCATION_PREFS = "location_preference";

    // Database Columns
    public static final int COL_EQ_MAGNITUDE = 1;
    public static final int COL_EQ_PLACE= 2;
    public static final int COL_EQ_DISTANCE = 7;

    private SettingsFragment settingsFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private boolean showGpsPrompt = false;
    private Toolbar toolbar;
    private ProgressBar loadingSpinner;
    private boolean mTwoPane;
    private Earthquake selectedEarthquake;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_list);

        loadingSpinner = (ProgressBar) findViewById(R.id.earthquake_list_activity_loading_spinner);

        setupToolbar();
        fetchEarthquakeData();
        inflateMenu();

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        settingsFragment = new SettingsFragment();

        final EarthquakeListFragment earthquakeListFragment = new EarthquakeListFragment();
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.add(R.id.main_frame_layout, earthquakeListFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mTwoPane = findViewById(R.id.earthquake_detail_tablet_layout) != null;

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onEarthquakeSelected(final View view, final Earthquake earthquake) {
        if (mTwoPane) {
            selectedEarthquake = earthquake;

            final FragmentManager fragmentManager = getFragmentManager();
            final DetailFragment fragment = (DetailFragment) fragmentManager.findFragmentById(R.id.fragment_detail);

            mShareActionProvider.setShareIntent(getDefaultIntent());
            fragment.setupViews(earthquake);
            setupMap(earthquake);
        } else {
            DetailActivity.newIntent(this, view.findViewById(R.id.earthquake_list_item_magnitude_text), earthquake);
        }
    }

    private void setupMap(final Earthquake earthquake) {
        final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_detail_map)).getMap();
        final LatLng eqLocation = new LatLng(Double.parseDouble(earthquake.getLatitude()),
                                             Double.parseDouble(earthquake.getLongitude()));

        if(map == null) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(eqLocation, 7));

        final Double magnitude = earthquake.getMagnitude();
        float hueColor;

        if(magnitude >= 0 && magnitude <= 0.9){
            hueColor = BitmapDescriptorFactory.HUE_GREEN;
        }
        else if(magnitude >= 9.0 && magnitude <= 9.9){
            hueColor = BitmapDescriptorFactory.HUE_RED;
        }
        else{
            hueColor = BitmapDescriptorFactory.HUE_BLUE;
        }

        map.addMarker(new MarkerOptions()
                              .title(getString(R.string.activity_detail_map_epicenter))
                              .position(eqLocation)
                              .icon(BitmapDescriptorFactory.defaultMarker(hueColor)));
    }

    @Override
    public void onBackPressed() {
        if (toolbar.getTitle().equals(getString(R.string.title_activity_settings))) {
            toolbar.getMenu().clear();
            toolbar.setNavigationIcon(null);
            toolbar.inflateMenu(R.menu.menu_main);
            toolbar.setTitle(getString(R.string.app_name));

            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (mTwoPane) {
            getMenuInflater().inflate(R.menu.menu_main_tablet, menu);

            final MenuItem shareItem = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    private Intent getDefaultIntent() {
        final String timeAndDate = selectedEarthquake.getTimeAndDate();
        final String dateFormat = getString(R.string.earthquake_detail_date_format);
        final String timeFormat = getString(R.string.earthquake_detail_time_format);
        final String formattedDate = Utils.getFormattedDateTime(timeAndDate, dateFormat);
        final String formattedTime = Utils.getFormattedDateTime(timeAndDate, timeFormat);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.sharing_intent_message),
                                                              selectedEarthquake.getMagnitude(),
                                                              formattedDate,
                                                              formattedTime,
                                                              selectedEarthquake.getPlace(),
                                                              selectedEarthquake.getLatitude(),
                                                              selectedEarthquake.getLongitude()));
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            final FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.main_frame_layout, settingsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            toolbar.setTitle(getString(R.string.title_activity_settings));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(final Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mCurrentLocation == null) {
            if(showGpsPrompt) {
                showGpsPrompt = false;
                showPromptActivateGPS();
            }
        } else {
            final String longitudeString = String.valueOf(mCurrentLocation.getLongitude());
            final String latitudeString = String.valueOf(mCurrentLocation.getLatitude());
            final SharedPreferences locationPreference = getSharedPreferences(LOCATION_PREFS, 0);
            final SharedPreferences.Editor editor = locationPreference.edit();

            editor.putString(PREFERENCE_LONGITUDE, longitudeString);
            editor.putString(PREFERENCE_LATITUDE, latitudeString);
            editor.apply();

            fetchEarthquakeData();
        }

        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
    }

    private void fetchEarthquakeData() {
        loadingSpinner.setVisibility(View.VISIBLE);

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String postUrl = sharedPrefs.getString(getString(R.string.searching_time_key),
                                                     getString(R.string.searching_time_default));

        ApiClient.getEarthquakeData(this, postUrl, new EarthquakeDataListener() {
            @Override
            public void onResponse(final ArrayList<Earthquake> earthquakes) {
                if (mTwoPane) {
                    final FragmentManager fragmentManager = getFragmentManager();
                    final DetailFragment fragment =
                            (DetailFragment) fragmentManager.findFragmentById(R.id.fragment_detail);

                    if (earthquakes != null && earthquakes.size() > 0) {
                        fragment.setupViews(earthquakes.get(0));
                        setupMap(earthquakes.get(0));
                    } else {
                        fragment.clearViews();
                    }
                }
                loadingSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(final Exception error) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(EarthquakeListActivity.this,
                               getString(R.string.volley_fetching_error_message),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.earthquake_list_activity_toolbar);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    private void showPromptActivateGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setMessage(getString(R.string.gps_needed));

        AlertDialog noInternetDialog = builder.create();

        noInternetDialog.show();
    }

    private void connectToGoogleApiClient() {
        if(mGoogleApiClient != null) {
            showGpsPrompt = true;
            mGoogleApiClient.connect();
        }
    }

    private void inflateMenu() {
        toolbar.inflateMenu(R.menu.menu_main);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_settings) {
                    final FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.main_frame_layout, settingsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    toolbar.setTitle(getString(R.string.title_activity_settings));
                    toolbar.getMenu().clear();
                    toolbar.setNavigationIcon(R.mipmap.ic_arrow_left);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            onBackPressed();
                        }
                    });

                    return true;
                }

                return true;
            }
        });
    }

    private boolean isLocationNull(){
        return mCurrentLocation == null;
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            bindPreferenceToValue(findPreference(getString(R.string.searching_time_key)));
            bindPreferenceToValue(findPreference(getString(R.string.magnitude_pref_key)));
            bindPreferenceToValue(findPreference(getString(R.string.searching_distance_key)));
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object value) {
            final String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                if (preference.getKey().equals(getString(R.string.searching_distance_key))) {
                    if (!((EarthquakeListActivity) getActivity()).isLocationNull()) {
                        final ListPreference listPreference = (ListPreference) preference;
                        final int prefIndex = listPreference.findIndexOfValue(stringValue);
                        listPreference.setValue(value.toString());
                        if (prefIndex >= 0) {
                            preference.setSummary(listPreference.getEntries()[prefIndex]);
                        }
                    }

                    ((EarthquakeListActivity) getActivity()).connectToGoogleApiClient();
                } else {
                    final ListPreference listPreference = (ListPreference) preference;
                    final int prefIndex = listPreference.findIndexOfValue(stringValue);
                    listPreference.setValue(value.toString());
                    if (prefIndex >= 0) {
                        preference.setSummary(listPreference.getEntries()[prefIndex]);
                        ((EarthquakeListActivity) getActivity()).fetchEarthquakeData();
                    }
                }
            }

            return true;
        }

        private void bindPreferenceToValue(final Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                final ListPreference listPreference = (ListPreference) preference;
                final int prefIndex = listPreference.findIndexOfValue(((ListPreference) preference).getValue());

                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }
}
