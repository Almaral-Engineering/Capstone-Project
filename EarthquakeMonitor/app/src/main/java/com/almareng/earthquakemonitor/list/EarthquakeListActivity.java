package com.almareng.earthquakemonitor.list;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.api.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class EarthquakeListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final String EARTHQUAKE_KEY = "earthquake";
    public static final String PREFERENCE_LONGITUDE = "preference_longitude";
    public static final String PREFERENCE_LATITUDE = "preference_latitude";
    public static final String LOCATION_PREFS = "location_preference";

    private SettingsFragment settingsFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private boolean showGpsPrompt = false;
    private Toolbar toolbar;
    private EarthquakeListFragment earthquakeListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_list);

        setupToolbar();
        fetchEarthquakeData(false);
        inflateMenu();

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        settingsFragment = new SettingsFragment();
    }

    private void fetchEarthquakeData(final boolean reload) {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String postUrl = sharedPrefs.getString(getString(R.string.searching_time_key),
                                                     getString(R.string.searching_time_default));

        ApiClient.getEarthquakeData(this, postUrl, new EarthquakeDataListener() {
            @Override
            public void onResponse(final ArrayList<Earthquake> earthquakes) {
                if (reload) {
                    earthquakeListFragment.reloadEarthquakes(earthquakes);
                } else {
                    earthquakeListFragment = EarthquakeListFragment.newInstance(earthquakes);
                    final FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.add(R.id.main_frame_layout, earthquakeListFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }

            @Override
            public void onErrorResponse(final Exception error) {

            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

            if(showGpsPrompt) {
                fetchEarthquakeData(true);
            }
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

    private void connectToGoogleApiClient(){
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
                    toolbar.setNavigationIcon(R.drawable.ic_action_back);
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
                        ((EarthquakeListActivity) getActivity()).fetchEarthquakeData(true);
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
