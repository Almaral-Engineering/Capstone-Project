package com.almareng.earthquakemonitor.details;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.almareng.earthquakemonitor.list.Earthquake;
import com.almareng.earthquakemonitor.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment {
    private TextView magnitudeText;
    private TextView dateText;
    private TextView timeText;
    private TextView longitudeText;
    private TextView latitudeText;
    private TextView depthText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        magnitudeText = (TextView) view.findViewById(R.id.fragment_detail_magnitude_text);
        dateText = (TextView) view.findViewById(R.id.fragment_detail_date_text);
        timeText = (TextView) view.findViewById(R.id.fragment_detail_time_text);
        longitudeText = (TextView) view.findViewById(R.id.fragment_detail_longitude_text);
        latitudeText = (TextView) view.findViewById(R.id.fragment_detail_latitude_text);
        depthText = (TextView) view.findViewById(R.id.fragment_detail_depth_text);
    }

    public void setupViews(final Earthquake earthquake) {
        if (earthquake != null) {
            final Long unixSeconds = Long.parseLong(earthquake.getTimeAndDate());
            final Date date = new Date(unixSeconds);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.earthquake_detail_date_format),
                                                                     Locale.getDefault());
            final SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.earthquake_detail_time_format),
                                                                     Locale.getDefault());
            final String formattedDate = dateFormat.format(date);
            final String formattedTime = timeFormat.format(date);

            magnitudeText.setText(String.valueOf(earthquake.getMagnitude()));
            dateText.setText(formattedDate);
            timeText.setText(formattedTime);
            longitudeText.setText(earthquake.getLongitude());
            latitudeText.setText(earthquake.getLatitude());
            depthText.setText(earthquake.getDepth());
        }
    }
}
