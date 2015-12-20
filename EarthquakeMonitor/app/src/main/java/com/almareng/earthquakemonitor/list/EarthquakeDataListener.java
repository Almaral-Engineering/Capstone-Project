package com.almareng.earthquakemonitor.list;

import java.util.ArrayList;

public interface EarthquakeDataListener {
    void onResponse(final ArrayList<Earthquake> earthquakes);
    void onErrorResponse(final Exception error);
}
