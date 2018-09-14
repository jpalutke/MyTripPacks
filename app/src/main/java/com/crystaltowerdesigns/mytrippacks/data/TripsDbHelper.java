package com.crystaltowerdesigns.mytrippacks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.crystaltowerdesigns.mytrippacks.data.TripsContract.TripEntry;
import com.crystaltowerdesigns.mytrippacks.data.TripsContract.StopEntry;

/**
 * Database helper for inventory.
 * Handles database creation and version control.
 */
class TripsDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "trips.db";

    /**
     * Database version. Increment with each new version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TripsDbHelper}.
     *
     * @param context app context
     */
    public TripsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Database creation the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO: verify validation against any table changes

        String SQL_CREATE_TRIPS_TABLE = "CREATE TABLE " + TripEntry.TABLE_NAME + " ("
                + TripEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TripEntry.COLUMN_TRIP_NUMBER + " TEXT NOT NULL, "
                + TripEntry.COLUMN_FROM_TO + " TEXT NOT NULL, "
                + TripEntry.COLUMN_RECEIVED_DATE + " TEXT NOT NULL, "
                + TripEntry.COLUMN_SUBMITTED_DATE + " TEXT, "
                + TripEntry.COLUMN_STATE + " INTEGER NOT NULL, "
                + TripEntry.COLUMN_HUB_INITIAL + " INTEGER NOT NULL, "
                + TripEntry.COLUMN_HUB_END + " INTEGER NOT NULL);";
        try {
            db.execSQL(SQL_CREATE_TRIPS_TABLE);
        } catch (Exception e) {
            Log.v("SQL ERROR", e.toString());
        }

        // TODO: verify validation against any table changes

        String SQL_CREATE_STOPS_TABLE = "CREATE TABLE " + StopEntry.TABLE_NAME + " ("
                + StopEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StopEntry.COLUMN_TRIP_NUMBER + " TEXT NOT NULL, "
                + StopEntry.COLUMN_LOCATION + " TEXT NOT NULL, "
                + StopEntry.COLUMN_HUB + " INTEGER NOT NULL, "
                + StopEntry.COLUMN_SORT_INDEX + " INTEGER NOT NULL, "
                + StopEntry.COLUMN_DATE_COMPLETED + " TEXT NOT NULL);";
        try {
            db.execSQL(SQL_CREATE_STOPS_TABLE);
        } catch (Exception e) {
            Log.v("SQL ERROR", e.toString());
        }

    }

    /**
     * Called when a database upgrade is needed.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade version by wiping the database tables
        if (newVersion != oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TripEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + StopEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}