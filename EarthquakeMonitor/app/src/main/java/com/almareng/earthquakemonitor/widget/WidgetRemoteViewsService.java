package com.almareng.earthquakemonitor.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.almareng.earthquakemonitor.R;
import com.almareng.earthquakemonitor.data.EqContract;
import com.almareng.earthquakemonitor.details.DetailActivity;
import com.almareng.earthquakemonitor.list.Earthquake;

public final class WidgetRemoteViewsService extends RemoteViewsService {
    // Database Columns
    public static final int COL_EQ_ID = 0;
    public static final int COL_EQ_MAGNITUDE = 1;
    public static final int COL_EQ_PLACE= 2;
    public static final int COL_EQ_TIME_DATE = 3;
    public static final int COL_EQ_LATITUDE = 4;
    public static final int COL_EQ_LONGITUDE = 5;
    public static final int COL_EQ_DEPTH = 6;
    public static final int COL_EQ_DISTANCE = 7;

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

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new EarthquakeRemoteViewsFactory();
    }

    public class EarthquakeRemoteViewsFactory implements RemoteViewsFactory {
        private Cursor data = null;

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            if (data != null) {
                data.close();
            }

            // This method is called by the app hosting the widget (e.g., the launcher)
            // However, our ContentProvider is not exported so it doesn't have access to the
            // data. Therefore we need to clear (and finally restore) the calling identity so
            // that calls use our process and permission
            final long identityToken = Binder.clearCallingIdentity();

            data = getContentResolver().query(EqContract.Entry.CONTENT_URI, null, null, null, null);

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getViewAt(final int position) {
            if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                return null;
            }

            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            final double magnitude = data.getDouble(COL_EQ_MAGNITUDE);
            final String place = data.getString(COL_EQ_PLACE);
            final String timeDate = data.getString(COL_EQ_TIME_DATE);
            final String lon = data.getString(COL_EQ_LONGITUDE);
            final String lat = data.getString(COL_EQ_LATITUDE);
            final String depth = data.getString(COL_EQ_DEPTH);
            final String distance = data.getString(COL_EQ_DISTANCE);

            views.setTextViewText(R.id.widget_magnitude_text, String.valueOf(magnitude));
            views.setTextViewText(R.id.widget_place_text, place);

            final Earthquake earthquake = new Earthquake(magnitude, place, timeDate, lon, lat, depth, distance);
            final Intent fillInIntent = new Intent();

            fillInIntent.putExtra(DetailActivity.EARTHQUAKE_KEY, earthquake);
            views.setOnClickFillInIntent(R.id.widget_list_item_layout, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(final int position) {
            if (data.moveToPosition(position)) {
                return data.getLong(COL_EQ_ID);
            }

            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    };
}
