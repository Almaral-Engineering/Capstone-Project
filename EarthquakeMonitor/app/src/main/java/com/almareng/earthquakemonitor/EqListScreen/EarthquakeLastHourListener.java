package com.almareng.earthquakemonitor.EqListScreen;

import java.util.ArrayList;

public interface EarthquakeLastHourListener {
    void onResponse(final ArrayList<Earthquake> earthquakes);
    void onErrorResponse(final Exception error);
}
