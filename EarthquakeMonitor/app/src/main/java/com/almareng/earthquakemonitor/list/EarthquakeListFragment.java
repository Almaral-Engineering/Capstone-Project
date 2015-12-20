package com.almareng.earthquakemonitor.list;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.details.DetailActivity;

import java.util.ArrayList;

public final class EarthquakeListFragment extends Fragment {
    private static final String EARTHQUAKE_LIST_KEY = "earthquake_list";

    public static EarthquakeListFragment newInstance(final ArrayList<Earthquake> earthquakes) {

        final Bundle args = new Bundle();

        args.putParcelableArrayList(EARTHQUAKE_LIST_KEY, earthquakes);

        final EarthquakeListFragment fragment = new EarthquakeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView earthquakeList = (ListView) view.findViewById(R.id.earthquake_list);
        final ArrayList<Earthquake> earthquakes = getArguments().getParcelableArrayList(EARTHQUAKE_LIST_KEY);
        final EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(getActivity(), earthquakes);

        earthquakeList.setAdapter(earthquakeAdapter);
        earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Earthquake chosenEarthquake = earthquakeAdapter.getItem(position);
                final Intent intent = new Intent(getActivity(), DetailActivity.class);

                intent.putExtra(EarthquakeListActivity.EARTHQUAKE_KEY, chosenEarthquake);
                startActivity(intent);
            }
        });
    }
}
