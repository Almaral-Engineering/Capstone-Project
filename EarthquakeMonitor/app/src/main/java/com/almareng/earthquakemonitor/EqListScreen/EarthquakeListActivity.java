package com.almareng.earthquakemonitor.EqListScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.almareng.earthquakemonitor.DetailScreen.DetailActivity;
import com.almareng.earthquakemonitor.R;

import java.util.ArrayList;

public class EarthquakeListActivity extends AppCompatActivity {
    public static final String EARTHQUAKE_KEY = "earthquake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_list);

        final Toolbar mainToolbar = (Toolbar) findViewById(R.id.earthquake_list_activity_toolbar);

        mainToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mainToolbar);

        final ListView earthquakeList = (ListView) findViewById(R.id.earthquake_list);
        final ArrayList<Earthquake> earthquakes = new ArrayList<>();

        earthquakes.add(new Earthquake("5.0", "5 km NW of Los Angeles, California", "1450038310130", "-149.8008", "61.5592", "54.6"));
        earthquakes.add(new Earthquake("2.3", "100 km E of Honolulu, Hawaii", "1450036311000", "-122.8130035", "38.8069992", "0.59"));

        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(this, earthquakes);

        earthquakeList.setAdapter(earthquakeAdapter);
        earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Earthquake chosenEarthquake = earthquakeAdapter.getItem(position);

                final Intent intent = new Intent(EarthquakeListActivity.this, DetailActivity.class);
                intent.putExtra(EARTHQUAKE_KEY, chosenEarthquake);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
