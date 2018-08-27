package com.crystaltowerdesigns.mytrippacks.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Trip Pack app.
 */
public final class TripsContract {
    /**
     * "CONTENT_AUTHORITY" using the package name for the app, it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.crystaltowerdesigns.mytrippacks";

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.crystaltowerdesigns.mytrippacks/ is a valid path for
     * looking at trip pack data.
     */
    public static final String PATH_TRIPS = "mytrippacks";

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.crystaltowerdesigns.mystops/ is a valid path for
     * looking at stop data.
     */
    public static final String PATH_STOPS = "mystops";

    /**
     * CONTENT_AUTHORITY is used for the base URI's to contact the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Constructor to prevent accidentally instantiating the contract class
    private TripsContract() {
        throw new AssertionError("No instances for you!");
    }

    /**
     * Trips Table Definition
     * {@link BaseColumns}
     * Inner class that defines constant values for the trip pack table.
     * Each entry in the table represents a single trip pack item.
     */
    public static final class TripEntry implements BaseColumns {

        /**
         * The content URI to access the trip pack data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRIPS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trip pack items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single trip pack item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        /**
         * Name of database table for trip pack items
         */
        @SuppressWarnings("SpellCheckingInspection")
        public final static String TABLE_NAME = "trips";

        /**
         * Unique ID number for the trip pack item (only use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * The Trip Number
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_TRIP_NUMBER = "trip_number";

        /**
         * from_to, Trip Summary (From and to locations)
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_FROM_TO = "from_to";

        /**
         * hub_start is the hub reading upon start of trip.
         * Normally the same as the hub_end of the prior trip
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HUB_INITIAL = "hub_start";

        /**
         * hub_end is the hub reading upon end of trip.
         * normally the same as the last valid hub_#.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HUB_END = "hub_end";


        public final static String COLUMN_STATE = "state";
        public final static String COLUMN_SUBMITTED_DATE = "submitted_date";
        public final static String COLUMN_RECEIVED_DATE = "received_date";

        public final static int STATE_ASSIGNED = 100;
        public final static int STATE_OPEN = 101;
        public final static int STATE_CLOSED = 102;
        public final static int STATE_SUBMITTED = 103;
    }

    /**
     * Stops Table Definition
     * {@link BaseColumns}
     * Inner class that defines constant values for the trip pack table.
     * Each entry in the table represents a single trip pack item.
     */
    public static final class StopEntry implements BaseColumns {

        /**
         * The content URI to access the trip pack data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOPS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trip pack items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOPS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single trip pack item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOPS;

        /**
         * Name of database table for trip pack items
         */
        @SuppressWarnings("SpellCheckingInspection")
        public final static String TABLE_NAME = "stops";

        /**
         * Unique ID number for the trip pack item (only use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Trip Number the stop belongs to.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TRIP_NUMBER = "trip_number";

        /**
         * Stop Location, the location of the stop.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_LOCATION = "location";

        /**
         * stop_index controls the order of stops for each trip.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SORT_INDEX = "stop_index";

        /**
         * arrival_hub is the hub value upon stop arrival
         * defaults to zero.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_HUB = "arrival_hub";

        /**
         * date_completed is the date the stop was completed.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DATE_COMPLETED = "date_completed";
    }
}

