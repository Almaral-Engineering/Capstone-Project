package com.almareng.earthquakemonitor.list;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;

import java.util.ArrayList;
import java.util.List;

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

        final Double magnitude = earthquake.getMagnitude();

        holder.magnitudeText.setText(String.valueOf(magnitude));
        holder.placeText.setText(earthquake.getPlace());

        if(magnitude >= 0 && magnitude <= 3.5){
            holder.magnitudeText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.placeText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.placeText.setAlpha(1f);

            holder.distanceText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        }
        else if(magnitude >= 6.5){
            holder.magnitudeText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.placeText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            holder.placeText.setAlpha(1f);

            holder.distanceText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }
        else{
            holder.magnitudeText.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.placeText.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.placeText.setAlpha(0.65f);

            holder.distanceText.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        String distanceToEarthquake = String.valueOf(earthquake.getDistanceToEpicenter());

        if (distanceToEarthquake == null || distanceToEarthquake.isEmpty()) {
            holder.distanceText.setVisibility(View.GONE);
        } else {
            holder.distanceText.setVisibility(View.VISIBLE);
            holder.distanceText.setText(distanceToEarthquake);

            if(distanceToEarthquake.contains(".")) {
                distanceToEarthquake = distanceToEarthquake.substring(0, distanceToEarthquake.lastIndexOf("."));
            }

            holder.distanceText.setText(String.format(context.getString(R.string.distance_format),
                                                      distanceToEarthquake));
        }

        return convertView;
    }

    public void updateEarthquakeList(final List<Earthquake> earthquakes) {
        this.earthquakes.clear();
        this.earthquakes.addAll(earthquakes);
        this.notifyDataSetChanged();
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
