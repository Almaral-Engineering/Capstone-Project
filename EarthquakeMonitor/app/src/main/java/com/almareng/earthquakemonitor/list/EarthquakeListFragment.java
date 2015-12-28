package com.almareng.earthquakemonitor.list;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.data.EqContract;
import com.almareng.earthquakemonitor.details.DetailActivity;

public final class EarthquakeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Database Columns
    public static final int COL_EQ_MAGNITUDE = 1;
    public static final int COL_EQ_PLACE= 2;
    public static final int COL_EQ_TIME_DATE = 3;
    public static final int COL_EQ_LATITUDE = 4;
    public static final int COL_EQ_LONGITUDE = 5;
    public static final int COL_EQ_DEPTH = 6;
    public static final int COL_EQ_DISTANCE = 7;

    private static final int EARTHQUAKE_LOADER_ID = 0;

    private static final String[] ENTRY_COLUMNS = {
            EqContract.Entry._ID,
            EqContract.Entry.COLUMN_EQ_MAGNITUDE,
            EqContract.Entry.COLUMN_EQ_PLACE,
            EqContract.Entry.COLUMN_EQ_TIME_DATE,
            EqContract.Entry.COLUMN_EQ_LATITUDE,
            EqContract.Entry.COLUMN_EQ_LONGITUDE,
            EqContract.Entry.COLUMN_EQ_DEPTH,
            EqContract.Entry.COLUMN_EQ_DISTANCE
    };

    private EarthquakeAdapter earthquakeAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView earthquakeList = (ListView) view.findViewById(R.id.earthquake_list);
        final TextView emptyView = (TextView) view.findViewById(R.id.earthquake_list_empty_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.earthquake_list_refresher);
        earthquakeAdapter = new EarthquakeAdapter(getActivity(), null, 0);

        earthquakeList.setEmptyView(emptyView);
        earthquakeList.setAdapter(earthquakeAdapter);
        earthquakeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Cursor cursor = earthquakeAdapter.getCursor();

                if (cursor != null && cursor.moveToPosition(position)) {
                    final Earthquake earthquake = new Earthquake(cursor.getDouble(COL_EQ_MAGNITUDE),
                                                                 cursor.getString(COL_EQ_PLACE),
                                                                 cursor.getString(COL_EQ_TIME_DATE),
                                                                 cursor.getString(COL_EQ_LONGITUDE),
                                                                 cursor.getString(COL_EQ_LATITUDE),
                                                                 cursor.getString(COL_EQ_DEPTH),
                                                                 cursor.getString(COL_EQ_DISTANCE));

                    final Intent detailIntent = new Intent(getActivity(), DetailActivity.class);

                    detailIntent.putExtra(EarthquakeListActivity.EARTHQUAKE_KEY, earthquake);
                    startActivity(detailIntent);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeListFragment.this);
            }
        });

        getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new CursorLoader(getActivity(), EqContract.Entry.CONTENT_URI, ENTRY_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        earthquakeAdapter.swapCursor(data);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        earthquakeAdapter.swapCursor(null);
    }
}
