package com.almareng.earthquakemonitor.EqListScreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.almareng.earthquakemonitor.R;

import java.util.ArrayList;

public class EarthquakeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_list);

        final Toolbar mainToolbar = (Toolbar) findViewById(R.id.earthquake_list_activity_toolbar);

        mainToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mainToolbar);

        final ListView earthquakeList = (ListView) findViewById(R.id.earthquake_list);
        final ArrayList<Earthquake> earthquakes = new ArrayList<>();
        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(this, earthquakes);

        earthquakeList.setAdapter(earthquakeAdapter);
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
