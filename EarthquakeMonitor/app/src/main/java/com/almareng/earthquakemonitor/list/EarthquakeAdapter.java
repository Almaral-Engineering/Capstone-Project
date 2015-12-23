package com.almareng.earthquakemonitor.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;

public class EarthquakeAdapter extends CursorAdapter {
    public EarthquakeAdapter(final Context context, final Cursor cursor, final int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.earthquake_list_item, parent, false);
        final EarthquakeViewHolder eqHolder = new EarthquakeViewHolder(view);

        view.setTag(eqHolder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final EarthquakeViewHolder holder = new EarthquakeViewHolder(view);

        final Double magnitude = cursor.getDouble(EarthquakeListActivity.COL_EQ_MAGNITUDE);

        holder.magnitudeText.setText(String.valueOf(magnitude));
        holder.placeText.setText(cursor.getString(EarthquakeListActivity.COL_EQ_PLACE));

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

        String distanceToEarthquake = String.valueOf(cursor.getDouble(EarthquakeListActivity.COL_EQ_DISTANCE));

        if (distanceToEarthquake.isEmpty()) {
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
