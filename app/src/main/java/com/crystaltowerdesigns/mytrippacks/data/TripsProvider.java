/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crystaltowerdesigns.mytrippacks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crystaltowerdesigns.mytrippacks.data.TripsContract.StopEntry;
import com.crystaltowerdesigns.mytrippacks.data.TripsContract.TripEntry;

import static com.crystaltowerdesigns.mytrippacks.data.TripsContract.TripEntry.*;
import static com.crystaltowerdesigns.mytrippacks.data.Validation.IS_DATE;
import static com.crystaltowerdesigns.mytrippacks.data.Validation.NOT_NULL;
import static com.crystaltowerdesigns.mytrippacks.data.Validation.isOneOf;
import static com.crystaltowerdesigns.mytrippacks.data.Validation.isValid;

/**
 * {@link ContentProvider} for Trip Pack app.
 */
public class TripsProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = TripsProvider.class.getSimpleName();

    /**
     * URI matcher codes for the content URI's
     */
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final int STOPS = 102;
    private static final int STOP_ID = 103;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(TripsContract.CONTENT_AUTHORITY, TripsContract.PATH_TRIPS, ITEMS);
        sUriMatcher.addURI(TripsContract.CONTENT_AUTHORITY, TripsContract.PATH_TRIPS + "/#", ITEM_ID);
        sUriMatcher.addURI(TripsContract.CONTENT_AUTHORITY, TripsContract.PATH_STOPS, STOPS);
        sUriMatcher.addURI(TripsContract.CONTENT_AUTHORITY, TripsContract.PATH_STOPS + "/#", STOP_ID);
    }

    /**
     * Database helper object
     */
    private TripsDbHelper mDbHelper;

    /**
     * @param context     Necessary context
     * @param TABLE_NAME  The table to retrieve the maximum value from
     * @param column_name The column name for which you wish the maximum value returned
     *
     * @return String value containing the result
     */
    public static String getMaximum(Context context, String TABLE_NAME, String column_name) {// use the data type of the column
        TripsDbHelper mDbHelper2;
        mDbHelper2 = new TripsDbHelper(context);
        SQLiteDatabase database = mDbHelper2.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{"MAX(" + column_name + ") AS MAX"}, null, null, null, null, null);
        String data = null;
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("MAX");
            data = cursor.getString(index);
            cursor.close();
        }
        database.close();
        return data;
    }

    /**
     * @param context     Necessary context
     * @param TABLE_NAME  The table to retrieve the minimum value from
     * @param column_name The column name for which you wish the minimum value returned
     *
     * @return String value containing the result
     */
    public static String getMinimum(Context context, String TABLE_NAME, String column_name) {// use the data type of the column
        TripsDbHelper mDbHelper2;
        mDbHelper2 = new TripsDbHelper(context);
        SQLiteDatabase database = mDbHelper2.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, new String[]{"MIN(" + column_name + ") AS MIN"}, null, null, null, null, null);
        String data = null;
        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("MIN");
            data = cursor.getString(index);
            cursor.close();
        }
        database.close();
        return data;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TripsDbHelper(getContext());
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Match URI it's code value
        int match = sUriMatcher.match(uri);
        // Extract the ID and set selection/selectionArgs if needed
        // Query the appropriate table with the given parameters
        switch (match) {
            case ITEMS:
                cursor = database.query(TripEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TripEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STOPS:
                cursor = database.query(StopEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STOP_ID:
                selection = StopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StopEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case STOPS:
                return insertItem(match, uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a trip item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertItem(int itemType, Uri uri, ContentValues values) {
        Long id = Long.valueOf(-1);
        // If there are no values to insert or there are invalid field contents, then don't try to update the database
        if (validateFields(values)) {
            Log.v("VALIDATION", "passes");
        }
        if (values.size() != 0 && validateFields(values)) {

            // Get writable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            switch (itemType) {
                case ITEMS:
                    id = database.insert(TripEntry.TABLE_NAME, null, values);
                    break;
                case STOPS:
                    id = database.insert(StopEntry.TABLE_NAME, null, values);
                    break;
            }
        }

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the trip content URI
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
            case STOPS:
                return updateItem(match, uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                // Extract the ID and set selection/selectionArgs
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(match, uri, contentValues, selection, selectionArgs);
            case STOP_ID:
                // Extract the ID and set selection/selectionArgs
                selection = StopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(match, uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.format("Update is not supported for %s", uri));
        }
    }

    /**
     * Update items specified in the selection and selection arguments.
     * Return the updated row count.
     */
    private int updateItem(int itemType, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update or there are invalid field contents, then don't try to update the database
        if (values.size() == 0 || !validateFields(values))
            return 0;

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowCount = 0;
        // Perform update and return the row count of deleted rows
        switch (itemType) {
            case ITEM_ID:
            case ITEMS:
                rowCount = database.update(TripEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STOP_ID:
            case STOPS:
                rowCount = database.update(StopEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        if (rowCount > 0)
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        return rowCount;
    }

    /** {@link}
     *
     * @param values ContentValues containing the fields and values to validate.
     * @return boolean value indicating whether or not the fields were valid.
     */
    private boolean validateFields(ContentValues values) {
        boolean allFieldsValid = true;
        if (values.containsKey(COLUMN_RECEIVED_DATE))
            allFieldsValid = allFieldsValid && isValid(this.getContext(), COLUMN_RECEIVED_DATE, values.getAsString(COLUMN_RECEIVED_DATE),
                    NOT_NULL, IS_DATE);

        if (values.containsKey(TripEntry.COLUMN_STATE))
            allFieldsValid = allFieldsValid && isOneOf(this.getContext(), COLUMN_STATE, values.getAsInteger(COLUMN_STATE),
                    STATE_ASSIGNED, STATE_OPEN, STATE_CLOSED, STATE_SUBMITTED);

        //TODO: validate remaining fields

        return allFieldsValid;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowCount = 0;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                break;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                break;
            case STOPS:
                // Delete all rows that match the selection and selection args
                break;
            case STOP_ID:
                // Delete a single row given by the ID in the URI
                selection = StopEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        switch (match) {
            case ITEM_ID:
            case ITEMS:
                rowCount = database.delete(TripEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOP_ID:
            case STOPS:
                rowCount = database.delete(StopEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }
        if (rowCount > 0)
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        return rowCount;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return TripEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return TripEntry.CONTENT_ITEM_TYPE;
            case STOPS:
                return StopEntry.CONTENT_LIST_TYPE;
            case STOP_ID:
                return StopEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(String.format("Unknown URI %s with match %d", uri, match));
        }
    }

}
