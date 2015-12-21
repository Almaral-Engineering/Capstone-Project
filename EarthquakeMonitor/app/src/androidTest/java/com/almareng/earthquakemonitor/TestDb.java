package com.almareng.earthquakemonitor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.almareng.earthquakemonitor.data.DbHelper;
import com.almareng.earthquakemonitor.data.EqContract;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);

        final SQLiteDatabase db = new DbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb(){

        final DbHelper dbHelper = new DbHelper(mContext);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues testValues = createEntryValues();
        final long entryRowId = db.insert(EqContract.Entry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(entryRowId != -1);

        final Cursor cursor = db.query(EqContract.Entry.TABLE_NAME,  // Table to Query
                                       null, // all columns
                                       null, // Columns for the "where" clause
                                       null, // Values for the "where" clause
                                       null, // columns to group by
                                       null, // columns to filter by row groups
                                       null); // sort order

        validateCursor(cursor, testValues);
    }

    static ContentValues createEntryValues() {
        final double testEqMag = 1.5;
        final String testEqPlace = "Arizona";
        final String testEqTimeAndDate = "112365478952";
        final String testEqLat = "24.99013";
        final String testEqLon = "-107.136584";
        final String testEqDep = "0.2";
        final ContentValues testValues = new ContentValues();

        testValues.put(EqContract.Entry.COLUMN_EQ_MAGNITUDE, testEqMag);
        testValues.put(EqContract.Entry.COLUMN_EQ_PLACE, testEqPlace);
        testValues.put(EqContract.Entry.COLUMN_EQ_TIME_DATE, testEqTimeAndDate);
        testValues.put(EqContract.Entry.COLUMN_EQ_LATITUDE, testEqLat);
        testValues.put(EqContract.Entry.COLUMN_EQ_LONGITUDE, testEqLon);
        testValues.put(EqContract.Entry.COLUMN_EQ_DEPTH, testEqDep);

        return testValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        final Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            final String columnName = entry.getKey();
            final int idx = valueCursor.getColumnIndex(columnName);

            assertFalse(idx == -1);

            final String expectedValue = entry.getValue().toString();

            assertEquals(expectedValue, valueCursor.getString(idx));
        }

        valueCursor.close();
    }
}
