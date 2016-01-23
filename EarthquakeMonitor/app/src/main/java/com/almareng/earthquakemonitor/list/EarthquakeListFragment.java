package com.almareng.earthquakemonitor.list;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.data.EqContract;

public final class EarthquakeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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

    public interface EarthquakeSelectedListener {
        void onEarthquakeSelected(final View view, final Earthquake earthquake);
    }

    private EarthquakeAdapter earthquakeAdapter;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView earthquakeList;
    private boolean mUseTodayLayout;
    private boolean mAutoSelectView;
    private int mChoiceMode;

    @Override
    public void onInflate(final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EarthquakeListFragment, 0, 0);

        mChoiceMode = a.getInt(R.styleable.EarthquakeListFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        mAutoSelectView = a.getBoolean(R.styleable.EarthquakeListFragment_autoSelectView, false);

        a.recycle();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);

        earthquakeList = (RecyclerView) view.findViewById(R.id.earthquake_recyclerview);
        final TextView emptyView = (TextView) view.findViewById(R.id.earthquake_list_empty_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.earthquake_list_refresher);

        earthquakeList.setLayoutManager(new LinearLayoutManager(getActivity()));

        earthquakeAdapter = new EarthquakeAdapter(getActivity(), new EarthquakeAdapter.OnItemClickListener() {
            @Override
            public void onClick(final Earthquake earthquake, final View view) {
                ((EarthquakeSelectedListener)getActivity()).onEarthquakeSelected(view, earthquake);
            }
        }, emptyView, mChoiceMode);

        earthquakeList.setAdapter(earthquakeAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeListFragment.this);
            }
        });

        getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);

        if (savedInstanceState != null) {
            earthquakeAdapter.onRestoreInstanceState(savedInstanceState);
        }

        earthquakeAdapter.setUseTodayLayout(mUseTodayLayout);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new CursorLoader(getActivity(), EqContract.Entry.CONTENT_URI, ENTRY_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        earthquakeAdapter.swapCursor(data);
        refreshLayout.setRefreshing(false);

        if (data.getCount() > 0 ) {
            earthquakeList.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // Since we know we're going to get items, we keep the listener around until
                    // we see Children.
                    if (earthquakeList.getChildCount() > 0) {
                        earthquakeList.getViewTreeObserver().removeOnPreDrawListener(this);
                        int itemPosition = earthquakeAdapter.getSelectedItemPosition();
                        if ( RecyclerView.NO_POSITION == itemPosition ) itemPosition = 0;
                        RecyclerView.ViewHolder vh = earthquakeList.findViewHolderForAdapterPosition(itemPosition);
                        if (null != vh && mAutoSelectView) {
                            earthquakeAdapter.selectView( vh );
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        earthquakeAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        earthquakeAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public void setUseTodayLayout(final boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (earthquakeAdapter != null) {
            earthquakeAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    private void setupToolbar(final View view) {
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.earthquake_list_fragment_toolbar);

        toolbar.setTitle(getString(R.string.app_name));
        ((EarthquakeListActivity) getActivity()).setSupportActionBar(toolbar);
    }
}
