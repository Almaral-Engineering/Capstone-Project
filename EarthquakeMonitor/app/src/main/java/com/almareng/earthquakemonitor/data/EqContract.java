package com.almareng.earthquakemonitor.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class EqContract {
    public static final String CONTENT_AUTHORITY = "com.almareng.earthquakemonitor";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EQ = "earthquakes";

    /* Inner class that defines the table contents of the coupons table*/
    public static final class Entry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EQ).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EQ;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EQ;

        // Table name
        public static final String TABLE_NAME = "earthquakes";

        public static final String COLUMN_EQ_MAGNITUDE = "magnitude";

        public static final String COLUMN_EQ_PLACE = "place";

        public static final String COLUMN_EQ_TIME_DATE = "time_date";

        public static final String COLUMN_EQ_LATITUDE = "latitude";

        public static final String COLUMN_EQ_LONGITUDE = "longitude";

        public static final String COLUMN_EQ_DEPTH = "depth";

        public static final String COLUMN_EQ_DISTANCE = "distance";

        public static Uri buildEqUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
