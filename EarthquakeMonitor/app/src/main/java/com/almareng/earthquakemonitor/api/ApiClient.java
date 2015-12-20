package com.almareng.earthquakemonitor.api;

import android.content.Context;
import android.util.Log;

import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.list.EarthquakeDataListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiClient {
    private static final String BASE_URL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/";
    private static final String LAST_HOUR_DATA = "all_hour.geojson";
    private static final String LAST_DAY_DATA = "all_day.geojson";
    private static final String LAST_WEEK_DATA = "all_week.geojson";
    private static final String HOUR_PATH = "hour";
    private static final String DAY_PATH = "day";
    private static final String WEEK_PATH = "week";

    private static final String FEATURES= "features";
    private static final String PROPERTIES = "properties";
    private static final String GEOMETRY = "geometry";
    private static final String COORDINATES = "coordinates";
    private static final String MAGNITUDE = "mag";
    private static final String PLACE = "place";
    private static final String TIME_DATE = "time";

    public static void getEarthquakeData(final Context context, String url, final EarthquakeDataListener listener) {
        switch (url) {
            case "hour":
                url = LAST_HOUR_DATA;
                break;
            case "day":
                url = LAST_DAY_DATA;
                break;
            case "week":
                url = LAST_WEEK_DATA;
                break;
        }

        final String path = BASE_URL + url;
        Log.d("MANZANA", path);
        final JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                final ArrayList<Earthquake> earthquakes = parseJsonToEarthquake(response);

                if (earthquakes == null) {
                    listener.onErrorResponse(new Exception());
                } else {
                    listener.onResponse(earthquakes);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                listener.onErrorResponse(error);
            }
        });

        queueRequest(context, jsonObjectRequest);
    }

    private static ArrayList<Earthquake> parseJsonToEarthquake(final JSONObject jsonObject) {
        final ArrayList<Earthquake> earthquakes = new ArrayList<>();
        final JSONArray features;

        try {
            features = jsonObject.getJSONArray(FEATURES);
        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < features.length(); i++) {
            final String magnitude;
            final String place;
            final String timeDate;
            final String longitude;
            final String latitude;
            final String depth;

            try {
                final JSONObject feature = (JSONObject) features.get(i);
                final JSONObject properties = feature.getJSONObject(PROPERTIES);
                final JSONObject geometry = feature.getJSONObject(GEOMETRY);
                final JSONArray coordinates = geometry.getJSONArray(COORDINATES);
                magnitude = properties.getString(MAGNITUDE);
                place = properties.getString(PLACE);
                timeDate = properties.getString(TIME_DATE);
                longitude = coordinates.getString(0);
                latitude = coordinates.getString(1);
                depth = coordinates.getString(2);
            } catch (final JSONException e) {
                e.printStackTrace();
                return null;
            }
            
            earthquakes.add(new Earthquake(magnitude, place, timeDate, longitude, latitude, depth));
        }

        return earthquakes;
    }

    private static void queueRequest(final Context context, final Request<?> request) {
        request.setTag(context);
        VolleyHelper.getInstance(context).getRequestQueue().add(request);
    }
}
