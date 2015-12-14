package com.almareng.earthquakemonitor.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;

import java.util.ArrayList;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private final Context context;
    private final ArrayList<Earthquake> earthquakes;

    public EarthquakeAdapter(Context context, ArrayList<Earthquake> earthquakes) {
        super(context, R.layout.earthquake_list_item);
        this.context = context;
        this.earthquakes = earthquakes;
    }

    @Override
    public int getCount() {
        return earthquakes.size();
    }

    @Override
    public Earthquake getItem(int position) {
        return earthquakes.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Earthquake earthquake = earthquakes.get(position);
        final EarthquakeViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.earthquake_list_item, parent, false);
            holder = new EarthquakeViewHolder(convertView);
            convertView.setTag(holder);
        } else{
            holder = (EarthquakeViewHolder) convertView.getTag();
        }

        holder.magnitudeText.setText(earthquake.getMagnitude());
        holder.placeText.setText(earthquake.getPlace());

        return convertView;
    }

    private class EarthquakeViewHolder {
        public TextView magnitudeText;
        public TextView placeText;
        public TextView distanceText;

        public EarthquakeViewHolder(View convertView) {
            magnitudeText = (TextView) convertView.findViewById(R.id.earthquake_list_item_magnitude_text);
            placeText = (TextView) convertView.findViewById(R.id.earthquake_list_item_place_text);
            distanceText = (TextView) convertView.findViewById(R.id.earthquake_list_item_distance_text);
        }
    }
}
