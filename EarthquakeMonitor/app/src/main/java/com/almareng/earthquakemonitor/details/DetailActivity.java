package com.almareng.earthquakemonitor.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.list.EarthquakeListActivity;
import com.almareng.earthquakemonitor.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Bundle extras = getIntent().getExtras();
        final Earthquake earthquake = extras.getParcelable(EarthquakeListActivity.EARTHQUAKE_KEY);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final DetailFragment detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.fragment_detail);

        detailFragment.setupViews(earthquake);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
}
