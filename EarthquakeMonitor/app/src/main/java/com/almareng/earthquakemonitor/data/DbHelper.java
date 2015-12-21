package com.almareng.earthquakemonitor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "earthquake.db";

    public DbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_COUPON_TABLE = "CREATE TABLE " + EqContract.Entry.TABLE_NAME + " (" +
                EqContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EqContract.Entry.COLUMN_EQ_MAGNITUDE + " REAL NOT NULL, " +
                EqContract.Entry.COLUMN_EQ_PLACE + " TEXT NOT NULL, " +
                EqContract.Entry.COLUMN_EQ_TIME_DATE + " TEXT NOT NULL," +
                EqContract.Entry.COLUMN_EQ_LATITUDE + " TEXT NOT NULL," +
                EqContract.Entry.COLUMN_EQ_LONGITUDE + " TEXT NOT NULL," +
                EqContract.Entry.COLUMN_EQ_DEPTH + " TEXT NOT NULL," +
                EqContract.Entry.COLUMN_EQ_DISTANCE + " TEXT);";

        db.execSQL(SQL_CREATE_COUPON_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,final int oldVersion,final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EqContract.Entry.TABLE_NAME);
        onCreate(db);
    }
}
