package com.almareng.earthquakemonitor;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.almareng.earthquakemonitor.data.EqContract;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(EqContract.Entry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(EqContract.Entry.CONTENT_URI, null, null, null, null);

        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {
        final ContentValues testValues = TestDb.createEntryValues();
        final Uri locationUri = mContext.getContentResolver().insert(EqContract.Entry.CONTENT_URI, testValues);
        final long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(EqContract.Entry.CONTENT_URI,
                                                                  null, // leaving "columns" null just returns all the columns.
                                                                  null, // cols for "where" clause
                                                                  null, // values for "where" clause
                                                                  null);// sort order

        TestDb.validateCursor(cursor, testValues);

        cursor = mContext.getContentResolver().query(EqContract.Entry.buildEqUri(locationRowId),
                                                     null, // leaving "columns" null just returns all the columns.
                                                     null, // cols for "where" clause
                                                     null, // values for "where" clause
                                                     null);  // sort order

        TestDb.validateCursor(cursor, testValues);
    }

    public void testGetType() {
        // content://packageName/earthquakes
        final String type = mContext.getContentResolver().getType(EqContract.Entry.CONTENT_URI);
        // vnd.android.cursor.dir/packageName/earthquakes
        assertEquals(EqContract.Entry.CONTENT_TYPE, type);
    }

    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    static ContentValues createEqsValues(double eqMag) {
        final String testEqPlace = "Arizona";
        final String testEqTimeAndDate = "112365478952";
        final String testEqLat = "24.99013";
        final String testEqLon = "-107.136584";
        final String testEqDep = "0.2";
        final ContentValues testValues = new ContentValues();

        testValues.put(EqContract.Entry.COLUMN_EQ_MAGNITUDE, eqMag);
        testValues.put(EqContract.Entry.COLUMN_EQ_PLACE, testEqPlace);
        testValues.put(EqContract.Entry.COLUMN_EQ_TIME_DATE, testEqTimeAndDate);
        testValues.put(EqContract.Entry.COLUMN_EQ_LATITUDE, testEqLat);
        testValues.put(EqContract.Entry.COLUMN_EQ_LONGITUDE, testEqLon);
        testValues.put(EqContract.Entry.COLUMN_EQ_DEPTH, testEqDep);

        return testValues;
    }

    public void testUpdateAndReadEqs() {
        insertEqData();

        // Make an update to one value.
        final double newEqMagnitude = 2.8;

        final ContentValues eqUpdate = new ContentValues();
        eqUpdate.put(EqContract.Entry.COLUMN_EQ_MAGNITUDE, newEqMagnitude);

        mContext.getContentResolver().update(EqContract.Entry.CONTENT_URI, eqUpdate, null, null);

        final Cursor eqCursor = mContext.getContentResolver().query(EqContract.Entry.CONTENT_URI,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    null);

        final ContentValues eqAltered = createEqsValues(2.8);

        eqAltered.put(EqContract.Entry.COLUMN_EQ_MAGNITUDE, newEqMagnitude);
        TestDb.validateCursor(eqCursor, eqAltered);
    }

    public void insertEqData() {
        final ContentValues kalamazooLocationValues = createEqsValues(3.5);
        final Uri locationInsertUri = mContext.getContentResolver().insert(EqContract.Entry.CONTENT_URI,
                                                                           kalamazooLocationValues);

        assertTrue(locationInsertUri != null);
    }
}
