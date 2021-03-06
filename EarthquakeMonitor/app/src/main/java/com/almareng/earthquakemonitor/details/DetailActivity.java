package com.almareng.earthquakemonitor.details;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.Utils;
import com.almareng.earthquakemonitor.list.Earthquake;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity {
    public static final String EARTHQUAKE_KEY = "earthquake";

    private Earthquake earthquake;

    public static void newIntent(final Activity activity, final View transitionView, final Earthquake earthquake) {
        final Intent detailIntent = new Intent(activity, DetailActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, transitionView, transitionView.getTransitionName());

            detailIntent.putExtra(EARTHQUAKE_KEY, earthquake);
            activity.startActivity(detailIntent, options.toBundle());
        } else {
            activity.startActivity(detailIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_detail);

        final Toolbar detailToolbar = (Toolbar) findViewById(R.id.activity_detail_toolbar);

        detailToolbar.setNavigationIcon(R.mipmap.ic_arrow_left);
        setSupportActionBar(detailToolbar);

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Bundle extras = getIntent().getExtras();
        earthquake = extras.getParcelable(EARTHQUAKE_KEY);

        setupViews();

        setupMap(earthquake);
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Slide transition = new Slide(Gravity.TOP);
            transition.addTarget(R.id.fragment_detail);
            transition.setInterpolator(AnimationUtils.loadInterpolator(this,
                                                                       android.R.interpolator.linear_out_slow_in));

            getWindow().setEnterTransition(transition);
        }
    }

    private void setupViews() {
        final FragmentManager fragmentManager = getFragmentManager();
        final DetailFragment detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.fragment_detail);

        if (earthquake != null) {
            detailFragment.setupViews(earthquake);
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        final MenuItem shareItem = menu.findItem(R.id.action_share);
        final ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        mShareActionProvider.setShareIntent(getDefaultIntent());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent getDefaultIntent() {
        final String timeAndDate = earthquake.getTimeAndDate();
        final String dateFormat = getString(R.string.earthquake_detail_date_format);
        final String timeFormat = getString(R.string.earthquake_detail_time_format);
        final String formattedDate = Utils.getFormattedDateTime(timeAndDate, dateFormat);
        final String formattedTime = Utils.getFormattedDateTime(timeAndDate, timeFormat);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.sharing_intent_message),
                                                              earthquake.getMagnitude(),
                                                              formattedDate,
                                                              formattedTime,
                                                              earthquake.getPlace(),
                                                              earthquake.getLatitude(),
                                                              earthquake.getLongitude()));
        return shareIntent;
    }
}
